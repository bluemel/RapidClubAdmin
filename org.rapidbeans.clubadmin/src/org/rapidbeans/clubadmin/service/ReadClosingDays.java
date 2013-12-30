/*
 * Rapid Beans Framework: ReadClosingDays.java
 *
 * Copyright Martin Bluemel, 2013
 *
 * 31.12.2013
 */

package org.rapidbeans.clubadmin.service;

import java.util.ArrayList;
import java.util.List;

import org.rapidbeans.clubadmin.closingdays.SchulferienReader;
import org.rapidbeans.clubadmin.domain.ClosingPeriod;
import org.rapidbeans.clubadmin.domain.Location;
import org.rapidbeans.clubadmin.domain.MasterData;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.basic.PropertyDate;
import org.rapidbeans.core.common.PrecisionDate;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.service.Action;

/**
 * The action to restore all application data.
 * 
 * @author Martin Bluemel
 */
public class ReadClosingDays extends Action {

    /**
     * Import new closing periods of the year 2014. TODO - Jahr einstellen
     * lassen - Containments von Feiertagen ausschließen - GUI-Texte
     */
    public void execute() {
        final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();
        final MasterData masterdata = app.getMasterData();
        final List<ClosingPeriod> foundClosingPeriods = new SchulferienReader().readSchulferienAndFeiertage(
                "www.schulferien.org", "bayern", "2014");
        final StringBuilder cpList = new StringBuilder();
        final List<ClosingPeriod> newClosingPeriods = new ArrayList<ClosingPeriod>();
        for (final ClosingPeriod cp : foundClosingPeriods) {
            if (!masterdata.getClosingperiods().contains(cp)) {
                newClosingPeriods.add(cp);
            }
        }
        if (newClosingPeriods.size() == 0) {
            cpList.append("Es wurden keine neuen Schlie" + Umlaut.SUML + "zeitr" + Umlaut.L_AUML + "me angelegt!");
        } else {
            cpList.append("Folgende Schlie" + Umlaut.SUML + "zeitr" + Umlaut.L_AUML + "me wurden neu angelegt:\n");
            for (final ClosingPeriod cp : newClosingPeriods) {
                masterdata.addClosingperiod(cp);
                for (final Location loc : masterdata.getLocations()) {
                    cp.addLocation(loc);
                }
                cpList.append("- " + cp.getName() + " " + PropertyDate.format(cp.getFrom(), PrecisionDate.year) + "\n");
            }
        }
        app.messageInfo(cpList.toString());
    }
}
