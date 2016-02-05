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

package org.openoffice.extensions.projecttemplates.calcaddin.datamodel.node;

import java.awt.Image;
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
import org.openoffice.extensions.projecttemplates.calcaddin.AddinActions;
import org.openoffice.extensions.util.datamodel.Function;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.PropertyContainer;

/**
 *
 * @author sg128468
 */
public class AddInNode extends AbstractNode implements PropertyChangeListener {
    
    Action[] actions;
    
    // node has subnodes
    public static final String NODE_ICON = 
            "org/openoffice/extensions/projecttemplates/calcaddin/icons/method.png"; // NOI18N
    public static final String PARAMETER_ICON = 
            "org/openoffice/extensions/projecttemplates/calcaddin/icons/parameter.png"; // NOI18N
    public static final String SIMPLE_ICON = 
            "org/openoffice/extensions/projecttemplates/calcaddin/icons/name.png"; // NOI18N
    public static final String DESC_ICON = 
            "org/openoffice/extensions/projecttemplates/calcaddin/icons/description.png"; // NOI18N
    
    public AddInNode(NbNodeObject obj, Children children) {
        super (children, Lookups.singleton(obj));
        if (obj.hasActions(NbNodeObject.ADDIN_TYPE)) {
            actions = obj.getActions(false);
        }
    }

    public AddInNode() {
        super (new AddInChildren());
        setDisplayName ("Root"); // NOI18N
    }
    
    public Image getIcon (int type) {
        String icon = SIMPLE_ICON;
        Object o = this.getLookup().lookup(NbNodeObject.class);
        if (o instanceof PropertyContainer) {
            if (o instanceof Function) {
                icon = NODE_ICON;
            }
            else {
                icon = PARAMETER_ICON;
            }
        }
        if (o instanceof LocalizedOpenOfficeOrgProperty) {
            icon = DESC_ICON;
        }
        return ImageUtilities.loadImage(icon);
    }    

    public String getDisplayName() {
        NbNodeObject object = (NbNodeObject)getLookup().lookup(NbNodeObject.class);
        String displayName = object.getDisplayName();
        if (displayName.equals(super.getDisplayName())) {
            return displayName;
        }
        setDisplayName(displayName);
        return displayName;
    }

    public Image getOpenedIcon (int type) {
        return getIcon(type);
    }

    public Action[] getActions(boolean context) {
        if (actions == null)
            return super.getActions(context);
        return actions;
    }
    
    /**
     * set AddinActions into the actions of this node, the AddinActions
     * are used to perform the actions available in the context menu of the node
     */
    public void setActions(AddinActions addinActions) {
        if (actions != null) {
            NbNodeObject o = (NbNodeObject)this.getLookup().lookup(NbNodeObject.class);
            // if the object should have actions for this type, 
            // give to it the base action class to wrap
            if (o.hasActions(NbNodeObject.ADDIN_TYPE)) {
                // rename setActions to something less confusing?
                o.setActions((BaseAction)addinActions);
            }
        }
        Children c = this.getChildren();
        Node[] subnodes = c.getNodes();
        for (int i=0; i<subnodes.length; i++)
            ((AddInNode)subnodes[i]).setActions(addinActions);
    }
    
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        NbNodeObject object = (NbNodeObject)getLookup().lookup(NbNodeObject.class);
        // create the properties in the object and let the object set them
        object.createProperties(sheet, this);
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
            ((AddInNode)subnodes[i]).triggerLanguageChange();
    }
}
