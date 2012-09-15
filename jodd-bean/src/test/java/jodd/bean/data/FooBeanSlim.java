// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.data;

import java.util.Map;
import java.util.List;

public class FooBeanSlim {

	private Integer fooInteger;
	private int fooint;
	private Long fooLong;
	private long foolong;
	private Byte fooByte;
	private byte foobyte;
	private Character fooCharacter;
	private char foochar;
	private Boolean fooBoolean;
	private boolean fooboolean;
	private Float fooFloat;
	private float foofloat;
	private Double fooDouble;
	private double foodouble;
	private String fooString;
	private String[] fooStringA;
	private Map fooMap;
	private List fooList;



	public String[] getStringA() {
		return fooStringA;
	}

	public List getList() {
		return fooList;
	}

	public Map getMap() {
		return fooMap;
	}
}
