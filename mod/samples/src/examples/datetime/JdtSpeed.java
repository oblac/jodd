// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.datetime;

import java.util.Calendar;
import java.util.GregorianCalendar;

import jodd.datetime.JDateTime;

public class JdtSpeed {

	private static int LOOPS = 1000000;

	public static void main(String args[]) {
		if (args.length >= 1) {
			if (args[0].equals("jdt")) {
				measureJdt();
			} else if (args[0].equals("gc")) {
				measureGc();
			}
		}
	}

	public static void measureJdt() {
		
		JDateTime jdt = new JDateTime();

		long start = System.currentTimeMillis();
		for (int i = 1; i < LOOPS; i++) {
			jdt.set(1968, 9, 29);
			jdt.getYear();
			jdt.getMonth();
			jdt.getDayOfMonth();
			
			jdt.addHour(1);
			jdt.getWeekOfMonth();
			jdt.getWeekOfYear();
		}
		System.out.println("time: " + (System.currentTimeMillis() - start) + "ms.");
	}


	public static void measureGc() {
		
		GregorianCalendar gt = new GregorianCalendar();

		long start = System.currentTimeMillis();
		for (int i = 1; i < LOOPS; i++) {
			gt.set(1968, 9, 29);
			gt.get(Calendar.YEAR);
			gt.get(Calendar.MONTH);
			gt.get(Calendar.DAY_OF_MONTH);
			
			gt.roll(Calendar.HOUR, true);
			gt.get(Calendar.WEEK_OF_MONTH);
			gt.get(Calendar.WEEK_OF_YEAR);
		}
		System.out.println("time: " + (System.currentTimeMillis() - start) + "ms.");
	}

}
