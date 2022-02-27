package org.rapidbeans.clubadmin.domain.report;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.MasterData;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.Training;
import org.rapidbeans.clubadmin.domain.TrainingDate;
import org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer;
import org.rapidbeans.clubadmin.domain.TrainingState;
import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.clubadmin.presentation.swing.ReportPresentationDialogSwing;
import org.rapidbeans.core.exception.PropValueNullException;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.domain.finance.Money;
import org.rapidbeans.domain.math.DayOfWeek;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.service.Action;

public class OverSupervisedTrainings extends Action {

	public void execute() {
		TypePropertyCollection.setDefaultCharSeparator(',');
		final NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		@SuppressWarnings("unused")
		final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();
		final MasterData masterData = app.getMasterData();
		final StringBuilder report = new StringBuilder();
		final SimpleDateFormat sfmt = new SimpleDateFormat("dd.MM.yyyy");
		for (final Department masterDepartment : masterData.getClubs().get(0).getDepartments()) {
			final TrainingsList trainingsList = (TrainingsList) app.loadTrainingslistDocument(null, masterDepartment)
					.getRoot();
			reportOverSupervisedTrainings(masterDepartment, trainingsList, report, nf, sfmt);
		}
		new ReportPresentationDialogSwing(report.toString(), "Bericht: Mehrfach betreute Trainings").show();
	}

	public void reportOverSupervisedTrainings(final Department masterDepartment, final TrainingsList trainingsList,
			final StringBuilder sb, final NumberFormat nf, SimpleDateFormat sfmt) {
		final Department department = findDepartmentWithName(trainingsList, masterDepartment);
		if (department == null) {
			throw new RapidBeansRuntimeException(
					String.format("Department \"%s\" not found in traings list file", masterDepartment.getName()));
		}
		sb.append("\n");
		sb.append("-----------------------------------------------------------\n");
		sb.append(String.format("Abteilung: %s\n", department.getName()));
		sb.append("-----------------------------------------------------------\n");
		for (final TrainingDate trainingDate : department.getTrainingdates()) {
			for (final Training training : trainingDate.getTrainings()) {
				if (training.getState() != TrainingState.checked) {
					continue;
				}
				final Collection<TrainingHeldByTrainer> trhbts = training.getHeldbytrainersSortedByValue();
				switch (trhbts.size()) {
				case 0:
					if (training.getState() != TrainingState.cancelled && training.getState() != TrainingState.closed) {
						sb.append(String.format("%s: !!! kein Trainer??? %s\n", sfmt.format(training.getDate()),
								training.getState()));
					}
					break;
				case 1:
					// sb.append(String.format("%s: 1 Trainer\n", training.getDate(),
					// training.getState()));
					break;
				default:
					final int cTrain = countTrainersInRole("Trainer", training);
					final int cCotr = countTrainersInRole("Cotrainer", training);
					String overcoaching = checkOvercoachingRules(training, cTrain, cCotr);
					sb.append(String.format("%s %s %s %s: %d Betreuer %d Trainer %d Cotrainer / %s Teilnehmer: ",
							overcoaching, training.getName(), german(training.getDayofweek()),
							sfmt.format(training.getDate()), trhbts.size(), cTrain, cCotr,
							countPaticipiantsAsString(training), training.getState()));
					int i = 0;
					for (TrainingHeldByTrainer trhbt : training.getHeldbytrainersSortedByValue()) {
						final Money moneyEarned = trhbt.getMoneyEarned();
						final Trainer trainer = trhbt.getTrainer();
						final String trainerName = trainer == null ? "Kein Trainer vermerkt"
								: trainer.getLastname() + ", " + trainer.getFirstname();
						final String role = trhbt.getRole().getName();
						if (i > 0) {
							sb.append(" || ");
						}
						sb.append(String.format("%s / %s: %s €", role, trainerName,
								nf.format(moneyEarned.getMagnitudeDouble())));
						i++;
					}
					sb.append("\n");
				}
			}
		}
	}

	private Department findDepartmentWithName(TrainingsList trainingsList, Department masterDepartment) {
		for (final Department department : trainingsList.getClubs().get(0).getDepartments()) {
			if (department.getName().equals(masterDepartment.getName())) {
				return department;
			}
		}
		return null;
	}

	private String checkOvercoachingRules(final Training training, final int cTrain, final int cCotr) {
		final int cPart = countPaticipiants(training);
		switch (determineTrainingKind(training)) {
		case INTEGRATIVE:
			if (cPart <= 10 && (cTrain > 1 || cCotr > 2) || (cPart > 10 && (cTrain > 1 || cCotr > 3))) {
				return "!!";
			}
			break;
		case CHILDREN_YOUTH:
			if (cPart <= 20 && (cTrain > 1 || cCotr > 1) || (cPart > 20 && (cTrain > 1 || cCotr > 2))) {
				return "!!";
			}
			break;
		case ADULTS:
			if (cPart <= 15 && (cTrain > 1 || cCotr > 0) || (cPart > 15 && (cTrain > 1 || cCotr > 1))) {
				return "!!";
			}
		}
		return "OK";
	}

	enum TrainingKind {
		ADULTS, CHILDREN_YOUTH, INTEGRATIVE
	};

	private int countTrainersInRole(final String roleName, final Training training) {
		int c = 0;
		for (final TrainingHeldByTrainer trhbt : training.getHeldbytrainers()) {
			if (trhbt.getRole().getName().equals(roleName)) {
				c++;
			}
		}
		return c;
	}

	private TrainingKind determineTrainingKind(final Training training) {
		if (training.getName().contains("Integrativ")) {
			return TrainingKind.INTEGRATIVE;
		} else if (training.getName().contains("Kind") || training.getName().contains("Jugend")) {
			return TrainingKind.CHILDREN_YOUTH;
		} else {
			return TrainingKind.ADULTS;
		}
	}

	private int countPaticipiants(final Training training) {
		Integer participiantscount;
		try {
			participiantscount = training.getPartipiciantscount();
		} catch (PropValueNullException e) {
			participiantscount = null;
		}
		return participiantscount == null ? 0 : participiantscount;
	}

	private String countPaticipiantsAsString(final Training training) {
		Integer participiantscount;
		try {
			participiantscount = training.getPartipiciantscount();
		} catch (PropValueNullException e) {
			participiantscount = null;
		}
		String sParticipiantscount = participiantscount == null ? "k.A." : participiantscount.toString();
		return sParticipiantscount;
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
