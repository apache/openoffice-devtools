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
package org.openoffice.extensions.filetypes.registry_ui;

import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;
import org.openoffice.extensions.util.LogWriter;

public class RegistryWizardPanel1 implements WizardDescriptor.Panel {

    public static final String XCU_FILE_TYPE = "xcu";
    public static final String XCS_FILE_TYPE = "xcs";
    
    private String m_fileType;

    public RegistryWizardPanel1(String fileType) {
        this.m_fileType = fileType;
    }
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private RegistryVisualPanel1 component;
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            component = new RegistryVisualPanel1(m_fileType);
        }
        return component;
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
    // If you have context help:
    // return new HelpCtx(SampleWizardPanel1.class);
    }

    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
    // If it depends on some condition (form filled out...), then:
    // return someCondition();
    // and when this condition changes (last form field filled in...) then:
    // fireChangeEvent();
    // and uncomment the complicated stuff below.
    }

    public final void addChangeListener(ChangeListener l) {
    }

    public final void removeChangeListener(ChangeListener l) {
    }
    /*
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0
    public final void addChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.add(l);
    }
    }
    public final void removeChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.remove(l);
    }
    }
    protected final void fireChangeEvent() {
    Iterator<ChangeListener> it;
    synchronized (listeners) {
    it = new HashSet<ChangeListener>(listeners).iterator();
    }
    ChangeEvent ev = new ChangeEvent(this);
    while (it.hasNext()) {
    it.next().stateChanged(ev);
    }
    }
     */

    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(Object settings) {
        component.read((WizardDescriptor)settings);
    }

    public void storeSettings(Object settings) {
        component.store((WizardDescriptor)settings);
    }
    
    public static boolean unZipFile(FileObject target, FileObject source) throws IOException {
        if (target == null || source == null) return false;
        InputStream in = source.getInputStream();
        try {
            FileLock lock = target.lock();
            try {
                OutputStream out = target.getOutputStream(lock);
                try {
                    FileUtil.copy(in, out);
                }
                catch (IOException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                } finally {
                    out.close();
                }
            } finally {
                lock.releaseLock();
            }
        }
        catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
        return true;
    }
    
}

