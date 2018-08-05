/*
 * Rapid Beans Framework: EditorPropertySalaryComponentTime.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 20.03.2008
 */

package org.rapidbeans.clubadmin.presentation.swing;

import org.rapidbeans.clubadmin.domain.AbstractSalary;
import org.rapidbeans.clubadmin.domain.SalaryComponent;
import org.rapidbeans.core.basic.Property;
import org.rapidbeans.domain.math.Time;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.EditorBean;
import org.rapidbeans.presentation.swing.EditorPropertyQuantitySwing;

/**
 * the bean editor GUI.
 *
 * @author Martin Bluemel
 */
public class EditorPropertySalaryComponentTime
    extends EditorPropertyQuantitySwing {

    /**
     * constructor.
     *
     * @param client the client
     * @param bizBeanEditor the parent bean editor
     * @param prop the bean property to edit
     * @param propBak the bean property backup
     */
    public EditorPropertySalaryComponentTime(final Application client,
            final EditorBean bizBeanEditor,
            final Property prop, final Property propBak) {
        super(client, bizBeanEditor, prop, propBak);
        updateScTime();
    }

    /**
     * Adapt the SalaryComponent's time according to the future
     * parent Salary's time.
     */
    protected void updateScTime() {
        if (this.getBeanEditor().isInNewMode()) {
            final AbstractSalary parentSalary = (AbstractSalary) this.getBeanEditor().getParentBean();
            final Time parentTime = parentSalary.getTime();
            ((SalaryComponent) this.getBeanEditor().getBean()).setTime(parentTime);
            updateUI();
        }        
    }
}
