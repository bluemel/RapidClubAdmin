/*
 * RapidBeans Application RapidClubAdmin: ClosingPeriod
 *
 * Copyright Martin Bluemel, 2008
 *
 * 13.12.2008
 */

package org.rapidbeans.clubadmin.domain;

import java.util.Date;

import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.exception.PropNotInitializedException;
import org.rapidbeans.core.type.TypeRapidBean;

/**
 * Specific operations of RapidBeans class ClosingPeriod.
 *
 * @author Martin Bluemel
 */
public class ClosingPeriod extends RapidBeanBaseClosingPeriod {

	/**
	 * handle a closing period insert event.
	 *
	 * @param parent the parent bean
	 */
	public void setParentBean(final RapidBean parent) {
		RapidBean parentOld = this.getParentBean();
		super.setParentBean(parent);
		if (parent == parentOld) {
			return;
		}
		if (parent != null && parent instanceof TrainingsList) {
			TrainingsList parentBp = (TrainingsList) parent;
			parentBp.updateTrainingsClosing();
		}
	}

	/**
	 * handle a closing period delete event.
	 */
	public void delete() {
		TrainingsList parentBp = null;
		final RapidBean parentBean = this.getParentBean();
		if (parentBean != null && parentBean instanceof TrainingsList) {
			parentBp = (TrainingsList) parentBean;
		}
		if (parentBp != null) {
			parentBp.updateTrainingsClosing();
		}
		super.delete();
	}

	/**
	 * Update oneday according to from or to.
	 */
	protected void updateOneday() {
		try {
			final Date from = getFrom();
			final Date to = getTo();
			if (from != null && to != null && from.equals(to)) {
				setOneday(true);
			}
		} catch (PropNotInitializedException e) {
		}
	}

	/**
	 * default constructor.
	 */
	public ClosingPeriod() {
		super();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s the string
	 */
	public ClosingPeriod(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa the string array
	 */
	public ClosingPeriod(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(ClosingPeriod.class);

	/**
	 * @return the bean's type
	 */
	public TypeRapidBean getType() {
		return type;
	}
}
