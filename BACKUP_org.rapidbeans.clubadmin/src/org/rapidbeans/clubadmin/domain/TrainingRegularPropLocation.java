/*
 * RapidBeans Application RapidClubAdmin: TrainingRegularPropLocation.java
 *
 * Copyright Martin Bluemel, 2009
 *
 * 13.11.2009
 */
package org.rapidbeans.clubadmin.domain;

import java.util.Collection;

import org.rapidbeans.core.basic.PropertyCollection;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypeProperty;


/**
 * extends quantity property TrainingRegular.timeend.
 */
public class TrainingRegularPropLocation extends PropertyCollection {

    /**
     * constructor.
     * @param type the property type
     * @param parentBean the parent bean
     */
    public TrainingRegularPropLocation(final TypeProperty type, final RapidBean parentBean) {
        super(type, parentBean);
    }

    @SuppressWarnings("unchecked")
    public Collection<Location> getValue() {
        Collection<Location> loc = null;
        final TrainingRegular tr = (TrainingRegular) this.getBean();
        final TrainingDate trd = (TrainingDate) tr.getParentBean();
        if (trd != null) {
            loc = (Collection<Location>) trd.getPropValue("location");
        }
        return loc;
    }

    public void setValue(final Object value) {
        final TrainingRegular tr = (TrainingRegular) this.getBean();
        final TrainingDate trd = (TrainingDate) tr.getParentBean();
        if (trd != null) {
            trd.setPropValue("location", value);
        }
    }
}
