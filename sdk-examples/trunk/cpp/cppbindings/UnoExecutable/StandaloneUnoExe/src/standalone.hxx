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

#ifndef __UNO_EXE_STANDALONE__
#define __UNO_EXE_STANDALONE__

#include <com/sun/star/lang/XMain.hpp>
#include <cppuhelper/implbase1.hxx>

namespace css = ::com::sun::star;

namespace standalone_uno_exe
{
    typedef ::cppu::WeakImplHelper1< css::lang::XMain > StandaloneUnoExe_Base;

    class StandaloneUnoExe : public StandaloneUnoExe_Base
    {
        private:
            css::uno::Reference< css::uno::XComponentContext > m_xContext;

        public:
            StandaloneUnoExe( const css::uno::Reference< css::uno::XComponentContext > &rxContext );
            ~StandaloneUnoExe();

            virtual sal_Int32 SAL_CALL run( const css::uno::Sequence< rtl::OUString > &aArguments ) throw( css::uno::RuntimeException );

            static css::uno::Reference< css::uno::XInterface > SAL_CALL CreateInstance( const css::uno::Reference< css::uno::XComponentContext > &rxContext );
            static css::uno::Sequence< rtl::OUString > SAL_CALL GetSupportedServiceNames_static();
            static rtl::OUString SAL_CALL GetImplementationName_static();
    };
}

#endif
