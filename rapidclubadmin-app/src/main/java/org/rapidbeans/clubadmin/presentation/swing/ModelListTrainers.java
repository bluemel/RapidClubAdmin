/*
 * Rapid Beans Application RapidClubAdmin: ModelListTrainers.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 04.01.2007
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.util.List;
import java.util.Set;

import org.rapidbeans.clubadmin.domain.Club;
import org.rapidbeans.clubadmin.domain.ClubadminUser;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Role;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.datasource.Document;

/**
 * List model for trainers used in the overview view
 *
 * @author Martin Bluemel
 */
public class ModelListTrainers extends ModelListBeans<Trainer> {

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
	public ModelListTrainers(final Document doc) {
		super(doc, "org.rapidbeans.clubadmin.domain.Trainer");
	}

	/**
	 * Filter rules for roles: 1) Role Trainer: show only the associated trainer 2)
	 * Role Departmentadministrator: show only the trainers of associated
	 * departments 3) Role Superadministrator: no filter
	 */
	protected void fillAuthorizationFilterPart(ClubadminUser user, Role role, StringBuffer buf) {
		switch (role) {
		case Trainer:
			if (user.getIsalsotrainer() != null) {
				buf.append("id = '");
				buf.append(user.getIsalsotrainer().getIdString());
				buf.append("'");
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
				buf.append("departments[");
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

	/**
	 * Extend the trainer filter by clubs.
	 *
	 * @param instances the departments for which parent clubs should be filtered.
	 */
	public void extendFilterClubs(final List<Club> clubs) {
		StringBuffer filterStringBuf = new StringBuffer(this.getFilterType().getName());
		filterStringBuf.append('[');
		if (clubs.size() > 0) {
			final String auhtnFilterPart = getAuthorizationFilterPart();
			if (auhtnFilterPart != null) {
				filterStringBuf.append(auhtnFilterPart);
				filterStringBuf.append(" && ");
			}
			filterStringBuf.append("departments[parentBean[");
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
			filterStringBuf.append("]]");
		}
		filterStringBuf.append(']');
		this.setFilter(filterStringBuf.toString());
		this.updateGUI();
	}

	/**
	 * Extend the trainer filter by departments.
	 *
	 * @param instances the departments for which parent clubs should be filtered.
	 */
	public void extendFilterDepartments(final List<Department> departments) {
		StringBuffer filterStringBuf = new StringBuffer(this.getFilterType().getName());
		filterStringBuf.append('[');
		if (departments.size() > 0) {
			final String auhtnFilterPart = getAuthorizationFilterPart();
			if (auhtnFilterPart != null) {
				filterStringBuf.append(auhtnFilterPart);
				filterStringBuf.append(" && ");
			}
			filterStringBuf.append("departments[");
			int i = 0;
			for (final Department dep : departments) {
				if (i > 0) {
					filterStringBuf.append(" || ");
				}
				filterStringBuf.append("id = '");
				filterStringBuf.append(dep.getIdString());
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
