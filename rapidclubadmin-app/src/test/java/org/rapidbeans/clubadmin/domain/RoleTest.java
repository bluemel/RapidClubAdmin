/*
 * Rapid Beans Framework: RoleTest.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 23.09.2008
 */
package org.rapidbeans.clubadmin.domain;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Martin
 */
public class RoleTest {

	@Test
	public void testOrder() {
		assertTrue(Role.SuperAdministrator.ordinal() > Role.DepartmentAdministrator.ordinal());
		assertTrue(Role.SuperAdministrator.ordinal() > Role.Trainer.ordinal());
		assertTrue(Role.DepartmentAdministrator.ordinal() > Role.Trainer.ordinal());
	}
}
