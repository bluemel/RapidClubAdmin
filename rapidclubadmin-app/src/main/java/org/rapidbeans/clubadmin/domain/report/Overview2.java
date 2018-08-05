/*
 * RapidBeans Application RapidClubAdmin: Overview.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 23.08.2007
 */
package org.rapidbeans.clubadmin.domain.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.SalaryComponent;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.Training;
import org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer;
import org.rapidbeans.clubadmin.domain.TrainingState;
import org.rapidbeans.clubadmin.domain.export.ExportJob;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.util.PlatformHelper;

/**
 * FIXME (BH): This is a copy of the Overview class and should be merged/cleaned
 * up.
 * 
 * Report business logic. Overview of held trainings and salary for one trainer.
 * 
 * @author Martin Bluemel
 */
public class Overview2 {

	private static final String LF = PlatformHelper.getLineFeed();

	public static String asString(final List<Trainer> trainers, final List<Department> departments,
			final RapidBeansLocale locale) {
		final StringBuffer sb = new StringBuffer();
		if (trainers.size() == 0) {
			sb.append("Kein Trainer ausgew�hlt!");
		} else if (departments.size() == 0) {
			sb.append("Keine Abteilung ausgew�hlt!");
		} else if (trainers.size() == 1 && departments.size() == 1) {
			final Trainer trainer = trainers.get(0);
			final Department department = departments.get(0);
			final List<TrainingHeldByTrainer> trhbts = Overview.findTrainigsHeld(trainer, department);
			sb.append("Trainings�bersicht   Trainer: " + trainer.getLastname() + ", " + trainer.getFirstname()
					+ ",   Abteilung: " + department.toString() + LF);
			sb.append("---------------------------------------------------------------------------" + LF);
			final List<TrainingHeldByTrainer> allTrhbts = new ArrayList<TrainingHeldByTrainer>();
			for (TrainingHeldByTrainer trhbt : trhbts) {
				allTrhbts.add(trhbt);
			}
			dumpTrhbts(allTrhbts, sb, locale);
		} else if (trainers.size() > 1) {
			sb.append("Mehr als ein Trainer ausgew�hlt!");
		} else if (departments.size() > 1) {
			final Trainer trainer = trainers.get(0);
			sb.append("Trainings�bersicht   Trainer: " + trainer.getLastname() + ", " + trainer.getFirstname()
					+ ",   Abteilungen: ");
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

			final List<TrainingHeldByTrainer> allTrhbts = new ArrayList<TrainingHeldByTrainer>();
			for (Department dep : departments) {
				final List<TrainingHeldByTrainer> trhbts = Overview.findTrainigsHeld(trainer, dep);
				for (TrainingHeldByTrainer trhbt : trhbts) {
					allTrhbts.add(trhbt);
				}
			}
			ExportJob.sort(allTrhbts);
			dumpTrhbts(allTrhbts, sb, locale);
		}
		return sb.toString();
	}

	private static void dumpTrhbts(List<TrainingHeldByTrainer> allTrhbts, StringBuffer sb, RapidBeansLocale locale) {

		Map<String, SummedComponent> sums = new HashMap<String, SummedComponent>();
		double overallMoney = 0;

		for (TrainingHeldByTrainer trhbt : allTrhbts) {
			final Training training = (Training) trhbt.getParentBean();
			if (training.getState() != TrainingState.checked) {
				continue;
			}

			final Department department = training.getDepartment();
			double time = training.getTimeend().getMagnitudeDouble() - training.getTimestart().getMagnitudeDouble();

			for (SalaryComponent cmp : trhbt.getSalary().getComponents()) {
				SummedComponent sc = getSummedComponent(department, cmp, sums);
				double timeExp = cmp.getTime().getMagnitudeDouble();
				double money = cmp.getMoney().getMagnitudeDouble();
				double units = time / timeExp;
				sc.units += units;
				sc.money += money * units;
				overallMoney += money * units;
			}
		}

		for (SummedComponent sc : sums.values()) {
			sb.append("  " + sc.description + " (" + sc.unit + ") " + sc.units + " Einheiten zu je " + sc.unitMoney
					+ " = " + sc.money + " Euro" + LF);
		}
		sb.append("Summe: " + overallMoney + " Euro" + LF + LF);
	}

	private static SummedComponent getSummedComponent(Department department, SalaryComponent cmp,
			Map<String, SummedComponent> sums) {
		String description = department.getName() + "/" + cmp.getDescription();
		SummedComponent sc = sums.get(description);
		if (sc == null) {
			sc = new SummedComponent(description, cmp.getTime().getMagnitudeDouble(),
					cmp.getMoney().getMagnitudeDouble());
			sums.put(description, sc);
		}
		return sc;
	}

	private static class SummedComponent {
		private final String description;

		private final double unit;

		private final double unitMoney;

		private double units = 0;

		private double money = 0;

		SummedComponent(String description, double unit, double unitMoney) {
			this.description = description;
			this.unit = unit;
			this.unitMoney = unitMoney;
		}
	}
}
