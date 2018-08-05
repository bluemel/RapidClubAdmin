/*
 * EasyBiz Application RapidClubAdmin: TrainingsListPropFrom.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 14.01.2007
 */
package org.rapidbeans.clubadmin.domain;

import java.util.Date;

import org.rapidbeans.core.basic.PropertyDate;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.exception.PropNotInitializedException;
import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.core.type.TypeProperty;

/**
 * extension from date property TrainingsList.from.
 */
public class TrainingsListPropFrom extends PropertyDate {

	/**
	 * constructor.
	 * 
	 * @param type       the property type
	 * @param parentBean the parent bean
	 */
	public TrainingsListPropFrom(final TypeProperty type, final RapidBean parentBean) {
		super(type, parentBean);
	}

	/**
	 * the special part of the validation.<br>
	 * implicitely also converts the given object.
	 *
	 * @param newDateIn the value object to validate
	 *
	 * @return the converted value which is the internal representation or if a
	 *         primitive type the corresponding value object
	 */
	public Date validate(final Object newDateIn) {
		Date newDate = super.validate(newDateIn);
		Date to = null;
		try {
			to = ((TrainingsList) this.getBean()).getTo();
		} catch (PropNotInitializedException e) {
			to = null;
		}
		if (newDate != null && to != null) {
			if (((Date) newDate).getTime() > to.getTime()) {
				final Object[] args = { newDate, to };
				throw new ValidationException("invalid.prop.trainingslist.from.greater.to", this.getBean(),
						"invalid value \"" + newDate.toString() + "\" for property \"from\""
								+ " greater than property \"to\" = \"" + to + "\"",
						args);
			}
		}
		return ((Date) newDate);
	}
}
