/*
 * Rapid Beans Framework: CreateTrainingsList.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 09.12.2005
 */

package org.rapidbeans.clubadmin.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.rapidbeans.clubadmin.domain.Club;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.MasterData;
import org.rapidbeans.clubadmin.domain.PersonalSalary;
import org.rapidbeans.clubadmin.domain.SalaryComponent;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.TrainerPlanning;
import org.rapidbeans.clubadmin.domain.TrainerRole;
import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.basic.Link;
import org.rapidbeans.core.basic.LinkFrozen;
import org.rapidbeans.core.basic.Property;
import org.rapidbeans.core.basic.PropertyAssociationend;
import org.rapidbeans.core.basic.PropertyCollection;
import org.rapidbeans.core.basic.PropertyDate;
import org.rapidbeans.core.basic.ThreadLocalValidationSettings;
import org.rapidbeans.core.common.PrecisionDate;
import org.rapidbeans.core.common.ReadonlyListCollection;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.presentation.ThreadLocalEventLock;
import org.rapidbeans.service.Action;

/**
 * The action to create a billing period.
 *
 * @author Martin Bluemel
 */
public class CreateTrainingsList extends Action {

	/**
	 * create a new billing period.
	 */
	public void execute() {
		try {
			ThreadLocalEventLock.set(null);
			final String depId = this.getArgumentValue("department");
			final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();
			final Document masterdoc = client.getMasterDoc();
			final Document doc = createNewTrainingsList(masterdoc, depId);
			client.setCurrentWorkingDepartment(doc);
			final DocumentView docview = ApplicationManager.getApplication().openDocumentView(doc, "trainingslist",
					"expert");
			docview.editBean(doc.getRoot());
		} finally {
			ThreadLocalEventLock.release();
		}
	}

	/**
	 * create a TrainingsList document out of a given master data document.
	 *
	 * @param srcDoc       the source document
	 * @param targetRootBp the target root
	 * @param department   specifies the department to restrict
	 */
	@SuppressWarnings("unchecked")
	public static Document createNewTrainingsList(final Document srcMasterDoc, final String depId) {
		Department department = null;
		if (depId != null && depId.length() > 0) {
			department = (Department) srcMasterDoc.findBean("org.rapidbeans.clubadmin.domain.Department", depId);
		}
		final String date = PropertyDate.format(new Date(), PrecisionDate.second);
		final TrainingsList root = new TrainingsList();
		final Document targetDoc = new Document("trainingslist_new_" + date, root);
		final MasterData srcRootMd = (MasterData) srcMasterDoc.getRoot();
		final TrainingsList targetRootBp = (TrainingsList) targetDoc.getRoot();
		targetRootBp.setTrainerhour(srcRootMd.getTrainerhour());
		if (department == null) {
			for (PropertyAssociationend srcColProp : srcRootMd.getColPropertiesComposition()) {
				// take all closing periods because the date
				// is not yet defined at that time.
				final String propName = srcColProp.getType().getPropName();
				final PropertyAssociationend tgtProp = (PropertyAssociationend) targetRootBp.getProperty(propName);
				if (tgtProp != null && !(propName.equals("users") || propName.equals("roles"))) {
					srcColProp.cloneValue(tgtProp, targetRootBp.getContainer());
				}
			}
			for (final Trainer trainer : targetRootBp.getTrainers()) {
				trainer.setUser(null);
			}
		} else {
			for (PropertyAssociationend srcColProp : srcRootMd.getColPropertiesComposition()) {
				// take all closing periods because the date
				// is not yet defined at that time.
				final String propName = srcColProp.getType().getPropName();
				final PropertyAssociationend tgtProp = (PropertyAssociationend) targetRootBp.getProperty(propName);
				if (tgtProp != null && (propName.equals("sports") || propName.equals("salaryComponentTypes")
						|| propName.equals("trainerattributes") || propName.equals("locations")
						|| propName.equals("closingperiods"))) {
					srcColProp.cloneValue(tgtProp, targetRootBp.getContainer());
				}
			}
			for (PropertyAssociationend srcColProp : srcRootMd.getColPropertiesComposition()) {
				final String propName = srcColProp.getName();
				final PropertyAssociationend tgtProp = (PropertyAssociationend) targetRootBp.getProperty(propName);
				if (tgtProp != null) {
					if (propName.equals("trainerroles")) {
						for (final Object o1 : (Collection<?>) srcColProp.getValue()) {
							final TrainerRole srcTr = (TrainerRole) o1;
							TrainerRole clonedTr = new TrainerRole(new String[] { srcTr.getName() });
							targetRootBp.addTrainerrole(clonedTr);
							for (PropertyAssociationend srcColProp1 : srcTr.getColPropertiesComposition()) {
								final String propName1 = srcColProp1.getName();
								final PropertyAssociationend tgtProp1 = (PropertyAssociationend) clonedTr
										.getProperty(propName1);
								if (propName1.equals("salarys")) {
									srcColProp1.cloneValue(tgtProp1, targetRootBp.getContainer());
								} else if (propName1.equals("personalSalarys")) {
									if (srcColProp1.getValue() != null) {
										for (Object o3 : (ReadonlyListCollection<?>) srcColProp1.getValue()) {
											final PersonalSalary ps = (PersonalSalary) o3;
											final Trainer person = ps.getPerson();
											if (person.getDepartments().contains(department)) {
												final PersonalSalary clonedPs = new PersonalSalary();
												((PropertyCollection) clonedPs.getProperty("person")).addLink(
														new LinkFrozen(person.getIdString()), false, false, false);
												clonedPs.setMoney(ps.getMoney());
												clonedPs.setTime(ps.getTime());
												clonedTr.addPersonalSalary(clonedPs);
												if (ps.getComponents() != null) {
													for (SalaryComponent sc : ps.getComponents()) {
														final SalaryComponent clonedSc = new SalaryComponent();
														((PropertyCollection) clonedSc
																.getProperty("salaryComponentType"))
																		.addLink(
																				new LinkFrozen(
																						sc.getSalaryComponentType()
																								.getIdString()),
																				false, false, false);
														clonedSc.setDescription(sc.getDescription());
														clonedSc.setMoney(sc.getMoney());
														clonedPs.addComponent(clonedSc);
													}
												}
											}
										}
									}
								}
							}
						}
					} else if (propName.equals("clubs")) {
						for (final Object o1 : (Collection<?>) srcColProp.getValue()) {
							final Club srcClub = (Club) o1;
							if (srcClub.getDepartments().contains(department)) {
								Club clonedClub = new Club(new String[] { srcClub.getName() });
								targetRootBp.addClub(clonedClub);
								for (final Department srcDep : srcClub.getDepartments()) {
									if (srcDep.equals(department)) {
										final Department clonedDep = new Department(
												new String[] { department.getName() });
										clonedClub.addDepartment((Department) clonedDep);
										for (Property prop : clonedDep.getPropertyList()) {
											if (!prop.getName().equals("user")) {
												final Property srcProp1 = srcDep.getProperty(prop.getName());
												srcProp1.cloneValue(prop, targetRootBp.getContainer());
											}
										}
									}
								}
							}
						}
					} else if (propName.equals("trainers")) {
						tgtProp.setValue(new ArrayList<Object>());
						for (final Object o1 : (Collection<?>) srcColProp.getValue()) {
							final Trainer srcTrainer = (Trainer) o1;
							if (srcTrainer.getDepartments() != null
									&& srcTrainer.getDepartments().contains(department)) {
								Trainer clonedTrainer = new Trainer(new String[] { srcTrainer.getLastname(),
										srcTrainer.getFirstname(), srcTrainer.getMiddlename() });
								targetRootBp.addTrainer(clonedTrainer);
								for (Property prop : clonedTrainer.getPropertyList()) {
									if (!prop.getName().equals("user")) {
										final Property srcProp1 = srcTrainer.getProperty(prop.getName());
										srcProp1.cloneValue(prop, targetRootBp.getContainer());
									}
								}
							}
						}
					}
				}
			}
			for (final Trainer trainer : targetRootBp.getTrainers()) {
				trainer.setUser(null);
				final List<Link> trpProps = (List<Link>) trainer.getProperty("trainerplannings").getValue();
				if (trpProps != null) {
					final List<Link> newTrpProps = new ArrayList<Link>();
					for (final Link trplink : trpProps) {
						final TrainerPlanning srcTrp = (TrainerPlanning) srcMasterDoc
								.findBean("org.rapidbeans.clubadmin.domain.TrainerPlanning", trplink.getIdString());
						final Department srcParentDep = (Department) srcTrp.getParentBean().getParentBean();
						if (srcParentDep.equals(department)) {
							newTrpProps.add(trplink);
						}
					}
					try {
						ThreadLocalValidationSettings.validationOff();
						trainer.getProperty("trainerplannings").setValue(newTrpProps);
					} finally {
						ThreadLocalValidationSettings.remove();
					}
				}
				final List<Link> depProps = (List<Link>) trainer.getProperty("departments").getValue();
				if (depProps != null) {
					final List<Link> newDepProps = new ArrayList<Link>();
					for (final Link deplink : depProps) {
						if (deplink != null && deplink.toString().equals(department.getIdString())) {
							newDepProps.add(deplink);
						}
					}
					try {
						ThreadLocalValidationSettings.validationOff();
						trainer.getProperty("departments").setValue(newDepProps);
					} finally {
						ThreadLocalValidationSettings.remove();
					}
				}
			}
		}

		targetDoc.resolveFrozenLinks();

		if (targetRootBp.getClubs().size() == 1) {
			targetRootBp.setForSingleClub(targetRootBp.getClubs().iterator().next());
			if (targetRootBp.getClubs().iterator().next().getDepartments().size() == 1) {
				targetRootBp.setForSingleDepartment(
						targetRootBp.getClubs().iterator().next().getDepartments().iterator().next());
			}
		}
		return targetDoc;
	}
}
