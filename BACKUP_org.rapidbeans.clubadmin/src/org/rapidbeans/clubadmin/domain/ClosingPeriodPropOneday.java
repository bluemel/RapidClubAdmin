/*
 * RapidBeans Application RapidClubAdmin: ClosingPeriodPropOneday.java
 *
 * Copyright Martin Bluemel, 2010
 *
 * 01/10/2011
 */
package org.rapidbeans.clubadmin.domain;

import java.util.Date;

import org.rapidbeans.core.basic.PropertyBoolean;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.exception.PropNotInitializedException;
import org.rapidbeans.core.type.TypeProperty;


/**
 * extension from date property ClosingPeriod.from.
 */
public class ClosingPeriodPropOneday extends PropertyBoolean {

    /**
     * constructor.
     * @param type the property type
     * @param parentBean the parent bean
     */
    public ClosingPeriodPropOneday(final TypeProperty type, final RapidBean parentBean) {
        super(type, parentBean);
    }

    public Boolean validate(final Object newValue) {

        Boolean newValueBool = convertValue(newValue);

        // on the fly migration for mandatory value
        if (newValueBool == null) {
            newValueBool = Boolean.FALSE;
            final ClosingPeriod cp = (ClosingPeriod) this.getBean();
            if (cp != null) {
                try {
                    final Date fromTime = cp.getFrom();
                    final Date toTime = cp.getTo();
                    if (fromTime != null && toTime != null) {
                        if (fromTime.equals(toTime)) {
                            newValueBool = Boolean.TRUE;
                        }
                    }
                } catch (PropNotInitializedException e) {    
                }
            }
        }
// else if (newValueBool.booleanValue() == false) {
//            final ClosingPeriod cp = (ClosingPeriod) this.getBean();
//            if (cp != null) {
//                try {
//                    final Date fromTime = cp.getFrom();
//                    final Date toTime = cp.getTo();
//                    if (fromTime != null && toTime != null) {
//                        if (fromTime.equals(toTime)) {
//                            throw new ValidationException(
//                                    "invalid.prop.closigperiod.oneday",
//                                    this.getBean(),
//                                    "Attribute \"oneday\" must not be set to 'false',"
//                                    + " whenever \"from\" date equals \"to\" date.");
//                        }
//                    }
//                } catch (PropNotInitializedException e) {    
//                }
//            }
//        }
        return newValueBool;
    }

    /**
     * Overwrite the generic value setter in order to
     * react on changes.
     * If the from date is given at the time this property
     * is set the to date is set equal to the from date.
     * Accepts the following data types:<br/>
     * <b>Boolean:</b> the Date<br/>
     * <b>String:</b> the Date as string<br/>
     *
     * @param newDateVal the new value to set
     */
    public void setValue(final Object newValue) {
        super.setValue(newValue);
        final Boolean newValueBool = (Boolean) super.getValue();
        final ClosingPeriod cp = (ClosingPeriod) this.getBean();
        if (newValueBool.booleanValue() == true) {
            if (cp != null) {
                try {
                    final Date fromTime = cp.getFrom();
                    final Date toTime = cp.getTo();
                    if (fromTime != null
                            && (toTime == null || (!fromTime.equals(toTime)))) {
                        cp.setTo(cp.getFrom());
                    }
                } catch (PropNotInitializedException e) {    
                }
            }
        }
    }
}
