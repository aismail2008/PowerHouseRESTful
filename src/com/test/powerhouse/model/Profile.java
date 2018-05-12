package com.test.powerhouse.model;

public class Profile {
	private static final int MONTHS_COUNT = 12;
	private String profileName;
	private Float[] fractionValues;
	private int count = 0;

	public Profile(String profileName) {
		this.profileName = profileName;
		fractionValues = new Float[MONTHS_COUNT];
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public Float[] getFractionValues() {
		return fractionValues;
	}

	public void setFractionValues(Float[] fractionValues) {
		this.fractionValues = fractionValues;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}
}
