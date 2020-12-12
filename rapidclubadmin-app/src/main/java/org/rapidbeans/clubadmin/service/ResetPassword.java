/*
 * Rapid Beans Framework: Test.java
 *
 * Copyright Martin Bluemel, 2010
 *
 * 28.02.2010
 */

package org.rapidbeans.clubadmin.service;

import java.io.IOException;
import java.net.MalformedURLException;

import org.rapidbeans.clubadmin.datasource.HttpClientPhp;
import org.rapidbeans.clubadmin.domain.ClubadminUser;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.service.Action;

/**
 * The action to restore all application data.
 * 
 * @author Martin Bluemel
 */
public class ResetPassword extends Action {

	private final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();

	private final RapidBeansLocale locale = app.getCurrentLocale();

	private final HttpClientPhp httpClient = new HttpClientPhp(
			"http://" + app.getServer() + '/' + app.getRoot() + "/fileio.php", "musashi09");

	/**
	 * open the training editing view.
	 */
	public void execute() {
		final ClubadminUser user = (ClubadminUser) app.getAuthenticatedUser();
		if (user.getEmail() == null || user.getEmail().trim().length() == 0) {
			app.messageInfo(locale.getStringMessage("resetpassword.abort1.message"), locale.getStringMessage("resetpassword.title"));
			return;
		}
		if (!app.messageYesNo(locale.getStringMessage("resetpassword.confirmation1.message"), locale.getStringMessage("resetpassword.title"))) {
			app.messageInfo(locale.getStringMessage("resetpassword.abort2.message"), locale.getStringMessage("resetpassword.title"));
			return;
		}
		try {
			final String newPwd = generateNewPwd();
			user.resetPwd(newPwd, false);
			app.save(app.getMasterDoc());
			httpClient.sendMail("bluemel.martin@gmx.de", "RapidClubAdmin: neue Zugangsdaten",
					"admin@budo-club-ismaning.de", String.format("Neues Passwort: %s", newPwd));
			app.messageInfo(locale.getStringMessage("resetpassword.confirmation2.message"), locale.getStringMessage("resetpassword.title"));
		} catch (MalformedURLException e) {
			throw new RapidBeansRuntimeException(e);
		} catch (IOException e) {
			throw new RapidBeansRuntimeException(e);
		}
	}

	private String generateNewPwd() {
		return "Musashi" + Integer.toString((int) (Math.random() * 10000));
	}
}
