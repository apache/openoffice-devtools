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

#include <sal/main.h>
#include <osl/process.h>
#include <osl/file.hxx>
#include <osl/file.hxx>
#include <rtl/ustring.hxx>

#include <iostream>
#include <vector>

using rtl::OUString;
using namespace std;
using namespace osl;

SAL_IMPLEMENT_MAIN_WITH_ARGS( argc, argv )
{
    oslProcessExitCode nExitCode = 1;
    oslProcessError eError;
    sal_Int32 nLen;
    sal_Int32 n = osl_getCommandArgCount( );

    if ( n != 2 )
    {
        OUString sProgFile;
        eError = osl_getExecutableFile( &sProgFile.pData );

        if ( eError == osl_Process_E_None && ( nLen = sProgFile.getLength( ) ) )
            n = sProgFile.lastIndexOf( sal_Unicode( '/' ) );

        if ( n != -1 && n < nLen )
            sProgFile = sProgFile.copy( ++n );

        cerr << "Usage:\n\t"
             << rtl::OUStringToOString( sProgFile, RTL_TEXTENCODING_UTF8 ).getStr( )
             << " <soffice_path> <file_to_open>\n";
    }

    OUString sExecutable, sFile, sURL;

    eError = osl_getCommandArg( 0, &sExecutable.pData );
    eError = osl_getCommandArg( 1, &sFile.pData );

    if ( FileBase::getFileURLFromSystemPath( sExecutable, sURL ) != FileBase::E_None || !sURL.getLength( ) )
    {
        cerr << "Cannot convert program path to URL\n";
        return nExitCode;
    }

    sExecutable = sURL;

    if ( FileBase::getFileURLFromSystemPath( sFile, sURL ) != FileBase::E_None || !sURL.getLength( ) )
    {
        cerr << "Cannot convert document path to URL\n";
        return nExitCode;
    }

    // -nologo -nofirststartwizard -norestore -quickstart=no
    vector< OUString > vArgs;
    vArgs.push_back( OUString( RTL_CONSTASCII_USTRINGPARAM( "-nologo" ) ) );
    vArgs.push_back( OUString( RTL_CONSTASCII_USTRINGPARAM( "-norestore" ) ) );
    vArgs.push_back( OUString( RTL_CONSTASCII_USTRINGPARAM( "-nofirststartwizard" ) ) );
    vArgs.push_back( OUString( RTL_CONSTASCII_USTRINGPARAM( "-quickstart=no" ) ) );
    vArgs.push_back( sURL );

    oslProcess aProcess;
    eError = osl_executeProcess(
                 /* [in] The file URL of the executable to be started.
                  * Can be NULL in this case the file URL of the executable
                  * must be the first element in ustrArguments.*/
                 sExecutable.pData,
                 /* [in] An array of argument strings.
                  * Can be NULL if strImageName is not NULL.
                  * If strImageName is NULL it is expected that the first element
                  * contains the file URL of the executable to start.*/
                 &vArgs[0].pData,
                 /* [in] The number of arguments provided.
                  * If this number is 0 strArguments will be ignored.*/
                 vArgs.size( ),
                 /* [in] A combination of int-constants to describe the mode of execution.*/
                 osl_Process_WAIT | osl_Process_DETACHED,
                 /* [in] The user and his rights for which the process is started.
                  * May be NULL in which case the process will be started
                  * in the context of the current user.*/
                 NULL,
                 /* [in] The file URL of the working directory of the new process.
                  * If the specified directory does not exist or
                  * is inaccessible the working directory of the newly created process
                  * is undefined.
                  * If this parameter is NULL or the caller provides an empty string
                  * the new process will have the same current working directory
                  * as the calling process.*/
                 NULL,
                 /* [in] An array of strings describing environment variables
                  * that should be merged into the environment of the new process.
                  * Each string has to be in the form "variable=value".
                  * This parameter can be NULL in which case the new process gets
                  * the same environment as the parent process. */
                 NULL,
                 /* [in] The number of environment variables to set.*/
                 0,
                 /* [out] Pointer to a oslProcess variable,
                  * which receives the handle of the newly created process.
                  * This parameter must not be NULL.*/
                 &aProcess );

    if ( eError != osl_Process_E_None )
        return nExitCode;

    oslProcessInfo info;

    info.Size = sizeof ( oslProcessInfo );
    eError = osl_getProcessInfo( aProcess, osl_Process_EXITCODE, &info );

    if ( eError != osl_Process_E_None )
        return 1;

    if ( info.Fields & osl_Process_EXITCODE )
    {
        nExitCode = info.Code;
        cout << "Office exit code: " << nExitCode << '\n';
    }

    osl_freeProcessHandle( aProcess );

    return 0;
}
