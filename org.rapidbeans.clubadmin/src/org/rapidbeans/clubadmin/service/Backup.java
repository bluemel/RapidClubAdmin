/*
 * Rapid Beans Framework: Backup.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 01.08.2008
 */

package org.rapidbeans.clubadmin.service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.rapidbeans.clubadmin.domain.BackupMode;
import org.rapidbeans.clubadmin.domain.BillingPeriod;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.basic.PropertyDate;
import org.rapidbeans.core.common.PrecisionDate;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DefaultEncodingUsage;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.service.Action;


/**
 * The action perform a full backup.
 *
 * @author Martin Bluemel
 */
public class Backup extends Action {

    /**
     * Intermediate (local) or final (web or local) backup.
     */
    public void execute() {

        final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();
        final BillingPeriod bp = app.getMasterData().getCurrentbillingperiod();
        final BackupMode mode = BackupMode.valueOf(this.getArgumentValue("mode"));

        switch (mode) {

        case intermediate:
            final File backupDir = app.getSettingsRapidClubAdmin().getBackupfolder();
            if (!backupDir.exists()) {
                FileHelper.mkdirs(backupDir);
            }
            final String backupDirBpName = bp.getProperty("from").toString()
                    + '_' + bp.getProperty("to").toString()
                    + '_' + PropertyDate.format(new Date(), PrecisionDate.minute);
            final File backupDirBp = new File(backupDir, backupDirBpName);
            if (!backupDirBp.exists()) {
                FileHelper.mkdirs(backupDirBp);
            }
            try {
                final String defaultEncoding = app.getSettings().getBasic().getDefaultencoding().name();
                final boolean forceEncoding = (app.getSettings().getBasic().getDefaultencodingusage() == DefaultEncodingUsage.write);
                String targetFileName = new File(app.getMasterDoc().getUrl().getFile()).getName();
                if (targetFileName.endsWith(";type=i")) {
                    targetFileName = targetFileName.substring(0, targetFileName.length() - 7);
                }
                final URL targetUrl = new File(backupDirBp, targetFileName).toURI().toURL();
                app.getMasterDoc().save(defaultEncoding, forceEncoding, targetUrl);

                if (bp.getDepartments() != null) {
                    for (Department dep : bp.getDepartments()) {
                        Document depTrlistDoc;
                        final DocumentView view = (DocumentView) app.getView(app.getTrainingslistViewname(null, dep));
                        if (view != null) {
                            depTrlistDoc = view.getDocument();
                        } else {
                            depTrlistDoc = app.loadTrainingslistDocument(null, dep);
                        }
                        final File backupFile = app.getCurrentTrainingsDataFileLocal(backupDirBp, null, dep);
                        if (!(backupFile.getParentFile().exists())) {
                            FileHelper.mkdirs(backupFile.getParentFile());
                        }
                        depTrlistDoc.save(defaultEncoding, forceEncoding, backupFile.toURI().toURL());
                    }
                }
            } catch (MalformedURLException e) {
                throw new RapidBeansRuntimeException(e);
            }
            break;

        case finalmode:
            final String backupSubdir = "history/"
                + bp.getProperty("from").toString()
                + '_' + bp.getProperty("to").toString();
            try {
                URL targetUrl = null;
                switch (app.getRunMode()) {
                case local:
                    final String mfilename = app.getMasterDoc().getUrl().getFile().replaceFirst("masterdata.xml",
                            backupSubdir + "/masterdata.xml").replace("%20", " ");
                    final File mfile = new File(mfilename);
                    if (!mfile.getParentFile().exists()) {
                        if (!mfile.getParentFile().mkdirs()) {
                            throw new RapidBeansRuntimeException("Problems to create folder \"" + mfile.getParent()
                                    + "\"");
                        }
                    }
                    targetUrl = mfile.toURI().toURL();
                    break;
                case web:
                    app.getWebFileManager().mkdirs(backupSubdir);
                    targetUrl = new URL(app.getMasterDoc().getUrl().toString().replaceFirst("masterdata.xml",
                            backupSubdir + "/masterdata.xml"));
                    break;
                }

                final URL oldUrl = app.getMasterDoc().getUrl();
                try {
                    app.getMasterDoc().setUrl(targetUrl);
                    app.save(app.getMasterDoc());
                } finally {
                    app.getMasterDoc().setUrl(oldUrl);
                }

                for (Department dep : bp.getDepartments()) {
                    Document depTrlistDoc;
                    final DocumentView view = (DocumentView) app.getView(app.getTrainingslistViewname(null, dep));
                    if (view != null) {
                        depTrlistDoc = view.getDocument();
                    } else {
                        depTrlistDoc = app.loadTrainingslistDocument(null, dep);
                    }
                    if (depTrlistDoc.getUrl().getProtocol().equals("file")) {
                        final String filename = depTrlistDoc.getUrl().getFile().replaceFirst("current", backupSubdir)
                                .replace("%20", " ");
                        final File file = new File(filename);
                        if (!file.getParentFile().exists()) {
                            if (!file.getParentFile().mkdirs()) {
                                throw new RapidBeansRuntimeException("Problems to create folder \"" + file.getParent()
                                        + "\"");
                            }
                        }
                        targetUrl = file.toURI().toURL();
                    } else {
                        targetUrl = new URL(depTrlistDoc.getUrl().toString().replaceFirst("current", backupSubdir));
                    }
                    final String dirs = StringHelper.splitBeforeLast(targetUrl.toString(), "/").replaceFirst(
                            "^.*(history)", "$1");
                    app.getWebFileManager().mkdirs(dirs);
                    depTrlistDoc.setUrl(targetUrl);
                    app.save(depTrlistDoc);
                }
            } catch (MalformedURLException e) {
                throw new RapidBeansRuntimeException(e);
            }
            break;

        default:
            throw new RapidBeansRuntimeException("Unexpected backup mode \"" + mode.toString() + "\"");
        }
    }
}
