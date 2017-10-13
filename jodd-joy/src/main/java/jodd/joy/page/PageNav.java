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

/**
 * Moderate page navigator. Like altavista.
 */
public class PageNav {

	public static final int DEFAULT_SHOWN = 10;

	protected int from;
	protected int to;

	public PageNav(PageData pageData) {
		this(pageData.getTotalPages(), pageData.getCurrentPage(), DEFAULT_SHOWN);
	}

	public PageNav(PageData pageData, int shown) {
		this(pageData.getTotalPages(), pageData.getCurrentPage(), shown);
	}

	public PageNav(int total, int current, int shown) {
		if (total == 0) {
			return;
		}
		if (total <= shown) {
			from = 1;
			to = total;
			return;
		}
		int leftMin = shown / 2;
		int rightMin = leftMin - 1;

		from = current - leftMin;
		if (from < 1) {
			from = 1;
			to = shown;
			return;
		}

		to = current + rightMin;
		if (to > total) {
			to = total;
			from = to - shown + 1;
		}
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

}