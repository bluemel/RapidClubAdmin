/*
 * Rapid Beans Framework: DocumentSave.java
 *
 * Copyright Martin Bluemel, 2010
 *
 * 26.04.2010
 */

package org.rapidbeans.clubadmin.service;

import org.rapidbeans.clubadmin.domain.RapidClubAdminBusinessLogicException;
import org.rapidbeans.clubadmin.domain.Training;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.service.Action;


/**
 * The action to save the current document on the local file system.
 *
 * @author Martin Bluemel
 */
public class DocumentSave extends Action {

    /**
     * default constructor.
     */
    public DocumentSave() {
        super(DocumentSave.class.getName());
    }

    /**
     * Save.
     */
    public void execute() {
        final Application app = ApplicationManager.getApplication();
        final Document doc = app.getActiveDocument();
        if (doc != null && doc.getChanged()) {
            Training training = null;
            try {
                for (final RapidBean bean : doc.findBeansByType(
                        "org.rapidbeans.clubadmin.domain.Training")) {
                    training = (Training) bean;
                    training.validate();
                }
                app.save(doc);
            } catch (RapidClubAdminBusinessLogicException e) {
                app.messageError(
                    e.getLocalizedMessage(app.getCurrentLocale()),
                    "Fehler beim Abspeichern in Training: "
                    + training.toStringGuiId(app.getCurrentLocale()));
            } catch (ValidationException e) {
                app.messageError(
                    e.getLocalizedMessage(app.getCurrentLocale()),
                    "Fehler beim Abspeichern in Training: "
                    + training.toStringGuiId(app.getCurrentLocale()));
            }
        }
    }
}
