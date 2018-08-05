/*
 * RapidBeans Application RapidClubAdmin: CLlosingPeriodPropTo.java
 *
 * Copyright Martin Bluemel, 2006
 *
 * 19.10.2006
 */
package org.rapidbeans.clubadmin.domain;

import java.util.Date;

import org.rapidbeans.core.basic.Property;
import org.rapidbeans.core.basic.PropertyDate;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.basic.ThreadLocalValidationSettings;
import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.core.type.TypeProperty;

/**
 * extension from date property ClosingPeriod.from.
 */
public class ClosingPeriodPropTo extends PropertyDate {

	/**
	 * constructor.
	 * 
	 * @param type       the property type
	 * @param parentBean the parent bean
	 */
	public ClosingPeriodPropTo(final TypeProperty type, final RapidBean parentBean) {
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
	public Date validate(final Object newValue) {
		if (!ThreadLocalValidationSettings.getValidation()) {
			return this.convertValue(newValue);
		}
		final Date date = (Date) super.validate(newValue);
		if (date == null) {
			return null;
		}
		final ClosingPeriod cp = (ClosingPeriod) this.getBean();
		if (this.getBean() == null) {
			return this.convertValue(newValue);
		}
		final Date fromTime = cp.getFrom();
		if (fromTime != null) {
			final PropertyDate newProp = (PropertyDate) Property.createInstance(this.getType(), null);
			newProp.setValue(newValue);
			final Object[] args = { newProp, this.getBean().getProperty("from") };
			if (date.getTime() < fromTime.getTime()) {
				throw new ValidationException("invalid.prop.closingperiod.from.less.from", this.getBean(),
						"invalid value \"" + date.toString() + "\" for property \"to\""
								+ " less than property \"from\" = \"" + fromTime.toString() + "\"",
						args);
			}
		}
		return date;
	}

	/**
	 * Overwrite the generic value setter in order to react on changes. Accepts the
	 * following datatypes:<br/>
	 * <b>Date:</b> the Date<br/>
	 * <b>String:</b> the Date as string<br/>
	 *
	 * @param newDateVal the new value to set
	 */
	public void setValue(final Object newDateVal) {
		super.setValue(newDateVal);
		final RapidBean bean = this.getBean();
		if (bean != null) {
			((ClosingPeriod) bean).updateOneday();
			final RapidBean parentBp = bean.getParentBean();
			if (parentBp != null && parentBp instanceof TrainingsList) {
				((TrainingsList) parentBp).updateTrainingsClosing();
			}
		}
	}
}
