/*
 * Rapid Beans Application RapidClubAdmin: ModelListDepartments.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 04.01.2007
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.rapidbeans.clubadmin.domain.Club;
import org.rapidbeans.clubadmin.domain.ClubadminUser;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Role;
import org.rapidbeans.datasource.Document;

/**
 * List model for departments used in the overview view
 *
 * @author Martin Bluemel
 */
public class ModelListDepartments extends ModelListBeans {

	/**
	 * for serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor.
	 *
	 * @param doc            the document
	 * @param filterTypename the type to filter
	 */
	public ModelListDepartments(final Document doc) {
		super(doc, "org.rapidbeans.clubadmin.domain.Department");
	}

	/**
	 * Filter rules for roles: 1) Role Trainer: show only departments of the
	 * associated trainer 2) Role Departmentadministrator: departments the user is
	 * associated to 3) Role Superadministrator: no filter
	 */
	protected void fillAuthorizationFilterPart(ClubadminUser user, Role role, StringBuffer buf) {
		switch (role) {
		case Trainer:
			if (user.getIsalsotrainer() != null) {
				final Collection<Department> departments = user.getIsalsotrainer().getDepartments();
				if (departments != null && departments.size() > 0) {
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
				} else {
					// filter all by filtering for a not existent id
					buf.append("id = '<filter all>'");
				}
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

	/**
	 * extend the departments filter by given parent clubs.
	 *
	 * @param instances the instances to filter.
	 */
	public void extendFilter(final List<Club> clubs) {
		StringBuffer filterStringBuf = new StringBuffer(this.getFilterType().getName());
		filterStringBuf.append('[');
		if (clubs.size() > 0) {
			final String auhtnFilterPart = getAuthorizationFilterPart();
			if (auhtnFilterPart != null) {
				filterStringBuf.append(auhtnFilterPart);
				filterStringBuf.append(" && ");
			}
			filterStringBuf.append("parentBean[");
			int i = 0;
			for (final Club club : clubs) {
				if (i > 0) {
					filterStringBuf.append(" || ");
				}
				filterStringBuf.append("id = '");
				filterStringBuf.append(club.getIdString());
				filterStringBuf.append("'");
				i++;
			}
			filterStringBuf.append("]");
		}
		filterStringBuf.append(']');
		this.setFilter(filterStringBuf.toString());
		this.updateGUI();
	}
}
