package org.rapidbeans.clubadmin.domain;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.util.Locale;

import org.junit.Test;

/**
 * UnitTest for class TrainingDate.
 *
 * @author Martin Bluemel
 */
public class TrainingTest {

	/**
	 * Date formatter.
	 */
	static final DateFormat DFDATE = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);

	/**
	 * Test method for constructor.
	 */
	@Test
	public void testCompareTo() {
		TrainingDate trdate1 = new TrainingDate("\"monday\" \"18:00\" \"XXX\"");
		TrainingRegular tr1 = new TrainingRegular("\"20061014\"");
		trdate1.addTraining(tr1);
		TrainingRegular tr2 = new TrainingRegular("\"20061015\"");
		trdate1.addTraining(tr2);
		TrainingRegular tr3 = new TrainingRegular("\"20061013\"");
		trdate1.addTraining(tr3);
		TrainingDate trdate2 = new TrainingDate("\"tuesday\" \"18:00\" \"XXX\"");
		TrainingRegular tr4 = new TrainingRegular("\"20061014\"");
		trdate2.addTraining(tr4);
		assertEquals(-1, tr1.compareTo(tr2));
		assertEquals(1, tr1.compareTo(tr3));
		assertEquals(-1, tr1.compareTo(tr4));
	}
}
