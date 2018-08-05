/*
 * EasyBiz Application RapidClubAdmin: SettingsBasicGuiPropTreeViewShowBeanLinks.java
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
 * extension from date property TrainingsList.to.
 */
public class TrainingsListPropTo extends PropertyDate {

	/**
	 * constructor.
	 * 
	 * @param type       the property type
	 * @param parentBean the parent bean
	 */
	public TrainingsListPropTo(final TypeProperty type, final RapidBean parentBean) {
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
		Date from = null;
		try {
			from = ((TrainingsList) this.getBean()).getFrom();
		} catch (PropNotInitializedException e) {
			from = null;
		}
		if (newDate != null && from != null) {
			if (((Date) newDate).getTime() < from.getTime()) {
				final Object[] args = { newDate, from };
				throw new ValidationException("invalid.prop.trainingslist.to.smaller.from", this.getBean(),
						"invalid value \"" + newDate.toString() + "\" for property \"to\""
								+ " less than property \"from\" = \"" + from + "\"",
						args);
			}
		}
		return ((Date) newDate);
	}
}
