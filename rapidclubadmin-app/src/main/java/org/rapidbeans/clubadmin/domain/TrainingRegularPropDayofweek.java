/*
 * RapidBeans Application RapidClubAdmin: TrainingRegularPropDayofweek.java
 *
 * Copyright Martin Bluemel, 2009
 *
 * 13.11.2009
 */
package org.rapidbeans.clubadmin.domain;

import org.rapidbeans.core.basic.PropertyChoice;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.ReadonlyListCollection;
import org.rapidbeans.core.type.TypeProperty;

/**
 * extends quantity property TrainingRegular.timeend.
 */
public class TrainingRegularPropDayofweek extends PropertyChoice {

	/**
	 * constructor.
	 * 
	 * @param type       the property type
	 * @param parentBean the parent bean
	 */
	public TrainingRegularPropDayofweek(final TypeProperty type, final RapidBean parentBean) {
		super(type, parentBean);
	}

	@SuppressWarnings("unchecked")
	public ReadonlyListCollection<TrainingRegular> getValue() {
		ReadonlyListCollection<TrainingRegular> dow = null;
		final TrainingRegular tr = (TrainingRegular) this.getBean();
		final TrainingDate trd = (TrainingDate) tr.getParentBean();
		if (trd != null) {
			dow = (ReadonlyListCollection<TrainingRegular>) trd.getPropValue("dayofweek");
		}
		return dow;
	}

	public void setValue(final Object value) {
		final TrainingRegular tr = (TrainingRegular) this.getBean();
		final TrainingDate trd = (TrainingDate) tr.getParentBean();
		if (trd != null) {
			trd.setPropValue("dayofweek", value);
		}
	}
}
