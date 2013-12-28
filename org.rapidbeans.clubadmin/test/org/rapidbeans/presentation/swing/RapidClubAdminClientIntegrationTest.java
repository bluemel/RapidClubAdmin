/*
 * Rapid Beans Framework: RapidClubAdminClientIntegrationTest.java
 *
 * Copyright Martin Bluemel, 2006
 *
 * 26.08.2006
 */

package org.rapidbeans.presentation.swing;

import java.io.File;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.CellRendererPane;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListModel;

import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.clubadmin.domain.ClosingPeriod;
import org.rapidbeans.clubadmin.domain.Club;
import org.rapidbeans.clubadmin.domain.Location;
import org.rapidbeans.clubadmin.domain.PersonalSalary;
import org.rapidbeans.clubadmin.domain.RapidClubAdminBusinessLogicException;
import org.rapidbeans.clubadmin.domain.Salary;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.TrainerPlanning;
import org.rapidbeans.clubadmin.domain.TrainerRole;
import org.rapidbeans.clubadmin.domain.Training;
import org.rapidbeans.clubadmin.domain.TrainingDate;
import org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer;
import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminSettings;
import org.rapidbeans.clubadmin.presentation.Settings;
import org.rapidbeans.clubadmin.presentation.swing.ViewTrainingHeldByTrainerList;
import org.rapidbeans.clubadmin.presentation.swing.ViewTrainings;
import org.rapidbeans.clubadmin.service.Umlaut;
import org.rapidbeans.core.basic.PropertyCollection;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.ReadonlyListCollection;
import org.rapidbeans.core.exception.BeanDuplicateException;
import org.rapidbeans.core.exception.BeanNotFoundException;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.core.exception.ValidationInstanceAssocTwiceException;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.datasource.DatasourceException;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.domain.finance.Currency;
import org.rapidbeans.domain.finance.Money;
import org.rapidbeans.domain.math.Time;
import org.rapidbeans.domain.math.UnitTime;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.CreateNewBeansEditorApplyBehaviour;
import org.rapidbeans.presentation.EditorBean;
import org.rapidbeans.presentation.EditorProperty;
import org.rapidbeans.presentation.MainWindow;
import org.rapidbeans.presentation.View;
import org.rapidbeans.presentation.config.ConfigApplication;
import org.rapidbeans.presentation.config.EditorPropNullBehaviour;
import org.rapidbeans.presentation.settings.SettingsAll;

import com.toedter.calendar.JCalendar;

/**
 * UI integration tests.
 * 
 * @author Martin Bluemel
 */
public class RapidClubAdminClientIntegrationTest {

    private static final Logger log = Logger.getLogger(RapidClubAdminClientIntegrationTest.class.getName());

    // Switch off the test mode if you want to see the GUI
    // while debugging in single step
    private static final boolean TEST_MODE = true;

    // private static final boolean TEST_MODE = false;

    private static final int treeViewIndexLocations = 7;

    private static final int treeViewIndexClosigperiods = 8;

    /**
     * the test client.
     */
    private static RapidClubAdminClient client = null;

    @BeforeClass
    public static void setUpClass() {
        TypePropertyCollection.setDefaultCharSeparator(',');
        client = new ApplicationMock();
        ApplicationManager.start(client);
    }

    @AfterClass
    public static void tearDownClass() {
        ApplicationManager.resetApplication();
        client = null;
    }

    /**
     * start the client.
     */
    @Before
    public void setUp() {
        TypePropertyCollection.setDefaultCharSeparator(',');
        TypePropertyCollection.setDefaultCharEscape('\\');
    }

    /**
     * end the client.
     */
    @After
    public void tearDown() {
        if (this.viewMaster != null) {
            client.removeView(this.viewMaster);
            this.viewMaster = null;
        }
        if (this.docMaster != null) {
            client.removeDocument(this.docMaster);
            this.docMaster = null;
        }
        if (this.viewTrainingsList != null) {
            client.removeView(this.viewTrainingsList);
            this.viewTrainingsList = null;
        }
        if (this.docTrainingsList != null) {
            client.removeDocument(this.docTrainingsList);
            this.docTrainingsList = null;
        }
    }

    /**
     * create two duplicate trainers and verify that in both times we get an
     * exception.
     */
    @Test
    public void testCreateTwoDuplicateTrainers() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        Assert.assertEquals("trainers", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(2)
                .getLastPathComponent()).getColProp().getType().getPropName());

        // select property node "trainers" in the tree view and
        // open a bean editor for creating trainers
        masterTree.setSelectionPath(masterTree.getPathForRow(2));
        masterTreeView.createBean();
        EditorBeanSwing createTrainerEditor = (EditorBeanSwing) masterDocView.getEditor(new Trainer(), true);

        // set lastname and firstname so that a Trainer would be created
        // that already exists
        EditorPropertyTextSwing propEditor = (EditorPropertyTextSwing) createTrainerEditor.getPropEditors().get(0);
        ((JTextField) propEditor.getWidget()).setText("Blümel");
        propEditor = (EditorPropertyTextSwing) createTrainerEditor.getPropEditors().get(1);
        ((JTextField) propEditor.getWidget()).setText("Martin");

        // simulate pressing the "Apply" button the 1st time
        try {
            createTrainerEditor.handleActionApply();
            Assert.fail("BeanDuplicateException expected");
        } catch (BeanDuplicateException e) {
            Assert.assertTrue(true);
        }

        // simulate pressing the "Apply" button the 2nd time
        try {
            createTrainerEditor.handleActionApply();
            Assert.fail("BeanDuplicateException expected");
        } catch (BeanDuplicateException e) {
            Assert.assertTrue(true);
        }
    }

    /**
     * create a trainer and verify that a tree node for the trainer is created.
     */
    @Test
    public void testCreateTrainer() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        Assert.assertEquals("trainers", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(2)
                .getLastPathComponent()).getColProp().getType().getPropName());

        // select property node "trainers" in the tree view and
        // open a bean editor for creating trainers
        masterTree.setSelectionPath(masterTree.getPathForRow(2));
        masterTree.expandPath(masterTree.getPathForRow(2));
        Assert.assertEquals("Blümel_Martin_",
                ((Trainer) masterTree.getPathForRow(3).getLastPathComponent()).getIdString());
        masterTreeView.createBean();
        EditorBeanSwing createTrainerEditor = (EditorBeanSwing) masterDocView.getEditor(new Trainer(), true);

        // set lastname and firstname so that a Trainer would be created
        // that already exists
        EditorPropertyTextSwing propEditor = (EditorPropertyTextSwing) createTrainerEditor.getPropEditors().get(0);
        ((JTextField) propEditor.getWidget()).setText("Adalbert");
        propEditor = (EditorPropertyTextSwing) createTrainerEditor.getPropEditors().get(1);
        ((JTextField) propEditor.getWidget()).setText("Alfons");

        // simulate pressing the "Ok" button
        createTrainerEditor.handleActionOk();

        Assert.assertEquals("Adalbert_Alfons_",
                ((Trainer) masterTree.getPathForRow(3).getLastPathComponent()).getIdString());
        Assert.assertEquals("Blümel_Martin_",
                ((Trainer) masterTree.getPathForRow(4).getLastPathComponent()).getIdString());
    }

    /**
     * create a duplicate trainer and verify that no tree node for the trainer
     * is created.
     */
    @Test
    public void testCreateTrainerDuplicate() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        Assert.assertEquals("trainers", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(2)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Assert.assertEquals("trainerattributes", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(6)
                .getLastPathComponent()).getColProp().getType().getPropName());

        // select property node "trainers" in the tree view and
        // open a bean editor for creating trainers
        masterTree.setSelectionPath(masterTree.getPathForRow(2));
        masterTreeView.createBean();
        EditorBeanSwing createTrainerEditor = (EditorBeanSwing) masterDocView.getEditor(new Trainer(), true);

        // set lastname and firstname so that a Trainer would be created
        // that already exists
        EditorPropertyTextSwing propEditor = (EditorPropertyTextSwing) createTrainerEditor.getPropEditors().get(0);
        ((JTextField) propEditor.getWidget()).setText("Blümel");
        propEditor = (EditorPropertyTextSwing) createTrainerEditor.getPropEditors().get(1);
        ((JTextField) propEditor.getWidget()).setText("Martin");

        // simulate pressing the "Apply" button the 1st time
        try {
            createTrainerEditor.handleActionApply();
            Assert.fail("BeanDuplicateException expected");
        } catch (BeanDuplicateException e) {
            Assert.assertTrue(true);
        }

        // close the editor via Cancel button
        createTrainerEditor.handleActionClose();
        Assert.assertEquals("trainerattributes", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(6)
                .getLastPathComponent()).getColProp().getType().getPropName());
    }

    /**
     * create a trainer planning.
     */
    @Test
    public void testCreateTrainerPlanning() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing docView = this.getTestviewTrainingsListExpert();
        Document doc = docView.getDocument();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) docView.getTreeView();
        treeView.setShowBeanLinks(false);
        JTree tree = (JTree) treeView.getTree();
        Assert.assertEquals("clubs", ((DocumentTreeNodePropColComp) tree.getPathForRow(1).getLastPathComponent())
                .getColProp().getType().getPropName());

        // select property node "trainerplannings" of the first
        // training dat of the first department of the first club
        // in the tree view and
        // open a bean editor for creating trainer plannings
        tree.expandPath(tree.getPathForRow(1));
        tree.expandPath(tree.getPathForRow(2));
        tree.expandPath(tree.getPathForRow(3));
        tree.expandPath(tree.getPathForRow(4));
        tree.expandPath(tree.getPathForRow(5));
        tree.expandPath(tree.getPathForRow(6));
        tree.setSelectionPath(tree.getPathForRow(7));
        EditorBeanSwing editor = (EditorBeanSwing) treeView.createBean();
        TrainingDate date = (TrainingDate) tree.getPathForRow(6).getLastPathComponent();
        Assert.assertEquals("Budo-Club Ismaning/Aikido/monday_19:00_Eurythmiesaal 1 Waldorfschule", date.getIdString());
        Assert.assertEquals(1, date.getTrainerplannings().size());
        Assert.assertEquals("Trainer", date.getTrainerplannings().iterator().next().getRole().getName());
        Assert.assertEquals("Blümel_Ulrike_", date.getTrainerplannings().iterator().next().getDefaulttrainers()
                .iterator().next().getIdString());

        // select role "Cotrainer"
        EditorPropertyComboboxSwing pe0 = (EditorPropertyComboboxSwing) editor.getPropEditors().get(0);
        ((JComboBox) pe0.getWidget()).setSelectedIndex(1);
        Assert.assertEquals(1, date.getTrainerplannings().size());

        // select trainer "Blümel_Martin"
        EditorPropertyListSwing pe1 = (EditorPropertyListSwing) editor.getPropEditors().get(1);
        EditorPropertyList2Swing pe2 = pe1.openListEditor();
        Trainer martin = (Trainer) doc.findBean("org.rapidbeans.clubadmin.domain.Trainer", "Blümel_Martin");
        pe2.getWidgetListOut().setSelectedValue(martin, false);
        // pe1.getCheckboxes().get("Blümel_Martin").setSelected(true);

        Assert.assertEquals(1, date.getTrainerplannings().size());
        Assert.assertEquals(7, doc.findBeansByType("org.rapidbeans.clubadmin.domain.TrainerPlanning").size());

        editor.handleActionOk();
        Assert.assertEquals(2, date.getTrainerplannings().size());
        Assert.assertEquals(8, doc.findBeansByType("org.rapidbeans.clubadmin.domain.TrainerPlanning").size());
    }

    /**
     * create a location using OK Button.
     */
    @Test
    public void testCreateLocationOk() {
        int treeViewIndex1 = treeViewIndexLocations + 1;
        int treeViewIndex2 = treeViewIndexLocations + 2;
        int treeViewIndex3 = treeViewIndexLocations + 3;
        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        Assert.assertEquals("locations", ((DocumentTreeNodePropColComp) masterTree
                .getPathForRow(treeViewIndexLocations).getLastPathComponent()).getColProp().getType().getPropName());

        masterTree.expandPath(masterTree.getPathForRow(treeViewIndexLocations));
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule", ((RapidBean) masterTree.getPathForRow(treeViewIndex1)
                .getLastPathComponent()).getIdString());

        // select property node "locations" in the tree view and
        // open a bean editor for creating locations
        masterTree.setSelectionPath(masterTree.getPathForRow(treeViewIndexLocations));
        EditorBeanSwing createTrainerEditor = (EditorBeanSwing) masterTreeView.createBean();

        // set the location name to "A"
        EditorPropertyTextSwing propEditor = (EditorPropertyTextSwing) createTrainerEditor.getPropEditors().get(0);
        ((JTextField) propEditor.getWidget()).setText("A");
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule", ((RapidBean) masterTree.getPathForRow(treeViewIndex1)
                .getLastPathComponent()).getIdString());
        propEditor.fireInputFieldChanged();

        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule", ((RapidBean) masterTree.getPathForRow(treeViewIndex1)
                .getLastPathComponent()).getIdString());

        // simulate pressing the "OK" button the 1st time
        createTrainerEditor.handleActionOk();

        Assert.assertEquals("A",
                ((RapidBean) masterTree.getPathForRow(treeViewIndex1).getLastPathComponent()).getIdString());
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule", ((RapidBean) masterTree.getPathForRow(treeViewIndex2)
                .getLastPathComponent()).getIdString());

        // select property node "locations" in the tree view again and
        // open a bean editor for creating locations
        masterTree.setSelectionPath(masterTree.getPathForRow(treeViewIndexLocations));
        createTrainerEditor = (EditorBeanSwing) masterTreeView.createBean();

        // set the location name to "B"
        propEditor = (EditorPropertyTextSwing) createTrainerEditor.getPropEditors().get(0);
        ((JTextField) propEditor.getWidget()).setText("B");
        propEditor.fireInputFieldChanged();

        Assert.assertEquals("A",
                ((RapidBean) masterTree.getPathForRow(treeViewIndex1).getLastPathComponent()).getIdString());
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule", ((RapidBean) masterTree.getPathForRow(treeViewIndex2)
                .getLastPathComponent()).getIdString());

        // simulate pressing the "OK" button a second time
        createTrainerEditor.handleActionOk();

        Assert.assertEquals("A",
                ((RapidBean) masterTree.getPathForRow(treeViewIndex1).getLastPathComponent()).getIdString());
        Assert.assertEquals("B",
                ((RapidBean) masterTree.getPathForRow(treeViewIndex2).getLastPathComponent()).getIdString());
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule", ((RapidBean) masterTree.getPathForRow(treeViewIndex3)
                .getLastPathComponent()).getIdString());
    }

    /**
     * create a location using Apply Button.
     */
    @Test
    public void testCreateLocationApply() {
        int treeViewIndex1 = treeViewIndexLocations + 1;
        int treeViewIndex2 = treeViewIndexLocations + 2;
        int treeViewIndex3 = treeViewIndexLocations + 3;

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        Assert.assertEquals("locations", ((DocumentTreeNodePropColComp) masterTree
                .getPathForRow(treeViewIndexLocations).getLastPathComponent()).getColProp().getType().getPropName());

        masterTree.expandPath(masterTree.getPathForRow(treeViewIndexLocations));
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule", ((RapidBean) masterTree.getPathForRow(treeViewIndex1)
                .getLastPathComponent()).getIdString());

        // select property node "locations" in the tree view and
        // open a bean editor for creating locations
        masterTree.setSelectionPath(masterTree.getPathForRow(treeViewIndexLocations));
        EditorBeanSwing createTrainerEditor = (EditorBeanSwing) masterTreeView.createBean();

        // set the location name to "A"
        EditorPropertyTextSwing propEditor = (EditorPropertyTextSwing) createTrainerEditor.getPropEditors().get(0);
        ((JTextField) propEditor.getWidget()).setText("A");
        propEditor.fireInputFieldChanged();

        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule", ((RapidBean) masterTree.getPathForRow(treeViewIndex1)
                .getLastPathComponent()).getIdString());

        // simulate pressing the "Apply" button the 1st time
        createTrainerEditor.handleActionApply();

        Assert.assertEquals("A",
                ((RapidBean) masterTree.getPathForRow(treeViewIndex1).getLastPathComponent()).getIdString());
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule", ((RapidBean) masterTree.getPathForRow(treeViewIndex2)
                .getLastPathComponent()).getIdString());

        // set the location name to "B"
        ((JTextField) propEditor.getWidget()).setText("B");
        propEditor.fireInputFieldChanged();

        Assert.assertEquals("A",
                ((RapidBean) masterTree.getPathForRow(treeViewIndex1).getLastPathComponent()).getIdString());
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule", ((RapidBean) masterTree.getPathForRow(treeViewIndex2)
                .getLastPathComponent()).getIdString());

        // simulate pressing the "Apply" button a second time
        createTrainerEditor.handleActionApply();

        Assert.assertEquals("A",
                ((RapidBean) masterTree.getPathForRow(treeViewIndex1).getLastPathComponent()).getIdString());
        Assert.assertEquals("B",
                ((RapidBean) masterTree.getPathForRow(treeViewIndex2).getLastPathComponent()).getIdString());
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule", ((RapidBean) masterTree.getPathForRow(treeViewIndex3)
                .getLastPathComponent()).getIdString());
    }

    /**
     * create a salary with one single trainer attribute.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testCreateSalarySingle() throws InterruptedException {

        // get the document tree view of document "masterdata"
        DocumentViewSwing view = this.getTestviewMasterdata();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) view.getTreeView();
        JTree masterTree = (JTree) treeView.getTree();
        Assert.assertEquals("trainerroles", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(3)
                .getLastPathComponent()).getColProp().getType().getPropName());

        // expand property node "trainerroles" in the tree view and
        masterTree.expandPath(masterTree.getPathForRow(3));
        // expand role "Trainer"
        masterTree.expandPath(masterTree.getPathForRow(4));
        // select property salaries
        masterTree.setSelectionPath(masterTree.getPathForRow(5));
        // open a bean editor for creating a new salary
        final EditorBeanSwing editor = (EditorBeanSwing) treeView.createBean();
        final HashMap<String, Object> buttons = editor.getButtonWidgets();

        Assert.assertFalse(editor.isAnyInputFieldChanged());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Überprüfen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Schließen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        // open the link editor for (key) property "trainerattribute"
        EditorPropertyListSwing propEdTrainerAttrs = (EditorPropertyListSwing) editor.getPropEditor("trainerattribute");
        EditorPropertyList2Swing propEdTrainerAttrsAssoc = propEdTrainerAttrs.openListEditor();
        // select trainer attribute "Abteilungsleiter"
        propEdTrainerAttrsAssoc.getWidgetListOut().setSelectedIndices(new int[] { 2 });
        propEdTrainerAttrsAssoc.addSelectedBeans();
        propEdTrainerAttrsAssoc.handleActionOk();
        final EditorProperty propEdMoney = editor.getPropEditor("money");
        ((JTextField) ((JPanel) propEdMoney.getWidget()).getComponent(0)).setText("10");
        JComboBox cbCurrencyEuro = ((JComboBox) ((JPanel) propEdMoney.getWidget()).getComponent(1));
        cbCurrencyEuro.setSelectedItem(Currency.euro);
        Assert.assertSame(Currency.euro, cbCurrencyEuro.getSelectedItem());
        retry(new Class[] { ArrayIndexOutOfBoundsException.class, BeanNotFoundException.class,
                ConcurrentModificationException.class, NullPointerException.class, EmptyStackException.class },
                new RetryableAction() {
                    public void doSomething() {
                        propEdMoney.fireInputFieldChanged();
                    }
                });
        final EditorProperty propEdTime = editor.getPropEditor("time");
        ((JTextField) ((JPanel) propEdTime.getWidget()).getComponent(0)).setText("1");
        retry(new Class[] { ArrayIndexOutOfBoundsException.class, BeanNotFoundException.class,
                DatasourceException.class, NullPointerException.class }, new RetryableAction() {
            public void doSomething() {
                ((JComboBox) ((JPanel) propEdTime.getWidget()).getComponent(1)).setSelectedItem(UnitTime.h);
            }
        });
        retry(new Class[] { BeanNotFoundException.class, NullPointerException.class }, new RetryableAction() {
            public void doSomething() {
                propEdTime.fireInputFieldChanged();
            }
        });

        Assert.assertTrue(editor.isAnyInputFieldChanged());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertEquals(true, ((JButton) buttons.get("ok")).isEnabled());
            }
        });
        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertEquals("Übernehmen", ((JButton) buttons.get("apply")).getText());
            }
        });
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Abbrechen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        // simulate pressing the "OK" button
        retry(new Class[] { ArrayIndexOutOfBoundsException.class, EmptyStackException.class,
                BeanDuplicateException.class, BeanNotFoundException.class, NullPointerException.class,
                ValidationInstanceAssocTwiceException.class }, new RetryableAction() {
            public void doSomething() {
                editor.handleActionOk();
            }
        });
    }

    /**
     * create a salary with multiple trainer attributes.
     * 
     * @throws InterruptedException
     * @throws InterruptedException
     */
    @Test
    public void testCreateSalaryMultipleEditMoneyAfter() throws InterruptedException {

        // get the document tree view of document "masterdata"
        DocumentViewSwing view = this.getTestviewMasterdata();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) view.getTreeView();
        JTree masterTree = (JTree) treeView.getTree();
        Assert.assertEquals("trainerroles", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(3)
                .getLastPathComponent()).getColProp().getType().getPropName());

        Document doc = view.getDocument();

        List<RapidBean> salaries = doc
                .findBeansByQuery("org.rapidbeans.clubadmin.domain.Salary[parentBean[id = 'Trainer']]");
        Assert.assertEquals(3, salaries.size());
        int i = 0;
        for (RapidBean bean : salaries) {
            switch (i++) {
            case 0:
                Assert.assertEquals("Trainer_Abteilungsleiter,Fachübungsleiter", bean.getIdString());
                break;
            case 1:
                Assert.assertEquals("Trainer_Fachübungsleiter", bean.getIdString());
                break;
            case 2:
                Assert.assertEquals("Trainer_null", bean.getIdString());
                break;
            }
        }

        // expand property node "trainerroles" in the tree view and
        // open a bean editor for creating trainers
        masterTree.expandPath(masterTree.getPathForRow(3));
        masterTree.expandPath(masterTree.getPathForRow(4));
        masterTree.setSelectionPath(masterTree.getPathForRow(5));
        final EditorBeanSwing editor = (EditorBeanSwing) treeView.createBean();
        final HashMap<String, Object> buttons = editor.getButtonWidgets();

        Assert.assertFalse(editor.isAnyInputFieldChanged());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Überprüfen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Schließen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        EditorPropertyListSwing propEdTrainerAttrs = (EditorPropertyListSwing) editor.getPropEditor("trainerattribute");
        final EditorPropertyList2Swing propEdTrainerAttrsAssoc = propEdTrainerAttrs.openListEditor();
        propEdTrainerAttrsAssoc.getWidgetListOut().setSelectedIndices(new int[] { 0, 2, 3 });
        propEdTrainerAttrsAssoc.addSelectedBeans();
        retry(BeanNotFoundException.class, new RetryableAction() {
            public void doSomething() {
                propEdTrainerAttrsAssoc.handleActionOk();
            }
        });

        final EditorProperty propEdMoney = editor.getPropEditor("money");
        ((JTextField) ((JPanel) propEdMoney.getWidget()).getComponent(0)).setText("10");
        retry(new Class[] { ArrayIndexOutOfBoundsException.class, BeanNotFoundException.class,
                ConcurrentModificationException.class, NullPointerException.class, EmptyStackException.class },
                new RetryableAction() {
                    public void doSomething() {
                        propEdMoney.fireInputFieldChanged();
                    }
                });
        retry(100, 300, new RetryableAction() {
            public void doSomething() {
                Assert.assertTrue(((JButton) buttons.get("ok")).isEnabled());
            }
        });
        Assert.assertTrue(editor.isAnyInputFieldChanged());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertEquals(true, ((JButton) buttons.get("ok")).isEnabled());
            }
        });
        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertEquals("Übernehmen", ((JButton) buttons.get("apply")).getText());
            }
        });
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Abbrechen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        // simulate pressing the "O. K." button
        retry(1, 300, new Class[] { ValidationInstanceAssocTwiceException.class, ArrayIndexOutOfBoundsException.class,
                BeanNotFoundException.class, NullPointerException.class, BeanDuplicateException.class,
                BeanNotFoundException.class }, new RetryableAction() {
            public void doSomething() {
                editor.handleActionOk();
            }
        });
        salaries = doc.findBeansByQuery("org.rapidbeans.clubadmin.domain.Salary[parentBean[id = 'Trainer']]");
        Assert.assertEquals(4, salaries.size());
        i = 0;
        for (RapidBean bean : salaries) {
            switch (i++) {
            case 0:
                Assert.assertEquals("Trainer_Abteilungsleiter,Fachübungsleiter", bean.getIdString());
                break;
            case 1:
                Assert.assertEquals("Trainer_Abteilungsleiter,Trainer A,Trainer B", bean.getIdString());
                break;
            case 2:
                Assert.assertEquals("Trainer_Fachübungsleiter", bean.getIdString());
                break;
            case 3:
                Assert.assertEquals("Trainer_null", bean.getIdString());
                break;
            }
        }
    }

    /**
     * create a salary with multiple trainer attributes.
     */
    @Test
    public void testCreateSalaryMultipleEditMoneyBefore() {

        // get the document tree view of document "masterdata"
        final DocumentViewSwing view = this.getTestviewMasterdata();
        final DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) view.getTreeView();
        final JTree masterTree = (JTree) treeView.getTree();
        Assert.assertEquals("trainerroles", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(3)
                .getLastPathComponent()).getColProp().getType().getPropName());

        final Document doc = view.getDocument();
        List<RapidBean> salaries = doc.findBeansByType(Salary.class.getName());
        Assert.assertEquals(5, salaries.size());
        int i = 0;
        for (RapidBean bean : salaries) {
            switch (i++) {
            case 0:
                Assert.assertEquals("Cotrainer_Abteilungsleiter", bean.getIdString());
                break;
            case 1:
                Assert.assertEquals("Cotrainer_null", bean.getIdString());
                break;
            case 2:
                Assert.assertEquals("Trainer_Abteilungsleiter,Fachübungsleiter", bean.getIdString());
                break;
            case 3:
                Assert.assertEquals("Trainer_Fachübungsleiter", bean.getIdString());
                break;
            case 4:
                Assert.assertEquals("Trainer_null", bean.getIdString());
                break;
            }
        }

        // expand property node "trainerroles" in the tree view and
        // open a bean editor for creating trainers
        masterTree.expandPath(masterTree.getPathForRow(3));
        masterTree.expandPath(masterTree.getPathForRow(4));
        masterTree.setSelectionPath(masterTree.getPathForRow(5));
        final EditorBeanSwing editor = (EditorBeanSwing) treeView.createBean();
        final HashMap<String, Object> buttons = editor.getButtonWidgets();

        Assert.assertFalse(editor.isAnyInputFieldChanged());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Überprüfen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Schließen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        // edit the money before having edited all key properties
        final EditorProperty propEdMoney = editor.getPropEditor("money");
        Assert.assertNull(propEdMoney.getProperty().getValue());
        JTextField propEdMoneyTf = (JTextField) ((JPanel) propEdMoney.getWidget()).getComponent(0);
        JComboBox propEdMoneyCb = (JComboBox) ((JPanel) propEdMoney.getWidget()).getComponent(1);
        propEdMoneyTf.setText("10");
        propEdMoney.fireInputFieldChanged();
        // the value stays edited
        Assert.assertEquals("10", propEdMoneyTf.getText());
        Assert.assertEquals("euro", propEdMoneyCb.getSelectedItem().toString());
        // but is not overtaken to the property
        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertNull(propEdMoney.getProperty().getValue());
            }
        });

        // assert the salaries existent so far before creation of the new one
        salaries = doc.findBeansByQuery("org.rapidbeans.clubadmin.domain.Salary[parentBean[id = 'Trainer']]");
        Assert.assertEquals(3, salaries.size());
        i = 0;
        for (RapidBean bean : salaries) {
            switch (i++) {
            case 0:
                Assert.assertEquals("Trainer_Abteilungsleiter,Fachübungsleiter", bean.getIdString());
                break;
            case 1:
                Assert.assertEquals("Trainer_Fachübungsleiter", bean.getIdString());
                break;
            case 2:
                Assert.assertEquals("Trainer_null", bean.getIdString());
                break;
            }
        }

        // edit the associated trainer attributes
        final EditorPropertyListSwing propEdTrainerAttrs = (EditorPropertyListSwing) editor
                .getPropEditor("trainerattribute");
        final EditorPropertyList2Swing propEdTrainerAttrsAssoc = propEdTrainerAttrs.openListEditor();
        propEdTrainerAttrsAssoc.getWidgetListOut().setSelectedIndices(new int[] { 0, 2, 3 });
        propEdTrainerAttrsAssoc.addSelectedBeans();
        propEdTrainerAttrsAssoc.handleActionOk();

        // assert the buttons state
        Assert.assertTrue(editor.isAnyInputFieldChanged());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertTrue(((JButton) buttons.get("ok")).isEnabled());
            }
        });
        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertEquals("Übernehmen", ((JButton) buttons.get("apply")).getText());
            }
        });
        Assert.assertTrue(((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Abbrechen", ((JButton) buttons.get("close")).getText());
        Assert.assertTrue(((JButton) buttons.get("close")).isEnabled());

        // simulate pressing the "O. K." button
        retry(1, 200,
                new Class[] { ArrayIndexOutOfBoundsException.class, BeanNotFoundException.class,
                        NullPointerException.class, ValidationInstanceAssocTwiceException.class,
                        BeanDuplicateException.class }, new RetryableAction() {
                    public void doSomething() {
                        editor.handleActionOk();
                    }
                });
        salaries = doc.findBeansByQuery("org.rapidbeans.clubadmin.domain.Salary[parentBean[id = 'Trainer']]");
        Assert.assertEquals(4, salaries.size());
        i = 0;
        for (RapidBean bean : salaries) {
            switch (i++) {
            case 0:
                Assert.assertEquals("Trainer_Abteilungsleiter,Fachübungsleiter", bean.getIdString());
                break;
            case 1:
                Assert.assertEquals("Trainer_Abteilungsleiter,Trainer A,Trainer B", bean.getIdString());
                break;
            case 2:
                Assert.assertEquals("Trainer_Fachübungsleiter", bean.getIdString());
                break;
            case 3:
                Assert.assertEquals("Trainer_null", bean.getIdString());
                break;
            }
        }
    }

    /**
     * create a duplicate salary with no attribute
     */
    @Test
    public void testCreateSalaryDuplicateWithoutAttribute() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing view = this.getTestviewMasterdata();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) view.getTreeView();
        JTree masterTree = (JTree) treeView.getTree();
        Assert.assertEquals("trainerroles", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(3)
                .getLastPathComponent()).getColProp().getType().getPropName());

        // expand property node "trainerroles" in the tree view and
        // open a bean editor for creating trainers
        masterTree.expandPath(masterTree.getPathForRow(3));
        masterTree.expandPath(masterTree.getPathForRow(4));
        masterTree.setSelectionPath(masterTree.getPathForRow(5));
        EditorBeanSwing editor = (EditorBeanSwing) treeView.createBean();
        HashMap<String, Object> buttons = editor.getButtonWidgets();

        Assert.assertFalse(editor.isAnyInputFieldChanged());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Überprüfen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Schließen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        EditorProperty propEdMoney = editor.getPropEditor("money");
        ((JTextField) ((JPanel) propEdMoney.getWidget()).getComponent(0)).setText("10");
        JComboBox cbCurrencyEuro = ((JComboBox) ((JPanel) propEdMoney.getWidget()).getComponent(1));
        cbCurrencyEuro.setSelectedItem(Currency.euro);
        Assert.assertSame(Currency.euro, cbCurrencyEuro.getSelectedItem());
        propEdMoney.fireInputFieldChanged();

        EditorProperty propEdTime = editor.getPropEditor("time");
        ((JTextField) ((JPanel) propEdTime.getWidget()).getComponent(0)).setText("1");
        ((JComboBox) ((JPanel) propEdTime.getWidget()).getComponent(1)).setSelectedItem(UnitTime.h);
        propEdTime.fireInputFieldChanged();

        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Überprüfen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Abbrechen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        // simulate pressing the "Apply" button the 1st time
        try {
            editor.handleActionApply();
            if (view.getClient().getTestMode()) {
                Assert.fail("BeanDuplicateException expected");
            }
        } catch (BeanDuplicateException e) {
            Assert.assertTrue(true);
        }
        try {
            editor.handleActionOk();
            if (view.getClient().getTestMode()) {
                Assert.fail("BeanDuplicateException expected");
            }
        } catch (BeanDuplicateException e) {
            Assert.assertTrue(true);
        }
    }

    /**
     * create a duplicate salary with attributes
     */
    @Test
    public void testCreateSalaryDuplicateWithAttributes() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing view = this.getTestviewMasterdata();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) view.getTreeView();
        JTree masterTree = (JTree) treeView.getTree();
        Assert.assertEquals("trainerroles", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(3)
                .getLastPathComponent()).getColProp().getType().getPropName());

        // expand property node "trainerroles" in the tree view and
        // open a bean editor for creating trainers
        masterTree.expandPath(masterTree.getPathForRow(3));
        masterTree.expandPath(masterTree.getPathForRow(4));
        masterTree.setSelectionPath(masterTree.getPathForRow(5));
        EditorBeanSwing editor = (EditorBeanSwing) treeView.createBean();
        HashMap<String, Object> buttons = editor.getButtonWidgets();

        Assert.assertFalse(editor.isAnyInputFieldChanged());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Überprüfen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Schließen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        EditorPropertyListSwing propEdTrainerAttrs = (EditorPropertyListSwing) editor.getPropEditor("trainerattribute");
        Assert.assertEquals(EditorPropNullBehaviour.always_null, propEdTrainerAttrs.getNullBehaviour());
        EditorPropertyList2Swing propEdTrainerAttrsAssoc = propEdTrainerAttrs.openListEditor();
        propEdTrainerAttrsAssoc.getWidgetListOut().setSelectedIndices(new int[] { 0, 1 });
        propEdTrainerAttrsAssoc.addSelectedBeans();
        propEdTrainerAttrsAssoc.handleActionOk();
        EditorProperty propEdMoney = editor.getPropEditor("money");
        ((JTextField) ((JPanel) propEdMoney.getWidget()).getComponent(0)).setText("10");
        JComboBox cbCurrencyEuro = ((JComboBox) ((JPanel) propEdMoney.getWidget()).getComponent(1));
        cbCurrencyEuro.setSelectedItem(Currency.euro);
        Assert.assertSame(Currency.euro, cbCurrencyEuro.getSelectedItem());
        propEdMoney.fireInputFieldChanged();

        EditorProperty propEdTime = editor.getPropEditor("time");
        ((JTextField) ((JPanel) propEdTime.getWidget()).getComponent(0)).setText("1");
        ((JComboBox) ((JPanel) propEdTime.getWidget()).getComponent(1)).setSelectedItem(UnitTime.h);
        propEdTime.fireInputFieldChanged();

        Assert.assertTrue(editor.isAnyInputFieldChanged());
        // editor.validateAndUpdateButtons(propEdTrainerAttrs);
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Überprüfen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Abbrechen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        // simulate pressing the "Apply" button the 1st time
        try {
            editor.handleActionApply();
            if (view.getClient().getTestMode()) {
                Assert.fail("BeanDuplicateException expected");
            }
        } catch (BeanDuplicateException e) {
            Assert.assertTrue(true);
        }
        try {
            editor.handleActionOk();
            if (view.getClient().getTestMode()) {
                Assert.fail("BeanDuplicateException expected");
            }
        } catch (BeanDuplicateException e) {
            Assert.assertTrue(true);
        }
    }

    /**
     * Open an editor for creating a new salary that per default is already
     * existing. Determine a non existing one. Create it.
     */
    @Test
    public void testCreateSalaryAfterDuplicateDefaultWithAttributes() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing view = this.getTestviewMasterdata();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) view.getTreeView();
        JTree masterTree = (JTree) treeView.getTree();
        Assert.assertEquals("trainerroles", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(3)
                .getLastPathComponent()).getColProp().getType().getPropName());

        Document doc = view.getDocument();

        // assert the setup, we have already three salaries defined:
        List<RapidBean> salaries = doc
                .findBeansByQuery("org.rapidbeans.clubadmin.domain.Salary[parentBean[id = 'Trainer']]");
        Assert.assertEquals(3, salaries.size());
        int i = 0;
        for (RapidBean bean : salaries) {
            switch (i++) {
            case 0:
                Assert.assertEquals("Trainer_Abteilungsleiter,Fachübungsleiter", bean.getIdString());
                break;
            case 1:
                Assert.assertEquals("Trainer_Fachübungsleiter", bean.getIdString());
                break;
            case 2:
                Assert.assertEquals("Trainer_null", bean.getIdString());
                break;
            }
        }

        // expand property node "trainerroles" in the tree view and
        // open a bean editor for creating trainers
        masterTree.expandPath(masterTree.getPathForRow(3));
        masterTree.expandPath(masterTree.getPathForRow(4));
        masterTree.setSelectionPath(masterTree.getPathForRow(5));
        final EditorBeanSwing editor = (EditorBeanSwing) treeView.createBean();
        final HashMap<String, Object> buttons = editor.getButtonWidgets();

        EditorProperty propEdMoney = editor.getPropEditor("money");
        ((JTextField) ((JPanel) propEdMoney.getWidget()).getComponent(0)).setText("10");
        JComboBox cbCurrencyEuro = ((JComboBox) ((JPanel) propEdMoney.getWidget()).getComponent(1));
        cbCurrencyEuro.setSelectedItem(Currency.euro);
        Assert.assertSame(Currency.euro, cbCurrencyEuro.getSelectedItem());
        propEdMoney.fireInputFieldChanged();

        EditorProperty propEdTime = editor.getPropEditor("time");
        ((JTextField) ((JPanel) propEdTime.getWidget()).getComponent(0)).setText("1");
        ((JComboBox) ((JPanel) propEdTime.getWidget()).getComponent(1)).setSelectedItem(UnitTime.h);
        propEdTime.fireInputFieldChanged();

        Assert.assertEquals(false, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Überprüfen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        EditorPropertyListSwing propEdTrainerAttrs = (EditorPropertyListSwing) editor.getPropEditor("trainerattribute");
        Assert.assertEquals(EditorPropNullBehaviour.always_null, propEdTrainerAttrs.getNullBehaviour());
        final EditorPropertyList2Swing propEdTrainerAttrsAssoc = propEdTrainerAttrs.openListEditor();
        propEdTrainerAttrsAssoc.getWidgetListOut().setSelectedIndices(new int[] { 0, 2, 3 });
        retry(new Class[] { NullPointerException.class }, new RetryableAction() {
            public void doSomething() {
                propEdTrainerAttrsAssoc.addSelectedBeans();
                propEdTrainerAttrsAssoc.handleActionOk();
            }
        });

        Assert.assertTrue(editor.isAnyInputFieldChanged());
        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertTrue(((JButton) buttons.get("ok")).isEnabled());
            }
        });
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        // simulate pressing the "OK" button
        retry(1, 200, new Class[] { ArrayIndexOutOfBoundsException.class, BeanDuplicateException.class,
                BeanNotFoundException.class, NullPointerException.class, ValidationInstanceAssocTwiceException.class },
                new RetryableAction() {
                    public void doSomething() {
                        editor.handleActionOk();
                    }
                });

        salaries = doc.findBeansByQuery("org.rapidbeans.clubadmin.domain.Salary[parentBean[id = 'Trainer']]");
        Assert.assertEquals(4, salaries.size());
        i = 0;
        for (RapidBean bean : salaries) {
            switch (i++) {
            case 0:
                Assert.assertEquals("Trainer_Abteilungsleiter,Fachübungsleiter", bean.getIdString());
                break;
            case 1:
                Assert.assertEquals("Trainer_Abteilungsleiter,Trainer A,Trainer B", bean.getIdString());
                break;
            case 2:
                Assert.assertEquals("Trainer_Fachübungsleiter", bean.getIdString());
                break;
            case 3:
                Assert.assertEquals("Trainer_null", bean.getIdString());
                break;
            }
        }
    }

    /**
     * First create a duplicate salary and afterwards create the right one.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testCreateSalaryAfterDuplicateWithAttributes() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing view = this.getTestviewMasterdata();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) view.getTreeView();
        JTree masterTree = (JTree) treeView.getTree();
        Assert.assertEquals("trainerroles", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(3)
                .getLastPathComponent()).getColProp().getType().getPropName());

        Document doc = view.getDocument();

        // assert the setup, we have already three salaries defined:
        Salary saldef = (Salary) doc.findBean("org.rapidbeans.clubadmin.domain.Salary", "Trainer_null");
        saldef.delete();
        final List<RapidBean> salaries1 = doc
                .findBeansByQuery("org.rapidbeans.clubadmin.domain.Salary[parentBean[id = 'Trainer']]");
        Assert.assertEquals(2, salaries1.size());
        int i = 0;
        for (RapidBean bean : salaries1) {
            switch (i++) {
            case 0:
                Assert.assertEquals("Trainer_Abteilungsleiter,Fachübungsleiter", bean.getIdString());
                break;
            case 1:
                Assert.assertEquals("Trainer_Fachübungsleiter", bean.getIdString());
                break;
            }
        }

        // expand property node "trainerroles" in the tree view and
        // open a bean editor for creating trainers
        masterTree.expandPath(masterTree.getPathForRow(3));
        masterTree.expandPath(masterTree.getPathForRow(4));
        masterTree.setSelectionPath(masterTree.getPathForRow(5));
        final EditorBeanSwing editor = (EditorBeanSwing) treeView.createBean();
        final HashMap<String, Object> buttons = editor.getButtonWidgets();

        EditorProperty propEdMoney = editor.getPropEditor("money");
        ((JTextField) ((JPanel) propEdMoney.getWidget()).getComponent(0)).setText("10");
        JComboBox cbCurrencyEuro = ((JComboBox) ((JPanel) propEdMoney.getWidget()).getComponent(1));
        cbCurrencyEuro.setSelectedItem(Currency.euro);
        Assert.assertSame(Currency.euro, cbCurrencyEuro.getSelectedItem());
        propEdMoney.fireInputFieldChanged();

        EditorProperty propEdTime = editor.getPropEditor("time");
        ((JTextField) ((JPanel) propEdTime.getWidget()).getComponent(0)).setText("1");
        ((JComboBox) ((JPanel) propEdTime.getWidget()).getComponent(1)).setSelectedItem(UnitTime.h);
        propEdTime.fireInputFieldChanged();

        Assert.assertEquals(true, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Übernehmen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        // 1st try: choose a combination of trainer attributes for which a
        // salary
        // already exists
        EditorPropertyListSwing propEdTrainerAttrs = (EditorPropertyListSwing) editor.getPropEditor("trainerattribute");
        Assert.assertEquals(EditorPropNullBehaviour.always_null, propEdTrainerAttrs.getNullBehaviour());
        final EditorPropertyList2Swing propEdTrainerAttrsAssoc1 = propEdTrainerAttrs.openListEditor();
        propEdTrainerAttrsAssoc1.getWidgetListOut().setSelectedIndices(new int[] { 0, 1 });
        propEdTrainerAttrsAssoc1.addSelectedBeans();
        retry(new RetryableAction() {
            public void doSomething() {
                propEdTrainerAttrsAssoc1.handleActionOk();
            }
        });

        Assert.assertTrue(editor.isAnyInputFieldChanged());
        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertFalse(((JButton) buttons.get("ok")).isEnabled());
            }
        });
        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertFalse(((JButton) buttons.get("apply")).isEnabled());
            }
        });
        Assert.assertTrue(((JButton) buttons.get("close")).isEnabled());

        // 2nd try: choose a valid combination of trainer attributes
        propEdTrainerAttrs = (EditorPropertyListSwing) editor.getPropEditor("trainerattribute");
        Assert.assertEquals(EditorPropNullBehaviour.always_null, propEdTrainerAttrs.getNullBehaviour());
        final EditorPropertyList2Swing propEdTrainerAttrsAssoc2 = propEdTrainerAttrs.openListEditor();
        propEdTrainerAttrsAssoc2.getWidgetListOut().setSelectedIndices(new int[] { 0, 1 });
        propEdTrainerAttrsAssoc2.addSelectedBeans();
        retry(new RetryableAction() {
            public void doSomething() {
                propEdTrainerAttrsAssoc2.handleActionOk();
            }
        });

        Assert.assertTrue(editor.isAnyInputFieldChanged());
        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertTrue(((JButton) buttons.get("ok")).isEnabled());
            }
        });
        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertTrue(((JButton) buttons.get("apply")).isEnabled());
            }
        });
        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertTrue(((JButton) buttons.get("close")).isEnabled());
            }
        });

        // simulate pressing the "OK" button
        retry(1, 500, new Class[] { ArrayIndexOutOfBoundsException.class, BeanDuplicateException.class,
                BeanNotFoundException.class, ConcurrentModificationException.class, EmptyStackException.class,
                NullPointerException.class, ValidationInstanceAssocTwiceException.class }, new RetryableAction() {
            public void doSomething() {
                editor.handleActionOk();
            }
        });

        final List<RapidBean> salaries2 = doc
                .findBeansByQuery("org.rapidbeans.clubadmin.domain.Salary[parentBean[id = 'Trainer']]");
        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertEquals(3, salaries2.size());
            }
        });
        i = 0;
        for (RapidBean bean : salaries2) {
            switch (i++) {
            case 0:
                Assert.assertEquals("Trainer_Abteilungsleiter,Fachübungsleiter", bean.getIdString());
                break;
            case 1:
                Assert.assertEquals("Trainer_Abteilungsleiter,Fachübungsleiter,Trainer A,Trainer B", bean.getIdString());
                break;
            case 2:
                Assert.assertEquals("Trainer_Fachübungsleiter", bean.getIdString());
                break;
            }
        }
    }

    /**
     * create a salary with one single trainer attribute.
     */
    @Test
    public void testCreateSalaryPersonalOk() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing view = this.getTestviewMasterdata();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) view.getTreeView();
        JTree masterTree = (JTree) treeView.getTree();
        Assert.assertEquals("trainerroles", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(3)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Document doc = view.getDocument();

        // expand property node "trainerroles" in the tree view and
        masterTree.expandPath(masterTree.getPathForRow(3));
        // expand role "Trainer"
        masterTree.expandPath(masterTree.getPathForRow(4));
        // select property personal salaries
        masterTree.setSelectionPath(masterTree.getPathForRow(6));
        // open a bean editor for creating a new personal salary
        EditorBeanSwing editor = (EditorBeanSwing) treeView.createBean();
        HashMap<String, Object> buttons = editor.getButtonWidgets();

        Assert.assertFalse(editor.isAnyInputFieldChanged());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Überprüfen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Schlie" + Umlaut.SUML + "en", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        // edit (key) property "person"
        final EditorPropertyComboboxSwing propEdPerson = (EditorPropertyComboboxSwing) editor.getPropEditor("person");
        final JComboBox cbPerson = (JComboBox) propEdPerson.getWidget();
        final Trainer berit = (Trainer) doc.findBean(Trainer.class.getName(), "Dahlheimer_Berit_");
        // retry does not help!!!
        cbPerson.setSelectedItem(berit);
        propEdPerson.fireInputFieldChanged();

        // edit property "money"
        EditorProperty propEdMoney = editor.getPropEditor("money");
        ((JTextField) ((JPanel) propEdMoney.getWidget()).getComponent(0)).setText("10");
        JComboBox cbCurrencyEuro = ((JComboBox) ((JPanel) propEdMoney.getWidget()).getComponent(1));
        cbCurrencyEuro.setSelectedItem(Currency.euro);
        propEdMoney.fireInputFieldChanged();

        // edit property "time"
        EditorProperty propEdTime = editor.getPropEditor("time");
        ((JTextField) ((JPanel) propEdTime.getWidget()).getComponent(0)).setText("1");
        ((JComboBox) ((JPanel) propEdTime.getWidget()).getComponent(1)).setSelectedItem(UnitTime.h);
        propEdTime.fireInputFieldChanged();

        Assert.assertTrue(editor.isAnyInputFieldChanged());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Übernehmen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Abbrechen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        PersonalSalary ps = (PersonalSalary) doc.findBean(PersonalSalary.class.getName(), "Trainer_Dahlheimer_Berit_");
        Assert.assertNull(ps);

        // simulate pressing the "OK" button
        editor.handleActionOk();

        ps = (PersonalSalary) doc.findBean(PersonalSalary.class.getName(), "Trainer_Dahlheimer_Berit_");
        Assert.assertNotNull(ps);
        Assert.assertEquals(berit, ps.getPerson());
        Assert.assertEquals(new Money("10 euro"), ps.getMoney());
        Assert.assertEquals(new Time("0.75 h"), ps.getTime());

        masterTree.expandPath(masterTree.getPathForRow(6));
        masterTree.setSelectionPath(masterTree.getPathForRow(7));
        PersonalSalary psEd = (PersonalSalary) masterTree.getSelectionPath().getLastPathComponent();
        Assert.assertSame(ps, psEd);
        editor = (EditorBeanSwing) treeView.editBeans();
        final EditorPropertyComboboxSwing propEdPerson1 = (EditorPropertyComboboxSwing) editor.getPropEditor("person");
        Assert.assertSame(berit, propEdPerson1.getInputFieldValue());
        propEdMoney = editor.getPropEditor("money");
        Assert.assertEquals(new Money("10 euro"), propEdMoney.getInputFieldValue());
        propEdTime = editor.getPropEditor("time");
        Assert.assertEquals(new Time("0.75 h"), propEdTime.getInputFieldValue());
    }

    /**
     * create a personal salary using the apply button and edit afterwards.
     */
    @Test
    public void testCreateSalaryPersonalApply() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing view = this.getTestviewMasterdata();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) view.getTreeView();
        JTree masterTree = (JTree) treeView.getTree();
        Assert.assertEquals("trainerroles", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(3)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Document doc = view.getDocument();

        // expand property node "trainerroles" in the tree view and
        masterTree.expandPath(masterTree.getPathForRow(3));
        // expand role "Trainer"
        masterTree.expandPath(masterTree.getPathForRow(4));
        // select property personal salaries
        masterTree.setSelectionPath(masterTree.getPathForRow(6));
        // open a bean editor for creating a new personal salary
        EditorBeanSwing editor = (EditorBeanSwing) treeView.createBean();
        HashMap<String, Object> buttons = editor.getButtonWidgets();

        Assert.assertFalse(editor.isAnyInputFieldChanged());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Überprüfen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Schlie" + Umlaut.SUML + "en", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        // edit (key) property "person"
        EditorPropertyComboboxSwing propEdPerson = (EditorPropertyComboboxSwing) editor.getPropEditor("person");
        JComboBox cbPerson = (JComboBox) propEdPerson.getWidget();
        Trainer berit = (Trainer) doc.findBean(Trainer.class.getName(), "Dahlheimer_Berit_");
        cbPerson.setSelectedItem(berit);
        propEdPerson.fireInputFieldChanged();

        // edit property "money"
        EditorProperty propEdMoney = editor.getPropEditor("money");
        ((JTextField) ((JPanel) propEdMoney.getWidget()).getComponent(0)).setText("10");
        JComboBox cbCurrencyEuro = ((JComboBox) ((JPanel) propEdMoney.getWidget()).getComponent(1));
        cbCurrencyEuro.setSelectedItem(Currency.euro);
        propEdMoney.fireInputFieldChanged();

        // edit property "time"
        EditorProperty propEdTime = editor.getPropEditor("time");
        ((JTextField) ((JPanel) propEdTime.getWidget()).getComponent(0)).setText("1");
        ((JComboBox) ((JPanel) propEdTime.getWidget()).getComponent(1)).setSelectedItem(UnitTime.h);
        propEdTime.fireInputFieldChanged();

        Assert.assertTrue(editor.isAnyInputFieldChanged());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Übernehmen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Abbrechen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        PersonalSalary ps = (PersonalSalary) doc.findBean(PersonalSalary.class.getName(), "Trainer_Dahlheimer_Berit_");
        Assert.assertNull(ps);

        // simulate pressing the "Apply" button
        editor.handleActionApply();

        ps = (PersonalSalary) doc.findBean(PersonalSalary.class.getName(), "Trainer_Dahlheimer_Berit_");
        Assert.assertNotNull(ps);
        Assert.assertSame(berit, ps.getPerson());
        Assert.assertEquals(new Money("10 euro"), ps.getMoney());
        Assert.assertEquals(new Time("0.75 h"), ps.getTime());

        masterTree.expandPath(masterTree.getPathForRow(6));
        masterTree.setSelectionPath(masterTree.getPathForRow(7));
        PersonalSalary psEd = (PersonalSalary) masterTree.getSelectionPath().getLastPathComponent();
        Assert.assertSame(ps, psEd);

        editor = (EditorBeanSwing) treeView.editBeans();
        propEdPerson = (EditorPropertyComboboxSwing) editor.getPropEditor("person");
        Assert.assertSame(berit, propEdPerson.getInputFieldValue());
        propEdMoney = editor.getPropEditor("money");
        Assert.assertEquals(new Money("10 euro"), propEdMoney.getInputFieldValue());
        propEdTime = editor.getPropEditor("time");
        Assert.assertEquals(new Time("0.75 h"), propEdTime.getInputFieldValue());
    }

    /**
     * create two personal salary using the apply button after each other.
     */
    @Test
    public void testCreateSalaryPersonalApplyConsecutive() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing view = this.getTestviewMasterdata();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) view.getTreeView();
        JTree masterTree = (JTree) treeView.getTree();
        Assert.assertEquals("trainerroles", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(3)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Document doc = view.getDocument();

        // expand property node "trainerroles" in the tree view and
        masterTree.expandPath(masterTree.getPathForRow(3));
        // expand role "Trainer"
        masterTree.expandPath(masterTree.getPathForRow(4));
        // select property personal salaries
        masterTree.setSelectionPath(masterTree.getPathForRow(6));
        // open a bean editor for creating a new personal salary
        EditorBeanSwing editor = (EditorBeanSwing) treeView.createBean();
        HashMap<String, Object> buttons = editor.getButtonWidgets();

        Assert.assertFalse(editor.isAnyInputFieldChanged());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Überprüfen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Schlie" + Umlaut.SUML + "en", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        // edit (key) property "person"
        EditorPropertyComboboxSwing propEdPerson = (EditorPropertyComboboxSwing) editor.getPropEditor("person");
        JComboBox cbPerson = (JComboBox) propEdPerson.getWidget();
        Trainer berit = (Trainer) doc.findBean(Trainer.class.getName(), "Dahlheimer_Berit_");
        cbPerson.setSelectedItem(berit);
        propEdPerson.fireInputFieldChanged();

        // edit property "money"
        EditorPropertyQuantitySwing propEdMoney = (EditorPropertyQuantitySwing) editor.getPropEditor("money");
        ((JTextField) ((JPanel) propEdMoney.getWidget()).getComponent(0)).setText("10");
        JComboBox cbCurrencyEuro = ((JComboBox) ((JPanel) propEdMoney.getWidget()).getComponent(1));
        cbCurrencyEuro.setSelectedItem(Currency.euro);
        propEdMoney.fireInputFieldChanged();

        // edit property "time"
        EditorProperty propEdTime = editor.getPropEditor("time");
        ((JTextField) ((JPanel) propEdTime.getWidget()).getComponent(0)).setText("1");
        ((JComboBox) ((JPanel) propEdTime.getWidget()).getComponent(1)).setSelectedItem(UnitTime.h);
        propEdTime.fireInputFieldChanged();

        Assert.assertTrue(editor.isAnyInputFieldChanged());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Übernehmen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Abbrechen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        PersonalSalary ps = (PersonalSalary) doc.findBean(PersonalSalary.class.getName(), "Trainer_Dahlheimer_Berit_");
        Assert.assertNull(ps);
        ps = (PersonalSalary) doc.findBean(PersonalSalary.class.getName(), "Trainer_Blümel_Martin_");
        Assert.assertNull(ps);

        // simulate pressing the "Apply" button
        editor.handleActionApply();

        Assert.assertSame(berit, propEdPerson.getInputFieldValue());
        Assert.assertNull(propEdMoney.getInputFieldValue());
        Assert.assertEquals(Currency.euro, propEdMoney.getWidgetComboBox().getSelectedItem());
        Assert.assertEquals(new Time("45.00 min"), propEdTime.getInputFieldValue());

        // edit (key) property "person"
        Trainer martin = (Trainer) doc.findBean(Trainer.class.getName(), "Blümel_Martin_");
        cbPerson.setSelectedItem(martin);
        propEdPerson.fireInputFieldChanged();
        // edit property "money"
        ((JTextField) ((JPanel) propEdMoney.getWidget()).getComponent(0)).setText("8");
        propEdMoney.fireInputFieldChanged();

        // simulate pressing the "Apply" button
        editor.handleActionApply();

        ps = (PersonalSalary) doc.findBean(PersonalSalary.class.getName(), "Trainer_Dahlheimer_Berit_");
        Assert.assertNotNull(ps);
        Assert.assertSame(berit, ps.getPerson());
        Assert.assertEquals(new Money("10 euro"), ps.getMoney());
        Assert.assertEquals(new Time("0.75 h"), ps.getTime());

        ps = (PersonalSalary) doc.findBean(PersonalSalary.class.getName(), "Trainer_Blümel_Martin_");
        Assert.assertNotNull(ps);
        Assert.assertSame(martin, ps.getPerson());
        Assert.assertEquals(new Money("8 euro"), ps.getMoney());
        Assert.assertEquals(new Time("45.00 min"), ps.getTime());
    }

    /**
     * create a one year closing period.
     */
    @Test
    public void testCreateClosingPeriod() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        Document master = client.getDocument("masterdata");
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        Assert.assertEquals("trainers", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(2)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Assert.assertEquals("closingperiods",
                ((DocumentTreeNodePropColComp) masterTree.getPathForRow(treeViewIndexClosigperiods)
                        .getLastPathComponent()).getColProp().getType().getPropName());
        Assert.assertNull(master.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod", "Test_20020101"));

        // select property node "closingperiods" in the tree view and
        // open a bean editor for creating closing periods
        masterTree.setSelectionPath(masterTree.getPathForRow(treeViewIndexClosigperiods));
        masterTreeView.createBean();

        EditorBeanSwing createCPEditor = (EditorBeanSwing) masterDocView.getEditor(new ClosingPeriod(), true);

        EditorPropertyTextSwing peName = (EditorPropertyTextSwing) createCPEditor.getPropEditor("name");
        ((JTextField) peName.getWidget()).setText("Test");
        peName.fireInputFieldChanged();
        EditorPropertyDateSwing peFrom = (EditorPropertyDateSwing) createCPEditor.getPropEditor("from");
        ((JTextField) peFrom.getWidget()).setText("01.01.2002");
        peFrom.fireInputFieldChanged();
        EditorPropertyDateSwing peTo = (EditorPropertyDateSwing) createCPEditor.getPropEditor("to");
        ((JTextField) peTo.getWidget()).setText("21.12.2002");
        peTo.fireInputFieldChanged();
        EditorPropertyCheckboxSwing peOneday = (EditorPropertyCheckboxSwing) createCPEditor.getPropEditor("oneday");
        Assert.assertFalse(peOneday.getInputFieldValue().booleanValue());

        // simulate pressing the "OK" button time
        createCPEditor.handleActionOk();
        Assert.assertNotNull(master.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod", "20020101_Test"));
    }

    /**
     * Test creating a one day closing period by setting the same "to" date.
     * checking the "oneday" check box.
     */
    @Test
    public void testCreateClosingPeriodOneday() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        Document master = client.getDocument("masterdata");
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        Assert.assertEquals("trainers", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(2)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Assert.assertEquals("closingperiods",
                ((DocumentTreeNodePropColComp) masterTree.getPathForRow(treeViewIndexClosigperiods)
                        .getLastPathComponent()).getColProp().getType().getPropName());
        Assert.assertNull(master.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod", "Test_20020101"));

        // select property node "closingperiods" in the tree view and
        // open a bean editor for creating closing periods
        masterTree.setSelectionPath(masterTree.getPathForRow(treeViewIndexClosigperiods));
        masterTreeView.createBean();

        EditorBeanSwing createCPEditor = (EditorBeanSwing) masterDocView.getEditor(new ClosingPeriod(), true);

        EditorPropertyTextSwing peName = (EditorPropertyTextSwing) createCPEditor.getPropEditor("name");
        EditorPropertyDateSwing peFrom = (EditorPropertyDateSwing) createCPEditor.getPropEditor("from");
        EditorPropertyDateSwing peTo = (EditorPropertyDateSwing) createCPEditor.getPropEditor("to");
        EditorPropertyCheckboxSwing peOneday = (EditorPropertyCheckboxSwing) createCPEditor.getPropEditor("oneday");
        ((JTextField) peName.getWidget()).setText("Test");
        peName.fireInputFieldChanged();
        ((JTextField) peFrom.getWidget()).setText("1.1.2");
        peFrom.fireInputFieldChanged();
        Assert.assertFalse(peOneday.getInputFieldValue().booleanValue());
        ((JTextField) peTo.getWidget()).setText("1.1.2");
        peTo.fireInputFieldChanged();
        Assert.assertTrue(peOneday.getInputFieldValue().booleanValue());
        Assert.assertTrue(((JComponent) peTo.getWidget()).isEnabled());

        // simulate pressing the "OK" button time
        createCPEditor.handleActionOk();
        Assert.assertNotNull(master.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod", "20020101_Test"));
    }

    /**
     * Test creating a one day closing period by setting the same "to" date.
     * checking the "oneday" check box.
     */
    @Test
    public void testCreateClosingPeriodTwice() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        Document master = client.getDocument("masterdata");
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        Assert.assertEquals("trainers", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(2)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Assert.assertEquals("closingperiods",
                ((DocumentTreeNodePropColComp) masterTree.getPathForRow(treeViewIndexClosigperiods)
                        .getLastPathComponent()).getColProp().getType().getPropName());
        Assert.assertNull(master.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod", "Test1_20020101"));

        // select property node "closingperiods" in the tree view and
        // open a bean editor for creating closing periods
        masterTree.setSelectionPath(masterTree.getPathForRow(treeViewIndexClosigperiods));
        masterTreeView.createBean();

        EditorBeanSwing createCPEditor = (EditorBeanSwing) masterDocView.getEditor(new ClosingPeriod(), true);

        EditorPropertyTextSwing peName = (EditorPropertyTextSwing) createCPEditor.getPropEditor("name");
        EditorPropertyDateSwing peFrom = (EditorPropertyDateSwing) createCPEditor.getPropEditor("from");
        EditorPropertyDateSwing peTo = (EditorPropertyDateSwing) createCPEditor.getPropEditor("to");
        EditorPropertyCheckboxSwing peOneday = (EditorPropertyCheckboxSwing) createCPEditor.getPropEditor("oneday");

        // fill in data for a first closing period
        ((JTextField) peName.getWidget()).setText("Test1");
        peName.fireInputFieldChanged();
        Assert.assertFalse(peFrom.getUIEventLock());
        ((JTextField) peFrom.getWidget()).setText("1.1.2");
        peFrom.fireInputFieldChanged();
        Assert.assertFalse(peFrom.getUIEventLock());
        Assert.assertEquals("01.01.2002", ((JTextField) peFrom.getWidget()).getText());
        Assert.assertFalse(peOneday.getInputFieldValue().booleanValue());
        ((JTextField) peTo.getWidget()).setText("2.1.2");
        peTo.fireInputFieldChanged();

        // simulate pressing the "Apply" button
        createCPEditor.handleActionApply();
        Assert.assertNotNull(master.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod", "20020101_Test1"));
        Assert.assertNull(peName.getInputFieldValue());
        Assert.assertNull(peFrom.getInputFieldValue());
        Assert.assertFalse(peFrom.getUIEventLock());
        Assert.assertNull(peTo.getInputFieldValue());
        Assert.assertFalse(createCPEditor.getEventLock());
        Assert.assertFalse(peOneday.getInputFieldValue().booleanValue());

        // fill in data for a second closing period
        ((JTextField) peName.getWidget()).setText("Test2");
        peName.fireInputFieldChanged();
        ((JTextField) peFrom.getWidget()).setText("1.2.2");
        peFrom.fireInputFieldChanged();
        Assert.assertEquals("01.02.2002", ((JTextField) peFrom.getWidget()).getText());
        Assert.assertFalse(peOneday.getInputFieldValue().booleanValue());
        ((JTextField) peTo.getWidget()).setText("02.02.2002");
        peTo.fireInputFieldChanged();

        // simulate pressing the "Apply" button
        createCPEditor.handleActionApply();
        Assert.assertNotNull(master.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod", "20020201_Test2"));
        Assert.assertNull(peName.getInputFieldValue());
        Assert.assertNull(peFrom.getInputFieldValue());
        Assert.assertNull(peTo.getInputFieldValue());
        Assert.assertFalse(peOneday.getInputFieldValue().booleanValue());
    }

    /**
     * Test creating a one day closing preiod by checking the "oneday" check
     * box.
     */
    @Test
    public void testCreateClosingPeriodOnedayConvenient() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        Document master = client.getDocument("masterdata");
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        Assert.assertEquals("trainers", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(2)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Assert.assertEquals("closingperiods",
                ((DocumentTreeNodePropColComp) masterTree.getPathForRow(treeViewIndexClosigperiods)
                        .getLastPathComponent()).getColProp().getType().getPropName());
        Assert.assertNull(master.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod", "Test_20020101"));

        // select property node "closingperiods" in the tree view and
        // open a bean editor for creating closing periods
        masterTree.setSelectionPath(masterTree.getPathForRow(treeViewIndexClosigperiods));
        masterTreeView.createBean();

        EditorBeanSwing createCPEditor = (EditorBeanSwing) masterDocView.getEditor(new ClosingPeriod(), true);
        EditorPropertyTextSwing peName = (EditorPropertyTextSwing) createCPEditor.getPropEditor("name");
        EditorPropertyDateSwing peFrom = (EditorPropertyDateSwing) createCPEditor.getPropEditor("from");
        EditorPropertyDateSwing peTo = (EditorPropertyDateSwing) createCPEditor.getPropEditor("to");
        EditorPropertyCheckboxSwing peOneday = (EditorPropertyCheckboxSwing) createCPEditor.getPropEditor("oneday");

        ((JTextField) peName.getWidget()).setText("Test");
        peName.fireInputFieldChanged();
        ((JTextField) peFrom.getWidget()).setText("01.01.2002");
        peFrom.fireInputFieldChanged();
        Assert.assertFalse(peOneday.getInputFieldValue().booleanValue());
        Assert.assertEquals("", ((JTextField) peTo.getWidget()).getText());
        Assert.assertTrue(((JComponent) peTo.getWidget()).isEnabled());

        ((JCheckBox) peOneday.getWidget()).setSelected(true);
        Assert.assertTrue(peOneday.getInputFieldValue().booleanValue());
        Assert.assertEquals("01.01.2002", ((JTextField) peTo.getWidget()).getText());
        Assert.assertFalse(((JComponent) peTo.getWidget()).isEnabled());

        // simulate pressing the "OK" button time
        createCPEditor.handleActionOk();
        Assert.assertNotNull(master.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod", "20020101_Test"));
    }

    /**
     * create a closing period add the to date via editor and edit a second
     * time. Verify the title of the tab.
     */
    @Test
    public void testCreateClosingPeriodAndEditAfterwards() {

        int treeViewIndex1 = treeViewIndexClosigperiods + 1;

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        Document master = this.getTestdocMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();

        // select property node "closingperiods" in the tree view
        // and open a bean editor for creating closing periods.
        Assert.assertEquals("closingperiods",
                ((DocumentTreeNodePropColComp) masterTree.getPathForRow(treeViewIndexClosigperiods)
                        .getLastPathComponent()).getColProp().getType().getPropName());
        masterTree.setSelectionPath(masterTree.getPathForRow(treeViewIndexClosigperiods));
        masterTreeView.createBean();
        EditorBeanSwing createCPEditor = (EditorBeanSwing) masterDocView.getEditor(new ClosingPeriod(), true);

        // Edit the new closing period's properties and create it
        EditorPropertyDateSwing pe2 = (EditorPropertyDateSwing) createCPEditor.getPropEditors().get(1);
        ((JTextField) pe2.getWidget()).setText("01.01.2001");
        pe2.fireInputFieldChanged();
        EditorPropertyDateSwing pe3 = (EditorPropertyDateSwing) createCPEditor.getPropEditors().get(2);
        ((JTextField) pe3.getWidget()).setText("02.01.2001");
        pe3.fireInputFieldChanged();
        EditorPropertyTextSwing pe1 = (EditorPropertyTextSwing) createCPEditor.getPropEditors().get(0);
        ((JTextField) pe1.getWidget()).setText("Test");
        pe1.fireInputFieldChanged();
        createCPEditor.handleActionOk();
        ClosingPeriod cp20010101 = (ClosingPeriod) master.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod",
                "20010101_Test");
        Assert.assertNotNull(cp20010101);
        Assert.assertEquals("Test", cp20010101.getName());
        Assert.assertEquals("20010101", cp20010101.getProperty("from").toString());
        Assert.assertEquals("20010102", cp20010101.getProperty("to").toString());

        // open the editor for editing and change the "to" date
        masterTree.expandPath(masterTree.getPathForRow(treeViewIndexClosigperiods));
        masterTree.setSelectionPath(masterTree.getPathForRow(treeViewIndex1));
        EditorBeanSwing editCpEditor = (EditorBeanSwing) masterTreeView.editBeans();
        JTabbedPane editorPane = masterDocView.getEditorPane();
        Assert.assertEquals(1, editorPane.getTabCount());
        Assert.assertEquals("Schließzeitraum: Test 01", editorPane.getTitleAt(0));
        pe3 = (EditorPropertyDateSwing) editCpEditor.getPropEditor("to");
        ((JTextField) pe3.getWidget()).setText("03.01.2001");
        pe3.fireInputFieldChanged();
        editCpEditor.handleActionOk();
        Assert.assertEquals("Test", cp20010101.getName());
        Assert.assertEquals("20010101", cp20010101.getProperty("from").toString());
        Assert.assertEquals("20010103", cp20010101.getProperty("to").toString());

        // open the editor for editing this bean a 2nd time
        // and check the title
        masterTree.setSelectionPath(masterTree.getPathForRow(treeViewIndex1));
        editCpEditor = (EditorBeanSwing) masterTreeView.editBeans();
        editorPane = masterDocView.getEditorPane();
        Assert.assertEquals(1, editorPane.getTabCount());
        Assert.assertEquals("Schließzeitraum: Test 01", editorPane.getTitleAt(0));
    }

    /**
     * create a closing period and a location while selecting all closing
     * periods including the new one.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testCreateClosingPeriodAndLocation() throws InterruptedException {

        int treeViewIndex0 = 9;
        int treeViewIndex1 = treeViewIndex0 + 1;

        // get the document tree view of document "masterdata"
        DocumentViewSwing docView = this.getTestviewTrainingsListExpert();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) docView.getTreeView();
        JTree tree = (JTree) treeView.getTree();
        Assert.assertEquals("locations", ((DocumentTreeNodePropColComp) tree.getPathForRow(treeViewIndex0)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Assert.assertEquals("closingperiods", ((DocumentTreeNodePropColComp) tree.getPathForRow(treeViewIndex1)
                .getLastPathComponent()).getColProp().getType().getPropName());

        // Create a closing period "xxx", "01.01.2001", "01.02.2001"
        tree.setSelectionPath(tree.getPathForRow(treeViewIndex1));
        EditorBean ed = treeView.createBean();
        EditorProperty ped = (EditorProperty) ed.getPropEditors().get(0);
        ((JTextField) ped.getWidget()).setText("xxx");
        ped.fireInputFieldChanged();
        Assert.assertEquals("xxx", ((JTextField) ped.getWidget()).getText());
        ped = (EditorProperty) ed.getPropEditors().get(1);
        ((JTextField) ped.getWidget()).setText("01.01.2001");
        ped.fireInputFieldChanged();
        ped = (EditorProperty) ed.getPropEditors().get(2);
        ((JTextField) ped.getWidget()).setText("01.02.2001");
        ped.fireInputFieldChanged();
        ed.handleActionOk();
        ClosingPeriod cp = (ClosingPeriod) ed.getBean();

        // Create a location "yyy", "zzz", "111" closed on
        // the new closing period "xxx"
        tree.setSelectionPath(tree.getPathForRow(treeViewIndex0));
        ed = treeView.createBean();
        ped = (EditorProperty) ed.getPropEditors().get(0);
        ((JTextField) ped.getWidget()).setText("yyy");
        ped.fireInputFieldChanged();
        Assert.assertEquals("yyy", ((JTextField) ped.getWidget()).getText());
        ped = (EditorProperty) ed.getPropEditors().get(1);
        ((JTextField) ped.getWidget()).setText("zzz");
        ped.fireInputFieldChanged();
        ped = (EditorProperty) ed.getPropEditors().get(2);
        ((JTextField) ped.getWidget()).setText("111");
        ped.fireInputFieldChanged();
        ped = (EditorProperty) ed.getPropEditors().get(0);
        Assert.assertEquals("yyy", ((JTextField) ped.getWidget()).getText());
        EditorPropertyListSwing ped1 = (EditorPropertyListSwing) ed.getPropEditors().get(3);
        EditorPropertyList2Swing ped2 = ped1.openListEditor();
        JList rightList = ped2.getWidgetListOut();
        rightList.setSelectedValue(cp, false);
        ed.handleActionOk();
    }

    /**
     * create a closing period and determine the location in one step.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testCreateClosingPeriodWithLocationAll() throws InterruptedException {
        testCreateClosingPeriodWithLocation(CreateNewBeansEditorApplyBehaviour.resetall);
    }

    /**
     * create a closing period and determine the location in one step.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testCreateClosingPeriodWithLocationKey() throws InterruptedException {
        testCreateClosingPeriodWithLocation(CreateNewBeansEditorApplyBehaviour.resetkey);
    }

    /**
     * create a closing period and determine the location in one step.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testCreateClosingPeriodWithLocationNone() throws InterruptedException {
        testCreateClosingPeriodWithLocation(CreateNewBeansEditorApplyBehaviour.resetnothing);
    }

    /**
     * create a closing period and determine the location in one step.
     * 
     * @param editorCreateApplyMode
     *            editor create apply mode
     * @throws InterruptedException
     */
    private void testCreateClosingPeriodWithLocation(final CreateNewBeansEditorApplyBehaviour editorCreateApplyMode)
            throws InterruptedException {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        final Document master = client.getDocument("masterdata");
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();

        // before setting any key property we have
        // a certain number of closing periods
        Assert.assertEquals(5, master.findBeansByType("org.rapidbeans.clubadmin.domain.ClosingPeriod").size());
        Location loc = (Location) master.findBean("org.rapidbeans.clubadmin.domain.Location",
                "Turnhalle Grundschule Süd");
        Assert.assertSame(3, loc.getClosedons().size());

        // select property node "closingperiods" in the tree view and
        // open a bean editor for creating closing periods
        masterTree.setSelectionPath(masterTree.getPathForRow(treeViewIndexClosigperiods));
        final EditorBeanSwing createCPEditor = (EditorBeanSwing) masterTreeView.createBean();
        createCPEditor.setCreateApplyMode(editorCreateApplyMode);

        // set the name to "Test"
        // the editor's bean should be added to the document
        // (container) then
        final EditorPropertyDateSwing peFrom = (EditorPropertyDateSwing) createCPEditor.getPropEditor("from");
        Assert.assertTrue(((JTextField) peFrom.getWidget()).isEditable());
        final EditorPropertyDateSwing peTo = (EditorPropertyDateSwing) createCPEditor.getPropEditor("to");
        Assert.assertTrue(((JTextField) peTo.getWidget()).isEditable());
        final EditorPropertyTextSwing peName = (EditorPropertyTextSwing) createCPEditor.getPropEditor("name");
        Assert.assertTrue(((JTextField) peName.getWidget()).isEditable());

        ((JTextField) peName.getWidget()).setText("Test");
        peName.fireInputFieldChanged();
        ((JTextField) peFrom.getWidget()).setText("01.01.2001");
        peFrom.fireInputFieldChanged();
        ((JTextField) peTo.getWidget()).setText("02.01.2001");
        peTo.fireInputFieldChanged();

        // set location "Turnhalle Grundschule Süd"
        EditorPropertyListSwing peLoc = (EditorPropertyListSwing) createCPEditor.getPropEditor("locations");
        final JList list = peLoc.getWidgetList();
        Assert.assertEquals(0, list.getModel().getSize());
        final EditorPropertyList2Swing peLocList = peLoc.openListEditor();
        RapidBean cp1 = master.findBean("org.rapidbeans.clubadmin.domain.Location", "Turnhalle Grundschule Süd");
        Assert.assertNotNull(cp1);
        peLocList.getWidgetListOut().setSelectedValue(cp1, false);
        retry(new Class[] { NullPointerException.class }, new RetryableAction() {
            public void doSomething() {
                peLocList.addSelectedBeans();
            }
        });
        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertEquals(1, list.getModel().getSize());
            }
        });

        // simulate pressing the "OK" button
        retry(new RetryableAction() {
            public void doSomething() {
                createCPEditor.handleActionOk();
            }
        });

        try {
            Assert.assertEquals(6, master.findBeansByType("org.rapidbeans.clubadmin.domain.ClosingPeriod").size());
            final ClosingPeriod cp20010101 = (ClosingPeriod) master.findBean(
                    "org.rapidbeans.clubadmin.domain.ClosingPeriod", "20010101_Test");
            Assert.assertNotNull(cp20010101);
            Assert.assertEquals("Test", cp20010101.getName());
            Assert.assertEquals("20010101", cp20010101.getProperty("from").toString());
            retry(new RetryableAction() {
                public void doSomething() {
                    Assert.assertEquals("20010102", cp20010101.getProperty("to").toString());
                    Collection<Location> locs = cp20010101.getLocations();
                    Assert.assertEquals(1, locs.size());
                    Assert.assertEquals("Turnhalle Grundschule Süd", locs.iterator().next().getIdString());
                    final Location loc1 = (Location) master.findBean("org.rapidbeans.clubadmin.domain.Location",
                            "Turnhalle Grundschule Süd");
                    Assert.assertSame(4, loc1.getClosedons().size());
                }
            });
        } catch (RuntimeException e) {
            Assert.fail("Sleep does not help!!!!!!!!!!!!!");
        }
    }

    /**
     * open the dialog to create a new closing period. insert a to date less
     * than from and verify that the Apply button has the check text.
     */
    @Test
    public void testCreateClosingPeriodInvalidTo() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        Assert.assertEquals("closingperiods",
                ((DocumentTreeNodePropColComp) masterTree.getPathForRow(treeViewIndexClosigperiods)
                        .getLastPathComponent()).getColProp().getType().getPropName());

        // select property node "closingperiods" in the tree view and
        // open a bean editor for creating closing periods
        masterTree.setSelectionPath(masterTree.getPathForRow(treeViewIndexClosigperiods));
        masterTreeView.createBean();
        EditorBeanSwing createCPEditor = (EditorBeanSwing) masterDocView.getEditor(new ClosingPeriod(), true);

        // set a to date smaller than the from date
        EditorPropertyTextSwing peText = (EditorPropertyTextSwing) createCPEditor.getPropEditors().get(0);
        ((JTextField) peText.getWidget()).setText("Independence Day");
        EditorPropertyDateSwing peDate = (EditorPropertyDateSwing) createCPEditor.getPropEditor("from");
        ((JTextField) peDate.getWidget()).setText("04.07.2006");
        peDate.fireInputFieldChanged();
        peDate = (EditorPropertyDateSwing) createCPEditor.getPropEditor("to");
        ((JTextField) peDate.getWidget()).setText("03.07.2006");
        peDate.fireInputFieldChanged();
        JButton applyButton = createCPEditor.getButtonApply();
        Assert.assertEquals(createCPEditor.getLocale().getStringGui("commongui.text.check"), applyButton.getText());
        Assert.assertEquals(EditorPropertySwing.COLOR_INVALID, ((JTextField) peDate.getWidget()).getBackground());

        // simulate pressing the "Apply" button the 1st time
        try {
            createCPEditor.handleActionApply();
            Assert.fail("ValidationException expected");
        } catch (ValidationException e) {
            Assert.assertTrue(true);
        }

        // simulate pressing the "Apply" button the 2nd time
        try {
            createCPEditor.handleActionApply();
            Assert.fail("ValidationException expected");
        } catch (ValidationException e) {
            Assert.assertTrue(true);
        }

        // correct the value
        ((JTextField) peDate.getWidget()).setText("04.07.2006");
        peDate.fireInputFieldChanged();
        Assert.assertEquals(createCPEditor.getLocale().getStringGui("commongui.text.apply"), applyButton.getText());
        Assert.assertEquals(EditorPropertySwing.COLOR_MANDATORY, ((JTextField) peDate.getWidget()).getBackground());
    }

    /**
     * create a special training.
     * 
     * @throws ParseException
     */
    @Test
    public void testCreateTrainingSpecial() throws ParseException {

        // get the document tree view of document "masterdata"
        DocumentViewSwing expertTrainingsListView = this.getTestviewTrainingsListExpert();
        DocumentTreeViewSwing expertTreeView = (DocumentTreeViewSwing) expertTrainingsListView.getTreeView();
        JTree expertTree = (JTree) expertTreeView.getTree();
        expertTree.setSelectionPath(expertTree.getPathForRow(3));
        expertTree.expandPath(expertTree.getPathForRow(3));
        expertTree.expandPath(expertTree.getPathForRow(4));
        expertTree.expandPath(expertTree.getPathForRow(5));
        expertTree.expandPath(expertTree.getPathForRow(6));
        expertTree.setSelectionPath(expertTree.getPathForRow(9));
        EditorBeanSwing createTrainingSpecialEditor = (EditorBeanSwing) expertTreeView.createBean();
        org.rapidbeans.clubadmin.presentation.swing.EditorPropertyDateSwing pedDate = (org.rapidbeans.clubadmin.presentation.swing.EditorPropertyDateSwing) createTrainingSpecialEditor
                .getPropEditor("date");
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        ((JCalendar) pedDate.getWidget()).setDate(df.parse("22.01.2006"));
        EditorPropertyTextSwing pedTimestart = (EditorPropertyTextSwing) createTrainingSpecialEditor
                .getPropEditor("timestart");
        ((JTextField) pedTimestart.getWidget()).setText("10:15");
        EditorPropertyTextSwing pedName = (EditorPropertyTextSwing) createTrainingSpecialEditor.getPropEditor("name");
        ((JTextField) pedName.getWidget()).setText("First Special Training");
        EditorPropertyComboboxSwing pedLocation = (EditorPropertyComboboxSwing) createTrainingSpecialEditor
                .getPropEditor("location");
        ((JComboBox) pedLocation.getWidget()).setSelectedIndex(0);
        EditorPropertyTextSwing pedTimeend = (EditorPropertyTextSwing) createTrainingSpecialEditor
                .getPropEditor("timeend");
        ((JTextField) pedTimeend.getWidget()).setText("11:45");

        // simulate pressing the "Ok" button
        createTrainingSpecialEditor.handleActionOk();

        // Assert.assertEquals("Adalbert_Alfons_", ((Trainer)
        // masterTree.getPathForRow(3).getLastPathComponent()).getIdString());
        // Assert.assertEquals("Blümel_Martin_", ((Trainer)
        // masterTree.getPathForRow(4).getLastPathComponent()).getIdString());
    }

    /**
     * Test deleting a training date which leads to a fairly complex delete
     * cascade.
     */
    @Test
    public void testDeleteTrainingDate() {

        // get the document tree view of the test TrainingsList document
        DocumentViewSwing docView = this.getTestviewTrainingsListExpert();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) docView.getTreeView();
        treeView.setShowBeanLinks(false);
        JTree tree = (JTree) treeView.getTree();
        Assert.assertEquals("clubs", ((DocumentTreeNodePropColComp) tree.getPathForRow(1).getLastPathComponent())
                .getColProp().getType().getPropName());

        tree.expandPath(tree.getPathForRow(1));
        tree.expandPath(tree.getPathForRow(2));
        tree.expandPath(tree.getPathForRow(3));
        tree.expandPath(tree.getPathForRow(4));
        tree.expandPath(tree.getPathForRow(5));
        tree.expandPath(tree.getPathForRow(6));
        TrainingDate date = (TrainingDate) tree.getPathForRow(6).getLastPathComponent();
        Assert.assertEquals("Budo-Club Ismaning/Aikido/monday_19:00_Eurythmiesaal 1 Waldorfschule", date.getIdString());
        DocumentTreeNodePropColComp colProp = (DocumentTreeNodePropColComp) tree.getPathForRow(7)
                .getLastPathComponent();
        Assert.assertEquals("trainerplannings", colProp.getColProp().getType().getPropName());
        colProp = (DocumentTreeNodePropColComp) tree.getPathForRow(8).getLastPathComponent();
        Assert.assertEquals("trainings", colProp.getColProp().getType().getPropName());
        tree.expandPath(tree.getPathForRow(8));
        Training training = (Training) tree.getPathForRow(9).getLastPathComponent();
        Assert.assertEquals("Budo-Club Ismaning/Aikido/monday_19:00_Eurythmiesaal 1 Waldorfschule/20060102",
                training.getIdString());
        training = (Training) tree.getPathForRow(21).getLastPathComponent();
        Assert.assertEquals("Budo-Club Ismaning/Aikido/monday_19:00_Eurythmiesaal 1 Waldorfschule/20060327",
                training.getIdString());
        date = (TrainingDate) tree.getPathForRow(22).getLastPathComponent();
        Assert.assertEquals("Budo-Club Ismaning/Aikido/monday_19:30_Turnhalle Grundschule Süd", date.getIdString());
        date = (TrainingDate) tree.getPathForRow(23).getLastPathComponent();
        Assert.assertEquals("Budo-Club Ismaning/Aikido/monday_20:30_Eurythmiesaal 1 Waldorfschule", date.getIdString());

        Document doc = docView.getDocument();
        Assert.assertNotNull(doc.findBean("org.rapidbeans.clubadmin.domain.TrainingDate",
                "Budo-Club Ismaning/Aikido/monday_19:00_Eurythmiesaal 1 Waldorfschule"));
        Assert.assertNotNull(doc.findBean("org.rapidbeans.clubadmin.domain.Training",
                "Budo-Club Ismaning/Aikido/monday_19:00_Eurythmiesaal 1 Waldorfschule/20060102"));
        Assert.assertNotNull(doc.findBean("org.rapidbeans.clubadmin.domain.Training",
                "Budo-Club Ismaning/Aikido/monday_19:00_Eurythmiesaal 1 Waldorfschule/20060327"));

        // delete TrainingDate
        // "Budo-Club Ismaning/Aikido/monday_19:00_Eurythmiesaal 1
        // Waldorfschule"
        tree.setSelectionPath(tree.getPathForRow(6));
        treeView.deleteBeans();

        // assert deletion from document
        Assert.assertNull(doc.findBean("org.rapidbeans.clubadmin.domain.TrainingDate",
                "Budo-Club Ismaning/Aikido/monday_19:00_Eurythmiesaal 1 Waldorfschule"));
        // assert one part of the delete cascade
        Assert.assertNull(doc.findBean("org.rapidbeans.clubadmin.domain.Training",
                "Budo-Club Ismaning/Aikido/monday_19:00_Eurythmiesaal 1 Waldorfschule/20060102"));
        Assert.assertNull(doc.findBean("org.rapidbeans.clubadmin.domain.Training",
                "Budo-Club Ismaning/Aikido/monday_19:00_Eurythmiesaal 1 Waldorfschule/20060327"));

        date = (TrainingDate) tree.getPathForRow(6).getLastPathComponent();
        Assert.assertEquals("Budo-Club Ismaning/Aikido/monday_19:30_Turnhalle Grundschule Süd", date.getIdString());
        date = (TrainingDate) tree.getPathForRow(7).getLastPathComponent();
        Assert.assertEquals("Budo-Club Ismaning/Aikido/monday_20:30_Eurythmiesaal 1 Waldorfschule", date.getIdString());
    }

    /**
     * Fixes a problem while deleting closing periods..
     * 
     * @throws MalformedURLException
     */
    @Test
    public void testDeleteClosingPeriod() throws MalformedURLException {
        // get the document tree view of the test TrainingsList document
        DocumentViewSwing docView = this.getTestviewTrainingsListExpert();
        Document doc = docView.getDocument();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) docView.getTreeView();
        treeView.setShowBeanLinks(false);
        JTree tree = (JTree) treeView.getTree();
        tree.expandPath(tree.getPathForRow(8));
        tree.expandPath(tree.getPathForRow(11));
        tree.setSelectionPath(tree.getPathForRow(9));

        ClosingPeriod xmasholidays = (ClosingPeriod) doc.findBean(ClosingPeriod.class.getName(),
                "20051222_Weihnachtsferien");
        Assert.assertNotNull(xmasholidays);

        treeView.deleteBeans();

        xmasholidays = (ClosingPeriod) doc.findBean(ClosingPeriod.class.getName(), "20051222_Weihnachtsferien");
        Assert.assertNull(xmasholidays);

        File testfile = new File("testdata/testDeleteClosingPeriod.xml");
        doc.setUrl(testfile.toURI().toURL());
        doc.save();
        doc = new Document(testfile);
        docView = (DocumentViewSwing) ApplicationManager.getApplication().openDocumentView(doc, "trainingslist",
                "expert");
        testfile.delete();
    }

    /**
     * Test if a bean editor is correctly updated. Change the collection
     * property of one of two beans that are inversely linked with each other
     * (ClosingPeriod and Location) and test the update of all property editors.
     */
    @Test
    public void testEditorUpdateClosingPeriodLocation() {

        int treeViewIndex1 = treeViewIndexClosigperiods + 1;
        int treeViewIndex2 = treeViewIndexClosigperiods + 2;
        int treeViewIndex4 = treeViewIndexClosigperiods + 4;

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        masterTreeView.setShowBeanLinks(false);
        JTree masterTree = (JTree) masterTreeView.getTree();
        Assert.assertEquals("locations", ((DocumentTreeNodePropColComp) masterTree
                .getPathForRow(treeViewIndexLocations).getLastPathComponent()).getColProp().getType().getPropName());
        Assert.assertEquals("closingperiods",
                ((DocumentTreeNodePropColComp) masterTree.getPathForRow(treeViewIndexClosigperiods)
                        .getLastPathComponent()).getColProp().getType().getPropName());

        // expand locations and closing periods
        masterTree.expandPath(masterTree.getPathForRow(treeViewIndexClosigperiods));
        masterTree.expandPath(masterTree.getPathForRow(treeViewIndexLocations));
        Assert.assertEquals("locations", ((DocumentTreeNodePropColComp) masterTree
                .getPathForRow(treeViewIndexLocations).getLastPathComponent()).getColProp().getType().getPropName());
        Assert.assertEquals("closingperiods", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(treeViewIndex2)
                .getLastPathComponent()).getColProp().getType().getPropName());

        // check Location "Turnhalle Grundschule Süd"
        masterTree.setSelectionPath(masterTree.getPathForRow(treeViewIndex1));
        masterTreeView.editBeans();
        EditorBeanSwing edLoc = (EditorBeanSwing) masterDocView.getEditor(
                (RapidBean) masterTree.getPathForRow(treeViewIndex1).getLastPathComponent(), false);
        Location loc = (Location) edLoc.getBean();
        Assert.assertEquals("Turnhalle Grundschule Süd", edLoc.getPropEditors().get(0).getInputFieldValue());
        EditorPropertyListSwing propEdLocCps = (EditorPropertyListSwing) edLoc.getPropEditors().get(3);
        JList listPropEdLocCps = propEdLocCps.getWidgetList();
        Assert.assertEquals(3, listPropEdLocCps.getModel().getSize());
        Assert.assertEquals("20051222_Weihnachtsferien",
                ((RapidBean) listPropEdLocCps.getModel().getElementAt(0)).getIdString());

        // check Closing Period "Schulputztag"
        masterTree.setSelectionPath(masterTree.getPathForRow(treeViewIndex4));
        EditorBeanSwing edCp = (EditorBeanSwing) masterTreeView.editBeans();
        ClosingPeriod cp = (ClosingPeriod) edCp.getBean();
        Assert.assertEquals("Schulputztag", edCp.getPropEditors().get(0).getInputFieldValue());
        EditorPropertyListSwing propedCpLocs = (EditorPropertyListSwing) edCp.getPropEditor("locations");
        ListModel modelListCpLocs = propedCpLocs.getWidgetList().getModel();
        Assert.assertEquals(1, modelListCpLocs.getSize());
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule",
                ((RapidBean) modelListCpLocs.getElementAt(0)).getIdString());
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule", ((RapidBean) propedCpLocs.getWidgetList().getModel()
                .getElementAt(0)).getIdString());
        EditorPropertyList2Swing propedCpLocs2 = (EditorPropertyList2Swing) propedCpLocs.openListEditor();
        ListModel modelListCpLocsLeft = propedCpLocs2.getWidgetListIn().getModel();
        Assert.assertEquals(1, modelListCpLocsLeft.getSize());
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule",
                ((RapidBean) modelListCpLocsLeft.getElementAt(0)).getIdString());
        ListModel modelListCpLocsRight = propedCpLocs2.getWidgetListOut().getModel();
        Assert.assertEquals(1, modelListCpLocsRight.getSize());
        Assert.assertEquals("Turnhalle Grundschule Süd",
                ((RapidBean) modelListCpLocsRight.getElementAt(0)).getIdString());

        // add location "Turnhalle Grundschule Süd to the closing period
        cp.addLocation(loc);

        // check all 4 editors
        Assert.assertEquals(4, listPropEdLocCps.getModel().getSize());
        Assert.assertEquals("20051222_Weihnachtsferien",
                ((RapidBean) listPropEdLocCps.getModel().getElementAt(0)).getIdString());
        Assert.assertEquals("20060116_Schulputztag",
                ((RapidBean) listPropEdLocCps.getModel().getElementAt(1)).getIdString());
        Assert.assertEquals(2, modelListCpLocs.getSize());
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule",
                ((RapidBean) modelListCpLocs.getElementAt(0)).getIdString());
        Assert.assertEquals("Turnhalle Grundschule Süd", ((RapidBean) modelListCpLocs.getElementAt(1)).getIdString());
        Assert.assertEquals(2, modelListCpLocsLeft.getSize());
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule",
                ((RapidBean) modelListCpLocsLeft.getElementAt(0)).getIdString());
        Assert.assertEquals("Turnhalle Grundschule Süd",
                ((RapidBean) modelListCpLocsLeft.getElementAt(1)).getIdString());
        Assert.assertEquals(0, modelListCpLocsRight.getSize());
    }

    /**
     * create a Club typing the letters succesively.
     */
    @Test
    public void testCreateClubTypingSuccessively() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing docView = this.getTestviewMasterdata();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) docView.getTreeView();
        JTree masterTree = (JTree) treeView.getTree();
        Assert.assertEquals("clubs", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(1).getLastPathComponent())
                .getColProp().getType().getPropName());

        masterTree.expandPath(masterTree.getPathForRow(1));
        Assert.assertEquals("Budo-Club Ismaning",
                ((RapidBean) masterTree.getPathForRow(2).getLastPathComponent()).getIdString());

        // select property node "clubs" in the tree view and
        // open a bean editor for creating new clubs
        masterTree.setSelectionPath(masterTree.getPathForRow(1));
        EditorBeanSwing ed = (EditorBeanSwing) treeView.createBean();

        // type Abc into the club name field successively
        EditorPropertyTextSwing propEditor = (EditorPropertyTextSwing) ed.getPropEditors().get(0);
        ((JTextField) propEditor.getWidget()).setText("A");
        propEditor.fireInputFieldChanged();
        Assert.assertEquals("Budo-Club Ismaning",
                ((RapidBean) masterTree.getPathForRow(2).getLastPathComponent()).getIdString());
        // type b into the club name field
        ((JTextField) propEditor.getWidget()).setText("Ab");
        propEditor.fireInputFieldChanged();
        Assert.assertEquals("Budo-Club Ismaning",
                ((RapidBean) masterTree.getPathForRow(2).getLastPathComponent()).getIdString());
        // type c into the club name field
        ((JTextField) propEditor.getWidget()).setText("Abc");
        propEditor.fireInputFieldChanged();
        Assert.assertEquals("Budo-Club Ismaning",
                ((RapidBean) masterTree.getPathForRow(2).getLastPathComponent()).getIdString());

        ed.handleActionOk();

        Club newBean = (Club) masterTree.getPathForRow(2).getLastPathComponent();
        Assert.assertEquals("Abc", newBean.getIdString());
        Assert.assertEquals("Abc", newBean.getName());
        Assert.assertEquals("Budo-Club Ismaning",
                ((RapidBean) masterTree.getPathForRow(3).getLastPathComponent()).getIdString());
    }

    /**
     * create a Trainer typing the letters succesively.
     */
    @Test
    public void testCreateTrainerTypingSuccessively() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing docView = this.getTestviewMasterdata();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) docView.getTreeView();
        JTree masterTree = (JTree) treeView.getTree();
        Assert.assertEquals("trainers", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(2)
                .getLastPathComponent()).getColProp().getType().getPropName());

        masterTree.expandPath(masterTree.getPathForRow(2));
        Assert.assertEquals("Blümel_Martin_",
                ((RapidBean) masterTree.getPathForRow(3).getLastPathComponent()).getIdString());

        // select property node "trainers" in the tree view and
        // open a bean editor for creating new Trainers
        masterTree.setSelectionPath(masterTree.getPathForRow(2));
        EditorBeanSwing ed = (EditorBeanSwing) treeView.createBean();

        // type Xyz into the club firstname field
        EditorPropertyTextSwing ped = (EditorPropertyTextSwing) ed.getPropEditors().get(1);
        ((JTextField) ped.getWidget()).setText("Xyz");
        ped.fireInputFieldChanged();

        // type Abc into the club lastname field successively
        ped = (EditorPropertyTextSwing) ed.getPropEditors().get(0);
        ((JTextField) ped.getWidget()).setText("A");
        ped.fireInputFieldChanged();
        // type b into the club name field
        ((JTextField) ped.getWidget()).setText("Ab");
        ped.fireInputFieldChanged();
        // type c into the club name field
        ((JTextField) ped.getWidget()).setText("Abc");
        ped.fireInputFieldChanged();

        ed.handleActionOk();

        Trainer newBean = (Trainer) masterTree.getPathForRow(3).getLastPathComponent();
        Assert.assertEquals("Abc_Xyz_", newBean.getIdString());
        Assert.assertEquals("Abc", newBean.getLastname());
        Assert.assertEquals("Blümel_Martin_",
                ((RapidBean) masterTree.getPathForRow(4).getLastPathComponent()).getIdString());
    }

    /**
     * Test editing a Trainer.
     */
    @Test
    public void testEditTrainer() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        Document master = client.getDocument("masterdata");
        Trainer martin = (Trainer) master.findBean("org.rapidbeans.clubadmin.domain.Trainer", "Blümel_Martin_");
        Assert.assertEquals("martin.bluemel@web.de", martin.getEmail());
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        Assert.assertEquals("trainers", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(2)
                .getLastPathComponent()).getColProp().getType().getPropName());

        // expand trainers
        masterTree.expandPath(masterTree.getPathForRow(2));

        // open Trainer "Blümel_Martin" for editing
        masterTree.setSelectionPath(masterTree.getPathForRow(3));
        EditorBeanSwing ed = (EditorBeanSwing) masterTreeView.editBeans();
        EditorPropertyTextSwing propedEmail = (EditorPropertyTextSwing) ed.getPropEditors().get(3);
        Assert.assertEquals("martin.bluemel@web.de", propedEmail.getInputFieldValue());
        Assert.assertEquals(false, ed.getButtonOk().isEnabled());
        Assert.assertEquals(false, ed.getButtonApply().isEnabled());
        Assert.assertEquals(true, ed.getButtonClose().isEnabled());

        // change Martin's email address
        ((JTextField) propedEmail.getWidget()).setText("martin.bluemel@gmx.de");

        ed.handleActionOk();
        Assert.assertEquals("martin.bluemel@gmx.de",
                ((Trainer) master.findBean("org.rapidbeans.clubadmin.domain.Trainer", "Blümel_Martin_")).getEmail());
    }

    /**
     * edit a trainer planning.
     */
    @Test
    public void testEditTrainerPlanning() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing docView = this.getTestviewTrainingsListExpert();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) docView.getTreeView();
        treeView.setShowBeanLinks(false);
        JTree tree = (JTree) treeView.getTree();
        int clubsrow = 1;
        Assert.assertEquals("clubs",
                ((DocumentTreeNodePropColComp) tree.getPathForRow(clubsrow).getLastPathComponent()).getColProp()
                        .getType().getPropName());

        // select property node "trainingdates" in the tree view and
        // open a bean editor for creating closing periods
        tree.expandPath(tree.getPathForRow(clubsrow));
        tree.expandPath(tree.getPathForRow(clubsrow + 1));
        tree.expandPath(tree.getPathForRow(clubsrow + 2));
        tree.expandPath(tree.getPathForRow(clubsrow + 3));
        tree.expandPath(tree.getPathForRow(clubsrow + 4));
        tree.expandPath(tree.getPathForRow(clubsrow + 5));
        tree.expandPath(tree.getPathForRow(clubsrow + 6));
        tree.setSelectionPath(tree.getPathForRow(clubsrow + 7));
        EditorBeanSwing editor = (EditorBeanSwing) treeView.editBeans();
        Assert.assertEquals(false, editor.getButtonOk().isEnabled());
        Assert.assertEquals(false, editor.getButtonApply().isEnabled());
        Assert.assertEquals(true, editor.getButtonClose().isEnabled());

        Document doc = docView.getDocument();
        TrainerPlanning planning = (TrainerPlanning) doc.findBean("org.rapidbeans.clubadmin.domain.TrainerPlanning",
                "17");
        TrainerPlanning planning1 = (TrainerPlanning) tree.getPathForRow(clubsrow + 7).getLastPathComponent();
        Assert.assertSame(planning, planning1);
        Assert.assertEquals(1, planning.getDefaulttrainers().size());

        EditorPropertyComboboxSwing pe0 = (EditorPropertyComboboxSwing) editor.getPropEditors().get(0);
        ((JComboBox) pe0.getWidget()).setSelectedIndex(0);
        EditorPropertyListSwing pe1 = (EditorPropertyListSwing) editor.getPropEditors().get(1);
        EditorPropertyList2Swing pe2 = pe1.openListEditor();
        JList listRight = pe2.getWidgetListOut();
        // try to add a trainer that is not available anymore
        Trainer berit = (Trainer) doc.findBean("org.rapidbeans.clubadmin.domain.Trainer", "Dahlheimer_Berit");
        listRight.setSelectedValue(berit, false);
        pe2.addSelectedBeans();
        // this should currently not throw an exception but also
        // not assoice the Location.
        Assert.assertEquals(1, planning.getDefaulttrainers().size());
    }

    /**
     * Tests deleting one single location ClosingPeriod. - open ClosingPeriod
     * "Weihnachtsferien 20051222" - remove location "Eurythmiesaal
     * Waldorfschule" - press O. K.
     */
    @Test
    public void testEditClosingPeriodLocationDeleteSimple() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        final int cprow = treeViewIndexClosigperiods;
        Assert.assertEquals("closingperiods", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(cprow)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Document masterdoc = masterDocView.getDocument();
        Location loc = (Location) masterdoc.findBean("org.rapidbeans.clubadmin.domain.Location",
                "Eurythmiesaal 1 Waldorfschule");
        ClosingPeriod cp = (ClosingPeriod) masterdoc.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod",
                "20051222_Weihnachtsferien");
        Assert.assertEquals(5, loc.getClosedons().size());
        Assert.assertEquals(2, cp.getLocations().size());

        // open closing period "20051222_Weihnachtsferien" for editing
        masterTree.expandPath(masterTree.getPathForRow(cprow));
        masterTree.setSelectionPath(masterTree.getPathForRow(cprow + 1));
        masterTreeView.editBeans();
        EditorBeanSwing ed = (EditorBeanSwing) masterDocView.getEditor((RapidBean) masterTree.getPathForRow(cprow + 1)
                .getLastPathComponent(), false);
        Assert.assertSame(cp, ed.getBean());
        EditorPropertyListSwing ped = (EditorPropertyListSwing) ed.getPropEditor("locations");
        Assert.assertEquals(2, ped.getWidgetList().getModel().getSize());
        HashMap<String, Object> buttons = ed.getButtonWidgets();
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Übernehmen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Schließen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        EditorPropertyList2Swing led = ped.openListEditor();
        Assert.assertEquals(2, led.getWidgetListIn().getModel().getSize());
        led.getWidgetListIn().setSelectedValue(loc, false);
        led.removeSelectedBeans();

        Assert.assertEquals(1, led.getWidgetListIn().getModel().getSize());
        Assert.assertEquals(1, ped.getWidgetList().getModel().getSize());
        Assert.assertEquals(1, cp.getLocations().size());
        Assert.assertEquals(4, loc.getClosedons().size());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Übernehmen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Abbrechen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        ed.handleActionOk();

        Assert.assertEquals(1, cp.getLocations().size());
        Assert.assertEquals(4, loc.getClosedons().size());
    }

    /**
     * Tests deleting one single location ClosingPeriod. - open ClosingPeriod
     * "Weihnachtsferien 20051222" - remove location
     * "Eurythmiesaal Waldorfschule" - press Apply.
     */
    @Test
    public void testEditClosingPeriodLocationDeleteApply() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        final int cprow = treeViewIndexClosigperiods;
        Assert.assertEquals("closingperiods", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(cprow)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Document masterdoc = masterDocView.getDocument();
        final Location locEsaal = (Location) masterdoc.findBean("org.rapidbeans.clubadmin.domain.Location",
                "Eurythmiesaal 1 Waldorfschule");
        ClosingPeriod cp = (ClosingPeriod) masterdoc.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod",
                "20051222_Weihnachtsferien");
        Assert.assertEquals(5, locEsaal.getClosedons().size());
        Assert.assertEquals(2, cp.getLocations().size());

        // open closing period "20051222_Weihnachtsferien" for editing
        masterTree.expandPath(masterTree.getPathForRow(cprow));
        masterTree.setSelectionPath(masterTree.getPathForRow(cprow + 1));
        EditorBeanSwing ed = (EditorBeanSwing) masterTreeView.editBeans();
        Assert.assertSame(cp, ed.getBean());
        EditorPropertyListSwing ped = (EditorPropertyListSwing) ed.getPropEditor("locations");
        Assert.assertEquals(2, ped.getWidgetList().getModel().getSize());
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule", ((RapidBean) ped.getWidgetList().getModel()
                .getElementAt(0)).getIdString());
        Assert.assertEquals("Turnhalle Grundschule Süd",
                ((RapidBean) ped.getWidgetList().getModel().getElementAt(1)).getIdString());
        HashMap<String, Object> buttons = ed.getButtonWidgets();
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Übernehmen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Schließen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        EditorPropertyList2Swing led = ped.openListEditor();
        Assert.assertEquals(2, led.getWidgetListIn().getModel().getSize());
        led.getWidgetListIn().setSelectedValue(locEsaal, false);
        led.removeSelectedBeans();

        Assert.assertEquals(1, led.getWidgetListIn().getModel().getSize());
        Assert.assertEquals(1, ped.getWidgetList().getModel().getSize());
        Assert.assertEquals(1, cp.getLocations().size());
        Assert.assertEquals(4, locEsaal.getClosedons().size());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Übernehmen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Abbrechen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        ed.handleActionApply();

        Assert.assertEquals(1, cp.getLocations().size());
        Assert.assertEquals(4, locEsaal.getClosedons().size());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Übernehmen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Schließen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        ed.handleActionOk();

        Assert.assertEquals(1, cp.getLocations().size());
        Assert.assertEquals(4, locEsaal.getClosedons().size());
    }

    /**
     * Tests deleting one single location ClosingPeriod. - open ClosingPeriod
     * "Weihnachtsferien 20051222" - remove location "Eurythmiesaal
     * Waldorfschule" - press Cancel.
     */
    @Test
    public void testEditClosingPeriodLocationDeleteCancel() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        final int cprow = treeViewIndexClosigperiods;
        Assert.assertEquals("closingperiods", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(cprow)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Document masterdoc = masterDocView.getDocument();
        final Location loc = (Location) masterdoc.findBean("org.rapidbeans.clubadmin.domain.Location",
                "Eurythmiesaal 1 Waldorfschule");
        final ClosingPeriod cp = (ClosingPeriod) masterdoc.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod",
                "20051222_Weihnachtsferien");
        Assert.assertEquals(5, loc.getClosedons().size());
        Assert.assertEquals(2, cp.getLocations().size());

        // open closing period "20051222_Weihnachtsferien" for editing
        masterTree.expandPath(masterTree.getPathForRow(cprow));
        masterTree.setSelectionPath(masterTree.getPathForRow(cprow + 1));
        masterTreeView.editBeans();
        final EditorBeanSwing edCp = (EditorBeanSwing) masterDocView.getEditor(
                (RapidBean) masterTree.getPathForRow(cprow + 1).getLastPathComponent(), false);
        Assert.assertSame(cp, edCp.getBean());
        EditorPropertyListSwing ped = (EditorPropertyListSwing) edCp.getPropEditor("locations");
        Assert.assertEquals(2, ped.getWidgetList().getModel().getSize());
        retry(new RetryableAction() {
            public void doSomething() {
                final HashMap<String, Object> buttons = edCp.getButtonWidgets();
                Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
                Assert.assertEquals(false, ((JButton) buttons.get("ok")).isEnabled());
                Assert.assertEquals("Übernehmen", ((JButton) buttons.get("apply")).getText());
                Assert.assertEquals(false, ((JButton) buttons.get("apply")).isEnabled());
                Assert.assertEquals("Schließen", ((JButton) buttons.get("close")).getText());
                Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());
            }
        });

        EditorPropertyList2Swing led = ped.openListEditor();
        Assert.assertEquals(2, led.getWidgetListIn().getModel().getSize());
        led.getWidgetListIn().setSelectedValue(loc, false);
        led.removeSelectedBeans();

        Assert.assertEquals(1, led.getWidgetListIn().getModel().getSize());
        Assert.assertEquals(1, ped.getWidgetList().getModel().getSize());
        Assert.assertEquals(1, cp.getLocations().size());
        Assert.assertEquals(4, loc.getClosedons().size());

        edCp.handleActionClose();

        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertEquals(2, cp.getLocations().size());
                Assert.assertEquals(5, loc.getClosedons().size());
            }
        });
    }

    /**
     * Tests changing more than one location in a ClosingPeriod.
     */
    @Test
    public void testEditClosingPeriodLocationMultipleLinks() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        final int cprow = treeViewIndexClosigperiods;
        Assert.assertEquals("closingperiods", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(cprow)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Document masterdoc = masterDocView.getDocument();
        final Location loc1 = (Location) masterdoc.findBean("org.rapidbeans.clubadmin.domain.Location",
                "Eurythmiesaal 1 Waldorfschule");
        final Location loc2 = (Location) masterdoc.findBean("org.rapidbeans.clubadmin.domain.Location",
                "Turnhalle Grundschule Süd");
        final ClosingPeriod cp = (ClosingPeriod) masterdoc.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod",
                "20051222_Weihnachtsferien");
        Assert.assertEquals(5, loc1.getClosedons().size());
        Assert.assertEquals(3, loc2.getClosedons().size());
        Assert.assertEquals(2, cp.getLocations().size());

        // open closing period "20051222_Weihnachtsferien" for editing
        masterTree.expandPath(masterTree.getPathForRow(cprow));
        masterTree.setSelectionPath(masterTree.getPathForRow(cprow + 1));
        masterTreeView.editBeans();
        final EditorBeanSwing ed = (EditorBeanSwing) masterDocView.getEditor(
                (RapidBean) masterTree.getPathForRow(cprow + 1).getLastPathComponent(), false);
        Assert.assertSame(cp, ed.getBean());
        final EditorPropertyListSwing ped = (EditorPropertyListSwing) ed.getPropEditor("locations");
        Assert.assertEquals(2, ped.getWidgetList().getModel().getSize());

        final EditorPropertyList2Swing led = ped.openListEditor();
        Assert.assertEquals(2, led.getWidgetListIn().getModel().getSize());
        Assert.assertEquals(0, led.getWidgetListOut().getModel().getSize());

        led.getWidgetListIn().setSelectedValue(loc1, false);
        led.removeSelectedBeans();
        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertEquals(1, led.getWidgetListIn().getModel().getSize());
                Assert.assertEquals(1, led.getWidgetListOut().getModel().getSize());
                Assert.assertEquals(1, ped.getWidgetList().getModel().getSize());
                Assert.assertEquals(1, cp.getLocations().size());
                Assert.assertEquals(4, loc1.getClosedons().size());
                Assert.assertEquals(3, loc2.getClosedons().size());
            }
        });

        led.getWidgetListIn().setSelectedValue(loc2, false);
        led.removeSelectedBeans();
        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertEquals(0, led.getWidgetListIn().getModel().getSize());
                Assert.assertEquals(2, led.getWidgetListOut().getModel().getSize());
                Assert.assertEquals(0, ped.getWidgetList().getModel().getSize());
                Assert.assertEquals(0, cp.getLocations().size());
                Assert.assertEquals(4, loc1.getClosedons().size());
                Assert.assertEquals(2, loc2.getClosedons().size());
            }
        });

        ed.handleActionOk();
    }

    /**
     * Add and remove some links between a location and a closing period and
     * check if the tree view is updated correctly.
     */
    @Test
    public void testEditLocationClosingPeriodsAddRemoveWithTreeView() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        Document masterdoc = masterDocView.getDocument();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        final JTree masterTree = (JTree) masterTreeView.getTree();

        // check the document
        final Location locEsaal1 = (Location) masterdoc.findBean(Location.class.getName(),
                "Eurythmiesaal 1 Waldorfschule");
        final Location locTurnhalle = (Location) masterdoc.findBean(Location.class.getName(),
                "Turnhalle Grundschule Süd");
        final ClosingPeriod cpXmas05 = (ClosingPeriod) masterdoc.findBean(ClosingPeriod.class.getName(),
                "20051222_Weihnachtsferien");
        cpXmas05.removeLocation(locTurnhalle);

        Assert.assertEquals(5, locEsaal1.getClosedons().size());
        Assert.assertEquals("20051222_Weihnachtsferien",
                ((ReadonlyListCollection<ClosingPeriod>) locEsaal1.getClosedons()).get(0).getIdString());
        Assert.assertEquals("20060116_Schulputztag", ((ReadonlyListCollection<ClosingPeriod>) locEsaal1.getClosedons())
                .get(1).getIdString());
        Assert.assertEquals("20061223_Weihnachtsferien",
                ((ReadonlyListCollection<ClosingPeriod>) locEsaal1.getClosedons()).get(2).getIdString());
        Assert.assertEquals("20070301_20-Jahre-Feier",
                ((ReadonlyListCollection<ClosingPeriod>) locEsaal1.getClosedons()).get(3).getIdString());
        Assert.assertEquals("20070330_Osterferien", ((ReadonlyListCollection<ClosingPeriod>) locEsaal1.getClosedons())
                .get(4).getIdString());

        Assert.assertEquals(2, locTurnhalle.getClosedons().size());
        Assert.assertEquals("20061223_Weihnachtsferien",
                ((ReadonlyListCollection<ClosingPeriod>) locTurnhalle.getClosedons()).get(0).getIdString());
        Assert.assertEquals("20070330_Osterferien",
                ((ReadonlyListCollection<ClosingPeriod>) locTurnhalle.getClosedons()).get(1).getIdString());

        Assert.assertEquals(1, cpXmas05.getLocations().size());
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule",
                ((ReadonlyListCollection<Location>) cpXmas05.getLocations()).get(0).getIdString());

        // expand all three entities in the tree view and check the rows
        Assert.assertEquals("locations", ((DocumentTreeNodePropColComp) masterTree
                .getPathForRow(treeViewIndexLocations).getLastPathComponent()).getColProp().getType().getPropName());
        Assert.assertEquals("closingperiods",
                ((DocumentTreeNodePropColComp) masterTree.getPathForRow(treeViewIndexClosigperiods)
                        .getLastPathComponent()).getColProp().getType().getPropName());
        masterTree.expandPath(masterTree.getPathForRow(treeViewIndexClosigperiods));
        masterTree.expandPath(masterTree.getPathForRow(treeViewIndexClosigperiods + 1));
        masterTree.expandPath(masterTree.getPathForRow(treeViewIndexClosigperiods + 2));
        masterTree.expandPath(masterTree.getPathForRow(treeViewIndexLocations));
        masterTree.expandPath(masterTree.getPathForRow(treeViewIndexLocations + 2));
        masterTree.expandPath(masterTree.getPathForRow(treeViewIndexLocations + 3));
        masterTree.expandPath(masterTree.getPathForRow(treeViewIndexLocations + 1));
        masterTree.expandPath(masterTree.getPathForRow(treeViewIndexLocations + 2));

        // compute new rows for all three entities after expanding
        // and check the tree view
        final int treeViewIndexLocationEsaal1 = treeViewIndexLocations + 1;
        Assert.assertSame(locEsaal1, masterTree.getPathForRow(treeViewIndexLocationEsaal1).getLastPathComponent());
        Assert.assertSame(locEsaal1.getProperty("closedons"),
                ((DocumentTreeNodePropCol) masterTree.getPathForRow(treeViewIndexLocationEsaal1 + 1)
                        .getLastPathComponent()).getColProp());
        Assert.assertEquals("20051222_Weihnachtsferien",
                ((DocumentTreeNodeBeanLink) masterTree.getPathForRow(treeViewIndexLocationEsaal1 + 2)
                        .getLastPathComponent()).getLinkedBean().getIdString());
        Assert.assertEquals("20060116_Schulputztag",
                ((DocumentTreeNodeBeanLink) masterTree.getPathForRow(treeViewIndexLocationEsaal1 + 3)
                        .getLastPathComponent()).getLinkedBean().getIdString());
        Assert.assertEquals("20061223_Weihnachtsferien",
                ((DocumentTreeNodeBeanLink) masterTree.getPathForRow(treeViewIndexLocationEsaal1 + 4)
                        .getLastPathComponent()).getLinkedBean().getIdString());
        Assert.assertEquals("20070301_20-Jahre-Feier",
                ((DocumentTreeNodeBeanLink) masterTree.getPathForRow(treeViewIndexLocationEsaal1 + 5)
                        .getLastPathComponent()).getLinkedBean().getIdString());
        Assert.assertEquals("20070330_Osterferien",
                ((DocumentTreeNodeBeanLink) masterTree.getPathForRow(treeViewIndexLocationEsaal1 + 6)
                        .getLastPathComponent()).getLinkedBean().getIdString());

        int treeViewIndexLocationTurnhalle1 = treeViewIndexLocationEsaal1 + 7;
        Assert.assertSame(locTurnhalle, masterTree.getPathForRow(treeViewIndexLocationTurnhalle1)
                .getLastPathComponent());
        Assert.assertSame(locTurnhalle.getProperty("closedons"),
                ((DocumentTreeNodePropCol) masterTree.getPathForRow(treeViewIndexLocationTurnhalle1 + 1)
                        .getLastPathComponent()).getColProp());
        Assert.assertEquals("20061223_Weihnachtsferien",
                ((DocumentTreeNodeBeanLink) masterTree.getPathForRow(treeViewIndexLocationTurnhalle1 + 2)
                        .getLastPathComponent()).getLinkedBean().getIdString());
        Assert.assertEquals("20070330_Osterferien",
                ((DocumentTreeNodeBeanLink) masterTree.getPathForRow(treeViewIndexLocationTurnhalle1 + 3)
                        .getLastPathComponent()).getLinkedBean().getIdString());

        final int treeViewIndexClosingperiodXmas05 = treeViewIndexLocations + 13;
        Assert.assertSame(cpXmas05, masterTree.getPathForRow(treeViewIndexClosingperiodXmas05).getLastPathComponent());
        Assert.assertSame(cpXmas05.getProperty("locations"),
                ((DocumentTreeNodePropCol) masterTree.getPathForRow(treeViewIndexClosingperiodXmas05 + 1)
                        .getLastPathComponent()).getColProp());
        Assert.assertSame(locEsaal1,
                ((DocumentTreeNodeBeanLink) masterTree.getPathForRow(treeViewIndexClosingperiodXmas05 + 2)
                        .getLastPathComponent()).getLinkedBean());

        // open closing period "20051222_Weihnachtsferien" for editing
        masterTree.setSelectionPath(masterTree.getPathForRow(treeViewIndexClosingperiodXmas05));
        EditorBeanSwing ed = (EditorBeanSwing) masterTreeView.editBeans();
        Assert.assertSame(cpXmas05, ed.getBean());
        final EditorPropertyListSwing ped = (EditorPropertyListSwing) ed.getPropEditor("locations");
        Assert.assertEquals(1, ped.getWidgetList().getModel().getSize());
        final EditorPropertyList2Swing ledCpXmas05Locations = ped.openListEditor();
        Assert.assertEquals(1, ledCpXmas05Locations.getWidgetListIn().getModel().getSize());
        Assert.assertEquals(1, ledCpXmas05Locations.getWidgetListOut().getModel().getSize());

        // break link between cpXmas05 and locEsaal1
        ledCpXmas05Locations.getWidgetListIn().setSelectedValue(locEsaal1, false);
        ledCpXmas05Locations.removeSelectedBeans();

        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertEquals(0, ledCpXmas05Locations.getWidgetListIn().getModel().getSize());
                Assert.assertEquals(2, ledCpXmas05Locations.getWidgetListOut().getModel().getSize());
                Assert.assertEquals(0, ped.getWidgetList().getModel().getSize());
            }
        });

        // cpXmas looses one link
        Assert.assertEquals(0, cpXmas05.getLocations().size());
        // locEsaal looses one link
        Assert.assertEquals(4, locEsaal1.getClosedons().size());
        // locTurnhalle stays untouched
        Assert.assertEquals(2, locTurnhalle.getClosedons().size());

        // add link between cpXmas05 and locLocTurnhalle
        ledCpXmas05Locations.getWidgetListOut().setSelectedValue(locTurnhalle, false);
        ledCpXmas05Locations.addSelectedBeans();

        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertEquals(1, ledCpXmas05Locations.getWidgetListIn().getModel().getSize());
                Assert.assertEquals(1, ledCpXmas05Locations.getWidgetListOut().getModel().getSize());
                Assert.assertEquals(1, ped.getWidgetList().getModel().getSize());

                // cpXmas05 wins one link
                Assert.assertEquals(1, cpXmas05.getLocations().size());
                // locEsaal1 stays untouched
                Assert.assertEquals(4, locEsaal1.getClosedons().size());
                // locTurnhalle should immediately win one link!!!
                Assert.assertEquals(3, locTurnhalle.getClosedons().size());
            }
        });

        ed.handleActionOk();

        retry(new RetryableAction() {
            public void doSomething() {
                Assert.assertEquals(4, locEsaal1.getClosedons().size());
                Assert.assertEquals("20060116_Schulputztag",
                        ((ReadonlyListCollection<ClosingPeriod>) locEsaal1.getClosedons()).get(0).getIdString());
                Assert.assertEquals("20061223_Weihnachtsferien",
                        ((ReadonlyListCollection<ClosingPeriod>) locEsaal1.getClosedons()).get(1).getIdString());
                Assert.assertEquals("20070301_20-Jahre-Feier",
                        ((ReadonlyListCollection<ClosingPeriod>) locEsaal1.getClosedons()).get(2).getIdString());
                Assert.assertEquals("20070330_Osterferien",
                        ((ReadonlyListCollection<ClosingPeriod>) locEsaal1.getClosedons()).get(3).getIdString());

                Assert.assertEquals(3, locTurnhalle.getClosedons().size());
                Assert.assertEquals("20051222_Weihnachtsferien",
                        ((ReadonlyListCollection<ClosingPeriod>) locTurnhalle.getClosedons()).get(0).getIdString());
                Assert.assertEquals("20061223_Weihnachtsferien",
                        ((ReadonlyListCollection<ClosingPeriod>) locTurnhalle.getClosedons()).get(1).getIdString());
                Assert.assertEquals("20070330_Osterferien",
                        ((ReadonlyListCollection<ClosingPeriod>) locTurnhalle.getClosedons()).get(2).getIdString());

                Assert.assertEquals(1, cpXmas05.getLocations().size());
                Assert.assertEquals("Turnhalle Grundschule Süd",
                        ((ReadonlyListCollection<Location>) cpXmas05.getLocations()).get(0).getIdString());
            }
        });

        // compute new rows for all three entities after expanding
        // and check the tree view

        retry(new Class[] { AssertionFailedError.class, ClassCastException.class, NullPointerException.class },
                new RetryableAction() {
                    public void doSomething() {
                        Assert.assertSame(locEsaal1.getProperty("closedons"), ((DocumentTreeNodePropCol) masterTree
                                .getPathForRow(treeViewIndexLocationEsaal1 + 1).getLastPathComponent()).getColProp());
                        Assert.assertEquals("20060116_Schulputztag", ((DocumentTreeNodeBeanLink) masterTree
                                .getPathForRow(treeViewIndexLocationEsaal1 + 2).getLastPathComponent()).getLinkedBean()
                                .getIdString());
                        Assert.assertEquals("20061223_Weihnachtsferien", ((DocumentTreeNodeBeanLink) masterTree
                                .getPathForRow(treeViewIndexLocationEsaal1 + 3).getLastPathComponent()).getLinkedBean()
                                .getIdString());
                        Assert.assertEquals("20070301_20-Jahre-Feier", ((DocumentTreeNodeBeanLink) masterTree
                                .getPathForRow(treeViewIndexLocationEsaal1 + 4).getLastPathComponent()).getLinkedBean()
                                .getIdString());
                        Assert.assertEquals("20070330_Osterferien", ((DocumentTreeNodeBeanLink) masterTree
                                .getPathForRow(treeViewIndexLocationEsaal1 + 5).getLastPathComponent()).getLinkedBean()
                                .getIdString());
                    }
                });

        final int treeViewIndexLocationTurnhalle2 = treeViewIndexLocationTurnhalle1;
        retry(new Class[] { AssertionFailedError.class, ClassCastException.class, NullPointerException.class },
                new RetryableAction() {
                    public void doSomething() {
                        int treeViewIndexLocationTurnhalle3 = treeViewIndexLocationTurnhalle2 - 1;
                        Assert.assertSame(locTurnhalle, masterTree.getPathForRow(treeViewIndexLocationTurnhalle3)
                                .getLastPathComponent());
                        Assert.assertSame(locTurnhalle.getProperty("closedons"), ((DocumentTreeNodePropCol) masterTree
                                .getPathForRow(treeViewIndexLocationTurnhalle3 + 1).getLastPathComponent())
                                .getColProp());
                        Assert.assertEquals("20051222_Weihnachtsferien", ((DocumentTreeNodeBeanLink) masterTree
                                .getPathForRow(treeViewIndexLocationTurnhalle3 + 2).getLastPathComponent())
                                .getLinkedBean().getIdString());
                        Assert.assertEquals("20061223_Weihnachtsferien", ((DocumentTreeNodeBeanLink) masterTree
                                .getPathForRow(treeViewIndexLocationTurnhalle3 + 3).getLastPathComponent())
                                .getLinkedBean().getIdString());
                        Assert.assertEquals("20070330_Osterferien", ((DocumentTreeNodeBeanLink) masterTree
                                .getPathForRow(treeViewIndexLocationTurnhalle3 + 4).getLastPathComponent())
                                .getLinkedBean().getIdString());

                        Assert.assertSame(cpXmas05, masterTree.getPathForRow(treeViewIndexClosingperiodXmas05)
                                .getLastPathComponent());
                        Assert.assertSame(
                                cpXmas05.getProperty("locations"),
                                ((DocumentTreeNodePropCol) masterTree.getPathForRow(
                                        treeViewIndexClosingperiodXmas05 + 1).getLastPathComponent()).getColProp());
                        Assert.assertSame(
                                locTurnhalle,
                                ((DocumentTreeNodeBeanLink) masterTree.getPathForRow(
                                        treeViewIndexClosingperiodXmas05 + 2).getLastPathComponent()).getLinkedBean());
                    }
                });
    }

    /**
     * Tests changing a oneday closing period to two days.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testEditClosingPeriodOnedayToMoreDays() throws InterruptedException {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        final int cprow = treeViewIndexClosigperiods;
        Assert.assertEquals("closingperiods", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(cprow)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Document masterdoc = masterDocView.getDocument();
        ClosingPeriod cpCleanday = (ClosingPeriod) masterdoc.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod",
                "20060116_Schulputztag");
        Assert.assertEquals(cpCleanday.getFrom(), cpCleanday.getTo());

        // open closing period "20060116_Schulputztag" for editing
        masterTree.expandPath(masterTree.getPathForRow(cprow));
        masterTree.setSelectionPath(masterTree.getPathForRow(cprow + 2));
        masterTreeView.editBeans();
        EditorBeanSwing ed = (EditorBeanSwing) masterDocView.getEditor((RapidBean) masterTree.getPathForRow(cprow + 2)
                .getLastPathComponent(), false);
        Assert.assertSame(cpCleanday, ed.getBean());
        EditorPropertyDateSwing pedTo = (EditorPropertyDateSwing) ed.getPropEditor("to");
        EditorPropertyCheckboxSwing pedOneday = (EditorPropertyCheckboxSwing) ed.getPropEditor("oneday");
        Assert.assertFalse(((JTextField) pedTo.getWidget()).isEnabled());
        Assert.assertTrue(((JCheckBox) pedOneday.getWidget()).isSelected());

        ((JCheckBox) pedOneday.getWidget()).setSelected(false);
        Assert.assertTrue(((JTextField) pedTo.getWidget()).isEnabled());
        Assert.assertEquals("", ((JTextField) pedTo.getWidget()).getText());

        ((JTextField) pedTo.getWidget()).setText("17.01.2006");
        pedTo.fireInputFieldChanged();
        Assert.assertFalse(((JCheckBox) pedOneday.getWidget()).isSelected());
        Assert.assertFalse(cpCleanday.getFrom().equals(cpCleanday.getTo()));
    }

    /**
     * Tests changing a oneday closing period to two days.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testEditClosingPeriodInvalidDateToEmptyCancel() throws InterruptedException {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        final int cprow = treeViewIndexClosigperiods;
        Assert.assertEquals("closingperiods", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(cprow)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Document masterdoc = masterDocView.getDocument();
        ClosingPeriod cpCleanday = (ClosingPeriod) masterdoc.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod",
                "20060116_Schulputztag");
        Date toBefore = cpCleanday.getTo();
        Assert.assertEquals(cpCleanday.getFrom(), cpCleanday.getTo());

        // open closing period "20060116_Schulputztag" for editing
        masterTree.expandPath(masterTree.getPathForRow(cprow));
        masterTree.setSelectionPath(masterTree.getPathForRow(cprow + 2));
        EditorBeanSwing ed = (EditorBeanSwing) masterTreeView.editBeans();
        Assert.assertSame(cpCleanday, ed.getBean());
        Assert.assertNotNull(ed.getBakbean().getProperty("to").getValue());
        EditorPropertyDateSwing pedTo = (EditorPropertyDateSwing) ed.getPropEditor("to");
        EditorPropertyCheckboxSwing pedOneday = (EditorPropertyCheckboxSwing) ed.getPropEditor("oneday");
        Assert.assertFalse(((JTextField) pedTo.getWidget()).isEnabled());
        Assert.assertTrue(((JCheckBox) pedOneday.getWidget()).isSelected());

        Assert.assertNotNull(ed.getBakbean().getProperty("to").getValue());
        ((JCheckBox) pedOneday.getWidget()).setSelected(false);
        Assert.assertNotNull(ed.getBakbean().getProperty("to").getValue());
        Assert.assertTrue(((JTextField) pedTo.getWidget()).isEnabled());
        Assert.assertEquals("", ((JTextField) pedTo.getWidget()).getText());

        HashMap<String, Object> buttons = ed.getButtonWidgets();
        Assert.assertEquals("Überprüfen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Abbrechen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        Assert.assertFalse(toBefore.equals(cpCleanday.getTo()));
        ed.handleActionClose();
        Assert.assertTrue(toBefore.equals(cpCleanday.getTo()));
    }

    /**
     * Tests changing a oneday closing period to two days.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testEditClosingPeriodToInvalidDateToLetter() throws InterruptedException {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        final int cprow = treeViewIndexClosigperiods;
        Assert.assertEquals("closingperiods", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(cprow)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Document masterdoc = masterDocView.getDocument();
        ClosingPeriod cpXmas = (ClosingPeriod) masterdoc.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod",
                "20051222_Weihnachtsferien");
        Date toBefore = cpXmas.getTo();

        // open closing period "20060116_Schulputztag" for editing
        masterTree.expandPath(masterTree.getPathForRow(cprow));
        masterTree.setSelectionPath(masterTree.getPathForRow(cprow + 1));
        masterTreeView.editBeans();
        EditorBeanSwing ed = (EditorBeanSwing) masterDocView.getEditor((RapidBean) masterTree.getPathForRow(cprow + 1)
                .getLastPathComponent(), false);
        Assert.assertSame(cpXmas, ed.getBean());
        EditorPropertyDateSwing pedTo = (EditorPropertyDateSwing) ed.getPropEditor("to");
        EditorPropertyCheckboxSwing pedOneday = (EditorPropertyCheckboxSwing) ed.getPropEditor("oneday");
        Assert.assertTrue(((JTextField) pedTo.getWidget()).isEnabled());
        Assert.assertFalse(((JCheckBox) pedOneday.getWidget()).isSelected());

        ((JTextField) pedTo.getWidget()).setText("XXX");
        pedTo.fireInputFieldChanged();
        Assert.assertFalse(((JCheckBox) pedOneday.getWidget()).isSelected());
        Assert.assertEquals(EditorPropertySwing.COLOR_INVALID, ((JTextField) pedTo.getWidget()).getBackground());

        HashMap<String, Object> buttons = ed.getButtonWidgets();
        Assert.assertEquals("Überprüfen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Abbrechen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        ed.handleActionClose();
        Assert.assertTrue(toBefore.equals(cpXmas.getTo()));
    }

    /**
     * Tests changing a oneday closing period to two days.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testEditClosingPeriodToInvalidDateToSmallerFrom() throws InterruptedException {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        final int cprow = treeViewIndexClosigperiods;
        Assert.assertEquals("closingperiods", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(cprow)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Document masterdoc = masterDocView.getDocument();
        ClosingPeriod cpXmas = (ClosingPeriod) masterdoc.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod",
                "20051222_Weihnachtsferien");

        // open closing period "20051222_Weihnachtsferien" for editing
        masterTree.expandPath(masterTree.getPathForRow(cprow));
        masterTree.setSelectionPath(masterTree.getPathForRow(cprow + 1));
        masterTreeView.editBeans();
        EditorBeanSwing ed = (EditorBeanSwing) masterDocView.getEditor((RapidBean) masterTree.getPathForRow(cprow + 1)
                .getLastPathComponent(), false);
        Assert.assertSame(cpXmas, ed.getBean());
        EditorPropertyDateSwing pedTo = (EditorPropertyDateSwing) ed.getPropEditor("to");
        EditorPropertyCheckboxSwing pedOneday = (EditorPropertyCheckboxSwing) ed.getPropEditor("oneday");
        Assert.assertTrue(((JTextField) pedTo.getWidget()).isEnabled());
        Assert.assertFalse(((JCheckBox) pedOneday.getWidget()).isSelected());

        ((JTextField) pedTo.getWidget()).setText("21.12.2005");
        pedTo.fireInputFieldChanged();
        Assert.assertFalse(((JCheckBox) pedOneday.getWidget()).isSelected());
        Assert.assertEquals(EditorPropertySwing.COLOR_INVALID, ((JTextField) pedTo.getWidget()).getBackground());

        ((JTextField) pedTo.getWidget()).setText("21.12.200");
        pedTo.fireInputFieldChanged();
        Assert.assertFalse(((JCheckBox) pedOneday.getWidget()).isSelected());
        Assert.assertEquals(EditorPropertySwing.COLOR_INVALID, ((JTextField) pedTo.getWidget()).getBackground());
    }

    /**
     * Tests changing more than one location in a ClosingPeriod.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testEditClosingPeriodOutsideChangeNotModifiedClosed() throws InterruptedException {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        final int cprow = treeViewIndexClosigperiods;
        Assert.assertEquals("closingperiods", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(cprow)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Document masterdoc = masterDocView.getDocument();
        final Location locEurythm = (Location) masterdoc.findBean("org.rapidbeans.clubadmin.domain.Location",
                "Eurythmiesaal 1 Waldorfschule");
        Location locSportshall = (Location) masterdoc.findBean("org.rapidbeans.clubadmin.domain.Location",
                "Turnhalle Grundschule Süd");
        ClosingPeriod cpXmas = (ClosingPeriod) masterdoc.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod",
                "20051222_Weihnachtsferien");
        ClosingPeriod cpCleanday = (ClosingPeriod) masterdoc.findBean("org.rapidbeans.clubadmin.domain.ClosingPeriod",
                "20060116_Schulputztag");

        // open closing period "20051222_Weihnachtsferien" for editing
        masterTree.expandPath(masterTree.getPathForRow(cprow));
        masterTree.setSelectionPath(masterTree.getPathForRow(cprow + 1));
        masterTreeView.editBeans();
        EditorBeanSwing ed = (EditorBeanSwing) masterDocView.getEditor((RapidBean) masterTree.getPathForRow(cprow + 1)
                .getLastPathComponent(), false);
        Assert.assertSame(cpXmas, ed.getBean());
        EditorPropertyListSwing ped = (EditorPropertyListSwing) ed.getPropEditor("locations");

        Assert.assertEquals(2, ped.getWidgetList().getModel().getSize());
        Assert.assertEquals(2, cpXmas.getLocations().size());
        Assert.assertEquals(1, cpCleanday.getLocations().size());
        Assert.assertEquals(5, locEurythm.getClosedons().size());
        Assert.assertEquals(3, locSportshall.getClosedons().size());

        final ArrayList<ClosingPeriod> newValue = new ArrayList<ClosingPeriod>();
        newValue.add(cpCleanday);
        retry(ArrayIndexOutOfBoundsException.class, new RetryableAction() {
            public void doSomething() {
                locEurythm.setClosedons(newValue);
            }
        });

        Assert.assertEquals(1, ped.getWidgetList().getModel().getSize());
        Assert.assertEquals(1, cpXmas.getLocations().size());
        Assert.assertEquals(1, cpCleanday.getLocations().size());
        Assert.assertEquals(1, locEurythm.getClosedons().size());
        Assert.assertEquals(3, locSportshall.getClosedons().size());

        ed.handleActionClose();

        Assert.assertEquals(1, ped.getWidgetList().getModel().getSize());
        Assert.assertEquals(1, cpXmas.getLocations().size());
        Assert.assertEquals(1, cpCleanday.getLocations().size());
        Assert.assertEquals(1, locEurythm.getClosedons().size());
        Assert.assertEquals(3, locSportshall.getClosedons().size());
    }

    /**
     * Tests deleting a location while editing a ClosingPeriod. - open
     * ClosingPeriod "Weihnachtsferien 20051222" - remove 1st Location
     * "Eurythmiesaal 1 Waldorfschule" - press OK
     */
    @Test
    public void testEditClosingPeriodLocationBeanRemoved() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        JTree masterTree = (JTree) masterTreeView.getTree();
        final int cprow = treeViewIndexClosigperiods;
        Assert.assertEquals("closingperiods", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(cprow)
                .getLastPathComponent()).getColProp().getType().getPropName());

        // expand closing periods
        masterTree.expandPath(masterTree.getPathForRow(cprow));

        // open closing period "20051222_Weihnachtsferien" for editing
        masterTree.setSelectionPath(masterTree.getPathForRow(cprow + 1));
        masterTreeView.editBeans();
        EditorBeanSwing ed = (EditorBeanSwing) masterDocView.getEditor((RapidBean) masterTree.getPathForRow(cprow + 1)
                .getLastPathComponent(), false);
        Assert.assertEquals("Weihnachtsferien", ed.getPropEditors().get(0).getInputFieldValue());
        EditorPropertyListSwing ped = (EditorPropertyListSwing) ed.getPropEditor("locations");
        Assert.assertEquals(2, ped.getWidgetList().getModel().getSize());

        // delete location "Turnhalle Grundschule Süd"
        Location loc = (Location) ped.getProperty().getBean().getContainer()
                .findBean("org.rapidbeans.clubadmin.domain.Location", "Turnhalle Grundschule Süd");
        Assert.assertSame(loc, ped.getWidgetList().getModel().getElementAt(1));
        loc.delete();

        Assert.assertEquals(1, ped.getWidgetList().getModel().getSize());

        // close the closing period editor
        ed.handleActionClose();

        masterTree.setSelectionPath(masterTree.getPathForRow(cprow + 1));
        masterTreeView.editBeans();
        ed = (EditorBeanSwing) masterDocView.getEditor((RapidBean) masterTree.getPathForRow(cprow + 1)
                .getLastPathComponent(), false);
        Assert.assertEquals("Weihnachtsferien", ed.getPropEditors().get(0).getInputFieldValue());
        ped = (EditorPropertyListSwing) ed.getPropEditor("locations");
        Assert.assertEquals(1, ped.getWidgetList().getModel().getSize());
        loc = (Location) ped.getProperty().getBean().getContainer()
                .findBean("org.rapidbeans.clubadmin.domain.Location", "Eurythmiesaal 1 Waldorfschule");
        Assert.assertSame(loc, ped.getWidgetList().getModel().getElementAt(0));
        loc.delete();
        ed.handleActionClose();
        Assert.assertEquals(0, ped.getWidgetList().getModel().getSize());
    }

    /**
     * Tests a sequence that formerly lead to a concurrent modification
     * exception. - open location "Eurythmiesaal 1 Waldorfschule" in a first
     * editor - open closing period "Weihnachtsferien 20051222" in a second
     * editor - unselect checkbox "Eurythmiesaal 1 Waldorfschule" - select
     * checkbox "Eurythmiesaal 1 Waldorfschule" again changed from Checkboxes to
     * List so: - remove "Eurythmiesaal 1 Waldorfschule" - add "Eurythmiesaal 1
     * Waldorfschule" again
     */
    @Test
    public void testEditLocationAndClosingPeriodCloseCp() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing masterDocView = this.getTestviewMasterdata();
        DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        masterTreeView.setShowBeanLinks(false);
        JTree masterTree = (JTree) masterTreeView.getTree();
        final int locationrow = treeViewIndexLocations;
        Assert.assertEquals("locations", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(locationrow)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Assert.assertEquals("closingperiods", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(locationrow + 1)
                .getLastPathComponent()).getColProp().getType().getPropName());

        // expand locations and closing periods
        masterTree.expandPath(masterTree.getPathForRow(locationrow + 1));
        masterTree.expandPath(masterTree.getPathForRow(locationrow));
        Assert.assertEquals("locations", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(locationrow)
                .getLastPathComponent()).getColProp().getType().getPropName());
        Assert.assertEquals("closingperiods", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(locationrow + 3)
                .getLastPathComponent()).getColProp().getType().getPropName());

        // open Location "Eurythmiesaal 1 Waldorfschule" for editing
        masterTree.setSelectionPath(masterTree.getPathForRow(locationrow + 1));
        masterTreeView.editBeans();
        EditorBeanSwing ed1 = (EditorBeanSwing) masterDocView.getEditor(
                (RapidBean) masterTree.getPathForRow(locationrow + 1).getLastPathComponent(), false);
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule", ed1.getPropEditors().get(0).getInputFieldValue());
        EditorPropertyListSwing proped1 = (EditorPropertyListSwing) ed1.getPropEditors().get(3);
        JList listPropEdLocCps = proped1.getWidgetList();
        Assert.assertEquals(5, listPropEdLocCps.getModel().getSize());
        Assert.assertEquals("20051222_Weihnachtsferien",
                ((RapidBean) listPropEdLocCps.getModel().getElementAt(0)).getIdString());
        Assert.assertEquals("20060116_Schulputztag",
                ((RapidBean) listPropEdLocCps.getModel().getElementAt(1)).getIdString());

        // open Closing Period "Weihnachtsferien" for editing
        masterTree.setSelectionPath(masterTree.getPathForRow(locationrow + 4));
        masterTreeView.editBeans();
        EditorBeanSwing ed2 = (EditorBeanSwing) masterDocView.getEditor(
                (RapidBean) masterTree.getPathForRow(locationrow + 4).getLastPathComponent(), false);
        Assert.assertEquals("Weihnachtsferien", ed2.getPropEditors().get(0).getInputFieldValue());
        EditorPropertyListSwing proped2 = (EditorPropertyListSwing) ed2.getPropEditor("locations");
        Assert.assertEquals(2, proped2.getWidgetList().getModel().getSize());
        Assert.assertEquals("Eurythmiesaal 1 Waldorfschule", ((RapidBean) proped2.getWidgetList().getModel()
                .getElementAt(0)).getIdString());
        Assert.assertEquals("Turnhalle Grundschule Süd", ((RapidBean) proped2.getWidgetList().getModel()
                .getElementAt(1)).getIdString());
        Location loc = (Location) proped2.getProperty().getBean().getContainer()
                .findBean("org.rapidbeans.clubadmin.domain.Location", "Eurythmiesaal 1 Waldorfschule");
        Assert.assertNotNull(loc);
        ((PropertyCollection) proped2.getProperty()).removeLink(loc);
        ((PropertyCollection) proped2.getProperty()).addLink(loc);
    }

    /**
     * create a duplicate salary.
     */
    @Test
    public void testEditSalary() {

        // get the document tree view of document "masterdata"
        DocumentViewSwing view = this.getTestviewMasterdata();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) view.getTreeView();
        JTree masterTree = (JTree) treeView.getTree();
        Assert.assertEquals("trainerroles", ((DocumentTreeNodePropColComp) masterTree.getPathForRow(3)
                .getLastPathComponent()).getColProp().getType().getPropName());

        // expand property node "trainerroles/salarys" in the tree view and
        // open a bean editor for editing a salary
        masterTree.expandPath(masterTree.getPathForRow(3));
        masterTree.expandPath(masterTree.getPathForRow(4));
        masterTree.expandPath(masterTree.getPathForRow(5));
        masterTree.setSelectionPath(masterTree.getPathForRow(6));
        EditorBeanSwing editor = (EditorBeanSwing) treeView.editBeans();
        HashMap<String, Object> buttons = editor.getButtonWidgets();

        // assert the usual "not changed" situation for the buttons
        Assert.assertFalse(editor.isAnyInputFieldChanged());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Übernehmen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(false, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Schließen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        EditorPropertyListSwing ped = (EditorPropertyListSwing) editor.getPropEditor("trainerattribute");
        Assert.assertFalse(ped.getWidgetEditButton().isEnabled());
        EditorProperty pedMoney = editor.getPropEditor("money");
        JTextField tf1 = (JTextField) ((JPanel) pedMoney.getWidget()).getComponent(0);
        tf1.setText("11");
        JComboBox cb1 = (JComboBox) ((JPanel) pedMoney.getWidget()).getComponent(1);
        cb1.setSelectedItem(Currency.euro);
        pedMoney.fireInputFieldChanged();
        JTextField tf2 = (JTextField) ((JPanel) editor.getPropEditor("time").getWidget()).getComponent(0);
        tf2.setText("1");
        JComboBox cb2 = (JComboBox) ((JPanel) editor.getPropEditor("time").getWidget()).getComponent(1);
        cb2.setSelectedItem(UnitTime.h);
        editor.getPropEditors().get(2).fireInputFieldChanged();
        Assert.assertTrue(editor.isAnyInputFieldChanged());
        Assert.assertEquals("OK", ((JButton) buttons.get("ok")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("ok")).isEnabled());
        Assert.assertEquals("Übernehmen", ((JButton) buttons.get("apply")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("apply")).isEnabled());
        Assert.assertEquals("Abbrechen", ((JButton) buttons.get("close")).getText());
        Assert.assertEquals(true, ((JButton) buttons.get("close")).isEnabled());

        // simulate pressing the "Apply" button the 1st time
        editor.handleActionApply();
    }

    /**
     * First test with the normal "trainingslist" view. A training held by
     * trainer with null role must not be accepted.
     */
    @Test
    public void testEditTrainingNullRole() {
        // get the normal trainings list view
        ViewTrainings docView = this.getTestviewTrainingsListTrainings();
        Document doc = docView.getDocument();
        ViewTrainingHeldByTrainerList trainersView = docView.getTrainersView();
        // add a new training held by trainer
        TrainingHeldByTrainer newTrhbt = trainersView.addNewHeldByTrainer();
        // set Martin as trainer but leave the trainer role undefined
        Trainer martin = (Trainer) doc.findBean("org.rapidbeans.clubadmin.domain.Trainer", "Blümel_Martin_");
        newTrhbt.setTrainer(martin);
        try {
            // try to check the training held by trainer with undefined role
            docView.toggleChecked();
        } catch (RapidClubAdminBusinessLogicException e) {
            // assert the exception that is thrown
            Assert.assertEquals("Trainings held by trainers must have a trainer role set.", e.getMessage());
        }
    }

    // /**
    // * The deletion of a CreditInstitute that is associated with a Trainer
    // * is not allowed. If we select multiple CreditInstitutes deletion
    // * breaks at the first one that can't be deleted but:
    // * currently we do not implement atomic behaviour here (no transaction).
    // * That means that all CreditInstitutes that can be deleted are
    // * deleted.
    // */
    // public void testDeleteCreditinstitutesUsed() {

    // // get the document tree view of document "masterdata"
    // DocumentViewSwing masterDocView = this.getTestviewMasterdata();
    // Document master = client.getDocument("masterdata");
    // DocumentTreeViewSwing masterTreeView = (DocumentTreeViewSwing)
    // masterDocView.getTreeView();
    // JTree masterTree = (JTree) masterTreeView.getTree();
    // Assert.assertEquals("creditinstitutes", ((DocumentTreeNodePropColComp)
    // masterTree.getPathForRow(7).getLastPathComponent())
    // .getColProp().getType().getPropName());

    // // expand creditinstitutes and select them all
    // masterTree.expandPath(masterTree.getPathForRow(7));
    // Assert.assertEquals("creditinstitutes", ((DocumentTreeNodePropColComp)
    // masterTree.getPathForRow(7).getLastPathComponent())
    // .getColProp().getType().getPropName());
    // Assert.assertEquals("ABC Bank", ((CreditInstitute)
    // masterTree.getPathForRow(8).getLastPathComponent()).getName());
    // Assert.assertEquals("Stadtsparkasse München", ((CreditInstitute)
    // masterTree.getPathForRow(12).getLastPathComponent()).getName());
    // Assert.assertNull(masterTree.getPathForRow(13));
    // TreePath[] paths = {
    // masterTree.getPathForRow(8),
    // masterTree.getPathForRow(9),
    // masterTree.getPathForRow(10),
    // masterTree.getPathForRow(11),
    // masterTree.getPathForRow(12)
    // };
    // masterTree.setSelectionPaths(paths);

    // // try to delete all credit institutes
    // Assert.assertEquals(5,
    // master.findBeansByType("org.rapidbeans.clubadmin.domain.CreditInstitute").size());
    // //try {
    // masterTreeView.deleteBeans();
    // //fail("expected RapidClubAdminDomainException");
    // //} catch (RapidClubAdminBusinessLogicException e) {
    // //assertTrue(true);
    // //}

    // // should have managed to delete 1 of them
    // Assert.assertEquals(0,
    // master.findBeansByType("org.rapidbeans.clubadmin.domain.CreditInstitute").size());

    // // assert that the tree view shows only the beans that were not deleted
    // //assertEquals("creditinstitutes", ((DocumentTreeNodePropColComp)
    // //masterTree.getPathForRow(7).getLastPathComponent())
    // //.getColProp().getType().getPropName());
    // //assertEquals("Bayrische Landesbank", ((CreditInstitute)
    // //masterTree.getPathForRow(8).getLastPathComponent()).getName());
    // //assertEquals("Halsabschneider Bank", ((CreditInstitute)
    // //masterTree.getPathForRow(9).getLastPathComponent()).getName());
    // //assertEquals("Hypovereinsbank München", ((CreditInstitute)
    // //masterTree.getPathForRow(10).getLastPathComponent()).getName());
    // //assertEquals("Stadtsparkasse München", ((CreditInstitute)
    // //masterTree.getPathForRow(11).getLastPathComponent()).getName());
    // //assertNull(masterTree.getPathForRow(12));
    // }

    /**
     * Test if Trainings are presented properly.
     */
    @Test
    public void testTreeViewShowTrainings() {

        DocumentViewSwing masterDocView = this.getTestviewTrainingsListExpert();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) masterDocView.getTreeView();
        treeView.setShowBeanLinks(false);
        JTree tree = (JTree) treeView.getTree();
        int clubsrow = 1;
        tree.expandPath(tree.getPathForRow(clubsrow));
        tree.expandPath(tree.getPathForRow(clubsrow + 1));
        tree.expandPath(tree.getPathForRow(clubsrow + 2));
        tree.expandPath(tree.getPathForRow(clubsrow + 3));
        tree.expandPath(tree.getPathForRow(clubsrow + 4));
        tree.expandPath(tree.getPathForRow(clubsrow + 6));
        // expand trainings and check the text of the first one
        Assert.assertEquals("trainings", ((DocumentTreeNodePropColComp) tree.getPathForRow(clubsrow + 8)
                .getLastPathComponent()).getColProp().getType().getPropName());
        tree.expandPath(tree.getPathForRow(clubsrow + 8));
        CellRendererPane crp = (CellRendererPane) tree.getComponent(0);
        DocumentTreeCellRenderer dtcr = (DocumentTreeCellRenderer) crp.getComponent(1);
        Assert.assertEquals("27.03 19:30 Iaido/Aikido Erwachsene", dtcr.getText());
    }

    /**
     * Test if Trainings a.
     */
    @Test
    public void testTrainingSetTrainerTwice() {

        // get the document tree view of a trainingslist document
        DocumentViewSwing docView = this.getTestviewTrainingsListExpert();
        DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) docView.getTreeView();
        treeView.setShowBeanLinks(false);
        JTree tree = (JTree) treeView.getTree();
        int clubsrow = 1;
        tree.expandPath(tree.getPathForRow(clubsrow));
        tree.expandPath(tree.getPathForRow(clubsrow + 1));
        tree.expandPath(tree.getPathForRow(clubsrow + 2));
        tree.expandPath(tree.getPathForRow(clubsrow + 3));
        tree.expandPath(tree.getPathForRow(clubsrow + 4));
        tree.expandPath(tree.getPathForRow(clubsrow + 6));
        // expand trainings and check the text of the first one
        Assert.assertEquals("trainings", ((DocumentTreeNodePropColComp) tree.getPathForRow(clubsrow + 8)
                .getLastPathComponent()).getColProp().getType().getPropName());
        tree.expandPath(tree.getPathForRow(clubsrow + 8));
        tree.expandPath(tree.getPathForRow(clubsrow + 9));
        tree.setSelectionPath(tree.getPathForRow(clubsrow + 10));
        EditorBean editor = treeView.createBean();
        EditorPropertyComboboxSwing proped0 = (EditorPropertyComboboxSwing) editor.getPropEditors().get(0);
        Assert.assertEquals("role", proped0.getProperty().getType().getPropName());
        JComboBox cb0 = (JComboBox) proped0.getWidget();
        TrainerRole roleTrainer = (TrainerRole) editor.getDocumentView().getDocument()
                .findBean("org.rapidbeans.clubadmin.domain.TrainerRole", "Trainer");
        Assert.assertNotNull(roleTrainer);
        Assert.assertNull(cb0.getSelectedItem());
        cb0.setSelectedItem(roleTrainer);
        Assert.assertSame(roleTrainer, proped0.getInputFieldValue());
        Assert.assertEquals("Trainer", proped0.getInputFieldValue().toString());
        EditorPropertyComboboxSwing proped1 = (EditorPropertyComboboxSwing) editor.getPropEditors().get(1);
        Assert.assertEquals("trainer", proped1.getProperty().getType().getPropName());
        JComboBox cb1 = (JComboBox) proped1.getWidget();
        Trainer berit = (Trainer) editor.getDocumentView().getDocument()
                .findBean("org.rapidbeans.clubadmin.domain.Trainer", "Dahlheimer_Berit_");
        Assert.assertNotNull(berit);
        Assert.assertNull(cb1.getSelectedItem());
        cb1.setSelectedItem(berit);
        Assert.assertSame(berit, cb1.getSelectedItem());
        Assert.assertSame(berit, proped1.getInputFieldValue());
        Assert.assertEquals("Dahlheimer_Berit_", proped1.getInputFieldValue().toString());
        try {
            editor.handleActionApply();
            Assert.fail("expected ValidationException");
        } catch (ValidationException e) {
            Assert.assertTrue(e.getMessage()
                    .startsWith("invalid value \"Dahlheimer_Berit_\" for property \"trainer\"."));
        }
    }

    /**
     * set from and to and decrease to aftwerads.
     * 
     * @throws ParseException
     *             if date parsing fails
     */
    @Test
    public void testBPUpdateTrainings() throws ParseException {
        DocumentViewSwing view = this.getTestviewTrainingsListExpert();
        Document doc = view.getDocument();
        TrainingsList bp = (TrainingsList) doc.getRoot();
        Assert.assertEquals(78, doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training").size());
        Assert.assertEquals(DFDATE.parse("31.03.2006"), bp.getTo());
        bp.setTo(DFDATE.parse("01.03.2006"));
        bp.updateTrainings(TrainingsList.UPDATE_MODE_PROPS, null);
        Assert.assertEquals(53, doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training").size());
    }

    /**
     * Date formatter.
     */
    static final DateFormat DFDATE = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);

    /**
     * test if both test view are initialized at all.
     */
    @Test
    public void testInitDocumentViews() {
        this.getTestviewTrainingsListExpert();
        View view = client.getView("trainingslist.expert");
        Assert.assertNotNull(view);
        this.getTestviewMasterdata();
        view = client.getView("masterdata.standard");
        Assert.assertNotNull(view);
    }

    /**
     * test document.
     */
    private Document docMaster = null;

    /**
     * test document.
     */
    private Document docTrainingsList = null;

    /**
     * test view.
     */
    private DocumentViewSwing viewMaster = null;

    /**
     * test view.
     */
    private View viewTrainingsList = null;

    /**
     * test helper.
     * 
     * @return a test document
     */
    private Document getTestdocMasterdata() {
        if (this.docMaster == null) {
            client.openDocumentView(new Document(new File("testdata/masterdata.xml")), "masterdata", "standard");
            this.docMaster = client.getDocument("masterdata");
            this.viewMaster = (DocumentViewSwing) client.getView("documentview.masterdata");
        }
        return this.docMaster;
    }

    /**
     * test helper.
     * 
     * @return a test view
     */
    private DocumentViewSwing getTestviewMasterdata() {
        retry(new Class[] { RapidBeansRuntimeException.class, ValidationException.class, }, new RetryableAction() {
            @Override
            public void doSomething() {
                if (docMaster == null) {
                    client.openDocumentView(new Document("masterdata", new File("testdata/masterdata.xml")),
                            "masterdata", "standard");
                    docMaster = client.getDocument("masterdata");
                    viewMaster = (DocumentViewSwing) client.getView("masterdata.standard");
                }
            }
        });
        return this.viewMaster;
    }

    /**
     * test helper.
     * 
     * @return a test view
     */
    private DocumentViewSwing getTestviewTrainingsListExpert() {
        if (this.docTrainingsList == null) {
            final File testfile = new File("testdata/trainingslist_20060101_20060331.xml");
            client.openDocumentView(new Document("trainingslist", testfile), "trainingslist", "expert");
            this.docTrainingsList = client.getDocument("trainingslist");
            this.viewTrainingsList = (DocumentViewSwing) client.getView("trainingslist" + ".expert");
        }
        return (DocumentViewSwing) this.viewTrainingsList;
    }

    /**
     * test helper.
     * 
     * @return a test view
     */
    private ViewTrainings getTestviewTrainingsListTrainings() {
        if (this.docTrainingsList == null) {
            final File testfile = new File("testdata/trainingslist_20060101_20060331.xml");
            client.openDocumentView(new Document("trainingslist", testfile), "trainingslist", "trainings");
            this.docTrainingsList = client.getDocument("trainingslist");
            this.viewTrainingsList = client.getView("trainingslist" + ".trainings");
        }
        return (ViewTrainings) this.viewTrainingsList;
    }

    private static class ApplicationMock extends RapidClubAdminClient {

        @Override
        public boolean getTestMode() {
            return TEST_MODE;
        }

        @Override
        public void start() {
            setAuthnRoleType("org.rapidbeans.clubadmin.domain.Role");
            this.setConfiguration((ConfigApplication) new Document(TypeRapidBean.forName(ConfigApplication.class
                    .getName()), new File("config/org/rapidbeans/clubadmin/Application.xml")).getRoot());
            this.getMasterDoc();
            Assert.assertNotNull(getAuthnDoc().findBean("org.rapidbeans.security.User", "testuser"));
            init();
            if (!this.getTestMode()) {
                this.getMainwindow().show();
            }
        }

        /**
         * initializes the application.
         */
        @Override
        public void init() {
            initLocales();
            initMessageDialog();
            this.setMainwindow(MainWindow.createInstance(this, getConfiguration().getMainwindow()));
        }

        private Document settingsDoc = null;

        @Override
        public Document getSettingsDoc() {
            if (this.settingsDoc == null) {
                this.settingsDoc = new Document(new File("testdata/testsettings.xml"));
            }
            return this.settingsDoc;
        }

        @Override
        public SettingsAll getSettings() {
            return (Settings) this.getSettingsDoc().getRoot();
        }

        @Override
        public RapidClubAdminSettings getSettingsRapidClubAdmin() {
            return ((Settings) this.getSettingsDoc().getRoot()).getSettings();
        }

        private Document masterDoc = null;

        private boolean initializingMasterData = false;

        public boolean isInitializingMasterData() {
            return initializingMasterData;
        }

        @Override
        public Document getMasterDoc() {
            if (this.masterDoc == null) {
                try {
                    initializingMasterData = true;
                    this.masterDoc = new Document(new File("testdata/masterdata.xml"));
                } finally {
                    initializingMasterData = false;
                }
            }
            return this.masterDoc;
        }
    }

    private static void retry(final RetryableAction action) {
        retry(1, 100, action);
    }

    private static void retry(final int sleepMillis, final int retryCount, final RetryableAction action) {
        try {
            action.doSomething();
        } catch (AssertionError e) {
            log.fine("assertion failed :-(, try retry");
            for (int i = 1; i <= retryCount; i++) {
                try {
                    try {
                        Thread.sleep(sleepMillis);
                    } catch (InterruptedException e1) {
                        throw new RuntimeException(e1);
                    }
                    action.doSomething();
                    log.fine("Hurray: " + i + " times retry helped");
                    break;
                } catch (AssertionError e1) {
                    Assert.assertTrue(true); // do nothing by intention
                }
            }
        }
    }

    private static void retry(final Class<?> exClass, final RetryableAction action) {
        retry(new Class[] { exClass }, action);
    }

    private static void retry(final Class<?>[] exClasses, final RetryableAction action) {
        retry(1, 100, exClasses, action);
    }

    private static void retry(final int sleepMillis, final int retryCount, final Class<?>[] exClasses,
            final RetryableAction action) {
        try {
            action.doSomething();
        } catch (RuntimeException e) {
            if (isOneOf(e.getClass(), exClasses)) {
                log.fine("got " + e.getClass().getName() + " :-(, try retry");
            } else {
                log.fine("got an unexpected exception :-(");
                throw e;
            }
            int i;
            for (i = 1; i <= retryCount; i++) {
                try {
                    try {
                        Thread.sleep(sleepMillis);
                    } catch (InterruptedException e1) {
                        throw new RuntimeException(e1);
                    }
                    action.doSomething();
                    log.fine("Hurray: " + i + " times retry helped");
                    break;
                } catch (RuntimeException e1) {
                    if (!isOneOf(e.getClass(), exClasses)) {
                        log.fine("got an unexpected exception :-(");
                        throw e;
                    }
                }
            }
            if (i > retryCount) {
                log.warning((i - 1) + " times retry did not help");
                throw e;
            }
        }
    }

    private static boolean isOneOf(final Class<?> clazz, final Class<?>[] classes) {
        boolean oneOf = false;
        for (final Class<?> curClass : classes) {
            if (curClass == clazz) {
                oneOf = true;
                break;
            }
        }
        return oneOf;
    }

    private interface RetryableAction {
        void doSomething();
    }
}
