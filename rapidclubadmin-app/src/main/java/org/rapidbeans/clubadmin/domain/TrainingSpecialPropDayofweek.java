/*
 * RapidBeans Application RapidClubAdmin: TrainingSpecialPropDayofweek.java
 *
 * Copyright Martin Bluemel, 2009
 *
 * 18.11.2009
 */
package org.rapidbeans.clubadmin.domain;

import java.util.ArrayList;
import java.util.Date;

import org.rapidbeans.core.basic.PropertyChoice;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.ReadonlyListCollection;
import org.rapidbeans.core.exception.PropNotInitializedException;
import org.rapidbeans.core.type.TypeProperty;
import org.rapidbeans.domain.math.DayOfWeek;

/**
 * extends quantity property TrainingSpecial.dayofweek.
 */
public class TrainingSpecialPropDayofweek extends PropertyChoice {

	/**
	 * constructor.
	 * 
	 * @param type       the property type
	 * @param parentBean the parent bean
	 */
	public TrainingSpecialPropDayofweek(final TypeProperty type, final RapidBean parentBean) {
		super(type, parentBean);
	}

	public ReadonlyListCollection<DayOfWeek> getValue() {
		final TrainingSpecial tr = (TrainingSpecial) this.getBean();
		final ArrayList<DayOfWeek> list = new ArrayList<DayOfWeek>();
		if (tr.getDate() != null) {
			list.add(TrainingSpecial.dateToDayOfWeek(tr.getDate()));
		}
		return new ReadonlyListCollection<DayOfWeek>(list, this.getType());
	}

	public void setValue(final Object value) {
		final TrainingSpecial tr = (TrainingSpecial) this.getBean();
		Date date = null;
		try {
			date = tr.getDate();
		} catch (PropNotInitializedException e) {
			date = null;
		}
		if (tr != null && date != null) {
			super.setValue(TrainingSpecial.dateToDayOfWeek(date));
		}
	}
}
