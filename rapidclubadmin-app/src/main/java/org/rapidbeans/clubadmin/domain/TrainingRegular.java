/*
 * RapidBeans Application RapidClubAdmin: TrainingRegular
 *
 * Copyright Martin Bluemel, 2008
 *
 * 01.11.2009
 */

package org.rapidbeans.clubadmin.domain;

import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.domain.math.TimeOfDay;

/**
 * Rapid Bean class: TrainingRegular.
 * 
 * @author Martin Bluemel
 */
public class TrainingRegular extends RapidBeanBaseTrainingRegular {

	public TimeOfDay getTimestart() {
		super.getTimestart();
		return ((TrainingDate) this.getParentBean()).getTimestart();
	}

	public TimeOfDay getTimeend() {
		return ((TrainingDate) this.getParentBean()).getTimeend();
	}

	/**
	 * Checks if the Training has exactly the default trainers.
	 *
	 * @return if the Training has the default trainers
	 */
	public boolean hasDefaultTrainers() {
		boolean defaultTrainers = true;
		boolean trainerIsDefault;
		TrainingDate trdate = (TrainingDate) this.getParentBean();
		if (this.getHeldbytrainers() == null) {
			if (trdate.getTrainerplannings() == null) {
				return true;
			} else {
				return false;
			}
		} else if (trdate.getTrainerplannings() == null) {
			return false;
		}
		if (this.getHeldbytrainers().size() != trdate.getTrainerplannings().size()) {
			defaultTrainers = false;
		} else {
			for (TrainingHeldByTrainer trHeld : this.getHeldbytrainers()) {
				trainerIsDefault = false;
				for (TrainerPlanning trplan : trdate.getTrainerplannings()) {
					if (trplan.getRole().equals(trHeld.getRole())) {
						if (trplan.getDefaulttrainers() != null) {
							for (Trainer deftrainer : trplan.getDefaulttrainers()) {
								if (deftrainer.equals(trHeld.getTrainer())) {
									trainerIsDefault = true;
									break;
								}
							}
						}
					}
					if (trainerIsDefault) {
						break;
					}
				}
				if (!trainerIsDefault) {
					defaultTrainers = false;
					break;
				}
			}
		}
		return defaultTrainers;
	}

	protected TrainingsList getTrainingsList() {
		RapidBean parent3 = this.getParentBean().getParentBean().getParentBean().getParentBean();
		if (parent3 instanceof TrainingsList) {
			return (TrainingsList) parent3;
		} else {
			return null;
		}
	}

	public Department getParentDepartment() {
		return (Department) this.getParentBean().getParentBean();
	}

	/**
	 * default constructor.
	 */
	public TrainingRegular() {
		super();
	}

	/**
	 * constructor out of a string.
	 *
	 * @param s the string
	 */
	public TrainingRegular(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 *
	 * @param sa the string array
	 */
	public TrainingRegular(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(TrainingRegular.class);

	/**
	 * @return the bean's type
	 */
	public TypeRapidBean getType() {
		return type;
	}
}
