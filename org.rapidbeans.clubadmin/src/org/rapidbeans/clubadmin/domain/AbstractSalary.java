/*
 * RapidBeans Application RapidClubAdmin: AbstractSalary
 *
 * Copyright Martin Bluemel, 2008
 *
 * 12.12.2008
 */

package org.rapidbeans.clubadmin.domain;


import java.math.BigDecimal;
import java.util.Collection;

import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.exception.PropNotInitializedException;
import org.rapidbeans.domain.finance.Currency;
import org.rapidbeans.domain.finance.Money;
import org.rapidbeans.domain.math.Time;
import org.rapidbeans.domain.math.UnitTime;
import org.rapidbeans.presentation.ApplicationManager;


/**
 * Operations for the parent class of all types of salary.
 *
 * @author Martin Bluemel
 */
public abstract class AbstractSalary extends RapidBeanBaseAbstractSalary {

    /**
     * initialize the salary's time according to the MasterData's trainerhour
     * if accessible.
     */
    protected void init() {
        final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();
        if (client != null && (!client.isInitializingMasterData())) {
            this.setTime(client.getMasterData().getTrainerhour());
        }
    }

    /**
     * The key property "rolename" is hereby bound
     * to the name of the parent bean.
     * overrides RapidBean.setParentBean().
     *
     * @param parent the parent bean
     */
    public void setParentBean(final RapidBean parent) {
        super.setParentBean(parent);
        this.clearId();
        if (parent != null) {
            this.setRolename(((TrainerRole) parent).getName());
        }
    }

    /**
     * compare two salaries.
     * normalize to money / hour.
     *
     * @return 0, -1, or 1 if less than, equal, greater than
     */
    public int compareValues(final AbstractSalary oSalary) {
        if ((this.getMoney() == null || this.getMoney().getMagnitude().equals(BigDecimal.ZERO))
                && ((oSalary.getMoney() == null) || oSalary.getMoney().getMagnitude().equals(BigDecimal.ZERO))) {
            return 0;
        } else if (this.getMoney() == null || this.getMoney().getMagnitude().equals(BigDecimal.ZERO)) {
            return -1;
        } else if (oSalary.getMoney() == null || oSalary.getMoney().getMagnitude().equals(BigDecimal.ZERO)) {
            return 1;
        } else {
            try {
                final BigDecimal euroPerHour = this.getMoney().convert(Currency.euro).getMagnitude()
                    .divide(this.getTime().convert(UnitTime.h).getMagnitude());
                final BigDecimal oEuroPerHour = oSalary.getMoney().convert(Currency.euro).getMagnitude()
                    .divide(oSalary.getTime().convert(UnitTime.h).getMagnitude());
                return euroPerHour.compareTo(oEuroPerHour);
            } catch (ArithmeticException e) {
                final Double euroPerHour = this.getMoney().convert(Currency.euro).getMagnitude().doubleValue()
                    / (this.getTime().convert(UnitTime.h).getMagnitude().doubleValue());
                final Double oEuroPerHour = oSalary.getMoney().convert(Currency.euro).getMagnitude().doubleValue()
                    / (oSalary.getTime().convert(UnitTime.h).getMagnitude().doubleValue());
                return euroPerHour.compareTo(oEuroPerHour);
            }
        }
    }

    /**
     * Update the money according to the components
     */
    public void updateMoneyFromComponents() {
        try {
        final Collection<SalaryComponent> comps = this.getComponents();
        if (comps != null && comps.size() > 0) {
            final Money moneyOld = this.getMoney();
            Currency currency = null;
            Time time = this.getTime();
            if (moneyOld != null) {
                currency = (Currency) moneyOld.getUnit();
            }
            BigDecimal sum = BigDecimal.ZERO;
            for (SalaryComponent comp : comps) {
                if (currency == null) {
                    currency = (Currency) comp.getMoney().getUnit();
                } else {
                    if ((comp.getMoney() != null) &&
                            (!(currency == comp.getMoney().getUnit()))) {
                        throw new RapidClubAdminBusinessLogicException(
                                "salary.component.money.unit.diff",
                                "Different salary component currency \""
                                + comp.getMoney().getUnit().toString() + "\"");
                    }
                }
                if (time == null) {
                    time = comp.getTime();
                } else {
                    if (!(time.equals(comp.getTime()))) {
                        throw new RapidClubAdminBusinessLogicException(
                                "salary.component.time.diff",
                                "Different salary component time \""
                                + comp.getTime().toString() + "\"");
                    }
                }
                if (comp.getMoney() != null && comp.getMoney().getMagnitude() != null) {
                    sum = sum.add(comp.getMoney().getMagnitude());
                }
            }
            if (this.getMoney() == null ||
                    (!(this.getMoney().getMagnitude().equals(sum)))) {
                this.setMoney(new Money(sum, currency));
            }
            if (this.getTime() == null) {
                this.setTime(time);
            }
        }
        } catch (PropNotInitializedException e) {
            // do nothing
        }
    }

    /**
     * Retrieve the component for a given SalaryComponentType.
     *
     * @param scType the type
     *
     * @return the component or null if not found.
     */
    public SalaryComponent getComponent(final SalaryComponentType scType) {
        for (SalaryComponent comp : this.getComponents()) {
            if (comp.getSalaryComponentType().equals(scType)) {
                return comp;
            }
        }
        return null;
    }

    /**
     * default constructor.
     */
    public AbstractSalary() {
        super();

    }

    /**
     * constructor out of a string.
     * @param s the string
     */
    public AbstractSalary(final String s) {
        super(s);

    }

    /**
     * constructor out of a string array.
     * @param sa the string array
     */
    public AbstractSalary(final String[] sa) {
        super(sa);
    }
}
