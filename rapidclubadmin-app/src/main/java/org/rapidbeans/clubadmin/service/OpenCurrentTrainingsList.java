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
public class OpenCurrentTrainingsList extends Action {

	/**
	 * Drives the action to open the master data document.
	 */
	public void execute() {
		final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();
		final RapidBeansLocale locale = client.getCurrentLocale();
		final String depId = this.getArgumentValue("department");
		try {
			client.openCurrentTrainingsList(depId);
		} catch (UtilException e) {
			if (e.getCause() instanceof UnknownHostException) {
				client.messageError(locale.getStringMessage("error.load.file.web.masterdata"),
						locale.getStringMessage("error.load.file.web.title"));
			} else {
				throw e;
			}
		} catch (LocalizedException e) {
			e.present();
		}
	}
}
