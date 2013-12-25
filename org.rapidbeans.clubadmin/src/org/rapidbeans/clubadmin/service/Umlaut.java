package org.rapidbeans.clubadmin.service;

public final class Umlaut {

    public static final String U_AUML = "\u00C4";

    public static final String U_OUML = "\u00D6";

    public static final String U_UUML = "\u00DC";

    public static final String L_AUML = "\u00E4";

    public static final String L_OUML = "\u00F6";

    public static final String L_UUML = "\u00FC";

    public static final String SUML = "\u00DF";

    public static final String RTF_U_AUML = "\\'c4";

    public static final String RTF_U_OUML = "\\'d6";

    public static final String RTF_U_UUML = "\\'dc";

    public static final String RTF_L_AUML = "\\'e4";

    public static final String RTF_L_OUML = "\\'f6";

    public static final String RTF_L_UUML = "\\'fc";

    public static final String RTF_SUML = "\\'df";

    public static final char ISO_U_AUML = (char) 0xC4;

    public static final char ISO_U_OUML = (char) 0xD6;

    public static final char ISO_U_UUML = (char) 0xDC;

    public static final char ISO_L_AUML = (char) 0xE4;

    public static final char ISO_L_OUML = (char) 0xF6;

    public static final char ISO_L_UUML = (char) 0xFC;

    public static final char ISO_SUML = (char) 0xDF;

    private Umlaut() {
    }

    public static String encodeRtf(final String s) {
        return s.replace(U_AUML, RTF_U_AUML).replace(U_OUML, RTF_U_OUML).replace(U_UUML, RTF_U_UUML)
                .replace(L_AUML, RTF_L_AUML).replace(L_OUML, RTF_L_OUML).replace(L_UUML, RTF_L_UUML)
                .replace(SUML, RTF_SUML);
    }
}
