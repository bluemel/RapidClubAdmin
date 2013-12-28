/*
 * Rapid Beans Clubadmin Application: SettingsTest.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * Jun 7, 2008
 */
package org.rapidbeans;

import java.io.File;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.settings.SettingsAll;
import org.rapidbeans.presentation.settings.SettingsAuthn;

/**
 * Unit tests for Settings.
 * 
 * @author Martin Bluemel
 */
public class SettingsTest extends TestCase {

    /**
     * Read a settings document with empty <b>&lt;authn/&gt;</b> element.
     */
    public void testReadSettingsAuthnEmpty() {
        File testfolder = new File("testdata/.rapidclubadmin");
        try {
            if (!testfolder.exists()) {
                Assert.assertTrue(testfolder.mkdir());
            }
            Document doc = new Document(new File("testdata/settingsAuthnEmpty.xml"));
            final SettingsAll settings = (SettingsAll) doc.getRoot();
            SettingsAuthn settingsAuthn = settings.getAuthn();
            assertNull(settingsAuthn.getCred());
        } finally {
            if (testfolder.exists()) {
                Assert.assertTrue(testfolder.delete());
            }
        }
    }
}
