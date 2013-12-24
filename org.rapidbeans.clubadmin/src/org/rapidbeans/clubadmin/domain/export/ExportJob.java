/*
 * RapidBeans Application RapidClubAdmin: ExportJob.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 08.04.2008
 */
package org.rapidbeans.clubadmin.domain.export;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.rapidbeans.clubadmin.domain.AbstractSalary;
import org.rapidbeans.clubadmin.domain.BillingPeriod;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.SalaryComponent;
import org.rapidbeans.clubadmin.domain.SalaryComponentType;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.Training;
import org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer;
import org.rapidbeans.clubadmin.domain.TrainingRegular;
import org.rapidbeans.clubadmin.domain.TrainingState;
import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.clubadmin.domain.report.Overview;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.domain.finance.Currency;
import org.rapidbeans.domain.finance.Money;
import org.rapidbeans.domain.math.Time;
import org.rapidbeans.domain.math.UnitTime;

/**
 * DB export business logic.
 * List of entries to be exported to the DB.
 *
 * @author Martin Bluemel
 */
public final class ExportJob {

    private static final Logger log = Logger.getLogger(
            ExportJob.class.getName()); 

    private Map<String, ExportJobResultEntry> result = new HashMap<String, ExportJobResultEntry>();

    public void addResultEntry(final ExportJobResultEntry entry) {
        final String key = entry.getEntityType() + "::" + entry.getEntityId();
        if (this.result.get(key) != null) {
            throw new RapidBeansRuntimeException("Export entry \""
                    + key + "\" is already added");
        }
        this.result.put(key, entry);
    }

    private BillingPeriod billingPeriod = null;

    public List<ExportJobResultEntry> getResultEntries(
            final ExportJobResultEntryModificationType modificationType,
            final String entityType) {
        final List<ExportJobResultEntry> resultEntries = new ArrayList<ExportJobResultEntry>();
        for (ExportJobResultEntry entry : this.result.values()) {
            if (entry.getModificationType() == modificationType
                    && entry.getEntityType().equals(entityType)) {
                    resultEntries.add(entry);
            }
        }
        return resultEntries;
    }

    /**
     * @return the billingPeriod's billingDate
     */
    public Date getDateFirstExport() {
        return this.getBillingPeriod().getDateExportFirst();
    }

    /**
     * @return the billingPeriod's billingDate as german localized string
     */
    public String getDateFirstExportString() {
        final DateFormat df = DateFormat.getDateInstance(
                DateFormat.MEDIUM, Locale.GERMAN);
        return df.format(this.getBillingPeriod().getDateExportFirst());
    }

    private List<Trainer> trainers = new ArrayList<Trainer>();

    private Map<String, List<ExportJobEntry>> exportEntryMap = new HashMap<String, List<ExportJobEntry>>();

    /**
     * Add a trainer with its export entries
     *
     * @param trainer trainer
     * @param entries the list of export entries
     */
    private void addExportEntries(final Trainer trainer,
            final List<ExportJobEntry> entries) {
        if (this.exportEntryMap.get(trainer.getIdString()) != null) {
            throw new RapidBeansRuntimeException("trainer already added");
        }
        this.exportEntryMap.put(trainer.getIdString(), entries);
        this.trainers.add(trainer);
    }

    /**
     * @return a set with all trainer IDs
     */
    public List<Trainer> getTrainers() {
        return this.trainers;
    }

    /**
     * @param trainerId the trainer id
     *
     * @return the list of export entries for the specified trainer
     */
    public List<ExportJobEntry> getExportEntries(final String trainerId) {
        return this.exportEntryMap.get(trainerId);
    }

    /**
     * @param trainer the trainer
     *
     * @return the list of export entries for the specified trainer
     */
    public List<ExportJobEntry> getExportEntries(final Trainer trainer) {
        return this.exportEntryMap.get(trainer.getIdString());
    }

    /**
     * Compute the Money earned for one given trainer.
     *
     * @param trainer the trainer
     *
     * @return the money earned
     */
    public Money getOverallEarnedMoney(final Trainer trainer) {
        Money sum = new Money(BigDecimal.ZERO, Currency.euro);
        for (ExportJobEntry entry : this.getExportEntries(trainer)) {
            sum = (Money) sum.add(entry.getEarnedMoney());
        }
        return sum;
    }

    /**
     * determine export job for
     * a given trainer and a given set of departements.
     *
     * @param period the BillingPeriod of this ExportJob
     * @param departments the Departments
     */
    public static ExportJob generateExportJob(
            final BillingPeriod period,
            final Department[] departments) {
        final Collection<Trainer> trainers = new TreeSet<Trainer>();
        for (Department dep : departments) {
            for (Trainer tr : dep.getTrainers()) {
                if (tr.getTrainingsheld() != null
                        && tr.getTrainingsheld().size() > 0) {
                    trainers.add(tr);
                }
            }
        }

        ExportJob job = new ExportJob(period);
        for (Trainer trainer : trainers) {
            final List<ExportJobEntry> exportEntries = determineExportEntries(departments, trainer);
            job.addExportEntries(trainer, exportEntries);
        }
        return job;
    }

    /**
     * Determine export entries for
     * a given trainer and a given set of departments.
     *
     * @param departments the Departments
     * @param trainer the trainer
     */
    final static List<ExportJobEntry> determineExportEntries(
            final Department[] departments,
            final Trainer trainer) {
        final List<TrainingHeldByTrainer> allTrhbts = new ArrayList<TrainingHeldByTrainer>();

        for (Department dep : departments) {
            final List<TrainingHeldByTrainer> trhbts =
                Overview.findTrainigsHeld(trainer, dep);
            for (TrainingHeldByTrainer trhbt : trhbts) {
                allTrhbts.add(trhbt);
            }
        }
        sort(allTrhbts);

        return determineExportEntries(trainer, allTrhbts);
    }

    /**
     * Generate single sheets.
     *
     * @param department the department
     * @param trainer the Trainer
     * @param allTrhbts all the Trainings held by the Trainer
     */
    private static List<ExportJobEntry> determineExportEntries(
            final Trainer trainer,
            final List<TrainingHeldByTrainer> allTrhbts) {
        log.finer("trainer = \"" + trainer.getIdString() + "\"");
        final List<ExportJobEntry> exportEntries = new ArrayList<ExportJobEntry>();
        final TrainingsList trList = (TrainingsList) trainer.getParentBean();
        final Time trainerHour = trList.getTrainerhour();
        final Map<SalaryComponentType, Money> moneyMap = new HashMap<SalaryComponentType, Money>();
        final Map<SalaryComponentType, AbstractSalary> salaryMap = new HashMap<SalaryComponentType, AbstractSalary>();
        Time sumTrainerHours = new Time("0 h");
        Money sumMoneyEarned = new Money("0 euro");
        for (RapidBean bean : allTrhbts) {
            final TrainingHeldByTrainer trhbt = (TrainingHeldByTrainer) bean;
            final TrainingRegular training = (TrainingRegular) trhbt.getParentBean();
            if (training.getState() != TrainingState.checked) {
                continue;
            }
            final Time hoursWorked = training.getTimeWorked(UnitTime.h);
            final Time trainerhoursWorked = (Time) hoursWorked.divide(trainerHour);
            final Money moneyEarned = trhbt.getMoneyEarned();
            log.finer("money earned: " + moneyEarned);
            sumMoneyEarned = (Money) sumMoneyEarned.add(moneyEarned);
            final AbstractSalary sal = trhbt.getSalary();
            if (sal != null && sal.getComponents() != null && sal.getComponents().size() > 0) {
                for (SalaryComponent sc : sal.getComponents()) {
                    log.finer("sc[" + sc.getSalaryComponentType().getName()
                            + "].getMoney = " + sc.getMoney());
                    log.finer("sal.getMoney = " + sal.getMoney());
                    final double factor = sc.getMoney().getMagnitude().doubleValue() /
                        sal.getMoney().getMagnitude().doubleValue();
                    log.finer("factor = " + factor);
                    final Money compMoneyEarned = new Money(
                            new BigDecimal(moneyEarned.getMagnitudeDouble() * factor),
                            (Currency) moneyEarned.getUnit());
                    log.finer("money earned ["
                            + sc.getSalaryComponentType().getName() + "]: "
                            + compMoneyEarned);
                    Money earned = moneyMap.get(sc.getSalaryComponentType());
                    if (earned != null) {
                        earned = (Money) earned.add(compMoneyEarned);
                    } else {
                        earned = compMoneyEarned;
                    }
                    log.finer("earned = " + earned.round(2));
                    moneyMap.put(sc.getSalaryComponentType(), earned);
                    salaryMap.put(sc.getSalaryComponentType(), sal);
                }
            }
            switch (training.getState()) {
            case checked:
                sumTrainerHours = (Time) sumTrainerHours.add(trainerhoursWorked);
                break;
            default:
                break;
            }
        }
        log.finer("                   sumTrainerHours = " + sumTrainerHours.round(2));
        log.finer("                   sumMoneyEarned = " + sumMoneyEarned.round(2));
        final int size = moneyMap.size();
        List<Entry<SalaryComponentType, Money>> entryList =
            new ArrayList<Entry<SalaryComponentType, Money>>(size);
        for (int i = 0; i < size; i++) {
            entryList.add(null);
        }
        int i = size - 1;
        for (Entry<SalaryComponentType, Money> entry : moneyMap.entrySet()) {
            entryList.set(i--, entry);
        }
        for (Entry<SalaryComponentType, Money> entry : entryList) {
            log.finer("trainer: " + trainer.getIdString()
                    + ", sctype: " + entry.getKey().getName()
                    + ", worked: " + sumTrainerHours
                    + ", earned: " + entry.getValue()
                    );
            final ExportJobEntry exportEntry = new ExportJobEntry();
            exportEntry.setTrainer(trainer);
            exportEntry.setHeldTrainerHours(sumTrainerHours);
            exportEntry.setSalaryComponentType(entry.getKey());
            exportEntry.setSalary(salaryMap.get(entry.getKey()));
            exportEntry.setEarnedMoney(entry.getValue());
            exportEntries.add(exportEntry);
        }
        return exportEntries;
    }

    final public static void sort(final List<TrainingHeldByTrainer> trhbts) {
        final int size = trhbts.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (isGreater(trhbts.get(j),
                        (TrainingHeldByTrainer) trhbts.get(i))) {
                    final TrainingHeldByTrainer trhbt = (TrainingHeldByTrainer) trhbts.get(i);
                    trhbts.set(i, trhbts.get(j));
                    trhbts.set(j, trhbt);
                }
            }
        }
    }

    /**
     * Helper for the bubble sort.
     *
     * @param tr1 the first TrainingHeldByTrainer
     * @param tr2 the second TrainingHeldByTrainer
     *
     * @return if the first is greater than the second
     */
    final static boolean isGreater(final TrainingHeldByTrainer tr1,
            final TrainingHeldByTrainer tr2) {
        boolean isGreater = false;
        final Training trn1 = (Training) tr1.getParentBean();
        final Training trn2 = (Training) tr2.getParentBean();
        switch (trn1.getDate().compareTo(trn2.getDate())) {
        case -1:
            isGreater = true;
            break;
        case 0:
            switch (trn1.getTimestart().compareTo(trn2.getTimestart())) {
            case -1:
                isGreater = true;
                break;
            default:
                break;
            }
            break;
        default:
            break;
        }
        return isGreater;
    }

    private ExportJob() {        
    }

    private ExportJob(final BillingPeriod period) {
        this.billingPeriod = period;
    }

    public BillingPeriod getBillingPeriod() {
        return billingPeriod;
    }
}
