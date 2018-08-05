/*
 * RapidBeans Application Rapid Club Admin: AbstractSalaryPropComponents.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 17.03.2008
 */
package org.rapidbeans.clubadmin.domain;

import org.rapidbeans.core.basic.Link;
import org.rapidbeans.core.basic.PropertyCollection;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypeProperty;

/**
 * extension of association property Salary.components.
 */
public class AbstractSalaryPropComponents extends PropertyCollection {

	/**
	 * constructor.
	 * 
	 * @param type       the property type
	 * @param parentBean the parent bean
	 */
	public AbstractSalaryPropComponents(final TypeProperty type, final RapidBean parentBean) {
		super(type, parentBean);
	}

	/**
	 * Interceptor for addLink.
	 *
	 * @param link the link to add
	 */
	public void addLink(final Link link) {
		super.addLink(link);
		final AbstractSalary sal = (AbstractSalary) this.getBean();
		sal.updateMoneyFromComponents();
	}

	/**
	 * Interceptor for removeLink.
	 *
	 * @param link the link to remove
	 */
	public void removeLink(final Link link) {
		super.removeLink(link);
		final AbstractSalary sal = (AbstractSalary) this.getBean();
		sal.updateMoneyFromComponents();
	}

	/**
	 * Interceptor for setValue.
	 *
	 * @param link the link to remove
	 */
	public void setValue(final Object value) {
		super.setValue(value);
		((AbstractSalary) this.getBean()).updateMoneyFromComponents();
	}
}
