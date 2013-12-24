/*
 * Rapid Beans Framework: ActionUserAuthn.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 20.11.2006
 */
package org.rapidbeans.clubadmin.service;

import javax.swing.JComponent;

import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.datasource.Filter;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.presentation.EditorBean;
import org.rapidbeans.presentation.swing.DocumentTreeViewSwing;
import org.rapidbeans.service.Action;

/**
 * @author Martin Bluemel
 */
public class OpenMyUserAccount extends Action {

    /**
     * implementation of the execute method.
     */
    public final void execute() {
        final Application client = ApplicationManager.getApplication();
        try {
            final Filter filter = new Filter();
            filter.addIncludes("org.rapidbeans.clubadmin.domain.ClubadminUser[id = '"
                    + client.getAuthenticatedUser().getIdString() + "']");
            final DocumentView view = client.openDocumentView(client.getAuthnDoc(),
                    "masterdata", "userselfadmin", filter);
            final DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) view.getTreeView();
            treeView.getTree().expandPath(treeView.getTree().getPathForRow(1));
            treeView.getTree().setSelectionPath(treeView.getTree().getPathForRow(2));
            final EditorBean editor = treeView.editBeans();
            ((JComponent) editor.getPropEditor("roles").getWidget()).setEnabled(false);
            ((JComponent) treeView.getWidget()).setVisible(false);
            view.getTreeView().setShowProperties(true);
        } catch (ValidationException e) {
            final RapidBeansLocale locale = ApplicationManager.getApplication().getCurrentLocale();
            if (!client.getTestMode()) {
                client.messageError(
                    e.getLocalizedMessage(locale),
                        locale.getStringGui("messagedialog.title.config.wrong"));
            }
        }
    }
}
