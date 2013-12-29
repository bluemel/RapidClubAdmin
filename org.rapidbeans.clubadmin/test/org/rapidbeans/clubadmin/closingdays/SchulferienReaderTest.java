package org.rapidbeans.clubadmin.closingdays;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.rapidbeans.clubadmin.domain.ClosingPeriod;

public class SchulferienReaderTest {

    @Test
    public void testReadSchulferienBayern() throws IOException {
        File testfile = new File("testdata/holidays/SchulferienBayern.html");
        // final List<ClosingPeriod> result = new
        // SchulferienReader().readSchulferien(new FileInputStream(testfile));
    }
}
