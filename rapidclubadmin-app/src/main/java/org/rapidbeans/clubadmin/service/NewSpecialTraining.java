/*
 * Rapid Beans Framework: NewSpecialTraining.java
 *
 * Copyright Martin Bluemel, 2009
 *
 * 18.11.2009
 */

package org.rapidbeans.clubadmin.service;

import javax.swing.JTree;

import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.presentation.swing.DocumentTreeViewSwing;
import org.rapidbeans.service.Action;

/**
 * The action to switch to the expert view and open the editor to create a new
 * TrainingSpecial instance under the one and only department
 *
 * @author Martin Bluemel
 */
public class NewSpecialTraining extends Action {

	/**
	 * open the expert view.
	 */
	public void execute() {
		final Application client = ApplicationManager.getApplication();
		final DocumentView activeView = client.getMainwindow().getActiveDocumentView();
		if (activeView == null) {
			client.messageInfo("no document view active");
			return;
		}
		final DocumentView expertView = client.openDocumentView(activeView.getDocument(), "trainingslist", "expert");
		final DocumentTreeViewSwing expertTreeView = (DocumentTreeViewSwing) expertView.getTreeView();
		JTree expertTree = (JTree) expertTreeView.getTree();
		expertTree.setSelectionPath(expertTree.getPathForRow(3));
		expertTree.expandPath(expertTree.getPathForRow(3));
		expertTree.expandPath(expertTree.getPathForRow(4));
		expertTree.expandPath(expertTree.getPathForRow(5));
		expertTree.expandPath(expertTree.getPathForRow(6));
		expertTree.setSelectionPath(expertTree.getPathForRow(9));
		expertTreeView.createBean();
	}
}
