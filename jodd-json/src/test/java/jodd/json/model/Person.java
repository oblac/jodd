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

package jodd.json.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Person {

	private Integer id;
	private String firstName;
	private String lastName;
	private Date birthDate;
	private String[] favoriteFoods;
	private Integer[] luckyNumbers;
	private Integer[][] pastLottoPicks;
	private String description;
	private List<LoopClassOne> loopClassOnes;

	private List<Address> addresses;

	private Map<String, Account> accounts;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public Map<String, Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(Map<String, Account> accounts) {
		this.accounts = accounts;
	}

	public String[] getFavoriteFoods() {
		return favoriteFoods;
	}

	public void setFavoriteFoods(String[] favoriteFoods) {
		this.favoriteFoods = favoriteFoods;
	}

	public Integer[] getLuckyNumbers() {
		return luckyNumbers;
	}

	public void setLuckyNumbers(Integer[] luckyNumbers) {
		this.luckyNumbers = luckyNumbers;
	}

	public Integer[][] getPastLottoPicks() {
		return pastLottoPicks;
	}

	public void setPastLottoPicks(Integer[][] pastLottoPicks) {
		this.pastLottoPicks = pastLottoPicks;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<LoopClassOne> getLoopClassOnes() {
		return loopClassOnes;
	}

	public void setLoopClassOnes(List<LoopClassOne> loopClassOnes) {
		this.loopClassOnes = loopClassOnes;
	}
}
