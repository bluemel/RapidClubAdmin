/*
 * RapidBeans Application RapidClubAdmin: TrainingDate
 *
 * Copyright Martin Bluemel, 2008
 *
 * 14.12.2008
 */

package org.rapidbeans.clubadmin.domain;

import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.domain.math.TimeOfDay;

/**
 * Rapid Bean class: TrainingDate.
 * 
 * @author Martin Bluemel
 */
public class TrainingDate extends RapidBeanBaseTrainingDate {

	/**
	 * the special part of the validation.<br>
	 * implicitly also converts the given object.
	 *
	 * @param newValue the value object to validate
	 *
	 * @return the converted value which is the internal representation or if a
	 *         primitive type the corresponding value object
	 */
	public void validateTimestart(final TimeOfDay time) {
		if (time != null) {
			final TimeOfDay timeend = getTimeend();
			if (timeend != null) {
				if (time.compareTo(timeend) == 1) {
					final Object[] args = { time, timeend };
					throw new ValidationException("invalid.prop.trainingdate.start.greater.end", this,
							"invalid value \"" + time.toString() + "\" for property \"timestart\""
									+ " greater than property \"timeend\" = \"" + timeend.toString() + "\"",
							args);
				}
				switch (time.compareTo(timeend)) {
				case 0:
					final Object[] args1 = { time, timeend };
					throw new ValidationException("invalid.prop.trainingdate.start.equals.end", this,
							"invalid value \"" + time.toString() + "\" for property \"timestart\""
									+ " equals property \"timeendt\" = \"" + timeend.toString() + "\"",
							args1);
				case 1:
					final Object[] args2 = { time, timeend };
					throw new ValidationException("invalid.prop.trainingdate.start.greater.end", this,
							"invalid value \"" + time.toString() + "\" for property \"timestart\""
									+ " greater than property \"timeend\" = \"" + timeend.toString() + "\"",
							args2);
				default:
					break;
				}
			}
		}
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
	public void validateTimeend(final TimeOfDay time) {
		if (time != null) {
			final TimeOfDay timestart = getTimestart();
			if (timestart != null) {
				switch (time.compareTo(timestart)) {
				case 0:
					final Object[] args1 = { time, timestart };
					throw new ValidationException("invalid.prop.trainingdate.start.equals.end", this,
							"invalid value \"" + time.toString() + "\" for property \"timeend\""
									+ " equals property \"timestart\" = \"" + timestart.toString() + "\"",
							args1);
				case -1:
					final Object[] args2 = { time, timestart };
					throw new ValidationException("invalid.prop.trainingdate.end.less.start", this,
							"invalid value \"" + time.toString() + "\" for property \"timeend\""
									+ " less than property \"timestart\" = \"" + timestart.toString() + "\"",
							args2);
				default:
					break;
				}
			}
		}
	}

	/**
	 * default constructor.
	 */
	public TrainingDate() {
		super();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s the string
	 */
	public TrainingDate(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa the string array
	 */
	public TrainingDate(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(TrainingDate.class);

	/**
	 * @return the bean's type
	 */
	public TypeRapidBean getType() {
		return type;
	}
}
