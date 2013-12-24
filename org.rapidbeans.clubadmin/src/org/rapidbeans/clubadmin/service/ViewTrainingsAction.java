/*
 * Rapid Beans Framework: ViewTrainings.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 09.12.2005
 */

package org.rapidbeans.clubadmin.service;

import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.service.Action;


/**
 * The action to create a billing period.
 *
 * @author Martin Bluemel
 */
public class ViewTrainingsAction extends Action {

    /**
     * open the training editing view.
     */
    public void execute() {
        final Application client =  ApplicationManager.getApplication();
        DocumentView activeView =
            client.getMainwindow().getActiveDocumentView();
        if (activeView == null) {
            client.messageInfo("no document view active");
            return;
        }
        final DocumentView newView = client.openDocumentView(activeView.getDocument(),
                "trainingslist", "trainings");
        if (activeView != null && newView != activeView) {
            activeView.close();
        }
    }
}
