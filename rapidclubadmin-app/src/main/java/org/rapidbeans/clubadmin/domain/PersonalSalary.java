/*
 * RapidBeans Application RapidClubAdmin: PersonalSalary
 *
 * Copyright Martin Bluemel, 2008
 *
 * 13.12.2008
 */

package org.rapidbeans.clubadmin.domain;

import org.rapidbeans.core.type.TypeRapidBean;

/**
 * Rapid Bean class: PersonalSalary.
 *
 * @author Martin Bluemel
 */
public class PersonalSalary extends org.rapidbeans.clubadmin.domain.RapidBeanBasePersonalSalary {

	/**
	 * default constructor.
	 */
	public PersonalSalary() {
		super();
		init();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s the string
	 */
	public PersonalSalary(final String s) {
		super(s);
		init();
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa the string array
	 */
	public PersonalSalary(final String[] sa) {
		super(sa);
		init();
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(PersonalSalary.class);

	/**
	 * @return the bean's type
	 */
	public TypeRapidBean getType() {
		return type;
	}
}
