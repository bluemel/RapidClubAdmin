/*
 * Rapid Club Admin Application: ExportJobTest.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 08.04.2008
 */
package org.rapidbeans.clubadmin.domain.export;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import junit.framework.TestCase;

import org.rapidbeans.clubadmin.domain.BillingPeriod;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.datasource.Document;

/**
 * @author Martin Bluemel
 */
public class ExportJobTest extends TestCase {

    public void testGenerateExportJob() {
        BillingPeriod bp = new BillingPeriod(new String[]{"20080101", "20080301"});
        Document testdoc1 = new Document(new File("testdata/testevidence/trainingsDataAikido.xml"));
        Department dep1 = (Department) testdoc1.findBean(Department.class.getName(), "Budo-Club Ismaning/Aikido");
        Document testdoc2 = new Document(new File("testdata/testevidence/trainingsDataKarate.xml"));
        Department dep2 = (Department) testdoc2.findBean(Department.class.getName(), "Budo-Club Ismaning/Karate");
        ExportJob job = ExportJob.generateExportJob(bp, new Department[]{dep1, dep2});
        assertEquals(4, job.getTrainers().size());
        List<ExportJobEntry> entries = job.getExportEntries("Blümel_Martin_");
        assertEquals(2, entries.size());
        int hits = 0;
        Trainer trainer = null;
        for (ExportJobEntry entry : entries) {
            assertEquals("Blümel_Martin_", entry.getTrainer().getIdString());
            trainer = entry.getTrainer();
            if (entry.getSalaryComponentType().getName().equals("Vergütung")) {
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

// No supported feature anymore
//    public void testExportToVerdat()
//        throws InterruptedException, IOException {
//        File testfile = new File("testdata/db/test.mdb");
//        try {
//            BillingPeriod bp = new BillingPeriod(new String[]{"20080401", "20080831"});
//            Document testdoc1 = new Document(new File("testdata/testevidence/trainingsDataAikido.xml"));
//            Department dep1 = (Department) testdoc1.findBean(Department.class.getName(), "Budo-Club Ismaning/Aikido");
//            Document testdoc2 = new Document(new File("testdata/testevidence/trainingsDataKarate.xml"));
//            Department dep2 = (Department) testdoc2.findBean(Department.class.getName(), "Budo-Club Ismaning/Karate");
//            ExportJob job = ExportJob.generateExportJob(bp, new Department[]{dep1, dep2});
//            FileHelper.copyFile(new File("testdata/db/VERDAT.mdb"), testfile);
//            DbExporterVerdat exporter = new DbExporterVerdat(job);
//            exporter.setToFile(new File("testdata/db/test.mdb"));
//            exporter.export();
//            assertEquals(1, job.getResultEntries(ExportJobResultEntryModificationType.create,
//                    "ï¿½bungsleiter").size());
//            assertEquals("Damir Dautovic", job.getResultEntries(ExportJobResultEntryModificationType.create,
//                    "ï¿½bungsleiter").iterator().next().getAttributeValue("UName"));
//            List<ExportJobResultEntry> abrechnungen = job.getResultEntries(
//                    ExportJobResultEntryModificationType.create, "ï¿½bungsleiterAbrechnung");
//            assertEquals(4, abrechnungen.size());
//            switch (PlatformHelper.getOs()) {
//            case windows:
//                Runtime.getRuntime().exec("cmd /C del testdata\\db\\test_*.mdb.bak").waitFor();
//                break;
//            case linux:
//                Runtime.getRuntime().exec("rm testdata/db/test_*.mdb.bak").waitFor();
//                break;
//            }
//        } finally {
//            if (testfile.exists()) {
//                testfile.delete();
//            }
//        }
//    }

    public void testAccesMsAccess()
    	throws ClassNotFoundException, SQLException {
    	Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        String database = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ="
            + "testdata/db/VERDAT.mdb;DriverID=22;READONLY=true}";
        Connection con = DriverManager.getConnection(database ,"","");
        assertNotNull(con);
    }
}
