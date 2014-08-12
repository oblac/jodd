// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.mock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Phone {
	private PhoneNumberType type;
	private String areaCode;
	private String exchange;
	private String number;

	private static final Pattern PHONE_PATTERN = Pattern.compile("\\(?(\\d{3})\\)?[\\s-](\\d{3})[\\s-](\\d{4})");

	protected Phone() {
	}

	public Phone(PhoneNumberType aType, String number) {
		this.type = aType;
		Matcher matcher = PHONE_PATTERN.matcher(number);
		if (matcher.matches()) {
			this.areaCode = matcher.group(1);
			this.exchange = matcher.group(2);
			this.number = matcher.group(3);
		}
		else {
			throw new IllegalArgumentException(number + " does not match one of these formats: (xxx) xxx-xxxx, xxx xxx-xxxx, or xxx xxx xxxx.");
		}
	}

	public String getAreaCode() {
		return areaCode;
	}

	public PhoneNumberType getType() {
		return type;
	}

	public void setType(PhoneNumberType type) {
		this.type = type;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPhoneNumber() {
		return "(" + areaCode + ") " + exchange + "-" + number;
	}
}
