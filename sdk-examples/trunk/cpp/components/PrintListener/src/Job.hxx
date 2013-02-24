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

#ifndef OOO_SDK_PRINT_JOB_HXX
#define OOO_SDK_PRINT_JOB_HXX

#include <cppuhelper/implbase2.hxx>
#include <cppuhelper/basemutex.hxx>

#include <com/sun/star/task/XJob.hpp>
#include <com/sun/star/lang/XServiceInfo.hpp>
#include <com/sun/star/uno/XComponentContext.hpp>

namespace css = com::sun::star;

namespace sdk_job_print_listener
{

    namespace
    {
        typedef ::cppu::WeakImplHelper2 <
            css::task::XJob,
            css::lang::XServiceInfo > Job_Base;
    }

    class Job :
        protected ::cppu::BaseMutex,
        public Job_Base
    {
        private:
            css::uno::Reference< css::uno::XComponentContext > m_xContext;

        public:
            explicit Job( const css::uno::Reference< css::uno::XComponentContext > &rxContext );
            virtual ~Job();

            static css::uno::Reference< css::uno::XInterface > Create( const css::uno::Reference< css::uno::XComponentContext > &rxContext ) throw( css::uno::Exception );
            static css::uno::Sequence< ::rtl::OUString > getSupportedServiceNames_static();
            static ::rtl::OUString getImplementationName_static();

            // ::com::sun::star::task::XJob
            virtual css::uno::Any SAL_CALL execute( const css::uno::Sequence< css::beans::NamedValue > &Arguments ) throw ( css::lang::IllegalArgumentException, css::uno::Exception, css::uno::RuntimeException );

            // ::com::sun::star::lang::XServiceInfo:
            virtual ::rtl::OUString SAL_CALL getImplementationName() throw ( css::uno::RuntimeException );
            virtual ::sal_Bool SAL_CALL supportsService( const ::rtl::OUString &ServiceName ) throw ( css::uno::RuntimeException );
            virtual css::uno::Sequence< ::rtl::OUString > SAL_CALL getSupportedServiceNames() throw ( css::uno::RuntimeException );
    };
}

#endif
