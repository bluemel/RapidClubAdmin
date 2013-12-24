/*
 * EasyBiz Application RapidClubAdmin: TrainingPropHelbytrainer.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 15.08.2007
 */
package org.rapidbeans.clubadmin.domain;

import org.rapidbeans.core.basic.Link;
import org.rapidbeans.core.basic.PropertyCollection;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypeProperty;


/**
 * extension from collection property TrainingHeldByTrainer.trainer.
 */
public class TrainingPropHelbytrainer extends PropertyCollection {

    /**
     * constructor.
     * @param type the property type
     * @param parentBean the parent bean
     */
    public TrainingPropHelbytrainer(final TypeProperty type, final RapidBean parentBean) {
        super(type, parentBean);
    }

    /**
     * intercept the addLink method.
     *
     * @param link the bean to add
     * @param addInverse if an inverse link should be added
     * @param checkContainerLinksToExternalObjects
     *        determines if links from an object living inside the
     *        container should be allowed.
     *        Be very careful to set this argument to false.
     * @param checkContainerAlreadyContains
     *        determines if the bean already is contained by the container
     */
    public void addLink(final Link link, final boolean addInverse,
            final boolean checkContainerLinksToExternalObjects,
            final boolean checkContainerAlreadyContains) {
        super.addLink(link, addInverse, checkContainerLinksToExternalObjects,
                checkContainerAlreadyContains);
        ((Training) this.getBean()).correctTrainingState();
    }

    /**
     * intercept the remove link method.
     *
     * @param link the bean reference to remove
     * @param removeInverse if the inverse link has to be removed
     * @param throwNotFound throw an exception if the link to remove is not in
     * @param deleteOrpahnedComponent delete an orphaned component
     */
    public void removeLink(final Link link, final boolean removeInverse,
            final boolean throwNotFound, final boolean deleteOrpahnedComponent) {
        super.removeLink(link, removeInverse, deleteOrpahnedComponent, throwNotFound);
        ((Training) this.getBean()).correctTrainingState();
    }

    /**
     * intercept the set value method.
     *
     * @param col the new value for this property
     * @param touchInverseLinks if an inverse link will be added or not
     * @param checkContainerLinksToExternalObjects
     *        determines if links from an object living inside the
     *        container should be allowed.
     *        Be very careful to set this argument to false.
     */
    public void setValue(final Object col,
            final boolean touchInverseLinks, final boolean checkContainerLinksToExternalObjects) {
        super.setValue(col, touchInverseLinks, checkContainerLinksToExternalObjects);
        ((Training) this.getBean()).correctTrainingState();
    }
}
