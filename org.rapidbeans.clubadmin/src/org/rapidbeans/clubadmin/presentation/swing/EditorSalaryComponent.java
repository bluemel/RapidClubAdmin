/*
 * RapidBeans Application RapidClubAdmin: EditorSalaryComponent.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 20.03.2008
 */
package org.rapidbeans.clubadmin.presentation.swing;

import org.rapidbeans.core.basic.PropertyCollection;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.presentation.swing.EditorBeanSwing;


/**
 * Extends the standard bean editor.
 *
 * @author Martin Bluemel
 */
public class EditorSalaryComponent extends EditorBeanSwing {

    /**
     * @param client the client
     * @param docView the document view
     * @param bizBean the bean
     * @param newBeanParentColProp the parent collection property
     */
    public EditorSalaryComponent(final Application client, final DocumentView docView,
            final RapidBean bizBean, final PropertyCollection newBeanParentColProp) {
        super(client, docView, bizBean, newBeanParentColProp);
    }

    /**
     * Adds the update of the SalaryComponent's time to the
     * apply action handler.
     */
    @Override
    public void handleActionApply() {
        super.handleActionApply();
        final EditorPropertySalaryComponentTime propedTime = (EditorPropertySalaryComponentTime)
            this.getPropEditor("time");
        propedTime.updateScTime();
        final EditorPropertySalaryComponentMoney propedMoney = (EditorPropertySalaryComponentMoney)
            this.getPropEditor("money");
        propedMoney.updateScMoney();
    }
}
