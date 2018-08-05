/*
 * EasyBiz Application RapidClubAdmin: TrainerPlanningPropDefaulttrainers.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 05.01.2007
 */
package org.rapidbeans.clubadmin.domain;

import java.util.Collection;

import org.rapidbeans.core.basic.Link;
import org.rapidbeans.core.basic.LinkFrozen;
import org.rapidbeans.core.basic.PropertyCollection;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.core.type.TypeProperty;
import org.rapidbeans.domain.math.TimeOfDay;

/**
 * extension from collection property TrainingHeldByTrainer.trainer.
 */
public class TrainingHeldByTrainerPropTrainer extends PropertyCollection {

	/**
	 * constructor.
	 * 
	 * @param type       the property type
	 * @param parentBean the parent bean
	 */
	public TrainingHeldByTrainerPropTrainer(final TypeProperty type, final RapidBean parentBean) {
		super(type, parentBean);
	}

	/**
	 * the special part of the validation.<br>
	 * Implicitly also converts the given object.
	 *
	 * @param newValue the value object to validate
	 * @param mode     ( add | remove | set )
	 *
	 * @return the converted value which is the internal representation or if a
	 *         primitive type the corresponding value object
	 */
	public Collection<Link> validate(final Object newValue, final ValidationMode mode) {
		final Collection<Link> colttrainer = (Collection<Link>) super.validate(newValue, mode);
		if (colttrainer != null) {
			final Link ltrainer = colttrainer.iterator().next();
			if (ltrainer != null && ltrainer instanceof RapidBean) {
				// business rules:
				// - same trainer within same parent Trainig must be used only once.
				// - trainer can not hold another training with overlapping time.
				final Trainer trainer = (Trainer) ltrainer;
				final Training parentTraining = (Training) this.getBean().getParentBean();
				final Collection<?> trainingsHeld = trainer.getTrainingsheld();
				if (parentTraining != null && trainingsHeld != null) {
					TrainingHeldByTrainer trainingHeld;
					for (Object link : trainingsHeld) {
						if (link instanceof LinkFrozen) {
							continue;
						}
						if (!(link instanceof TrainingHeldByTrainer)) {
							throw new RapidClubAdminBusinessLogicException("xxx",
									"unexpected class \"" + link.getClass().getName() + "\"");
						}
						trainingHeld = (TrainingHeldByTrainer) link;
						final Training training = (Training) trainingHeld.getParentBean();
						if (training != null) {
							if (trainingHeld != this.getBean()) {
								if (trainingHeld != null && training == parentTraining) {
									final Object[] args = { ltrainer, training, trainingHeld.getRole() };
									throw new ValidationException(
											"invalid.prop.trainingheldbytrainer.trainer.alreadyholds.same",
											this.getBean(),
											"invalid value \"" + ltrainer.getIdString()
													+ "\" for property \"trainer\".\n"
													+ "This trainer does already hold this training ("
													+ training.getIdString() + ").",
											args);
								} else {
									if (training.getDate().equals(parentTraining.getDate())) {
										final TimeOfDay ts1 = training.getTimestart();
										final TimeOfDay ts2 = parentTraining.getTimestart();
										final TimeOfDay te1 = training.getTimeend();
										final TimeOfDay te2 = parentTraining.getTimeend();
										if ((((ts1.compareTo(ts2) > 0) && (ts1.compareTo(te2) < 0))
												|| ((te1.compareTo(ts2) > 0) && (te1.compareTo(te2) < 0)))
												&& (training.getState() != TrainingState.closed
														&& training.getState() != TrainingState.cancelled
														&& parentTraining.getState() != TrainingState.closed
														&& parentTraining.getState() != TrainingState.cancelled)) {
											final Object[] args = { ltrainer, training };
											throw new ValidationException(
													"invalid.prop.trainingheldbytrainer.trainer.alreadyholds.other",
													this.getBean(),
													"invalid value \"" + ltrainer.getIdString()
															+ "\" for property \"trainer\".\n"
															+ "This trainer does already hold another training at"
															+ " the same date and overlapping time \""
															+ training.getIdString() + "\"",
													args);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return colttrainer;
	}

	/**
	 * intercept the set value method.
	 *
	 * @param col                                  the new value for this property
	 * @param touchInverseLinks                    if an inverse link will be added
	 *                                             or not
	 * @param checkContainerLinksToExternalObjects determines if links from an
	 *                                             object living inside the
	 *                                             container should be allowed. Be
	 *                                             very careful to set this argument
	 *                                             to false.
	 * @param validate                             turn validation on / off
	 */
	public void setValue(final Object col, final boolean touchInverseLinks,
			final boolean checkContainerLinksToExternalObjects, final boolean validate) {
		super.setValue(col, touchInverseLinks, checkContainerLinksToExternalObjects, validate);
		final TrainingHeldByTrainer trhbt = (TrainingHeldByTrainer) this.getBean();
		final Training training = (Training) trhbt.getParentBean();
		if (training != null) {
			final Collection<TrainingHeldByTrainer> trhbts = training.getHeldbytrainers();
			if (trhbts != null) {
				boolean doNotCheck = false;
				for (TrainingHeldByTrainer trhbt1 : trhbts) {
					try {
						if (trhbt1.getRole() == null) {
							doNotCheck = true;
							break;
						}
//                        if (trhbt1.getTrainer() == null) {
//                            doNotCheck = true;
//                            break;
//                        }
					} catch (RapidBeansRuntimeException e) {
						doNotCheck = true;
						break;
					}
				}
				if (!doNotCheck) {
					training.correctTrainingState();
				}
			}
		}
	}
}
