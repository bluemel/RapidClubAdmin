/*
 * Rapid Club Admin Application: TrainerIconManager.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 15.03.2008
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.presentation.IconInfoList;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.clubadmin.presentation.Settings;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.config.ConfigApplication;

/**
 * @author Martin Bluemel
 */
public class TrainerIconManagerTest extends TestCase {

    private TrainerIconManager iconManager = null;

    private RapidClubAdminClient applicationMock = null;

    public void setUp() {
        if (this.applicationMock == null) {
            Settings settings = new Settings();
            Document settingsDoc = new Document("settings", settings);
            ConfigApplication cfg = new ConfigApplication();
            this.applicationMock = new RapidClubAdminClient() {
                public boolean getTestMode() {
                    return true;
                }
            };
            this.applicationMock.setConfiguration(cfg);
            this.applicationMock.setSettingsDoc(settingsDoc);
        }
        FileHelper.copyDeep(new File("testdata/iconmanager/iconrepotestdata"),
                new File("testdata/iconmanager/iconrepository"), false, true);
        if (new File("testdata/iconmanager/iconcache").exists()) {
            FileHelper.deleteDeep(new File("testdata/iconmanager/iconcache"));
        }
        FileHelper.mkdirs(new File("testdata/iconmanager/iconcache"));
        this.iconManager = new TrainerIconManager(
                this.applicationMock, TrainerIconManager.ICON_REPOSITORY_TYPE_LOCAL,
                "testdata/iconmanager/iconrepository",
                "testdata/iconmanager/iconrepository/iconlist.xml",
                "testdata/iconmanager/iconcache");
    }

    public void tearDown() {
        iconManager = null;
        FileHelper.deleteDeep(new File("testdata/iconmanager/iconrepository"));
        FileHelper.deleteDeep(new File("testdata/iconmanager/iconcache"));
    }


    /**
     * Test method for TrainerIconManager#updateIcons().
     */
    public void testUpdateIcons() {
        final String[][] trainerNames = {
                { "Doe", "John" },  
                { "Miller", "Art" },  
                { "Smith", "James" }
        };
        File i1 = new File("testdata/iconmanager/iconcache/Doe_John_.jpg");
        File i3 = new File("testdata/iconmanager/iconcache/Smith_James_.jpg");
        assertFalse(i1.exists());
        assertFalse(i3.exists());

        // first update should download the icon files to the cache
        iconManager.updateIcons(createTrainersList(trainerNames), false);
        assertTrue(i1.exists());
        long i1LastModified = i1.lastModified();
        assertTrue(i3.exists());
        long i3LastModified = i3.lastModified();

        // second update should do nothing because the files
        // are up to date
        iconManager.updateIcons(createTrainersList(trainerNames), false);
        assertTrue(i1.exists());
        assertEquals(i1LastModified, i1.lastModified());
        assertTrue(i3.exists());
        assertEquals(i3LastModified, i3.lastModified());
    }

    /**
     * Test method for {@link org.rapidbeans.clubadmin.presentation.swing.TrainerIconManager#importIcon(org.rapidbeans.clubadmin.domain.Trainer, java.io.File)}.
     */
    public void testImportIcon() {
        final String[][] trainerNames = {
                { "Doe", "John" },  
                { "Miller", "Art" },  
                { "Smith", "James" }
        };
        iconManager.updateIcons(createTrainersList(trainerNames), false);

        File importTargetCache = new File(
            "testdata/iconmanager/iconcache/Nemo_Captain_.jpg");
        File importTargetRepository = new File(
            "testdata/iconmanager/iconrepository/Nemo_Captain_.jpg");
        assertFalse(importTargetCache.exists());
        assertFalse(importTargetRepository.exists());
        IconInfoList list = (IconInfoList) iconManager.getIconDoc().getRoot();
        assertEquals(3, list.getIcons().size());
        Trainer trainer = new Trainer(new String[]{"Nemo", "Captain"});
        assertNull(list.getIconInfo(trainer));

        File importSource = new File(
                "testdata/iconmanager/iconrepotestdata/Captain.jpg");
        iconManager.importIcon(trainer,
                importSource);

        assertTrue(importTargetCache.exists());
        assertTrue(importTargetRepository.exists());
        list = (IconInfoList) iconManager.getIconDoc().getRoot();
        assertEquals(4, list.getIcons().size());
        assertEquals("Nemo_Captain_", list.getIconInfo(trainer).getTrainerid());
    }

    /**
     * Test method for {@link org.rapidbeans.clubadmin.presentation.swing.TrainerIconManager#deleteIcon(org.rapidbeans.clubadmin.domain.Trainer)}.
     */
    public void testDeleteIcon() {
        final String[][] trainerNames = {
                { "Doe", "John" },  
                { "Miller", "Art" },  
                { "Smith", "James" }
        };
        iconManager.updateIcons(createTrainersList(trainerNames), false);

        File importTargetCache = new File(
            "testdata/iconmanager/iconcache/Doe_John_.jpg");
        File importTargetRepository = new File(
            "testdata/iconmanager/iconrepository/Doe_John_.jpg");
        assertTrue(importTargetCache.exists());
        assertTrue(importTargetRepository.exists());
        IconInfoList list = (IconInfoList) iconManager.getIconDoc().getRoot();
        assertEquals(3, list.getIcons().size());
        Trainer trainer = new Trainer(new String[]{"Doe", "John"});
        assertNotNull(list.getIconInfo(trainer));

        iconManager.deleteIcon(trainer);

        assertFalse(importTargetCache.exists());
        assertFalse(importTargetRepository.exists());
        list = (IconInfoList) iconManager.getIconDoc().getRoot();
        assertEquals(2, list.getIcons().size());
        assertNull(list.getIconInfo(trainer));
    }

    private List<Trainer> createTrainersList(final String[][] trainerNames) {
        final List<Trainer> trainers = new ArrayList<Trainer>();
        for (String[] sa : trainerNames) {
            trainers.add(new Trainer(sa));
        }
        return trainers;
    }
}
