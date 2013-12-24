/**
 * 
 */
package org.rapidbeans.clubadmin.service;

import junit.framework.TestCase;

/**
 * @author Martin
 */
public class ObfcteTest extends TestCase {

    public final void testObfcte() {
        assertEquals("AidqirrehlTdqq!", Obfcte.ofcte("Dies ist ein Test!"));
        assertEquals("Dies ist ein Test!", Obfcte.deofcte("AidqirrehlTdqq!"));
    }
}
