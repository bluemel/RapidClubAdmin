/*
 * RapidBeans Application RapidClubAdmin: Overview.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 23.08.2007
 */
package org.rapidbeans.clubadmin.domain.report;

import org.rapidbeans.clubadmin.domain.MasterData;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.clubadmin.presentation.swing.ReportPresentationDialogSwing;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.service.Action;

/**
 * Report: Trainers as line separated list
 * 
 * @author Martin Bluemel
 */
public final class Trainers extends Action {

	public void execute() {
		@SuppressWarnings("unused")
		final MasterData masterData = ((RapidClubAdminClient) ApplicationManager.getApplication()).getMasterData();
		final StringBuilder report = new StringBuilder();
		masterData.getTrainers().forEach(t -> {
			System.out.println(String.format("%s, %s", t.getLastname(), t.getFirstname()));
			report.append(String.format("%s, %s\n", t.getLastname(), t.getFirstname()));
		});
		new ReportPresentationDialogSwing(report.toString(), "Bericht: Trainer").show();
	}
}
