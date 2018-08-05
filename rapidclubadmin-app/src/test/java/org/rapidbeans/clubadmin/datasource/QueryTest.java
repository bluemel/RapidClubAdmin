package org.rapidbeans.clubadmin.datasource;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.rapidbeans.datasource.query.Query;

public class QueryTest {

	/**
	 * test interpreting a query that formerly made difficulties.
	 */
	@Test
	public void testInterpretComplexQuery() {
		Query query = new Query(
				"org.rapidbeans.clubadmin.domain.Trainer[" + "rela[id = 'a' || id = 'b'] && relb[id = 'c']]");
		assertNotNull(query);
	}
}
