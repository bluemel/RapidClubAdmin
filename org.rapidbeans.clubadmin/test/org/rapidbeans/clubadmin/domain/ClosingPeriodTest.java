/*
 * RapidClubAdmin Application: ClosingPeriodTest.java
 *
 * Copyright Martin Bluemel, 2006
 *
 * 14.10.2006
 */
package org.rapidbeans.clubadmin.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;

import org.rapidbeans.clubadmin.domain.ClosingPeriod;
import org.rapidbeans.core.exception.ValidationException;

import junit.framework.TestCase;

/**
 * @author Martin Bluemel
 */
public class ClosingPeriodTest extends TestCase {

    /**
     * Test method for {@link org.rapidbeans.clubadmin.domain.ClosingPeriod#ClosingPeriod()}.
     */
    public void testClosingPeriod() {
        ClosingPeriod cp = new ClosingPeriod();
        assertEquals(null, cp.getName());
        assertEquals(null, cp.getFrom());
        assertEquals(null, cp.getTo());
    }

    /**
     * Test constructing a valid closing period.
     *
     * @throws ParseException if parsing the test dates fails
     */
    public void testClosingPeriodValid() throws ParseException {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);
        ClosingPeriod cp = new ClosingPeriod("\"20061014\" \"test\" \"20061018\"");
        assertEquals("test", cp.getName());
        assertEquals(df.parse("14.10.2006"), cp.getFrom());
        assertEquals(df.parse("18.10.2006"), cp.getTo());
        assertEquals(false, cp.getOneday());
    }

    /**
     * Test constructing a valid closing period
     * giving same "from" and "to" date.
     * Attribute "oneday" is supposed to be 'true' automatically.
     *
     * @throws ParseException if parsing the test dates fails
     */
    public void testCreateClosingPeriodFromToSame() throws ParseException {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);
        ClosingPeriod cp = new ClosingPeriod("\"20061014\" \"test\" \"20061014\"");
        assertEquals("test", cp.getName());
        assertEquals(df.parse("14.10.2006"), cp.getFrom());
        assertEquals(df.parse("14.10.2006"), cp.getTo());
        assertEquals(true, cp.getOneday());
    }

    /**
     * Test constructing a valid closing period
     * giving different "from" and "to" date.
     * Attribute "oneday" is supposed to be 'false' automatically.
     *
     * @throws ParseException if parsing the test dates fails
     */
    public void testCreateClosingPeriodFromToDifferent() throws ParseException {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);
        ClosingPeriod cp = new ClosingPeriod("\"20061014\" \"test\" \"20061015\"");
        assertEquals("test", cp.getName());
        assertEquals(df.parse("14.10.2006"), cp.getFrom());
        assertEquals(df.parse("15.10.2006"), cp.getTo());
        assertEquals(false, cp.getOneday());
    }

    /**
     * Test constructing a valid closing period
     * giving different "from" and "to" and setting also "oneday" = 'false'
     * accordingly.
     *
     * @throws ParseException if parsing the test dates fails
     */
    public void testCreateClosingPeriodOnedayFalse() throws ParseException {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);
        ClosingPeriod cp = new ClosingPeriod("\"20061014\" \"test\" \"20061015\" \"false\"");
        assertEquals("test", cp.getName());
        assertEquals(df.parse("14.10.2006"), cp.getFrom());
        assertEquals(df.parse("15.10.2006"), cp.getTo());
        assertEquals(false, cp.getOneday());
    }

    /**
     * Test constructing a valid closing period
     * giving different "from" and "to" but setting also "oneday" = 'true' 
     * Attribute "to" is supposed to be same as from.
     *
     * @throws ParseException if parsing the test dates fails
     */
    public void testCreateClosingPeriodOnedayTrue() throws ParseException {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);
        ClosingPeriod cp = new ClosingPeriod("\"20061014\" \"test\" \"20061015\" \"true\"");
        assertEquals("test", cp.getName());
        assertEquals(df.parse("14.10.2006"), cp.getFrom());
        assertEquals(df.parse("14.10.2006"), cp.getTo());
        assertEquals(true, cp.getOneday());
    }

    /**
     * Setting "oneday" to 'true' makes the to date equals to
     * the "from" date.
     *
     * @throws ParseException if parsing the test dates fails
     */
    public void testSetOnedayTrue() throws ParseException {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);
        ClosingPeriod cp = new ClosingPeriod("\"20061014\" \"test\" \"20061015\"");
        assertEquals(df.parse("14.10.2006"), cp.getFrom());
        assertEquals(df.parse("15.10.2006"), cp.getTo());
        assertEquals(false, cp.getOneday());
        cp.setOneday(true);
        assertEquals(df.parse("14.10.2006"), cp.getTo());
        assertEquals(true, cp.getOneday());
    }

//    /**
//     * Setting "oneday" to 'false' is not possible if two different
//     * dates are defined.
//     *
//     * @throws ParseException if parsing the test dates fails
//     */
//    public void testSetOnedayFalse() throws ParseException {
//        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);
//        ClosingPeriod cp = new ClosingPeriod("\"20061014\" \"test\" \"20061014\"");
//        assertEquals(df.parse("14.10.2006"), cp.getFrom());
//        assertEquals(df.parse("14.10.2006"), cp.getTo());
//        assertEquals(true, cp.getOneday());
//        try {
//            cp.setOneday(false);
//            fail("Expected ValidationException");
//        } catch (ValidationException e) {
//            assertTrue(true);
//        }
//    }

    /**
     * Test constructing a date with invalid single property.
     *
     * @throws ParseException if parsing the test dates fails
     */
    public void testClosingPeriodInvalidSingleProperty() throws ParseException {
        try {
            new ClosingPeriod("\"2006101x\" \"test\"");
            fail("ecpected ValidationException");
        } catch (ValidationException e) {
            assertTrue(true);
        }
    }

    /**
     * Test constructing a date with an invalid combination
     * of properties (from < to).
     *
     * @throws ParseException if parsing the test dates fails
     */
    public void testClosingPeriodInvalidCombiProperty() throws ParseException {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);
        ClosingPeriod cp = new ClosingPeriod("\"20061018\" \"test\"");
        try {
            cp.setTo(df.parse("17.10.2006"));
            fail("expected ValidationException");
        } catch (ValidationException e) {
            assertTrue(true);
        }
    }
}
