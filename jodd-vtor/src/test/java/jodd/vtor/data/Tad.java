// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

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
