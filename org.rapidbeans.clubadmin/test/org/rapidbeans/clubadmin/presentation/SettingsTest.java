/*
 * RapidClubAdmin: SettingsTest.java
*
* Copyright Martin Bluemel, 2007
*
* Jan 4, 2007
*/
package org.rapidbeans.clubadmin.presentation;

import junit.framework.TestCase;

import org.rapidbeans.datasource.CharsetsAvailable;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DefaultEncodingUsage;
import org.rapidbeans.presentation.OpenWindowBehaviour;
import org.rapidbeans.presentation.settings.SettingsBasicGui;

/**
 * Unit tests for class Settings.
 *
 * @author Martin Bluemel
 */
public final class SettingsTest extends TestCase {

    /**
     * Test method for constructor Settings().
     */
    public void testSettings() {
        ApplicationManager.resetApplication();
        Settings settings = new Settings();
        assertNotNull(settings);
        SettingsBasic basic = settings.getBasic();
        assertEquals(CharsetsAvailable.UTF_8, basic.getDefaultencoding());
        assertEquals(DefaultEncodingUsage.write, basic.getDefaultencodingusage());
        SettingsBasicGui gui = basic.getGui();
        assertSame(OpenWindowBehaviour.maximized, gui.getDocViewOpenWindowBehaviour());
        RapidClubAdminSettings ecaSettings = settings.getSettings();
        assertNull(ecaSettings.getBackupfolder());
    }
}
