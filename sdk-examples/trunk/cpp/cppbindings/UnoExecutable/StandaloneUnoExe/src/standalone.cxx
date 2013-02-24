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

#include "standalone.hxx"

#include <algorithm>
#include <cstdlib>
#include <functional>
#include <iostream>
#include <vector>

#include <rtl/instance.hxx>

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
}

StandaloneUnoExe::StandaloneUnoExe(
    const css::uno::Reference< css::uno::XComponentContext > &rxContext )
    : StandaloneUnoExe_Base()
    , m_xContext( rxContext )
{
    OSL_TRACE( "StandaloneUnoExe::StandaloneUnoExe" );

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

StandaloneUnoExe::~StandaloneUnoExe()
{
    OSL_TRACE( "StandaloneUnoExe::~StandaloneUnoExe" );
}

/**
 * UNO exe invokation:
 *
 * uno [...] -- [arguments passed to the component's XMain::run()]
 */
sal_Int32 SAL_CALL
StandaloneUnoExe::run(
    const css::uno::Sequence< rtl::OUString > &aArguments )
throw ( css::uno::RuntimeException )
{
    OSL_TRACE( "StandaloneUnoExe::run" );

    // Dummy: print the command line args
    for (const rtl::OUString *pBegin = aArguments.getConstArray(),
        *pEnd = pBegin + aArguments.getLength();
        pBegin != pEnd; pBegin++ )
    {
        std::cout << '\t' << U2C(*pBegin) << '\n';
    }

    return EXIT_SUCCESS;
}


css::uno::Reference< css::uno::XInterface > SAL_CALL
StandaloneUnoExe::CreateInstance(
    const css::uno::Reference< css::uno::XComponentContext > &rxContext )
{
    OSL_TRACE( "StandaloneUnoExe::CreateInstance" );

    return css::uno::Reference< css::uno::XInterface >(
               static_cast< OWeakObject * >(
                   new StandaloneUnoExe( rxContext ) ) );
}

rtl::OUString SAL_CALL
StandaloneUnoExe::GetImplementationName_static()
{
    OSL_TRACE( "StandaloneUnoExe::GetImplementationName_static" );

    return StaticImplementationName::get();
}

css::uno::Sequence< rtl::OUString > SAL_CALL
StandaloneUnoExe::GetSupportedServiceNames_static()
{
    OSL_TRACE( "StandaloneUnoExe::GetSupportedServiceNames_static" );

    return StaticSupportedServices::get();
}

