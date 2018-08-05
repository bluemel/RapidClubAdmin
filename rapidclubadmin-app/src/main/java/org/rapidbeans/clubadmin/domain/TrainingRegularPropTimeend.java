/*
 * RapidBeans Application RapidClubAdmin: TrainingRegularPropTimeend.java
 *
 * Copyright Martin Bluemel, 2009
 *
 * 15.11.2009
 */
package org.rapidbeans.clubadmin.domain;

import org.rapidbeans.core.basic.PropertyQuantity;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypeProperty;
import org.rapidbeans.domain.math.TimeOfDay;

/**
 * extends quantity property TrainingRegular.timestart.
 */
public class TrainingRegularPropTimeend extends PropertyQuantity {

	/**
	 * constructor.
	 * 
	 * @param type       the property type
	 * @param parentBean the parent bean
	 */
	public TrainingRegularPropTimeend(final TypeProperty type, final RapidBean parentBean) {
		super(type, parentBean);
	}

	public TimeOfDay getValue() {
		TimeOfDay t = null;
		final TrainingRegular tr = (TrainingRegular) this.getBean();
		final TrainingDate trd = (TrainingDate) tr.getParentBean();
		if (trd != null) {
			t = (TimeOfDay) trd.getPropValue("timeend");
		}
		return t;
	}

	public void setValue(final Object value) {
		final TrainingRegular tr = (TrainingRegular) this.getBean();
		final TrainingDate trd = (TrainingDate) tr.getParentBean();
		if (trd != null) {
			trd.setPropValue("timeend", value);
		}
	}
}
