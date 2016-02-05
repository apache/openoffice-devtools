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

package org.openoffice.extensions.projecttemplates.addon.datamodel;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyEditorSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openoffice.extensions.config.ConfigurationSettings;
import org.openoffice.extensions.projecttemplates.addon.AddOnWizardIterator;
import org.openoffice.extensions.util.LogWriter;

/**
 *
 * @author sg128468
 */
public class IconCustomEditor extends PropertyEditorSupport 
            implements VetoableChangeListener, ExPropertyEditor {
    
    // this list must be alphabetically sorted for Arrays.binarySearch to work!
    private static String[] approvedImageTypeExtensions = new String[] {
        "bmp", "gif", "jpeg", "jpg", "png",
    }; // NOI18N

    private String m_text;
    private PropertyEnv m_propertyEnv;
    private InnerIconCustomEditor m_innerCustomEditor;
    private String m_path;
    
    /** Creates new form IconCustomEditor */
    public IconCustomEditor() {
        m_innerCustomEditor = new InnerIconCustomEditor();
    }

    @Override
    public Component getCustomEditor() {
        m_innerCustomEditor.setCurrentDirectory(ConfigurationSettings.getDefaultFileChooserStartingDir());
        return m_innerCustomEditor;
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public boolean isPaintable() {
        return true;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
        Object o = getValue();
        Image img = null;
        if (o instanceof Image)
            img = (Image)o;

        ImageObserver ob = new ImageObserver() {
           public boolean imageUpdate(Image i, int infoflags,  int x, int y, int width, int height) {
               return false;
           }
        };
        Rectangle resize = calculateSize(img, box, ob);
        if (gfx.drawImage(img, resize.x, resize.y, resize.width, resize.height, ob)) {
            m_text = null;
        }
        else {
            m_text = NbBundle.getMessage(AddOnWizardIterator.class, "TF_IconCustomEditorErrorMessage");
        }
    }

    
    @Override
    public String getAsText() {
        return m_text;
    }

    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if (PropertyEnv.PROP_STATE.equals(evt.getPropertyName())) { 
            File f = m_innerCustomEditor.getSelectedFile();
            try {
                if (f != null) {
                    if (!f.isDirectory()) { // why should it be a directory? but anyway
                        ConfigurationSettings.storeDefaultFileChooserStartingDir(f.getParentFile());
                    }
                    else {
                        ConfigurationSettings.storeDefaultFileChooserStartingDir(f);
                    }

                    String path = f.getCanonicalPath();
                    // first load the image icon, makes obtaining the image easy
                    ImageIcon icon = new ImageIcon(path);
                    if (icon != null) { // store the icon and the path there
                        m_path = path;
                        setValue(icon.getImage()); // this triggers setValue in OOOIconProperty! -> set path first
                    }
                }
            } catch (IllegalArgumentException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
    }

    public void attachEnv(PropertyEnv propertyEnv) {
        this.m_propertyEnv = propertyEnv;
        propertyEnv.setState(PropertyEnv.STATE_NEEDS_VALIDATION);
        propertyEnv.addVetoableChangeListener(this);
    }
    
//    public static Rectangle calculateSize(Image img, Rectangle box, Rectangle minmum, ImageObserver ob) {
//            
//    }
    
    public String getPath() {
        return m_path;
    }
    
    
    public static Rectangle calculateSize(Image img, Rectangle box, ImageObserver ob) {
        Rectangle resized = new Rectangle(box);
        if (img == null) return resized;
        double scale = (double)img.getWidth(ob) / (double)img.getHeight(ob);
        if (box.getHeight() * scale > box.getWidth()) {
            // have to scale the height
            int height = (int)(scale * resized.width);
            resized.setSize(resized.width, height);
        }
        else {
            // have to scale the width
            int width = (int)(scale * resized.height);
            resized.setSize(width, resized.height);
        }
        return resized;
    }
    
    public class InnerIconCustomEditor extends JFileChooser {
    
        /** Creates a new instance of IconCustomEditor2 */
        public InnerIconCustomEditor() {
            super();
            this.setControlButtonsAreShown(false);
            this.setFileFilter(new FileFilter(){
                public boolean accept(File f) {
                    if (f.isDirectory()) return true;
                    try {
                        String name = f.getCanonicalPath();
                        String extension = name.substring(name.lastIndexOf('.') + 1);
                        return Arrays.binarySearch(approvedImageTypeExtensions, extension) >= 0;
                    } catch (IOException ex) {
                        LogWriter.getLogWriter().printStackTrace(ex);
                    }
                    return false;
                }
                public String getDescription() {
                    return NbBundle.getMessage(AddOnWizardIterator.class, "LBL_FileChooser_SupportedFiles");
                }
            });
        }

        @Override
        public File getSelectedFile() {
            return super.getSelectedFile();
        }
    }
}    
    