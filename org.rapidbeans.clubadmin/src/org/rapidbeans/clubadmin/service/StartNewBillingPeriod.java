/*
 * Rapid Beans Framework: StartNewBillingPeriod.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 30.03.2008
 */

package org.rapidbeans.clubadmin.service;

import java.rmi.UnknownHostException;

import javax.swing.JComponent;

import org.rapidbeans.clubadmin.domain.BackupMode;
import org.rapidbeans.clubadmin.domain.BillingPeriod;
import org.rapidbeans.clubadmin.domain.MasterData;
import org.rapidbeans.clubadmin.presentation.MenuHelper;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.exception.LocalizedException;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.core.exception.UtilException;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.presentation.swing.DocumentTreeViewSwing;
import org.rapidbeans.service.Action;
import org.rapidbeans.service.ActionArgument;


/**
 * The action to start a new billing period.
 *
 * @author Martin Bluemel
 */
public class StartNewBillingPeriod extends Action {

    /**
     * Drives the action to start a new billing period.
     */
    public void execute() {
        final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();
        final RapidBeansLocale locale = client.getCurrentLocale();
        try {
            // (a bit of business logic) delete old billing period
            final MasterData md = client.getMasterData();
            final BillingPeriod currentBp = md.getCurrentbillingperiod();
            if (currentBp != null) {
                if (!client.messageYesNo("Bitte bestätigen Sie den korrekten Abschluss\n"
                        + "des bisher bearbeiteten Abrechnungszeitraum.",
                        "Neuer Abrechnungszeitraum")) {
                    client.messageInfo(
                            "Anlegen eine neuen Abrechnungszeitraums"
                            + " abgebrochen", "Neuer Abrechnungszeitraum");
                    return;
                }
                Backup backupAction = new Backup();
                ActionArgument arg = new ActionArgument();
                arg.setName("mode");
                arg.setValue(BackupMode.finalmode.toString());
                backupAction.addArgument(arg);
                backupAction.execute();

                currentBp.delete();
                MenuHelper.updateSpecificMenus();
            }
            if (md.getCurrentbillingperiod() != null) {
                throw new RapidBeansRuntimeException("current bp unexpectedly not null");
            }

            final DocumentView view = client.openDocumentView(
                    client.getMasterDoc(), "masterdata", "billingperiod"/*, filter*/);
            final DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) view.getTreeView();
            treeView.getTree().setSelectionPath(treeView.getTree().getPathForRow(10));
            treeView.createBean();
            ((JComponent) treeView.getWidget()).setVisible(false);
        } catch (UtilException e) {
            if (e.getCause() instanceof UnknownHostException) {
                client.messageError(locale.getStringMessage("error.load.file.web.masterdata"),
                        locale.getStringMessage("error.load.file.web.title"));
            } else {
                throw e;
            }
        } catch (LocalizedException e) {
            e.present();
            client.popupProgramSettings("localmasterdatafile");
        }
    }
}
