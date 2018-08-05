/*
 * Rapid Club Admin Application: ToolbarTrainingslistSwing.java
 *
 * Copyright Martin Bluemel, 2010
 *
 * 02.19.2010
 */

package org.rapidbeans.clubadmin.presentation.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JToolBar;

import org.rapidbeans.clubadmin.domain.ClubadminUser;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.presentation.enabler.EnablerOpenCurrentTrainingsList;
import org.rapidbeans.clubadmin.service.OpenCurrentTrainingsList;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.datasource.event.AddedEvent;
import org.rapidbeans.datasource.event.ChangedEvent;
import org.rapidbeans.datasource.event.DocumentChangeListener;
import org.rapidbeans.datasource.event.RemovedEvent;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.MainWindow;
import org.rapidbeans.presentation.config.ConfigToolbar;
import org.rapidbeans.presentation.config.ConfigToolbarButton;
import org.rapidbeans.presentation.swing.ToolbarSwing;
import org.rapidbeans.service.ActionArgument;

public class ToolbarTrainingslistSwing
    extends ToolbarSwing implements DocumentChangeListener {

    private Document activeDocument = null;

    public ToolbarTrainingslistSwing(
            final Application client,
            final MainWindow mainWindow,
            final ConfigToolbar toolbarConfig,
            final String resourcePath) {
        super(client, mainWindow, toolbarConfig, resourcePath);

        // needed to be informed about any change of the active document
        this.activeDocument = mainWindow.getActiveDocument();
        if (this.activeDocument != null) {
            this.activeDocument.addDocumentChangeListener(this);
        }

        // create the "open current trainings list" buttons
        // for all authorized departments
        final JToolBar toolBarWidget = (JToolBar) this.getWidget();
        final ClubadminUser authnUser = (ClubadminUser) client.getAuthenticatedUser();
        if (authnUser != null) {
            final Set<Department> authDeps = authnUser.getAuthorizedDepartments();
            final Department[] authDepsSorted = authDeps.toArray(new Department[authDeps.size()]);
            Arrays.sort(authDepsSorted);
            for (final Department dep : authDepsSorted) {
                final ConfigToolbarButton conf = new ConfigToolbarButton();
                conf.setName(dep.getName());
                final ToolbarButtonTrainingslistDepartment button =
                    new ToolbarButtonTrainingslistDepartment(
                            dep, conf, client, mainWindow, resourcePath);

                final JButton buttonWidget = (JButton) button.getWidget();

                // associate button action
                final OpenCurrentTrainingsList openAction = new OpenCurrentTrainingsList();
                final ActionArgument arg = new ActionArgument();
                arg.setName("department");
                arg.setValue(dep.getIdString());
                openAction.addArgument(arg);
                buttonWidget.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        openAction.execute();
                    }
                });

                // associate the enabler
                button.setEnabler(new EnablerOpenCurrentTrainingsList(mainWindow, dep));

                // do both: add the button and the widget
                this.addButton(button);
                toolBarWidget.add(buttonWidget);
            }
        }
    }

    /**
     * the update method
     */
    @Override
    public void update() {
        super.update();
        if (getMainWindow() == null) {
            return;
        }
        final Document currentlyActiveDocument = getMainWindow().getActiveDocument();
        if (this.activeDocument != null
                && this.activeDocument != currentlyActiveDocument) {
            this.activeDocument.removeDocumentChangeListener(this);
        }
        if (this.activeDocument == null
                || this.activeDocument != currentlyActiveDocument) {
            this.activeDocument = currentlyActiveDocument;
        }
        if (currentlyActiveDocument != null) {
            currentlyActiveDocument.addDocumentChangeListener(this);
        }
    }

    @Override
    public void beanAddPre(AddedEvent e) {
    }

    @Override
    public void beanAdded(AddedEvent e) {
        super.update();
    }

    @Override
    public void beanChangePre(ChangedEvent e) {
    }

    @Override
    public void beanChanged(ChangedEvent e) {
        super.update();
    }

    @Override
    public void beanRemovePre(RemovedEvent e) {
    }

    @Override
    public void beanRemoved(RemovedEvent e) {
        super.update();
    }

    @Override
    public void documentSaved() {
        super.update();
    }
}
