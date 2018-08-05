/*
 * RapidBeans Application RapidClubAdmin: ExportJobEntry.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 08.04.2008
 */
package org.rapidbeans.clubadmin.domain.export;

import org.rapidbeans.clubadmin.domain.AbstractSalary;
import org.rapidbeans.clubadmin.domain.SalaryComponentType;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.domain.finance.Money;
import org.rapidbeans.domain.math.Time;


/**
 * Report business logic.
 * Overview of held trainings and trainer hours for one trainer.
 *
 * @author Martin Bluemel
 */
public final class ExportJobEntry {

//    private static final Logger log = Logger.getLogger(
//            ExportJobEntry.class.getName()); 

    private Trainer trainer = null;

    private Time heldTrainerHours = null;

    private Money earnedMoney = null;

    private AbstractSalary salary = null;

    private SalaryComponentType salaryComponentType = null;

    /**
     * @return the trainer
     */
    public Trainer getTrainer() {
        return trainer;
    }

    /**
     * @param trainer the trainer to set
     */
    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    /**
     * @return the heldTrainerHours
     */
    public Time getHeldTrainerHours() {
        return heldTrainerHours;
    }

    /**
     * @param heldTrainerHours the heldTrainerHours to set
     */
    public void setHeldTrainerHours(Time heldTrainerHours) {
        this.heldTrainerHours = heldTrainerHours;
    }

    /**
     * @return the earnedMoney
     */
    public Money getEarnedMoney() {
        return earnedMoney;
    }

    /**
     * @param earnedMoney the earnedMoney to set
     */
    public void setEarnedMoney(Money earnedMoney) {
        this.earnedMoney = earnedMoney;
    }

    /**
     * @return the salary component type
     */
    public SalaryComponentType getSalaryComponentType() {
        return salaryComponentType;
    }

    /**
     * @param sct the salary component type to set
     */
    public void setSalaryComponentType(SalaryComponentType sct) {
        this.salaryComponentType = sct;
    }

    /**
     * @return the salary
     */
    public AbstractSalary getSalary() {
        return salary;
    }

    /**
     * @param salary the salary to set
     */
    public void setSalary(AbstractSalary salary) {
        this.salary = salary;
    }
}
