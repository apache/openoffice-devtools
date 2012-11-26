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
package org.openoffice.extensions.filetypes.odf;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.text.DataEditorSupport;

public class OdfDataObject extends MultiDataObject {

    public static final String[] ALL_ODF_EXTENSIONS = new String[]{
        "odt", "ods", "odp", "odb", "odg", "odc", "odf"
     };
    private static final String GENERAL_IMAGE_ICON = "org/openoffice/extensions/filetypes/odf/ooo_16.png"; // NOI18N
    private static final String WRITER_IMAGE_ICON = "org/openoffice/extensions/filetypes/odf/Galaxy_OOo3_writer-doc_16.png"; // NOI18N
    private static final String MATH_IMAGE_ICON = "org/openoffice/extensions/filetypes/odf/Galaxy_OOo3_math-doc_16.png"; // NOI18N
    private static final String IMPRESS_IMAGE_ICON = "org/openoffice/extensions/filetypes/odf/Galaxy_OOo3_impress-doc_16.png"; // NOI18N
    private static final String DRAW_IMAGE_ICON = "org/openoffice/extensions/filetypes/odf/Galaxy_OOo3_draw-doc_16.png"; // NOI18N
    private static final String CHART_IMAGE_ICON = "org/openoffice/extensions/filetypes/odf/Galaxy_OOo3_chart-doc_16.png"; // NOI18N
    private static final String CALC_IMAGE_ICON = "org/openoffice/extensions/filetypes/odf/Galaxy_OOo3_calc-doc_16.png"; // NOI18N
    private static final String BASE_IMAGE_ICON = "org/openoffice/extensions/filetypes/odf/Galaxy_OOo3_base-doc_16.png"; // NOI18N

    private String iconString;
    
    public OdfDataObject(FileObject pf, OdfDataLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        String ext = pf.getExt();
        if (ext.endsWith("odt")) { // NOI18N
            iconString = WRITER_IMAGE_ICON;
        }
        else if (ext.endsWith("ods")) { // NOI18N
            iconString = CALC_IMAGE_ICON;
        }
        else if (ext.endsWith("odb")) { // NOI18N
            iconString = BASE_IMAGE_ICON;
        }
        else if (ext.endsWith("odp")) { // NOI18N
            iconString = IMPRESS_IMAGE_ICON;
        }
        else if (ext.endsWith("odf")) { // NOI18N
            iconString = MATH_IMAGE_ICON;
        }
        else if (ext.endsWith("odc")) { // NOI18N
            iconString = CHART_IMAGE_ICON;
        }
        else if (ext.endsWith("odg")) { // NOI18N
            iconString = DRAW_IMAGE_ICON;
        }
        else { // fallback
            iconString = GENERAL_IMAGE_ICON;
        }
        CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
    }

    @Override
    protected Node createNodeDelegate() {
        return new OdfDataNode(this, getLookup());
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }
    
    public String getIconString() {
        return iconString;
    }
}
