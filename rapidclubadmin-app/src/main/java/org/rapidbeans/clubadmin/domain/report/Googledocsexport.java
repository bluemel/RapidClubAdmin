/*
 * RapidBeans Application RapidClubAdmin: Overview.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 23.08.2007
 */
package org.rapidbeans.clubadmin.domain.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.RapidClubAdminBusinessLogicException;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.Training;
import org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer;
import org.rapidbeans.clubadmin.domain.TrainingState;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.clubadmin.presentation.swing.ReportPresentationDialogSwing;
import org.rapidbeans.clubadmin.service.Umlaut;
import org.rapidbeans.core.basic.PropertyDate;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.exception.PropValueNullException;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.datasource.query.Query;
import org.rapidbeans.domain.finance.Currency;
import org.rapidbeans.domain.finance.Money;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.MessageDialog;
import org.rapidbeans.presentation.config.ApplicationGuiType;
import org.rapidbeans.service.Action;

/**
 * Report: Prepare data for input into Googledocs App "BCI Trainerzeiten"
 * 
 * @author Martin Bluemel
 */
public final class Googledocsexport extends Action {

    public void execute() {
        final Document activeDoc = ((RapidClubAdminClient) ApplicationManager.getApplication()).getActiveDocument();
        if (activeDoc == null) {
            MessageDialog.createInstance(ApplicationGuiType.swing).messageError("Keine Traingsliste ausgewählt", "Fehler");
            return;
        }
        final RapidBeansLocale locale = ((RapidClubAdminClient) ApplicationManager.getApplication()).getCurrentLocale();
        final Department department = activeDoc.findBeansByQuery("org.rapidbeans.clubadmin.domain.Department[name = 'Aikido']").stream()
            .map(bean -> (Department) bean)
            .collect(Collectors.toList()).get(0);
        final List<Trainer> trainers = activeDoc.findBeansByQuery("org.rapidbeans.clubadmin.domain.Trainer").stream()
            .map(bean -> (Trainer) bean)
            .filter(t -> t.getDepartments().contains(department))
            .collect(Collectors.toList());
        final StringBuilder sb = new StringBuilder();
        sb.append("---------------------------------------------------------------------------------------------------------------\n");
        sb.append("---------------------------------------------------------------------------------------------------------------\n");
        sb.append("BCI Trainerzeiten f" + Umlaut.L_UUML + "r Abteilung \"" + department.getName() + "\"\n");
        sb.append("zur Eingabe als Mehrfachnachweis in der \"BCI Trainerzeiten\" App\n");
        sb.append("---------------------------------------------------------------------------------------------------------------\n");
        sb.append("---------------------------------------------------------------------------------------------------------------\n");
        sb.append("\n");

        for (final Trainer trainer : trainers) {
            if (findTrainigsHeld(trainer, department).size() > 0) {
                printMehrfachnachweis(locale, sb, trainer, department);
            }
        }
        new ReportPresentationDialogSwing(sb.toString(), "BCI Trainerzeiten, Merhfachnachweise").show();
    }

    private static void printMehrfachnachweis(final RapidBeansLocale locale, final StringBuilder sb, final Trainer trainer, final Department department) {
        List<TrainingHeldByTrainer> res = findTrainigsHeld(trainer, department);
        sb.append("---------------------------------------------------------------------------------------------------------------\n");
        sb.append("Mehrfachnachweis f" + Umlaut.L_UUML + "r Trainer: " + trainer.getLastname() + ", "
            + trainer.getFirstname() + ",   Abteilung: " + department.getName() + "\n");
        sb.append("------------------------------------- START Mehrfachnachweis -------------------------------------\n");
        Money sumMoneyEarned = null;
        for (final TrainingHeldByTrainer trhbt : res) {
            final Training training = (Training) trhbt.getParentBean();
            Money moneyEarned = null;
            if (training.getState() == TrainingState.checked) {
                moneyEarned = trhbt.getMoneyEarned();
            }
            if (moneyEarned != null) {
                if (sumMoneyEarned == null) {
                    sumMoneyEarned = new Money(moneyEarned.getMagnitude(), (Currency) moneyEarned.getUnit());
                } else {
                    if (!(moneyEarned.getUnit() == sumMoneyEarned.getUnit())) {
                        throw new RapidClubAdminBusinessLogicException("xxx", "unexcpected money unit");
                    }
                    sumMoneyEarned = new Money(sumMoneyEarned.getMagnitude().add(moneyEarned.getMagnitude()),
                        (Currency) sumMoneyEarned.getUnit());
                }
            }
            if (training.getState() == TrainingState.checked) {
                // Pro Zeile ein Eintrag mit Datum, Anzahl der Stunden und optional Teilnehmer (tt.mm.jjjj/##.#[/#]).
                sb.append(StringHelper.fillUp(PropertyDate.formatDate(training.getDate(), locale), 11, ' ',
                    StringHelper.FillMode.right));
                sb.append('/');
                // sb.append(training.getTimestart().toString());
                // sb.append('/');
                sb.append(String.format("%.2f", trhbt.getMinutesWorked().floatValue() / 60));
                try {
                    if (training.getPartipiciantscount() > 0) {
                        sb.append('/');
                        sb.append(Integer.toString(training.getPartipiciantscount()));
                    }
                } catch (PropValueNullException e) {
                    // do nothing
                }
                sb.append('\n');
            }
        }
        if (sumMoneyEarned == null) {
            sumMoneyEarned = new Money(BigDecimal.ZERO, Currency.euro);
        }
        sb.append("------------------------------------- ENDE Mehrfachnachweis --------------------------------------\n");
        // sb.append("Verdienst: " + sumMoneyEarned.toStringGui(locale, 2, 2));
        sb.append("\n");
    }

    public static List<TrainingHeldByTrainer> findTrainigsHeld(final Trainer trainer, final Department department) {
        final String qs = "org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer[" + "trainer[id = '"
            + trainer.getIdString() + "']" + " & parentBean[parentBean[parentBean[id = '" + department.getIdString()
            + "']]]" + "]";
        final Query query1 = new Query(qs);
        final List<RapidBean> res1 = department.getContainer().findBeansByQuery(query1);
        final Query query2 = new Query(
            "org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer[" + "trainer[id = '" + trainer.getIdString()
                + "']" + " & parentBean[parentBean[id = '" + department.getIdString() + "']]]" + "]");
        final List<RapidBean> res2 = department.getContainer().findBeansByQuery(query2);
        for (final RapidBean specialTraining : res2) {
            res1.add(specialTraining);
        }
        final RapidBean[] a1 = res1.toArray(new RapidBean[0]);
        Arrays.sort(a1);
        final List<TrainingHeldByTrainer> trhbts = new ArrayList<TrainingHeldByTrainer>();
        for (final RapidBean bean : a1) {
            TrainingHeldByTrainer trhbt = (TrainingHeldByTrainer) bean;
            if (((Training) trhbt.getParentBean()).getState() == TrainingState.checked) {
                trhbts.add(trhbt);
            }
        }
        return trhbts;
    }
}
