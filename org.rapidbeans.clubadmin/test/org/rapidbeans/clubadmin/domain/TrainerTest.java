/**
 * 
 */
package org.rapidbeans.clubadmin.domain;

import junit.framework.TestCase;

/**
 * @author Martin Bluemel
 */
public class TrainerTest extends TestCase {
    public void testGetIdStringWithEmptyMiddleName() {
        Trainer trainer = new Trainer(new String[]{"Bluemel", "Martin"});
        assertEquals("Bluemel_Martin_", trainer.getIdString());
    }
}
