/*
 * Rapid Beans Framework: RapidClubAdminClientIntegrationTest.java
 *
 * Copyright Martin Bluemel, 2006
 *
 * 26.08.2006
 */

package org.rapidbeans.clubadmin.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.clubadmin.presentation.swing.ViewOverview;
import org.rapidbeans.clubadmin.service.OpenCurrentTrainingsList;
import org.rapidbeans.clubadmin.service.Umlaut;
import org.rapidbeans.clubadmin.service.ViewOverviewAction;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.Toolbar;
import org.rapidbeans.presentation.ToolbarButton;
import org.rapidbeans.presentation.config.ConfigApplication;
import org.rapidbeans.presentation.swing.ToolbarButtonSwing;
import org.rapidbeans.presentation.swing.ToolbarSwing;
import org.rapidbeans.service.ActionArgument;

/**
 * UI integration tests.
 * 
 * @author Martin Bluemel
 */
public class RapidClubAdminClientIntegrationTest {

	private static final boolean TEST_MODE = true;

	private static final DateFormat DF = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);

	private static final Properties OPTIONS = new Properties();
	static {
		OPTIONS.put("root", "src/test/resources/config/test01");
	}

	private static RapidClubAdminClient clientTrainer = null;

	private static int testMethodCount = -1;

	private static int testMethodIndex = 0;

	private int countTestMethods() {
		int count = 0;
		for (Method method : this.getClass().getMethods()) {
			if (method.getName().startsWith("test")) {
				count++;
			}
		}
		return count;
	}

	@BeforeClass
	public static void setUpClass() {
		if (!new File("src/test/resources/testsettings.xml").exists()) {
			FileHelper.copyFile(new File("src/test/resources/testsettingsTemplate.xml"),
					new File("src/test/resources/testsettings.xml"));
		}
		TypePropertyCollection.setDefaultCharSeparator(',');
		TypePropertyCollection.setDefaultCharEscape('\\');
	}

	@AfterClass
	public static void tearDownClass() {
		if (new File("src/test/resources/testsettings.xml").exists()) {
			new File("src/test/resources/testsettings.xml").delete();
		}
	}

	/**
	 * start the client.
	 */
	@Before
	public void setUp() {
		if (clientTrainer == null) {
			TypePropertyCollection.setDefaultCharSeparator(',');
			clientTrainer = new ApplicationMock();
			ApplicationMock.setAuthnRoleType("org.rapidbeans.clubadmin.domain.Role");
			clientTrainer.logon("jogi", "");
			ApplicationManager.start(clientTrainer);
			assertNotNull(clientTrainer.getAuthenticatedUser());
		}
		if (testMethodCount == -1) {
			testMethodCount = this.countTestMethods();
		}
	}

	/**
	 * end the client.
	 */
	@After
	public void tearDown() {
		testMethodIndex++;
		if (testMethodIndex == testMethodCount) {
			ApplicationManager.resetApplication();
			clientTrainer = null;
		}
	}

	/**
	 * Date formatter.
	 */
	static final DateFormat DFDATE = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);

	/**
	 * - open the training's list's view "Overview" - select "Jogi" - and check if
	 * Jogi has earned 160 bugs so far.
	 * 
	 * @throws ParseException if parsing of a date fails
	 */
	@Test
	public void testOpenTrainingsList() throws ParseException {
		OpenCurrentTrainingsList openAction = new OpenCurrentTrainingsList();
		ActionArgument arg = new ActionArgument();
		arg.setName("department");
		arg.setValue("FC Hintertupfingen/Fu" + Umlaut.SUML + "ball");
		openAction.addArgument(arg);
		openAction.execute();
		Document doc = clientTrainer.getActiveDocument();
		TrainingsList list = (TrainingsList) doc.getRoot();
		assertEquals(DF.parse("01.01.2010"), list.getFrom());
		assertEquals(DF.parse("31.03.2010"), list.getTo());
		assertEquals("FC Hintertupfingen", list.getClubs().iterator().next().getIdString());
		new ViewOverviewAction().execute();
		ViewOverview view = (ViewOverview) clientTrainer
				.getView("currentTrainings_FC Hintertupfingen/Fu" + Umlaut.SUML + "ball.overview");
		view.getTrainersList().setSelectedIndex(1);
		assertTrue(view.getText().contains("160,00 EUR"));
	}

	/**
	 * Verify that opening master data is not possible for Trainer "jogi".
	 */
	@Test
	public void testMastardataDisabledForJogi() throws ParseException {
		ToolbarSwing settingsToolbar = null;
		for (final Toolbar toolbar : clientTrainer.getMainwindow().getToolbars()) {
			if (toolbar.getName().equals("settings")) {
				settingsToolbar = (ToolbarSwing) toolbar;
			}
		}
		ToolbarButtonSwing masterdataButton = null;
		ToolbarButtonSwing myuseraccountButton = null;
		ToolbarButtonSwing settingsButton = null;
		for (final ToolbarButton button : settingsToolbar.getButtons()) {
			if (button.getName().equals("masterdata")) {
				masterdataButton = (ToolbarButtonSwing) button;
			} else if (button.getName().equals("myuseraccount")) {
				myuseraccountButton = (ToolbarButtonSwing) button;
			} else if (button.getName().equals("settings")) {
				settingsButton = (ToolbarButtonSwing) button;
			}
		}
		assertNotNull(settingsButton);
		assertNotNull(myuseraccountButton);
		assertNull(masterdataButton);
	}

	private class ApplicationMock extends RapidClubAdminClient {

		@Override
		public boolean getTestMode() {
			return TEST_MODE;
		}

		@Override
		public void start() {
			super.start();
			RapidBeansLocale locale = new LocaleMock();
			locale.setName("de");
			locale.setLocale(new Locale("de"));
			locale.init(this);
			this.setCurrentLocale(locale);
		}

		private Settings settings = null;

		@Override
		public RapidClubAdminSettings getSettingsRapidClubAdmin() {
			if (this.settings == null) {
				this.settings = (Settings) new Document(new File("src/test/resources/testsettings.xml")).getRoot();
			}
			return settings.getSettings();
		}

		private Document settingsDoc = null;

		@Override
		public Document getSettingsDoc() {
			if (this.settingsDoc == null) {
				this.settingsDoc = new Document(new File("src/test/resources/testsettings.xml"));
			}
			return this.settingsDoc;
		}

		private Document configDoc = null;

		@Override
		public ConfigApplication getConfiguration() {
			boolean init = false;
			if (this.configDoc == null) {
				this.configDoc = new Document(new File("src/main/resources/org/rapidbeans/clubadmin/Application.xml"));
				init = true;
			}
			final ConfigApplication config = (ConfigApplication) this.configDoc.getRoot();
			if (init) {
				super.setConfiguration(config);
			}
			return config;
		}

		@Override
		public void setConfiguration(final ConfigApplication config) {
			if (config == null) {
				this.configDoc = null;
			} else {
				this.configDoc = (Document) config.getContainer();
			}
			super.setConfiguration(config);
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
					this.masterDoc = new Document(new File("src/test/resources/config/test01/data/masterdata.xml"));
				} finally {
					initializingMasterData = false;
				}
			}
			return this.masterDoc;
		}

		/**
		 * setter for unit testing reasons.
		 * 
		 * @param doc the masterdata document
		 */
		@Override
		public void setMasterDoc(final Document doc) {
			this.masterDoc = doc;
			super.setMasterDoc(doc);
		}

		@Override
		public String getRootpackage() {
			return "org.rapidbeans.clubadmin";
		}

		@Override
		public Properties getOptions() {
			return OPTIONS;
		}
	}

	private class LocaleMock extends RapidBeansLocale {
	}
}
