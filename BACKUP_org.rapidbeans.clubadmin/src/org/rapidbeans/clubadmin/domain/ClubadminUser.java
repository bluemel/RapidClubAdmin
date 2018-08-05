/*
 * RapidBeans Application RapidClubAdmin: ClubadminUser
 *
 * Copyright Martin Bluemel, 2008
 *
 * 13.12.2008
 */

package org.rapidbeans.clubadmin.domain;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.security.User;


/**
 * Specific operations of RapidBeans class ClubadminUser.
 *
 * @author Martin Bluemel
 */
public class ClubadminUser extends RapidBeanBaseClubadminUser {

    /**
     * determines if the user has the role with the given name.
     *
     * @param rolename the role's name
     *
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(final String rolename) {
        if (this.getRole() == null) {
            return false;
        }
        final int rolenameOrdinal = Role.valueOf(rolename).ordinal();
        return this.getRole().ordinal() >= rolenameOrdinal;
    }

    // determine all departments the user is authorized to see
    public Set<Department> getAuthorizedDepartments() {

        final Set<Department> authnDeps = new HashSet<Department>();

        // super administrators may access all departments
        if (this.getRole() == Role.SuperAdministrator) {
            MasterData md = (MasterData) this.getParentBean();
            if (md.getClubs() != null) {
                for (Club club : md.getClubs()) {
                    if (club.getDepartments() != null) {
                        for (Department dep : club.getDepartments()) {
                            authnDeps.add(dep);
                        }
                    }
                }
            }
            return authnDeps;
        }

        // add all departments associated directly to the user
        // this includes all departments of a department administrator
        if (this.getDepartments() != null) {
            for (Department dep : this.getDepartments()) {
                authnDeps.add(dep);
            }
        }

        // if the user is trainer, add also all the trainer's departments
        if (this.getIsalsotrainer() != null
            && this.getIsalsotrainer().getDepartments() != null) {
            for (Department dep : this.getIsalsotrainer().getDepartments()) {
                authnDeps.add(dep);
            }
        }

        return authnDeps;
    }

    /**
     * Reset the pwd.
     */
    public void resetPwd() {
        final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();
        if (this.getDepartments() == null || this.getDepartments().size() == 0) {
            app.messageInfo(app.getCurrentLocale().getStringMessage(
                    "warn.user.password.reset", this.getAccountname()));
            this.setPwd(null);
            return;
        }
        if (this.getDepartments().size() == 1
                && ((List<Department>) this.getDepartments()).get(0).getDefaultpassword() != null) {
            this.setPwd(User.hashPwd(
                ((List<Department>) this.getDepartments()).get(0).getDefaultpassword(),
                app.getConfiguration().getAuthorization().getPwdhashalgorithm()));
            app.messageInfo("Warnung: Password für Anwenderkonto \""
                    + this.getAccountname() + "\" wurde auf \""
                    + ((List<Department>) this.getDepartments()).get(0).getDefaultpassword()
                    + "\" zurückgesetzt.", "Warnung");
        } else {
            final Club refClub = (Club) ((List<Department>) this.getDepartments()).get(0).getParentBean();
            boolean allDepsHaveEqualClub = true;
            for (final Department dep : this.getDepartments()) {
                final Club club = (Club) dep.getParentBean();
                if (club != refClub) {
                    allDepsHaveEqualClub = false;
                    break;
                }
            }
            if (allDepsHaveEqualClub
                    && refClub.getDefaultpassword() != null) {
                this.setPwd(User.hashPwd(
                        refClub.getDefaultpassword(),
                        app.getConfiguration().getAuthorization().getPwdhashalgorithm()));
                app.messageInfo("Warnung: Password für Anwenderkonto \""
                        + this.getAccountname() + "\" wurde auf \""
                        + refClub.getDefaultpassword() + "\" zurückgesetzt.",
                        "Warnung");
            } else {
                app.messageInfo("Warnung: Password für Anwenderkonto \""
                        + this.getAccountname() + "\" wurde gelöscht.",
                        "Warnung");
                this.setPwd(null);
            }
        }
    }

    /**
     * default constructor.
     */
    public ClubadminUser() {
        super();
    }

    /**
     * constructor out of a string.
     * @param s the string
     */
    public ClubadminUser(final String s) {
        super(s);
    }

    /**
     * constructor out of a string array.
     * @param sa the string array
     */
    public ClubadminUser(final String[] sa) {
        super(sa);
    }

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(ClubadminUser.class);
	

    /**
     * @return the bean's type
     */
    public TypeRapidBean getType() {
        return type;
    }
}
