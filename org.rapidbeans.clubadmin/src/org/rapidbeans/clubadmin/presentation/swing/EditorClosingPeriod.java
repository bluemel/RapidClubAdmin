/*
 * RapidBeans Application RapidClubAdmin: EditorClosingPeriod.java
 *
 * Copyright Martin Bluemel, 2011
 *
 * 14.01.2011
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.rapidbeans.core.basic.PropertyCollection;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.basic.ThreadLocalValidationSettings;
import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.presentation.EditorProperty;
import org.rapidbeans.presentation.swing.EditorBeanSwing;


/**
 * Extends the standard bean editor.
 *
 * @author Martin Bluemel
 */
public class EditorClosingPeriod extends EditorBeanSwing {

    /**
     * @param client the client
     * @param docView the document view
     * @param bean the bean
     * @param newBeanParentColProp the parent collection property
     */
    public EditorClosingPeriod(final Application client,
            final DocumentView docView,
            final RapidBean bean,
            final PropertyCollection newBeanParentColProp) {
        super(client, docView, bean, newBeanParentColProp);
        updateOnedayEnabled();
    }

    /**
     * @param propEditor the editor that notified the change.
     */
    public void inputFieldChanged(final EditorProperty propEditor) {
        Date from = null;
        Date to = null;
        try {
            from = (Date) getPropEditor("from").getInputFieldValue();
        } catch (ValidationException e) {
            from = null;
        }
        try {
            to = (Date) getPropEditor("to").getInputFieldValue();
        } catch (ValidationException e) {
            from = null;
        }
        if (propEditor.getProperty().getName().equals("from")
                || propEditor.getProperty().getName().equals("to")) {
            if (from != null && to != null && from.equals(to)) {
                if (getPropEditor("oneday").getProperty().getValue().equals(Boolean.FALSE)) {
                    getPropEditor("oneday").getProperty().setValue(Boolean.TRUE);
                    ((JComponent) getPropEditor("to").getWidget()).setEnabled(false);
                }
            } else {
                if (getPropEditor("oneday").getProperty().getValue().equals(Boolean.TRUE)) {
                    getPropEditor("oneday").getProperty().setValue(Boolean.FALSE);
                    ((JComponent) getPropEditor("to").getWidget()).setEnabled(true);
                }
            }
        } else if (propEditor.getProperty().getName().equals("oneday")) {
            if (getPropEditor("oneday").getProperty().getValue().equals(Boolean.FALSE)
                    && from != null && to != null && from.equals(to)) {
                try {
                    ThreadLocalValidationSettings.validationOff();
                    ((JTextField) getPropEditor("to").getWidget()).setText("");
                    this.validateAndUpdateButtons(propEditor);
                } finally {
                    ThreadLocalValidationSettings.remove();
                }
                ((JComponent) getPropEditor("to").getWidget()).setEnabled(true);
            }
            updateOnedayEnabled();
        }
        super.inputFieldChanged(propEditor);
    }

    private void updateOnedayEnabled() {
        if (getPropEditor("oneday").getProperty().getValue().equals(Boolean.TRUE)) {
            ((JComponent) getPropEditor("to").getWidget()).setEnabled(false);
        } else {
            ((JComponent) getPropEditor("to").getWidget()).setEnabled(true);
        }
    }
}
