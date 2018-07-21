package org.rapidbeans.clubadmin.service;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.rapidbeans.clubadmin.domain.Training;
import org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer;
import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.datasource.Document;

public class HistoryReportTest {

    @Test
    public void test() {
        TypePropertyCollection.setDefaultCharSeparator(',');
        final File dir = new File("history");
        assertTrue(dir.exists());
        final Map<String, Integer> trainingsCountMap = new HashMap<String, Integer>();
        for (final File subdir : dir.listFiles()) {
            final File trlist = new File(subdir, "Aikido/trainingslist.xml");
            if (trlist.exists()) {
                System.out.println(trlist.getAbsolutePath());
                stat(new Document(trlist), trainingsCountMap);
            }
        }
        for (final Entry<String, Integer> entry : trainingsCountMap.entrySet()) {
            System.out.println("@@@ " + entry.getKey() + ": " + entry.getValue().toString());
        }
    }

    private void stat(final Document doc, final Map<String, Integer> trainingsCountMap) {
        for (final RapidBean bean : doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training")) {
            final Training tr = (Training) bean;
            if (tr.getHeldbytrainers() == null) {
                return;
            }
            for (final TrainingHeldByTrainer trhbt : tr.getHeldbytrainers()) {
                if (trhbt.getTrainer() == null) {
                    continue;
                }
                final String trname = trhbt.getTrainer().getIdString();
                final Integer cnt = trainingsCountMap.get(trname);
                if (cnt != null) {
                    trainingsCountMap.put(trname, cnt.intValue() + 1);
                } else {
                    trainingsCountMap.put(trname, 1);
                }
            }
        }
    }
}
