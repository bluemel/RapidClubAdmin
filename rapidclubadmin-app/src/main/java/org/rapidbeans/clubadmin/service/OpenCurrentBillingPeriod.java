/*
 * Rapid Beans Framework: OpenMasterdata.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 04.01.2005
 */

package org.rapidbeans.clubadmin.service;

import java.net.UnknownHostException;

import javax.swing.JComponent;

import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.exception.LocalizedException;
import org.rapidbeans.core.exception.UtilException;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.presentation.swing.DocumentTreeViewSwing;
import org.rapidbeans.service.Action;

/**
 * The action open the current billing period.
 *
 * @author Martin Bluemel
 */
public class OpenCurrentBillingPeriod extends Action {

	/**
	 * Drives the action to open the current billing period.
	 */
	public void execute() {
		final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();
		final RapidBeansLocale locale = client.getCurrentLocale();
		try {
//            final Filter filter = new Filter();
//            filter.addIncludes("org.rapidbeans.clubadmin.domain.BillingPeriod");
//            filter.applyFilter(client.getMasterDoc(), true);
			final DocumentView view = client.openDocumentView(client.getMasterDoc(), "masterdata",
					"billingperiod"/* , filter */);
			final DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) view.getTreeView();
//            treeView.getTree().expandPath(treeView.getTree().getPathForRow(1));
//            treeView.getTree().setSelectionPath(treeView.getTree().getPathForRow(2));
			treeView.getTree().expandPath(treeView.getTree().getPathForRow(10));
			treeView.getTree().setSelectionPath(treeView.getTree().getPathForRow(11));
			treeView.editBeans();
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
