/*
 * RapidBeans Application Rapid Club Admin: SalaryComponentPropMoney.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 17.03.2008
 */
package org.rapidbeans.clubadmin.domain;

import org.rapidbeans.core.basic.PropertyQuantity;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypeProperty;


/**
 * extension of money property SalaryComponent.money.
 */
public class SalaryComponentPropMoney extends PropertyQuantity {

    /**
     * constructor.
     * @param type the property type
     * @param parentBean the parent bean
     */
    public SalaryComponentPropMoney(final TypeProperty type, final RapidBean parentBean) {
        super(type, parentBean);
    }

    /**
     * Interceptor for setValue.
     *
     * @param link the link to remove
     */
    public void setValue(final Object value) {
        super.setValue(value);
        RapidBean bean = this.getBean();
        if (bean != null) {
            bean = bean.getParentBean();
            if (bean != null) {
                ((AbstractSalary) bean).updateMoneyFromComponents();
            }
        }
    }
}
