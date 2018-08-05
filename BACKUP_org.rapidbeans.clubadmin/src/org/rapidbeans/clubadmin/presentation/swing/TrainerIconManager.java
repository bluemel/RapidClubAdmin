/*
 * Rapid Club Admin Application: TrainerIconManager.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 06.10.2007
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.rapidbeans.clubadmin.domain.Club;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.presentation.IconInfo;
import org.rapidbeans.clubadmin.presentation.IconInfoList;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.settings.SettingsAll;

/**
 * Manages an caches Trainer icons.
 * Mirrors icons between a (remote) repository and a
 * local folder.
 *
 * @author Martin Bluemel
 */
public class TrainerIconManager {

    private static final Logger log = Logger.getLogger(
            TrainerIconManager.class.getName()); 

    public static final int ICON_REPOSITORY_TYPE_LOCAL = 1;

    public static final int ICON_REPOSITORY_TYPE_REMOTE = 2;

    /**
     * the application instance
     */
    private RapidClubAdminClient application = null;

    /**
     * flag to indicate local or remote icon repository.
     */
    private int iconRepositoryType = ICON_REPOSITORY_TYPE_REMOTE;

    /**
     * the icon repository's relative path.
     */
    private String iconRepositoryPath = "trainerIcons";

    /**
     * The icon list document's relative path
     */
    private String iconDocPath = "trainerIcons/iconlist.xml";

    /**
     * the local folder.
     */
    private File localIconCache = null;

     /**
     * dimension of the squared trainer icon.
     */
    public static final int ICON_DIM = 70;

    /**
     * the icon caching map.
     */
    private Map<String, CachedIcon> iconMap = new HashMap<String, CachedIcon>();

    /**
     * The constructor.
     *
     * @param app the application instance
     * @param iconRepoType LOCAL or REMOTE
     *        Defaults to REMOTE
     * @param iconRepository the relative path the icon repository folder.
     *        Defaults to to "trainerIcons"
     * @param iconListDoc the relative path of the icon list file.
     *        Defaults to "trainerIcons/iconlist.xml" if null
     * @param localIconCache the relative path to the local icon cache folder.
     *        Defaults to "&lt;settings dir&gt;/trainerIcons" if null
     */
    public TrainerIconManager(
            final RapidClubAdminClient app,
            final int iconRepoType,
            final String iconRepository,
            final String iconListDoc,
            final String localIconCache) {
        if (app == null) {
            this.application =
                ((RapidClubAdminClient) ApplicationManager.getApplication());
        } else {
            this.application = app;
        }
        this.iconRepositoryType = iconRepoType;
        if (iconRepository != null) {
            this.iconRepositoryPath = iconRepository;
            if (iconRepoType == ICON_REPOSITORY_TYPE_LOCAL) {
                final File f = new File(this.iconRepositoryPath);
                if (!f.exists()) {
                    throw new RapidBeansRuntimeException(
                            "file not found \"" + f.getAbsolutePath() + "\"");
                }
                if (!f.isDirectory()) {
                    throw new RapidBeansRuntimeException(
                            "file is not a folder \"" + f.getAbsolutePath() + "\"");                
                }
            }
        }
        if (iconListDoc != null) {
            this.iconDocPath = iconListDoc;
            if (iconRepoType == ICON_REPOSITORY_TYPE_LOCAL) {
                final File f = new File(this.iconDocPath);
                if (!f.exists()) {
                    throw new RapidBeansRuntimeException(
                            "file not found \"" + f.getAbsolutePath() + "\"");
                }
                if (!f.isFile()) {
                    throw new RapidBeansRuntimeException(
                            "file is not a normal file \"" + f.getAbsolutePath() + "\"");                
                }
            }
        }
        if (localIconCache != null) {
            this.localIconCache = new File(localIconCache);
            if (!this.localIconCache.exists()) {
                throw new RapidBeansRuntimeException(
                        "file not found \""
                        + this.localIconCache.getAbsolutePath()
                        + "\"");
            }
            if (!this.localIconCache.isDirectory()) {
                throw new RapidBeansRuntimeException(
                        "file is not a folder \""
                        + this.localIconCache.getAbsolutePath()
                        + "\"");                
            }
        }
    }

    /**
     * the default icon.
     */
    private static ImageIcon defaultIcon = null;

    static {
        try {
            defaultIcon = new ImageIcon(
                    ImageIO.read(Application.class.getResource("pictures/team1.gif"))
                        .getScaledInstance(ICON_DIM, ICON_DIM, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            throw new RapidBeansRuntimeException(e);
        }
    }

    /**
     * Determines the icon file for a specific Trainer.
     *
     * @param trainerId the Trainer's identity.
     *
     * @return the trainer's value.
     */
    public File getIconFile(final String trainerId) {
        return new File(getLocalIconCache(), trainerId + ".jpg");
    }

    /**
     * @return the defaultIcon
     */
    public static ImageIcon getDefaultIcon() {
        return defaultIcon;
    }

    /**
     * Get an appropriate icon for this trainer.
     *
     * @param trainer the trainer for which you want an icon
     *
     * @return the icon
     */
    public ImageIcon get(final Trainer trainer) {
        ImageIcon trainerIcon = defaultIcon;
        if (trainer != null) {
            final String trainerId = trainer.getIdString();
            final File iconFile = getIconFile(trainerId);
            if (iconFile.exists()) {
                try {
                    final CachedIcon cachedIcon = this.iconMap.get(trainerId);
                    if (cachedIcon == null
                            || cachedIcon.getLastModified() < iconFile.lastModified()) {
                        trainerIcon = new ImageIcon(readScaledLocalFile(iconFile));
                        final CachedIcon newCachedIcon = new CachedIcon(
                                trainerIcon, iconFile.lastModified());
                        this.iconMap.put(trainerId, newCachedIcon);
                    } else {
                        trainerIcon = cachedIcon.getIcon();
                    }
                } catch (IOException e) {
                    throw new RapidBeansRuntimeException(e);
                }
            }
        }
        return trainerIcon;
    }

    /**
     * Import an icon for the specified trainer.
     *
     * @param trainer the trainer for which you want to set the icon
     * @param iconfile the icon file
     *
     * @return the icon
     */
    public void importIcon(final Trainer trainer, final File iconfile) {
        final File icontarget = new File(getLocalIconCache(), trainer.getIdString() + ".jpg");
        try {
            BufferedImage image = this.readScaledLocalFile(iconfile);
            ImageIO.write(image, "jpeg", icontarget);
        } catch (IOException e) {
            throw new RapidBeansRuntimeException(e);
        }
        if (this.application.getSettingsRapidClubAdmin().getShareiconsoverweb()) {
            final Document iconDoc = getIconDoc();
            if (this.iconRepositoryType == ICON_REPOSITORY_TYPE_REMOTE) {
                this.application.getWebFileManager().uploadFileFtp(icontarget,
                    this.iconRepositoryPath + "/" + icontarget.getName(), null, null);
            } else {
                FileHelper.copyFile(icontarget,
                        new File(this.iconRepositoryPath, icontarget.getName()),
                        true);
            }
            IconInfoList iconInfoList = (IconInfoList) iconDoc.getRoot();
            if (iconInfoList.getIcons() == null) {
                iconInfoList.setIcons(new ArrayList<IconInfo>());
            }
            log.fine("import icon: loaded icon list document, found "
                    + iconInfoList.getIcons().size() + " icons");
            iconInfoList.markIconUpdate(trainer);
            this.application.save(iconDoc);
            log.fine("import icon: wrote icon list document \""
                    + iconDoc.getUrl() + "\"");
        }
    }

    /**
     * Delete the icon for the specified trainer.
     *
     * @param trainer the trainer for which you want to set the icon
     * @param iconfile the icon file
     *
     * @return the icon
     */
    public void deleteIcon(final Trainer trainer) {
        final File iconFile = new File(getLocalIconCache(),
                trainer.getIdString() + ".jpg");
        FileHelper.deleteDeep(iconFile, true);
        while (iconFile.exists()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RapidBeansRuntimeException(e);
            }
        }
        this.getIconFile(trainer.getIdString()).delete();
        this.iconMap.remove(trainer.getIdString());
        if (this.application.getSettingsRapidClubAdmin().getShareiconsoverweb()) {
            final Document iconDoc = getIconDoc();
            IconInfoList iconInfoList = (IconInfoList) iconDoc.getRoot();
            if (iconInfoList.getIcons() == null) {
                iconInfoList.setIcons(new ArrayList<IconInfo>());
            }
            log.fine("delete icon: loaded icon list document, found "
                    + iconInfoList.getIcons().size() + " icons");
            iconInfoList.removeIcon(iconInfoList.getIconInfo(trainer));
            this.application.save(iconDoc);
            log.fine("delete icon: wrote icon list document \""
                    + iconDoc.getUrl() + "\"");
            if (this.iconRepositoryType == ICON_REPOSITORY_TYPE_REMOTE) {
                // deleting the remote file is currently not supported
            } else {
                new File(this.iconRepositoryPath, trainer.getIdString() + ".jpg").delete();
            }
        }
    }

    /**
     * Update all icons from repository that are not up to date or not yet there.
     * 
     * @param trainers all list of all Trainers to update icons for
     * @param runInBackground if that task should run in background
     */
    public void updateIcons(final Collection<Trainer> trainers,
            final boolean runInBackground) {
        if (!this.application.getSettingsRapidClubAdmin().getShareiconsoverweb()) {
            return;
        }
        if (runInBackground) {
            new Thread() {
                public void run() {
                    runUpdateIcons(trainers);
                }
            }.start();
        } else {
            runUpdateIcons(trainers);
        }
    }

    /**
     * Update all icons from repository that are not up to date or not yet there.
     * 
     * @param trainers all list of all Trainers to update icons for
     */
    private void runUpdateIcons(final Collection<Trainer> trainers) {
        final IconInfoList iconInfoList = (IconInfoList) getIconDoc().getRoot();
        for (final Trainer trainer : trainers) {
            if (!isLocalIconfileUpToDate(iconInfoList, trainer)) {
                final File target = new File(getLocalIconCache(),
                        trainer.getIdString() + ".jpg");
                URL sourceUrl = null;
                try {
                    if (this.iconRepositoryType == ICON_REPOSITORY_TYPE_REMOTE) {
                        sourceUrl = application.getWebFileManager().getHttpUrl(
                                "trainerIcons/" + trainer.getIdString() + ".jpg", null, null);
                        log.fine("downloading file \"" + sourceUrl + "\"");
                        application.getWebFileManager().downloadFileHtpp("trainerIcons/"
                                + trainer.getIdString() + ".jpg",
                                target);
                    } else {
                        final File source = new File(this.iconRepositoryPath
                                + File.separator + trainer.getIdString() + ".jpg");
                        sourceUrl = source.toURI().toURL();
                        log.fine("updating icon from source \""
                                + source.getAbsolutePath() + "\"");
                        FileHelper.copyFile(source, target);
                    }
                } catch (RapidBeansRuntimeException e) {
                    if (e.getCause() instanceof FileNotFoundException) {
                        log.warning("source file \"" + sourceUrl + "\" not found");
                        continue;
                    }
                } catch (MalformedURLException e) {
                    throw new RapidBeansRuntimeException(e);
                }
            }
        }
    }

    private Document iconDoc = null;
    /**
     * Load an icon document or create a new one if it does not exist.
     *
     * @return the icon list document loaded
     */
    protected synchronized Document getIconDoc() {
        if (this.iconDoc == null) {
            try {
                if (this.iconRepositoryType == ICON_REPOSITORY_TYPE_REMOTE) {
                    this.iconDoc = this.application.getWebFileManager().downloadDocument(
                            "iconlist", this.iconDocPath, false, false, false, null, null);
                } else {
                    final File iconDocFile = new File(this.iconDocPath);
                    if (iconDocFile.exists()) {
                        this.iconDoc = new Document("iconlist", iconDocFile);
                    } else {
                        this.iconDoc = createIconDoc(null);
                        this.iconDoc.save();
                    }
                }
            } catch (RapidBeansRuntimeException e) {
                if (e.getCause() instanceof FileNotFoundException) {
                    iconDoc = createIconDoc(e);
                } else {
                    throw e;
                }
            }
        }
        return this.iconDoc;
    }

    private Document createIconDoc(RapidBeansRuntimeException ex) {
        Document iconDoc;
        String msg = "Local ";
        if (this.iconRepositoryType == ICON_REPOSITORY_TYPE_REMOTE) {
            msg = "Remote ";
        }
        log.fine(msg + "icon list file " + this.iconDocPath + " not found.");
        final IconInfoList iconInfoList = new IconInfoList();
        iconInfoList.setIcons(new ArrayList<IconInfo>());
        iconDoc = new Document("iconlist", iconInfoList);
        if (this.iconRepositoryType == ICON_REPOSITORY_TYPE_REMOTE) {
            iconDoc.setUrl(this.application.getWebFileManager().getFtpUrl(
                    this.iconDocPath, null, null));
        } else {
            try {
                iconDoc.setUrl(new File(this.iconDocPath).toURI().toURL());
            } catch (MalformedURLException e1) {
                if (ex == null) {
                    throw new RapidBeansRuntimeException(e1);
                } else {
                    throw new RapidBeansRuntimeException(ex);
                }
            }
        }
        log.fine("Created new icon list file \"" + iconDoc.getUrl() + "\"");
        return iconDoc;
    }

    /**
     * reads an image from a file and adjusts the scale if neccessary
     *
     * @param iconFile the file containing the image
     *
     * @return a squared image with the required dimension
     *
     * @throws IOException when IO fails
     */
    private BufferedImage readScaledLocalFile(final File iconFile)
        throws IOException {
        final BufferedImage origImage = ImageIO.read(iconFile);
        if (origImage == null) {
            throw new RapidBeansRuntimeException("can't read image file " + iconFile.getAbsolutePath());
        }
        if (origImage.getHeight(null) == -1 || origImage.getWidth(null) == -1) {
            throw new RapidBeansRuntimeException("cant determine image dimensions");
        }
        if (origImage.getHeight(null) > ICON_DIM || origImage.getWidth(null) > ICON_DIM) {
            final Image tkImage = origImage.getScaledInstance(
                    ICON_DIM, ICON_DIM, Image.SCALE_SMOOTH);
            final BufferedImage scaledImage = new BufferedImage(ICON_DIM, ICON_DIM,
                    BufferedImage.TYPE_INT_RGB);
            final Graphics g = scaledImage.getGraphics();
            g.drawImage(tkImage, 0, 0, ICON_DIM, ICON_DIM, null);
            g.dispose();
            return scaledImage;
        } else {
            return origImage;
        }
    }

    /**
     * Determines if the local icon file needs a update r not
     * @param infoList the info list
     * @param trainer the trainer
     * @return false if the local icon file needs a update,
     *         true if not
     */
    private boolean isLocalIconfileUpToDate(final IconInfoList infoList, final Trainer trainer) {
        boolean uptodate = false;
        IconInfo info = infoList.getIconInfo(trainer);
        if (info == null) {
            uptodate = true;
        } else {
            final File localfile = new File(getLocalIconCache(), info.getTrainerid() + ".jpg");
            if (localfile.exists()) {
                long remoteFileDate = info.getLastupload().getTime();
                long localFileDate = localfile.lastModified();
                uptodate = (remoteFileDate < localFileDate);
            } else {
                uptodate = false;
            }
        }
        return uptodate;
    }

    /**
     * @return the local icon storage folder
     */
    private File getLocalIconCache() {
        if (this.localIconCache == null) {
            File iconDir = new File(SettingsAll.getDirname()
                    + File.separator + "trainerIcons");
            if (!iconDir.exists()) {
                FileHelper.mkdirs(iconDir);
            }
            this.localIconCache = iconDir;
        }
        return this.localIconCache;
    }

    /**
     *  Create a dummy trainer for downloading the department icons
     *
     * @param department the department to lead the icon for
     *
     * @return the dummy trainer
     */
    public static Trainer createDepartmentIconDummyTrainer(final Department department) {
        return createDepartmentIconDummyTrainer(((Club) department.getParentBean()), department);
    }

    /**
     *  Create a dummy trainer for downloading the department icons
     *
     * @param club the department's club
     * @param department the department to lead the icon for
     *
     * @return the dummy trainer
     */
    public static Trainer createDepartmentIconDummyTrainer(final Club club, final Department department) {
        Trainer departmentIconDummyTrainer = null;
        if (club == null && department == null) {
            departmentIconDummyTrainer = new Trainer(new String[]{
                    "Department",
                    "dummy",
                    "dummy"
            });
        } else if (club == null) {
            departmentIconDummyTrainer = new Trainer(new String[]{
                    "Department",
                    "dummy",
                    department.getName()
            });
        } else if (department == null) {
            departmentIconDummyTrainer = new Trainer(new String[]{
                    "Department",
                    club.getName(),
                    "dummy"
            });
        } else {
            departmentIconDummyTrainer = new Trainer(new String[]{
                    "Department",
                    club.getName(),
                    department.getName()
            });
        }
        return departmentIconDummyTrainer;
    }

    private class CachedIcon {

        /**
         * the icon.
         */
        private ImageIcon icon = null;

        /**
         * modification date at last caching time.
         */
        private long lastModified = -1;

        /**
         * @return the icon
         */
        public ImageIcon getIcon() {
            return icon;
        }

//        /**
//         * @param icon the icon to set
//         */
//        public void setIcon(ImageIcon icon) {
//            this.icon = icon;
//        }

        /**
         * @return the lastModified
         */
        public long getLastModified() {
            return lastModified;
        }

//        /**
//         * @param lastModified the lastModified to set
//         */
//        public void setLastModified(long lastModified) {
//            this.lastModified = lastModified;
//        }

        public CachedIcon(final ImageIcon iicon, final long modified) {
            this.icon = iicon;
            this.lastModified = modified;
        }
    }
}
