/*
 * RapidBeans Application RapidClubAdmin: TrainerRole
 *
 * Copyright Martin Bluemel, 2008
 *
 * 14.12.2008
 */

package org.rapidbeans.clubadmin.domain;

import java.util.Collection;

import org.rapidbeans.core.type.TypeRapidBean;

/**
 * Rapid Bean class: TrainerRole.
 * 
 * @author Martin Bluemel
 */
public class TrainerRole extends RapidBeanBaseTrainerRole {

	/**
	 * Map a trainer attribute to a salary for this role. A map would be better
	 * here.
	 * 
	 * @param attr the trainer attribute
	 *
	 * @return the salary for a given attribute
	 */
	public Salary getSalary(final Collection<TrainerAttribute> attrs1) {
		for (Salary salary : this.getSalarys()) {
			Collection<TrainerAttribute> attrs2 = salary.getTrainerattribute();
			if ((attrs2 == null && attrs1 == null) || (attrs2 != null && attrs2.equals(attrs1))) {
				return salary;
			}
		}
		return null;
	}

	/**
	 * default constructor.
	 */
	public TrainerRole() {
		super();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s the string
	 */
	public TrainerRole(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa the string array
	 */
	public TrainerRole(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(TrainerRole.class);

	/**
	 * @return the bean's type
	 */
	public TypeRapidBean getType() {
		return type;
	}
}
