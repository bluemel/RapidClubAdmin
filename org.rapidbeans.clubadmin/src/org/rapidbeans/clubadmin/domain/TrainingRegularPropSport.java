/*
 * RapidBeans Application RapidClubAdmin: TrainingRegularPropSport.java
 *
 * Copyright Martin Bluemel, 2009
 *
 * 14.11.2009
 */
package org.rapidbeans.clubadmin.domain;

import java.util.Collection;

import org.rapidbeans.core.basic.PropertyCollection;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypeProperty;


/**
 * extends string property TrainingRegular.sport.
 */
public class TrainingRegularPropSport extends PropertyCollection {

    /**
     * constructor.
     * @param type the property type
     * @param parentBean the parent bean
     */
    public TrainingRegularPropSport(final TypeProperty type, final RapidBean parentBean) {
        super(type, parentBean);
    }

    @SuppressWarnings("unchecked")
    public Collection<Sport> getValue() {
        Collection<Sport> sports = null;
        final TrainingRegular tr = (TrainingRegular) this.getBean();
        final TrainingDate trd = (TrainingDate) tr.getParentBean();
        if (trd != null) {
            sports = (Collection<Sport>) trd.getPropValue("sport");
        }
        return sports;
    }

    public void setValue(final Object value) {
        final TrainingRegular tr = (TrainingRegular) this.getBean();
        final TrainingDate trd = (TrainingDate) tr.getParentBean();
        if (trd != null) {
            trd.setPropValue("sport", value);
        }
    }
}
