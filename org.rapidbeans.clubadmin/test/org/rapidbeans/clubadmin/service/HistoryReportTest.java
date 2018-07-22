package org.rapidbeans.clubadmin.service;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Ignore;
import org.junit.Test;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Training;
import org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.core.util.StringHelper.FillMode;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.domain.finance.Money;

public class HistoryReportTest {

    @Test
    @Ignore
    public void test() {
        TypePropertyCollection.setDefaultCharSeparator(',');
        final File histdir = new File("history");
        assertTrue(histdir.exists());
        final List<Trstat> trainings = new ArrayList<Trstat>();
        // final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        final NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        final List<Integer> years = readTrainingsFromHistory(histdir, trainings, 2009, 2018);

        final Map<Integer, Map<Department, Money>> statDepOverYears = new HashMap<Integer, Map<Department, Money>>();
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
            final Department dep = training.getParentBean() instanceof Department ? (Department) training
                    .getParentBean() : (Department) training.getParentBean().getParentBean();
            Money depYearMoney = depMap.get(dep);
            if (depYearMoney == null) {
                depYearMoney = new Money("0 euro");
            }
            // System.out.println(String.format("%s: %s %s",
            // df.format(training.getDate()), department.getName(),
            // training.getTimeWorked(UnitTime.h)));
            for (TrainingHeldByTrainer trhbt : training.getHeldbytrainersSortedByValue()) {
                depYearMoney = (Money) depYearMoney.add(trhbt.getMoneyEarned());
                // System.out.println(String.format("  (%s) %s: %s EUR",
                // trhbt.getRole(), trhbt.getTrainer(),
                // nf.format(trhbt.getMoneyEarned().getMagnitudeDouble())));
            }
            depMap.put(dep, depYearMoney);
        }

        for (final int year : years) {
            System.out.println();
            System.out.println(String.format("--- %s --------------------", year));
            Money sumYears = new Money("0 euro");
            Money sumYearsWoGrundschule = new Money("0 euro");
            Map<Department, Money> depMap = statDepOverYears.get(year);
            for (final Entry<Department, Money> entry : depMap.entrySet()) {
                System.out.println(String.format("%s %s EUR",
                        StringHelper.fillUp(entry.getKey().getName() + ':', 14, ' ', FillMode.right),
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
            System.out
                    .println(String.format("Summe Jahr     %s EUR", StringHelper.fillUp(
                            nf.format(sumYearsWoGrundschule.getMagnitudeDouble()), 10, ' ', FillMode.left)));
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
