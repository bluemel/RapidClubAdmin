/*
 * RapidBeans Application RapidClubAdmin: MasterData
 *
 * Copyright Martin Bluemel, 2010
 *
 * 26.02.2010
 */

package org.rapidbeans.clubadmin.domain;


import java.util.ArrayList;
import java.util.List;

import org.rapidbeans.core.basic.RapidEnum;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.clubadmin.domain.Role;


/**
 * Rapid Bean class: MasterData.
 * 
 * @author Martin Bluemel
 */
public class MasterData extends RapidBeanBaseMasterData {

    public void generateUsersForTrainers() {
        final List<RapidEnum> trainerRole = new ArrayList<RapidEnum>();
        trainerRole.add(Role.Trainer);
        for (final Trainer trainer : this.getTrainers()) {
            if (trainer.getUser() == null) {
                String accountname = trainer.getLastname().toLowerCase();
                if (this.getContainer().findBean(
                        ClubadminUser.class.getName(), accountname) != null) {
                    accountname = trainer.getLastname().toLowerCase() + "_"
                        + trainer.getFirstname().toLowerCase().substring(0, 1);
                    if (this.getContainer().findBean(
                            ClubadminUser.class.getName(), accountname) != null) {
                        accountname = trainer.getLastname().toLowerCase() + "_"
                            + trainer.getFirstname().toLowerCase();
                        if (this.getContainer().findBean(
                                ClubadminUser.class.getName(), accountname) != null) {
                            accountname = trainer.getLastname().toLowerCase() + "_"
                                + trainer.getFirstname().toLowerCase() + "_"
                                + trainer.getMiddlename().toLowerCase().substring(0, 1);
                                ;
                             int i = 0;
                             while (this.getContainer().findBean(
                                     ClubadminUser.class.getName(), accountname) != null) {
                                 accountname = trainer.getLastname().toLowerCase() + "_"
                                    + trainer.getFirstname().toLowerCase() + "_"
                                    + trainer.getMiddlename().toLowerCase().substring(0, 1)
                                    + Integer.toString(i);
                                 i++;
                             }
                        }
                    }
                }
                final ClubadminUser newUser = new ClubadminUser(accountname);
                newUser.setLastname(trainer.getLastname());
                newUser.setFirstname(trainer.getFirstname());
                for (final Department dep : trainer.getDepartments()) {
                    newUser.addDepartment(dep);
                }
                newUser.setRoles(trainerRole);
                newUser.resetPwd();
                this.addUser(newUser);
                newUser.setIsalsotrainer(trainer);
            }
        }
    }

    /**
     * default constructor.
     */
    public MasterData() {
        super();
    }

    /**
     * constructor out of a string.
     * @param s the string
     */
    public MasterData(final String s) {
        super(s);
    }

    /**
     * constructor out of a string array.
     * @param sa the string array
     */
    public MasterData(final String[] sa) {
        super(sa);
    }

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(MasterData.class);

    /**
     * @return the bean's type
     */
    public TypeRapidBean getType() {
        return type;
    }
}
