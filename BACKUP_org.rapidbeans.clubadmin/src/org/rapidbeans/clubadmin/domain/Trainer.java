/*
 * RapidBeans Application RapidClubAdmin: Trainer
 *
 * Copyright Martin Bluemel, 2008
 *
 * 13.12.2008
 */

package org.rapidbeans.clubadmin.domain;


import java.util.HashMap;

import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.domain.finance.Money;


/**
 * Rapid Bean class: Trainer.
 * 
 * @author Martin Bluemel
 */
public class Trainer extends RapidBeanBaseTrainer {

    private static final Money ZERO_MONEY = new Money("0 euro");

    /**
     * The cache for fast salary hashing.
     */
    private HashMap<TrainerRole, PersonalSalary> personalSalaries = null;

    /**
     * Return the personal salary of this trainer for the given role.
     *
     * @param the given role.
     *
     * @return personal salary of this trainer for the given role.
     */
    private PersonalSalary getPersonalSalary(final TrainerRole role) {
        if (this.personalSalaries == null && this.getSalaries() != null) {
            this.personalSalaries = new HashMap<TrainerRole, PersonalSalary>();
            for (PersonalSalary sal : this.getSalaries()) {
                this.personalSalaries.put(
                        (TrainerRole) sal.getParentBean(), sal);
            }
        }
        if (this.personalSalaries != null) {
            return this.personalSalaries.get(role);
        }
        return null;
    }

    /**
     * Determine a Salary a Trainer gets because of<br/>
     * 1) a personal salary<br/>
     * 2) his / her trainer attributes.<br/>
     *
     * @param role the role to investigate
     *
     * @return the personal salary for this role salary of the best
     *         paid trainer attribute combination
     */
    public AbstractSalary getDefaultSalaryForRole(final TrainerRole role) {
        if (role == null) {
            throw new IllegalArgumentException("null role given");
        }
        if (getPersonalSalary(role) != null) {
            return getPersonalSalary(role);
        }
        Salary salMax = new Salary();
        salMax.setMoney(ZERO_MONEY);
        salMax.setTime(((TrainingsList) this.getParentBean()).getTrainerhour());
        if (role.getSalarys() != null) {
            for (Salary sal : role.getSalarys()) {
                if (sal.trainerAttributesMatch(this)) {
                    if (salMax.getMoney() == ZERO_MONEY || (sal != null && sal.compareValues(salMax) > 0)) {
                        salMax = sal;
                    }                
                }
            }
        }
        return salMax;
    }

    /**
     * default constructor.
     */
    public Trainer() {
        super();
    }

    /**
     * constructor out of a string.
     *
     * @param s the string
     */
    public Trainer(final String s) {
        super(s);
    }

    /**
     * constructor out of a string array.
     *
     * @param sa the string array
     */
    public Trainer(final String[] sa) {
        super(sa);
    }

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(Trainer.class);

    /**
     * @return the bean's type
     */
    public TypeRapidBean getType() {
        return type;
    }
}
