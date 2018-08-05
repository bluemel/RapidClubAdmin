/*
 * Rapid Club Admin Application: ExportJobTest.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 08.04.2008
 */
package org.rapidbeans.clubadmin.domain.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.rapidbeans.clubadmin.domain.BillingPeriod;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.service.Umlaut;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.datasource.Document;

/**
 * @author Martin Bluemel
 */
public class ExportJobTest {

	@Before
	public void setUp() {
		TypePropertyCollection.setDefaultCharSeparator(',');
	}

	@Test
	public void testGenerateExportJob() {
		BillingPeriod bp = new BillingPeriod(new String[] { "20080101", "20080301" });
		Document testdoc1 = new Document(new File("src/test/resources/testevidence/trainingsDataAikido.xml"));
		Department dep1 = (Department) testdoc1.findBean(Department.class.getName(), "Budo-Club Ismaning/Aikido");
		Document testdoc2 = new Document(new File("src/test/resources/testevidence/trainingsDataKarate.xml"));
		Department dep2 = (Department) testdoc2.findBean(Department.class.getName(), "Budo-Club Ismaning/Karate");
		ExportJob job = ExportJob.generateExportJob(bp, new Department[] { dep1, dep2 });
		assertEquals(4, job.getTrainers().size());
		List<ExportJobEntry> entries = job.getExportEntries("Bl" + Umlaut.L_UUML + "mel_Martin_");
		assertEquals(2, entries.size());
		int hits = 0;
		Trainer trainer = null;
		for (ExportJobEntry entry : entries) {
			assertEquals("Bl" + Umlaut.L_UUML + "mel_Martin_", entry.getTrainer().getIdString());
			trainer = entry.getTrainer();
			if (entry.getSalaryComponentType().getName().equals("Verg" + Umlaut.L_UUML + "tung")) {
				assertEquals("1158.25 euro", entry.getEarnedMoney().toString());
				assertEquals("113 h", entry.getHeldTrainerHours().toString());
				hits++;
			} else if (entry.getSalaryComponentType().getName().equals("Zulage")) {
				assertEquals("197.75 euro", entry.getEarnedMoney().toString());
				assertEquals("113 h", entry.getHeldTrainerHours().toString());
				hits++;
			}
		}
		assertEquals(2, hits);
		assertEquals("1356.00 euro", job.getOverallEarnedMoney(trainer).toString());
	}

	@Test
	public void testAccesMsAccess() throws ClassNotFoundException, SQLException {
		// Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		// Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

		// String database = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ="
		// + "src/test/resources/db/VERDAT.mdb;DriverID=22;READONLY=true}";
		// String database = "jdbc:ucanaccess://" + new
		// File("src/test/resources/db/VERDAT.mdb").getAbsolutePath()
		// + ";memory=true";
		String database = "jdbc:ucanaccess://src/test/resources/db/VERDAT.mdb;memory=true";
		Connection con = DriverManager.getConnection(database, "", "");
		assertNotNull(con);
	}
}
