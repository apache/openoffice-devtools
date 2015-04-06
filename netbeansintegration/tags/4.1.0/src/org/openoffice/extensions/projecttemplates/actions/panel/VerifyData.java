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
package org.openoffice.extensions.projecttemplates.actions.panel;

import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author sg128468
 */
public class VerifyData {

    private static VerifyData theVerifyer;

    public static VerifyData getVerifyer() {
        if (theVerifyer == null) {
            theVerifyer = new VerifyData();
        }
        return theVerifyer;
    }

    private final int NO_ERROR = 0;
    private final int WARNING = 1;
    private final int ERROR = 2;
    
    private String mErrorMessage;
    private int mErrorLevel;
    
    
    private VerifyData() {
    }
    
    private void reset() {
        mErrorLevel = NO_ERROR;
        mErrorMessage = "";
    }
    
    /**
     * verify the data from the data handler: verifies all but localized 
     * and UI stuff
     * @param m_Handler the data handler
     * @return true if data is ok.
     */
    public boolean verifyData(DataHandler m_Handler) {
        reset();
        boolean result = true;
        String identifier = m_Handler.getIdentifier();
        if (identifier != null && identifier.length() > 0 && identifier.indexOf('.') == -1) {
            result = false;
            mErrorLevel = WARNING;
            mErrorMessage = NbBundle.getMessage(VerifyData.class, "VerifyData.ErrorMessage.Identifier");
        }
        String version = m_Handler.getVersion();
        // regexp matches numbers followed by a dot followed by numbers
        final String regexp = "\\d+(\\.\\d+)*";
        if (version!= null && version.length() > 0 && !version.matches(regexp)) {
            result = false;
            mErrorLevel = WARNING;
            mErrorMessage = NbBundle.getMessage(VerifyData.class, "VerifyData.ErrorMessage.Version");
        }
        String depVersion = m_Handler.getDependencyNumber();
        if (depVersion!= null && depVersion.length() > 0 && !depVersion.matches(regexp)) { // same regexp as before
            result = false;
            mErrorLevel = WARNING;
            mErrorMessage = NbBundle.getMessage(VerifyData.class, "VerifyData.ErrorMessage.DepVersion");
        }
        return result;
    }
    
    /**
     * verify the localized data and UI stuff from the data handler
     * @param m_Handler the data handler
     * @return true if data is ok.
     */
    public boolean verifyLocalizedData(DataHandler m_Handler) {
        reset();

        GenericDescriptionProperty<String> displayData = m_Handler.getDisplayData();
        GenericDescriptionProperty<String> descriptionData = m_Handler.getDescriptionData();
        GenericDescriptionProperty<String> licenseFiles = m_Handler.getLicenseFiles();
        GenericDescriptionProperty<String[]> publisherData = m_Handler.getPublisherData();
        String[] displayLocales = displayData.getAllLocales();
        String[] descriptionLocales = descriptionData.getAllLocales();
        String[] licenseLocales = licenseFiles.getAllLocales();
        String[] publisherLocales = publisherData.getAllLocales();
        // get most locales
        String[] localeList = getBiggestLocaleArray(displayLocales, descriptionLocales, publisherLocales, licenseLocales);
        for (int i = 0; i < localeList.length; i++) {
            String locale = localeList[i];
            // at least one entry must be there
            if (displayLocales != null && displayLocales.length > 0) {
                if (displayData.getPropertyForLocale(locale) == null) {
                    mErrorLevel = WARNING;
                    buildErrorMessage(NbBundle.getMessage(VerifyData.class, "VerifyData.ErrorMessage.DisplayName"), locale);
                    return false; // fast exit
                }
            }
            if (descriptionLocales != null && descriptionLocales.length > 0) {
                if (descriptionData.getPropertyForLocale(locale) == null) {
                    mErrorLevel = WARNING;
                    buildErrorMessage(NbBundle.getMessage(VerifyData.class, "VerifyData.ErrorMessage.Description"), locale);
                    return false; // fast exit
                }
            }
            if (licenseLocales != null && licenseLocales.length > 0) {
                if (licenseFiles.getPropertyForLocale(locale) == null) {
                    mErrorLevel = WARNING;
                    buildErrorMessage(NbBundle.getMessage(VerifyData.class, "VerifyData.ErrorMessage.License"), locale);
                    return false; // fast exit
                }
            }
            if (publisherLocales != null && publisherLocales.length > 0) {
                if (publisherData.getPropertyForLocale(locale) == null) {
                    mErrorLevel = WARNING;
                    buildErrorMessage(NbBundle.getMessage(VerifyData.class, "VerifyData.ErrorMessage.DisplayName"), locale);
                    return false; // fast exit
                }
                else { // realyy needed? test it!
                    
                }
            }
        }
        return true;
    }

    private void buildErrorMessage(String message, String locale) {
        mErrorMessage = message.replace("$1", locale);
    }
    
    private String[] getBiggestLocaleArray(String[] locale1, String[] locale2, String[] locale3, String[] locale4) {
        String[] returnLocale = new String[0];
        if (locale1 != null && returnLocale.length < locale1.length) {
            returnLocale = locale1;
        }
        if (locale2 != null && returnLocale.length < locale2.length) {
            returnLocale = locale2;
        }
        if (locale3 != null && returnLocale.length < locale3.length) {
            returnLocale = locale3;
        }
        if (locale4 != null && returnLocale.length < locale4.length) {
            returnLocale = locale4;
        }
        return returnLocale;
    }
    
    public boolean allIsOK() {
        return mErrorLevel == NO_ERROR;
    }
    
    public String getErrorMessage() {
        if (!allIsOK()) {
            return mErrorMessage;
        }
        return "";
    }
    
    public Icon getErrorIcon() {
        String icon = "org/openoffice/extensions/projecttemplates/component/icons/error.png"; // NOI18N
        ImageIcon imageIcon = null;
        Image img = null;
        switch(mErrorLevel) {
            case ERROR:
                img = ImageUtilities.loadImage(icon);
                imageIcon = new ImageIcon(img);
                break;
            case WARNING:
                img = ImageUtilities.loadImage(icon);
                imageIcon = new ImageIcon(img);
                break;
            default:
                // icon stays null in this case
        }
        return imageIcon;
    }
}
