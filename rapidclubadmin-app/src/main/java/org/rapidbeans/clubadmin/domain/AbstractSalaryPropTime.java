/*
 * EasyBiz Application RapidClubAdmin: AbstractSalaryPropTime.java
 *
 * Copyright Martin Bluemel, 2006
 *
 * 21.11.2006
 */
package org.rapidbeans.clubadmin.domain;

import java.util.Collection;

import org.rapidbeans.core.basic.PropertyQuantity;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.basic.RapidQuantity;
import org.rapidbeans.core.exception.PropNotInitializedException;
import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.core.type.TypeProperty;
import org.rapidbeans.domain.math.Time;

/**
 * extension from date property ClosingPeriod.from.
 */
public class AbstractSalaryPropTime extends PropertyQuantity {

	/**
	 * constructor.
	 * 
	 * @param type       the property type
	 * @param parentBean the parent bean
	 */
	public AbstractSalaryPropTime(final TypeProperty type, final RapidBean parentBean) {
		super(type, parentBean);
	}

	/**
	 * the special part of the validation.<br>
	 * implicitely also converts the given object.
	 *
	 * @param newValue the value object to validate
	 *
	 * @return the converted value which is the internal representation or if a
	 *         primitive type the corresponding value object
	 */
	public Time validate(final Object newValue) {
		final Time time = (Time) super.validate(newValue);
		if (time == null) {
			RapidQuantity money;
			try {
				money = ((AbstractSalary) this.getBean()).getMoney();
			} catch (PropNotInitializedException e) {
				money = null;
			}
			if (money != null) {
				throw new ValidationException("invalid.prop.salary.time.null", this,
						"invalid null value for property \"time\"" + " because property \"money\" != null");
			}
		}
		return time;
	}

	/**
	 * Overrides the set value method of PropertyQuantity in order to react to
	 * changes.
	 */
	@Override
	public void setValue(final Object value) {
		super.setValue(value);
		Time newTime = (Time) super.convertValue(value);
		final AbstractSalary parentSalary = (AbstractSalary) this.getBean();
		if (parentSalary != null) {
			try {
				final Collection<SalaryComponent> comps = parentSalary.getComponents();
				if (comps != null) {
					for (SalaryComponent comp : comps) {
						comp.setTime(newTime);
					}
				}
			} catch (PropNotInitializedException e) {
				// do nothing
			}
		}
	}
}
