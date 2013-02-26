/**************************************************************
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 *************************************************************/

#include <algorithm>
#include <cstdlib>
#include <functional>
#include <iostream>
#include <vector>

#include <cppuhelper/basemutex.hxx>
#include <cppuhelper/bootstrap.hxx>
#include <cppuhelper/implbase1.hxx>
#include <osl/process.h>
#include <rtl/instance.hxx>
#include <rtl/ref.hxx>
#include <sal/main.h>

#include <com/sun/star/beans/PropertyValue.hpp>
#include <com/sun/star/bridge/XBridgeFactory.hpp>
#include <com/sun/star/connection/XConnector.hpp>
#include <com/sun/star/frame/FrameSearchFlag.hpp>
#include <com/sun/star/frame/XComponentLoader.hpp>
#include <com/sun/star/frame/XDesktop.hpp>
#include <com/sun/star/lang/XComponent.hpp>
#include <com/sun/star/lang/XEventListener.hpp>
#include <com/sun/star/lang/XServiceInfo.hpp>
#include <com/sun/star/text/XTextDocument.hpp>
#include <com/sun/star/util/XModifiable.hpp>

#define C2U( constAsciiStr ) \
    ( rtl::OUString( RTL_CONSTASCII_USTRINGPARAM( constAsciiStr ) ) )

namespace css = ::com::sun::star;
using namespace osl;

namespace connection_aware_client
{
    static std::ostream &
    operator<<( std::ostream &stream, const rtl::OUString &rStr )
    {
        return stream << rtl::OUStringToOString( rStr, RTL_TEXTENCODING_UTF8 ).getStr();
    }

    static std::ostream &
    operator<<( std::ostream &stream, const css::uno::Exception &e )
    {
        return stream << e.Message;
    }

    struct UnoConnUrl
    {
        rtl::OUString Connection;
        rtl::OUString Object;
    };

    static bool lcl_ParseUnoUrl( const rtl::OUString &sUrl, UnoConnUrl &url )
    {
        // must start with "uno:"
        if ( sUrl.compareToAscii( RTL_CONSTASCII_STRINGPARAM( "uno:" ) != 0 ) )
            return false;

        sal_Int32 nIndex = sizeof( "uno:" ) - 1;

        url.Connection = sUrl.getToken( 0, sal_Unicode( ';' ), nIndex );
        // there should be an initial object
        if ( nIndex == -1 )
            return false;

        // protocol must be "urp"
        rtl::OUString sProtocol = sUrl.getToken( 0, sal_Unicode( ';' ), nIndex );
        if ( nIndex == -1 || !sProtocol.equalsAsciiL( RTL_CONSTASCII_STRINGPARAM( "urp" ) ) )
            return false;

        url.Object = sUrl.getToken( 0, sal_Unicode( ';' ), nIndex );

        return url.Object.getLength() > 0 /* && nIndex == -1 */;
    }

    typedef ::cppu::WeakImplHelper1< css::lang::XEventListener > RemoteConnection_Base;

    class RemoteConnection
        : protected ::cppu::BaseMutex, public RemoteConnection_Base
    {
        private:
            css::uno::Reference< css::uno::XComponentContext > m_xLocalContext;
            css::uno::Reference< css::lang::XComponent > m_xBridge;
            css::uno::Reference< css::uno::XInterface > m_xRemoteObject;
            UnoConnUrl m_aUnoURL;
        public:
            RemoteConnection(
                const css::uno::Reference< css::uno::XComponentContext > &xLocalContext,
                const UnoConnUrl &url );
            ~RemoteConnection();

            void close();

            void SAL_CALL disposing( const css::lang::EventObject &aEvent )
            throw ( css::uno::RuntimeException );

            css::uno::Reference< css::uno::XInterface > getRemoteObject();
    };
}

using namespace connection_aware_client;

RemoteConnection::RemoteConnection(
    const css::uno::Reference< css::uno::XComponentContext > &xLocalContext,
    const UnoConnUrl &url )
    : RemoteConnection_Base()
    , m_xLocalContext( xLocalContext )
    , m_xBridge()
    , m_xRemoteObject()
    , m_aUnoURL( url )
{
    OSL_TRACE( "RemoteConnection::RemoteConnection" );
}

RemoteConnection::~RemoteConnection()
{
    OSL_TRACE( "RemoteConnection::~RemoteConnection" );
}

void RemoteConnection::close()
{
    OSL_TRACE( "RemoteConnection::close" );
    osl::MutexGuard aGuard( m_aMutex );
    if ( m_xBridge.is() )
    {
        m_xBridge->removeEventListener( this );
        m_xBridge.clear();
        m_xRemoteObject.clear();
    }
}

void SAL_CALL
RemoteConnection::disposing( const css::lang::EventObject &aEvent )
throw ( css::uno::RuntimeException )
{
    OSL_TRACE( "RemoteConnection::disposing" );

    osl::MutexGuard aGuard( m_aMutex );
    OSL_ENSURE( aEvent.Source == m_xBridge, "Different source object!" );

    m_xBridge.clear();
    m_xRemoteObject.clear();
}

css::uno::Reference< css::uno::XInterface >
RemoteConnection::getRemoteObject()
{
    OSL_TRACE( "RemoteConnection::getRemoteObject" );

    osl::ResettableMutexGuard aGuard( m_aMutex );
    css::uno::Reference< css::uno::XInterface > xRemoteObject = m_xRemoteObject;
    css::uno::Reference< css::bridge::XBridge > xBridge( m_xBridge, css::uno::UNO_QUERY );
    css::uno::Reference< css::uno::XComponentContext > xContext = m_xLocalContext;
    aGuard.clear();

    if ( xRemoteObject.is() || !xContext.is() )
        return xRemoteObject;

    if ( !xBridge.is() )
    {
        try
        {
            css::uno::Reference< css::connection::XConnector > xConnector(
                xContext->getServiceManager()->createInstanceWithContext(
                    rtl::OUString( RTL_CONSTASCII_USTRINGPARAM(
                                       "com.sun.star.connection.Connector" ) ),
                    xContext ), css::uno::UNO_QUERY );

            css::uno::Reference< css::connection::XConnection > xConnection(
                xConnector->connect( m_aUnoURL.Connection ) );

            css::uno::Reference< css::bridge::XBridgeFactory > xBridgeFactory(
                xContext->getServiceManager()->createInstanceWithContext(
                    rtl::OUString( RTL_CONSTASCII_USTRINGPARAM(
                                       "com.sun.star.bridge.BridgeFactory" ) ),
                    xContext ), css::uno::UNO_QUERY );

            xBridge.set(
                xBridgeFactory->createBridge(
                    rtl::OUString(),
                    C2U( "urp" ),
                    xConnection,
                    css::uno::Reference< css::bridge::XInstanceProvider>() ) );

            css::uno::Reference< css::lang::XComponent > xBridgeComp(
                xBridge, css::uno::UNO_QUERY_THROW );
            xBridgeComp->addEventListener( this );

            aGuard.reset();
            m_xBridge = xBridgeComp;
            aGuard.clear();
        }
        catch ( const css::uno::Exception &e )
        {
            std::cerr << "getRemoteObject() caugth an exception: " << e << '\n';
        }
    }

    if ( xBridge.is() )
    {
        try
        {
            xRemoteObject.set(
                xBridge->getInstance(
                    m_aUnoURL.Object ),
                css::uno::UNO_QUERY_THROW );
        }
        catch ( const css::uno::Exception &e )
        {
            std::cerr << "getRemoteObject() caugth an exception: " << e << '\n';
        }

        aGuard.reset();
        m_xRemoteObject = xRemoteObject;
        aGuard.clear();
    }

    return xRemoteObject;
}


static css::uno::Reference< css::uno::XInterface >
testRemoteContext(
    const css::uno::Reference< css::uno::XComponentContext > &xContext )
{
    try
    {
        css::uno::Reference< css::frame::XComponentLoader > xLoader(
            xContext->getServiceManager()->createInstanceWithContext(
                C2U( "com.sun.star.frame.Desktop" ), xContext ),
            css::uno::UNO_QUERY_THROW );

        css::uno::Reference< css::text::XTextDocument > xDoc(
            xLoader->loadComponentFromURL(
                C2U( "private:factory/swriter" ),
                C2U( "_default" ),
                css::frame::FrameSearchFlag::ALL,
                css::uno::Sequence< css::beans::PropertyValue >() ),
            css::uno::UNO_QUERY_THROW );

        css::uno::Reference< css::text::XText > xText( xDoc->getText() );
        xText->setString( C2U( "Hello world!" ) );

        // change the modified flag to close the document without interaction
        css::uno::Reference< css::util::XModifiable > xModified(
            xDoc, css::uno::UNO_QUERY_THROW );
        xModified->setModified( sal_False );

        return xLoader;
    }
    catch ( const css::uno::Exception &e )
    {
        std::cerr << "getRemoteObject() caugth an exception: " << e << '\n';
    }

    return 0;
}


SAL_IMPLEMENT_MAIN_WITH_ARGS( argc, argv )
{
    oslProcessError eError;
    sal_Int32 n = osl_getCommandArgCount( );
    if ( n != 2 )
    {
        rtl::OUString sProgFile;
        sal_Int32 nLen;
        eError = osl_getExecutableFile( &sProgFile.pData );

        if ( eError == osl_Process_E_None && ( nLen = sProgFile.getLength( ) ) )
            n = sProgFile.lastIndexOf( sal_Unicode( '/' ) );

        if ( n != -1 && n < nLen )
            sProgFile = sProgFile.copy( ++n );

        std::cerr << "Usage:\n\t" << sProgFile << "-env:URE_MORE_TYPES=<path to offapi.rdb> <UNO url>\n";

        return EXIT_FAILURE;
    }

    // In this example, the second argument should contain a UNO connection URL
    // to connect to the remote server running in the UNO executable launched
    // in server mode.
    UnoConnUrl aUrl;
    rtl::OUString sUrl;
    eError = osl_getCommandArg( 1, &sUrl.pData );

    if (  eError != osl_Process_E_None ||
            !sUrl.getLength() ||
            !lcl_ParseUnoUrl( sUrl, aUrl ) )
    {
        std::cerr << "You must specify a valid UNO connection URL\n";
        return EXIT_FAILURE;
    }

    std::cout << "Make sure OpenOffice is running and listening for connections!\n";
    std::cout << "Press ENTER to start the test...";
    std::cin.get();

    // get the local component context
    css::uno::Reference< css::uno::XComponentContext > xLocalContext;
    try
    {
        xLocalContext.set(
            cppu::defaultBootstrap_InitialComponentContext() );
    }
    catch ( const css::uno::Exception &e )
    {
        std::cerr << "Exception bootstrapping URE! " << e << '\n';
        return EXIT_FAILURE;
    }

    // Hold a reference to the connection so that it lives during this function
    rtl::Reference< RemoteConnection > aConnection(
        new RemoteConnection( xLocalContext, aUrl ) );

    // get the object exported by the remote server
    css::uno::Reference< css::uno::XComponentContext > xRemoteContext(
        aConnection->getRemoteObject(), css::uno::UNO_QUERY );

    if ( !xRemoteContext.is() )
    {
        std::cerr << "Cannot get remote component context!\n";
    }
    else
    {
        css::uno::Reference< css::frame::XDesktop > xDesktop(
            testRemoteContext( xRemoteContext ),
            css::uno::UNO_QUERY_THROW );

        if ( xDesktop.is() )
        {
            std::cout << "Press ENTER to terminate the Desktop...";
            std::cin.get();
            bool bTerminate = true;
            try
            {
                xDesktop->terminate();
            }
            catch ( const css::uno::Exception &e )
            {
                std::cerr << "Cannot terminate the Desktop!\n";
                bTerminate = false;
            }

            if ( bTerminate )
            {
                // wait a little until it terminates
                std::cout << "The office is terminating...\nPress ENTER to test the bridge...";
                std::cin.get();

                // retreiving the remote context should fail now:
                // the bridge gets disposed, we try to reconnect,
                // but there is no office instance listening
                xRemoteContext.set( aConnection->getRemoteObject(), css::uno::UNO_QUERY );
                if ( xRemoteContext.is() )
                    std::cerr << "Got a remote context with a ternimated office!\n";
            }
        }
    }

    std::cout << "Press ENTER to finish the example...";
    std::cin.get();

    aConnection->close();
    aConnection.clear();

    return EXIT_SUCCESS;
}

