/*
 * EasyBiz Application RapidClubAdmin: TrainerPropDepartments.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 09.09.2006
 */
package org.rapidbeans.clubadmin.domain;

import org.rapidbeans.core.basic.Link;
import org.rapidbeans.core.basic.PropertyCollection;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypeProperty;

/**
 * extension from collection property Trainer.departments.
 */
public class TrainerPropDepartments extends PropertyCollection {

	/**
	 * constructor.
	 * 
	 * @param type       the property type
	 * @param parentBean the parent bean
	 */
	public TrainerPropDepartments(final TypeProperty type, final RapidBean parentBean) {
		super(type, parentBean);
	}

	/**
	 * remove the trainer from all trainer planning instances belonging to the
	 * department removed
	 *
	 * @param link                    the bean reference to remove
	 * @param removeInverse           if the inverse link has to be removed
	 * @param throwNotFound           throw an exception if the link to remove is
	 *                                not in
	 * @param deleteOrpahnedComponent unlink an orphaned component
	 * @param validate                validate
	 */
	public void removeLink(final Link link, final boolean removeInverse, final boolean throwNotFound,
			final boolean deleteOrpahnedComponent) {
		super.removeLink(link, removeInverse, throwNotFound, deleteOrpahnedComponent);
	}
}
