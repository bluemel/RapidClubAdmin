/*
 * EasyBiz Application RapidClubAdmin: CLlosingPeriodPropTo.java
 *
 * Copyright Martin Bluemel, 2006
 *
 * 19.10.2006
 */
package org.rapidbeans.clubadmin.domain;

import org.rapidbeans.core.basic.PropertyQuantity;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypeProperty;
import org.rapidbeans.domain.math.TimeOfDay;


/**
 * extension from date property ClosingPeriod.from.
 */
public class TrainingDatePropTimeend extends PropertyQuantity {

    /**
     * constructor.
     * @param type the property type
     * @param parentBean the parent bean
     */
    public TrainingDatePropTimeend(final TypeProperty type, final RapidBean parentBean) {
        super(type, parentBean);
    }

    /**
     * the special part of the validation.<br>
     * implicitly also converts the given object.
     *
     * @param newValue
     *            the value object to validate
     *
     * @return the converted value which is the internal representation or if a
     *         primitive type the corresponding value object
     */
    public TimeOfDay validate(final Object newValue) {
        final TimeOfDay time = (TimeOfDay) super.validate(newValue);
        ((TrainingDate) this.getBean()).validateTimeend(time);
        return time;
    }
}
