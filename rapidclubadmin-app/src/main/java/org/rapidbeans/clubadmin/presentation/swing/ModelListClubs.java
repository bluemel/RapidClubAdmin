/*
 * Rapid Beans Application RapidClubAdmin: ModelListCLubs.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 04.01.2007
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.util.Set;

import javax.swing.ListModel;

import org.rapidbeans.clubadmin.domain.Club;
import org.rapidbeans.clubadmin.domain.ClubadminUser;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Role;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.datasource.Document;

/**
 * List model for clubs used in the overview view
 *
 * @author Martin Bluemel
 */
public class ModelListClubs<T extends Club> extends ModelListBeans<RapidBean> implements ListModel<RapidBean> {

	/**
	 * for serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor.
	 *
	 * @param doc            the document
	 * @param filterTypename the type to filter
	 */
	public ModelListClubs(final Document doc) {
		super(doc, "org.rapidbeans.clubadmin.domain.Club");
	}

	/**
	 * Filter rules for roles:
	 * 1) Role Trainer: show only the clubs of the
	 * departments the trainer (associated to the user)
	 * is associated to
	 * 2) Role Departmentadministrator: show only the clubs of departments the user
	 * is associated to
	 * 3) Role Superadministrator: no filter
	 */
	protected void fillAuthorizationFilterPart(final ClubadminUser user, final Role role, final StringBuffer buf) {
		switch (role) {
		case Trainer:
			if (user.getIsalsotrainer() != null) {
				buf.append("departments[");
				buf.append("trainers[");
				buf.append("id = '");
				buf.append(user.getIsalsotrainer().getIdString());
				buf.append("']]");
			} else {
				// filter all by filtering for a not existent id
				buf.append("id = '<filter all>'");
			}
			break;
		case DepartmentAdministrator:
			final Set<Department> departments = user.getAuthorizedDepartments();
			if (departments == null || departments.size() == 0) {
				// filter all by filtering for a not existent id
				buf.append("id = '<filter all>'");
			} else {
				buf.append("departments[");
				int i = 0;
				for (Department dep : departments) {
					if (i > 0) {
						buf.append(" || ");
					}
					buf.append("id = '");
					buf.append(dep.getIdString());
					buf.append("'");
					i++;
				}
				buf.append("]");
			}
			break;
		case SuperAdministrator:
			// no filter
			break;
		default:
			// filter all by filtering for a not existent id
			buf.append("id = '<filter all>'");
			break;
		}
	}
}
