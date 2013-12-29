/*
 * Rapid Club Admin Application: OverviewTest.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 10.10.2007
 */
package org.rapidbeans.clubadmin.domain.report;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

import org.junit.Before;
import org.junit.Test;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.TrainingRegular;
import org.rapidbeans.clubadmin.domain.TrainingState;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.datasource.Document;

/**
 * @author Bluemel Martin
 */
public class OverviewTest {

    @Before
    public void setUp() {
        TypePropertyCollection.setDefaultCharSeparator(',');
    }

    @Test
    public void testAsStringMartin1() {
        Locale formerDefault = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);
            Document testdoc = new Document(new File("testdata/trainingslist_20060101_20060331.xml"));
            Trainer martin = (Trainer) testdoc
                    .findBeanByQuery("org.rapidbeans.clubadmin.domain.Trainer[id = 'Blümel_Martin_']");
            ArrayList<Trainer> trainers = new ArrayList<Trainer>();
            trainers.add(martin);
            Department aikido = (Department) testdoc
                    .findBeanByQuery("org.rapidbeans.clubadmin.domain.Department[id = 'Budo-Club Ismaning/Aikido']");
            ArrayList<Department> departments = new ArrayList<Department>();
            departments.add(aikido);
            RapidBeansLocale locale = new RapidBeansLocale("de");
            locale.init("org.rapidbeans.clubadmin");
            for (RapidBean bean : testdoc.findBeansByType("org.rapidbeans.clubadmin.domain.Training")) {
                TrainingRegular training = (TrainingRegular) bean;
                training.setState(TrainingState.checked);
            }
            String overview = Overview.asString(trainers, departments, locale);
            StringTokenizer st = new StringTokenizer(overview, "\n");
            assertEquals("Trainingsübersicht   Trainer: Blümel, Martin,   Abteilung: Budo-Club Ismaning/Aikido",
                    st.nextElement());
            assertEquals("---------------------------------------------------------------------------",
                    st.nextElement());
            String test1 = (String) st.nextElement();
            assertEquals("  1. 02.01.2006 Montag     20:30 Meditation II                    20,00 EUR", test1);
        } finally {
            Locale.setDefault(formerDefault);
        }
    }

    @Test
    public void testAsStringMartin2() {
        Locale formerDefault = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);
            Document testdoc = new Document(new File("testdata/trainingslist_20070901_20071231.xml"));
            Trainer martin = (Trainer) testdoc
                    .findBeanByQuery("org.rapidbeans.clubadmin.domain.Trainer[id = 'Blümel_Martin_']");
            ArrayList<Trainer> trainers = new ArrayList<Trainer>();
            trainers.add(martin);
            Department aikido = (Department) testdoc
                    .findBeanByQuery("org.rapidbeans.clubadmin.domain.Department[id = 'Budo-Club Ismaning/Aikido']");
            ArrayList<Department> departments = new ArrayList<Department>();
            departments.add(aikido);
            RapidBeansLocale locale = new RapidBeansLocale("de");
            locale.init("org.rapidbeans.clubadmin");
            for (RapidBean bean : testdoc.findBeansByType("org.rapidbeans.clubadmin.domain.Training")) {
                TrainingRegular training = (TrainingRegular) bean;
                training.setState(TrainingState.checked);
            }
            String overview = Overview.asString(trainers, departments, locale);
            StringTokenizer st = new StringTokenizer(overview, "\n");
            assertEquals("Trainingsübersicht   Trainer: Blümel, Martin,   Abteilung: Budo-Club Ismaning/Aikido",
                    st.nextElement());
            assertEquals("---------------------------------------------------------------------------",
                    st.nextElement());
            assertEquals("  1. 06.09.2007 Donnerstag 20:00 Aikido Erwachsene                20,50 EUR",
                    st.nextElement());
        } finally {
            Locale.setDefault(formerDefault);
        }
    }

    @Test
    public void testAsStringReinhard() {
        Locale formerDefault = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);
            Document testdoc = new Document(new File("testdata/trainingslist_20070901_20071231.xml"));
            Trainer martin = (Trainer) testdoc
                    .findBeanByQuery("org.rapidbeans.clubadmin.domain.Trainer[id = 'Landsberger_Reinhard_']");
            ArrayList<Trainer> trainers = new ArrayList<Trainer>();
            trainers.add(martin);
            Department aikido = (Department) testdoc
                    .findBeanByQuery("org.rapidbeans.clubadmin.domain.Department[id = 'Budo-Club Ismaning/Aikido']");
            ArrayList<Department> departments = new ArrayList<Department>();
            departments.add(aikido);
            RapidBeansLocale locale = new RapidBeansLocale("de");
            locale.init("org.rapidbeans.clubadmin");
            for (RapidBean bean : testdoc.findBeansByType("org.rapidbeans.clubadmin.domain.Training")) {
                TrainingRegular training = (TrainingRegular) bean;
                training.setState(TrainingState.checked);
            }
            String overview = Overview.asString(trainers, departments, locale);
            StringTokenizer st = new StringTokenizer(overview, "\n");
            assertEquals("Trainingsübersicht   Trainer: Landsberger, Reinhard,   Abteilung: Budo-Club Ismaning/Aikido",
                    st.nextElement());
            assertEquals("---------------------------------------------------------------------------",
                    st.nextElement());
            assertEquals("  1. 04.09.2007 Dienstag   18:00 Aikido Kinder/Erwachsene         15,50 EUR",
                    st.nextElement());
        } finally {
            Locale.setDefault(formerDefault);
        }
    }
}
