/**
 * 
 */
package org.rapidbeans.clubadmin.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Martin
 */
public class ObfcteTest {

	@Test
	public final void testObfcte() {
		assertEquals("AidqirrehlTdqq!", Obfcte.ofcte("Dies ist ein Test!"));
		assertEquals("Dies ist ein Test!", Obfcte.deofcte("AidqirrehlTdqq!"));
	}
}
