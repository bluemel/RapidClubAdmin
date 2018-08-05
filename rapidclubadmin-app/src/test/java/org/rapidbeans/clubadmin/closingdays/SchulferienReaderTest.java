package org.rapidbeans.clubadmin.closingdays;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.rapidbeans.clubadmin.domain.ClosingPeriod;
import org.rapidbeans.core.basic.PropertyDate;
import org.rapidbeans.core.common.PrecisionDate;

public class SchulferienReaderTest {

	@Test
	public void testReadSchulferienBayern() throws IOException {
		// http://www.schulferien.org/Bayern/bayern.html
		File testfile = new File("src/test/resources/holidays/SchulferienBayern.html");
		final List<ClosingPeriod> result = new SchulferienReader().readSchulferien(new FileInputStream(testfile),
				"2014");
		assertEquals(7, result.size());
		assertEquals("20141027_Herbstferien", result.get(5).getIdString());
		assertEquals("Herbstferien", result.get(5).getName());
		assertEquals("20141027", PropertyDate.format(result.get(5).getFrom(), PrecisionDate.day));
		assertEquals("20141031", PropertyDate.format(result.get(5).getTo(), PrecisionDate.day));
		assertEquals(false, result.get(5).getOneday());
	}

	@Test
	public void testReadFeiertageBayern() throws IOException {
		// http://www.schulferien.org/Feiertage/2013/feiertage_2013.html
		File testfile = new File("src/test/resources/holidays/Feiertage2013.html");
		final List<ClosingPeriod> result = new SchulferienReader().readFeiertage(new FileInputStream(testfile),
				"bayern");
		assertEquals(13, result.size());
		assertEquals("2. Weihnachtstag", result.get(12).getName());
		assertEquals("20131226", PropertyDate.format(result.get(12).getFrom(), PrecisionDate.day));
		assertEquals("20131226", PropertyDate.format(result.get(12).getTo(), PrecisionDate.day));
		assertEquals(true, result.get(12).getOneday());
	}

	@Test
	@Ignore
	public void testReadFromSchulferienOrg() {
		final List<ClosingPeriod> result = new SchulferienReader().readSchulferienAndFeiertage("www.schulferien.org",
				"bayern", "2014");
		assertEquals(20, result.size());
	}
}
