/*
 * Rapid Beans Framework: TrainingHeldByTrainerTest.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * Jan 6, 2007
 */
package org.rapidbeans.clubadmin.domain;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.domain.math.TimeOfDay;

/**
 * Unit test for domain class TrainingHeldByTrainer.
 *
 * @author Martin Bluemel
 */
public class TrainingHeldByTrainerTest {

	/**
	 * Test method for
	 * {@link org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer#TrainingHeldByTrainer()}.
	 */
	@Test
	public void testTrainingHeldByTrainer() {
		TrainingHeldByTrainer tr = new TrainingHeldByTrainer();
		assertNull(tr.getRole());
		assertNull(tr.getTrainer());
	}

	/**
	 * Test method for setStrainer. Prooves that you can assing two different
	 * trainer to one and the same training.
	 */
	@Test
	public void testSetTrainerTwoDifferent() {
		TrainerRole rTrainer = new TrainerRole("\"trainer\"");

		// set up a training 2007/01/01 monday 9:00 at location "Sportshall"
		TrainingRegular training = createTraining(rTrainer, "20070101", "monday", "09:00", "10:30", "Sportshall");

		Trainer michael = new Trainer("\"Meyer\" \"Michael\"");

		TrainingHeldByTrainer heldby1 = new TrainingHeldByTrainer();
		training.addHeldbytrainer(heldby1);
		heldby1.setRole(rTrainer);
		heldby1.setTrainer(michael);

		// assinging another trainer to the same traininig is possible
		Trainer alf = new Trainer("\"Mueller\" \"Alf\"");

		TrainingHeldByTrainer heldby2 = new TrainingHeldByTrainer();
		training.addHeldbytrainer(heldby2);
		heldby2.setRole(rTrainer);
		heldby2.setTrainer(alf);
	}

	/**
	 * Prooves that you can assing one and the same trainer to two different
	 * trainings at the same date but on non overlapping time.
	 */
	@Test
	public void testSetTrainerSameToTwoDiffTrainingsSameDateNoOverlap() {
		TrainerRole rTrainer = new TrainerRole("\"trainer\"");

		// set up a first training 2007/01/01 monday 9:00 at location "Sportshall"
		TrainingRegular training = createTraining(rTrainer, "20070101", "monday", "09:00", "10:30", "Sportshall");

		// set up a second training 2007/01/01 monday 13:00 at location "Sportshall2"
		TrainingRegular training2 = createTraining(rTrainer, "20070101", "monday", "13:00", "15:00", "Sportshall2");

		Trainer michael = new Trainer("\"Meyer\" \"Michael\"");

		// assinging a trainer to training 1
		TrainingHeldByTrainer heldby1 = new TrainingHeldByTrainer();
		training.addHeldbytrainer(heldby1);
		heldby1.setRole(rTrainer);
		heldby1.setTrainer(michael);

		// assinging the same trainer to training 2 is possible because the
		// times do not overlap
		TrainingHeldByTrainer heldby2 = new TrainingHeldByTrainer();
		training2.addHeldbytrainer(heldby2);
		heldby2.setRole(rTrainer);
		heldby2.setTrainer(michael);
	}

	/**
	 * Test method for getTrainer(). Prooves that you get a validation exception if
	 * you try to assing a trainer to the same training twice.
	 */
	@Test
	public void testSetTrainerSame() {
		TrainerRole rTrainer = new TrainerRole("\"trainer\"");

		// set up a training 2007/01/01 monday 9:00 at location "Sportshall"
		TrainingRegular training = createTraining(rTrainer, "20070101", "monday", "09:00", "10:30", "Sportshall");

		Trainer michael = new Trainer("\"Meyer\" \"Michael\"");

		TrainingHeldByTrainer heldby1 = new TrainingHeldByTrainer();
		training.addHeldbytrainer(heldby1);
		heldby1.setRole(rTrainer);
		heldby1.setTrainer(michael);

		TrainingHeldByTrainer heldby2 = new TrainingHeldByTrainer();
		training.addHeldbytrainer(heldby2);
		heldby2.setRole(rTrainer);
		try {
			heldby2.setTrainer(michael);
		} catch (ValidationException e) {
			assertTrue(true);
		}
	}

	/**
	 * Test method for getTrainer(). Prooves that you get a validation exception if
	 * you try to assing a trainer to the training that is alread assigne to another
	 * training at the same date with overlapping time.
	 */
	@Test
	public void testSetTrainerAlreadyHeldAnotherTraingAtOverlappingTime() {
		TrainerRole rTrainer = new TrainerRole("\"trainer\"");

		TrainerPlanning pTrainer = new TrainerPlanning();
		pTrainer.setRole(rTrainer);

		// set up a first training 2007/01/01 monday 9:00 at location "Sportshall"
		TrainingRegular training = createTraining(rTrainer, "20070101", "monday", "09:00", "10:30", "Sportshall");

		// set up a second training 2007/01/01 monday 9:30 at location "Sportshall2"
		// time overlaps with training 1
		TrainingRegular training2 = createTraining(rTrainer, "20070101", "monday", "09:30", "11:00", "Sportshall2");

		Trainer michael = new Trainer("\"Meyer\" \"Michael\"");

		TrainingHeldByTrainer heldby1 = new TrainingHeldByTrainer();
		training.addHeldbytrainer(heldby1);
		heldby1.setRole(rTrainer);
		heldby1.setTrainer(michael);

		TrainingHeldByTrainer heldby2 = new TrainingHeldByTrainer();
		training2.addHeldbytrainer(heldby2);
		heldby2.setRole(rTrainer);
		try {
			heldby2.setTrainer(michael);
			fail("expected ValidationException");
		} catch (ValidationException e) {
			assertTrue(true);
		}
	}

	/**
	 * create a training with the given role (new triningdate, new location).
	 *
	 * @param rTrainer  the trainer role
	 * @param date      training date as String
	 * @param day       trainingdate day of week as string
	 * @param timeBegin trainingdate begin time as string
	 * @param timeEnd   trainingdate end time as string
	 * @param location  trainingdate location as string
	 *
	 * @return the training.
	 */
	private TrainingRegular createTraining(final TrainerRole rTrainer, final String date, final String day,
			final String timeBegin, final String timeEnd, final String location) {
		TrainerPlanning pTrainer = new TrainerPlanning();
		pTrainer.setRole(rTrainer);
		TrainingDate mondayMorning = new TrainingDate("\"" + day + "\" \"" + timeBegin + "\"");
		mondayMorning.setTimeend(new TimeOfDay(timeEnd));
		Location sportshall = new Location("\"" + location + "\"");
		mondayMorning.setLocation(sportshall);
		mondayMorning.addTrainerplanning(pTrainer);
		TrainingRegular training = new TrainingRegular("\"" + date + "\"");
		mondayMorning.addTraining(training);
		return training;
	}
}
