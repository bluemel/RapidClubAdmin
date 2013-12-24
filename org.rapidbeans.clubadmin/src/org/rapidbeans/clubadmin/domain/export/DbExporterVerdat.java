/*
 * RapidBeans Application RapidClubAdmin: DbExporterVerdat.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 18.07.2008
 */
package org.rapidbeans.clubadmin.domain.export;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Logger;

import org.rapidbeans.clubadmin.domain.RapidClubAdminBusinessLogicException;
import org.rapidbeans.clubadmin.domain.SalaryComponentType;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.core.util.FileHelper;
import org.rapidbeans.domain.math.UnitTime;


/**
 * DB export business logic. Exports to MS Access VERDAT.mdb.
 * 
 * @author Martin Bluemel
 */
public final class DbExporterVerdat {

    // properties

    private File toFile = null;

    // internal attributes
    private ExportJob exportJob = null;

    private RapidBeansLocale locale = null;

    private NumberFormat nf = null;

    private DateFormat df = null;

    private Connection con = null;

    private static final Logger log = Logger.getLogger(
            DbExporterVerdat.class.getName()); 

    public DbExporterVerdat(final ExportJob job) {
        try {
            this.exportJob = job;
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            this.locale = new RapidBeansLocale("de");
            this.locale.init("org.rapidbeans.clubadmin");
            this.locale.setLocale(Locale.GERMAN);
            nf = NumberFormat.getInstance(Locale.ENGLISH);
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(2);
            nf.setGroupingUsed(false);
            df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void export() {
        if (this.toFile == null) {
            throw new RapidClubAdminBusinessLogicException(
                    "export.failure.exportfilenotdefined", "Target file for export is not defined.");
        }
        if (!this.toFile.exists()) {
            throw new RapidClubAdminBusinessLogicException(
                    "export.failure.exportfilenotexistent", "Target file for export does not exist.");
        }
        FileHelper.backup(this.toFile);
        if (exportJob.getBillingPeriod().getDateExportFirst() == null) {
            if (exportJob.getBillingPeriod().getDateClosing() == null) {
                exportJob.getBillingPeriod().setDateClosing(new java.util.Date());
            }
            exportJob.getBillingPeriod().setDateExportFirst(
                    exportJob.getBillingPeriod().getDateClosing());
        }
        try {
            con = this.getConnection();
            for (Trainer trainer : exportJob.getTrainers()) {
                String uelId = insertUebungsleiterIfNotInserted(trainer);
                for (ExportJobEntry entry : exportJob.getExportEntries(trainer)) {
                    log.fine("inserting entry: "
                            + entry.getTrainer().getIdString() + ", "
                            + entry.getSalaryComponentType().getName() + ": "
                            + entry.getHeldTrainerHours().toStringGui(locale, 2, 2) + ", "
                            + entry.getEarnedMoney().toStringGui(locale, 2, 2));
                    final String uaId = insertUelAbrechnungIfNotInserted(trainer, uelId, entry);
                    final String ubId = insertUelBerichIfNotInserted(trainer, uelId, entry);
                    insertUelStundenIfNotInserted(trainer, uaId, ubId, entry);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * An Uebungleiter is externally identified
     * 1) by it's external system ID if this is set
     * 2) by "<firstname> <lastname>" otherwise
     *
     * @param trainer the Trainer (Uebungleiter)
     *
     * @return the ID of the Uebungsleiter inserted
     *
     * @throws SQLException
     * @throws InterruptedException
     */
    private String insertUebungsleiterIfNotInserted(final Trainer trainer)
    throws SQLException, InterruptedException {
        String uelId = this.findUelId(trainer);
        if (uelId != null) {
            log.fine("Übungsleiter for Trainer \""
                    + trainer.getIdString() + "\" already exists.");
            return uelId;
        }

        log.fine("Inserting Übungleiter for Trainer \""
                + trainer.getIdString() + "\"...");
        final Statement statement = con.createStatement();
        String trainername = trainer.getLastname();
        if (trainername == null) {
            throw new RuntimeException("ERROR during inserting trainer \""
                    + trainer.getIdString() + " to MS Acces:\n"
                    + "Trainer has no \"last name\" specified.");
        }
        if (trainer.getFirstname() != null) {
            trainername = trainer.getFirstname() + " " + trainername;
        }
        String sql = "INSERT INTO Übungsleiter"
            + " (UName";
        if (trainer.getEmail() != null) {
            sql += ", UEmail";
        }
        sql += ") VALUES ('" + trainername + "'";
        if (trainer.getEmail() != null) {
            sql += ", '" + trainer.getEmail() + "'";
        }
        sql += ");";

        if (statement.executeUpdate(sql) != 1) {
            throw new RuntimeException("ERROR during insert trainer \""
                    + trainer.getIdString() + " to Übungsleiter");
        }

        // This seems to take a while, MS Access is no D-Zug
        while (uelId == null) {
            uelId = this.findUelId(trainer);
            if (uelId == null) {
                Thread.sleep(100);
            }
        }

        ExportJobResultEntry resultEntry = new ExportJobResultEntry(
                ExportJobResultEntryModificationType.create,
                "Übungsleiter", uelId);
        resultEntry.addAttribute("UName", trainername);
        resultEntry.addAttribute("UEmail", trainer.getEmail());
        exportJob.addResultEntry(resultEntry);

        return uelId;
    }

    private static final String abrechnungsStatus = "1";
    private static final String abrechnungsKostenstellenID = "01810";
    private static final String abrechnungsFibukonto = "1000";
    private static final String abrechnungsFibuGKonto = "3000";
    private static final String abrechnungsWaehrung = "EUR";
    private static final String abrechnungsWaehrungsKennziffer = "1";
    private static final String abrechnungsMwstID = "0";
    private static final String abrechnungsMwst = "0";
    private static final String abrechnungsMwstBetrag = "0";
    private static final String abrechnungsGebucht = "0";

    private String insertUelAbrechnungIfNotInserted(final Trainer trainer,
            final String uelId, final ExportJobEntry entry)
    throws SQLException, InterruptedException {
        String uelAbrechnungId = this.findUelAbrechnungId(uelId);
        if (uelAbrechnungId != null) {
            log.fine("Übungleiter Abrechnung for Trainer \""
                    + trainer.getIdString() + "\" on date \""
                    + exportJob.getDateFirstExportString() + "\" already exists.");
            return uelAbrechnungId;
        }
        log.fine("Inserting Übungleiter Abrechnung for Trainer \""
                + trainer.getIdString() + "\" on date \""
                + exportJob.getDateFirstExportString() + "\"...");
        final Statement statement = con.createStatement();
        String sql = "INSERT INTO ÜbungsleiterAbrechnung"
            + " (UID, UARechnungID, UARechnungNR, UADatum, Status"
            + ", KostenstelleID, FIBUKONTO, FIBUGKONTO, DM, WährungKZ"
            + ", MwstID, Mwst, MwstBetrag, GBetrag, Gebucht, DTAText"
            + ") VALUES ("
            + uelId
            + ", " + getAbrechnungsId(uelId)
            + ", '" + getAbrechnungsNummer(uelId) + "'"
            + ", " + getAbrechnungsDatum()
            + ", " + abrechnungsStatus
            + ", '" + abrechnungsKostenstellenID + "'"
            + ", " + abrechnungsFibukonto
            + ", " + abrechnungsFibuGKonto
            + ", '" + abrechnungsWaehrung + "'"
            + ", " + abrechnungsWaehrungsKennziffer
            + ", " + abrechnungsMwstID
            + ", " + abrechnungsMwst
            + ", " + abrechnungsMwstBetrag
            + ", " + getAbrechnungsGBetrag(trainer)
            + ", " + abrechnungsGebucht
            + ", '" + this.getAbrechnungsDtaText() + "'"
            + ");";
        log.fine("update SQL: " + sql);
        if (statement.executeUpdate(sql) != 1) {
            throw new RuntimeException("ERROR during Inserting"
                    + " ÜbungsleiterAbrechnung for Trainer \""
                    + trainer.getIdString() + "\" and date \""
                    + exportJob.getDateFirstExportString() + "\"");
        }

        // This seems to take a while, MS Access is no D-Zug
        while (uelAbrechnungId == null) {
            uelAbrechnungId = this.findUelAbrechnungId(uelId);
            if (uelAbrechnungId == null) {
                Thread.sleep(100);
            }
        }

        ExportJobResultEntry resultEntry = new ExportJobResultEntry(
                ExportJobResultEntryModificationType.create,
                "ÜbungsleiterAbrechnung", uelAbrechnungId);
        resultEntry.addAttribute("UID", uelId);
        resultEntry.addAttribute("UARechnungID", getAbrechnungsId(uelId));
        resultEntry.addAttribute("UARechnungNR", getAbrechnungsNummer(uelId));
        resultEntry.addAttribute("UADatum", df.format(exportJob.getDateFirstExport()));
        resultEntry.addAttribute("GBetrag", getAbrechnungsGBetrag(trainer));
        resultEntry.addAttribute("DM", abrechnungsWaehrung);
        resultEntry.addAttribute("DTAText", this.getAbrechnungsDtaText());
        
        exportJob.addResultEntry(resultEntry);

        return uelAbrechnungId;
    }

    private String insertUelBerichIfNotInserted(final Trainer trainer,
            final String uelId, final ExportJobEntry entry)
    throws SQLException, InterruptedException {
        String uelBereichId = this.findUelBereichId(entry, uelId);
        final String bezeichnung = this.getUelBereichBezeichnung(entry);
        if (uelBereichId != null) {
            log.fine("ÜbungleiterBereich for Trainer \""
                    + trainer.getIdString() + "\" and for SalaryComponentType \""
                    + bezeichnung + "\" already exists.");
            return uelBereichId;
        }

        log.fine("Inserting ÜbungleiterBereich for Trainer \""
                + trainer.getIdString() + "\" and for SalaryComponentType \""
                + bezeichnung + "\"...");
        final Statement statement = con.createStatement();
        String sql = "INSERT INTO ÜbungsleiterBereich"
            + " (UID, Bezeichnung, Einheit, Preis) VALUES ("
            + uelId
            + ", '" + bezeichnung + "'"
            + ", '" + getUelStundenEinheit(entry) + "'"
            + ", " + getUelStundenPreis(entry)
            + ");";
        log.fine("update SQL: " + sql);
        if (statement.executeUpdate(sql) != 1) {
            throw new RuntimeException("ERROR during Inserting"
                    + " ÜbungsleiterBereich for Trainer \""
                    + trainer.getIdString() + "\" and for SalaryComponentType \""
                    + bezeichnung + "\"");
        }

        // This seems to take a while, MS Access is no D-Zug
        while (uelBereichId == null) {
            uelBereichId = this.findUelBereichId(entry, uelId);
            if (uelBereichId == null) {
                Thread.sleep(100);
            }
        }

        ExportJobResultEntry resultEntry = new ExportJobResultEntry(
                ExportJobResultEntryModificationType.create,
                "�bungsleiterBereich", uelBereichId);
        resultEntry.addAttribute("UID", uelId);
        resultEntry.addAttribute("Bezeichnung", bezeichnung);
        resultEntry.addAttribute("Einheit", getUelStundenEinheit(entry));
        resultEntry.addAttribute("Preis", getUelStundenPreis(entry));
        exportJob.addResultEntry(resultEntry);

        return uelBereichId;
    }

    private String insertUelStundenIfNotInserted(final Trainer trainer,
            final String uaId, final String ubId, final ExportJobEntry entry)
    throws SQLException, InterruptedException {
        String uelStundenId = this.findUelStundenId(uaId, ubId, this.getUelBereichBezeichnung(entry));
        if (uelStundenId != null) {
            log.fine("ÜbungsleiterStunden for Abrechnung \""
                    + uaId + "\" and Uebungsleiter \""
                    + ubId + "\" already exists.");
            return uelStundenId;
        }

        log.fine("Inserting ÜbungsleiterStunden for Trainer \""
                + trainer.getIdString() + "\" with name \""
                + this.getUelBereichBezeichnung(entry) + "\"...");
        final Statement statement = con.createStatement();
        final String menge = nf.format(entry.getHeldTrainerHours().getMagnitudeDouble());
        final SalaryComponentType scType = entry.getSalaryComponentType();
        final String preis = nf.format(entry.getSalary().getComponent(scType).getMoney().getMagnitudeDouble());
        final String betrag = nf.format(entry.getEarnedMoney().getMagnitudeDouble());
        final String bezeichnung = this.getUelBereichBezeichnung(entry);

        final String sql = "INSERT INTO �bungsleiterStunden"
            + " (UAID, UBID, USDatum, USMenge, USPreis"
            + ", USBetrag, USBezeichnung, USEinheit) VALUES ("
            + uaId + ", " + ubId + ", "
            + "'" + df.format(exportJob.getDateFirstExport()) + "'"
            + ", " + menge + ", " + preis + ", " + betrag
            + ", '" + bezeichnung + "'"
            + ", '" + getUelStundenEinheit(entry) + "');";
        log.fine("update SQL: " + sql);
        if (statement.executeUpdate(sql) != 1) {
            throw new RuntimeException("ERROR during Inserting"
                    + " ÜbungsleiterStunden for Trainer \""
                    + trainer.getIdString() + "\" with name \""
                    + bezeichnung + "\"");
        }

        // This seems to take a while, MS Access is no D-Zug
        while (uelStundenId == null) {
            uelStundenId = this.findUelStundenId(uaId, ubId, bezeichnung);
            if (uelStundenId == null) {
                Thread.sleep(100);
            }
        }

        ExportJobResultEntry resultEntry = new ExportJobResultEntry(
                ExportJobResultEntryModificationType.create,
                "�bungsleiterStunden", uelStundenId);

        resultEntry.addAttribute("UAID", uaId);
        resultEntry.addAttribute("UBID", ubId);
        resultEntry.addAttribute("USDatum", df.format(exportJob.getDateFirstExport()));
        resultEntry.addAttribute("USMenge", menge);
        resultEntry.addAttribute("USPreis", preis);
        resultEntry.addAttribute("USBetrag", betrag);
        resultEntry.addAttribute("USBezeichnung", bezeichnung);
        resultEntry.addAttribute("USEinheit", getUelStundenEinheit(entry));
        exportJob.addResultEntry(resultEntry);

        return uelStundenId;
    }

    private String findUelId(final Trainer trainer) throws SQLException {
        String uelId = null;
        final Statement statement = con.createStatement();
        final String query = "SELECT UId FROM �bungsleiter WHERE UName = '"
            + trainer.getFirstname() + " " + trainer.getLastname() + "';";
        log.fine("query = \"" + query + "\"");
        if (!statement.execute(query)) {
            throw new RuntimeException("ERROR during select Uid from �bungsleiter");
        }
        final ResultSet resultSet = statement.getResultSet();
        if  (!resultSet.next()) {
            log.fine("Übungsleiter Id of Trainer \""
                    + trainer.getIdString() + "\": not found");
        } else {
            uelId = resultSet.getString(1);
            log.fine("Found �bungsleiter Id from Trainer \"" + trainer.getIdString()
                    + "\": " + uelId);
        }
        return uelId;
    }

    private String findUelAbrechnungId(final String uelId) {
        String uelAbrechnungId = null;
        try {
            final Statement statement = con.createStatement();
            String query = "SELECT UAID FROM �bungsleiterAbrechnung"
                + " WHERE UID = " + uelId
                + " AND UADatum = " + this.getAbrechnungsDatum() + ";";
            log.fine("query = \"" + query + "\"");
            if (!statement.execute(query)) {
                throw new RuntimeException("ERROR during select Uid from �bungsleiter");
            }
            final ResultSet resultSet = statement.getResultSet();
            if (!resultSet.next()) {
                log.fine("ÜbungsleiterAbrechnung Id for Uebungleiter \""
                        + uelId + "\" with date = "
                        + exportJob.getDateFirstExportString() + ": not found");
            } else {
                uelAbrechnungId = resultSet.getString(1);
            }
            return uelAbrechnungId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find an ÜbungsleiterBereich identified uniquely by the
     * Übungsleiter's ID and its Bezeichnung.
     * 
     * @param entry the ExportJobEntry defining the earned money
     * @param uelId the Übungsleiter ID
     *
     * @return the String define�ng the unique ID of an
     *         ÜbungsleiterBereich or null if not found.
     */
    private String findUelBereichId(final ExportJobEntry entry, final String uelId) {
        String uelBereichId = null;
        try {
            final Statement statement = con.createStatement();
            final String bezeichnung = this.getUelBereichBezeichnung(entry);

            String query = "SELECT UBID FROM ÜbungsleiterBereich"
                + " WHERE UID = " + uelId
                + " AND Bezeichnung = '" + bezeichnung + "';";
            log.fine("query = \"" + query + "\"");
            if (!statement.execute(query)) {
                throw new RuntimeException("ERROR during select UBID from ÜbungsleiterBereich");
            }
            final ResultSet resultSet = statement.getResultSet();
            if (!resultSet.next()) {
                log.fine("Übungsleiter Bereich for Uebungleiter \""
                        + uelId + "\" and with Bezeichnung \"" + bezeichnung
                        + "\": not found");
            } else {
                uelBereichId = resultSet.getString(1);
            }
            return uelBereichId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String findUelStundenId(
            final String uaId, final String ubId, final String bez) {
        String uelStundenId = null;
        try {
            final Statement statement = con.createStatement();
            String query = "SELECT USID FROM ÜbungsleiterStunden"
                + " WHERE UAID = " + uaId
                + " AND UBID = " + ubId
                + " AND USBezeichnung = '" + bez
                + "';";
            log.fine("query = \"" + query + "\"");
            if (!statement.execute(query)) {
                throw new RuntimeException("ERROR during select USID from ÜbungsleiterStunden");
            }
            final ResultSet resultSet = statement.getResultSet();
            if (!resultSet.next()) {
                log.fine("Übungsleiter Stunden for UebungleiterAbrechnung \""
                        + uaId + "\" and for ÜbungsleiterBereich \""
                        + ubId + "\" and for Bezeichnung: \""
                        + bez + "\" not found");
            } else {
                uelStundenId = resultSet.getString(1);
            }
            return uelStundenId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAbrechnungsNummer(final String uelId) {
        final Calendar cal = new GregorianCalendar();
        cal.setTime(exportJob.getDateFirstExport());
        final StringBuffer sb = new StringBuffer();
        sb.append(getYear(cal));
        sb.append('-');
        sb.append(getMonth(cal));
        sb.append('-');
        sb.append(formatUelId(uelId));
        return sb.toString();
    }

    private String getAbrechnungsId(final String uelId) {
        final Calendar cal = new GregorianCalendar();
        cal.setTime(exportJob.getDateFirstExport());
        final StringBuffer sb = new StringBuffer();
        sb.append(getYear(cal));
        sb.append(getMonth(cal));
        sb.append(formatUelId(uelId));
        return sb.toString();
    }

    private static String getYear(Calendar cal) {
        return Integer.toString(cal.get(Calendar.YEAR));
    }

    private static String getMonth(Calendar cal) {
        return StringHelper.fillUp(Integer.toString(cal.get(Calendar.MONTH) + 1), 2, '0', StringHelper.FillMode.left);
    }

    private static String formatUelId(final String uelId) {
        return StringHelper.fillUp(uelId, 3, '0', StringHelper.FillMode.left);
    }

    private String getAbrechnungsDatum() {
        return Long.toString(exportJob.getDateFirstExport().getTime() / (86400000) + 25570);
    }

    private String getAbrechnungsGBetrag(final Trainer trainer) {
        return nf.format(exportJob.getOverallEarnedMoney(trainer).getMagnitudeDouble());
    }

    private String getAbrechnungsDtaText() {        
        return "Honorar "
            + toMonthYearNrString(this.exportJob.getBillingPeriod().getFrom())
            + " - "
            + toMonthYearNrString(this.exportJob.getBillingPeriod().getTo());
    }

    private String getUelBereichBezeichnung(final ExportJobEntry entry) {
//        return entry.getSalaryComponentType().getName();
        return entry.getSalary().getComponent(entry.getSalaryComponentType()).getDescription();
    }

    private String getUelStundenEinheit(final ExportJobEntry entry) {
        String einheit = null;
        final int minutes = (int) entry.getSalary().getTime().convert(UnitTime.min).getMagnitudeLong();
        switch (minutes) {
        case 15: einheit = "15 Min."; break;
        case 30: einheit = "0.5 Std."; break;
        case 45: einheit = "� Std."; break;
        case 60: einheit = "1 Std."; break;
        default: einheit = Integer.toString(minutes) + " " + "Min."; break;
        }
        return einheit;
    }

    private String getUelStundenPreis(final ExportJobEntry entry) {
        return nf.format(entry.getSalary().getComponent(entry.getSalaryComponentType()).getMoney().getMagnitudeDouble());
    }

    /**
     * @param toFile the toFile to set
     */
    public void setToFile(File toFile) {
        this.toFile = toFile;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ="
                + this.toFile.getPath() + ";DriverID=22;READONLY=true}", "", "");
    }

    private String toMonthYearNrString(final Date date) {
        final StringBuffer sb = new StringBuffer();
        final Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        sb.append(StringHelper.fillUp(Integer.toString(cal.get(Calendar.MONTH)), 2, '0', StringHelper.FillMode.left));
        sb.append(".");
        sb.append(Integer.toString(cal.get(Calendar.YEAR)));
        return sb.toString();
    }

    public static String reportResult(final ExportJob exportJob) {
        final StringBuffer sb = new StringBuffer();
        sb.append("Neu angelegte Eintr�ge:");
        sb.append(PlatformHelper.getLineFeed());
        sb.append(PlatformHelper.getLineFeed());
        sb.append("  Übungsleiter:");
        sb.append(PlatformHelper.getLineFeed());
        int i = 1;
        for (ExportJobResultEntry entry : exportJob.getResultEntries(
                ExportJobResultEntryModificationType.create,
                "Übungsleiter")) {
            sb.append("    ");
            sb.append(StringHelper.fillUp(Integer.toString(i), 3, ' ', StringHelper.FillMode.left));
            sb.append(": UID: " + entry.getEntityId());
            appendAttrVal(sb, entry, "UName");
            sb.append(PlatformHelper.getLineFeed());
            i++;
        }
        sb.append(PlatformHelper.getLineFeed());
        sb.append("  ÜbungsleiterAbrechnung:");
        sb.append(PlatformHelper.getLineFeed());
        i = 1;
        for (ExportJobResultEntry entry : exportJob.getResultEntries(
                ExportJobResultEntryModificationType.create,
                "ÜbungsleiterAbrechnung")) {
            sb.append("    ");
            sb.append(StringHelper.fillUp(Integer.toString(i), 3, ' ', StringHelper.FillMode.left));
            sb.append(": UAID: " + entry.getEntityId());
            appendAttrVal(sb, entry, "UID");
            appendAttrVal(sb, entry, "UARechnungID");
            appendAttrVal(sb, entry, "UARechnungNR");
            appendAttrVal(sb, entry, "UADatum");
            appendAttrVal(sb, entry, "GBetrag");
            appendAttrVal(sb, entry, "DM");
            appendAttrVal(sb, entry, "DTAText");
            sb.append(PlatformHelper.getLineFeed());
            i++;
        }
        sb.append(PlatformHelper.getLineFeed());
        sb.append("  ÜbungsleiterBereich:");
        sb.append(PlatformHelper.getLineFeed());
        i = 1;
        for (ExportJobResultEntry entry : exportJob.getResultEntries(
                ExportJobResultEntryModificationType.create,
                "ÜbungsleiterBereich")) {
            sb.append("    ");
            sb.append(StringHelper.fillUp(Integer.toString(i), 3, ' ', StringHelper.FillMode.left));
            sb.append(": UBID: " + entry.getEntityId());
            appendAttrVal(sb, entry, "UID");
            appendAttrVal(sb, entry, "Bezeichnung");
            appendAttrVal(sb, entry, "Einheit");
            appendAttrVal(sb, entry, "Preis");
            sb.append(PlatformHelper.getLineFeed());
            i++;
        }
        sb.append(PlatformHelper.getLineFeed());
        sb.append("  ÜbungsleiterStunden:");
        sb.append(PlatformHelper.getLineFeed());
        i = 1;
        for (ExportJobResultEntry entry : exportJob.getResultEntries(
                ExportJobResultEntryModificationType.create,
                "ÜbungsleiterStunden")) {
            sb.append("    ");
            sb.append(StringHelper.fillUp(Integer.toString(i), 3, ' ', StringHelper.FillMode.left));
            sb.append(": USID: " + entry.getEntityId());
            appendAttrVal(sb, entry, "UAID");
            appendAttrVal(sb, entry, "UBID");
            appendAttrVal(sb, entry, "USDatum");
            appendAttrVal(sb, entry, "USMenge");
            appendAttrVal(sb, entry, "USPreis");
            appendAttrVal(sb, entry, "USBetrag");
            appendAttrVal(sb, entry, "USBezeichnung");
            appendAttrVal(sb, entry, "USEinheit");
            sb.append(PlatformHelper.getLineFeed());
            i++;
        }
        return sb.toString();
    }

    private static void appendAttrVal(final StringBuffer sb,
            final ExportJobResultEntry entry, final String attrname) {
        sb.append(", ");
        sb.append(attrname);
        sb.append(": ");
        sb.append(entry.getAttributeValue(attrname));
    }
}
