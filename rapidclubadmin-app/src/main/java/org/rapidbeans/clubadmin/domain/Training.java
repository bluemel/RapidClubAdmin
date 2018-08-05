/*
 * RapidBeans Application RapidClubAdmin: Training
 *
 * Copyright Martin Bluemel, 2008
 *
 * 14.12.2008
 */

package org.rapidbeans.clubadmin.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.rapidbeans.clubadmin.presentation.CustomerSettings;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.domain.math.Time;
import org.rapidbeans.domain.math.TimeOfDay;
import org.rapidbeans.domain.math.UnitTime;
import org.rapidbeans.presentation.ApplicationManager;

/**
 * Rapid Bean class: Training.
 * 
 * @author Martin Bluemel
 */
public abstract class Training extends RapidBeanBaseTraining {

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
	 * @return all TrainingHeldByTrainer association objects in a sorted manner
	 */
	public Collection<TrainingHeldByTrainer> getHeldbytrainersSortedByValue() {

		// clone the collection
		Collection<TrainingHeldByTrainer> trhbts = this.getHeldbytrainers();
		ArrayList<TrainingHeldByTrainer> trhbtsSorted = new ArrayList<TrainingHeldByTrainer>();
		if (trhbts != null) {
			for (TrainingHeldByTrainer trhbt : trhbts) {
				trhbtsSorted.add(trhbt);
			}

			// bubble sort the cloned collection
			final int trhbtsSize = trhbts.size();
			TrainingHeldByTrainer trhbt;
			for (int i = 0; i < trhbtsSize; i++) {
				for (int j = i + 1; j < trhbtsSize; j++) {
					switch (trhbtsSorted.get(i).compareValues(trhbtsSorted.get(j))) {
					case 1:
						break;
					case -1:
						trhbt = trhbtsSorted.get(j);
						trhbtsSorted.set(j, trhbtsSorted.get(i));
						trhbtsSorted.set(i, trhbt);
						break;
					case 0:
						break;
					default:
						throw new RapidBeansRuntimeException(
								"unexpected compare result " + trhbtsSorted.get(i).compareValues(trhbtsSorted.get(j)));
					}
				}
			}
		}

		return trhbtsSorted;
	}

	/**
	 * A Training that has been "closed" or "cancelled" or "checkedbefore" is reset.
	 * So it will become either "asplanned" or "modified"
	 */
	public void resetState() {
		if (hasDefaultTrainers()) {
			this.setState(TrainingState.asplanned);
		} else {
			this.setState(TrainingState.modified);
		}
	}

	/**
	 * Checks if the Training has exactly the default trainers.
	 *
	 * @return if the Training has the default trainers
	 */
	public boolean hasDefaultTrainers() {
		return false;
	}

	/**
	 * toggle the "cancelled" state.
	 */
	public void toggleCancelled() {
		switch (this.getState()) {
		case asplanned:
		case modified:
			setState(TrainingState.cancelled);
			markChecked();
			break;
		case checked:
			throw new RapidClubAdminBusinessLogicException("training.invalid.state.transition.checked.cancelled",
					"Checked Trainings can't be changed. Uncheck them before");
		case cancelled:
			this.resetState();
			break;
		case closed:
			throw new RapidClubAdminBusinessLogicException("training.invalid.state.transition.closed.cancelled",
					"Trainings with closed location can't be set to cancelled");
		default:
			throw new RapidBeansRuntimeException("Unexpected TrainigState" + ", order = " + this.getState().ordinal());
		}
	}

	/**
	 * toggle the "checked" state.
	 */
	public void toggleChecked() {
		switch (this.getState()) {
		case asplanned:
		case modified:
			validate();
			setState(TrainingState.checked);
			markChecked();
			break;
		case checked:
			checkChangingAllowed();
			this.resetState();
			break;
		case cancelled:
			throw new RapidClubAdminBusinessLogicException("training.invalid.state.transition.cancelled.checked",
					"Cancelled Trainings can't be checked or unchecked." + "Uncancel them before");
		case closed:
			throw new RapidClubAdminBusinessLogicException("training.invalid.state.transition.closed.checked",
					"Trainings with closed location can't be set to cancelled");
		default:
			throw new RapidBeansRuntimeException("Unexpected TrainigState" + ", order = " + this.getState().ordinal());
		}
	}

	private void markChecked() {
		final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();
		final ClubadminUser currentUser = app.getAuthenticatedClubadminUser();
		final TrainingsList trlist = getTrainingsList();
		if (trlist != null) {
			ClubadminUser user = (ClubadminUser) trlist.getContainer().findBean(ClubadminUser.class.getName(),
					currentUser.getIdString());
			if (user == null) {
				user = new ClubadminUser();
				user.setId(currentUser.getId());
				user.setAccountname(currentUser.getAccountname());
				user.setFirstname(currentUser.getFirstname());
				user.setLastname(currentUser.getLastname());
				user.setEmail(currentUser.getEmail());
				trlist.addUser(user);
			} else {
				if (user.getLastname() == null || user.getLastname().length() == 0) {
					user.setLastname(currentUser.getLastname());
				}
			}
		}
		setCheckedDate(new Date());
		setCheckedByUser(currentUser);
	}

	private TrainingsList getTrainingsList() {
		RapidBean parent2 = this.getParentBean().getParentBean().getParentBean();
		if (parent2 instanceof TrainingsList) {
			return (TrainingsList) parent2;
		}
		RapidBean parent3 = parent2.getParentBean();
		if (parent3 instanceof TrainingsList) {
			return (TrainingsList) parent3;
		}
		return null;
	}

	public void validate() {
		if (this.getHeldbytrainers() != null) {
			for (final TrainingHeldByTrainer trhbt : this.getHeldbytrainers()) {
				if (trhbt.getRole() == null) {
					throw new RapidClubAdminBusinessLogicException("invalid.training.state.transition.checked.nullrole",
							"Trainings held by trainers must have a trainer role set.", new Object[] { trhbt });
				}
				trhbt.validate();
			}
		}
		if (!checkFutureChangeOk()) {
			throw new RapidClubAdminBusinessLogicException("invalid.training.state.transition.checked.future",
					"Future trainings may not be checked.");
		}
	}

	public boolean checkFutureChangeOk() {
		final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();
		if (app == null) {
			return true;
		}
		if (app.getAuthenticatedClubadminUser() == null) {
			return true;
		}
		final CustomerSettings customerSettings = app.getCustomerSettings();
		if (this.getState() == TrainingState.checked
				&& (!app.getAuthenticatedClubadminUser().hasRole("SuperAdministrator"))
				&& (!app.getAuthenticatedClubadminUser().hasRole("DepartmentAdministrator"))
				&& this.getDate().compareTo(new Date()) > 0 && customerSettings != null
				&& customerSettings.getClubadminPolicies() != null
				&& customerSettings.getClubadminPolicies().getDisableCheckingOfFutureTrainings()) {
			return false;
		} else {
			return true;
		}
	}

	public static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000;

	public void checkChangingAllowed() {
		if (!checkChangingAllowedOk()) {
			throw new RapidClubAdminBusinessLogicException("invalid.training.state.transition.checked.allowed",
					"No sufficient role to change trainings after one day.");
		}
	}

	public boolean checkChangingAllowedOk() {
		final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();
		final ClubadminUser user = app.getAuthenticatedClubadminUser();

		// if we work without authentication do not allow anything
		if (user == null) {
			return false;
		}

		// if user has the minimal role to perform this action
		// then grant permission
		final CustomerSettings customerSettings = app.getCustomerSettings();
		if (customerSettings != null && customerSettings.getClubadminPolicies() != null) {
			final Role minimalRole = customerSettings.getClubadminPolicies().getChangingOfCheckedAllowedFor();
			if (user.getRole().ordinal() >= minimalRole.ordinal()) {
				return true;
			}
		}

		// for other users
		switch (this.getState()) {

		// you may change trainings that are not yet checked, cancelled or closed
		case asplanned:
		case modified:
			return true;

		// you must not change closed trainings
		case closed:
			return false;

		// you may change trainings that are cancelled or closed
		case cancelled:
		case checked:
			// if they are not marked yet by a distinct user
			if (this.getCheckedByUser() == null || this.getCheckedDate() == null) {
				return true;
			}
			// if you are the user that has checked it
			// and you have just checked it (1 day allowance)
			if (user.equals(this.getCheckedByUser())
					&& (System.currentTimeMillis() - this.getCheckedDate().getTime()) < ONE_DAY_MILLIS) {
				return true;
			}
			return false;

		default:
			throw new RapidBeansRuntimeException("Unexpected state \"" + this.getState().name() + "\"");
		}
	}

	/**
	 * correct the training state.
	 */
	protected void correctTrainingState() {
		if (this.getParentBean() == null) {
			return;
		}
		switch (this.getState()) {
		case asplanned:
		case modified:
			this.resetState();
			break;
		case checked:
		case cancelled:
		case closed:
			break;
		default:
			throw new RapidBeansRuntimeException("Unexpected TrainigState" + ", order = " + this.getState().ordinal());
		}
	}

	/**
	 * default constructor.
	 */
	public Training() {
		super();
	}

	/**
	 * constructor out of a string.
	 *
	 * @param s the string
	 */
	public Training(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 *
	 * @param sa the string array
	 */
	public Training(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(Training.class);

	/**
	 * @return the bean's type
	 */
	public TypeRapidBean getType() {
		return type;
	}

	/**
	 * Computes the time worked which is: timeend - timestart
	 *
	 * @param unitTime the desired time unit for the result
	 *
	 * @return timeend - timestart
	 */
	public Time getTimeWorked(final UnitTime unitTime) {
		return (Time) new Time(this.getTimeend().getMagnitude().subtract(this.getTimestart().getMagnitude()),
				(UnitTime) this.getTimeend().getUnit()).convert(unitTime);
	}

	public abstract Department getParentDepartment();

	/**
	 * Retrieves the parent department independently from the depth.
	 *
	 * @return the parent department
	 */
	public Department getDepartment() {
		RapidBean parentBean = this.getParentBean();
		while (parentBean != null) {
			if (parentBean instanceof Department) {
				return (Department) parentBean;
			}
			parentBean = parentBean.getParentBean();
		}
		return null;
	}
}
