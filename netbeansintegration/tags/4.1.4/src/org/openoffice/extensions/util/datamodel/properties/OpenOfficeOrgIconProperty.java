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

package org.openoffice.extensions.util.datamodel.properties;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openoffice.extensions.projecttemplates.addon.datamodel.IconCustomEditor;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;

/**
 *
 * @author sg128468
 */
public class OpenOfficeOrgIconProperty implements OpenOfficeOrgProperty, NbNodeObject {
    
    String name;
    Image value;
    String path;
    
    private String description;
    private IconCustomEditor editor;
    private NbNodeObject parent;
    
    /** Creates a new instance of OpenOfficeOrgIconProperty */
    public OpenOfficeOrgIconProperty(String name, String path, String longDescription, NbNodeObject parent, IconCustomEditor editor) {
        this.name = name;
        this.path = path;
        if (path != null) {
            ImageIcon icon = new ImageIcon(path);
            value = icon.getImage();
        }
        this.editor = editor;
        this.parent = parent;
        this.description = longDescription;
    }

    public boolean isLocalized() {
        return false;
    }

    public String getPropertyName() {
        return name;
    }

    public void setPropertyName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Image getImage() {
        return value;
    }
    
    public String getValueForLanguage(int langID) throws UnknownOpenOfficeOrgLanguageIDException {
        return path;
    }

    public void removeValueForLanguage(int langID) throws UnknownOpenOfficeOrgLanguageIDException {
        value = null;
    }

    public void setValueForLanguage(int langID, String value) throws UnknownOpenOfficeOrgLanguageIDException {
        path = value;
    }

    public NbNodeObject getParent() {
        return parent;
    }

    public String getDisplayName() {
        return name;
    }

    public NbNodeObject[] getAllSubObjects() {
        return null;
    }

    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
        Node.Property nameProp = new IconProperty();
        if (sheet != null) {
            Sheet.Set set = Sheet.createPropertiesSet();
            set.put(nameProp);
            sheet.put(set);
        }
        if (listener != null) {
            nameProp.getPropertyEditor().addPropertyChangeListener(listener);
        }
        return new Node.Property[]{nameProp};
    }

    public boolean hasActions(int type) {
        return false;
    }

    public Action[] getActions(boolean context) {
        return null;
    }

    public void setActions(BaseAction actions) {
    }

    public int getType() {
        return NbNodeObject.PROPERTY_TYPE; // own type for this??
    }
    
    public class IconProperty extends Node.Property<Image> {
        public IconProperty() {
            super(Image.class);
            setName(getPropertyName());
            this.setShortDescription(description);
        }
        public Image getValue() {
            return value;
        }
        public void setValue(Image v) {
            value = v;
            try { // rather clumsy way to set the path of the image
                setValueForLanguage(-1, editor.getPath());
            } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
                Exceptions.printStackTrace(ex);
            }
//            if (v instanceof String) {
//                path = (String)v;
//                // first load the image icon, makes obtaining the image easy
////                ImageIcon icon = new ImageIcon(path);
////                value = icon.getImage();
//                try {
//                    value = ImageIO.read(new File(path));
//                } catch (IOException ex) {
//                    LogWriter.getLogWriter().printStackTrace(ex);
//                }
//            }
//            if (v instanceof String) {
//                path = (String)v;
//                // first load the image icon, makes obtaining the image easy
////                ImageIcon icon = new ImageIcon(path);
////                value = icon.getImage();
//                try {
//                    value = ImageIO.read(new File(path));
//                } catch (IOException ex) {
//                    LogWriter.getLogWriter().printStackTrace(ex);
//                }
//            }
        }
        @Override
        public PropertyEditor getPropertyEditor() {
            if (editor == null)
                return super.getPropertyEditor();
            return editor;
        }
        public boolean canRead() {
            return true;
        }
        public boolean canWrite() {
            return true;
        }
        public NbNodeObject getPropertyParent() {
            return getParent();
        }
    }    
}
