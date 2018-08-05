/*
 * Rapid Beans Framework: OpenMasterdata.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 04.01.2005
 */

package org.rapidbeans.clubadmin.service;

import java.net.UnknownHostException;

import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.exception.LocalizedException;
import org.rapidbeans.core.exception.UtilException;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.service.Action;


/**
 * The action to open the masterdata document.
 *
 * @author Martin Bluemel
 */
public class OpenMasterdata extends Action {

    /**
     * Drives the action to open the masterdata document.
     */
    public void execute() {
        final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();
//        Role role = null;
//        if (client.getAuthenticatedUser() != null
//                && client.getAuthenticatedUser().getRoles() != null
//                && client.getAuthenticatedUser().getRoles().size() > 0) {
//            role = client.getAuthenticatedUser().getRoles().get(0);
//        }
        final RapidBeansLocale locale = client.getCurrentLocale();
        try {
//            final Filter filter = new Filter();
//            filter.addIncludes("org.rapidbeans.clubadmin.domain.ClosingPeriod");
//            filter.addIncludes("org.rapidbeans.clubadmin.domain.Club");
//            filter.addIncludes("org.rapidbeans.clubadmin.domain.Department");
//            filter.addIncludes("org.rapidbeans.clubadmin.domain.Location");
//            filter.addIncludes("org.rapidbeans.clubadmin.domain.Salary");
//            filter.addIncludes("org.rapidbeans.clubadmin.domain.SalaryComponentType");
//            filter.addIncludes("org.rapidbeans.clubadmin.domain.Sport");
//            filter.addIncludes("org.rapidbeans.clubadmin.domain.Trainer");
//            filter.addIncludes("org.rapidbeans.clubadmin.domain.TrainerAttribute");
//            filter.addIncludes("org.rapidbeans.clubadmin.domain.TrainerPlanning");
//            filter.addIncludes("org.rapidbeans.clubadmin.domain.TrainerRole");
//            filter.addIncludes("org.rapidbeans.clubadmin.domain.TrainingDate");
//            filter.addIncludes("org.rapidbeans.clubadmin.domain.ClubadminUser");
//            filter.applyFilter(client.getMasterDoc(), true);
            client.openDocumentView(
                    client.getMasterDoc(), "masterdata", "standard"/*, filter*/);
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
