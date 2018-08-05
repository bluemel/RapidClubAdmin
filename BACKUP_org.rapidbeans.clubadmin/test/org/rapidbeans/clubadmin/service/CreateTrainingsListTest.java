/**
 * 
 */
package org.rapidbeans.clubadmin.service;

import java.io.File;

import junit.framework.TestCase;

import org.rapidbeans.datasource.Document;

/**
 * @author bm092114
 *
 */
public class CreateTrainingsListTest extends TestCase {

    /**
     * Test method for {@link org.rapidbeans.clubadmin.service.CreateTrainingsList#createNewTrainingsList(org.rapidbeans.datasource.Document, java.lang.String)}.
     */
    public final void testCreateNewTrainingsList() {
        Document masterdoc = new Document(new File("testdata/masterdata.xml"));
        CreateTrainingsList.createNewTrainingsList(
            masterdoc, "Budo-Club Ismaning/Judo");
        Document masterdoc2 = new Document(new File("testdata/masterdata2.xml"));
        CreateTrainingsList.createNewTrainingsList(
                masterdoc2, "Budo-Club Ismaning/Judo");
    }
}
