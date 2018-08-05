/*
 * Rapid Beans Framework: RapidBeanTest.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 22.11.2005
 */

package org.rapidbeans.core.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.rapidbeans.clubadmin.domain.TrainerRole;
import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.datasource.Document;

/**
 * Unit Test for bean.
 *
 * @author Martin Bluemel
 */
public final class RapidClubAdminBeanTest {

	/**
	 * initialize the default link serialization separator.
	 */
	@Before
	public void setUp() {
		TypePropertyCollection.setDefaultCharSeparator(',');
	}

	/**
	 * test the parent property for the trainer roles.
	 */
	@Test
	public void testGetParentCompColProperty() {
		TypeRapidBean.forName("org.rapidbeans.clubadmin.domain.TrainerPlanning")
				.setIdGenerator(new IdGeneratorNumeric());
		TypeRapidBean.forName("org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer")
				.setIdGenerator(new IdGeneratorNumeric());
		Document testdoc = new Document(TypeRapidBean.forName(TrainingsList.class.getName()),
				new File("src/test/resources/trainingslist_20060101_20060331.xml"));
		TrainerRole role = (TrainerRole) testdoc.findBean("org.rapidbeans.clubadmin.domain.TrainerRole", "Trainer");
		assertNotNull(role);
		PropertyAssociationend parentColProp = role.getParentProperty();
		assertEquals("trainerroles", parentColProp.getType().getPropName());
	}
}
