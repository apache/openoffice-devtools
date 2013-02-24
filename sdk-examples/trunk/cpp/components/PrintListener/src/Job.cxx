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

#include "macros.hxx"
#include "Job.hxx"
#include "Listener.hxx"

#include <rtl/instance.hxx>

#include <com/sun/star/document/XDocumentEventBroadcaster.hpp>
#include <com/sun/star/document/XEventBroadcaster.hpp>
#include <com/sun/star/frame/XModel2.hpp>
#include <com/sun/star/view/XPrintJobBroadcaster.hpp>
#include <com/sun/star/view/XPrintable.hpp>

using namespace ::com::sun::star;
using namespace sdk_job_print_listener;

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
                    JOB_IMPLEMENTATION_NAME ) );

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
                              "com.sun.star.task.Job" ) );
            return aSeq;
        }
    };
}


Job::Job ( uno::Reference< uno::XComponentContext > const &rxContext )
    : m_xContext ( rxContext )
{
    OSL_TRACE ( "Job::Job" );
}

Job::~Job()
{
    OSL_TRACE ( "Job::~Job" );
}

// ::com::sun::star::task::XJob:
uno::Any SAL_CALL Job::execute (
    const uno::Sequence< beans::NamedValue > &aArguments )
throw ( lang::IllegalArgumentException,
        uno::Exception, uno::RuntimeException )
{
    OSL_TRACE ( "Job::execute" );

    ::sal_Int32 i = 0;
    ::sal_Int32 nLength = aArguments.getLength();
    const beans::NamedValue *pArray = aArguments.getConstArray();
    uno::Sequence< beans::NamedValue > lEnv;

    for ( i = 0; i < nLength; ++i )
    {
        if ( pArray[i].Name.equalsAscii ( "Environment" ) )
        {
            pArray[i].Value >>= lEnv;
            break;
        }
    }

    uno::Reference< frame::XModel2 > xModel;
    nLength = lEnv.getLength();
    pArray = lEnv.getConstArray();

    for ( i = 0; i < nLength; ++i )
    {
        if ( pArray[i].Name.equalsAscii ( "Model" ) )
        {
            pArray[i].Value >>= xModel;
            break;
        }
    }

    // only listen for documents that can be printed
    uno::Reference< view::XPrintable > xPrintable ( xModel, uno::UNO_QUERY );
    if ( !xPrintable.is() )
        return uno::Any();

    PrintListener *pListener = new PrintListener ( m_xContext );

    // the XDocumentEventBroadcaster notifies only "OnPrint" events
    // that is, *only* css::view::PrintableState_JOB_STARTED
    uno::Reference< document::XDocumentEventBroadcaster > xBroadcaster(
        xModel, uno::UNO_QUERY );
    if ( xBroadcaster.is() )
    {
        xBroadcaster->addDocumentEventListener ( pListener );
    }
    else
    {
        uno::Reference< document::XEventBroadcaster > xOldBroadcaster(
            xModel, uno::UNO_QUERY );
        if ( xOldBroadcaster.is() )
        {
            xOldBroadcaster->addEventListener ( pListener );
        }
    }

    // the XPrintJobBroadcaster gives more detailed notification
    uno::Reference< view::XPrintJobBroadcaster > xPrintBroadcaster(
        xModel, uno::UNO_QUERY );
    if ( xPrintBroadcaster.is() )
        xPrintBroadcaster->addPrintJobListener( pListener );

    return uno::Any();
}


// com.sun.star.uno.XServiceInfo:
::rtl::OUString SAL_CALL Job::getImplementationName()
throw ( uno::RuntimeException )
{
    return StaticImplementationName::get();
}

::sal_Bool SAL_CALL Job::supportsService ( ::rtl::OUString const &serviceName )
throw ( uno::RuntimeException )
{
    const uno::Sequence< ::rtl::OUString > &sServiceNames = StaticSupportedServices::get();
    const rtl::OUString *pStr = sServiceNames.getConstArray();
    const rtl::OUString *pEnd; pStr + sServiceNames.getLength();
    for ( ; pStr != pEnd; pStr++ )
    {
        if ( pStr && *pStr == serviceName )
            break;
    }

    return pStr != pEnd;
}

uno::Sequence< ::rtl::OUString > SAL_CALL Job::getSupportedServiceNames()
throw ( uno::RuntimeException )
{
    return StaticSupportedServices::get();
}


uno::Reference< uno::XInterface > Job::Create (
    const uno::Reference< uno::XComponentContext > &rxContext )
throw ( uno::Exception )
{
    return static_cast< ::cppu::OWeakObject * > ( new Job ( rxContext ) );
}

::rtl::OUString Job::getImplementationName_static()
{
    return StaticImplementationName::get();
}

uno::Sequence< ::rtl::OUString > Job::getSupportedServiceNames_static()
{
    return StaticSupportedServices::get();
}


