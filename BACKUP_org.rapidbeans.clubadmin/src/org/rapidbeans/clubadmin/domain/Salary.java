/*
 * RapidBeans Application RapidClubAdmin: Salary
 *
 * Copyright Martin Bluemel, 2008
 *
 * 12.12.2008
 */

package org.rapidbeans.clubadmin.domain;



import org.rapidbeans.core.type.TypeRapidBean;


/**
 * Rapid Bean class: Salary.
 * Partially generated Java class
 * !!!Do only edit manually in marked sections!!!
 **/
public class Salary extends RapidBeanBaseSalary {

    public boolean trainerAttributesMatch(final Trainer trainer) {
        // if the Salary doesn't require attributes, match := true
        if (this.getTrainerattribute() == null || this.getTrainerattribute().size() == 0) {
            return true;
        }
        // if the Salary requires at least one attribute and the Trainer
        // has not got any: match := false
        if (trainer.getTrainerattributes() == null || trainer.getTrainerattributes().size() == 0) {
            return false;
        }
        // if the Salary requires at least one attribute and the Trainer
        // has got at least one: check match
        for (TrainerAttribute trAtt : this.getTrainerattribute()) {
            if (!trainer.getTrainerattributes().contains(trAtt)) {
                return false;
            }
        }
        return true;
    }

    /**
     * default constructor.
     */
    public Salary() {
        super();
        init();
    }

    /**
     * constructor out of a string.
     * @param s the string
     */
    public Salary(final String s) {
        super(s);
        init();
    }

    /**
     * constructor out of a string array.
     * @param sa the string array
     */
    public Salary(final String[] sa) {
        super(sa);
        init();
    }

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(Salary.class);

    /**
     * @return the RapidBean type
     */
    public TypeRapidBean getType() {
        return type;
    }
}
