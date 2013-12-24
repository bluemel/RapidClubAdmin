/*
 * Rapid Beans Framework: EnablerOpenCurrentBillingPeriod.java
 *
 * Copyright Martin Bluemel, 2010
 *
 * 02/20/2010
 */
package org.rapidbeans.clubadmin.presentation.enabler;

import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.presentation.MainWindow;
import org.rapidbeans.presentation.enabler.Enabler;

/**
 * @author Martin Bluemel
 */
public class EnablerOpenCurrentTrainingsList extends Enabler {

    private MainWindow mainWindow = null;

    private Department department = null;

    public EnablerOpenCurrentTrainingsList(final MainWindow mainWindow,
            final Department department) {
        this.mainWindow = mainWindow;
        this.department = department;
    }

    /**
     * the enabling method of every enabler.
     *
     * @return if the open trainingslist tool bar button is enabled or not.
     */
    public boolean getEnabled() {
        if (this.mainWindow.getActiveDocument() == null) {
            return true;
        }
        if (!(this.mainWindow.getActiveDocument().getRoot() instanceof TrainingsList)) {
            return true;
        }
        final TrainingsList trlist = (TrainingsList)
            this.mainWindow.getActiveDocument().getRoot();
        if (trlist.getForSingleDepartment().equals(this.department)) {
            return false;
        } else {
            return true;
        }
    }
}
