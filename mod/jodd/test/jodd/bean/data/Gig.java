// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.data;

import jodd.mutable.MutableInteger;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Gig {

	private String zoro;

	private List<String> listOfStrings;

	private ArrayList<MutableInteger> listOfIntegers;

	private List<? extends Abean> listOfAbeans;

	private Map<String, Integer> mapOfIntegers;

	private HashMap<String, ? extends Abean> mapOfAbeans;


	private List listOfStrings2;

	private ArrayList listOfIntegers2;

	private List listOfAbeans2;

	private Map mapOfIntegers2;

	private HashMap mapOfAbeans2;


	// ---------------------------------------------------------------- accessors


	public List<String> getListOfStrings() {
		return listOfStrings;
	}

	public void setListOfStrings(List<String> listOfStrings) {
		this.listOfStrings = listOfStrings;
	}

	public ArrayList<MutableInteger> getListOfIntegers() {
		return listOfIntegers;
	}

	public void setListOfIntegers(ArrayList<MutableInteger> listOfIntegers) {
		this.listOfIntegers = listOfIntegers;
	}

	public List<? extends Abean> getListOfAbeans() {
		return listOfAbeans;
	}

	public void setListOfAbeans(List<Abean> listOfAbeans) {
		this.listOfAbeans = listOfAbeans;
	}

	public Map<String, Integer> getMapOfIntegers() {
		return mapOfIntegers;
	}

	public void setMapOfIntegers(Map<String, Integer> mapOfIntegers) {
		this.mapOfIntegers = mapOfIntegers;
	}

	public HashMap<String, ? extends Abean> getMapOfAbeans() {
		return mapOfAbeans;
	}

	public void setMapOfAbeans(HashMap<String, Abean> mapOfAbeans) {
		this.mapOfAbeans = mapOfAbeans;
	}


	public List getListOfStrings2() {
		return listOfStrings2;
	}

	public void setListOfStrings2(List listOfStrings2) {
		this.listOfStrings2 = listOfStrings2;
	}

	public ArrayList getListOfIntegers2() {
		return listOfIntegers2;
	}

	public void setListOfIntegers2(ArrayList listOfIntegers2) {
		this.listOfIntegers2 = listOfIntegers2;
	}

	public List getListOfAbeans2() {
		return listOfAbeans2;
	}

	public void setListOfAbeans2(List listOfAbeans2) {
		this.listOfAbeans2 = listOfAbeans2;
	}

	public Map getMapOfIntegers2() {
		return mapOfIntegers2;
	}

	public void setMapOfIntegers2(Map mapOfIntegers2) {
		this.mapOfIntegers2 = mapOfIntegers2;
	}

	public HashMap getMapOfAbeans2() {
		return mapOfAbeans2;
	}

	public void setMapOfAbeans2(HashMap mapOfAbeans2) {
		this.mapOfAbeans2 = mapOfAbeans2;
	}


	public String getZoro() {
		return zoro;
	}

	public void setZoro(String zoro) {
		this.zoro = zoro;
	}
}
