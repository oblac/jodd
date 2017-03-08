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

package jodd.vtor.data;

import jodd.datetime.JDateTime;
import jodd.vtor.constraint.TimeAfter;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Tad {

	@TimeAfter("2011-05-01 11:12:13.456")
	JDateTime one = new JDateTime("2011-05-01 11:12:13.456");

	@TimeAfter("2011-05-01 11:12:13.456")
	JDateTime oneOk = new JDateTime("2011-05-01 11:12:13.457");

	@TimeAfter("2011-05-01 11:12:13")
	Date date = new Date(111, 4, 1, 11, 12, 13);

	@TimeAfter("2011-05-01 11:12:13")
	Date dateOk = new Date(111, 4, 1, 11, 12, 14);

	@TimeAfter("2011-05-01 11:12:13")
	Calendar calendar = new GregorianCalendar(2011, 4, 1, 11, 12, 13);

	@TimeAfter("2011-05-01 11:12:13")
	Calendar calendarOk = new GregorianCalendar(2011, 4, 1, 11, 12, 14);

}
