// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

/* created by makebean v0.25 (coded by Weird) */

package jodd.bean.data;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class FooBean2 {

	private Timestamp fooTimestamp;
	public Timestamp getFooTimestamp() {
		return fooTimestamp;
	}
	public void setFooTimestamp(Timestamp v) {
		fooTimestamp = v;
	}

	private Time fooTime;
	public Time getFooTime() {
		return fooTime;
	}
	public void setFooTime(Time v) {
		fooTime = v;
	}

	private Date fooDate;
	public Date getFooDate() {
		return fooDate;
	}
	public void setFooDate(Date v) {
		fooDate = v;
	}

	private BigDecimal fooBigDecimal;
	public BigDecimal getFooBigDecimal() {
		return fooBigDecimal;
	}
	public void setFooBigDecimal(BigDecimal v) {
		fooBigDecimal = v;
	}


}
