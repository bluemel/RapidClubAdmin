package org.rapidbeans.clubadmin.closingdays;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.rapidbeans.clubadmin.domain.ClosingPeriod;
import org.rapidbeans.core.basic.PropertyDate;
import org.rapidbeans.core.common.PrecisionDate;

public class SchulferienReaderTest {

    @Test
    public void testReadSchulferienBayern() throws IOException {
        // http://www.schulferien.org/Bayern/bayern.html
        File testfile = new File("testdata/holidays/SchulferienBayern.html");
        final List<ClosingPeriod> result = new SchulferienReader().readSchulferien(new FileInputStream(testfile),
                "2014");
        Assert.assertEquals(7, result.size());
        Assert.assertEquals("20141027_Herbstferien", result.get(5).getIdString());
        Assert.assertEquals("Herbstferien", result.get(5).getName());
        Assert.assertEquals("20141027", PropertyDate.format(result.get(5).getFrom(), PrecisionDate.day));
        Assert.assertEquals("20141031", PropertyDate.format(result.get(5).getTo(), PrecisionDate.day));
        Assert.assertEquals(false, result.get(5).getOneday());
    }

    @Test
    public void testReadFeiertageBayern() throws IOException {
        // http://www.schulferien.org/Feiertage/2013/feiertage_2013.html
        File testfile = new File("testdata/holidays/Feiertage2013.html");
        final List<ClosingPeriod> result = new SchulferienReader().readFeiertage(new FileInputStream(testfile),
                "bayern");
        Assert.assertEquals(13, result.size());
        Assert.assertEquals("2. Weihnachtstag", result.get(12).getName());
        Assert.assertEquals("20131226", PropertyDate.format(result.get(12).getFrom(), PrecisionDate.day));
        Assert.assertEquals("20131226", PropertyDate.format(result.get(12).getTo(), PrecisionDate.day));
        Assert.assertEquals(true, result.get(12).getOneday());
    }

    @Test
    @Ignore
    public void testReadFromSchulferienOrg() {
        final List<ClosingPeriod> result = new SchulferienReader().readSchulferienAndFeiertage("www.schulferien.org",
                "bayern", "2014");
        Assert.assertEquals(20, result.size());
    }
}
