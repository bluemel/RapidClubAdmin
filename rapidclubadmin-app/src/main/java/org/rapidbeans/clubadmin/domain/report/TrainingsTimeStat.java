package org.rapidbeans.clubadmin.domain.report;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.TrainerRole;
import org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.core.util.StringHelper.FillMode;
import org.rapidbeans.domain.math.UnitTime;

public class TrainingsTimeStat {

	public SortedSet<Department> getDepartments() {
		return departments;
	}

	public SortedSet<Trainer> getTrainers() {
		return trainers;
	}

	private final StringBuilder sb = new StringBuilder();

	private final SortedSet<Department> departments = new TreeSet<>();
	private final SortedSet<Trainer> trainers = new TreeSet<>();
	private final SortedSet<TrainerRole> trainerRoles = new TreeSet<>();
	private final Map<TrainerDepRole, TrainingsTimeStatForTrainer> statMap = new HashMap<>();

	protected StringBuilder getSb() {
		return sb;
	}

	public String getReport() {
		return sb.toString();
	}

	public void add(Department department, TrainingHeldByTrainer trhbt) {
		TrainerDepRole trDepRole = new TrainerDepRole(trhbt.getTrainer(), department, trhbt.getRole());
		TrainingsTimeStatForTrainer stat = this.statMap.get(trDepRole);
		if (stat == null) {
			stat = new TrainingsTimeStatForTrainer(trDepRole);
			this.departments.add(department);
			this.trainers.add(trhbt.getTrainer());
			this.trainerRoles.add(trhbt.getRole());
			this.statMap.put(trDepRole, stat);
		}
		if (trhbt.getSalary() != null && trhbt.getSalary().getTime().getMagnitudeLong() > 0) {
			stat.addTime(trhbt.getSalary().getTime());
			stat.addMoney(trhbt.getMoneyEarned());
		}
	}

	public String report() {
		final StringBuilder sb = new StringBuilder();
		sb.append("============================== stat report START ==============================\n");
		for (final Department department : this.departments) {
			boolean departmentPrinted = false;
			for (final Trainer trainer : this.trainers) {
				boolean trainerPrinted = false;
				for (final TrainerRole trainerRole : this.trainerRoles) {
					final TrainingsTimeStatForTrainer stat = this.statMap
							.get(new TrainerDepRole(trainer, department, trainerRole));
					if (stat != null && stat.getTime().getMagnitudeLong() > 0) {
						if (!departmentPrinted) {
							sb.append(String.format("Department %s\n", department.getName()));
							departmentPrinted = true;
						}
						if (!trainerPrinted) {
							sb.append(String.format("  Trainer %s, %s\n", trainer.getLastname(), trainer.getFirstname()));
							trainerPrinted = true;
						}
						long hours = stat.getTime().getMagnitudeLong() / 60;
						long minutes = stat.getTime().getMagnitudeLong() - (hours * 60);
						sb.append(String.format("    Role %s: time: %s:%s / %s, money: %s\n", trainerRole.getName(),
								hours, minutes, stat.getTime().convert(UnitTime.h), stat.getMoney()));
					}
				}
			}
		}
		sb.append("============================== stat report END ==============================\n");
		return sb.toString();
	}

	public String reportCsv() {
		final StringBuilder sb = new StringBuilder();
		sb.append("============================== stat CSV report START ==============================\n");
		sb.append("Trainer;Abteilung;Rolle;Industriezeit [h];Zeit [hh:mm];Verdienst\n");
		for (final Department department : this.departments) {
			for (final Trainer trainer : this.trainers) {
				for (final TrainerRole trainerRole : this.trainerRoles) {
					final TrainingsTimeStatForTrainer stat = this.statMap
							.get(new TrainerDepRole(trainer, department, trainerRole));
					if (stat != null && stat.getTime().getMagnitudeLong() > 0) {
						long hours = stat.getTime().getMagnitudeLong() / 60;
						long minutes = stat.getTime().getMagnitudeLong() - (hours * 60);
						sb.append(String.format("%s, %s;%s;%s;%s;%s:%s;%s\n",
								trainer.getLastname(), trainer.getFirstname(), department.getName(), trainerRole.getName(),
								stat.getTime().convert(UnitTime.h).getMagnitude().toString().replace('.', ','),
								hours,
								StringHelper.fillUp(Long.toString(minutes), 2, '0', FillMode.left),
								stat.getMoney().getMagnitude() + " €"));
					}
				}
			}
		}
		sb.append("============================== stat CSV report END ==============================\n");
		return sb.toString();
	}
}
