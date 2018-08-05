/*
 * EasyBiz Application RapidClubAdmin: TrainingDatePropTimestart.java
 *
 * Copyright Martin Bluemel, 2006
 *
 * 23.11.2006
 */
package org.rapidbeans.clubadmin.domain;

import org.rapidbeans.core.basic.PropertyQuantity;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypeProperty;
import org.rapidbeans.domain.math.TimeOfDay;

/**
 * extension from date property Training.timestart.
 */
public class TrainingSpecialPropTimestart extends PropertyQuantity {

	/**
	 * constructor.
	 * 
	 * @param type       the property type
	 * @param parentBean the parent bean
	 */
	public TrainingSpecialPropTimestart(final TypeProperty type, final RapidBean parentBean) {
		super(type, parentBean);
	}

	/**
	 * the special part of the validation.<br>
	 * implicitly also converts the given object.
	 *
	 * @param newValue the value object to validate
	 *
	 * @return the converted value which is the internal representation or if a
	 *         primitive type the corresponding value object
	 */
	public TimeOfDay validate(final Object newValue) {
		final TimeOfDay time = (TimeOfDay) super.validate(newValue);
		((Training) this.getBean()).validateTimestart(time);
		return time;
	}
}
