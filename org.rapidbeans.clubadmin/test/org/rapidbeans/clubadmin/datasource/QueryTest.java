package org.rapidbeans.clubadmin.datasource;

import junit.framework.TestCase;

import org.rapidbeans.datasource.query.Query;

public class QueryTest extends TestCase {

    /**
     * test interpreting a query that formerly made difficulties.
     */
    public void testInterpretComplexQuery() {
        Query query = new Query("org.rapidbeans.clubadmin.domain.Trainer["
                + "rela[id = 'a' || id = 'b'] && relb[id = 'c']]");
        assertNotNull(query);
    }
}
