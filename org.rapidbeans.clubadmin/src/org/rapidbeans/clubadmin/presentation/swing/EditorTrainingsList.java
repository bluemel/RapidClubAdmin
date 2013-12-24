/*
 * RapidBeans Application RapidClubAdmin: EditorTrainingsList.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 14.01.2007
 */
package org.rapidbeans.clubadmin.presentation.swing;

import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.core.basic.PropertyCollection;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.presentation.swing.EditorBeanSwing;


/**
 * Extends the standard bean editor by plugging in some update
 * actions to the OK and apply buttons.
 *
 * @author Martin Bluemel
 */
public class EditorTrainingsList extends EditorBeanSwing {

    /**
     * @param client the client
     * @param docView the document view
     * @param bizBean the bean
     * @param newBeanParentColProp the parent collection property
     */
    public EditorTrainingsList(final Application client, final DocumentView docView,
            final RapidBean bizBean, final PropertyCollection newBeanParentColProp) {
        super(client, docView, bizBean, newBeanParentColProp);
    }

    /**
     * action handler for OK button.
     */
    public void handleActionOk() {
        super.handleActionOk();
        this.updateTrainingsList();
    }

    /**
     * action handler for Apply button.
     */
    public void handleActionApply() {
        super.handleActionApply();
        this.updateTrainingsList();
    }

    /**
     * Update the parent Billing Period.
     */
    private void updateTrainingsList() {
        final TrainingsList bp = ((TrainingsList) this.getBean());
        bp.updateClosingPeriodsFromMasterdata();
        bp.updateTrainings(TrainingsList.UPDATE_MODE_PROPS, null);
        bp.updateTrainingsClosing();
    }
}
