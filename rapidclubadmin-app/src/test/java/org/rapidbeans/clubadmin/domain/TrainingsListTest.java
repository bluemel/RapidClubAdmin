package org.rapidbeans.clubadmin.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.rapidbeans.clubadmin.service.Umlaut;
import org.rapidbeans.core.basic.BeanSorter;
import org.rapidbeans.core.basic.IdGeneratorNumeric;
import org.rapidbeans.core.basic.IdKeyprops;
import org.rapidbeans.core.basic.IdKeypropswithparentscope;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.RapidBeanDeserializer;
import org.rapidbeans.core.type.TypeProperty;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.datasource.query.Query;
import org.rapidbeans.domain.math.DayOfWeek;
import org.rapidbeans.domain.math.TimeOfDay;

/**
 * UnitTests for TrainingsList.
 * 
 * @author Martin Bluemel
 */
public class TrainingsListTest {

	/**
	 * Date formatter.
	 */
	static final DateFormat DFDATE = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);

	@Before
	public void setUp() {
		TypePropertyCollection.setDefaultCharSeparator(',');
	}

	/**
	 * Test method for getType().
	 */
	@Test
	public void testGetType() {
		TrainingsList period = new TrainingsList();
		TypeRapidBean type = period.getType();
		assertEquals("org.rapidbeans.clubadmin.domain.TrainingsList", type.getName());
	}

	/**
	 * Test method for TrainingsList().
	 * 
	 * @throws ParseException if parsing fails
	 */
	@Test
	public void testTrainingsList() throws ParseException {
		final TrainingsList period = new TrainingsList();
		period.setFrom(DFDATE.parse("01.01.2006"));
		assertEquals(DFDATE.parse("01.01.2006"), period.getFrom());
		period.setTo(DFDATE.parse("31.03.2006"));
		assertEquals(DFDATE.parse("31.03.2006"), period.getTo());
	}

	/**
	 * Test method for TrainingsList(String).
	 * 
	 * @throws ParseException if parsing fails
	 */
	@Test
	public void testTrainingsListString() throws ParseException {
		final String s = "\"20060101\" \"20060331\"";
		final TrainingsList period = new TrainingsList(s);
		assertEquals(DFDATE.parse("01.01.2006"), period.getFrom());
		assertEquals(DFDATE.parse("31.03.2006"), period.getTo());
	}

	/**
	 * Test method for TrainingsList(String[]).
	 * 
	 * @throws ParseException if parsing fails
	 */
	@Test
	public void testTrainingsListStringArray() throws ParseException {
		final String[] sa = { "20060101", "20060331" };
		final TrainingsList period = new TrainingsList(sa);
		assertEquals(DFDATE.parse("01.01.2006"), period.getFrom());
		assertEquals(DFDATE.parse("31.03.2006"), period.getTo());
	}

	/**
	 * Test method for deserializing a document file. Links are not resolved.
	 */
	@Test
	public void testDeserializeFile() {
		IdGeneratorNumeric numericIdGenerator = new IdGeneratorNumeric();
		numericIdGenerator.setMode(IdGeneratorNumeric.GENERATION_STRATEGY_COMPACT);
		TypeRapidBean.forName("org.rapidbeans.clubadmin.domain.TrainerPlanning").setIdGenerator(numericIdGenerator);
		TypeRapidBean.forName("org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer")
				.setIdGenerator(numericIdGenerator);

		MasterData period = (MasterData) (new RapidBeanDeserializer()).loadBean(null,
				new File("src/test/resources/masterdata.xml"));
		Collection<Trainer> trainers = period.getTrainers();
		assertEquals(6, trainers.size());
		int i = 1;
		for (Trainer trainer : trainers) {
			switch (i) {
			case 0:
				assertSame(IdKeyprops.class, trainer.getId().getClass());
				assertEquals("Allesk�nner_Hans", trainer.getIdString());
				assertEquals("Allesk�nner", trainer.getLastname());
				break;
			default:
				break;
			}
			i++;
		}

		Club club = period.getClubs().iterator().next();
		Department dep = club.getDepartments().iterator().next();
		Collection<TrainingDate> trainingdates = dep.getTrainingdates();
		assertEquals(6, trainingdates.size());
		i = 1;
		for (TrainingDate date : trainingdates) {
			switch (i) {
			case 1:
				assertSame(IdKeypropswithparentscope.class, date.getId().getClass());
				assertEquals("Budo-Club Ismaning/Aikido/monday_19:00_Eurythmiesaal 1 Waldorfschule",
						date.getIdString());
				assertEquals(DayOfWeek.monday, date.getDayofweek());
				assertEquals(new TimeOfDay("19:00"), date.getTimestart());
				assertEquals(new TimeOfDay("20:30"), date.getTimeend());
				assertEquals(90, date.getTimeend().getMagnitudeLong() - date.getTimestart().getMagnitudeLong());
				assertNotNull(((TrainerPlanning) date.getTrainerplannings().iterator().next()).getDefaulttrainers());
				break;
			default:
				break;
			}
			i++;
		}
	}

	/**
	 * Test method for loading a document from a file. Links should be resolved.
	 */
	@Test
	public void testLoadDocument() {
		final char defaultCharSeparatorBefore = TypePropertyCollection.getDefaultCharSeparator();
		try {
			IdGeneratorNumeric idGenerator = new IdGeneratorNumeric();
			TypeRapidBean.forName("org.rapidbeans.clubadmin.domain.TrainerPlanning").setIdGenerator(idGenerator);
			TypeRapidBean.forName("org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer").setIdGenerator(idGenerator);
			Document doc = new Document("masterdata",
					new File("src/test/resources/trainingslist_20060101_20060331.xml"));
			TrainingsList period = (TrainingsList) doc.getRoot();
			Collection<Trainer> trainers = period.getTrainers();
			assertEquals(6, trainers.size());
			int i = 1;
			for (Trainer trainer : trainers) {
				switch (i) {
				case 1:
					assertSame(IdKeyprops.class, trainer.getId().getClass());
					assertEquals("Bl" + Umlaut.L_UUML + "mel_Martin_", trainer.getIdString());
					assertEquals("Bl" + Umlaut.L_UUML + "mel", trainer.getLastname());
					break;
				default:
					break;
				}
				i++;
			}

			Collection<RapidBean> trainingdates = doc.findBeansByType("org.rapidbeans.clubadmin.domain.TrainingDate");
			assertEquals(6, trainingdates.size());

			// Trainer defaulttrainer;
			TrainingDate date;
			for (RapidBean b : trainingdates) {
				date = (TrainingDate) b;
				if (date.getIdString().equals("Iaido/Aikido Erwachsene")) {
					assertSame(IdKeyprops.class, date.getId().getClass());
					assertEquals(DayOfWeek.monday, date.getDayofweek());
					assertEquals(new TimeOfDay("19:30"), date.getTimestart());
					assertEquals(new TimeOfDay("21:30"), date.getTimeend());
					assertEquals(120, date.getTimeend().getMagnitudeLong() - date.getTimestart().getMagnitudeLong());
					Collection<TrainerPlanning> plannedTrainers = date.getTrainerplannings();
					assertEquals(1, plannedTrainers.size());
				}
			}

			// Collection<CreditInstitute> creditinstitutes =
			// period.getCreditinstitutes();
			// assertEquals(5, creditinstitutes.size());
			// i = 1;
			// for (CreditInstitute inst : creditinstitutes) {
			// switch (i) {
			// case 4:
			// assertSame(IdKeyprops.class, inst.getId().getClass());
			// assertEquals("Hypovereinsbank M�nchen", inst.getIdString());
			// assertEquals(new Integer(70020270), (Object)
			// inst.getIdentnumber());
			// break;
			// default:
			// break;
			// }
			// i++;
			// }

			Collection<Location> locs = period.getLocations();
			assertEquals(2, locs.size());
			i = 0;
			int j;
			Collection<ClosingPeriod> cps;
			for (Location loc : locs) {
				switch (i) {
				case 0:
					assertEquals("Eurythmiesaal 1 Waldorfschule", loc.getIdString());
					cps = loc.getClosedons();
					assertEquals(2, cps.size());
					j = 0;
					for (ClosingPeriod cp : cps) {
						switch (j) {
						case 1:
							assertEquals("20060116_Schulputztag", cp.getIdString());
							break;
						case 0:
							assertEquals("20051222_Weihnachtsferien", cp.getIdString());
							break;
						default:
							fail("unexpected index");
						}
						j++;
					}
					break;
				case 1:
					assertEquals("Turnhalle Grundschule S" + Umlaut.L_UUML + "d", loc.getIdString());
					cps = loc.getClosedons();
					assertEquals(1, cps.size());
					assertEquals("20051222_Weihnachtsferien", cps.iterator().next().getIdString());
					break;
				default:
					fail("unexpected index");
				}
				i++;
			}

			cps = period.getClosingperiods();
			assertEquals(2, cps.size());
			i = 0;
			for (ClosingPeriod cp : cps) {
				switch (i) {
				case 1:
					assertEquals("20060116_Schulputztag", cp.getIdString());
					locs = cp.getLocations();
					assertEquals(1, locs.size());
					assertEquals("Eurythmiesaal 1 Waldorfschule", locs.iterator().next().getIdString());
					break;
				case 0:
					assertEquals("20051222_Weihnachtsferien", cp.getIdString());
					locs = cp.getLocations();
					assertEquals(2, locs.size());
					j = 0;
					for (Location loc : locs) {
						switch (j) {
						case 0:
							assertEquals("Eurythmiesaal 1 Waldorfschule", loc.getIdString());
							break;
						case 1:
							assertEquals("Turnhalle Grundschule S" + Umlaut.L_UUML + "d", loc.getIdString());
							break;
						default:
							fail("unexpected index");
						}
						j++;
					}
					break;
				default:
					fail("unexpected index");
				}
				i++;
			}
		} finally {
			TypePropertyCollection.setDefaultCharSeparator(defaultCharSeparatorBefore);
		}
	}

	/**
	 * Simple test.
	 * 
	 * @throws ParseException if parsing fails
	 */
	@Test
	public void testGenerateTrainingsNormal() throws ParseException {
		Document doc = setupBPDocument("20070101", "20070101");
		TrainingsList bp = (TrainingsList) doc.getRoot();
		assertEquals(1, doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training").size());
		assertEquals(2, doc.findBeansByType("org.rapidbeans.clubadmin.domain.TrainingDate").size());
		List<RapidBean> trs = bp.createNewTrainings(DFDATE.parse("15.01.2007"), DFDATE.parse("28.01.2007"));
		assertEquals(4, trs.size());
		assertEquals("FCK/football/monday_19:30_Hall/20070115", trs.get(0).getIdString());
		assertEquals("FCK/football/thursday_20:00_Hall/20070118", trs.get(1).getIdString());
		assertEquals("FCK/football/monday_19:30_Hall/20070122", trs.get(2).getIdString());
		assertEquals("FCK/football/thursday_20:00_Hall/20070125", trs.get(3).getIdString());
	}

	/**
	 * Generate trainings for exactly one day.
	 * 
	 * @throws ParseException in case of parsing problems
	 */
	@Test
	public void testGenerateTrainingsOne() throws ParseException {
		Document doc = setupBPDocument("20070101", "20070101");
		TrainingsList bp = (TrainingsList) doc.getRoot();
		List<RapidBean> trs = bp.createNewTrainings(DFDATE.parse("15.01.2007"), DFDATE.parse("15.01.2007"));
		assertEquals(1, trs.size());
		assertEquals("FCK/football/monday_19:30_Hall/20070115", trs.get(0).getIdString());
	}

	/**
	 * Give a span of days where no training date is covered.
	 * 
	 * @throws ParseException in case of parsing problems
	 */
	@Test
	public void testGenerateTrainingsNone() throws ParseException {
		Document doc = setupBPDocument("20070101", "20070101");
		TrainingsList bp = (TrainingsList) doc.getRoot();
		bp.createNewTrainings(DFDATE.parse("16.01.2007"), DFDATE.parse("17.01.2007"));
		List<RapidBean> trs = (List<RapidBean>) doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training");
		assertEquals(1, trs.size());
	}

	/**
	 * Test illegal date order.
	 * 
	 * @throws ParseException in case of parsing problems
	 */
	@Test
	public void testGenerateTrainingsIllegal() throws ParseException {
		Document doc = setupBPDocument("20070101", "20070101");
		List<RapidBean> trs = (List<RapidBean>) doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training");
		assertEquals(1, trs.size());
		TrainingsList bp = (TrainingsList) doc.getRoot();
		try {
			bp.createNewTrainings(DFDATE.parse("18.01.2007"), DFDATE.parse("15.01.2007"));
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
		trs = (List<RapidBean>) doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training");
		assertEquals(1, trs.size());
	}

	/**
	 * Extend the TrainingsList for one week.
	 * 
	 * @throws ParseException if parsing fails
	 */
	@Test
	public void testChangeFromAndGenTraings() throws ParseException {
		Document doc = setupBPDocument("20070101", "20070101");
		List<RapidBean> trs = (List<RapidBean>) doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training");
		assertEquals(1, trs.size());
		TrainingsList bp = (TrainingsList) doc.getRoot();
		assertEquals(DFDATE.parse("01.01.2007"), bp.getFrom());
		assertEquals(DFDATE.parse("01.01.2007"), bp.getTo());

		bp.setTo(DFDATE.parse("07.01.2007"));
		// currently (since we do not have transactions) a special
		// TrainingsListEditor calls the update trainings method
		// of the TrainingsList.
		// So we have to simulate that
		bp.updateTrainings(TrainingsList.UPDATE_MODE_PROPS, null);
		trs = (List<RapidBean>) doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training");
		assertEquals(2, trs.size());
		assertEquals("FCK/football/monday_19:30_Hall/20070101", trs.get(0).getIdString());
		TrainingHeldByTrainer trhbt = (TrainingHeldByTrainer) ((TrainingRegular) trs.get(0)).getHeldbytrainers()
				.iterator().next();
		assertEquals("Smith", trhbt.getTrainer().getLastname());
		assertEquals("Trainer", trhbt.getRole().getName());
		assertEquals("FCK/football/thursday_20:00_Hall/20070104", trs.get(1).getIdString());

		bp.setTo(DFDATE.parse("15.01.2007"));
		// currently (since we do not have transactions) a special
		// TrainingsListEditor calls the update trainings method
		// of the TrainingsList.
		// So we have to simulate that
		bp.updateTrainings(TrainingsList.UPDATE_MODE_PROPS, null);
		trs = (List<RapidBean>) doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training");
		assertEquals(5, trs.size());
		assertEquals("FCK/football/monday_19:30_Hall/20070101", trs.get(0).getIdString());
		assertEquals("FCK/football/monday_19:30_Hall/20070108", trs.get(1).getIdString());
		assertEquals("FCK/football/monday_19:30_Hall/20070115", trs.get(2).getIdString());
		assertEquals("FCK/football/thursday_20:00_Hall/20070104", trs.get(3).getIdString());
		assertEquals("FCK/football/thursday_20:00_Hall/20070111", trs.get(4).getIdString());

		bp.setTo(DFDATE.parse("11.01.2007"));
		// currently (since we do not have transactions) a special
		// TrainingsListEditor calls the update trainings method
		// of the TrainingsList.
		// So we have to simulate that
		bp.updateTrainings(TrainingsList.UPDATE_MODE_PROPS, null);
		// trs = (List<RapidBean>)
		// doc.findBeansByQuery("org.rapidbeans.clubadmin.domain.Training sort by
		// date");
		Query query = new Query("org.rapidbeans.clubadmin.domain.Training");

		TypeProperty[] propTypeArray = { new TrainingRegular().getProperty("date").getType(), };
		query.setSorter(new BeanSorter(propTypeArray));
		trs = doc.findBeansByQuery(query);
		assertEquals(4, trs.size());
		assertEquals("FCK/football/monday_19:30_Hall/20070101", trs.get(0).getIdString());
		assertEquals("FCK/football/thursday_20:00_Hall/20070104", trs.get(1).getIdString());
		assertEquals("FCK/football/monday_19:30_Hall/20070108", trs.get(2).getIdString());
		assertEquals("FCK/football/thursday_20:00_Hall/20070111", trs.get(3).getIdString());

		bp.setTo(DFDATE.parse("01.01.2007"));
		// currently (since we do not have transactions) a special
		// TrainingsListEditor calls the update trainings method
		// of the TrainingsList.
		// So we have to simulate that
		bp.updateTrainings(TrainingsList.UPDATE_MODE_PROPS, null);
		trs = (List<RapidBean>) doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training");
		assertEquals(1, trs.size());
		assertEquals("FCK/football/monday_19:30_Hall/20070101", trs.get(0).getIdString());
	}

	/**
	 * Set up a small test billing period document.
	 * 
	 * 1 club "FCK" 1 department "Football" 2 training dates "monday" 19:30 - 21:30
	 * "thursday" 20:00 - 21:30
	 * 
	 * @param dFrom from date
	 * @param dTo   to date
	 * 
	 * @return the document
	 */
	private Document setupBPDocument(final String dFrom, final String dTo) {
		TrainingsList bp = new TrainingsList("\"" + dFrom + "\" \"" + dTo + "\"");

		Club fck = new Club("\"FCK\"");
		bp.addClub(fck);
		Department football = new Department("\"football\"");
		fck.addDepartment(football);

		Location loc = new Location("\"Hall\"");
		bp.addLocation(loc);

		Trainer john = new Trainer("\"Smith\" \"John\"");
		bp.addTrainer(john);
		john.addDepartment(football);

		TrainerRole roleTrainer = new TrainerRole("\"Trainer\"");
		bp.addTrainerrole(roleTrainer);

		TrainingDate trd1 = new TrainingDate();
		trd1.setDayofweek(DayOfWeek.monday);
		trd1.setTimestart(new TimeOfDay("19:30"));
		trd1.setTimeend(new TimeOfDay("21:30"));
		trd1.setName("Mo");
		trd1.setLocation(loc);

		TrainerPlanning tp1 = new TrainerPlanning();
		tp1.addDefaulttrainer(john);
		tp1.setRole(roleTrainer);
		trd1.addTrainerplanning(tp1);

		TrainingDate trd2 = new TrainingDate();
		trd2.setDayofweek(DayOfWeek.thursday);
		trd2.setTimestart(new TimeOfDay("20:00"));
		trd2.setTimeend(new TimeOfDay("21:30"));
		trd2.setName("Th");
		trd2.setLocation(loc);

		football.addTrainingdate(trd1);
		football.addTrainingdate(trd2);
		Document doc = new Document("trainingslist_test", bp);

		bp.updateTrainings(TrainingsList.UPDATE_MODE_PROPS, null);

		return doc;
	}
}
