/*
 * RapidBeans Application RapidClubAdmin: Overview.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 23.08.2007
 */
package org.rapidbeans.clubadmin.domain.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.RapidClubAdminBusinessLogicException;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.Training;
import org.rapidbeans.clubadmin.domain.TrainingDate;
import org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer;
import org.rapidbeans.clubadmin.domain.TrainingRegular;
import org.rapidbeans.clubadmin.domain.TrainingState;
import org.rapidbeans.clubadmin.domain.export.ExportJob;
import org.rapidbeans.clubadmin.service.Umlaut;
import org.rapidbeans.core.basic.PropertyDate;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.datasource.query.Query;
import org.rapidbeans.domain.finance.Currency;
import org.rapidbeans.domain.finance.Money;

/**
 * Report business logic. Overview of held trainings and salary for one trainer.
 * 
 * @author Martin Bluemel
 */
public class Overview {

	public static String asString(final List<Trainer> trainers, final List<Department> departments,
			final RapidBeansLocale locale) {
		final StringBuffer sb = new StringBuffer();
		if (trainers.size() == 0) {
			sb.append("Kein Trainer ausgew" + Umlaut.L_AUML + "hlt!");
		} else if (departments.size() == 0) {
			sb.append("Keine Abteilung ausgew" + Umlaut.L_AUML + "hlt!");
		} else if (trainers.size() == 1 && departments.size() == 1) {
			final Trainer trainer = trainers.get(0);
			final Department department = departments.get(0);
			List<TrainingHeldByTrainer> res = findTrainigsHeld(trainer, department);
			sb.append("Trainings" + Umlaut.L_UUML + "bersicht   Trainer: " + trainer.getLastname() + ", "
					+ trainer.getFirstname() + ",   Abteilung: " + department.toString() + "\n");
			sb.append("---------------------------------------------------------------------------\n");
			int i = 1;
			Money sumMoneyEarned = null;
			for (final TrainingHeldByTrainer trhbt : res) {
				final Training training = (Training) trhbt.getParentBean();
				Money moneyEarned = null;
				if (training.getState() == TrainingState.checked) {
					moneyEarned = trhbt.getMoneyEarned();
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
				sb.append(StringHelper.fillUp(Integer.toString(i), 3, ' ', StringHelper.FillMode.left));
				sb.append(". ");
				sb.append(StringHelper.fillUp(PropertyDate.formatDate(training.getDate(), locale), 11, ' ',
						StringHelper.FillMode.right));
				sb.append(StringHelper.fillUp(training.getDayofweek().toStringGui(locale), 11, ' ',
						StringHelper.FillMode.right));
				sb.append(StringHelper.fillUp(training.getTimestart().toString(), 6, ' ', StringHelper.FillMode.right));
				sb.append(StringHelper.fillUp(training.getName().toString(), 30, ' ', StringHelper.FillMode.right));
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
				sb.append('\n');
				i++;
			}
			if (sumMoneyEarned == null) {
				sumMoneyEarned = new Money(BigDecimal.ZERO, Currency.euro);
			}
			sb.append("===========================================================================\n");
			sb.append("                                                               ");
			sb.append(
					StringHelper.fillUp(sumMoneyEarned.toStringGui(locale, 2, 2), 12, ' ', StringHelper.FillMode.left));
			sb.append('\n');
		} else if (trainers.size() > 1) {
			sb.append("Mehr als ein Trainer ausgew" + Umlaut.L_AUML + "hlt!");
		} else if (departments.size() > 1) {
			final Trainer trainer = trainers.get(0);
			sb.append("Trainings" + Umlaut.L_UUML + "bersicht   Trainer: " + trainer.getLastname() + ", "
					+ trainer.getFirstname() + ",   Abteilungen: ");
			boolean firstRun = true;
			for (final Department dep : departments) {
				if (!firstRun) {
					sb.append(", ");
				}
				sb.append(dep.toString());
				firstRun = false;
			}
			sb.append(PlatformHelper.getLineFeed());
			sb.append("---------------------------------------------------------------------------");
			sb.append(PlatformHelper.getLineFeed());
			int n = 1;
			Money sumMoneyEarned = null;

			final List<TrainingHeldByTrainer> allTrhbts = new ArrayList<TrainingHeldByTrainer>();
			for (Department dep : departments) {
				final List<TrainingHeldByTrainer> trhbts = findTrainigsHeld(trainer, dep);
				for (TrainingHeldByTrainer trhbt : trhbts) {
					allTrhbts.add(trhbt);
				}
			}

			ExportJob.sort(allTrhbts);

			for (final TrainingHeldByTrainer trhbt : allTrhbts) {
				final TrainingRegular training = (TrainingRegular) trhbt.getParentBean();
				final TrainingDate trainingDate = (TrainingDate) training.getParentBean();
				Money moneyEarned = null;
				if (training.getState() == TrainingState.checked) {
					moneyEarned = trhbt.getMoneyEarned();
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
				sb.append(StringHelper.fillUp(trainingDate.getDayofweek().toStringGui(locale), 11, ' ',
						StringHelper.FillMode.right));
				sb.append(StringHelper.fillUp(trainingDate.getTimestart().toString(), 6, ' ',
						StringHelper.FillMode.right));
				sb.append(StringHelper.fillUp(trainingDate.getName().toString(), 60, ' ', StringHelper.FillMode.right));
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
			sb.append(
					StringHelper.fillUp(sumMoneyEarned.toStringGui(locale, 2, 2), 12, ' ', StringHelper.FillMode.left));
			sb.append(PlatformHelper.getLineFeed());
		}
		return sb.toString();
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
