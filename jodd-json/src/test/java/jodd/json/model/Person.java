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
