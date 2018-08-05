/*
 * Rapid Club Admin Application: ExportJobTest.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 24.03.2008
 */
package org.rapidbeans.clubadmin.domain.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.report.Evidence.Template;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminSettings;
import org.rapidbeans.clubadmin.presentation.Settings;
import org.rapidbeans.clubadmin.service.Umlaut;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.ApplicationManager;

/**
 * @author Martin Bluemel
 */
public class EvidenceTest {

    @Before
    public void setUp() {
        TypePropertyCollection.setDefaultCharSeparator(',');
    }

    /**
     * Test method for
     * {@link org.rapidbeans.clubadmin.domain.report.Evidence#insertEvidence(java.lang.String, org.rapidbeans.clubadmin.domain.Trainer, org.rapidbeans.clubadmin.domain.Department, java.util.List)}
     * .
     * 
     * @throws IOException
     *             if IO fails
     */
    @Test
    public void testReadTemplate() throws IOException {
        Template tmpl = Evidence.readTemplate(new File("testdata/testevidence/templateEvidence.rtf"));
        File testfile = new File("testdata/testevidence/test.rtf");
        FileWriter out = new FileWriter(testfile);
        out.write(tmpl.getHeader());
        out.write(tmpl.getBody());
        out.write(tmpl.getBody());
        out.write(tmpl.getFooter());
        out.close();
        assertTrue(FileHelper.filesEqual(testfile, new File("testdata/testevidence/templateEvidence1x2.rtf"), true,
                true));
        assertTrue(testfile.delete());
    }

    @Test
    public void testDetermineRowCount30() throws IOException {
        Template tmpl = Evidence.readTemplate(new File("testdata/testevidence/templateEvidence.rtf"));
        assertEquals(30, tmpl.getRowcount());
    }

    @Test
    public void testDetermineRowCount32() throws IOException {
        Template tmpl = Evidence.readTemplate(new File("testdata/testevidence/templateEvidence32.rtf"));
        assertEquals(32, tmpl.getRowcount());
    }

    @Test
    public void testReportDepartmentSimple() throws IOException {
        ApplicationMock app = new ApplicationMock();
        ApplicationManager.start(app);
        Document testdoc = new Document(new File("testdata/testevidence/trainingsDataAikido.xml"));
        Trainer trainer = (Trainer) testdoc.findBean(Trainer.class.getName(), "Bl" + Umlaut.L_UUML + "mel_Ulrike_");
        Assert.assertEquals("Ulrike", trainer.getFirstname());
        Department department = (Department) testdoc.findBean(Department.class.getName(), "Budo-Club Ismaning/Aikido");
        Assert.assertEquals("Aikido", department.getName());
        File testfile = new File("testdata/testevidence/testreport.rtf");
        File expected = new File("testdata/testevidence/resultEvidenceDepartmentSimple.rtf");
        File template = new File("testdata/testevidence/templateEvidence.rtf");
        Evidence.printReportEvidence(testfile, template, department, trainer, true);
        assertTrue(
                "Test file " + testfile.getAbsolutePath() + " does not equal reference file "
                        + expected.getAbsolutePath(), FileHelper.filesEqual(testfile, expected, true, true));
        testfile.delete();
        ApplicationManager.resetApplication();
    }

    @Test
    public void testReportOverflowDepartmentSingle() throws IOException {
        ApplicationMock app = new ApplicationMock();
        ApplicationManager.start(app);
        Document testdoc = new Document(new File("testdata/testevidence/trainingsDataAikido.xml"));
        Trainer trainer = (Trainer) testdoc.findBean(Trainer.class.getName(), "Bl" + Umlaut.L_UUML + "mel_Martin_");
        Department department = (Department) testdoc.findBean(Department.class.getName(), "Budo-Club Ismaning/Aikido");
        File testfile = new File("testdata/testevidence/testreport.rtf");
        File expected = new File("testdata/testevidence/resultEvidenceOverflowDepartmentSingle.rtf");
        File template = new File("testdata/testevidence/templateEvidence.rtf");
        Evidence.printReportEvidence(testfile, template, department, trainer, true);
        // Runtime.getRuntime().exec("cmd.exe /C " +
        // testfile.getAbsolutePath());
        assertTrue(FileHelper.filesEqual(testfile, expected, true, true));
        testfile.delete();
        ApplicationManager.resetApplication();
    }

    @Test
    public void testReportOverflowDepartmentCoherent() throws IOException {
        ApplicationMock app = new ApplicationMock();
        ApplicationManager.start(app);
        Document testdoc = new Document(new File("testdata/testevidence/trainingsDataAikido.xml"));
        Trainer trainer = (Trainer) testdoc.findBean(Trainer.class.getName(), "Bl" + Umlaut.L_UUML + "mel_Martin_");
        Department department = (Department) testdoc.findBean(Department.class.getName(), "Budo-Club Ismaning/Aikido");
        File testfile = new File("testdata/testevidence/testreport.rtf");
        File expected = new File("testdata/testevidence/resultEvidenceOverflowDepartmentCoherent.rtf");
        File template = new File("testdata/testevidence/templateEvidence.rtf");
        Evidence.printReportEvidence(testfile, template, department, trainer, false);
        // Runtime.getRuntime().exec("cmd.exe /C " +
        // testfile.getAbsolutePath());
        assertTrue(FileHelper.filesEqual(testfile, expected, true, true));
        testfile.delete();
        ApplicationManager.resetApplication();
    }

    @Test
    public void testReportOverflowDepartmentsCoherent() throws IOException {
        ApplicationMock app = new ApplicationMock();
        ApplicationManager.start(app);
        String trainerId = "Bl" + Umlaut.L_UUML + "mel_Martin_";
        Document testdoc1 = new Document(new File("testdata/testevidence/trainingsDataAikido.xml"));
        Department dep1 = (Department) testdoc1.findBean(Department.class.getName(), "Budo-Club Ismaning/Aikido");
        Document testdoc2 = new Document(new File("testdata/testevidence/trainingsDataKarate.xml"));
        Department dep2 = (Department) testdoc2.findBean(Department.class.getName(), "Budo-Club Ismaning/Karate");
        File testfile = new File("testdata/testevidence/testreport.rtf");
        File expected = new File("testdata/testevidence/resultEvidenceOverflowDepartmentsCoherent.rtf");
        File template = new File("testdata/testevidence/templateEvidence.rtf");
        Evidence.printReportEvidence(testfile, template, new Department[] { dep1, dep2 }, trainerId, false);
        // Runtime.getRuntime().exec("cmd.exe /C " +
        // testfile.getAbsolutePath());
        assertTrue(FileHelper.filesEqual(testfile, expected, true, true));
        testfile.delete();
        ApplicationManager.resetApplication();
    }

    @Test
    public void testReportCollected() throws IOException {
        ApplicationMock app = new ApplicationMock();
        ApplicationManager.start(app);
        Document testdoc1 = new Document(new File("testdata/testevidence/trainingsDataAikido.xml"));
        Department dep1 = (Department) testdoc1.findBean(Department.class.getName(), "Budo-Club Ismaning/Aikido");
        Document testdoc2 = new Document(new File("testdata/testevidence/trainingsDataKarate.xml"));
        Department dep2 = (Department) testdoc2.findBean(Department.class.getName(), "Budo-Club Ismaning/Karate");
        File testfile = new File("testdata/testevidence/testreport.rtf");
        File expected = new File("testdata/testevidence/resultEvidenceCollected.rtf");
        File template = new File("testdata/testevidence/templateEvidence.rtf");
        Evidence.printReportEvidence(testfile, template, new Department[] { dep1, dep2 }, false);
        // Runtime.getRuntime().exec("cmd.exe /C " +
        // testfile.getAbsolutePath());
        assertTrue(FileHelper.filesEqual(testfile, expected, true, true));
        testfile.delete();
        ApplicationManager.resetApplication();
    }

    private class ApplicationMock extends RapidClubAdminClient {

        @Override
        public void init() {
        }

        @Override
        public void start() {
            RapidBeansLocale locale = new LocaleMock();
            locale.setName("de");
            locale.setLocale(new Locale("de"));
            this.setCurrentLocale(locale);
            this.setRoot("testdata");
        }

        @Override
        public RapidClubAdminSettings getSettingsRapidClubAdmin() {
            Settings settings = (Settings) new Document(new File("testdata/testsettings.xml")).getRoot();
            return settings.getSettings();
        }
    }

    private class LocaleMock extends RapidBeansLocale {
    }
}
