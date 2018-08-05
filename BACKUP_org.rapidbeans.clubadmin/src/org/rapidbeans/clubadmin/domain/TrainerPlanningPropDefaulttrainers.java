/*
 * EasyBiz Application RapidClubAdmin: TrainerPlanningPropDefaulttrainers.java
 *
 * Copyright Martin Bluemel, 2009
 *
 * 26.10.2006
 */
package org.rapidbeans.clubadmin.domain;

import java.util.Collection;

import org.rapidbeans.core.basic.Link;
import org.rapidbeans.core.basic.LinkFrozen;
import org.rapidbeans.core.basic.PropertyCollection;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.basic.ThreadLocalValidationSettings;
import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.core.type.TypeProperty;


/**
 * extension from collection property TrainerPlanning.defaulttrainer.
 */
public class TrainerPlanningPropDefaulttrainers extends PropertyCollection {

    /**
     * constructor.
     * @param type the property type
     * @param parentBean the parent bean
     */
    public TrainerPlanningPropDefaulttrainers(final TypeProperty type, final RapidBean parentBean) {
        super(type, parentBean);
    }

    /**
     * Validate the trainer to be assigned as default trainer
     * for this trainer planning.<br>
     *
     * @param newValue the value object to validate
     *
     * @return the converted value which is the internal representation or if a
     *         primitive type the corresponding value object
     */
    @SuppressWarnings("unchecked")
    public Collection<Link> validate(final Object newValue) {

        // general validation includes conversion
        final Collection<Link> collection =
            (Collection<Link>) super.validate(newValue);
        if (!ThreadLocalValidationSettings.getValidation()) {
            return collection;
        }
        if (collection == null) {
            return null;
        }

        // special validation
        if (collection != null) {
            for (Link ldefaulttrainer : collection) {
                if (ldefaulttrainer != null && ldefaulttrainer instanceof RapidBean) {
                    final Trainer defaulttrainer = (Trainer) ldefaulttrainer;
                    final TrainingDate parentDate = (TrainingDate) this.getBean().getParentBean();
                    if (parentDate == null) {
                        continue;
                    }
                    final Department department = (Department) parentDate.getParentBean();

                    // business rules:
                    // - default trainer must be permitted for department
                    if ((defaulttrainer.getDepartments() == null)
                            || (!(defaulttrainer.getDepartments().contains(department)))) {
                        final Object[] args1 =  {ldefaulttrainer, department};
                        throw new ValidationException(
                                "invalid.prop.trainerplanning.defaulttrainer.notauthorized.department",
                                this.getBean(),
                                "invalid value \"" + ldefaulttrainer.getIdString()
                                + "\" for property \"defaultrainer\".\n"
                                + "This trainer is not authorized for department ("
                                + department.getIdString() + ").", args1);
                    }
                    // - same default trainer within same parent TrainigDate must be used only once.
                    // - default trainer must not be planned within another training date with overlapping time.
                    final Collection<?> plannings =
                        defaulttrainer.getTrainerplannings();
                    if (plannings != null) {
                        TrainerPlanning trplan;
                        for (Object oTrplan : plannings) {
                            if (oTrplan instanceof LinkFrozen) {
                                continue;
                            }
                            if (!(oTrplan instanceof TrainerPlanning)) {
                            	throw new RapidClubAdminBusinessLogicException("xxx", "unexpected class \""
                            			 + oTrplan.getClass().getName() + "\"");
                            }
                            trplan = (TrainerPlanning) oTrplan;
                            final TrainingDate trplanDate = (TrainingDate) trplan.getParentBean();
                            if (trplan != this.getBean()) {
                                if (trplanDate == parentDate) {
                                    final Object[] args2 =  {ldefaulttrainer, parentDate};
                                    throw new ValidationException(
                                            "invalid.prop.trainerplanning.defaulttrainer.alreadyplanned.same",
                                            this.getBean(),
                                            "invalid value \"" + ldefaulttrainer.getIdString()
                                            + "\" for property \"defaultrainer\".\n"
                                            + "This trainer is already planned for the same date ("
                                            + trplanDate.getIdString() + ").", args2);
                                } else {
                                    if (trplanDate.getDayofweek() == parentDate.getDayofweek()) {
                                        if (((trplanDate.getTimestart().compareTo(parentDate.getTimestart()) > 0)
                                                && (trplanDate.getTimestart().compareTo(parentDate.getTimeend()) < 0))
                                                || (trplanDate.getTimeend().compareTo(parentDate.getTimestart()) > 0)
                                                && (trplanDate.getTimeend().compareTo(parentDate.getTimeend()) < 0)) {
                                            final Object[] args3 = {ldefaulttrainer,
                                                    trplanDate, trplanDate.getLocation()};
                                            throw new ValidationException(
                                                    "invalid.prop.trainerplanning.defaulttrainer.alreadyplanned.other",
                                                    this.getBean(),
                                                    "invalid value \"" + ldefaulttrainer.getIdString()
                                                    + "\" for property \"defaultrainer\".\n"
                                                    + "This trainer is already planned for another date \""
                                                    + trplanDate.getIdString() + "\" partially"
                                                    + " happening on the same time at location \""
                                                    + trplanDate.getLocation().getIdString() + "\"", args3);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return collection;
    }
}
