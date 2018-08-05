/*
 * Rapid Club Admin: RapidClubAdminClient.java
 *
 * Copyright Martin Bluemel, 2009
 *
 * January 1, 2006
 */
package org.rapidbeans.clubadmin.presentation;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.rapidbeans.clubadmin.datasource.WebFileManager;
import org.rapidbeans.clubadmin.domain.BillingPeriod;
import org.rapidbeans.clubadmin.domain.Club;
import org.rapidbeans.clubadmin.domain.ClubadminUser;
import org.rapidbeans.clubadmin.domain.DataFileType;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.MasterData;
import org.rapidbeans.clubadmin.domain.Role;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.clubadmin.presentation.swing.TrainerIconManager;
import org.rapidbeans.clubadmin.service.OpenCurrentTrainingsList;
import org.rapidbeans.clubadmin.service.OpenMyUserAccount;
import org.rapidbeans.core.basic.Property;
import org.rapidbeans.core.basic.PropertyAssociationend;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.basic.ThreadLocalValidationSettings;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.exception.LocalizedException;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.core.type.RapidBeansTypeLoader;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.datasource.CharsetsAvailable;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DefaultEncodingUsage;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.presentation.settings.SettingsBasicGui;
import org.rapidbeans.presentation.settings.swing.SettingsBasicGuiSwing;
import org.rapidbeans.presentation.swing.DocumentTreeViewSwing;
import org.rapidbeans.presentation.swing.EditorBeanSwing;
import org.rapidbeans.presentation.swing.EditorPropertyFileSwing;
import org.rapidbeans.presentation.swing.EditorPropertySwing;
import org.rapidbeans.security.ChangePwdAfterNextlogonType;
import org.rapidbeans.service.Action;
import org.rapidbeans.service.ActionArgument;

/**
 * RapidClubAdminClient is the application root object and the one and only
 * singleton. This class implements also reusable parts of the service layer.
 */
public class RapidClubAdminClient extends Application {

    private static final Logger log = Logger.getLogger(RapidClubAdminClient.class.getName());

    /**
     * The server root specifies the server + any subfolder that is above the
     * (relative) application root. E. g. server = www.muenchen-surf.de/bluemel
     * root = software/rapidbeans/rapidclubadmin/test
     * 
     * The server root is given as start argument "-server" if the application
     * is supposed to run against a server ("web" case - the usual productive
     * use case). If the application is meant to run locally do not specify this
     * argument at all. The server root stays null then. ("local" case - the
     * usual test use case)
     */
    private String server = null;

    public String getServer() {
        return server;
    }

    /**
     * The application root is relative to the server root in the "web" case e.
     * g. server = "www.muenchen-surf.de/bluemel" root =
     * "software/rapidbeans/rapidclubadmin/test" In the local case it is simply
     * an absolute file system path. e. g. server = null root = -root
     * "D:/Projects/RapidClubAdmin"
     */
    private String root = null;

    /**
     * @return the root
     */
    public String getRoot() {
        return root;
    }

    /**
     * Setter intended for test purposes only.
     * 
     * @param root
     *            the root to set
     */
    protected void setRoot(String root) {
        this.root = root;
    }

    public RapidClubAdminClient() {
        super();
        Locale.setDefault(Locale.GERMANY);
        RapidBeansTypeLoader.getInstance().addXmlRootElementBinding("applicationcfg",
                "org.rapidbeans.presentation.config.swing.ConfigApplicationSwing", true);
    }

    /**
     * starts the client.
     * 
     * @param options
     *            the start options
     */
    public void start() {
        final String className = StringHelper.splitLast(this.getClass().getName(), ".");
        final String classFileName = className + ".class";
        final URL urlToThisClass = this.getClass().getResource(classFileName);
        final String pathToThisClass = urlToThisClass.toString();
        log.fine("pathToThisClass = \"" + pathToThisClass + "\"");
        this.server = this.getOptions().getProperty("server");
        this.root = this.getOptions().getProperty("root");
        if (this.server == null && this.root == null && pathToThisClass.startsWith("jar:http://")) {
            final StringTokenizer st = new StringTokenizer(pathToThisClass, "/");
            st.nextToken();
            this.server = st.nextToken();
            final StringBuffer sb = new StringBuffer();
            String tok = st.nextToken();
            int i = 0;
            while ((!tok.startsWith("rapidclubadmin-")) && (!tok.endsWith(".jar!"))) {
                if (i > 0) {
                    sb.append('/');
                }
                sb.append(tok);
                tok = st.nextToken();
                i++;
            }
            this.root = sb.toString();
        }
        log.info("server = \"" + this.server + "\"");
        log.info("applet / application Root = \"" + this.root + "\"");
        if (getRunMode() == DataFileType.web) {
            this.webFileManager = new WebFileManager(this.server, this.root);
            this.webFileManager.init();
            this.trainerIcons = new TrainerIconManager(this, TrainerIconManager.ICON_REPOSITORY_TYPE_REMOTE, null,
                    null, null);
        } else {
            this.trainerIcons = new TrainerIconManager(this, TrainerIconManager.ICON_REPOSITORY_TYPE_LOCAL, null, null,
                    null);
        }
        init();
        if (this.isUsingAuthorization() && this.getAuthenticatedUser() == null) {
            return;
        }
        if (getAuthenticatedUser() != null && getPwdChanged()) {
            ((ClubadminUser) getAuthenticatedUser()).setChangePwdAfterNextLogon(ChangePwdAfterNextlogonType.no);
            save(getMasterDoc());
        }
        MenuHelper.updateSpecificMenus();
        final Department defaultDep = this.getAuthorizedWorkingDepartment();
        if (defaultDep == null) {
            if (this.getAuthenticatedUser() != null
                    && ((ClubadminUser) this.getAuthenticatedUser()).getRole() != Role.SuperAdministrator) {
                if (!this.getTestMode()) {
                    this.messageError(
                            this.getCurrentLocale().getStringMessage("error.authorization.nodepartment",
                                    (String) this.getAuthenticatedUser().getPropValue("accountname")), this
                                    .getCurrentLocale().getStringMessage("error.authorization.title"));
                }
                System.exit(1);
            }
        }
        if (this.getSettingsRapidClubAdmin().getDefaultdatafileloadinitially() && defaultDep != null) {
            final String depIdString = defaultDep.getIdString();
            final ActionArgument actionArgument = new ActionArgument();
            actionArgument.setName("department");
            actionArgument.setValue(depIdString);
            final List<ActionArgument> actionArguments = new ArrayList<ActionArgument>();
            actionArguments.add(actionArgument);
            final Action openAction = new OpenCurrentTrainingsList();
            openAction.setArguments(actionArguments);
            this.getActionManager().execute(openAction);
        }
        getMainwindow().show();
        getMainwindow().getFooter().showMessage(
                getCurrentLocale().getStringMessage("info.clubadmin.started.successfully"));
        if (!this.getSettingsRapidClubAdmin().getPleasedontnag()) {
            askForUserEmail();
        }
    }

    public DataFileType getRunMode() {
        if (this.server != null) {
            return DataFileType.web;
        }
        return DataFileType.local;
    }

    /**
     * ends the client.
     * 
     * @return if canceled
     */
    public boolean end() {
        return super.end();
    }

    /**
     * @return the application specific settings.
     */
    public RapidClubAdminSettings getSettingsRapidClubAdmin() {
        Settings settings = null;
        if (this.getTestMode()) {
            settings = (Settings) new Document(new File("testdata/testsettings.xml")).getRoot();
        } else {
            settings = (Settings) this.getSettings();
        }
        return settings.getSettings();
    }

    /**
     * the master data document.
     */
    private Document masterDoc = null;

    /**
     * setter for unit testing reasons.
     * 
     * @param doc
     *            the masterdata document
     */
    public void setMasterDoc(final Document doc) {
        this.masterDoc = doc;
    }

    /**
     * flag to prevent endless recursion.
     */
    private boolean initMasterData = false;

    /**
     * @return if the client currently is initializing MasterData
     */
    public boolean isInitializingMasterData() {
        return this.initMasterData;
    }

    private File getLocalmasterdatafile() {
        final File localMaserdataFile = new File(this.root, "data/masterdata.xml");
        if (!localMaserdataFile.exists()) {
            throw new RapidBeansRuntimeException("Local masterdata file \"" + localMaserdataFile.getAbsolutePath()
                    + "\" not found.");
        }
        return localMaserdataFile;
    }

    /**
     * @return the masterdata document.
     */
    public Document getMasterDoc() {
        try {
            this.initMasterData = true;
            if (this.masterDoc == null) {
                switch (getRunMode()) {
                case local:
                    try {
                        final File masterdatafile = getLocalmasterdatafile();
                        if (masterdatafile == null) {
                            this.messageError(this.getCurrentLocale().getStringMessage(""), this.getCurrentLocale()
                                    .getStringMessage("error.init.title"));
                            this.end();
                            throw new LocalizedException("error.init.local.not.masterdatafile", "error.init.title",
                                    "No local masterdata file specified");
                        }
                        if (!masterdatafile.exists()) {
                            if (masterdatafile.getParentFile().exists()) {
                                final Document masterdoc = new Document("masterdata", new MasterData());
                                masterdoc.setUrl(masterdatafile.toURI().toURL());
                                // direct save is OK for the "local" case
                                masterdoc.save();
                            } else {
                                throw new LocalizedException("error.load.file.local.masterdata.filenotfound",
                                        "error.load.file.local.title", "parent folder of masterdata file \""
                                                + masterdatafile.getAbsolutePath() + "\" does not exist",
                                        new Object[] { masterdatafile.getAbsolutePath() });
                            }
                        }
                        if (this.masterDoc == null) {
                            this.masterDoc = new Document("masterdata", masterdatafile);
                        }
                    } catch (MalformedURLException e) {
                        throw new RapidBeansRuntimeException(e);
                    }
                    break;
                case web:
                    this.masterDoc = this.webFileManager.downloadDocument("masterdata", FILE_NAME_MASTERDATA, true,
                            true, true, null, null);
                    final List<Trainer> trainers = this.getAllTrainersFromDocument(this.masterDoc);
                    this.trainerIcons.updateIcons(trainers, true);
                    break;
                default:
                    break;
                }
            }

            return this.masterDoc;
        } finally {
            this.initMasterData = false;
        }
    }

    /**
     * the customer document.
     */
    private CustomerSettings customerSettings = null;

    /**
     * @return the customer data.
     */
    public CustomerSettings getCustomerSettings() {
        if (this.customerSettings == null) {
            this.initCustomerSettings();
        }
        return this.customerSettings;
    }

    private void initCustomerSettings() {
        if (this.getWebFileManager() != null) {
            final Document doc = this.getWebFileManager().downloadDocumentHttpreadonly("customersettings.xml",
                    "customersettings", null, null);
            this.customerSettings = (CustomerSettings) doc.getRoot();
        }
    }

    /**
     * Get a list of trainers from the master doc.
     * 
     * @param doc
     *            the document to search for trainers
     * 
     * @return the list of all trainers.
     */
    private List<Trainer> getAllTrainersFromDocument(final Document doc) {
        final List<Trainer> trainers = new ArrayList<Trainer>();
        final List<RapidBean> beans = doc.findBeansByType("org.rapidbeans.clubadmin.domain.Trainer");
        for (final RapidBean bean : beans) {
            trainers.add((Trainer) bean);
        }
        return trainers;
    }

    /**
     * @return the authn document.
     */
    public Document getAuthnDoc() {
        if (super.getAuthnDoc() == null) {
            super.setAuthnDoc(this.getMasterDoc());
        }
        return super.getAuthnDoc();
    }

    /**
     * Set the authentication document
     */
    @Override
    protected void setAuthnDoc(final Document doc) {
        super.setAuthnDoc(doc);
        this.setMasterDoc(doc);
    }

    /**
     * @return the masterdata document.
     */
    public MasterData getMasterData() {
        return (MasterData) this.getMasterDoc().getRoot();
    }

    /**
     * open the current TrainingsList document view.
     * 
     * @param workinkDepartmentId
     *            the Id of the working department
     * 
     * @return the view if opened successfully
     */
    public final DocumentView openCurrentTrainingsList(final String workingDepartmentId) {
        final Department department = getWorkingDepartment(workingDepartmentId);
        Document doc = null;
        DocumentView currentView = (DocumentView) getView(getTrainingslistViewname(null, department));
        if (currentView != null) {
            doc = currentView.getDocument();
            this.getMainwindow().putToFront(currentView);
        } else {
            doc = loadTrainingslistDocument(null, department);
            if (doc != null) {
                currentView = openDocumentView(doc, "trainingslist",
                        this.getConfiguration().getConfigDocument("trainingslist").getDefaultview());
            }
        }
        setCurrentWorkingDepartment(doc);
        return currentView;
    }

    /**
     * open a history TrainingsList document view.
     * 
     * @param workinkDepartmentId
     *            the Id of the working department
     * 
     * @return the view if opened successfully
     */
    public final DocumentView openHistoryTrainingsList(final String bpId, final String workingDepartmentId) {
        if (this.getTestMode()) {
            return null;
        }
        final Department department = getWorkingDepartment(workingDepartmentId);
        final String[] sa = StringHelper.split(bpId, "_").toArray(new String[2]);
        final BillingPeriod bp = new BillingPeriod(sa);
        Document doc = null;
        DocumentView docView = (DocumentView) getView(getTrainingslistViewname(bp, department));
        if (docView != null) {
            doc = docView.getDocument();
            this.getMainwindow().putToFront(docView);
        } else {
            doc = loadTrainingslistDocument(bp, department);
            if (doc != null) {
                docView = openDocumentView(doc, "trainingslist",
                        this.getConfiguration().getConfigDocument("trainingslist").getDefaultview());
            }
        }
        return docView;
    }

    public Department getWorkingDepartment(final String workingDepartmentId) {
        Department workingDepartment = null;
        if (workingDepartmentId == null) {
            workingDepartment = getAuthorizedWorkingDepartment();
        } else {
            workingDepartment = (Department) this.getMasterDoc().findBean("org.rapidbeans.clubadmin.domain.Department",
                    workingDepartmentId);
        }
        if (workingDepartment == null) {
            this.messageError(
                    this.getCurrentLocale().getStringMessage("error.authorization.nodepartment",
                            (String) this.getAuthenticatedUser().getPropValue("accountname")), this.getCurrentLocale()
                            .getStringMessage("error.authorization.title"));
            return null;
        } else {
            if (!userIsAuthorized(workingDepartment.getIdString())) {
                this.messageError("Working department not authorized", "title");
            }
            this.setCurrentWorkingDepartment(workingDepartment);
        }
        return workingDepartment;
    }

    /**
     * Load a TrainingsList document locally or from the web.
     * 
     * @param bp
     *            specifies the BillingPeriod for history TrainingsLists<br/>
     *            Must be <code>null</code> if a current TrainingsList is to be
     *            loaded.
     * @param department
     *            specifies the department for which the TrainingsList document
     *            shall be loaded.
     * 
     * @return a document with the loaded TrainingsList as root element.
     */
    public Document loadTrainingslistDocument(final BillingPeriod bp, final Department department) {
        Document doc = null;
        final String docname = getTrainingslistDocname(bp, department);
        log.info("RapidClubAdminClient.loadTrainingslistDocument: \"" + docname + "\"");
        switch (getRunMode()) {
        case local:
            final File localCurrentTrainingsDataFile = this.getCurrentTrainingsDataFileLocal(bp, department);
            if (localCurrentTrainingsDataFile == null) {
                throw new LocalizedException("error.load.file.local.trainingslist.nourl",
                        "error.load.file.local.title", "No local default data file defined");
            }
            if (!localCurrentTrainingsDataFile.exists()) {
                throw new LocalizedException("error.load.file.local.trainingslist.filenotfound",
                        "error.load.file.local.title", "local file \""
                                + localCurrentTrainingsDataFile.getAbsolutePath() + "\" not found.",
                        new String[] { localCurrentTrainingsDataFile.getAbsolutePath() });
            }
            if (!localCurrentTrainingsDataFile.canRead()) {
                throw new LocalizedException("error.load.file.local.trainingslist.filenotreadable",
                        "error.load.file.local.title", "local file \""
                                + localCurrentTrainingsDataFile.getAbsolutePath() + "\" is not readable.",
                        new String[] { localCurrentTrainingsDataFile.getAbsolutePath() });
            }
            doc = new Document(docname, localCurrentTrainingsDataFile);
            break;
        case web:
            doc = this.webFileManager.downloadDocument(docname, RapidClubAdminClient.FILE_NAME_TRAININGS_LIST, true,
                    true, true, bp, department);
            if (doc != null) {
                this.trainerIcons.updateIcons(getAllTrainersFromDocument(doc), true);
            }
            break;
        default:
            break;
        }
        if (bp != null && doc != null) {
            doc.setReadonly(true);
        }
        return doc;
    }

    public DocumentView getTrainingslistView(final BillingPeriod bp, final Department department) {
        final String viewname = getTrainingslistViewname(bp, department);
        return (DocumentView) getView(viewname);
    }

    public String getTrainingslistDocname(final BillingPeriod bp, final Department department) {
        String docname = "currentTrainings";
        if (bp != null) {
            docname += '_' + bp.getIdString();
        }
        if (department != null) {
            docname += '_' + department.getIdString();
        }
        return docname;
    }

    public String getTrainingslistViewname(final BillingPeriod bp, final Department department) {
        return getTrainingslistDocname(bp, department) + ".trainings";
    }

    /**
     * Sets the current working department if annotated in the given
     * TrainingsList document.
     * 
     * @param doc
     *            a BilligPeriod document
     */
    public void setCurrentWorkingDepartment(final Document doc) {
        final Department wd = ((TrainingsList) doc.getRoot()).getForSingleDepartment();
        if (wd != null) {
            this.setCurrentWorkingDepartment(wd);
        }
    }

    /**
     * Sets the current working department according to the given department
     * 
     * @param dep
     *            the working Department to set.
     */
    public void setCurrentWorkingDepartment(final Department wd) {
        this.setCurrentWorkingDepartment(wd.getIdString());
    }

    /**
     * Sets the current working department according to the given department
     * 
     * @param dep
     *            the working Department to set.
     */
    public void setCurrentWorkingDepartment(final String depId) {
        ((Settings) this.getSettings()).getSettings().setWorkingdepartment(depId);
        save(getSettingsDoc());
    }

    public ClubadminUser getAuthenticatedClubadminUser() {
        ClubadminUser user = null;
        if (this.getAuthenticatedUser() != null) {
            user = (ClubadminUser) this.getAuthenticatedUser();
        }
        return user;
    }

    private boolean userIsAuthorized(final String wdIdString) {
        boolean userIsAuthorized = false;
        if (wdIdString != null && wdIdString.length() > 0 && this.getAuthenticatedUser() != null
                && this.getAuthenticatedClubadminUser().getAuthorizedDepartments() != null) {
            for (Department dep : this.getAuthenticatedClubadminUser().getAuthorizedDepartments()) {
                if (dep.getIdString().equals(wdIdString)) {
                    userIsAuthorized = true;
                }
            }
        }
        return userIsAuthorized;
    }

    /**
     * get the user's current working department from settings.
     * 
     * @return the user's current working department.
     */
    private Department getAuthorizedWorkingDepartment() {

        Department wd = null;

        // preferably take the working department out of the personal settings
        String wdIdString = getSettingsRapidClubAdmin().getWorkingdepartment();
        // but check if the user is authorized
        if (!userIsAuthorized(wdIdString)) {
            wdIdString = null;
        }

        // fallback 1: take the first department the user is authorized for
        if (wdIdString == null || wdIdString.length() == 0) {
            if (this.getAuthenticatedUser() != null) {
                ClubadminUser user = (ClubadminUser) this.getAuthenticatedUser();
                final Set<Department> authDeps = user.getAuthorizedDepartments();
                if (authDeps != null && authDeps.size() > 0) {
                    wd = authDeps.iterator().next();
                }
            }
        }

        // retrieve the department out of the id
        if (wdIdString != null && wdIdString.length() > 0) {
            wd = (Department) getMasterDoc().findBean("org.rapidbeans.clubadmin.domain.Department", wdIdString);
        }
        return wd;
    }

    /**
     * pop up the program settings and try to focus a certain property
     */
    public void popupProgramSettings(final String property) {
        final DocumentView settingsView = openDocumentView(getSettingsDoc(), "settings", "standard");
        final DocumentTreeViewSwing treeView = (DocumentTreeViewSwing) settingsView.getTreeView();
        treeView.setShowProperties(false);
        final JTree tree = (JTree) treeView.getTree();
        final TreePath path = tree.getPathForRow(3);
        tree.setSelectionPath(path);
        final Object[] keys = { path };
        final Object[] selObjs = { path.getLastPathComponent() };
        EditorBeanSwing ed = (EditorBeanSwing) treeView.editBeans(keys, selObjs);
        EditorPropertyFileSwing proped = (EditorPropertyFileSwing) ed.getPropEditor(property);
        if (proped == null) {
            ApplicationManager.getApplication().messageError(
                    "property editor for property \"" + property + "\" not found");
        } else {
            ((JTextField) proped.getTextWidget()).requestFocus();
            ((JTextField) proped.getTextWidget()).setBackground(EditorPropertySwing.COLOR_INVALID);
        }
    }

    /**
     * Drives the action to save the active document as the current trainings
     * list.
     * 
     * This action potentially overwrites data.
     * 
     * @param doctype
     *            { "trainingslist" | "masterdata"}
     */
    public void saveAsCurrent(final String doctype) {
        final Document activeDocument = getActiveDocument();
        if (activeDocument == null) {
            throw new RapidBeansRuntimeException("no document active");
        }
        this.saveAsCurrent(doctype, activeDocument);
    }

    /**
     * Drives the action to save the active document as the current trainings
     * list.
     * 
     * This action potentially overwrites data.
     * 
     * @param doctype
     *            { "trainingslist" | "masterdata"}
     * @param doc
     *            the document to save
     */
    public void saveAsCurrent(final String doctype, final Document doc) {
        final RapidBeansLocale locale = getCurrentLocale();
        if (doctype.equals("masterdata")) {
            if (!doc.getName().equals("masterdata")) {
                throw new RapidBeansRuntimeException("active document is not a masterdata");
            }
        } else {
            if (!doc.getName().startsWith("trainingslist")) {
                throw new RapidBeansRuntimeException("active document is not a trainings list");
            }
        }

        URL url = null;

        switch (getRunMode()) {
        case local:
            try {
                File file = null;
                if (doctype.equals("masterdata")) {
                    file = getLocalmasterdatafile();
                } else {
                    final Department dep = ((TrainingsList) doc.getRoot()).getForSingleDepartment();
                    file = this.getCurrentTrainingsDataFileLocal(null, dep);
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                }
                if (file != null) {
                    url = file.toURI().toURL();
                }
            } catch (MalformedURLException e) {
                throw new RapidBeansRuntimeException(e);
            }
            break;

        default:
            if (doctype.equals("masterdata")) {
                url = this.webFileManager.getUploadUrl(FILE_NAME_MASTERDATA, null, null);
            } else {
                final Department dep = ((TrainingsList) doc.getRoot()).getForSingleDepartment();
                url = this.webFileManager.getUploadUrl(FILE_NAME_TRAININGS_LIST, null, dep);
                // ensure that directory data/current/<department> exists
                final String parentUrl = StringHelper.strip(StringHelper.splitBeforeLast(url.getFile(), "/"), '/');
                this.webFileManager.mkdirs(parentUrl);
            }
            break;
        }

        if (url == null) {
            final String message = "error.save.file." + getRunMode().toString() + "." + doctype + ".nourl";
            final String title = "error.save.file." + getRunMode().toString() + "." + doctype + ".title";
            messageError(locale.getStringMessage(message), locale.getStringMessage(title));
        } else {
            if (messageYesNo(locale.getStringMessage("question.saveas.file." + doctype + ".knowwhatyoudo"),
                    locale.getStringMessage("question.saveas.file.title"))) {
                doc.setUrl(url);
                save(doc);
            }
        }
        this.setCurrentWorkingDepartment(doc);
    }

    public static final String FILE_NAME_MASTERDATA = "masterdata.xml";

    public static final String FILE_NAME_TRAININGS_LIST = "trainingslist.xml";

    /**
     * @param dep
     *            the department of the trainings document
     * 
     * @return an URL to locate the local current trainings document
     */
    public File getCurrentTrainingsDataFileLocal(final BillingPeriod bp, final Department dep) {
        final File trainingsDocRoot = new File(this.getRoot(), "data");
        return getCurrentTrainingsDataFileLocal(trainingsDocRoot, bp, dep);
    }

    /**
     * @param trainingsDocRoot
     *            the root of all evil
     * @param dep
     *            the department of the trainings document
     * 
     * @return an URL to locate the local current trainings document
     */
    public File getCurrentTrainingsDataFileLocal(final File trainingsDocRoot, final BillingPeriod bp,
            final Department dep) {
        if (!trainingsDocRoot.exists()) {
            throw new RapidBeansRuntimeException("Root folder \"" + trainingsDocRoot.getAbsolutePath() + "\""
                    + " does not exist.");
        }
        if (!trainingsDocRoot.isDirectory()) {
            throw new RapidBeansRuntimeException("File \"" + trainingsDocRoot.getAbsolutePath() + "\""
                    + " is not a folder.");
        }
        File localTrainingsDocFile = null;
        if (dep == null) {
            if (bp == null) {
                localTrainingsDocFile = new File(trainingsDocRoot, "/current/" + FILE_NAME_TRAININGS_LIST);
            } else {
                localTrainingsDocFile = new File(trainingsDocRoot, "/history/" + bp.getIdString() + "/"
                        + FILE_NAME_TRAININGS_LIST);
            }
        } else {
            if (this.getMasterData().getClubs().size() == 1) {
                if (bp == null) {
                    localTrainingsDocFile = new File(trainingsDocRoot.getAbsolutePath() + "/current/" + dep.getName()
                            + "/" + FILE_NAME_TRAININGS_LIST);
                } else {
                    localTrainingsDocFile = new File(trainingsDocRoot.getAbsolutePath() + "/history/"
                            + bp.getIdString() + "/" + dep.getName() + "/" + FILE_NAME_TRAININGS_LIST);
                }
            } else {
                if (bp == null) {
                    localTrainingsDocFile = new File(trainingsDocRoot.getAbsolutePath() + "/current/"
                            + ((Club) dep.getParentBean()).getName() + "/" + dep.getName() + "/"
                            + FILE_NAME_TRAININGS_LIST);
                } else {
                    localTrainingsDocFile = new File(trainingsDocRoot.getAbsolutePath() + "/history/"
                            + bp.getIdString() + "/" + ((Club) dep.getParentBean()).getName() + "/" + dep.getName()
                            + "/" + FILE_NAME_TRAININGS_LIST);
                }
            }
        }
        return localTrainingsDocFile;
    }

    /**
     * the one and only TrainerIconManager instance.
     */
    private TrainerIconManager trainerIcons = null;

    /**
     * @return the one and only TrainerIconManager instance
     */
    public TrainerIconManager getTrainerIcons() {
        return trainerIcons;
    }

    /**
     * the one and only WebFileManager instance.
     */
    private WebFileManager webFileManager = null;

    /**
     * @return the one and only WebFileManager instance
     */
    public WebFileManager getWebFileManager() {
        return webFileManager;
    }

    public static final String HEIDI = "v0e11igsinn10s";

    private void askForUserEmail() {
        final ClubadminUser user = (ClubadminUser) this.getAuthenticatedUser();
        final RapidBeansLocale locale = this.getCurrentLocale();
        if (user != null && (user.getEmail() == null || user.getEmail().length() == 0)) {
            this.messageInfo(locale.getStringMessage("info.user.pleasenteremail"));
            new OpenMyUserAccount().execute();
        }
    }

    /**
     * Migrate old GUI settings to new GUI Swing settings.
     */
    @SuppressWarnings("unchecked")
    public Document getSettingsDoc() {
        final Document settingsDoc = super.getSettingsDoc();
        final Settings settings = (Settings) settingsDoc.getRoot();
        final SettingsBasicGui originalGuiSettings = settings.getBasic().getGui();
        if (!(originalGuiSettings instanceof SettingsBasicGuiSwing)) {
            final SettingsBasicGuiSwing swingGuiSettings = new SettingsBasicGuiSwing();
            for (Property prop : originalGuiSettings.getPropertyList()) {
                if (prop instanceof PropertyAssociationend) {
                    final PropertyAssociationend pa = (PropertyAssociationend) prop;
                    if (pa.getValue() != null) {
                        final RapidBean composite = ((List<RapidBean>) pa.getValue()).get(0);
                        final RapidBean clone = composite.clone();
                        clone.setContainer(null);
                        clone.setParentBean(null);
                        swingGuiSettings.setPropValue(prop.getName(), clone);
                    }
                } else {
                    swingGuiSettings.setPropValue(prop.getName(), prop.getValue());
                }
            }
            settings.getBasic().setGui(swingGuiSettings);
        }
        if (settings.getBasic().getDefaultencoding() != CharsetsAvailable.UTF_8) {
            try {
                ThreadLocalValidationSettings.validationOff();
                settings.getBasic().setDefaultencoding(CharsetsAvailable.UTF_8);
            } finally {
                ThreadLocalValidationSettings.remove();
            }
        }
        if (settings.getBasic().getDefaultencodingusage() != DefaultEncodingUsage.write) {
            try {
                ThreadLocalValidationSettings.validationOff();
                settings.getBasic().setDefaultencodingusage(DefaultEncodingUsage.write);
            } finally {
                ThreadLocalValidationSettings.remove();
            }
        }
        return settingsDoc;
    }

    /**
     * Save a certain document using the WebFileManager.
     * 
     * @param doc
     *            the document to save
     */
    public void save(final Document doc) {
        final String defaultEncoding = getSettings().getBasic().getDefaultencoding().name();
        boolean forceEncoding = (getSettings().getBasic().getDefaultencodingusage() == DefaultEncodingUsage.write);
        if (getRunMode() == DataFileType.web && this.getSettingsRapidClubAdmin().getShareiconsoverweb()
                && (!this.getTestMode())) {
            WebFileManager webFile = getWebFileManager();
            webFile.upload(doc, defaultEncoding, forceEncoding);
        } else {
            doc.save();
        }
        doc.fireDocumentSaved();
    }
}
