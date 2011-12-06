// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.page;

import junit.framework.TestCase;

public class PageDataTest extends TestCase {

	public void testPaging() {
		PageData pd = new PageData(PageData.calcPageOfItem(0, 20), 50, 20);
		assertEquals(1, pd.getCurrentPage());
		assertEquals(0, pd.getFirstIndex());
		assertEquals(19, pd.getLastIndex());
		assertEquals(20, pd.getPageSize());
		assertEquals(50, pd.getTotalItems());
		assertEquals(3, pd.getTotalPages());
		assertEquals(20, pd.getPageItemsCount());
		assertTrue(pd.hasNextPage());
		assertFalse(pd.hasPreviousPage());

		pd = new PageData(PageData.calcPageOfItem(19, 20), 50, 20);
		assertEquals(1, pd.getCurrentPage());
		assertEquals(0, pd.getFirstIndex());
		assertEquals(19, pd.getLastIndex());
		assertEquals(3, pd.getTotalPages());
		assertEquals(20, pd.getPageItemsCount());
		assertTrue(pd.isFirstPage());

		pd = new PageData(PageData.calcPageOfItem(20, 20), 50, 20);
		assertEquals(2, pd.getCurrentPage());
		assertEquals(20, pd.getFirstIndex());
		assertEquals(39, pd.getLastIndex());
		assertEquals(20, pd.getPageItemsCount());

		pd = new PageData(PageData.calcPageOfItem(40, 20), 50, 20);
		assertEquals(3, pd.getCurrentPage());
		assertEquals(40, pd.getFirstIndex());
		assertEquals(49, pd.getLastIndex());
		assertEquals(10, pd.getPageItemsCount());
		assertTrue(pd.isLastPage());
	}

	public void testOver() {
		PageData pd = new PageData(PageData.calcPageOfItem(-1, 20), 50, 20);
		assertEquals(1, pd.getCurrentPage());
		assertEquals(0, pd.getFirstIndex());
		assertEquals(19, pd.getLastIndex());
		assertEquals(20, pd.getPageSize());
		assertEquals(50, pd.getTotalItems());
		assertEquals(3, pd.getTotalPages());
		assertTrue(pd.hasNextPage());
		assertFalse(pd.hasPreviousPage());
		assertEquals(20, pd.getPageItemsCount());

		pd = new PageData(PageData.calcPageOfItem(100, 20), 50, 20);
		assertEquals(3, pd.getCurrentPage());
		assertEquals(40, pd.getFirstIndex());
		assertEquals(49, pd.getLastIndex());
		assertTrue(pd.isLastPage());
		assertEquals(10, pd.getPageItemsCount());
	}

	public void testOne() {
		PageData pd = new PageData(1, 1, 20);
		assertEquals(1, pd.getCurrentPage());
		assertEquals(0, pd.getFirstIndex());
		assertEquals(0, pd.getLastIndex());
		assertEquals(20, pd.getPageSize());
		assertEquals(1, pd.getTotalItems());
		assertEquals(1, pd.getTotalPages());
		assertEquals(1, pd.getPageItemsCount());
	}

	public void testMinusOne() {
		PageData pd = new PageData(-1, 100, 20);
		assertEquals(1, pd.getCurrentPage());
		assertEquals(0, pd.getFirstIndex());
		assertEquals(19, pd.getLastIndex());
		assertEquals(20, pd.getPageSize());
		assertEquals(100, pd.getTotalItems());
		assertEquals(5, pd.getTotalPages());
		assertEquals(20, pd.getPageItemsCount());

	}

	public void testZero() {
		PageData pd = new PageData(1, 0, 20);
		assertTrue(pd.isLastPage());
		assertTrue(pd.isFirstPage());
		assertEquals(0, pd.getCurrentPage());
		assertEquals(0, pd.getFirstIndex());
		assertEquals(0, pd.getLastIndex());
		assertEquals(20, pd.getPageSize());
		assertEquals(0, pd.getTotalItems());
		assertEquals(0, pd.getTotalPages());
		assertEquals(0, pd.getPageItemsCount());

		pd = new PageData(4, 0, 20);
		assertTrue(pd.isLastPage());
		assertTrue(pd.isFirstPage());
		assertEquals(0, pd.getCurrentPage());
		assertEquals(0, pd.getFirstIndex());
		assertEquals(0, pd.getLastIndex());
		assertEquals(20, pd.getPageSize());
		assertEquals(0, pd.getTotalItems());
		assertEquals(0, pd.getTotalPages());
		assertEquals(0, pd.getPageItemsCount());

		pd = new PageData(-11, 0, 20);
		assertTrue(pd.isLastPage());
		assertTrue(pd.isFirstPage());
		assertEquals(0, pd.getCurrentPage());
		assertEquals(0, pd.getFirstIndex());
		assertEquals(0, pd.getLastIndex());
		assertEquals(20, pd.getPageSize());
		assertEquals(0, pd.getTotalItems());
		assertEquals(0, pd.getTotalPages());
		assertEquals(0, pd.getPageItemsCount());

		pd = new PageData(0, 0, 20);
		assertTrue(pd.isLastPage());
		assertTrue(pd.isFirstPage());
		assertEquals(0, pd.getCurrentPage());
		assertEquals(0, pd.getFirstIndex());
		assertEquals(0, pd.getLastIndex());
		assertEquals(20, pd.getPageSize());
		assertEquals(0, pd.getTotalItems());
		assertEquals(0, pd.getTotalPages());
		assertEquals(0, pd.getPageItemsCount());
	}

	public void testCalc() {
		assertEquals(0, PageData.calcFirstItemIndexOfPage(1, 5, 3));
		assertEquals(0, PageData.calcFirstItemIndexOfPage(1, 5, 10));
		assertEquals(5, PageData.calcFirstItemIndexOfPage(2, 5, 10));
		assertEquals(0, PageData.calcFirstItemIndexOfPage(2, 5, 5));
		assertEquals(0, PageData.calcFirstItemIndexOfPage(2, 5, 4));
		assertEquals(5, PageData.calcFirstItemIndexOfPage(2, 5, 6));

		assertEquals(0, PageData.calcFirstItemIndexOfPage(2, 5, 0));
		assertEquals(0, PageData.calcFirstItemIndexOfPage(0, 5, 10));
		assertEquals(0, PageData.calcFirstItemIndexOfPage(-1, 5, 10));
	}


	public void testFlow() {
		PageRequest pageRequest = new PageRequest();
		pageRequest.setPage(1);
		pageRequest.setSize(20);

		int startIndex = PageData.calcFirstItemIndexOfPage(pageRequest, 100);
		assertEquals(0, startIndex);

		pageRequest = new PageRequest();
		startIndex = PageData.calcFirstItemIndexOfPage(pageRequest, 100);
		assertEquals(0, startIndex);

		PageData pageData = new PageData(1, 100, 0);
		assertEquals(1, pageData.getCurrentPage());

	}

}
