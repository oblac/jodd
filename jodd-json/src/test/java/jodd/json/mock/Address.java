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

public class Address {
	private Person person;
	private String street;
	private String city;
	private String state;
	private Zipcode zipcode;

	public Address() {
	}

	public Address(String street, String city, String state, Zipcode zipcode) {
		this.street = street;
		this.city = city;
		this.state = state;
		this.zipcode = zipcode;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Zipcode getZipcode() {
		return zipcode;
	}

	public void setZipcode(Zipcode zipcode) {
		this.zipcode = zipcode;
	}


	public void setPerson(Person person) {
		this.person = person;
	}

	public Person getPerson() {
		return person;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Address address = (Address) o;

		if (city != null ? !city.equals(address.city) : address.city != null) {
			return false;
		}
		if (state != null ? !state.equals(address.state) : address.state != null) {
			return false;
		}
		if (street != null ? !street.equals(address.street) : address.street != null) {
			return false;
		}
		if (zipcode != null ? !zipcode.equals(address.zipcode) : address.zipcode != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = street != null ? street.hashCode() : 0;
		result = 31 * result + (city != null ? city.hashCode() : 0);
		result = 31 * result + (state != null ? state.hashCode() : 0);
		result = 31 * result + (zipcode != null ? zipcode.hashCode() : 0);
		return result;
	}
}
