/*
 * RapidBeans Application RapidClubAdmin: Overview.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 23.08.2007
 */
package org.rapidbeans.clubadmin.domain.report;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.MasterData;
import org.rapidbeans.clubadmin.domain.RapidClubAdminBusinessLogicException;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.Training;
import org.rapidbeans.clubadmin.domain.TrainingDate;
import org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer;
import org.rapidbeans.clubadmin.domain.TrainingRegular;
import org.rapidbeans.clubadmin.domain.TrainingSpecial;
import org.rapidbeans.clubadmin.domain.TrainingState;
import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.clubadmin.domain.export.ExportJob;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.clubadmin.presentation.swing.ReportPresentationDialogSwing;
import org.rapidbeans.clubadmin.service.Umlaut;
import org.rapidbeans.core.basic.PropertyDate;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.datasource.query.Query;
import org.rapidbeans.domain.finance.Currency;
import org.rapidbeans.domain.finance.Money;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.service.Action;

/**
 * Report business logic. Overview of held trainings and salary for one trainer.
 * 
 * @author Martin Bluemel
 */
public class TrainingsTimeStatisticsAction extends Action {

	public void execute() {
		TypePropertyCollection.setDefaultCharSeparator(',');
		final NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		@SuppressWarnings("unused")
		final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();
		final MasterData masterData = app.getMasterData();
		final TrainingsTimeStat stat = new TrainingsTimeStat();
		for (final Department masterDepartment : masterData.getClubs().get(0).getDepartments()) {
			final TrainingsList trainingsList = (TrainingsList) app.loadTrainingslistDocument(null, masterDepartment)
					.getRoot();
			final Department department = findDepartmentWithName(trainingsList, masterDepartment);
			if (department == null) {
				throw new RapidBeansRuntimeException(
						String.format("Department \"%s\" not found in traings list file", masterDepartment.getName()));
			}
			for (final Trainer trainer : department.getTrainers()) {
				execute(stat, trainer, department, app.getCurrentLocale());				
			}
		}
		final String report2 = stat.report();
		new ReportPresentationDialogSwing(stat.getReport() + "\n\n" + report2, "Bericht: abgehaltene Trainingszeiten").show();
	}

	private Department findDepartmentWithName(TrainingsList trainingsList, Department masterDepartment) {
		for (final Department department : trainingsList.getClubs().get(0).getDepartments()) {
			if (department.getName().equals(masterDepartment.getName())) {
				return department;
			}
		}
		return null;
	}

	public static void execute(final TrainingsTimeStat stat, final Trainer trainer, final Department department,
			final RapidBeansLocale locale) {
		final StringBuilder sb = stat.getSb();
		sb.append("Trainings" + Umlaut.L_UUML + "bersicht   Trainer: " + trainer.getLastname() + ", "
				+ trainer.getFirstname() + ",   Abteilung: ");
		sb.append(department.toString());
		sb.append(PlatformHelper.getLineFeed());
		sb.append("---------------------------------------------------------------------------");
		sb.append(PlatformHelper.getLineFeed());
		int n = 1;
		Money sumMoneyEarned = null;

		final List<TrainingHeldByTrainer> allTrhbts = new ArrayList<TrainingHeldByTrainer>();
		final List<TrainingHeldByTrainer> trhbts = findTrainigsHeld(trainer, department);
		for (TrainingHeldByTrainer trhbt : trhbts) {
			allTrhbts.add(trhbt);
		}

		ExportJob.sort(allTrhbts);

		for (final TrainingHeldByTrainer trhbt : allTrhbts) {
			final Training training = (Training) trhbt.getParentBean();
			TrainingRegular trainingRegular = null;
			TrainingDate trainingDate = null;
			TrainingSpecial trainingSpecial = null;
			if (training instanceof TrainingRegular) {
				trainingRegular = (TrainingRegular) training;
				trainingDate = (TrainingDate) trainingRegular.getParentBean();
			} else if (training instanceof TrainingSpecial) {
				trainingSpecial = (TrainingSpecial) training;
			}
			Money moneyEarned = null;
			if (training.getState() == TrainingState.checked) {
				moneyEarned = trhbt.getMoneyEarned();
				stat.add(department, trhbt);
			}
			if (moneyEarned != null) {
				if (sumMoneyEarned == null) {
					sumMoneyEarned = new Money(moneyEarned.getMagnitude(), (Currency) moneyEarned.getUnit());
				} else {
					if (!(moneyEarned.getUnit() == sumMoneyEarned.getUnit())) {
						throw new RapidClubAdminBusinessLogicException("xxx", "unexcpected money unit");
					}
					sumMoneyEarned = new Money(sumMoneyEarned.getMagnitude().add(moneyEarned.getMagnitude()),
							(Currency) sumMoneyEarned.getUnit());
				}
			}
			sb.append(StringHelper.fillUp(Integer.toString(n), 3, ' ', StringHelper.FillMode.left));
			sb.append(". ");
			sb.append(StringHelper.fillUp(PropertyDate.formatDate(training.getDate(), locale), 11, ' ',
					StringHelper.FillMode.right));
			sb.append(
					StringHelper.fillUp(
							trainingDate == null ? "(S) " + trainingSpecial.getDayofweek().toStringGui(locale)
									: trainingDate.getDayofweek().toStringGui(locale),
							15, ' ', StringHelper.FillMode.right));
			sb.append(StringHelper.fillUp(trainingDate == null
					? trainingSpecial.getTimestart().toString() + " - " + trainingSpecial.getTimeend().toString()
					: trainingDate.getTimestart().toString() + " - " + trainingDate.getTimeend().toString(), 15, ' ', StringHelper.FillMode.right));
			sb.append(StringHelper.fillUp(
					trainingDate == null ? trainingSpecial.getName() : trainingDate.getName().toString(), 60, ' ',
					StringHelper.FillMode.right));
			if (moneyEarned != null) {
				sb.append(StringHelper.fillUp(moneyEarned.toStringGui(locale, 2, 2), 12, ' ',
						StringHelper.FillMode.left));
			} else {
				switch (training.getState()) {
				case cancelled:
					sb.append(training.getState().toStringGui(locale));
					break;
				case closed:
					sb.append(training.getState().toStringGui(locale));
					break;
				case asplanned:
				case modified:
					sb.append(training.getState().toStringGui(locale));
					break;
				default:
					break;
				}
			}
			sb.append(PlatformHelper.getLineFeed());
			n++;
		}
		if (sumMoneyEarned == null) {
			sumMoneyEarned = new Money(BigDecimal.ZERO, Currency.euro);
		}
		sb.append("===========================================================================");
		sb.append(PlatformHelper.getLineFeed());
		sb.append("                                                               ");
		sb.append(StringHelper.fillUp(sumMoneyEarned.toStringGui(locale, 2, 2), 12, ' ', StringHelper.FillMode.left));
		sb.append(PlatformHelper.getLineFeed());
	}

	public static List<TrainingHeldByTrainer> findTrainigsHeld(final Trainer trainer, final Department department) {
		final String qs = "org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer[" + "trainer[id = '"
				+ trainer.getIdString() + "']" + " & parentBean[parentBean[parentBean[id = '" + department.getIdString()
				+ "']]]" + "]";
		final Query query1 = new Query(qs);
		final List<RapidBean> res1 = department.getContainer().findBeansByQuery(query1);
		final Query query2 = new Query(
				"org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer[" + "trainer[id = '" + trainer.getIdString()
						+ "']" + " & parentBean[parentBean[id = '" + department.getIdString() + "']]]" + "]");
		final List<RapidBean> res2 = department.getContainer().findBeansByQuery(query2);
		for (final RapidBean specialTraining : res2) {
			res1.add(specialTraining);
		}
		final RapidBean[] a1 = res1.toArray(new RapidBean[0]);
		Arrays.sort(a1);
		final List<TrainingHeldByTrainer> trhbts = new ArrayList<TrainingHeldByTrainer>();
		for (final RapidBean bean : a1) {
			trhbts.add((TrainingHeldByTrainer) bean);
		}
		return trhbts;
	}
}
