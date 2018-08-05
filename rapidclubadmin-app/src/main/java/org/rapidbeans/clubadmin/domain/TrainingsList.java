/*
 * RapidBeans Application RapidClubAdmin: TrainingsList
 *
 * Copyright Martin Bluemel, 2008
 *
 * 14.12.2008
 */

package org.rapidbeans.clubadmin.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.basic.Id;
import org.rapidbeans.core.basic.PropertyAssociationend;
import org.rapidbeans.core.basic.PropertyDate;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.basic.ThreadLocalValidationSettings;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.core.exception.ValidationInstanceAssocTwiceException;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.domain.math.DateGenerator;
import org.rapidbeans.presentation.ApplicationManager;

/**
 * Rapid Bean class: TrainingsList.
 * 
 * @author Martin Bluemel
 */
public class TrainingsList extends RapidBeanBaseTrainingsList {

	public AllTrainingState getTrainingsState() {
		AllTrainingState allTrainingsState = AllTrainingState.all_asplanned;
		boolean one_modified = false;
		boolean one_checked = false;
		boolean all_checked = true;
		for (TrainingRegular tr : this.getTrainings()) {
			switch (tr.getState()) {
			case asplanned:
				all_checked = false;
				break;
			case modified:
				one_modified = true;
				all_checked = false;
				break;
			case checked:
				one_checked = true;
				break;
			case cancelled:
			case closed:
				break;
			default:
				throw new RapidBeansRuntimeException("Unexpected state \"" + allTrainingsState.name() + "\"");
			}
			if (one_modified && (!all_checked)) {
				break;
			}
		}
		if (all_checked) {
			allTrainingsState = AllTrainingState.all_checked;
		} else if (one_modified || one_checked) {
			allTrainingsState = AllTrainingState.one_checkormodified;
		}
		return allTrainingsState;
	}

	public List<TrainingRegular> getTrainings() {
		final List<TrainingRegular> trainings = new ArrayList<TrainingRegular>();
		for (RapidBean bean : this.getContainer().findBeansByType(TrainingRegular.class.getName())) {
			trainings.add((TrainingRegular) bean);
		}
		return trainings;
	}

	/**
	 * update mode: from property has changed.
	 */
	public static final int UPDATE_MODE_FROM = 1;

	/**
	 * update mode: to property has changed.
	 */
	public static final int UPDATE_MODE_TO = 2;

	/**
	 * update mode: from property has changed.
	 */
	public static final int UPDATE_MODE_PROPS = 3;

	/**
	 * the maximal period where trainings update is done.
	 */
	private static final long MAX_PERIOD_FOR_TRAININGS_UPDATE = 5L * 366 * 24 * 60 * 60 * 1000;

	/**
	 * creates new trainings and removes unneccessary trainings after from or to
	 * date of this trainings period has changed.
	 *
	 * @param mode    indicates the field that has been changed
	 * @param newDate the new date value f the from or to field
	 */
	public void updateTrainings(final int mode, final Date newDate) {

		if (this.getContainer() == null) {
			return;
		}

		// Collection<RapidBean> trsToAdd = null;
		Collection<RapidBean> trsToRemove = null;
		Date dFrom = null;
		Date dTo = null;

		switch (mode) {
		case UPDATE_MODE_FROM:
			dFrom = newDate;
			dTo = this.getTo();
			break;
		case UPDATE_MODE_TO:
			dFrom = this.getFrom();
			dTo = newDate;
			break;
		case UPDATE_MODE_PROPS:
			dFrom = this.getFrom();
			dTo = this.getTo();
			break;
		default:
			throw new IllegalArgumentException("Unexpected mode " + mode);
		}
		if (((dTo.getTime() - dFrom.getTime()) > MAX_PERIOD_FOR_TRAININGS_UPDATE)
				&& ApplicationManager.getApplication() != null
				&& (!ApplicationManager.getApplication().getTestMode())) {
			ApplicationManager.getApplication().messageInfo(ApplicationManager.getApplication().getCurrentLocale()
					.getStringMessage("info.bizrule.trainingslist.notrainigsupdate"));
			return;
		}

		this.createNewTrainings(dFrom, dTo);
		trsToRemove = this.findObsoleteTrainings(dFrom, dTo);

		for (RapidBean bean : trsToRemove) {
			bean.delete();
		}
	}

	/**
	 * update a training's closing state.
	 */
	public void updateTrainingsClosing() {
		TrainingRegular tr;
		if (this.getContainer() == null) {
			return;
		}
		Collection<ClosingPeriod> cps = this.getClosingperiods();
		if (cps == null) {
			cps = new ArrayList<ClosingPeriod>();
		}
		for (RapidBean b : this.getContainer().findBeansByType("org.rapidbeans.clubadmin.domain.Training")) {
			tr = (TrainingRegular) b;
			if (trainingInOneOfCps(tr, (Collection<ClosingPeriod>) cps)) {
				tr.setState(TrainingState.closed);
			} else {
				if (tr.getState() == TrainingState.closed) {
					tr.resetState();
				}
			}
		}
	}

	/**
	 * Test if a training is within the boundaries.
	 *
	 * @param tr  trainig
	 * @param cps the periots
	 * 
	 * @return is the Traing beginns now.
	 */
	private boolean trainingInOneOfCps(final TrainingRegular tr, final Collection<ClosingPeriod> cps) {
		final Date trdate = tr.getDate();
		if (trdate == null) {
			return false;
		}
		final TrainingDate trDate = (TrainingDate) tr.getParentBean();
		Date cpFrom, cpTo;
		boolean trdOnLocationOfCp;
		Collection<Location> locs;
		for (ClosingPeriod cp : cps) {
			trdOnLocationOfCp = false;
			locs = cp.getLocations();
			if (locs != null) {
				for (Location loc : locs) {
					if (trDate.getLocation() == loc) {
						trdOnLocationOfCp = true;
						break;
					}
				}
			}
			if (!trdOnLocationOfCp) {
				continue;
			}
			cpFrom = cp.getFrom();
			if (cpFrom == null) {
				continue;
			}
			cpTo = cp.getTo();
			if (cpTo == null) {
				continue;
			}
			if (cpFrom.getTime() <= trdate.getTime() && cpTo.getTime() >= trdate.getTime()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * removes unneccessary closing periods and adds additional ones from masterdata
	 * after from or to date of this trainings list has changed.
	 */
	@SuppressWarnings("unchecked")
	public void updateClosingPeriodsFromMasterdata() {

		if (this.getContainer() == null) {
			return;
		}

		// copy new closing periods from masterdata
		RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();
		if (client != null) {
			final MasterData masterdata = client.getMasterData();
			if (masterdata != null && masterdata.getClosingperiods() != null) {
				for (ClosingPeriod cpMaster : masterdata.getClosingperiods()) {
					if (PropertyDate.dateIntervalsOverlap(cpMaster.getFrom(), cpMaster.getTo(), this.getFrom(),
							this.getTo()) && (!this.getContainer().contains(cpMaster))) {
						// cp does overlap the trainings period
						// and is not contained
						final ClosingPeriod newCp = new ClosingPeriod();
						newCp.setId(Id.createInstance(newCp, cpMaster.getIdString()));
						newCp.setFrom(cpMaster.getFrom());
						newCp.setName(cpMaster.getName());
						try {
							ThreadLocalValidationSettings.validationOff();
							newCp.getProperty("to").setValue(cpMaster.getTo());
							this.addClosingperiod(newCp);
							PropertyAssociationend cps = (PropertyAssociationend) cpMaster.getProperty("locations");
							for (Location loc : (Collection<Location>) cps.getValue()) {
								try {
									newCp.addLocation(loc);
								} catch (ValidationInstanceAssocTwiceException e) {
									loc = null;
								}
							}
						} finally {
							ThreadLocalValidationSettings.remove();
						}
					}
				}
			}
		}

		// collect unnecessary closing periods to delete
		Collection<ClosingPeriod> cpsToDelete = new ArrayList<ClosingPeriod>();
		if (this.getClosingperiods() != null) {
			for (ClosingPeriod cp : this.getClosingperiods()) {
				if (!PropertyDate.dateIntervalsOverlap(cp.getFrom(), cp.getTo(), this.getFrom(), this.getTo())) {
					// cp does not overlap the trainings period
					cpsToDelete.add(cp);
				}
			}
		}
		for (ClosingPeriod cp : cpsToDelete) {
			cp.delete();
		}
	}

	/**
	 * generate new trainings for the given period.
	 *
	 * @param dFrom start date for generation
	 * @param dTo   end date
	 *
	 * @return collection with trainings to remove
	 */
	protected List<RapidBean> createNewTrainings(final Date dFrom, final Date dTo) {

		final List<RapidBean> newTrs = new ArrayList<RapidBean>();
		final HashMap<Integer, Collection<TrainingDate>> trdates = getTrainingDatesAsGCIndexedHashMap();
		GregorianCalendar gc = new GregorianCalendar();

		for (Date d : new DateGenerator().generateDays(dFrom, dTo)) {
			gc.setTime(d);
			switch (gc.get(GregorianCalendar.DAY_OF_WEEK)) {
			case GregorianCalendar.MONDAY:
				createTrainingsForDay(newTrs, d, trdates.get(GregorianCalendar.MONDAY));
				break;
			case GregorianCalendar.TUESDAY:
				createTrainingsForDay(newTrs, d, trdates.get(GregorianCalendar.TUESDAY));
				break;
			case GregorianCalendar.WEDNESDAY:
				createTrainingsForDay(newTrs, d, trdates.get(GregorianCalendar.WEDNESDAY));
				break;
			case GregorianCalendar.THURSDAY:
				createTrainingsForDay(newTrs, d, trdates.get(GregorianCalendar.THURSDAY));
				break;
			case GregorianCalendar.FRIDAY:
				createTrainingsForDay(newTrs, d, trdates.get(GregorianCalendar.FRIDAY));
				break;
			case GregorianCalendar.SATURDAY:
				createTrainingsForDay(newTrs, d, trdates.get(GregorianCalendar.SATURDAY));
				break;
			case GregorianCalendar.SUNDAY:
				createTrainingsForDay(newTrs, d, trdates.get(GregorianCalendar.SUNDAY));
				break;
			default:
				break;
			}
		}
		return newTrs;
	}

	/**
	 * find all obsolete trainings not in the given period.
	 *
	 * @param dFrom start date for the period
	 * @param dTo   end date
	 *
	 * @return collection with trainings to remove
	 */
	protected Collection<RapidBean> findObsoleteTrainings(final Date dFrom, final Date dTo) {
		final Collection<RapidBean> obsTrainings = new ArrayList<RapidBean>();
		if (this.getContainer().findBeansByType("org.rapidbeans.clubadmin.domain.Training").size() == 0) {
			return obsTrainings;
		}
		final long tFrom = dFrom.getTime();
		final long tTo = dTo.getTime();
		long time;
		TrainingRegular tr;
		for (RapidBean b : this.getContainer().findBeansByType("org.rapidbeans.clubadmin.domain.Training")) {
			tr = (TrainingRegular) b;
			time = tr.getDate().getTime();
			if (time < tFrom || time > tTo) {
				obsTrainings.add(tr);
			}
		}
		return obsTrainings;
	}

	/**
	 * @return a HashMap with all TrainingDates indexed by their day of week as
	 *         GregorianCalendar defined int constant.
	 */
	private HashMap<Integer, Collection<TrainingDate>> getTrainingDatesAsGCIndexedHashMap() {

		final HashMap<Integer, Collection<TrainingDate>> trdates = new HashMap<Integer, Collection<TrainingDate>>();

		trdates.put(GregorianCalendar.MONDAY, new ArrayList<TrainingDate>());
		trdates.put(GregorianCalendar.TUESDAY, new ArrayList<TrainingDate>());
		trdates.put(GregorianCalendar.WEDNESDAY, new ArrayList<TrainingDate>());
		trdates.put(GregorianCalendar.THURSDAY, new ArrayList<TrainingDate>());
		trdates.put(GregorianCalendar.FRIDAY, new ArrayList<TrainingDate>());
		trdates.put(GregorianCalendar.SATURDAY, new ArrayList<TrainingDate>());
		trdates.put(GregorianCalendar.SUNDAY, new ArrayList<TrainingDate>());

		for (RapidBean b : this.getContainer().findBeansByType("org.rapidbeans.clubadmin.domain.TrainingDate")) {
			switch (((TrainingDate) b).getDayofweek()) {
			case monday:
				trdates.get(GregorianCalendar.MONDAY).add((TrainingDate) b);
				break;
			case tuesday:
				trdates.get(GregorianCalendar.TUESDAY).add((TrainingDate) b);
				break;
			case wednesday:
				trdates.get(GregorianCalendar.WEDNESDAY).add((TrainingDate) b);
				break;
			case thursday:
				trdates.get(GregorianCalendar.THURSDAY).add((TrainingDate) b);
				break;
			case friday:
				trdates.get(GregorianCalendar.FRIDAY).add((TrainingDate) b);
				break;
			case saturday:
				trdates.get(GregorianCalendar.SATURDAY).add((TrainingDate) b);
				break;
			case sunday:
				trdates.get(GregorianCalendar.SUNDAY).add((TrainingDate) b);
				break;
			default:
				break;
			}

		}
		return trdates;
	}

	/**
	 * create all trainings for one day and add them to the given trainings
	 * collection.
	 *
	 * @param trainingsCreated the trainings to create
	 * @param d                the date
	 * @param trdates          all training dates
	 */
	private void createTrainingsForDay(final Collection<RapidBean> trainingsCreated, final Date d,
			final Collection<TrainingDate> trdates) {
		TrainingRegular tr;
		TrainingHeldByTrainer trhbt;
		for (TrainingDate trdate : trdates) {
			tr = new TrainingRegular();
			tr.setDate(d);
			if ((trdate.getTrainings() == null) || (!trdate.getTrainings().contains(tr))) {
				trdate.addTraining(tr);
				if (trdate.getTrainerplannings() != null) {
					for (TrainerPlanning pl : trdate.getTrainerplannings()) {
						trhbt = new TrainingHeldByTrainer();
						if (pl.getDefaulttrainers() == null || pl.getDefaulttrainers().size() == 0) {
							trhbt.setTrainer(null);
						} else {
							// take the first default trainer if any defined
							trhbt.setPropValue("trainer", pl.getDefaulttrainers().iterator().next().getIdString());
						}
						if (pl.getRole() == null) {
							trhbt.setRole(null);
						} else {
							trhbt.setPropValue("role", pl.getRole().getIdString());
						}
						tr.addHeldbytrainer(trhbt);
					}
				}
			}
			trainingsCreated.add(tr);
		}
	}

	/**
	 * default constructor.
	 */
	public TrainingsList() {
		super();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s the string
	 */
	public TrainingsList(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa the string array
	 */
	public TrainingsList(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(TrainingsList.class);

	/**
	 * @return the bean's type
	 */
	public TypeRapidBean getType() {
		return type;
	}
}
