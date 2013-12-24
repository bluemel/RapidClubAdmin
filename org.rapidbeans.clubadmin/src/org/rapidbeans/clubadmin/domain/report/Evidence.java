/*
 * RapidBeans Application RapidClubAdmin: Overview.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 23.08.2007
 */
package org.rapidbeans.clubadmin.domain.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.rapidbeans.clubadmin.RapidClubAdmin;
import org.rapidbeans.clubadmin.domain.Club;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.RapidClubAdminBusinessLogicException;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.Training;
import org.rapidbeans.clubadmin.domain.TrainingDate;
import org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer;
import org.rapidbeans.clubadmin.domain.TrainingRegular;
import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.basic.PropertyDate;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.exception.PropValueNullException;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.domain.math.Time;
import org.rapidbeans.domain.math.UnitTime;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.settings.SettingsAll;

/**
 * Report business logic.
 * Overview of held trainings and trainer hours for one trainer.
 *
 * @author Martin Bluemel
 */
public final class Evidence {

    private static final Logger log = Logger.getLogger(
            Evidence.class.getName()); 

    /**
     * Print a Trainer trainings evidence for
     * a given trainer and a given set of departements.
     *
     * @param evidenceFile the report output file
     * @param templateFile the report template file
     * @param departments the Departments
     * @param trainer the trainer
     * @param singleSheets if true single formulars will be printed,
     *                     if false coherent sheets will be printed
     */
    public static void printReportEvidence(
            final File evidenceFile,
            final File templateFile,
            final Department[] departments,
            final String trainerId,
            final boolean singleSheets) {

        try {
            final FileWriter out = new FileWriter(evidenceFile);
            final Template template = readTemplate(templateFile);
            out.write(template.getHeader());

            printReportSheets(out, template, departments, trainerId,
                    singleSheets);

            out.write(template.getFooter());
            out.close();
        } catch (IOException e) {
            throw new RapidBeansRuntimeException(e);
        }
    }

    /**
     * Print a Trainer trainings evidence for
     * a given trainer and a given set of departements.
     *
     * @param evidenceFile the report output file
     * @param templateFile the report template file
     * @param departments the Departments
     * @param trainer the trainer
     * @param singleSheets if true single formulars will be printed,
     *                     if false coherent sheets will be printed
     */
    public static void printReportEvidence(
            final File evidenceFile,
            final File templateFile,
            final Department[] departments,
            final boolean singleSheets) {

        try {
            final FileWriter out = new FileWriter(evidenceFile);
            final Template template = readTemplate(templateFile);
            out.write(template.getHeader());

            final Collection<Trainer> trainers = new TreeSet<Trainer>();
            for (Department dep : departments) {
                for (Trainer tr : dep.getTrainers()) {
                    if (tr.getTrainingsheld() != null
                            && tr.getTrainingsheld().size() > 0) {
                        trainers.add(tr);
                    }
                }
            }

            for (Trainer trainer : trainers) {
                printReportSheets(out, template, departments,
                        trainer, singleSheets);
            }

            out.write(template.getFooter());
            out.close();
        } catch (IOException e) {
            throw new RapidBeansRuntimeException(e);
        }
    }

    /**
     * Print report sheets for
     * a given trainer and a given set of departements.
     *
     * @param evidenceFile the report output file
     * @param templateFile the report template file
     * @param departments the Departments
     * @param trainerId the trainer's id
     * @param singleSheets if true single formulars will be printed,
     *                     if false coherent sheets will be printed
     */
    final static void printReportSheets(
            final FileWriter out,
            final Template template,
            final Department[] departments,
            final String trainerId,
            final boolean singleSheets) throws IOException {
        final Trainer trainer = (Trainer) departments[0].getContainer().findBeanByQuery(
                "org.rapidbeans.clubadmin.domain.Trainer[id = '" + trainerId + "']");
        printReportSheets(out, template, departments, trainer, singleSheets);
    }

    /**
     * Print report sheets for
     * a given trainer and a given set of departements.
     *
     * @param evidenceFile the report output file
     * @param templateFile the report template file
     * @param departments the Departments
     * @param trainer the trainer
     * @param singleSheets if true single formulars will be printed,
     *                     if false coherent sheets will be printed
     */
    final static void printReportSheets(
            final FileWriter out,
            final Template template,
            final Department[] departments,
            final Trainer trainer,
            final boolean singleSheets) throws IOException {
        final List<TrainingHeldByTrainer> allTrhbts = new ArrayList<TrainingHeldByTrainer>();

        for (Department dep : departments) {
            final List<TrainingHeldByTrainer> trhbts = Overview.findTrainigsHeld(
                    trainer, dep);
            for (TrainingHeldByTrainer trhbt : trhbts) {
                allTrhbts.add(trhbt);
            }
        }

        final int size = allTrhbts.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (isGreater((TrainingHeldByTrainer) allTrhbts.get(j),
                        (TrainingHeldByTrainer) allTrhbts.get(i))) {
                    final TrainingHeldByTrainer trhbt = (TrainingHeldByTrainer) allTrhbts.get(i);
                    allTrhbts.set(i, allTrhbts.get(j));
                    allTrhbts.set(j, trhbt);
                }
            }
        }

        final Club club = (Club) departments[0].getParentBean();
        generateSheets(out, template.getBody(), template.getRowcount(),
                club, null, trainer,
                allTrhbts, singleSheets);
    }

    /**
     * Helper for the bubble sort.
     *
     * @param tr1 the first TrainingHeldByTrainer
     * @param tr2 the second TrainingHeldByTrainer
     *
     * @return if the first is greater than the second
     */
    final static boolean isGreater(final TrainingHeldByTrainer tr1,
            final TrainingHeldByTrainer tr2) {
        boolean isGreater = false;
        final TrainingRegular trn1 = (TrainingRegular) tr1.getParentBean();
        final TrainingRegular trn2 = (TrainingRegular) tr2.getParentBean();
        switch (trn1.getDate().compareTo(trn2.getDate())) {
        case -1:
            isGreater = true;
            break;
        case 0:
            final TrainingDate trd1 = (TrainingDate) trn1.getParentBean();
            final TrainingDate trd2 = (TrainingDate) trn2.getParentBean();
            switch (trd1.getTimestart().compareTo(trd2.getTimestart())) {
            case -1:
                isGreater = true;
                break;
            default:
                break;
            }
            break;
        default:
            break;
        }
        return isGreater;
    }

    /**
     * Print a Trainer trainings evidence for
     * a given trainer and a given department.
     *
     * @param evidenceFile the report output file
     * @param templateFile the report template file
     * @param department the department
     * @param trainer the trainer
     * @param singleSheets if true single formulars will be printed,
     *                     if false coherent sheets will be printed
     */
    public static void printReportEvidence(
            final File evidenceFile,
            final File templateFile,
            final Department department,
            final Trainer trainer,
            final boolean singleSheets) {
        try {
            // query the TrainingHeldByTrainer objects out of the database
            final List<TrainingHeldByTrainer> allTrhbts =
                Overview.findTrainigsHeld(trainer, department);

            FileWriter out = new FileWriter(evidenceFile);
            final Template template = readTemplate(templateFile);
            out.write(template.getHeader());
            generateSheets(out, template.getBody(), template.getRowcount(),
                    (Club) department.getParentBean(), department, trainer,
                    allTrhbts, singleSheets);
            out.write(template.getFooter());
            out.close();
        } catch (IOException e) {
            throw new RapidBeansRuntimeException(e);
        }
    }

    /**
     * Generate single sheets.
     *
     * @param out
     * @param templateBody
     * @param rowCount
     * @param club
     * @param department
     * @param trainer
     * @param allTrhbts
     * @param singleSheets
     *
     * @param out the file writer to put the output to
     * @param templateBody the template body to fill
     * @param rowCount determines how many rows per sheet at maximum
     * @param club the Club
     * @param department the department for which the traine held trainings
     * @param trainer the Trainer
     * @param allTrhbts all the Trainings held by the Trainer
     * @param singleSheets if true single formulars will be printed,
     *                     if false coherent sheets will be printed
     *
     * @throws IOException if IO fails
     */
    private static void generateSheets(
            final FileWriter out,
            final String templateBody,
            final int rowCount,
            final Club club,
            final Department department,
            final Trainer trainer,
            final List<TrainingHeldByTrainer> allTrhbts,
            final boolean singleSheets) throws IOException {
        int n = 0;
        final int allTrhbtsSize = allTrhbts.size();
        int currentPageNo = 1;
        int pageCount = 1;
        if (allTrhbtsSize > rowCount) {
            pageCount = allTrhbtsSize / rowCount;
            if (allTrhbtsSize % rowCount > 0) {
                pageCount++;
            }
        }
        Time sumTrainerhours = new Time("0 s");
        while (n < allTrhbtsSize) {
            final List<TrainingHeldByTrainer> trhbts = new ArrayList<TrainingHeldByTrainer>();
            final int iStart = n;
            final int iEnd = iStart + rowCount;
            for (int i = iStart; i < iEnd && i < allTrhbtsSize; i++) {
                trhbts.add((TrainingHeldByTrainer) allTrhbts.get(i));
                n++;
            }
            sumTrainerhours = Evidence.generateEvidenceBody(
                    out, templateBody, club, department, trainer, trhbts, rowCount,
                    pageCount, currentPageNo, singleSheets, sumTrainerhours);
            if (currentPageNo < pageCount) {
                currentPageNo++;
            }
        }
    }

    /**
     * Fills the given report template with data.
     *
     * @param out the file writer to put the output to
     * @param rtfTemplate the report template to fills
     * @param club the Club
     * @param trainer the Trainer
     * @param department the department for which the traine held trainings
     * @param trhbts the Trainings held by the Trainer
     * @param pageCount the count of pages to print for
     *        one single Trainer combined with one single department.
     * @param singleSheets if true single formulars will be printed,
     *                     if false coherent sheets will be printed
     * @param uebertrag the "Uebertrag"
     *
     * @return the time summed up so far
     *
     * @throws IOException  i case of IO problems
     */
    private static Time generateEvidenceBody(
            final FileWriter out,
            final String rtfTemplate,
            final Club club,
            final Department department,
            final Trainer trainer,
            final List<TrainingHeldByTrainer> trhbts,
            final int rowCount, final int pageCount,
            final int currentPageNo, final boolean singleSheets,
            final Time uebertrag) throws IOException {

        final Map<String, String> map = new HashMap<String, String>();
        final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();
        final RapidBeansLocale locale = client.getCurrentLocale();
        final TrainingsList trList = (TrainingsList) trainer.getParentBean();
        map.put("@YEAR@", PropertyDate.formatDate(trList.getTo(), locale,
                DateFormat.MEDIUM, DateFormat.YEAR_FIELD));
        map.put("@TRAINER@", trainer.getFirstname() + " "
                + trainer.getLastname());
        if (department != null) {
            map.put("@CLUB@", club.getName()
                    + " (Abteilung: " + department.getName() + ")");
        } else {
            map.put("@CLUB@", club.getName());
        }
        if ((!singleSheets) || pageCount == 1) {
            map.put("@BS@", PropertyDate.formatDate(trList.getFrom(),
                    locale, DateFormat.MEDIUM, DateFormat.MONTH_FIELD)
                    + "." + PropertyDate.formatDate(trList.getFrom(),
                            locale, DateFormat.MEDIUM, DateFormat.YEAR_FIELD));
            map.put("@BE@", PropertyDate.formatDate(trList.getTo(),
                    locale, DateFormat.MEDIUM, DateFormat.MONTH_FIELD)
                    + "." + PropertyDate.formatDate(trList.getTo(),
                            locale, DateFormat.MEDIUM, DateFormat.YEAR_FIELD));
        } else {
            map.put("@BS@", PropertyDate.formatDate(
                    ((TrainingRegular) trhbts.get(0).getParentBean()).getDate(), locale));
            map.put("@BE@", PropertyDate.formatDate(
                    ((TrainingRegular) trhbts.get(trhbts.size() - 1).getParentBean()).getDate(), locale));
        }
        if ((!singleSheets) && (pageCount > 1)) {
            map.put("@PAGE@", "BLATT "
                    + Integer.toString(currentPageNo)
                    + " / " + Integer.toString(pageCount));
        } else {
            map.put("@PAGE@", "");
        }
        map.put("@MONATANFANG@", trList.getProperty("from").toStringGui(locale));
        map.put("@MONATENDE@", trList.getProperty("to").toStringGui(locale));
        int n = 1;
        Time sumHours = uebertrag;
        int partipiciantsCount = 0;
        String lineNo = null;
        final NumberFormat formatter2 = NumberFormat.getInstance(locale.getLocale());
        formatter2.setMinimumFractionDigits(2);
        formatter2.setMaximumFractionDigits(2);
        final Time trainerHour = (Time) trList.getTrainerhour().convert(UnitTime.min);

        for (TrainingHeldByTrainer trhbt : trhbts) {
            final Training training = (Training) trhbt.getParentBean();
            final Time hoursWorked = training.getTimeWorked(UnitTime.h);
            Time trainerhoursWorked = null;
            try {
                trainerhoursWorked = new Time(hoursWorked.convert(UnitTime.min).getMagnitude().divide(trainerHour.getMagnitude()), UnitTime.min);
            } catch (ArithmeticException e) {
                double d = (hoursWorked.convert(UnitTime.min).getMagnitude().doubleValue()) / (trainerHour.getMagnitude().doubleValue());
                trainerhoursWorked = new Time(new BigDecimal(d), UnitTime.min);             
            }
            switch (training.getState()) {
            case checked:
                sumHours = new Time(sumHours.getMagnitude().add(hoursWorked.getMagnitude()),
                        UnitTime.h);
                try {
                    partipiciantsCount = training.getPartipiciantscount();
                } catch (PropValueNullException e) {
                    partipiciantsCount = -1;
                }
                lineNo = StringHelper.fillUp(Integer.toString(n), 2, '0', StringHelper.FillMode.left);
                map.put("@01" + lineNo + "@", training.getProperty("date").toStringGui(locale));
                map.put("@02" + lineNo + "@",
                        training.getTimestart().toString() + " - "
                        + training.getTimeend().toString());
                map.put("@03" + lineNo + "@",
                        formatter2.format(trainerhoursWorked.getMagnitude()));
                String sport = "";
                if (training.getSport() != null) {
                    if (training.getSport().getShortname() != null
                            && training.getSport().getShortname().trim().length() > 0) {
                        sport = training.getSport().getShortname();
                    } else {
                        sport = training.getSport().getName();
                    }
                } else {
                    // take the Department's name as default for Sport
                    Department dep = null;
                    if (training instanceof TrainingRegular) {
                        dep = (Department) training.getParentBean().getParentBean();
                    } else {
                        dep = (Department) training.getParentBean();
                    }
                    if (dep != null) {
                        sport = dep.getName();
                    }
                }
                map.put("@04" + lineNo + "@", sport);
                if (partipiciantsCount == -1) {
                    map.put("@05" + lineNo + "@", "-");
                } else {
                    map.put("@05" + lineNo + "@", Integer.toString(partipiciantsCount));
                }
                if (training.getLocation() != null) {
                    if (training.getLocation().getStreet() != null
                            && (!training.getLocation().getStreet().equals(""))) {
                        map.put("@06" + lineNo + "@", training.getLocation().getStreet());
                    } else {
                        map.put("@06" + lineNo + "@", training.getLocation().getName());
                    }
                } else {
                    map.put("@06" + lineNo + "@", "");
                }
                n++;
                break;
            case cancelled:
            case closed:
            case asplanned:
            case modified:
            default:
                break;
            }
        }
        for (int i = n; i <= rowCount; i++) {
            lineNo = StringHelper.fillUp(Integer.toString(i), 2, '0', StringHelper.FillMode.left);
            map.put("@01" + lineNo + "@", "");
            map.put("@02" + lineNo + "@", "");
            map.put("@03" + lineNo + "@", "");
            map.put("@04" + lineNo + "@", "");
            map.put("@05" + lineNo + "@", "");
            map.put("@06" + lineNo + "@", "");
        }
        Time sumTrainerhoursWorked = null;
        try {
            sumTrainerhoursWorked = new Time(sumHours.convert(UnitTime.min).getMagnitude().divide(trainerHour.getMagnitude()), UnitTime.min);
        } catch (ArithmeticException e) {
            double d = (sumHours.convert(UnitTime.min).getMagnitude().doubleValue()) / (trainerHour.getMagnitude().doubleValue());
            sumTrainerhoursWorked = new Time(new BigDecimal(d), UnitTime.min);
        }
        if (singleSheets || (currentPageNo == pageCount)) {
            map.put("@TSUM@", "Summe");
            map.put("@F1@",
                    "Es wird bestätigt, daß die Eintragungen richtig sind.");
            map.put("@F2@", "Ismaning, der");
            map.put("@F3@", "Ort, Datum");
            map.put("@F4@", "______________________________");
            map.put("@F5@", "Unterschrift des Vereinsvorsitzenden");
            map.put("@F6@", "________________________");
            map.put("@F7@", "Unterschrift des Übungsleiters");
        } else {
            map.put("@TSUM@", "Übertrag");
            map.put("@F1@", "");
            map.put("@F2@", "");
            map.put("@F3@", "");
            map.put("@F4@", "");
            map.put("@F5@", "");
            map.put("@F6@", "");
            map.put("@F7@", "");
        }
        map.put("@SUM@", formatter2.format(sumTrainerhoursWorked.getMagnitude()));

        char c;
        int state = 0;
        int len = rtfTemplate.length();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < len; i++) {
            c = rtfTemplate.charAt(i);
            switch (state) {
            case 0:
                switch (c) {
                case '@':
                    buf.append(c);
                    state = 1;
                    break;
                default:
                    out.write(c);
                    break;
                }
                break;
            case 1:
                switch (c) {
                case '@':
                    buf.append(c);
                    final String key = buf.toString();
                    final String val = map.get(key);
                    if (val == null) {
                        throw new RapidClubAdminBusinessLogicException(
                                "evidence.rtftemplate.parser.invalid.key",
                                "No value found for key \"" + key + "\"");
                    }
                    out.write(val);
                    buf.setLength(0);
                    state = 0;
                    break;
                default:
                    buf.append(c);
                    if (buf.length() > 20) {
                        out.write(buf.toString());
                        buf.setLength(0);
                        state = 0;                        
                    }
                }
                break;
            default:
                throw new RapidClubAdminBusinessLogicException(
                        "evidence.rtftemplate.parser.invalid.state",
                        "invalid state " + state);
            }
        }

        if (singleSheets) {
            return new Time("0 s");
        }
        return sumHours;
    }

    /**
     * find the report template file in the settings dir and copy
     * it from res if it is not already there.
     *
     * @return the report template file.
     */
    public static File findEvidenceReportTemplateFile(final boolean keepLocalTemplateFile) {
        try {
            final Application client = ApplicationManager.getApplication();
            final RapidBeansLocale locale = client.getCurrentLocale();
            File templateFile = new File(
                    "res/org/rapidbeans/clubadmin/reports/"
                    + locale.getName() + "/templateNachweis.rtf");
            if (templateFile.exists()) {
                log.fine("exit 1: template file: \"" + templateFile.getAbsolutePath() + "\"");
                return templateFile;
            }

            templateFile = new File(SettingsAll.getDirname() + "/templateNachweis.rtf");
            log.fine("searching file \"" + templateFile.getAbsolutePath() + "\"...");
            if (templateFile.exists()) {
                if (keepLocalTemplateFile) {
                  log.fine("exit 2: template file: \"" + templateFile.getAbsolutePath() + "\"");
                  return templateFile;
                } else {
                    log.fine("deleting local template file");
                    templateFile.delete();
                }
            }

            log.fine("reading resource  \"org/rapidbeans/clubadmin/reports/"
                    + locale.getName() + "/templateNachweis.rtf\"...");
            InputStream is = RapidClubAdmin.class.getResourceAsStream("reports/"
                    + locale.getName() + "/templateNachweis.rtf");
            if (is == null) {
                throw new RuntimeException("file \"" + templateFile.getAbsolutePath() + "\" not found");
            }

            File userDir = new File(System.getProperty("user.home") + "/JClubMaster");
            if (!userDir.exists()) {
                log.fine("making directory \"" + userDir.getAbsolutePath() + "\"...");
                if (!userDir.mkdir())
                    throw new RuntimeException("Can't create directory \"" + userDir.getAbsolutePath() + "\"");
            }
            log.fine("creating new file \"" + templateFile.getAbsolutePath() + "\"...");
            if (!templateFile.createNewFile()) {
                throw new RuntimeException("Can't create file \"" + templateFile.getAbsolutePath() + "\"");
            }
            log.fine("writing file \"" + templateFile.getAbsolutePath() + "\"...");
            OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(templateFile));
            InputStreamReader ir = new InputStreamReader(is);
            char[] ca = new char[1024];
            int len;
            while ((len = ir.read(ca)) != -1) {
                ow.write(ca, 0, len);
            }
            ir.close();
            ow.close();
            log.fine("exit 3: template file: \"" + templateFile.getAbsolutePath() + "\"");
            return templateFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** 
     * @return the template file split into
     *         header, body and footer.
     *
     * @throws IOException if IO fails
     */
    protected static Template readTemplate(final File templateFile) throws IOException {
        // read the template file into one string
        final FileReader in = new FileReader(templateFile);
        final StringBuffer sb = new StringBuffer();
        final Template template = new Template();
        try {
            // split before "{\rtlch"
            int c;
            String substr;
            int len = 0;
            int state = 0;
            while ((c = in.read()) != -1) {
                sb.append((char) c);
                switch (state) {
                case 0:
                    if (len > 7) {
                        substr = sb.substring(len - 7, len);
                        if (substr.equals("{\\rtlch")) {
                            template.setHeader(sb.substring(0, len - 7));
                            state = 1;
                            sb.setLength(0);
                            sb.append("{\\rtlch");
                            sb.append((char) c);
                            len = 7;
                        }
                    }
                    break;
                default:
                    break;
                }
                len++;
            }
            template.setBody(sb.substring(0, len - 1));
            template.setFooter("}");
            template.setRowcount(determineRowCount(template.getBody()));
            return template;
        } finally {
            in.close();
        }
    }

    private static int determineRowCount(String body) {
        int rowCount = 0;
        int fromIndex = 0;
        int foundIndex = body.indexOf("@01", fromIndex);
        while (foundIndex != -1) {
            rowCount++;
            fromIndex = foundIndex + 5;
            foundIndex = body.indexOf("@01", fromIndex);
        }
        return rowCount;
    }

    /**
     * Encapsulates header, body and footer of an rtf template
     */
    public static class Template {
        private String header = null;
        private String body = null;
        private String footer = null;
        private int rowCount = 0;

        /**
         * @return the header
         */
        public String getHeader() {
            return header;
        }

        /**
         * @param header the header to set
         */
        public void setHeader(String header) {
            this.header = header;
        }

        /**
         * @return the body
         */
        public String getBody() {
            return body;
        }

        /**
         * @param body the body to set
         */
        public void setBody(String body) {
            this.body = body;
        }

        /**
         * @return the footer
         */
        public String getFooter() {
            return footer;
        }

        /**
         * @param footer the footer to set
         */
        public void setFooter(String footer) {
            this.footer = footer;
        }

        public int getRowcount() {
            return this.rowCount;
        }

        public void setRowcount(final int count) {
            this.rowCount = count;
        }

        public void dump() {
            FileWriter wr = null;
            try {
                wr = new FileWriter(new File("C:/Temp/template_dump.log"));

                wr.write("Template Dump:\r\nrow count = " + this.rowCount + "\r\n");

                wr.write("*** START header ***********************************\r\n");
                wr.write(this.header);
                wr.write("\r\n");
                wr.write("*** END header ***********************************\r\n");

                wr.write("*** START footer ***********************************\r\n");
                wr.write(this.footer);
                wr.write("\r\n");
                wr.write("*** END footer ***********************************\r\n");

                wr.write("*** START body ***********************************\r\n");
                wr.write(this.body);
                wr.write("\r\n");
                wr.write("*** END body ***********************************\r\n");
            } catch (IOException e) {
                throw new RapidBeansRuntimeException(e);
            } finally {
                try {
                    if (wr != null) {
                        wr.close();
                    }
                } catch (IOException e) {
                    throw new RapidBeansRuntimeException(e);
                }
            }
        }
    }
}
