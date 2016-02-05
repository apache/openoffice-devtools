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

package org.openoffice.extensions.projecttemplates.addon.datamodel.node;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;
import org.openoffice.extensions.projecttemplates.addon.datamodel.AddOn;
import org.openoffice.extensions.projecttemplates.addon.datamodel.Command;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 * @author sg128468
 */
public class AddOnNode extends AbstractNode implements PropertyChangeListener {
    
    Action[] actions;
    
    // node has subnodes
    public static final String METHOD_ICON = 
            "org/openoffice/extensions/projecttemplates/component/icons/method.png"; // NOI18N
    public static final String INTERFACE_ICON = 
            "org/openoffice/extensions/projecttemplates/component/icons/interface.png"; // NOI18N
    public static final String FOLDER_CLOSED_ICON = 
            "org/openoffice/extensions/projecttemplates/component/icons/folder.png"; // NOI18N
    public static final String FOLDER_OPEN_ICON = 
            "org/openoffice/extensions/projecttemplates/component/icons/folderopen.png"; // NOI18N
    private static final String SEPARATOR_ICON = 
            "org/openoffice/extensions/projecttemplates/addon/icons/separator.gif";  // NOI18N
    
    boolean displayCommandProps;
    boolean showSeparatorIcon;
    
    public AddOnNode(NbNodeObject obj, Children children) {
        this (obj, children, true, false);
    }

    public AddOnNode(NbNodeObject obj, Children children, boolean displayCommandProps, boolean showSeparatorIcon) {
        super (children, Lookups.singleton(obj));
        if (obj.hasActions(NbNodeObject.ADDON_TYPE)) {
            actions = obj.getActions(false);
        }
        this.displayCommandProps = displayCommandProps;
        this.showSeparatorIcon = showSeparatorIcon;
    }

    public AddOnNode() {
        super(new AddOnChildren());
        setDisplayName ("Root"); // NOI18N
    }

    public Image getOpenedIcon (int type) {
        return getIcon(type, true);
    }

    public Image getIcon (int type) {
        return getIcon(type, false);
    }    

    private Image getIcon(int type, boolean opened) {
        String icon = null;
        NbNodeObject o = (NbNodeObject)this.getLookup().lookup(NbNodeObject.class);
        if (o.getType() == NbNodeObject.UI_MENU_TYPE) {
            if (opened)
                icon = FOLDER_OPEN_ICON;
            else 
                icon = FOLDER_CLOSED_ICON;
        }
        else if (o.getType() == NbNodeObject.FUNCTION_TYPE) {
            Image img = getIcon((Command)o);
            if (img == null)  // fallback
                icon = METHOD_ICON;
            else 
                return img; // bad: exit from within method...
        }
        else if (showSeparatorIcon && o.getType() == NbNodeObject.UI_SEPARATOR_TYPE) {
            icon = SEPARATOR_ICON;
        }
        else {
            return new VolatileImage() {
                public BufferedImage getSnapshot() {
                    return null;
                }

                public int getWidth() {
                    return 0;
                }

                public int getHeight() {
                    return 0;
                }

                public Graphics2D createGraphics() {
                    return null;
                }

                public int validate(GraphicsConfiguration gc) {
                    return VolatileImage.IMAGE_INCOMPATIBLE;
                }

                public boolean contentsLost() {
                    return true;
                }

                public ImageCapabilities getCapabilities() {
                    return null;
                }

                public int getWidth(ImageObserver observer) {
                    return 0;
                }

                public int getHeight(ImageObserver observer) {
                    return 0;
                }

                public Object getProperty(String name, ImageObserver observer) {
                    return null;
                }
            };
        }
        return ImageUtilities.loadImage(icon);
    }

    public boolean isMenu() {
        Object o = this.getLookup().lookup(NbNodeObject.class);
        if (o instanceof AddOn) {
            return true;
        }
        return false;
    }
    
    public String getDisplayName() {
        NbNodeObject object = (NbNodeObject)getLookup().lookup(NbNodeObject.class);
        String displayName = object.getDisplayName();
        if (!displayName.equals("AddOn")) { // NOI18N
            this.fireCookieChange();  // trigger isValid function for catching equal command names
        }
        if (displayName.equals(super.getDisplayName())) {
            return displayName;
        }
        setDisplayName(displayName);
        return displayName;
    }

    public Action[] getActions(boolean context) {
        if (actions == null)
            return super.getActions(context);
        return actions;
    }
    
    /**
     * set AddOnActions into the actions of this node, the AddOnActions
     * are used to perform the actions available in the context menu of the node
     */
    public void setActions(BaseAction addonActions) {
        if (actions != null) {
            NbNodeObject o = (NbNodeObject)this.getLookup().lookup(NbNodeObject.class);
            // if the object should have actions for this type, 
            // give to it the base action class to wrap
            if (o.hasActions(NbNodeObject.ADDON_TYPE)) {
                // rename setActions to something less confusing?
                o.setActions(addonActions);
            }
        }
        Children c = this.getChildren();
        if (c != null) {
            Node[] subnodes = c.getNodes();
            for (int i=0; i<subnodes.length; i++)
                ((AddOnNode)subnodes[i]).setActions(addonActions);
        }
    }
    
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        NbNodeObject object = (NbNodeObject)getLookup().lookup(NbNodeObject.class);
        // create the properties in the object and let the object set them
        if (object.getType() == NbNodeObject.FUNCTION_TYPE) {
            Command command = (Command)object;
            command.createProperties(sheet, this, displayCommandProps);
        }
        else {
            object.createProperties(sheet, this);
        }
        return sheet;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        this.fireCookieChange();
        this.fireDisplayNameChange("o", "n"); // NOI18N
        if (this.getParentNode() != null) {
            ((PropertyChangeListener)this.getParentNode()).propertyChange(evt);
        }
    }
    
    public void triggerChangeEvent() {
        this.fireCookieChange();
        this.fireDisplayNameChange("o", "n"); // NOI18N
    }
    
    public void triggerLanguageChange() {
        NbNodeObject object = (NbNodeObject)getLookup().lookup(NbNodeObject.class);
        // create the properties in the object and let the object set them
        // this triggers the update of the property sheet
        Sheet newSheet = this.getSheet();
        Property[] newSet = object.createProperties(newSheet, this); 
        this.setSheet(newSheet);

// fire property changes did not work: properties were not updated.                 
//        PropertySet[] propset = this.getPropertySets();
//        this.firePropertySetsChange(propset, null);
//        for(int i=0; i<propset.length; i++) {
//            Property[] prop = propset[i].getProperties();
//            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "  " + propset[i].getDisplayName()); // NOI18N
//            for (int j=0; j<prop.length; j++) {
//                    LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "    " + prop[j].getDisplayName()); // NOI18N
//                    this.firePropertyChange(prop[j].getDisplayName(), null, ""); // NOI18N
//                }
//            }
//        }

        Node[] subnodes = this.getChildren().getNodes();
        for (int i=0; i<subnodes.length; i++)
            ((AddOnNode)subnodes[i]).triggerLanguageChange();
    }
    
    private Image getIcon(Command com) {
        String[] iconPropNames = new String[] {
            com.PROPERTY_CONTAINER_ICON_LOWRES_SMALL,
            com.PROPERTY_CONTAINER_ICON_LOWRES_BIG,
            com.PROPERTY_CONTAINER_ICON_HIRES_SMALL,
            com.PROPERTY_CONTAINER_ICON_HIRES_BIG,
        };
        Image img = null;
        OpenOfficeOrgIconProperty iconProp = null;
        try {
            for (int i = 0; i < iconPropNames.length; i++) {
                iconProp = (OpenOfficeOrgIconProperty)
                    com.getProperty(iconPropNames[i]);
                if (iconProp.getImage() != null) {
                    img = iconProp.getImage();
                }
            }
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        if (img != null)
            return img.getScaledInstance(24, 24, Image.SCALE_FAST);
        return null;
    }
}
