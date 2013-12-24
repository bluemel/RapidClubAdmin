/*
 * Rapid Beans Framework: ViewExpert.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 01.08.2007
 */

package org.rapidbeans.clubadmin.service;

import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.service.Action;


/**
 * The action to switch to the expert view
 *
 * @author Martin Bluemel
 */
public class ViewExpertAction extends Action {

    /**
     * open the expert view.
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
                "trainingslist", "expert");
        if (activeView != null && newView != activeView) {
            activeView.close();
        }
    }
}
