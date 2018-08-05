package org.rapidbeans.clubadmin.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import junit.framework.TestCase;

/**
 * @author Martin Bluemel
 */
public class TestHelper {

	public static void assertFilesEqual(final File f1, final File f2) {
		LineNumberReader lnr1 = null;
		LineNumberReader lnr2 = null;
		try {
			lnr1 = new LineNumberReader(new InputStreamReader(new FileInputStream(f1)));
			lnr2 = new LineNumberReader(new InputStreamReader(new FileInputStream(f2)));
			String l1, l2;
			while ((l1 = lnr1.readLine()) != null) {
				l2 = lnr2.readLine();
				if (l2 == null) {
					TestCase.fail("Files " + f1.getAbsolutePath() + " and " + f2.getAbsolutePath() + " differ."
							+ " Line " + (lnr1.getLineNumber() - 1) + " stands alone");
				}
				TestCase.assertEquals("Files " + f1.getAbsolutePath() + " and " + f2.getAbsolutePath() + " differ."
						+ " Line " + (lnr1.getLineNumber() - 1) + " is different", l1, l2);
			}
			if (lnr2.readLine() != null) {
				TestCase.fail("Files " + f1.getAbsolutePath() + " and " + f2.getAbsolutePath() + " differ." + " Line "
						+ (lnr2.getLineNumber() - 1) + " stands alone");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (lnr1 != null) {
					lnr1.close();
				}
				if (lnr2 != null) {
					lnr2.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
