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

package jodd.util;

import java.util.Locale;
import java.text.DateFormatSymbols;

/**
 * Simple holder for <code>DateFormatSymbols</code> that doesn't create new array on each call.
 * This improves performance by avoiding duplication of returned arrays.
 * <p>
 * Use this class from {@link LocaleUtil} or cache it manually.
 */
public class DateFormatSymbolsEx {

	protected final String[] months;
	protected final String[] shortMonths;
	protected final String[] weekdays;
	protected final String[] shortWeekdays;
	protected final String[] eras;
	protected final String[] ampms;

	public DateFormatSymbolsEx(Locale locale) {
		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);

		months = dateFormatSymbols.getMonths();
		shortMonths = dateFormatSymbols.getShortMonths();
		weekdays = dateFormatSymbols.getWeekdays();
		shortWeekdays = dateFormatSymbols.getShortWeekdays();
		eras = dateFormatSymbols.getEras();
		ampms = dateFormatSymbols.getAmPmStrings();
	}

	// ---------------------------------------------------------------- getters

	/**
	 * Returns month string.
	 */
	public String getMonth(int i) {
		return this.months[i];
	}

	/**
	 * Returns short months.
	 */
	public String getShortMonth(int i) {
		return this.shortMonths[i];
	}

	/**
	 * Returns weekday.
	 */
	public String getWeekday(int i) {
		return this.weekdays[i];
	}

	/**
	 * Returns short weekday.
	 */
	public String getShortWeekday(int i) {
		return this.shortWeekdays[i];
	}

	/**
	 * Returns BC era.
	 */
	public String getBcEra() {
		return this.eras[0];
	}

	/**
	 * Returns AD era.
	 */
	public String getAdEra() {
		return this.eras[1];
	}

	/**
	 * Returns AM.
	 */
	public String getAM() {
		return this.ampms[0];
	}

	/**
	 * Returns PM.
	 */
	public String getPM() {
		return this.ampms[1];
	}

}