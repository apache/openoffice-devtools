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

#include "client.hxx"

#include <algorithm>
#include <cstdlib>
#include <functional>
#include <iostream>
#include <vector>

#include <rtl/instance.hxx>
#include <rtl/ref.hxx>
#include <cppuhelper/basemutex.hxx>

#include <com/sun/star/bridge/XBridgeFactory.hpp>
#include <com/sun/star/connection/XConnector.hpp>
#include <com/sun/star/lang/XComponent.hpp>
#include <com/sun/star/lang/XEventListener.hpp>
#include <com/sun/star/lang/XServiceInfo.hpp>


#ifndef IMPLEMENTATION_NAME
#error  You must define IMPLEMENTATION_NAME
#endif

#ifndef SERVICE_NAME
#error  You must define SERVICE_NAME
#endif

#define C2U( constAsciiStr ) \
    ( rtl::OUString( RTL_CONSTASCII_USTRINGPARAM( constAsciiStr ) ) )

#define U2C( ouString ) \
    ( rtl::OUStringToOString( ouString, RTL_TEXTENCODING_UTF8 ).getStr() )


using namespace standalone_uno_exe;

namespace
{
    struct StaticImplementationName :
        public rtl::StaticWithInit <
            const rtl::OUString,
            StaticImplementationName >
    {
        const rtl::OUString &operator()() const
        {
            static rtl::OUString aName(
                RTL_CONSTASCII_USTRINGPARAM(
                    IMPLEMENTATION_NAME ) );

            return aName;
        }
    };

    struct StaticSupportedServices :
        public rtl::StaticWithInit <
            const css::uno::Sequence< rtl::OUString >,
            StaticSupportedServices >
    {
        const css::uno::Sequence< rtl::OUString > &
        operator()() const
        {
            static css::uno::Sequence< rtl::OUString > aSeq( 1 );
            aSeq[0] = rtl::OUString(
                          RTL_CONSTASCII_USTRINGPARAM(
                              SERVICE_NAME ) );
            return aSeq;
        }
    };

    struct PrintOUStringSeq
            : public std::unary_function< const rtl::OUString &, void >
    {
            PrintOUStringSeq()
                : m_nCount( 0 )
            {}

            void operator()( const rtl::OUString &rStr ) const
            {
                std::cout << '[' << m_nCount << "] " << U2C( rStr ) << '\n';
                ++m_nCount;
            }
        private:
            mutable int m_nCount;
    };

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
                const UnoConnUrl &url )
                : RemoteConnection_Base()
                , m_xLocalContext( xLocalContext )
                , m_xBridge()
                , m_xRemoteObject()
                , m_aUnoURL( url )
            {
                OSL_TRACE( "RemoteConnection::RemoteConnection" );
            }

            ~RemoteConnection()
            {
                OSL_TRACE( "RemoteConnection::~RemoteConnection" );
            }

            void close()
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

            void SAL_CALL disposing( const css::lang::EventObject &aEvent )
            throw ( css::uno::RuntimeException )
            {
                OSL_TRACE( "RemoteConnection::disposing" );

                osl::MutexGuard aGuard( m_aMutex );
                OSL_ENSURE( aEvent.Source == m_xBridge, "Different source object!" );

                m_xBridge.clear();
                m_xRemoteObject.clear();
            }

            css::uno::Reference< css::uno::XInterface > getRemoteObject()
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

                        // create a nameless bridge with no instance provider
                        // using the middle part of the UNO URL
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
                        OSL_TRACE( "getRemoteObject() caugth an exception: %s", U2C( e.Message ) );
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
                        OSL_TRACE( "getRemoteObject() caugth an exception: %s", U2C( e.Message ) );
                    }

                    aGuard.reset();
                    m_xRemoteObject = xRemoteObject;
                    aGuard.clear();
                }

                return xRemoteObject;
            }
    };
}

ClientUnoExe::ClientUnoExe(
    const css::uno::Reference< css::uno::XComponentContext > &rxContext )
    : ClientUnoExe_Base()
    , m_xContext( rxContext )
{
    OSL_TRACE( "ClientUnoExe::ClientUnoExe" );

    // m_xContext is the URE-local component context
    // In order to use the office components,
    // we should bootstrap an office instance
    css::uno::Reference< css::lang::XMultiComponentFactory > xMCF(
        m_xContext->getServiceManager() );
    css::uno::Sequence< rtl::OUString > sServices = xMCF->getAvailableServiceNames();

    const rtl::OUString *pBegin = sServices.getConstArray();
    std::vector< rtl::OUString > vServices(
        pBegin, pBegin + sServices.getLength() );

    std::cout << "\n\nAvailable services:\n";
    std::for_each(
        vServices.begin(), vServices.end(), PrintOUStringSeq() );
}

ClientUnoExe::~ClientUnoExe()
{
    OSL_TRACE( "ClientUnoExe::~ClientUnoExe" );
}

/**
 * UNO exe invokation:
 *
 * uno [...] -- [arguments passed to the component's XMain::run()]
 */
sal_Int32 SAL_CALL
ClientUnoExe::run(
    const css::uno::Sequence< rtl::OUString > &aArguments )
throw ( css::uno::RuntimeException )
{
    OSL_TRACE( "ClientUnoExe::run" );

    // In this example, the first argument should contain a UNO connection URL
    // to connect to the remote server running in the UNO executable launched
    // in server mode.
    UnoConnUrl aUrl;
    rtl::OUString sUrl;

    if ( aArguments.getLength() != 1 ||
            !( sUrl = aArguments[0] ).getLength() ||
            !lcl_ParseUnoUrl( sUrl, aUrl ) )
    {
        std::cerr << "You must specify the UNO connection URL\n";
        return EXIT_FAILURE;
    }

    // Hold a reference to the connection so that it lives during this method
    rtl::Reference< RemoteConnection > aConnection( new RemoteConnection( m_xContext, aUrl ) );

    // get the object exported by the remote server
    css::uno::Reference< css::lang::XServiceInfo > xServiceInfo(
        aConnection->getRemoteObject(), css::uno::UNO_QUERY );

    if ( xServiceInfo.is() )
    {
        std::cout << "Remote object: " << U2C( xServiceInfo->getImplementationName() ) << '\n';

        std::cout << "Supported services:\n";
        css::uno::Sequence< rtl::OUString > aSupportedService(
            xServiceInfo->getSupportedServiceNames() );

        const rtl::OUString *pBegin = aSupportedService.getConstArray();
        std::vector< rtl::OUString > vServices(
            pBegin, pBegin + aSupportedService.getLength() );
        std::for_each(
            vServices.begin(), vServices.end(), PrintOUStringSeq() );
    }

    std::cout << "Press ENTER to exit XMain::run()...";
    std::cin.get();

    aConnection->close();
    aConnection.clear();

    return EXIT_SUCCESS;

}


css::uno::Reference< css::uno::XInterface > SAL_CALL
ClientUnoExe::CreateInstance(
    const css::uno::Reference< css::uno::XComponentContext > &rxContext )
{
    OSL_TRACE( "ClientUnoExe::CreateInstance" );

    return css::uno::Reference< css::uno::XInterface >(
               static_cast< OWeakObject * >(
                   new ClientUnoExe( rxContext ) ) );
}

rtl::OUString SAL_CALL
ClientUnoExe::GetImplementationName_static()
{
    OSL_TRACE( "ClientUnoExe::GetImplementationName_static" );

    return StaticImplementationName::get();
}

css::uno::Sequence< rtl::OUString > SAL_CALL
ClientUnoExe::GetSupportedServiceNames_static()
{
    OSL_TRACE( "ClientUnoExe::GetSupportedServiceNames_static" );

    return StaticSupportedServices::get();
}

