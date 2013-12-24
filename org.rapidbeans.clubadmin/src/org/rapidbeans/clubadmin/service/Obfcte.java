/*
 * RapidBeans Application RapidClubAdmin: Obfcte.java
 *
 * Copyright Martin Bluemel, 2007 - 2013
 *
 * 22.12.2013
 */
package org.rapidbeans.clubadmin.service;

public final class Obfcte {

    public static String deofcte(final String s) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c1 = (char) ((int) s.charAt(i) + ((i + 3) % 4));
            sb.append(c1);
        }
        return sb.toString();
    }

    public static String ofcte(final String s) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c1 = (char) ((int) s.charAt(i) - ((i + 3) % 4));
            sb.append(c1);
        }
        return sb.toString();
    }

}
