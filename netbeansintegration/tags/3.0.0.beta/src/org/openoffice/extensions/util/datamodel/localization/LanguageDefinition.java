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

package org.openoffice.extensions.util.datamodel.localization;

import org.openide.util.NbBundle;

/**
 *
 * @author sg128468
 */
public class LanguageDefinition {
    
    // id's for languages: not allowed to have 'gaps'
    public static final int LANGUAGE_ID_sq = 0;
    public static final int LANGUAGE_ID_ar = 1;
    public static final int LANGUAGE_ID_hy = 2;
    public static final int LANGUAGE_ID_az = 3;
    public static final int LANGUAGE_ID_eu = 4;
    public static final int LANGUAGE_ID_bn = 5;
    public static final int LANGUAGE_ID_bra = 6;
    public static final int LANGUAGE_ID_bre = 7;
    public static final int LANGUAGE_ID_bg = 8;
    public static final int LANGUAGE_ID_ca = 9;
    public static final int LANGUAGE_ID_zh_CN = 10;
    public static final int LANGUAGE_ID_zh_TW = 11;
    public static final int LANGUAGE_ID_cs = 12;
    public static final int LANGUAGE_ID_hr = 13;
    public static final int LANGUAGE_ID_da = 14;
    public static final int LANGUAGE_ID_nl = 15;
    public static final int LANGUAGE_ID_en = 16;
    public static final int LANGUAGE_ID_en_US = 17;
    public static final int LANGUAGE_ID_eo = 18;
    public static final int LANGUAGE_ID_et = 19;
    public static final int LANGUAGE_ID_fi = 20;
    public static final int LANGUAGE_ID_fr = 21;
    public static final int LANGUAGE_ID_gl = 22;
    public static final int LANGUAGE_ID_ga = 23;
    public static final int LANGUAGE_ID_gd = 24;
    public static final int LANGUAGE_ID_ka = 25;
    public static final int LANGUAGE_ID_de = 26;
    public static final int LANGUAGE_ID_el = 27;
    public static final int LANGUAGE_ID_gu = 28;
    public static final int LANGUAGE_ID_he = 29;
    public static final int LANGUAGE_ID_hi = 30;
    public static final int LANGUAGE_ID_hu = 31;
    public static final int LANGUAGE_ID_id = 32;
    public static final int LANGUAGE_ID_it = 33;
    public static final int LANGUAGE_ID_ja = 34;
    public static final int LANGUAGE_ID_km = 35;
    public static final int LANGUAGE_ID_ko = 36;
    public static final int LANGUAGE_ID_lo = 37;
    public static final int LANGUAGE_ID_lt = 38;
    public static final int LANGUAGE_ID_mk = 39;
    public static final int LANGUAGE_ID_ml = 40;
    public static final int LANGUAGE_ID_mr = 41;
    public static final int LANGUAGE_ID_ms = 42;
    public static final int LANGUAGE_ID_ne = 43;
    public static final int LANGUAGE_ID_no = 44;
    public static final int LANGUAGE_ID_fa = 45;
    public static final int LANGUAGE_ID_pl = 46;
    public static final int LANGUAGE_ID_pt = 47;
    public static final int LANGUAGE_ID_br = 48;
    public static final int LANGUAGE_ID_pa = 49;
    public static final int LANGUAGE_ID_ro = 50;
    public static final int LANGUAGE_ID_ru = 51;
    public static final int LANGUAGE_ID_sg = 52;
    public static final int LANGUAGE_ID_sr = 53;
    public static final int LANGUAGE_ID_si = 54;
    public static final int LANGUAGE_ID_sl = 55;
    public static final int LANGUAGE_ID_sk = 56;
    public static final int LANGUAGE_ID_es = 57;
    public static final int LANGUAGE_ID_sv = 58;
    public static final int LANGUAGE_ID_tg = 59;
    public static final int LANGUAGE_ID_ta = 60;
    public static final int LANGUAGE_ID_te = 61;
    public static final int LANGUAGE_ID_tet = 62;
    public static final int LANGUAGE_ID_th = 63;
    public static final int LANGUAGE_ID_bo = 64;
    public static final int LANGUAGE_ID_tr = 65;
    public static final int LANGUAGE_ID_uk_UA = 66;
    public static final int LANGUAGE_ID_vi = 67;
    public static final int LANGUAGE_ID_cy = 68;
    public static final int LANGUAGE_COUNT = 69;

    private static String[] languageLongName = new String[] {
        NbBundle.getMessage(LanguageDefinition.class, "Albanian").concat("(sq)"),
        NbBundle.getMessage(LanguageDefinition.class, "Arabic").concat("(ar)"),
        NbBundle.getMessage(LanguageDefinition.class, "Armenian").concat("(hy)"),
        NbBundle.getMessage(LanguageDefinition.class, "Azerbaijani").concat("(az)"),
        NbBundle.getMessage(LanguageDefinition.class, "Basque").concat("(eu)"),
        NbBundle.getMessage(LanguageDefinition.class, "Bengali").concat("(bn)"),
        NbBundle.getMessage(LanguageDefinition.class, "Brazil").concat("(br)"),
        NbBundle.getMessage(LanguageDefinition.class, "Breton").concat("(bre)"),
        NbBundle.getMessage(LanguageDefinition.class, "Bulgarian").concat("(bg)"),
        NbBundle.getMessage(LanguageDefinition.class, "Catalan").concat("(ca)"),
        NbBundle.getMessage(LanguageDefinition.class, "Chinese").concat("(zh-CN)"),
        NbBundle.getMessage(LanguageDefinition.class, "Chinese").concat("(zh-TW)"),
        NbBundle.getMessage(LanguageDefinition.class, "Czech").concat("(cs)"),
        NbBundle.getMessage(LanguageDefinition.class, "Croatian").concat("(hr)"),
        NbBundle.getMessage(LanguageDefinition.class, "Danish").concat("(da)"),
        NbBundle.getMessage(LanguageDefinition.class, "Dutch").concat("(nl)"),
        NbBundle.getMessage(LanguageDefinition.class, "English").concat("(en)"),
        NbBundle.getMessage(LanguageDefinition.class, "English").concat("(en-US)"),
        NbBundle.getMessage(LanguageDefinition.class, "Esperanto").concat("(eo)"),
        NbBundle.getMessage(LanguageDefinition.class, "Estonian").concat("(et)"),
        NbBundle.getMessage(LanguageDefinition.class, "Finnish").concat("(fi)"),
        NbBundle.getMessage(LanguageDefinition.class, "French").concat("(fr)"),
        NbBundle.getMessage(LanguageDefinition.class, "Galician").concat("(gl)"),
        NbBundle.getMessage(LanguageDefinition.class, "GaelicIrish").concat("(ga)"),
        NbBundle.getMessage(LanguageDefinition.class, "GaelicScottish").concat("(gd)"),
        NbBundle.getMessage(LanguageDefinition.class, "Georgian").concat("(ka)"),
        NbBundle.getMessage(LanguageDefinition.class, "German").concat("(de)"),
        NbBundle.getMessage(LanguageDefinition.class, "Greek").concat("(el)"),
        NbBundle.getMessage(LanguageDefinition.class, "Gujarati").concat("(gu)"),
        NbBundle.getMessage(LanguageDefinition.class, "Hebrew").concat("(he)"),
        NbBundle.getMessage(LanguageDefinition.class, "Hindi").concat("(hi)"),
        NbBundle.getMessage(LanguageDefinition.class, "Hungarian").concat("(hu)"),
        NbBundle.getMessage(LanguageDefinition.class, "Indonesian").concat("(id)"),
        NbBundle.getMessage(LanguageDefinition.class, "Italiano").concat("(it)"),
        NbBundle.getMessage(LanguageDefinition.class, "Japanese").concat("(ja)"),
        NbBundle.getMessage(LanguageDefinition.class, "Khmer").concat("(km)"),
        NbBundle.getMessage(LanguageDefinition.class, "Korean").concat("(ko)"),
        NbBundle.getMessage(LanguageDefinition.class, "Lao").concat("(lo)"),
        NbBundle.getMessage(LanguageDefinition.class, "Lithuanian").concat("(lt)"),
        NbBundle.getMessage(LanguageDefinition.class, "Macedonian").concat("(mk)"),
        NbBundle.getMessage(LanguageDefinition.class, "Malayalam").concat("(ml)"),
        NbBundle.getMessage(LanguageDefinition.class, "Marathi").concat("(mr)"),
        NbBundle.getMessage(LanguageDefinition.class, "Malaysian").concat("(ms)"),
        NbBundle.getMessage(LanguageDefinition.class, "Nepali").concat("(ne)"),
        NbBundle.getMessage(LanguageDefinition.class, "Norwegian").concat("(no)"),
        NbBundle.getMessage(LanguageDefinition.class, "Persian").concat("(fa)"),
        NbBundle.getMessage(LanguageDefinition.class, "Polish").concat("(pl)"),
        NbBundle.getMessage(LanguageDefinition.class, "Portuguese").concat("(pt)"),
        NbBundle.getMessage(LanguageDefinition.class, "PortugueseBrasil").concat("(pt-BR)"),
        NbBundle.getMessage(LanguageDefinition.class, "Punjabi").concat("(pa)"),
        NbBundle.getMessage(LanguageDefinition.class, "Romanian").concat("(ro)"),
        NbBundle.getMessage(LanguageDefinition.class, "Russian").concat("(ru)"),
        NbBundle.getMessage(LanguageDefinition.class, "Sangu").concat("(sg)"),
        NbBundle.getMessage(LanguageDefinition.class, "Serbian").concat("(sr)"),
        NbBundle.getMessage(LanguageDefinition.class, "Sinhala").concat("(si)"),
        NbBundle.getMessage(LanguageDefinition.class, "Slovenian").concat("(sl)"),
        NbBundle.getMessage(LanguageDefinition.class, "Slovakian").concat("(sk)"),
        NbBundle.getMessage(LanguageDefinition.class, "Spanish").concat("(es)"),
        NbBundle.getMessage(LanguageDefinition.class, "Swedish").concat("(sv)"),
        NbBundle.getMessage(LanguageDefinition.class, "Tajik").concat("(tg)"),
        NbBundle.getMessage(LanguageDefinition.class, "Tamil").concat("(ta)"),
        NbBundle.getMessage(LanguageDefinition.class, "Telegu").concat("(te)"),
        NbBundle.getMessage(LanguageDefinition.class, "Tetum").concat("(tet)"),
        NbBundle.getMessage(LanguageDefinition.class, "Thai").concat("(th)"),
        NbBundle.getMessage(LanguageDefinition.class, "Tibetan").concat("(bo)"),
        NbBundle.getMessage(LanguageDefinition.class, "Turkish").concat("(tr)"),
        NbBundle.getMessage(LanguageDefinition.class, "Ukrainian").concat("(uk-UA)"),
        NbBundle.getMessage(LanguageDefinition.class, "Vietnamese").concat("(vi)"),
        NbBundle.getMessage(LanguageDefinition.class, "Welsh").concat("(cy)"),
    }; // NOI18N

    private static String[] languageShortName = new String[] {
        "sq",
        "ar",
        "hy",
        "az",
        "eu",
        "bn",
        "br",
        "bre",
        "bg",
        "ca",
        "zh-CN",
        "zh-TW",
        "cs",
        "hr",
        "da",
        "nl",
        "en",
        "en-US",
        "eo",
        "et",
        "fi",
        "fr",
        "gl",
        "ga",
        "gd",
        "ka",
        "de",
        "el",
        "gu",
        "he",
        "hi",
        "hu",
        "id",
        "it",
        "ja",
        "km",
        "ko",
        "lo",
        "lt",
        "mk",
        "ml",
        "mr",
        "ms",
        "ne",
        "no",
        "fa",
        "pl",
        "pt",
        "pt-BR",
        "pa",
        "ro",
        "ru",
        "sg",
        "sr",
        "si",
        "sl",
        "sk",
        "es",
        "sv",
        "tg",
        "ta",
        "te",
        "tet",
        "th",
        "bo",
        "tr",
        "uk-UA",
        "vi",
        "cy",
    }; // NOI18N
    
    /**
     * Do not create
     */
    private LanguageDefinition() {
    }
    
    public static String[] getLanguages() {
        return languageLongName;
    }
    
    public static String[] getLanguagesShortName() {
        return languageShortName;
    }

    public static int getLanguageIdForName(String languageName) {
        for (int i=0; i<languageLongName.length; i++) {
            if (languageLongName[i].equals(languageName)) {
                return i;
            }
        }
        return -1;
    }
    
    public static int getLanguageIdForShortName(String shortName) {
        for (int i=0; i<languageShortName.length; i++) {
            if (languageShortName[i].equals(shortName)) {
                return i;
            }
        }
        return -1;
    }
    
    public static int getLanguageId(String name) {
        if (name != null) {
            for (int i=0; i<languageShortName.length; i++) {
                if (languageShortName[i].equals(name)) {
                    return i;
                }
                if (languageLongName[i].equals(name)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public static String getLanguageNameForId(int id) {
        if (!hasLanguage(id))
            return null;
        return languageLongName[id];
    }
    
    public static String getLanguageShortNameForId(int id) {
        if (!hasLanguage(id))
            return null;
        return languageShortName[id];
    }
    
    public static boolean hasLanguage(int langID) {
        if (langID < 0 || langID >= LANGUAGE_COUNT) {
            return false;
        }
        return true;
    }

    public static String getLanguageNameForShortName(String languageShortName) {
        int id = getLanguageId(languageShortName);
        return getLanguageNameForId(id);
    }

    public static String getLanguageShortNameForName(String languageName) {
        int id = getLanguageId(languageName);
        return getLanguageShortNameForId(id);
    }

}
