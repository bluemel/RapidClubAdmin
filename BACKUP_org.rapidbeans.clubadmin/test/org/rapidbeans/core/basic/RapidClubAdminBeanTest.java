/*
 * Rapid Beans Framework: RapidBeanTest.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 22.11.2005
 */

package org.rapidbeans.core.basic;

import java.io.File;

import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.clubadmin.domain.TrainerRole;
import org.rapidbeans.core.basic.IdGeneratorNumeric;
import org.rapidbeans.core.basic.PropertyAssociationend;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.datasource.Document;

import junit.framework.TestCase;

/**
 * Unit Test for bean.
 *
 * @author Martin Bluemel
 */
public final class RapidClubAdminBeanTest extends TestCase {

    /**
     * initialize the default link serialization separator.
     */
    public void setUp() {
        TypePropertyCollection.setDefaultCharSeparator(',');        
    }

    /**
     * test the parent property for the trainer roles.
     */
    public void testGetParentCompColProperty() {
        TypeRapidBean.forName("org.rapidbeans.clubadmin.domain.TrainerPlanning").setIdGenerator(
                new IdGeneratorNumeric());
        TypeRapidBean.forName("org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer").setIdGenerator(
                new IdGeneratorNumeric());
        Document testdoc = new Document(
                TypeRapidBean.forName(TrainingsList.class.getName()),
                new File("testdata/trainingslist_20060101_20060331.xml"));
        TrainerRole role = (TrainerRole) testdoc.findBean(
                "org.rapidbeans.clubadmin.domain.TrainerRole", "Trainer");
        assertNotNull(role);
        PropertyAssociationend parentColProp = role.getParentProperty();
        assertEquals("trainerroles", parentColProp.getType().getPropName());
    }
}
