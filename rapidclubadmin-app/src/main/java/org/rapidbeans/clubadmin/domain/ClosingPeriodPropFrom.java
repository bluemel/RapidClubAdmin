/*
 * RapidBeans Application RapidClubAdmin: ClosingPeriodPropFrom.java
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
public class ClosingPeriodPropFrom extends PropertyDate {

	/**
	 * constructor.
	 * 
	 * @param type       the property type
	 * @param parentBean the parent bean
	 */
	public ClosingPeriodPropFrom(final TypeProperty type, final RapidBean parentBean) {
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
	public Date validate(final Object newValue) {
		if (!ThreadLocalValidationSettings.getValidation()) {
			return this.convertValue(newValue);
		}
		final Date newDate = (Date) super.validate(newValue);
		if (newDate == null) {
			return null;
		}
		final ClosingPeriod cp = (ClosingPeriod) this.getBean();
		if (this.getBean() == null) {
			return this.convertValue(newValue);
		}
		final Date toTime = cp.getTo();
		if (toTime != null) {
			if (newDate.getTime() > toTime.getTime()) {
				final PropertyDate newProp = (PropertyDate) Property.createInstance(this.getType(), null);
				newProp.setValue(newValue);
				final Object[] args = { newProp, this.getBean().getProperty("to") };
				throw new ValidationException("invalid.prop.closingperiod.from.greater.to", this.getBean(),
						"invalid value \"" + newDate.toString() + "\" for property \"from\""
								+ " greater than property \"to\" = \"" + toTime.toString() + "\"",
						args);
			}
		}
		return newDate;
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
			final RapidBean parentBp = bean.getParentBean();
			if (parentBp != null && parentBp instanceof TrainingsList) {
				((TrainingsList) parentBp).updateTrainingsClosing();
			}
		}
	}
}
