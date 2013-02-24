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

#ifndef OOO_SDK_PRINT_LISTENER_HXX
#define OOO_SDK_PRINT_LISTENER_HXX

#include <cppuhelper/implbase3.hxx>

#include <com/sun/star/document/XDocumentEventListener.hpp>
#include <com/sun/star/document/XEventListener.hpp>
#include <com/sun/star/view/XPrintJobListener.hpp>
#include <com/sun/star/uno/XComponentContext.hpp>

namespace css = com::sun::star;

namespace sdk_job_print_listener
{
    namespace
    {
        typedef ::cppu::WeakImplHelper3 <
            css::view::XPrintJobListener,
            css::document::XDocumentEventListener,
            css::document::XEventListener > PrintListener_Base;
    }

    class PrintListener : public PrintListener_Base
    {
        private:
            css::uno::Reference< css::uno::XComponentContext >  m_xContext;

        public:
            PrintListener ( const css::uno::Reference< css::uno::XComponentContext > &rxContext );
            virtual ~PrintListener();

            // ::com::sun::star::view::XPrintJobListener
            virtual void SAL_CALL printJobEvent( const css::view::PrintJobEvent &Event ) throw ( css::uno::RuntimeException );

            // ::com::sun::star::document::XDocumentEventListener:
            virtual void SAL_CALL documentEventOccured ( const css::document::DocumentEvent &Event ) throw ( css::uno::RuntimeException );

            // ::com::sun::star::document::XEventListener:
            virtual void SAL_CALL notifyEvent ( const css::document::EventObject   &Event ) throw ( css::uno::RuntimeException );

            // ::com::sun::star::lang::XEventListener:
            virtual void SAL_CALL disposing ( const css::lang::EventObject &Source ) throw ( css::uno::RuntimeException );
    };
}

#endif
