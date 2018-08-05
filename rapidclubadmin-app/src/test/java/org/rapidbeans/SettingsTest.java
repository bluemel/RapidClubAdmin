/*
 * Rapid Beans Clubadmin Application: SettingsTest.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * Jun 7, 2008
 */
package org.rapidbeans;

import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Test;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.settings.SettingsAll;
import org.rapidbeans.presentation.settings.SettingsAuthn;

/**
 * Unit tests for Settings.
 * 
 * @author Martin Bluemel
 */
public class SettingsTest {

	/**
	 * Read a settings document with empty <b>&lt;authn/&gt;</b> element.
	 */
	@Test
	public void testReadSettingsAuthnEmpty() {
		File testfolder = new File("src/test/resources/.rapidclubadmin");
		try {
			if (!testfolder.exists()) {
				assertTrue(testfolder.mkdir());
			}
			Document doc = new Document(new File("src/test/resources/settingsAuthnEmpty.xml"));
			final SettingsAll settings = (SettingsAll) doc.getRoot();
			SettingsAuthn settingsAuthn = settings.getAuthn();
			assertNull(settingsAuthn.getCred());
		} finally {
			if (testfolder.exists()) {
				assertTrue(testfolder.delete());
			}
		}
	}

	private void assertTrue(boolean mkdir) {
		// TODO Auto-generated method stub

	}
}
