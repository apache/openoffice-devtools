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

package org.openoffice.extensions.util.typebrowser;

import java.awt.dnd.DropTarget;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.swing.tree.TreeModel;
import org.openide.ErrorManager;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
final class UnoIdlTypeBrowserTopComponent extends TopComponent implements ExplorerManager.Provider, PropertyChangeListener {
    
    private static final long serialVersionUID = 1L;
    private static UnoIdlTypeBrowserTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/openoffice/extensions/util/typebrowser/idlfile.png"; // NOI18N
    private static final String PREFERRED_ID = "UnoIdlTypeBrowserTopComponent"; // NOI18N
    private transient ExplorerManager explorerManager = new ExplorerManager();
    
    /**
     * panel to show if no type browser is available
     */
    javax.swing.JPanel noViewJPanel;
    
    private UnoIdlTypeBrowserTopComponent() {
        initComponents();
        
//        associateLookup(ExplorerUtils.createLookup(explorerManager, getActionMap()));
//        explorerManager.setRootContext(new MenuElementNode(
//                UIElementType.createInitialUIStructure(), new UIElementChildren()));

/*        setName(NbBundle.getMessage(UnoIdlTypeBrowserTopComponent.class, "CTL_UnoIdlTypeBrowserTopComponent"));
        setToolTipText(NbBundle.getMessage(UnoIdlTypeBrowserTopComponent.class, "HINT_UnoIdlTypeBrowserTopComponent"));
        setIcon(Utilities.loadImage(ICON_PATH, true));
        noViewJPanel = new TypeBrowserNotActiveJPanel(
            NbBundle.getMessage(UnoIdlTypeBrowserTopComponent.class, "CTL_NoView"), 
            NbBundle.getMessage(UnoIdlTypeBrowserTopComponent.class, "HINT_Installation"),
            NbBundle.getMessage(UnoIdlTypeBrowserTopComponent.class, "CTL_AddOffice")
        );
        setViewportView();
        ConfigurationSettings settings = ConfigurationSettings.getSettings();
        settings.registerPropertyChangeListener(settings.KEY_OFFICE_INSTALLATION, this); */
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jScrollPane2 = new BeanTreeView();

        jScrollPane1.setViewportView(jTree1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized UnoIdlTypeBrowserTopComponent getDefault() {
        if (instance == null) {
            instance = new UnoIdlTypeBrowserTopComponent();
        }
        return instance;
    }
    
    /**
     * Obtain the UnoIdlTypeBrowserTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized UnoIdlTypeBrowserTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, 
                "Cannot find UnoIdlTypeBrowser component. It will not be located properly in the window system."); // NOI18N
            return getDefault();
        }
        if (win instanceof UnoIdlTypeBrowserTopComponent) {
            return (UnoIdlTypeBrowserTopComponent)win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING, 
            "There seem to be multiple components with the '" + PREFERRED_ID + "' ID. That is a potential source of errors and unexpected behavior."); // NOI18N
        return getDefault();
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    public void componentOpened() {
        // TODO add custom code on component opening
    }
    
    public void componentClosed() {
        // TODO add custom code on component closing
    }
    
    /** replaces this in object stream */
    public Object writeReplace() {
        return new ResolvableHelper();
    }
    
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            return UnoIdlTypeBrowserTopComponent.getDefault();
        }
    }
    
    private TreeModel getTreeModel() {
        TreeModel model = null;
//        try {
//
//            TypeBrowser tb = new TypeBrowser();//TypeBrowser.getTypeBrowserInstance();
//            DefaultMutableTreeNode rootNode = tb.getAllTypeNodes();

//            if (rootNode != null)
//                model = new DefaultTreeModel(rootNode);
//        }
//        catch( Exception e) {
//            e.printStackTrace(System.out);
//        }
        return model;
    }

    private void setViewportView() {
        TreeModel model = getTreeModel();
        if (model == null) {
            jScrollPane1.setViewportView(noViewJPanel);
        }
        else {
            jTree1.setModel(model);
            jScrollPane1.setViewportView(jTree1);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
//        setViewportView();
    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

}
