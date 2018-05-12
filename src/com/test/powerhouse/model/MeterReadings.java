package com.test.powerhouse.model;

public class MeterReadings {
	private static final int MONTHS_COUNT = 12;
	private String profileName;
	private String meterId;
	private Integer[] readings;
	private int count;

	public MeterReadings(String profileName, String meterId) {
		this.profileName = profileName;
		this.setMeterId(meterId);
		readings = new Integer[MONTHS_COUNT];
		setCount(0);
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public Integer[] getReadings() {
		return readings;
	}

	public void setReadings(Integer[] readings) {
		this.readings = readings;
	}

	public String getMeterId() {
		return meterId;
	}

	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
