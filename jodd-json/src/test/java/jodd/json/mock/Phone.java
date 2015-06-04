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
			throw new IllegalArgumentException();
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
