/*
 * RapidBeans Application RapidClubAdmin: TrainingRegularPropName.java
 *
 * Copyright Martin Bluemel, 2009
 *
 * 14.11.2009
 */
package org.rapidbeans.clubadmin.domain;

import org.rapidbeans.core.basic.PropertyString;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypeProperty;

/**
 * extends string property TrainingRegular.name.
 */
public class TrainingRegularPropName extends PropertyString {

	/**
	 * constructor.
	 * 
	 * @param type       the property type
	 * @param parentBean the parent bean
	 */
	public TrainingRegularPropName(final TypeProperty type, final RapidBean parentBean) {
		super(type, parentBean);
	}

	public String getValue() {
		String s = null;
		final TrainingRegular tr = (TrainingRegular) this.getBean();
		final TrainingDate trd = (TrainingDate) tr.getParentBean();
		if (trd != null) {
			s = (String) trd.getPropValue("name");
		}
		return s;
	}

	public void setValue(final Object value) {
		final TrainingRegular tr = (TrainingRegular) this.getBean();
		final TrainingDate trd = (TrainingDate) tr.getParentBean();
		if (trd != null) {
			trd.setPropValue("name", value);
		}
	}
}
