/*
 * Rapid Beans Framework: OpenHistoryTrainingsList.java
 *
 * Copyright Martin Bluemel, 2009
 *
 * 27.02.2009
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
 * The action to open a history training list document.
 *
 * @author Martin Bluemel
 */
public class OpenHistoryTrainingsList extends Action {

	/**
	 * Drives the action to open a history training list.
	 */
	public void execute() {
		final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();
		final RapidBeansLocale locale = client.getCurrentLocale();
		final String bpId = this.getArgumentValue("billingperiod");
		final String depId = this.getArgumentValue("department");
		try {
			client.openHistoryTrainingsList(bpId, depId);
		} catch (UtilException e) {
			if (e.getCause() instanceof UnknownHostException) {
				client.messageError(locale.getStringMessage("error.load.file.web.masterdata"),
						locale.getStringMessage("error.load.file.web.title"));
			} else {
				throw e;
			}
		} catch (LocalizedException e) {
			e.printStackTrace();
			e.present();
		}
	}
}
