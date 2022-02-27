package org.rapidbeans.clubadmin.domain.report;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.TrainingRegular;
import org.rapidbeans.clubadmin.domain.TrainingState;
import org.rapidbeans.clubadmin.service.Umlaut;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.datasource.Document;

public class TrainingsTimeStatisticsTest {

	@BeforeClass
	public static void setUp() {
		TypePropertyCollection.setDefaultCharSeparator(',');
	}

	@Test
	public void testExecuteMartin1() {
		Locale formerDefault = Locale.getDefault();
		try {
			Locale.setDefault(Locale.GERMANY);
			Document testdoc = new Document(new File("src/test/resources/trainingslist_20060101_20060331.xml"));
			Trainer martin = (Trainer) testdoc.findBeanByQuery(
					"org.rapidbeans.clubadmin.domain.Trainer[id = 'Bl" + Umlaut.L_UUML + "mel_Martin_']");
			ArrayList<Trainer> trainers = new ArrayList<Trainer>();
			trainers.add(martin);
			Department aikido = (Department) testdoc
					.findBeanByQuery("org.rapidbeans.clubadmin.domain.Department[id = 'Budo-Club Ismaning/Aikido']");
			RapidBeansLocale locale = new RapidBeansLocale("de");
			locale.init("org.rapidbeans.clubadmin");
			for (RapidBean bean : testdoc.findBeansByType("org.rapidbeans.clubadmin.domain.Training")) {
				TrainingRegular training = (TrainingRegular) bean;
				training.setState(TrainingState.checked);
			}
			final TrainingsTimeStat stat = new TrainingsTimeStat();
			for (final Trainer trainer : trainers) {
				TrainingsTimeStatisticsAction.execute(stat, trainer, aikido, locale);
			}
			System.out.println(stat.getReport());
//			StringTokenizer st = new StringTokenizer(overview.toString(), "\n");
//			String token = st.nextToken();
//			String tokenExpected1 = "Trainings" + Umlaut.L_UUML + "bersicht   Trainer: Bl" + Umlaut.L_UUML
//					+ "mel, Martin,   Abteilung: Budo-Club Ismaning/Aikido";
//			assertEquals(tokenExpected1, token);
//			token = st.nextToken();
//			assertEquals("---------------------------------------------------------------------------", token);
//			String test1 = (String) st.nextElement();
//			assertEquals("  1. 02.01.2006 Montag     20:30 Meditation II                    20,00 EUR", test1);
		} finally {
			Locale.setDefault(formerDefault);
		}
	}
}
