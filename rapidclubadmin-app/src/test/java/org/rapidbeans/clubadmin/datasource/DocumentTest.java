/*
 * Rapid Beans Clubadmin Application: DocumentTest.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * Nov 27, 2005
 */
package org.rapidbeans.clubadmin.datasource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.rapidbeans.clubadmin.domain.ClosingPeriod;
import org.rapidbeans.clubadmin.domain.Club;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Location;
import org.rapidbeans.clubadmin.domain.MasterData;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.TrainerAttribute;
import org.rapidbeans.clubadmin.domain.TrainingDate;
import org.rapidbeans.clubadmin.domain.TrainingRegular;
import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.clubadmin.test.TestHelper;
import org.rapidbeans.core.basic.BeanSorter;
import org.rapidbeans.core.basic.IdGeneratorNumeric;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.ReadonlyListCollection;
import org.rapidbeans.core.exception.BeanDuplicateException;
import org.rapidbeans.core.type.TypeProperty;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.datasource.query.Query;

/**
 * Unit tests for class Length.
 *
 * @author Martin Bluemel
 */
public final class DocumentTest {

	/**
	 * set up that test.
	 */
	@Before
	public void setUp() {
		TypePropertyCollection.setDefaultCharSeparator(',');
		TypeRapidBean tpType = TypeRapidBean.forName("org.rapidbeans.clubadmin.domain.TrainerPlanning");
		if (tpType.getIdGenerator() == null) {
			tpType.setIdGenerator(new IdGeneratorNumeric());
		}
		tpType = TypeRapidBean.forName("org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer");
		if (tpType.getIdGenerator() == null) {
			tpType.setIdGenerator(new IdGeneratorNumeric());
		}
	}

	/**
	 * Date formatter.
	 */
	static final DateFormat DFDATE = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);

	/**
	 * Date formatter.
	 */
	static final DateFormat DFTIME = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM,
			Locale.GERMAN);

	/**
	 * Date formatter.
	 */
	static final DateFormat DFTIMELONG = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG,
			Locale.GERMAN);

	@Test
	public void testReadMasterdata() {
		new Document(new File("src/test/resources/masterdata1.xml"));
		new Document(new File("src/test/resources/data/masterdata.xml"));
		final MasterData masterData = (MasterData) new Document(new File("src/test/resources/data/masterdata2.xml"))
				.getRoot();
		for (final Trainer t : masterData.getTrainers()) {
			final String attrList = toStringAttrs(t.getTrainerattributes());
			final String depList = toStringDeps(t.getDepartments());
			assertNotNull(attrList);
			assertNotNull(depList);
		}
	}

	private String toStringAttrs(ReadonlyListCollection<TrainerAttribute> attrs) {
		final StringBuilder sb = new StringBuilder();
		if (attrs != null) {
			boolean consec = false;
			for (final TrainerAttribute ta : attrs) {
				if (consec) {
					sb.append(", ");
				}
				sb.append(ta.getName());
				consec = true;
			}
		}
		final String list = sb.toString();
		return (list.equals("") ? "" : " (" + list + ')');
	}

	private String toStringDeps(ReadonlyListCollection<Department> departments) {
		final StringBuilder sb = new StringBuilder();
		boolean consec = false;
		for (final Department d : departments) {
			if (consec) {
				sb.append(", ");
			}
			sb.append(d.getName());
			consec = true;
		}
		return sb.toString();
	}

	/**
	 * test creating duplicate Trainers.
	 *
	 * @throws ParseException in case of (unexpected) parsing error
	 */
	@Test
	public void testCreateDuplicates() throws ParseException {
		Document doc = setupTestTrainingsListDocument();
		TrainingsList period = (TrainingsList) doc.getRoot();
		String[] propValues = { "A", "B" };
		period.addTrainer(new Trainer(propValues));
		try {
			period.addTrainer(new Trainer(propValues));
			fail("expected a BeanDuplicateException");
		} catch (BeanDuplicateException e) {
			assertTrue(true);
		}
		try {
			period.addTrainer(new Trainer(propValues));
			fail("expected a BeanDuplicateException");
		} catch (BeanDuplicateException e) {
			assertTrue(true);
		}
	}

	/**
	 * test deleting a training.
	 *
	 * @throws ParseException in case of (unexpected) parsing error
	 */
	@Test
	public void testDeleteTraining() throws ParseException {
		Document doc = setupTestTrainingsListDocument();
		assertEquals(2, doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training").size());
		TrainingRegular tr = (TrainingRegular) doc.findBean("org.rapidbeans.clubadmin.domain.Training",
				"Budo-Club Ismaning/Aikido/monday_20:30_Turnhalle Grundschule S�d/20061106");
		assertNotNull(tr);
		tr.delete();
		assertEquals(1, doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training").size());
	}

	/**
	 * test deleting a trainer.
	 *
	 * @throws ParseException in case of (unexpected) parsing error
	 */
	@Test
	public void testDeleteTrainer() throws ParseException {
		Document doc = setupTestTrainingsListDocument();
		assertEquals(2, doc.findBeansByType("org.rapidbeans.clubadmin.domain.Trainer").size());
		Trainer tr = (Trainer) doc.findBean("org.rapidbeans.clubadmin.domain.Trainer", "Bl�mel_Martin_");
		assertNotNull(tr);
		tr.delete();
		assertEquals(1, doc.findBeansByType("org.rapidbeans.clubadmin.domain.Trainer").size());
	}

	/**
	 * test remove a component bean from a composite. Because the component is part
	 * of a document it is automatically deleted there. This includes breaking
	 * references to that orphaned component.
	 * 
	 * @throws MalformedURLException
	 */
	@Test
	public void testRemoveLinkComponentDeleteOrphanedFromDoc_nton() throws MalformedURLException {
		// set up a billing period document with one closing period and one location
		// linked with each other
		MasterData bp = new MasterData();
		Document doc = new Document(bp);
		Location wembley = new Location("Wembley");
		bp.addLocation(wembley);
		ClosingPeriod summerHolidays = new ClosingPeriod(new String[] { "20080701", "Summer Holydays" });
		bp.addClosingperiod(summerHolidays);
		wembley.addClosedon(summerHolidays);

		assertEquals(1, bp.getLocations().size());
		assertSame(wembley, bp.getLocations().iterator().next());
		assertEquals(1, bp.getClosingperiods().size());
		assertSame(summerHolidays, bp.getClosingperiods().iterator().next());
		assertSame(wembley, doc.findBeansByType("org.rapidbeans.clubadmin.domain.Location").get(0));
		assertSame(summerHolidays, doc.findBeansByType("org.rapidbeans.clubadmin.domain.ClosingPeriod").get(0));
		assertEquals(1, wembley.getClosedons().size());
		assertSame(summerHolidays, wembley.getClosedons().iterator().next());
		assertEquals(1, summerHolidays.getLocations().size());
		assertSame(wembley, summerHolidays.getLocations().iterator().next());

		bp.removeClosingperiod(summerHolidays);

		assertEquals(1, bp.getLocations().size());
		assertSame(wembley, bp.getLocations().iterator().next());
		assertEquals(0, bp.getClosingperiods().size());
		assertSame(wembley, doc.findBeansByType("org.rapidbeans.clubadmin.domain.Location").get(0));
		assertEquals(0, doc.findBeansByType("org.rapidbeans.clubadmin.domain.ClosingPeriod").size());
		assertEquals(0, wembley.getClosedons().size());
		assertEquals(0, summerHolidays.getLocations().size());
		File docfile = new File("src/test/resources/testDocFile.xml");
		doc.setUrl(docfile.toURI().toURL());
		doc.save();

		doc = new Document(docfile);
		bp = (MasterData) doc.getRoot();
		wembley = (Location) doc.findBean("org.rapidbeans.clubadmin.domain.Location", "Wembley");
		summerHolidays = (ClosingPeriod) doc.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod",
				"20080701_Summer Holydays");
		assertNull(summerHolidays);
		assertEquals(1, bp.getLocations().size());
		assertSame(wembley, bp.getLocations().iterator().next());
		assertNull(bp.getClosingperiods());
		assertSame(wembley, doc.findBeansByType("org.rapidbeans.clubadmin.domain.Location").get(0));
		assertEquals(0, doc.findBeansByType("org.rapidbeans.clubadmin.domain.ClosingPeriod").size());
		assertEquals(0, wembley.getClosedons().size());
		docfile.delete();
	}

	/**
	 * test the setup.
	 *
	 * @throws ParseException in case of (unexpected) parsing error
	 */
	@Test
	public void testSetupTestTrainingsListDoc() throws ParseException {
		Document doc = setupTestTrainingsListDocument();
		TrainingsList period = (TrainingsList) doc.getRoot();
		assertEquals(DFDATE.parse("01.10.2006"), period.getFrom());
		assertEquals(DFDATE.parse("31.12.2006"), period.getTo());
		Trainer martin = (Trainer) doc.findBean("org.rapidbeans.clubadmin.domain.Trainer", "Bl�mel_Martin_");
		assertEquals("Bl�mel_Martin_", martin.getIdString());
		RapidBean periodWithTrainerMartin = doc
				.findBeanByQuery("org.rapidbeans.clubadmin.domain.TrainingsList[trainers[lastname = 'Bl�mel']]");
		assertSame(period, periodWithTrainerMartin);

		assertEquals(1, ((Department) ((Club) period.getClubs().iterator().next()).getDepartments().iterator().next())
				.getTrainingdates().size());
	}

	/**
	 * test the setup.
	 *
	 * @throws ParseException in case of (unexpected) parsing error
	 */
	@Test
	public void testSetupTestMasterDataDoc() throws ParseException {
		Document doc = setupTestMasterDataDocument();
		MasterData period = (MasterData) doc.getRoot();
		Trainer martin = (Trainer) doc.findBean("org.rapidbeans.clubadmin.domain.Trainer", "Bl�mel_Martin_");
		assertEquals("Bl�mel_Martin_", martin.getIdString());
		RapidBean periodWithTrainerMartin = doc
				.findBeanByQuery("org.rapidbeans.clubadmin.domain.MasterData[trainers[lastname = 'Bl�mel']]");
		assertSame(period, periodWithTrainerMartin);
	}

	/**
	 * write the test billing period and read it afterwards.
	 *
	 * @throws IOException if IO fails
	 */
	@Test
	public void testWriteAndRead() throws IOException {

		// check sorting
		Location loc = new Location("Munich");
		TrainingDate td = new TrainingDate(new String[] { "monday", "08:00" });
		assertSame(TreeSet.class,
				((TypePropertyCollection) td.getProperty("trainings").getType()).getCollectionClass());
		td.setLocation(loc);
		TrainingRegular t1 = new TrainingRegular("20100101");
		TrainingRegular t2 = new TrainingRegular("20100102");
		TrainingRegular t3 = new TrainingRegular("20100103");
		assertEquals(-1, "20100101".compareTo("20100102"));
		assertEquals(-1, t1.compareTo(t2));
		assertEquals(-1, t1.compareTo(t3));
		assertEquals(-1, t2.compareTo(t3));
		assertEquals(1, t2.compareTo(t1));
		assertEquals(1, t3.compareTo(t1));
		assertEquals(1, t3.compareTo(t2));
		td.addTraining(t2);
		td.addTraining(t3);
		td.addTraining(t1);
		TreeSet<TrainingRegular> treeSet = new TreeSet<TrainingRegular>();
		treeSet.add(t2);
		treeSet.add(t3);
		treeSet.add(t1);
		assertSame(t1, treeSet.toArray()[0]);
		assertSame(t2, treeSet.toArray()[1]);
		assertSame(t3, treeSet.toArray()[2]);
		List<TrainingRegular> trainings = (List<TrainingRegular>) td.getTrainings();
		assertSame(TreeSet.class,
				((TypePropertyCollection) td.getProperty("trainings").getType()).getCollectionClass());
		assertSame(t1, trainings.get(0));
		assertSame(t2, trainings.get(1));
		assertSame(t3, trainings.get(2));
		assertEquals("monday_8:00_Munich/20100101", t1.getIdString());
		assertEquals("monday_8:00_Munich/20100102", t2.getIdString());
		assertEquals("monday_8:00_Munich/20100103", t3.getIdString());
		assertEquals(-1, t1.compareTo(t2));
		assertEquals(-1, t1.compareTo(t3));
		assertEquals(-1, t2.compareTo(t3));
		assertEquals(1, t3.compareTo(t2));

		// check read / write / read
		File testfilesrc = new File("src/test/resources/trainingslist_20060101_20060331.xml");
		Document doc = new Document("testdoc", testfilesrc);
		TrainingDate trd = (TrainingDate) doc.findBean("org.rapidbeans.clubadmin.domain.TrainingDate",
				"Budo-Club Ismaning/Aikido/monday_19:00_Eurythmiesaal 1 Waldorfschule");
		trainings = (List<TrainingRegular>) trd.getTrainings();
		TrainingRegular tr1 = trainings.get(0);
		TrainingRegular tr2 = trainings.get(1);
		assertEquals(2, "c".compareTo("a"));
		assertEquals(7, "20060327".compareTo("20060320"));
		assertEquals(-1, tr1.compareTo(tr2));
		assertEquals(-7, tr1.getIdString().compareTo(tr2.getIdString()));
		File testfile = new File("src/test/resources/testdoc.xml");
		doc.setUrl(testfile.toURI().toURL());
		doc.save();
		TestHelper.assertFilesEqual(testfilesrc, testfile);
		// assert readability
		doc = new Document("testdoc", testfile);
		testfile.delete();
	}

	/**
	 * test with sorting.
	 *
	 * @throws ParseException if parsing fails
	 */
	@Test
	public void testFindTrainingWithSorting() throws ParseException {
		Document doc = setupTestTrainingsListDocument();
		final Query query = new Query("org.rapidbeans.clubadmin.domain.Training");
		List<RapidBean> resultSet = doc.findBeansByQuery(query);
		assertEquals(2, resultSet.size());
		TrainingRegular tr = new TrainingRegular();
		final TypeProperty[] proptypes = { tr.getProperty("date").getType() };
		/* List trainings = */ doc.findBeansByQuery(query);
		final BeanSorter sorter = new BeanSorter(proptypes);
		query.setSorter(sorter);
		resultSet = doc.findBeansByQuery(query);
		assertEquals(2, resultSet.size());
	}

	/**
	 * detect the problem we have with sorted trainings.
	 */
	@Test
	public void testDetectProblemWithSortedTrainings() {
		Document doc = new Document(new File("src/test/resources/trainingslist_20060101_20060331.xml"));
		List<RapidBean> trainings = doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training");
		assertEquals(78, trainings.size());
		final TypeProperty[] proptypes = { new TrainingRegular().getProperty("date").getType() };
		final BeanSorter sorter = new BeanSorter(proptypes);
		final Query query = new Query("org.rapidbeans.clubadmin.domain.Training");
		query.setSorter(sorter);
		trainings = doc.findBeansByQuery(query);
		assertEquals(78, trainings.size());
	}

	@Test
	public void testReadInitMasterData() {
		final Document doc = new Document(new File("src/test/resources/config/initial/data/masterdata.xml"));
		assertNotNull(doc.findBean("org.rapidbeans.clubadmin.domain.ClubadminUser", "administrator"));
	}

	/**
	 * @return the test TrainingsList Document.
	 *
	 * @exception ParseException if Date parsing fails
	 */
	private Document setupTestTrainingsListDocument() throws ParseException {

		TrainingsList period = new TrainingsList("\"20061001\" \"20061231\"");
		Document document = new Document("testtrainingslist", period);

		Trainer martin = new Trainer("\"Bl�mel\" \"Martin\"");
		// martin.setCreditinstitute(hvb);
		period.addTrainer(martin);

		Trainer chris = new Trainer("\"H�ppner\" \"Christop\"");
		// chris.setCreditinstitute(hvb);
		period.addTrainer(chris);

		Club bci = new Club("\"Budo-Club Ismaning\"");
		period.addClub(bci);

		Location loc = new Location("\"Turnhalle Grundschule S�d\"");
		period.addLocation(loc);

		Department aikido = new Department("\"Aikido\"");
		bci.addDepartment(aikido);

		TrainingDate aikidoMonday = new TrainingDate("\"monday\" \"20:30\" \"Turnhalle Grundschule S�d\"");
		aikidoMonday.setName("Aikido Monday");
		aikido.addTrainingdate(aikidoMonday);

		TrainingRegular tr = new TrainingRegular("\"20061106\"");
		tr.setDate(DFDATE.parse("06.11.2006"));
		aikidoMonday.addTraining(tr);

		tr = new TrainingRegular("\"20061113\"");
		tr.setDate(DFDATE.parse("13.11.2006"));
		aikidoMonday.addTraining(tr);

		return document;
	}

	/**
	 * @return the test document.
	 *
	 * @exception ParseException if Date parsing fails
	 */
	private Document setupTestMasterDataDocument() throws ParseException {
		Trainer martin = new Trainer();
		martin.setFirstname("Martin");
		martin.setLastname("Bl�mel");
		// martin.setCreditinstitute(hvb);

		TrainingDate aikidoMonday = new TrainingDate();
		aikidoMonday.setName("Aikido Monday");

		MasterData ms = new MasterData();
		ms.addTrainer(martin);

		Document document = new Document("testmasterdata", ms);
		return document;
	}
}
