/*
 * RapidClubAdminApplication: CreditInstituteTest.java
 *
 * Copyright Martin Bluemel, 2006
 *
 * 14.10.2006
 */
package org.rapidbeans.clubadmin.domain;

import junit.framework.TestCase;

/**
 * Test for the credit institute.
 *
 * @author Martin Bluemel
 */
public class CreditInstituteTest extends TestCase {

    /**
     * Credit Institute.
     */
    public void testDelete() {
        TrainingsList bp = new TrainingsList();
//        Document bpdoc = new Document("test", bp);
        Trainer bluemel = new Trainer("Bluemel Martin");
        bp.addTrainer(bluemel);
//        CreditInstitute nationalBank = new CreditInstitute("\"National Bank\"");
//        bp.addCreditinstitute(nationalBank);
//        CreditInstitute moneyBank = new CreditInstitute("\"Money Bank\"");
//        bp.addCreditinstitute(moneyBank);
//        bluemel.setCreditinstitute(moneyBank);
//
//        // delete the National Bank
//        // this is O. K. for the National Bank is not used.
//        assertNotNull(bpdoc.findBean(
//                "org.rapidbeans.clubadmin.domain.CreditInstitute", "National Bank"));
//        nationalBank.delete();
//        assertNull(bpdoc.findBean(
//                "org.rapidbeans.clubadmin.domain.CreditInstitute", "National Bank"));
//
//        // delete the Money Bank
//        // this is not permitted for the Money Bank is used by Bluemel.
//        try {
//            moneyBank.delete();
//            fail("expected RapidClubAdminDomainException");
//        } catch (RapidClubAdminBusinessLogicException e) {
//            assertTrue(true);
//        }
    }
}
