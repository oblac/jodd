// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.speed.datetime;

import jodd.datetime.JStopWatch;
import jodd.datetime.JDateTime;

import java.util.GregorianCalendar;
import java.util.Calendar;

public class JDateTimeBenchmark {

	static JStopWatch swatch = new JStopWatch();

	public static void main(String[] args) throws Exception {
		System.out.println("\ntest #1");		// first two results in excell chart
		test1Calendar();
		test1Jodd();
		System.out.println("\ntest #2");
		test2Calendar();
		test2Jodd();
	}

	// ---------------------------------------------------------------- test #1

	static final int loop1 = 10000000;

	private static void test1Calendar() {
		GregorianCalendar gc = new GregorianCalendar();
		swatch.start();
		for (int i = loop1; i > 0; i--) {
			gc.add(Calendar.HOUR, 1);
			gc.add(Calendar.MONTH, -1);
			gc.get(Calendar.HOUR);
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());
	}

	private static void test1Jodd() {
		JDateTime jdt = new JDateTime();
		swatch.start();
		for (int i = loop1; i > 0; i--) {
			jdt.addHour(1);
			jdt.subMonth(1);
			jdt.getHour();
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());
	}

	// ---------------------------------------------------------------- test #2

	static int loop2 = 10000000;

	public static void test2Jodd() {
		JDateTime jdt = new JDateTime();
		swatch.start();
		for (int i = 1; i < loop2; i++) {
			jdt.set(1968, 9, 29);
			jdt.getYear();
			jdt.getMonth();
			jdt.getDayOfMonth();

			jdt.addHour(1);
			jdt.getWeekOfMonth();
			jdt.getWeekOfYear();
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());
	}


	public static void test2Calendar() {
		GregorianCalendar gt = new GregorianCalendar();
		swatch.start();
		for (int i = 1; i < loop2; i++) {
			gt.set(1968, 9, 29);
			gt.get(Calendar.YEAR);
			gt.get(Calendar.MONTH);
			gt.get(Calendar.DAY_OF_MONTH);

			gt.roll(Calendar.HOUR, true);
			gt.get(Calendar.WEEK_OF_MONTH);
			gt.get(Calendar.WEEK_OF_YEAR);
		}
		swatch.stop();
		System.out.println("---elapsed: " + swatch.elapsed());
	}


}
