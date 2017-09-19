// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.joy.page;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PageDataTest {

	@Test
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

	@Test
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

	@Test
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

	@Test
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

	@Test
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

	@Test
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


	@Test
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
