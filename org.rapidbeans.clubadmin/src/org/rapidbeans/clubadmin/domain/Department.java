/*
 * RapidBeans Application RapidClubAdmin: Department
 *
 * Copyright Martin Bluemel, 2008
 *
 * 13.12.2008
 */

package org.rapidbeans.clubadmin.domain;


import org.rapidbeans.core.type.TypeRapidBean;


/**
 * Rapid Bean class: Department.
 * 
 * @author Martin Bluemel
 */
public class Department extends RapidBeanBaseDepartment {

    /**
     * Test if the given trainer is associated to this department.
     *
     * @param trainer the trainer to test
     *
     * @return if the given trainer is associated to this department
     */
    public boolean hasTrainer(final Trainer trainer) {
        boolean hasTrainer = false;
        for (Trainer tr : this.getTrainers()) {
            if (tr.equals(trainer)) {
                hasTrainer = true;
                break;
            }
        }
        return hasTrainer;
    }

    /**
     * remove this trainer from all trainer plannings and trainings
     * @param trainer
     */
    public void removeTrainerFromAllTrainerplannings(
            final Trainer trainer) {
        if (this.getTrainingdates() != null) {
            for (TrainingDate trdate : this.getTrainingdates()) {
                if (trdate.getTrainerplannings() != null) {
                    for (TrainerPlanning trplan : trdate.getTrainerplannings()) {
                        if (trplan.getDefaulttrainers() != null
                                && trplan.getDefaulttrainers().contains(trainer)) {
                            trplan.removeDefaulttrainer(trainer);
                        }
                    }
                }
                if (trdate.getTrainings() != null) {
                    for (TrainingRegular training : trdate.getTrainings()) {
                        if (training.getHeldbytrainers() != null) {
                            for (TrainingHeldByTrainer trhbt : training.getHeldbytrainers()) {
                                if (trhbt.getTrainer().equals(trainer)) {
                                    training.removeHeldbytrainer(trhbt);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @return this Departement's default password or the parent club one's.
     */
    public String getDefaultpassword() {
        if (super.getDefaultpassword() != null) {
            return super.getDefaultpassword();
        } else {
            return ((Club) this.getParentBean()).getDefaultpassword();
        }
    }

    /**
     * default constructor.
     */
    public Department() {
        super();
    }

    /**
     * constructor out of a string.
     * @param s the string
     */
    public Department(final String s) {
        super(s);
    }

    /**
     * constructor out of a string array.
     * @param sa the string array
     */
    public Department(final String[] sa) {
        super(sa);
    }

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(Department.class);

    /**
     * @return the bean's type
     */
    public TypeRapidBean getType() {
        return type;
    }
}
