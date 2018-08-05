/**
 * 
 */
package org.rapidbeans.clubadmin.service;

import java.io.File;

import org.junit.Test;
import org.rapidbeans.datasource.Document;

public class CreateTrainingsListTest {

	/**
	 * Test method for
	 * {@link org.rapidbeans.clubadmin.service.CreateTrainingsList#createNewTrainingsList(org.rapidbeans.datasource.Document, java.lang.String)}.
	 */
	@Test
	public final void testCreateNewTrainingsList() {
		Document masterdoc = new Document(new File("src/test/resources/masterdata.xml"));
		CreateTrainingsList.createNewTrainingsList(masterdoc, "Budo-Club Ismaning/Judo");
		Document masterdoc2 = new Document(new File("src/test/resources/masterdata2.xml"));
		CreateTrainingsList.createNewTrainingsList(masterdoc2, "Budo-Club Ismaning/Judo");
	}
}
