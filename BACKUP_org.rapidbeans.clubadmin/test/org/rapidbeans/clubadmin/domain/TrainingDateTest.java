package org.rapidbeans.clubadmin.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.clubadmin.domain.Club;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Location;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.TrainerPlanning;
import org.rapidbeans.clubadmin.domain.TrainerRole;
import org.rapidbeans.clubadmin.domain.TrainingDate;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.domain.math.DayOfWeek;
import org.rapidbeans.domain.math.TimeOfDay;

import junit.framework.TestCase;

/**
 * UnitTest for class TrainingDate.
 *
 * @author Martin Bluemel
 */
public class TrainingDateTest extends TestCase {

    /**
     * Date formatter.
     */
    static final DateFormat DFDATE = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);

    /**
     * Test method for constructor.
     */
    public void testGetType() {
        TrainingDate trdate = new TrainingDate();
        TypeRapidBean type = trdate.getType();
        assertEquals("org.rapidbeans.clubadmin.domain.TrainingDate", type.getName());
    }

    /**
     * Test method for constructor.
     */
    public void testTrainingDate() {
        TrainingDate trdate = new TrainingDate();
        trdate.setTimestart(new TimeOfDay("19:30"));
        assertEquals("19:30", trdate.getTimestart().toString());
        trdate.setTimeend(new TimeOfDay("21:30"));
        assertEquals("21:30", trdate.getTimeend().toString());
    }

    /**
     * test validation "to" less "from".
     */
    public void testValidateToLessFrom() {
        TrainingDate trdate = new TrainingDate();
        trdate.setTimestart(new TimeOfDay("19:30"));
        try {
            trdate.setTimeend(new TimeOfDay("19:00"));
            fail("expected ValidationException");
        } catch (ValidationException e) {
            assertEquals("invalid.prop.trainingdate.end.less.start", e.getSignature());
        }
        try {
            trdate.setTimeend(new TimeOfDay("19:30"));
            fail("expected ValidationException");
        } catch (ValidationException e) {
            assertEquals("invalid.prop.trainingdate.start.equals.end", e.getSignature());
        }
    }

    /**
     * test validation "to" less "from".
     */
    public void testValidateFromGreaterTo() {
        TrainingDate trdate = new TrainingDate();
        trdate.setTimeend(new TimeOfDay("19:30"));
        try {
            trdate.setTimestart(new TimeOfDay("19:31"));
            fail("expected ValidationException");
        } catch (ValidationException e) {
            assertEquals("invalid.prop.trainingdate.start.greater.end", e.getSignature());
        }
        try {
            trdate.setTimestart(new TimeOfDay("19:30"));
            fail("expected ValidationException");
        } catch (ValidationException e) {
            assertEquals("invalid.prop.trainingdate.start.equals.end", e.getSignature());
        }
    }

    /**
     * Delete one training date.
     * The trainings for this training date should be automatically deleted:
     * (composition delete cascade).
     *
     * @throws ParseException if parsing fails
     */
    public void testDeleteTraining() throws ParseException {
        Document doc = setupBPDocument("20070101", "20070115");
        List<RapidBean> trdates = (List<RapidBean>) doc.findBeansByType("org.rapidbeans.clubadmin.domain.TrainingDate");
        assertEquals(2, trdates.size());
        List<RapidBean> trs = (List<RapidBean>) doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training");
        assertEquals(5, trs.size());
        TrainingDate td1 = (TrainingDate) trdates.get(0);
        assertEquals("FCK/football/monday_19:30_Hall", td1.getIdString());
        td1.delete();
        trdates = (List<RapidBean>) doc.findBeansByType("org.rapidbeans.clubadmin.domain.TrainingDate");
        assertEquals(1, trdates.size());
        trs = (List<RapidBean>) doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training");
        assertEquals(2, trs.size());
        trdates.get(0).delete();
        trdates = (List<RapidBean>) doc.findBeansByType("org.rapidbeans.clubadmin.domain.TrainingDate");
        assertEquals(0, trdates.size());
        trs = (List<RapidBean>) doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training");
        assertEquals(0, trs.size());
    }

//    /*
//     * Test method for 'org.rapidbeans.clubadmin.domain.TrainingsList.TrainingsList(String)'
//     */
//    public void testTrainingsListString() throws ParseException {
//        final String s = "\"20060101\" \"20060331\"";
//        final TrainingsList period = new TrainingsList(s);
//        assertEquals(DFDATE.parse("01.01.2006"), period.getFrom());
//        assertEquals(DFDATE.parse("31.03.2006"), period.getTo());
//    }
//
//    /*
//     * Test method for 'org.rapidbeans.clubadmin.domain.TrainingsList.TrainingsList(String[])'
//     */
//    public void testTrainingsListStringArray() throws ParseException {
//        final String[] sa = { "20060101", "20060331" };
//        final TrainingsList period = new TrainingsList(sa);
//        assertEquals(DFDATE.parse("01.01.2006"), period.getFrom());
//        assertEquals(DFDATE.parse("31.03.2006"), period.getTo());
//    }


    /**
     * Set up a small test billing period document.
     *
     * 1 club "FCK"
     * 1 department "Football"
     * 2 training dates "monday"   19:30 - 21:30
     *                  "thursday" 20:00 - 21:30
     * @param dFrom from date
     * @param dTo to date
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
