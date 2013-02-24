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
#include "Listener.hxx"

#include <com/sun/star/view/XPrintJob.hpp>
#include <com/sun/star/frame/XModel2.hpp>

using namespace ::com::sun::star;
using namespace sdk_job_print_listener;

PrintListener::PrintListener ( uno::Reference< uno::XComponentContext > const &rxContext )
    : m_xContext ( rxContext )
{
    OSL_TRACE ( "PrintListener::PrintListener" );
}

PrintListener::~PrintListener()
{
    OSL_TRACE ( "PrintListener::~PrintListener" );
}

// ::com::sun::star::view::XPrintJobListener
void SAL_CALL PrintListener::printJobEvent(
    const css::view::PrintJobEvent &aPrintJobEvent )
throw ( css::uno::RuntimeException )
{
    OSL_TRACE( "PrintListener::printJobEvent" );

#if OSL_DEBUG_LEVEL > 0
    switch ( aPrintJobEvent.State )
    {
        case css::view::PrintableState_JOB_STARTED:
            OSL_TRACE( "PrintListener::printJobEvent - JOB_STARTED" );
            break;
        case css::view::PrintableState_JOB_COMPLETED:
            OSL_TRACE( "PrintListener::printJobEvent - JOB_COMPLETED" );
            break;
        case css::view::PrintableState_JOB_SPOOLED:
            OSL_TRACE( "PrintListener::printJobEvent - JOB_SPOOLED" );
            break;
        case css::view::PrintableState_JOB_ABORTED:
            OSL_TRACE( "PrintListener::printJobEvent - JOB_ABORTED" );
            break;
        case css::view::PrintableState_JOB_FAILED:
            OSL_TRACE( "PrintListener::printJobEvent - JOB_FAILED" );
            break;
        case css::view::PrintableState_JOB_SPOOLING_FAILED:
            OSL_TRACE( "PrintListener::printJobEvent - JOB_SPOOLING_FAILED" );
            break;
    }

    uno::Reference< view::XPrintJob > xPrintJob(
        aPrintJobEvent.Source, uno::UNO_QUERY );
    if ( !xPrintJob.is() )
        return;

    // Before Apache OpenOffice 4.0 this sequence was empty
    uno::Sequence< beans::PropertyValue > aPrintOps(
        xPrintJob->getPrintOptions() );
    for (const beans::PropertyValue *pPropVal = aPrintOps.getConstArray(),
            *pEnd = pPropVal + aPrintOps.getLength();
            pPropVal != pEnd; pPropVal++ )
    {
        OSL_TRACE( "\"%s\"", U2C( pPropVal->Name ) );
    }
#else
    (void) aPrintJobEvent;
#endif
}


// ::com::sun::star::document::XDocumentEventListener:
void SAL_CALL PrintListener::documentEventOccured( const document::DocumentEvent &rEvent )
throw ( uno::RuntimeException )
{
    OSL_TRACE( "PrintListener::documentEventOccured - EventName = %s", U2C( rEvent.EventName ) );

    if ( rEvent.EventName.equalsAsciiL( RTL_CONSTASCII_STRINGPARAM ( "OnPrint" ) ) )
    {
        // Do something...
        uno::Reference< frame::XModel2 > xModel( rEvent.Source, uno::UNO_QUERY );
        uno::Reference< frame::XController2 > xController( rEvent.ViewController );
        uno::Any aSupplement = rEvent.Supplement;

        OSL_ENSURE( xModel.is(), "PrintListener::documentEventOccured - Source != XModel");
        OSL_ENSURE( xController.is(), "PrintListener::documentEventOccured - No XController!");
        OSL_TRACE( "Supplement is dummy? ", !aSupplement.hasValue() ? "yes" : "no" );
    }
}


// ::com::sun::star::document::XEventListener:
void SAL_CALL PrintListener::notifyEvent( const document::EventObject   &rEvent )
throw ( uno::RuntimeException )
{
    OSL_TRACE ( "PrintListener::notifyEvent - EventName = %s", U2C ( rEvent.EventName ) );

    document::DocumentEvent aDocEvent;
    aDocEvent.EventName = rEvent.EventName;
    aDocEvent.Source = rEvent.Source;
    documentEventOccured ( aDocEvent );
}

// ::com::sun::star::lang::XEventListener:
void SAL_CALL PrintListener::disposing( const lang::EventObject &/*Source*/ )
throw ( uno::RuntimeException )
{
    OSL_TRACE ( "PrintListener::disposing" );
}
