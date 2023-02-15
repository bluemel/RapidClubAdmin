/*
 * RapidBeans Application RapidClubAdmin: TrainingHeldByTrainer
 *
 * Copyright Martin Bluemel, 2008
 *
 * 14.12.2008
 */

package org.rapidbeans.clubadmin.domain;

import java.math.BigDecimal;
import org.rapidbeans.core.basic.Link;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.domain.finance.Currency;
import org.rapidbeans.domain.finance.Money;
import org.rapidbeans.domain.math.Time;
import org.rapidbeans.domain.math.UnitTime;

/**
 * Rapid Bean class: TrainingHeldByTrainer.
 * 
 * @author Martin Bluemel
 */
public class TrainingHeldByTrainer extends RapidBeanBaseTrainingHeldByTrainer {

    /**
     * over define sorting => sort for importance.
     */
    public int compareValues(final TrainingHeldByTrainer o) {
        int compare = 0;
        final TrainingHeldByTrainer oTrhbt = (TrainingHeldByTrainer) o;
        AbstractSalary salary = null;
        AbstractSalary oSalary = null;
        try {
            salary = this.getSalary();
        } catch (RapidClubAdminBusinessLogicException e) {
            if (e.getSignature().equals("trhbt.null.role")) {
                salary = null;
            } else {
                throw e;
            }
        }
        try {
            oSalary = oTrhbt.getSalary();
        } catch (RapidClubAdminBusinessLogicException e) {
            if (e.getSignature().equals("trhbt.null.role")) {
                oSalary = null;
            } else {
                throw e;
            }
        }
        if (salary != null && oSalary != null) {
            compare = salary.compareValues(oSalary);
            if (compare == 0) {
                final Trainer trainer = this.getTrainer();
                final Trainer oTrainer = oTrhbt.getTrainer();
                if (trainer != null && oTrainer != null) {
                    compare = oTrainer.compareTo(trainer);
                }
            }
        } else if (salary != null && oSalary == null) {
            compare = 1;
        } else if (salary == null && oSalary != null) {
            compare = -1;
        } else if (salary == null && oSalary == null) {
            compare = 0;
        }
        return compare;
    }

    /**
     * compute the salary for this TrainingHeldByTrainer.
     *
     * @return the salary or null if the TrainingHeldByTrainer if not defined
     *         properly
     */
    public AbstractSalary getSalary() {
        if (this.getTrainer() != null) {
            if (this.getRole() == null) {
                throw new RapidClubAdminBusinessLogicException("trhbt.null.role",
                    "Null role for TrainingHeldByTrainer \"" + this.getIdString() + "\" for Training \""
                        + this.getParentBean().getIdString() + "\"");
            }
            return this.getTrainer().getDefaultSalaryForRole(this.getRole());
        } else {
            final Salary salary = new Salary();
            salary.setMoney(new Money("0 euro"));
            salary.setTime(new Time("1 h"));
            return salary;
        }
    }

    /**
     * Computes the money earned for this training held by this trainer.
     *
     * @return the money earned for this training
     */
    public BigDecimal getMinutesWorked() {
        final Training tr = (Training) this.getParentBean();
        final BigDecimal minutesStart = tr.getTimestart().convert(UnitTime.min).getMagnitude();
        final BigDecimal minutesEnd = tr.getTimeend().convert(UnitTime.min).getMagnitude();
        return minutesEnd.subtract(minutesStart);
    }

    /**
    * Computes the money earned for this training held by this trainer.
    *
    * @return the money earned for this training
    */
    public Money getMoneyEarned() {
        final Money noMoneyEarned = new Money("0 euro");
        final AbstractSalary salary = this.getSalary();
        if (salary == null) {
            return noMoneyEarned;
        }
        final Time time = salary.getTime();
        final BigDecimal minutesSalary = time.convert(UnitTime.min).getMagnitude();
        final Money moneySalary = salary.getMoney();
        if (moneySalary == null) {
            return noMoneyEarned;
        }
        final BigDecimal moneySalaryMag = moneySalary.getMagnitude();
        final BigDecimal minutesWorked = getMinutesWorked();
        BigDecimal factor = null;
        try {
            factor = minutesWorked.divide(minutesSalary);
        } catch (ArithmeticException e) {
            factor = new BigDecimal(minutesWorked.doubleValue() / minutesSalary.doubleValue());
        }
        final BigDecimal moneyEarnedMagnitude = moneySalaryMag.multiply(factor);
        final Money moneyEarned = new Money(moneyEarnedMagnitude, (Currency) moneySalary.getUnit());
        return moneyEarned;
    }

    public int compareTo(final Link o) {
        if (!(o instanceof TrainingHeldByTrainer)) {
            return -1;
        }
        Training thisTraining = (Training) this.getParentBean();
        TrainingHeldByTrainer trhbt = (TrainingHeldByTrainer) o;
        Training trhbtTraining = (Training) trhbt.getParentBean();
        final int c1 = thisTraining.getDate().compareTo(trhbtTraining.getDate());
        if (c1 != 0) {
            return c1;
        }
        final int c2 = thisTraining.getTimestart().compareTo(trhbtTraining.getTimestart());
        return c2;
    }

    /**
     * validate the whole bean.
     */
    public void validate() {
        super.validate();
    }

    /**
     * default constructor.
     */
    public TrainingHeldByTrainer() {
        super();
    }

    /**
     * constructor out of a string.
     *
     * @param s the string
     */
    public TrainingHeldByTrainer(final String s) {
        super(s);
    }

    /**
     * constructor out of a string array.
     *
     * @param sa the string array
     */
    public TrainingHeldByTrainer(final String[] sa) {
        super(sa);
    }

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(TrainingHeldByTrainer.class);

    /**
     * @return the bean's type
     */
    public TypeRapidBean getType() {
        return type;
    }
}
