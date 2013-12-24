/*
 * Rapid Beans Framework: EditorPropertyAbstractSalaryMoney.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * created 20.03.2008
 */

package org.rapidbeans.clubadmin.presentation.swing;

import java.util.Collection;

import javax.swing.JComponent;

import org.rapidbeans.clubadmin.domain.AbstractSalary;
import org.rapidbeans.clubadmin.domain.SalaryComponent;
import org.rapidbeans.core.basic.Property;
import org.rapidbeans.core.event.PropertyChangeEvent;
import org.rapidbeans.datasource.event.ChangedEvent;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.EditorBean;
import org.rapidbeans.presentation.swing.EditorPropertyMoneySwing;

/**
 * the bean editor GUI.
 *
 * @author Martin Bluemel
 */
public class EditorPropertyAbstractSalaryMoney
    extends EditorPropertyMoneySwing {

    /**
     * Constructor.
     *
     * @param client the client
     * @param bizBeanEditor the parent bean editor
     * @param prop the bean property to edit
     * @param propBak the bean property backup
     */
    public EditorPropertyAbstractSalaryMoney(Application client, EditorBean bizBeanEditor, Property prop, Property propBak) {
        super(client, bizBeanEditor, prop, propBak);
        // call of updateUI seems to be included in the super constructor
    }

    /**
     * Appends some action to the update handler.
     * Input is forbidden as long as the AbstractSalary has components.
     */
    @Override
    public void updateUI() {
        super.updateUI();
        final AbstractSalary sal = (AbstractSalary) this.getProperty().getBean();
        if (sal != null) {
            final Collection<SalaryComponent> comps = sal.getComponents();
            if (comps != null && comps.size() > 0) {
                ((JComponent) this.getWidget()).setEnabled(false);
            } else {
                ((JComponent) this.getWidget()).setEnabled(true);
            }
        }        
    }

    /**
     * bean changed event.
     * @param e changed event
     */
    @Override
    public void beanChanged(final ChangedEvent e)  {
        final Property propMoney = this.getProperty();
        final Property propComponents = this.getProperty().getBean().getProperty("components");
        for (PropertyChangeEvent propEv : e.getPropertyEvents()) {
            final Property prop = propEv.getProperty();
            if (prop == propMoney || prop == propComponents) {
                this.updateUI();
                break;
            }
        }
    }
}
