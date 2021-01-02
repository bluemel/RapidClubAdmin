package org.rapidbeans.clubadmin.service;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Ignore;
import org.junit.Test;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.Training;
import org.rapidbeans.clubadmin.domain.TrainingDate;
import org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer;
import org.rapidbeans.clubadmin.domain.TrainingState;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.core.util.StringHelper.FillMode;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.domain.finance.Money;
import org.rapidbeans.domain.math.DayOfWeek;
import org.rapidbeans.domain.math.Time;
import org.rapidbeans.domain.math.UnitTime;

public class HistoryReportTestRunManually {

	@Test
	@Ignore
	public void generateMultipleTrainerStatistics() {
		TypePropertyCollection.setDefaultCharSeparator(',');
		final File histdir = new File("history");
		assertTrue(histdir.exists());

		// final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		final NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);

		final List<Trstat> trainings = new ArrayList<Trstat>();
		readTrainingsFromHistory(histdir, trainings, 2017, 2018);

		SimpleDateFormat sfmt = new SimpleDateFormat("dd.MM.yyyy");
		for (final Trstat trstat : trainings) {
			final Training training = trstat.getTraining();
			final Collection<TrainingHeldByTrainer> trhbts = training.getHeldbytrainersSortedByValue();
			switch (trhbts.size()) {
			case 0:
				if (training.getState() != TrainingState.cancelled && training.getState() != TrainingState.closed) {
					System.out.println(String.format("%s: !!! kein Trainer??? %s", sfmt.format(training.getDate()),
							training.getState()));
				}
				break;
			case 1:
				// System.out.println(String.format("%s: 1 Trainer", training.getDate(),
				// training.getState()));
				break;
			default:
				System.out.print(
						String.format("%s %s %s: %d Trainer: ", training.getName(), german(training.getDayofweek()),
								sfmt.format(training.getDate()), trhbts.size(), training.getState()));
				int i = 0;
				for (TrainingHeldByTrainer trhbt : training.getHeldbytrainersSortedByValue()) {
					final Money moneyEarned = trhbt.getMoneyEarned();
					final Trainer trainer = trhbt.getTrainer();
					final String trainerName = trainer == null ? "Kein Trainer vermerkt"
							: trainer.getLastname() + ", " + trainer.getFirstname();
					final String role = trhbt.getRole().getName();
					if (i > 0) {
						System.out.print(" || ");
					}
					System.out.print(String.format("%s / %s: %s €", role, trainerName,
							nf.format(moneyEarned.getMagnitudeDouble())));
					i++;
				}
				System.out.println();
			}
		}
	}

	private String german(DayOfWeek dayofweek) {
		switch (dayofweek) {
		case monday:
			return "MO";
		case tuesday:
			return "DI";
		case wednesday:
			return "MI";
		case thursday:
			return "DO";
		case friday:
			return "FR";
		case saturday:
			return "SA";
		case sunday:
			return "SO";
		}
		return "???";
	}

	@Test
	@Ignore
	public void generateOverallTrainingsStatistics() {
		TypePropertyCollection.setDefaultCharSeparator(',');
		final File histdir = new File("history");
		assertTrue(histdir.exists());

		// final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		final NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);

		final List<Trstat> trainings = new ArrayList<Trstat>();
		final List<Integer> years = readTrainingsFromHistory(histdir, trainings, 2009, 2018);

		// collect statistics
		final Map<Integer, Map<Department, Money>> statDepOverYears = new HashMap<Integer, Map<Department, Money>>();
		final Map<Integer, Map<Department, Integer>> statDepTrainingsOverYears = new HashMap<Integer, Map<Department, Integer>>();
		final Map<Integer, Map<Department, Time>> statDepTrTimeOverYears = new HashMap<Integer, Map<Department, Time>>();
		final Map<Integer, Map<Department, Map<String, Money>>> statDepTrdateOverYears = new HashMap<Integer, Map<Department, Map<String, Money>>>();
		final Map<Integer, Map<Department, Map<String, Money>>> statDepTrainerOverYears = new HashMap<Integer, Map<Department, Map<String, Money>>>();

		for (final Trstat trstat : trainings) {
			final Training training = trstat.getTraining();
			final int year = trstat.getYear();
			// final boolean specialTraining = training.getParentBean()
			// instanceof Department;
			Map<Department, Money> depMap = statDepOverYears.get(year);
			if (depMap == null) {
				depMap = new HashMap<Department, Money>();
				statDepOverYears.put(year, depMap);
			}
			Map<Department, Integer> trSumMap = statDepTrainingsOverYears.get(year);
			if (trSumMap == null) {
				trSumMap = new HashMap<Department, Integer>();
				statDepTrainingsOverYears.put(year, trSumMap);
			}
			Map<Department, Time> trTimeMap = statDepTrTimeOverYears.get(year);
			if (trTimeMap == null) {
				trTimeMap = new HashMap<Department, Time>();
				statDepTrTimeOverYears.put(year, trTimeMap);
			}
			Map<Department, Map<String, Money>> trDateDepMap = statDepTrdateOverYears.get(year);
			if (trDateDepMap == null) {
				trDateDepMap = new HashMap<Department, Map<String, Money>>();
				statDepTrdateOverYears.put(year, trDateDepMap);
			}
			Map<Department, Map<String, Money>> trainerDepMap = statDepTrainerOverYears.get(year);
			if (trainerDepMap == null) {
				trainerDepMap = new HashMap<Department, Map<String, Money>>();
				statDepTrainerOverYears.put(year, trainerDepMap);
			}
			final Department dep = training.getParentBean() instanceof Department
					? (Department) training.getParentBean()
					: (Department) training.getParentBean().getParentBean();
			Money depYearMoney = depMap.get(dep);
			if (depYearMoney == null) {
				depYearMoney = new Money("0 euro");
			}
			Integer trSum = trSumMap.get(dep);
			if (trSum == null) {
				trSum = 0;
			}
			Time trTime = trTimeMap.get(dep);
			if (trTime == null) {
				trTime = new Time("0 h");
			}
			Map<String, Money> trDateMap = trDateDepMap.get(dep);
			if (trDateMap == null) {
				trDateMap = new HashMap<String, Money>();
				trDateDepMap.put(dep, trDateMap);
			}
			final String trDateName = training.getParentBean() instanceof Department
					? "Sonderveranstaltung " + ((Department) training.getParentBean()).getName()
					: ((TrainingDate) training.getParentBean()).getDayofweek().toString() + ", "
							+ ((TrainingDate) training.getParentBean()).getTimestart() + "-"
							+ ((TrainingDate) training.getParentBean()).getTimeend() + ", "
							+ ((TrainingDate) training.getParentBean()).getName();
			Money depTradateYearMoney = trDateMap.get(trDateName);
			if (depTradateYearMoney == null) {
				depTradateYearMoney = new Money("0 euro");
			}
			Map<String, Money> trainerMap = trainerDepMap.get(dep);
			if (trainerMap == null) {
				trainerMap = new HashMap<String, Money>();
				trainerDepMap.put(dep, trainerMap);
			}

			for (TrainingHeldByTrainer trhbt : training.getHeldbytrainersSortedByValue()) {
				final Money moneyEarned = trhbt.getMoneyEarned();
				depYearMoney = (Money) depYearMoney.add(moneyEarned);
				depTradateYearMoney = (Money) depTradateYearMoney.add(moneyEarned);

				final Trainer trainer = trhbt.getTrainer();
				final String trainerName = trainer == null ? "Kein Trainer vermerkt"
						: trainer.getLastname() + ", " + trainer.getFirstname();
				Money depTrainerYearMoney = trainerMap.get(trainerName);
				if (depTrainerYearMoney == null) {
					depTrainerYearMoney = new Money("0 euro");
				}
				depTrainerYearMoney = (Money) depTrainerYearMoney.add(moneyEarned);
				trainerMap.put(trainerName, depTrainerYearMoney);
			}
			depMap.put(dep, depYearMoney);
			trSumMap.put(dep, ++trSum);
			trTimeMap.put(dep, (Time) trTime.add(training.getTimeWorked(UnitTime.h)));
			trDateMap.put(trDateName, depTradateYearMoney);
		}

		for (final int year : years) {
			System.out.println();
			System.out.println(String.format("--- %s --------------------", year));
			Money sumYears = new Money("0 euro");
			Money sumYearsWoGrundschule = new Money("0 euro");
			Map<Department, Money> depMap = statDepOverYears.get(year);
			Map<Department, Integer> trSumMap = statDepTrainingsOverYears.get(year);
			Map<Department, Time> trTimeMap = statDepTrTimeOverYears.get(year);
			for (final Entry<Department, Money> entry : depMap.entrySet()) {
				System.out.println(String.format("%s %s Trainings, %s: %s EUR",
						StringHelper.fillUp(entry.getKey().getName(), 14, ' ', FillMode.right),
						StringHelper.fillUp(Integer.toString(trSumMap.get(entry.getKey())), 5, ' ', FillMode.left),
						StringHelper.fillUp(trTimeMap.get(entry.getKey()).toString(), 7, ' ', FillMode.left),
						StringHelper.fillUp(nf.format(entry.getValue().getMagnitudeDouble()), 10, ' ', FillMode.left)));
				sumYears = (Money) sumYears.add(entry.getValue());
				if (!entry.getKey().getName().equals("Grundschule")) {
					sumYearsWoGrundschule = (Money) sumYearsWoGrundschule.add(entry.getValue());
				}
			}
			System.out.println(String.format("-----------------------------"));
			if (!sumYearsWoGrundschule.equals(sumYears)) {
				System.out.println(String.format("Summe Jahr (Gs)%s EUR",
						StringHelper.fillUp(nf.format(sumYears.getMagnitudeDouble()), 10, ' ', FillMode.left)));
			}
			System.out.println(String.format("Summe Jahr     %s EUR", StringHelper
					.fillUp(nf.format(sumYearsWoGrundschule.getMagnitudeDouble()), 10, ' ', FillMode.left)));
			System.out.println(String.format("-----------------------------"));
		}

		System.out.println();
		System.out.println();
		System.out.println();
		for (final int year : years) {
			System.out.println();
			System.out.println();
			System.out.println(String.format("--- %s --------------------", year));
			Money sumYears = new Money("0 euro");
			Money sumYearsWoGrundschule = new Money("0 euro");
			Map<Department, Money> depMap = statDepOverYears.get(year);
			Map<Department, Integer> trSumMap = statDepTrainingsOverYears.get(year);
			Map<Department, Time> trTimeMap = statDepTrTimeOverYears.get(year);
			Map<Department, Map<String, Money>> trDateDepMap = statDepTrdateOverYears.get(year);
			Map<Department, Map<String, Money>> trainerDepMap = statDepTrainerOverYears.get(year);
			for (final Entry<Department, Money> entry : depMap.entrySet()) {
				System.out.println();
				System.out.println(String.format("%s %s Trainings, %s: %s EUR",
						StringHelper.fillUp(entry.getKey().getName(), 14, ' ', FillMode.right),
						StringHelper.fillUp(Integer.toString(trSumMap.get(entry.getKey())), 5, ' ', FillMode.left),
						StringHelper.fillUp(trTimeMap.get(entry.getKey()).toString(), 7, ' ', FillMode.left),
						StringHelper.fillUp(nf.format(entry.getValue().getMagnitudeDouble()), 10, ' ', FillMode.left)));
				sumYears = (Money) sumYears.add(entry.getValue());
				if (!entry.getKey().getName().equals("Grundschule")) {
					sumYearsWoGrundschule = (Money) sumYearsWoGrundschule.add(entry.getValue());
				}
				System.out.println(String.format("-----------------------------"));
				final Map<String, Money> trDateMap = trDateDepMap.get(entry.getKey());
				for (final Entry<String, Money> entryTrdate : trDateMap.entrySet()) {
					System.out.println(String.format("  TERMIN  %s: %s EUR",
							StringHelper.fillUp(entryTrdate.getKey(), 60, ' ', FillMode.right), StringHelper.fillUp(
									nf.format(entryTrdate.getValue().getMagnitudeDouble()), 10, ' ', FillMode.left)));
				}
				System.out.println(String.format("-----------------------------"));
				final Map<String, Money> trainerMap = trainerDepMap.get(entry.getKey());
				for (final Entry<String, Money> entryTrainer : trainerMap.entrySet()) {
					System.out.println(String.format("  TRAINER %s: %s EUR",
							StringHelper.fillUp(entryTrainer.getKey(), 60, ' ', FillMode.right), StringHelper.fillUp(
									nf.format(entryTrainer.getValue().getMagnitudeDouble()), 10, ' ', FillMode.left)));
				}
			}
			System.out.println(String.format("-----------------------------"));
			if (!sumYearsWoGrundschule.equals(sumYears)) {
				System.out.println(String.format("Summe Jahr (Gs)%s EUR",
						StringHelper.fillUp(nf.format(sumYears.getMagnitudeDouble()), 10, ' ', FillMode.left)));
			}
			System.out.println(String.format("Summe Jahr     %s EUR", StringHelper
					.fillUp(nf.format(sumYearsWoGrundschule.getMagnitudeDouble()), 10, ' ', FillMode.left)));
			System.out.println(String.format("-----------------------------"));
		}
	}

	private final List<Integer> readTrainingsFromHistory(final File histdir, final List<Trstat> trainings,
			final int minYear, final int maxYear) {
		final List<Integer> years = new ArrayList<Integer>();
		for (final File subdir1 : histdir.listFiles()) {
			final int year = Integer.parseInt(subdir1.getName().substring(0, 4));
			if (year < minYear || year > maxYear) {
				continue;
			}
			if (!years.contains(year)) {
				years.add(year);
			}
			System.out.println(String.format("reading file: %s, %d...", subdir1, year));
			for (final File subdir2 : subdir1.listFiles()) {
				final File trlist = new File(subdir2, "trainingslist.xml");
				if (trlist.exists()) {
					// System.out.println(trlist.getAbsolutePath());
					stat(new Document(trlist), year, trainings);
				}
			}
		}
		return years;
	}

	private void stat(final Document doc, final int year, final List<Trstat> trainings) {
		for (final RapidBean bean : doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training")) {
			final Training training = (Training) bean;
			trainings.add(new Trstat(year, training));
		}
	}

	class Trstat {
		private final int year;

		private final Training training;

		public Trstat(final int year, final Training training) {
			this.year = year;
			this.training = training;
		}

		public int getYear() {
			return year;
		}

		public Training getTraining() {
			return training;
		}
	}
}
