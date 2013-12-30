/*
 * Rapid Beans Framework: ReadClosingDays.java
 *
 * Copyright Martin Bluemel, 2013
 *
 * 31.12.2013
 */

package org.rapidbeans.clubadmin.service;

import java.util.List;

import org.rapidbeans.clubadmin.closingdays.SchulferienReader;
import org.rapidbeans.clubadmin.domain.ClosingPeriod;
import org.rapidbeans.clubadmin.domain.MasterData;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.service.Action;

/**
 * The action to restore all application data.
 * 
 * @author Martin Bluemel
 */
public class ReadClosingDays extends Action {

    /**
     * open the training editing view.
     */
    public void execute() {
        final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();
        final MasterData masterdata = app.getMasterData();
        final List<ClosingPeriod> newClosingPeriods = new SchulferienReader().readSchulferienAndFeiertage(
                "www.schulferien.org", "bayern", "2014");
        for (final ClosingPeriod cp : newClosingPeriods) {
            if (!masterdata.getClosingperiods().contains(cp)) {
                masterdata.addClosingperiod(cp);
            }
        }
    }
}
