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

package org.openoffice.extensions.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.util.Utilities;

/**
 * Replaces strings in content being unzipped to customize the project name to
 * match what the user entered in the wizard.
 *
 */
public class Substitutions {
    private Properties props;               
    private String[] pathProperties;
    private String[] contentProperties;    
    private WizardDescriptor wiz;


    /** Creates a new instance of Substitutions */
    public Substitutions(WizardDescriptor wiz, InputStream propertyStream, String[] pathProperties, String[] contentProperties) throws IOException {
        if (propertyStream == null) {
            throw new IOException ("Could not find properties file"); // NOI18N
        }
        props = new Properties();
        props.load(propertyStream);
        
        this.wiz = wiz;
        this.pathProperties = pathProperties;
        this.contentProperties = contentProperties;
    }

    private Substitutions (InputStream stream, WizardDescriptor wiz, String[] pathProperties, String[] contentProperties) throws IOException {
        if (stream == null) {
            throw new IOException ("Could not find properties file"); // NOI18N
        }
        props = new Properties();
        props.load(stream);
        
        this.wiz = wiz;
        this.pathProperties = pathProperties;
        this.contentProperties = contentProperties;        
    }


    public String substitutePath (String filepath) {
        String result = null;
        if (filepath.indexOf("CentralRegistrationClass.java") != -1)  { // NOI18N
            result = ((String)wiz.getProperty(
                "CompleteSourcePath")).concat(
                "/CentralRegistrationClass.java"); // NOI18N
        }
        else {
            result = props.getProperty(filepath);
            if (result != null) {            
                for (int i=0; i < pathProperties.length; i++) {
                    String propertyVariable = "%" +  pathProperties[i] + "%"; // NOI18N
                    result = result.replaceAll(propertyVariable, (String) wiz.getProperty(pathProperties[i]));                
                }            
            }
        }
        return result == null ? filepath : result;
    }

    public InputStream substituteContent (long originalSize, InputStream input, String filename) throws IOException {
        if (filename.endsWith (".gif") || filename.endsWith (".png") || filename.endsWith(".jar")) { // NOI18N
            return input;
        }
        if (originalSize > Integer.MAX_VALUE || originalSize < 0) {
            throw new IllegalArgumentException ("File too large: " + // NOI18N
                    originalSize);
        }
        ByteArrayOutputStream temp = new ByteArrayOutputStream ((int) originalSize);
        FileUtil.copy (input, temp);
        byte[] b = temp.toByteArray();

        //XXX do we want default charset, or UTF-8 - UTF-8 I think...
        CharBuffer cb = Charset.defaultCharset().decode(ByteBuffer.wrap(b));
        String data = cb.toString();

       for (Iterator iter = props.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            String val = props.getProperty(key);

            for (int i=0; i < contentProperties.length; i++) {
                String propertyVariable = "%" +  contentProperties[i] + "%"; // NOI18N
                String replacement = (String) wiz.getProperty(contentProperties[i]);
                if (replacement == null) replacement = ""; // NOI18N
                val = val.replaceAll(propertyVariable, replacement);                
            }

            Matcher m = Pattern.compile(key).matcher(data);
            data = m.replaceAll(val);
        }

        return new ByteArrayInputStream (data.getBytes());
    }
}

