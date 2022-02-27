package org.rapidbeans.clubadmin.domain.report;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.datasource.Document;

public class OverSupervisedTrainingsTest {

	@BeforeClass
	public static void setUp() {
		TypePropertyCollection.setDefaultCharSeparator(',');
	}

	@Test
	public void test() {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		@SuppressWarnings("unused")
		StringBuilder report = new StringBuilder();
		SimpleDateFormat sfmt = new SimpleDateFormat("dd.MM.yyyy");
		TrainingsList trainingsList = (TrainingsList) new Document(
			new File("src/test/resources/reports/overSupervisedTrainings/trlist01.xml")).getRoot();
		final Department department = new Department("Aikido"); 
		new OverSupervisedTrainings().reportOverSupervisedTrainings(department, trainingsList, report, nf, sfmt);
		System.out.println(report);
	}
}
