/**
 * 
 */
package com.ibm.util.merge.directive.provider;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.*;

public class DataTableTest {
	private DataTable table;
    
    @Before
    public void beforeEachTest() {
    	table = new DataTable();
		table.setCols(new ArrayList<String>(Arrays.asList("One", "Two", "Three")));
		table.addRow(new ArrayList<String>(Arrays.asList("R1C1","R1C2","R1C3")));
		table.addRow(new ArrayList<String>(Arrays.asList("R2C1","R2C2","R2C3")));
		table.addRow(new ArrayList<String>(Arrays.asList("R3C1","R3C2","R3C3")));
		table.addRow(new ArrayList<String>(Arrays.asList("R4C1","R4C2","R4C3")));
    }
    @After
    public void afterEachTest() {
    	table = null;
    }

	@Test
	public void testGetValueIntStringCol() {
		assertEquals("R2C1", table.getValue(1, "One"));
	}
	@Test
	public void testGetValueIntStringColNotFound() {
		assertEquals("", table.getValue(1, "Four"));
	}
	@Test
	public void testGetValueIntStringRowLessThanZero() {
		assertEquals("", table.getValue(-1, "One"));
	}
	@Test
	public void testGetValueIntStringRowGreaterThanSize() {
		assertEquals("", table.getValue(9, "One"));
	}
	@Test
	public void testGetValueIntIntRowLessThanZero() {
		assertEquals("", table.getValue(-1, 0));
	}
	@Test
	public void testGetValueIntIntRowGreaterThanSize() {
		assertEquals("", table.getValue(5, 1));
	}
	@Test
	public void testGetValueIntIntColLessThanZero() {
		assertEquals("", table.getValue(1, -1));
	}
	@Test
	public void testGetValueIntIntColGreaterThanSize() {
		assertEquals("", table.getValue(1, 10));
	}
	@Test
	public void testGetValueIntInt() {
		assertEquals("R4C3", table.getValue(3, 2));
	}
}
