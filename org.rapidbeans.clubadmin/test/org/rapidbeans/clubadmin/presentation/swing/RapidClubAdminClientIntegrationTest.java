/*
 * Rapid Beans Framework: RapidClubAdminClientIntegrationTest.java
 *
 * Copyright Martin Bluemel, 2006
 *
 * 26.08.2006
 */

package org.rapidbeans.clubadmin.presentation.swing;

import java.io.File;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import junit.framework.TestCase;

import org.rapidbeans.clubadmin.domain.ClosingPeriod;
import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminSettings;
import org.rapidbeans.clubadmin.presentation.Settings;
import org.rapidbeans.clubadmin.service.CreateTrainingsList;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.ApplicationManager;

/**
 * UI integration tests.
 *
 * @author Martin Bluemel
 */
public class RapidClubAdminClientIntegrationTest extends TestCase {

    private static RapidClubAdminClient client = null;
    private static int testMethodCount = -1;
    private static int testMethodIndex = 0;

    private int countTestMethods() {
        int count = 0;
        for (Method method : this.getClass().getMethods()) {
            if (method.getName().startsWith("test")) {
                count++;
            }
        }
        return count;
    }

    /**
     * start the client.
     */
    public void setUp() {
        if (client == null) {
            TypePropertyCollection.setDefaultCharSeparator(',');
            client = new ApplicationMock();
            ApplicationManager.start(client);
        }
        if (testMethodCount == -1) {
            testMethodCount = this.countTestMethods();
        }
    }

    /**
     * end the client.
     */
    public void tearDown() {
        testMethodIndex++;
        if (testMethodIndex == testMethodCount) {
            ApplicationManager.resetApplication();
            client = null;
        }
    }

    /**
     * Date formatter.
     */
    static final DateFormat DFDATE = DateFormat.getDateInstance(
            DateFormat.MEDIUM, Locale.GERMAN);

    /**
     * create a new billing period.
     */
    public void testCreateNewTrainingsList() {
        Document doc = CreateTrainingsList.createNewTrainingsList(
                client.getMasterDoc(), null);
        assertNotNull(doc);
    }

    /**
     * set from and to and decrease to afterwards.
     *
     * @throws ParseException if date parsing fails
     */
    public void testBPUpdateTrainings() throws ParseException{
        Document doc = CreateTrainingsList.createNewTrainingsList(
                client.getMasterDoc(), null);
        TrainingsList bp = (TrainingsList) doc.getRoot();
        assertEquals(0, doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training").size());
        bp.setFrom(DFDATE.parse("01.01.2007"));
        bp.setTo(DFDATE.parse("01.12.2007"));
        bp.updateTrainings(TrainingsList.UPDATE_MODE_TO, DFDATE.parse("01.12.2007"));
        assertEquals(288, doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training").size());
        bp.setTo(DFDATE.parse("31.03.2007"));
        bp.updateTrainings(TrainingsList.UPDATE_MODE_TO, DFDATE.parse("31.03.2007"));
        assertEquals(78, doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training").size());
    }

    /**
     * set from and to and dcrease to aftwerads.
     *
     * @throws ParseException if date parsing fails
     */
    public void testBPUpdateClosingPeriods() throws ParseException{
        Document masterDoc = client.getMasterDoc();
        ((RapidClubAdminClient) ApplicationManager.getApplication()).setMasterDoc(masterDoc);
        Document doc = CreateTrainingsList.createNewTrainingsList(
                client.getMasterDoc(), null);
        TrainingsList bp = (TrainingsList) doc.getRoot();
        List<RapidBean> cps = doc.findBeansByType(
                "org.rapidbeans.clubadmin.domain.ClosingPeriod");
        assertEquals(5, cps.size());
        assertEquals(DFDATE.parse("22.12.2005"), ((ClosingPeriod) cps.get(0)).getFrom());
        assertEquals(DFDATE.parse("08.01.2006"), ((ClosingPeriod) cps.get(0)).getTo());
        assertEquals(DFDATE.parse("16.01.2006"), ((ClosingPeriod) cps.get(1)).getFrom());
        assertEquals(DFDATE.parse("16.01.2006"), ((ClosingPeriod) cps.get(1)).getTo());
        assertEquals(DFDATE.parse("23.12.2006"), ((ClosingPeriod) cps.get(2)).getFrom());
        assertEquals(DFDATE.parse("06.01.2007"), ((ClosingPeriod) cps.get(2)).getTo());
        assertEquals(DFDATE.parse("01.03.2007"), ((ClosingPeriod) cps.get(3)).getFrom());
        assertEquals(DFDATE.parse("05.03.2007"), ((ClosingPeriod) cps.get(3)).getTo());
        assertEquals(DFDATE.parse("30.03.2007"), ((ClosingPeriod) cps.get(4)).getFrom());
        assertEquals(DFDATE.parse("10.04.2007"), ((ClosingPeriod) cps.get(4)).getTo());
        bp.setFrom(DFDATE.parse("01.01.2007"));
        bp.setTo(DFDATE.parse("31.03.2007"));
        bp.updateClosingPeriodsFromMasterdata();
        cps = doc.findBeansByType("org.rapidbeans.clubadmin.domain.ClosingPeriod");
        assertEquals(3, cps.size());
        assertEquals(DFDATE.parse("23.12.2006"), ((ClosingPeriod) cps.get(0)).getFrom());
        assertEquals(DFDATE.parse("06.01.2007"), ((ClosingPeriod) cps.get(0)).getTo());
        assertEquals(DFDATE.parse("01.03.2007"), ((ClosingPeriod) cps.get(1)).getFrom());
        assertEquals(DFDATE.parse("05.03.2007"), ((ClosingPeriod) cps.get(1)).getTo());
        assertEquals(DFDATE.parse("30.03.2007"), ((ClosingPeriod) cps.get(2)).getFrom());
        assertEquals(DFDATE.parse("10.04.2007"), ((ClosingPeriod) cps.get(2)).getTo());
        bp.setFrom(DFDATE.parse("12.01.2006"));
        bp.updateClosingPeriodsFromMasterdata();
        cps = doc.findBeansByType("org.rapidbeans.clubadmin.domain.ClosingPeriod");
        assertEquals(4, cps.size());
        assertEquals(DFDATE.parse("16.01.2006"), ((ClosingPeriod) cps.get(0)).getFrom());
        assertEquals(DFDATE.parse("16.01.2006"), ((ClosingPeriod) cps.get(0)).getTo());
        assertEquals(DFDATE.parse("23.12.2006"), ((ClosingPeriod) cps.get(1)).getFrom());
        assertEquals(DFDATE.parse("06.01.2007"), ((ClosingPeriod) cps.get(1)).getTo());
        assertEquals(DFDATE.parse("01.03.2007"), ((ClosingPeriod) cps.get(2)).getFrom());
        assertEquals(DFDATE.parse("05.03.2007"), ((ClosingPeriod) cps.get(2)).getTo());
        assertEquals(DFDATE.parse("30.03.2007"), ((ClosingPeriod) cps.get(3)).getFrom());
        assertEquals(DFDATE.parse("10.04.2007"), ((ClosingPeriod) cps.get(3)).getTo());
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
        }

        @Override
        public RapidClubAdminSettings getSettingsRapidClubAdmin() {
            Settings settings = (Settings) new Document(new File("testdata/testsettings.xml")).getRoot();
            return settings.getSettings();
        }

        private Document masterDoc = null;
        private boolean initializingMasterData = false;

        public boolean isInitializingMasterData() {
            return initializingMasterData;
        }

        @Override
        public Document getMasterDoc() {
            if (this.masterDoc == null) {
                try {
                    initializingMasterData = true;
                    this.masterDoc = new Document(new File("testdata/masterdata.xml"));
                } finally {
                    initializingMasterData = false;
                }
            }
            return this.masterDoc;
        }
    }

    private class LocaleMock extends RapidBeansLocale {
    }
}
