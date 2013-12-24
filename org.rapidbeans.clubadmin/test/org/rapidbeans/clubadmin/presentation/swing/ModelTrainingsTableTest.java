/*
 * Rapid Beans Framework: ModelTrainingsTableTest.java
 *
 * Copyright Martin Bluemel, 2009
 *
 * 02.11.2009
 */

package org.rapidbeans.clubadmin.presentation.swing;

import java.io.File;

import junit.framework.TestCase;

import org.rapidbeans.clubadmin.domain.TrainingRegular;
import org.rapidbeans.clubadmin.domain.TrainingSpecial;
import org.rapidbeans.core.basic.BeanSorter;
import org.rapidbeans.core.type.TypeProperty;
import org.rapidbeans.datasource.Document;

/**
 * training table model tests.
 *
 * @author Martin Bluemel
 */
public class ModelTrainingsTableTest extends TestCase {

    /**
     * set up.
     */
    public void setUp() {
    }

    /**
     * tear down.
     */
    public void tearDown() {
        BeanSorter.set(null);
    }

    public void testSortTrainings() {
        Document doc = new Document(
                new File("testdata/trainingslist_withSpecialTrainings.xml"));
        TrainingRegular tr01 = (TrainingRegular) doc.findBean(
                "org.rapidbeans.clubadmin.domain.TrainingRegular",
                "Budo-Club Ismaning/Aikido/monday_19:30_Turnhalle Grundschule Süd/20070903");
        assertNotNull(tr01);
        TrainingRegular tr02 = (TrainingRegular) doc.findBean(
                "org.rapidbeans.clubadmin.domain.TrainingRegular",
                "Budo-Club Ismaning/Aikido/monday_20:30_Turnhalle Grundschule Süd/20070903");
        assertNotNull(tr02);
        TrainingSpecial tr03 = (TrainingSpecial) doc.findBean(
                "org.rapidbeans.clubadmin.domain.TrainingSpecial",
                "Budo-Club Ismaning/Aikido/8:15_Turnhalle Grundschule Süd_20070904");
        assertNotNull(tr03);
        TrainingRegular tr04 = (TrainingRegular) doc.findBean(
                "org.rapidbeans.clubadmin.domain.TrainingRegular",
                "Budo-Club Ismaning/Aikido/tuesday_18:00_Turnhalle Grundschule Süd/20070904");
        assertNotNull(tr04);
        TrainingSpecial tr05 = (TrainingSpecial) doc.findBean(
                "org.rapidbeans.clubadmin.domain.TrainingSpecial",
                "Budo-Club Ismaning/Aikido/22:00_Turnhalle Grundschule Süd_20070904");
        assertNotNull(tr05);

        final TypeProperty[] proptypes = {
                tr01.getProperty("date").getType(),
                tr01.getProperty("timestart").getType()
                };
        BeanSorter.set(proptypes);

        assertEquals(-1, tr01.compareTo(tr02));
        assertEquals(1, tr02.compareTo(tr01));
        assertEquals(0, tr01.compareTo(tr01));
        assertEquals(-1, tr01.compareTo(tr03));
        assertEquals(1, tr03.compareTo(tr01));
        assertEquals(-1, tr01.compareTo(tr04));
        assertEquals(1, tr04.compareTo(tr01));
        assertEquals(-1, tr01.compareTo(tr05));
        assertEquals(1, tr05.compareTo(tr01));

        assertEquals(-1, tr02.compareTo(tr03));
        assertEquals(1, tr03.compareTo(tr02));
        assertEquals(-1, tr02.compareTo(tr04));
        assertEquals(1, tr04.compareTo(tr02));
        assertEquals(-1, tr02.compareTo(tr05));
        assertEquals(1, tr05.compareTo(tr02));

        assertEquals(-1, tr03.compareTo(tr04));
        assertEquals(1, tr04.compareTo(tr03));
        assertEquals(-1, tr03.compareTo(tr05));
        assertEquals(1, tr05.compareTo(tr03));

        assertEquals(-1, tr04.compareTo(tr05));
        assertEquals(1, tr05.compareTo(tr04));
    }

    /**
     * Test the model sorting with different training subclasses.
     */
    public void testModelSorting() {
        Document doc = new Document(
                new File("testdata/trainingslist_withSpecialTrainings.xml"));
        ModelTrainingsTable model = new ModelTrainingsTable(doc);
        assertEquals("Budo-Club Ismaning/Aikido/monday_19:30_Turnhalle Grundschule Süd/20070903",
                model.getTrainingAt(0).getIdString());
        assertEquals("Budo-Club Ismaning/Aikido/monday_20:30_Turnhalle Grundschule Süd/20070903",
                model.getTrainingAt(1).getIdString());
        assertEquals("Budo-Club Ismaning/Aikido/8:15_Turnhalle Grundschule Süd_20070904",
                model.getTrainingAt(2).getIdString());
        assertEquals("Budo-Club Ismaning/Aikido/tuesday_18:00_Turnhalle Grundschule Süd/20070904",
                model.getTrainingAt(3).getIdString());
        assertEquals("Budo-Club Ismaning/Aikido/22:00_Turnhalle Grundschule Süd_20070904",
                model.getTrainingAt(4).getIdString());
    }
}
