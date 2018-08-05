/**
 * 
 */
package org.rapidbeans.clubadmin.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Martin Bluemel
 */
public class TrainerTest {
	@Test
	public void testGetIdStringWithEmptyMiddleName() {
		Trainer trainer = new Trainer(new String[] { "Bluemel", "Martin" });
		assertEquals("Bluemel_Martin_", trainer.getIdString());
	}
}
