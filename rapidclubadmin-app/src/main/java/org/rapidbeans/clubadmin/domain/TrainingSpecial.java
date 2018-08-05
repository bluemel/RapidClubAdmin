/*
 * RapidBeans Application RapidClubAdmin: TrainingSpecial
 *
 * Copyright Martin Bluemel, 2009
 *
 * 18.11.2009
 */

package org.rapidbeans.clubadmin.domain;

import java.util.Date;
import java.util.GregorianCalendar;

import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.domain.math.DayOfWeek;

/**
 * Rapid Bean class: TrainingSpecial.
 * 
 * @author Martin Bluemel
 */
public class TrainingSpecial extends RapidBeanBaseTrainingSpecial {

	public Department getParentDepartment() {
		return (Department) this.getParentBean();
	}

	/**
	 * Intercept changes on property "date" and update dependent attribute
	 * "dayofweek"
	 *
	 * @param event the PropertyChangeEvent to fire
	 */
//    protected void propertyChanged(final PropertyChangeEvent event) {
//        super.propertyChanged(event);
//        if (event.getProperty().getName().equals("date")) {
//            final DayOfWeek dayOfWeek = dateToDayOfWeek((Date) event.getNewValue());
//            // turn of read only validation of the generic setter
//            try {
//                ThreadLocalValidationSettings.readonlyOff();
//                setDayofweek(dayOfWeek);
//            } finally {
//                ThreadLocalValidationSettings.remove();
//            }
//        }
//    }

	public static DayOfWeek dateToDayOfWeek(final Date newValue) {
		final GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(newValue);
		final int iDayOfWeek = cal.get(GregorianCalendar.DAY_OF_WEEK);
		switch (iDayOfWeek) {
		case 1:
			return DayOfWeek.sunday;
		case 2:
			return DayOfWeek.monday;
		case 3:
			return DayOfWeek.tuesday;
		case 4:
			return DayOfWeek.wednesday;
		case 5:
			return DayOfWeek.thursday;
		case 6:
			return DayOfWeek.friday;
		case 7:
			return DayOfWeek.saturday;
		default:
			throw new RapidBeansRuntimeException("Illegal day of week " + iDayOfWeek);
		}
	}

	/**
	 * default constructor.
	 */
	public TrainingSpecial() {
		super();
	}

	/**
	 * constructor out of a string.
	 *
	 * @param s the string
	 */
	public TrainingSpecial(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 *
	 * @param sa the string array
	 */
	public TrainingSpecial(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(TrainingSpecial.class);

	/**
	 * @return the bean's type
	 */
	public TypeRapidBean getType() {
		return type;
	}
}
