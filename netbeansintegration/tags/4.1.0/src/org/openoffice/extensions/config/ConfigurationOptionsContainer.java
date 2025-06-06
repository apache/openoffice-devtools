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

package org.openoffice.extensions.config;

import org.openide.util.NbBundle;

/**
 *
 * @author  sg128468
 */
public class ConfigurationOptionsContainer extends javax.swing.JPanel {
    
    private ConfigurationPanel m_ConfigurationPanel;
    private LogOutputSettingsDialog m_LogSettingsPanel;
    
    /** Creates new form ConfigurationOptionsContainer */
    public ConfigurationOptionsContainer(String officeInstallation, String sdkInstallation) {
        initComponents();
        m_ConfigurationPanel = new ConfigurationPanel(officeInstallation, sdkInstallation);
        m_LogSettingsPanel = new LogOutputSettingsDialog();
        String panel1Title = NbBundle.getMessage(ConfigurationPanel.class, "ConfigurationDialog_ShortTitle");
        String panel2Title = NbBundle.getMessage(ConfigurationPanel.class, "LogOuptutSettingsDialog_ShortTitle");
        m_ConfigurationTabs.addTab(panel1Title, m_ConfigurationPanel);
        m_ConfigurationTabs.addTab(panel2Title, m_LogSettingsPanel);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_ConfigurationTabs = new javax.swing.JTabbedPane();

        m_ConfigurationTabs.setBackground(new java.awt.Color(238, 238, 238));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(m_ConfigurationTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(m_ConfigurationTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane m_ConfigurationTabs;
    // End of variables declaration//GEN-END:variables
    
    public void setOffice(String office) {
        m_ConfigurationPanel.setOffice(office);
    }
    
    public void setSDK(String sdk) {
        m_ConfigurationPanel.setSDK(sdk);
    }

    public String getOffice() {
        return m_ConfigurationPanel.getOffice();
    }
    
    public String getSDK() {
        return m_ConfigurationPanel.getSDK();
    }
    
    public void updateLogging() {
        m_LogSettingsPanel.initializeFields();
    }

    public String getLogLevel() {
        return m_LogSettingsPanel.getLogLevel();
    }
    
    public String getLogFile() {
        return m_LogSettingsPanel.getLogFile();
    }
    
    public boolean valid() {
        return m_ConfigurationPanel.valid() && m_LogSettingsPanel.valid();
    }
}
