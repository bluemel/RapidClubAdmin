/*
 * RapidBeans Application RapidClubAdmin: MenuHelper.java
 *
 * Copyright Martin Bluemel, 2009
 *
 * 01.04.2009
 */
package org.rapidbeans.clubadmin.presentation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.rapidbeans.clubadmin.domain.Club;
import org.rapidbeans.clubadmin.domain.ClubadminUser;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.service.CreateTrainingsList;
import org.rapidbeans.clubadmin.service.OpenCurrentTrainingsList;
import org.rapidbeans.clubadmin.service.OpenHistoryTrainingsList;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.MenuEntry;
import org.rapidbeans.presentation.MenuItem;
import org.rapidbeans.presentation.Menubar;
import org.rapidbeans.presentation.Submenu;
import org.rapidbeans.presentation.config.ConfigMenuItem;
import org.rapidbeans.presentation.config.ConfigSubmenu;
import org.rapidbeans.presentation.swing.ActionHandlerActionListener;
import org.rapidbeans.presentation.swing.MenuItemSwing;
import org.rapidbeans.presentation.swing.SubmenuSwing;
import org.rapidbeans.security.User;
import org.rapidbeans.service.Action;
import org.rapidbeans.service.ActionArgument;

public class MenuHelper {

    /**
     * initialize authorization specific menus according to
     * ACLs.
     */
    public static void updateSpecificMenus() {

        final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();

        if (client.getAuthenticatedUser() == null) {
            return;
        }

        final boolean createNew = determineNewAllowed();
        final Set<Department> authDeps = ((ClubadminUser)
                client.getAuthenticatedUser()).getAuthorizedDepartments();
        final Department[] authDepsSorted = authDeps.toArray(new Department[authDeps.size()]);
        Arrays.sort(authDepsSorted);

        final Menubar menubar = client.getMainwindow().getMenubar();
        final SubmenuSwing menuFile = MenuHelper.findSubmenu(menubar, "file");
        final SubmenuSwing menuNew = MenuHelper.findSubmenu(menuFile, "new");

        // "new" menu
        if (createNew) {
            final SubmenuSwing menuTrainingslist = MenuHelper.findSubmenu(menuNew, "trainingslist");
            for (final Department dep : authDepsSorted) {
                if (findMenuitem(menuTrainingslist, dep.getIdString()) == null
                        && dep.getTrainingdates() != null
                        && dep.getTrainingdates().size() > 0) {
                    MenuHelper.createMenuItem(menuTrainingslist, null, dep.getIdString(), new CreateTrainingsList());
                }
            }
        }

        final SubmenuSwing menuOpen = MenuHelper.findSubmenu(menuFile, "open");

        // "open current" menu
        final SubmenuSwing menuOpenCurrent = MenuHelper.findSubmenu(menuOpen, "current");
        for (Department dep : authDepsSorted) {
            if (findMenuitem(menuOpenCurrent, dep.getIdString()) == null
                    && dep.getTrainingdates() != null
                    && dep.getTrainingdates().size() > 0) {
                MenuHelper.createMenuItem(menuOpenCurrent, null, dep.getIdString(), new OpenCurrentTrainingsList());
            }
        }

        // "open history" menu
        final SubmenuSwing menuOpenHistory = MenuHelper.findSubmenu(menuOpen, "history");
        if (getHistorybillingperiods().size() > 0) {
            for (String historyBp : getHistorybillingperiods()) {
                SubmenuSwing historySubmenu = findSubmenu(menuOpenHistory, historyBp);
                if (historySubmenu == null) {
                    historySubmenu = MenuHelper.createSubmenu(menuOpenHistory, historyBp);
                }
                final List<String> histBps = getHistorybillingperiodDepartments(historyBp);
                for (String dep : histBps) {
                    if (findMenuitem(historySubmenu, dep) == null) {
                        if (authDeps.contains(new Department("\"" + dep + "\""))) {
                            MenuHelper.createMenuItem(historySubmenu, historyBp, dep, new OpenHistoryTrainingsList());
                        }
                    }
                }
                final List<MenuEntry> meListClone = new ArrayList<MenuEntry>();
                for (MenuEntry me : historySubmenu.getMenuentrys()) {
                    meListClone.add(me);
                }
                for (MenuEntry me : meListClone) {
                    if (!histBps.contains(me.getName())) {
                        ((JMenu) historySubmenu.getWidget()).remove((JMenuItem) me.getWidget());
                        historySubmenu.removeMenuentry(me);
                    }
                }
            }
        } else {
            ((JMenu) menuOpenHistory.getWidget()).setVisible(false);
        }
    }

    public static SubmenuSwing findSubmenu(final Menubar menubar, final String name) {
        if (menubar != null && menubar.getMenus() != null) {
            for (final Submenu menu : menubar.getMenus()) {
                if (menu.getName().equals(name)) {
                    return (SubmenuSwing) menu;
                }
            }
        }
        return null;
    }

    public static SubmenuSwing findSubmenu(final Submenu menu, final String name) {
        for (final MenuEntry menuEntry : menu.getMenuentrys()) {
            if (menuEntry instanceof SubmenuSwing && menuEntry.getName().equals(name)) {
                return (SubmenuSwing) menuEntry;
            }
        }
        return null;
    }

    public static SubmenuSwing createSubmenu(final SubmenuSwing parentMenu,
            final String historyBp) {
        final ConfigSubmenu config =
            new ConfigSubmenu(new String[]{historyBp});
        final SubmenuSwing submenu = new SubmenuSwing(config,
                ApplicationManager.getApplication(),
                "mainwindow.menubar.file.open.history");
        parentMenu.addMenuentry(submenu);
        ((JMenu) parentMenu.getWidget()).add((JMenu) submenu.getWidget());
        return submenu;
    }

    public static MenuItem findMenuitem(final Submenu menu, final String name) {
        for (final MenuEntry menuEntry : menu.getMenuentrys()) {
            if (menuEntry instanceof MenuItem && menuEntry.getName().equals(name)) {
                return (MenuItem) menuEntry;
            }
        }
        return null;
    }

    /**
     * Create a department menu item.
     *
     * @param parentMenu the parent menu
     * @param dep the department
     * @param action the action to execute when the menu item is activated
     */
    public static void createMenuItem(final SubmenuSwing parentMenu,
            final String bp, final String dep, final Action action) {
        final ConfigMenuItem config =
            new ConfigMenuItem(new String[]{dep});
        final MenuItemSwing depMenuItem = new MenuItemSwing(config,
                ApplicationManager.getApplication(), "mainwindow.menubar.file.trainingslist");
        final List<ActionArgument> actionArguments = new ArrayList<ActionArgument>();
        ActionArgument actionArgument = new ActionArgument();
        actionArgument.setName("department");
        actionArgument.setValue(dep);
        actionArguments.add(actionArgument);
        if (bp != null) {
            actionArgument = new ActionArgument();
            actionArgument.setName("billingperiod");
            actionArgument.setValue(bp);
            actionArguments.add(actionArgument);
        }
        action.setArguments(actionArguments);
        depMenuItem.setAction(action);
        parentMenu.addMenuentry(depMenuItem);
        final JMenuItem depMenuItemWidget = (JMenuItem) depMenuItem.getWidget();
        depMenuItemWidget.addActionListener(
                new ActionHandlerActionListener(action));
        ((JMenu) parentMenu.getWidget()).add(depMenuItemWidget);
    }

    private static List<String> getHistorybillingperiods() {

        final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();

        final List<String> histbps = new ArrayList<String>();
        final String bpDir = "data/history";
        String[] bpFilenames = null;
        if (client.getWebFileManager() != null) {
            bpFilenames = client.getWebFileManager().list(bpDir);
        } else {
            bpFilenames = new File(client.getRoot() + "/" + bpDir).list();
        }
        if (bpFilenames != null) {
            Arrays.sort(bpFilenames);
            for (int i = bpFilenames.length - 1; i >= 0; i--) {
                histbps.add(bpFilenames[i]);
            }
        }
        return histbps;
    }

    private static List<String> getHistorybillingperiodDepartments(
            final String bp) {

        final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();

        final List<String> histdeps = new ArrayList<String>();
        final String depDir = "data/history/" + bp;
        switch (client.getMasterData().getClubs().size()) {
        case 0:
            break;
        case 1:
            final Club singleClub = client.getMasterData().getClubs().iterator().next();
            String[] depFilenames = null;
            if (client.getWebFileManager() != null) {
                depFilenames = client.getWebFileManager().list(depDir);
            } else {
                depFilenames = new File(client.getRoot() + "/" + depDir).list();
            }
            for (String depFilename : depFilenames) {
                if (!depFilename.contains(".")) {
                    histdeps.add(singleClub.getName() + "/" + depFilename);
                }
            }
            break;
         default:
             break;
        }
        return sort(histdeps);
    }

    private static final String[] SA = new String[0];

    public static List<String> sort(List<String> list) {
        final List<String> sorted = new ArrayList<String>(list.size());
        final String[] a = list.toArray(SA);
        Arrays.sort(a);
        for (final String s : a) {
            sorted.add(s);
        }
        return sorted;
    }

    private static boolean determineNewAllowed() {

        final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();

        final Set<Department> authnDepartments = ((ClubadminUser)
                client.getAuthenticatedUser()).getAuthorizedDepartments();
        if ((!((User) client.getAuthenticatedUser()).hasRole("SuperAdministrator"))
            || authnDepartments == null
            || authnDepartments.size() == 0) {
            return false;
        }
        return true;
    }
}
