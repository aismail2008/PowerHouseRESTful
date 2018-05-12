package com.test.powerhouse;

public enum OperationStatusEnum {
	ERROR_FRACTIONS_SUM("Error_Sum of fraction for given profile is not equal to 1"), 
	METER_NO_FOUND("No meter found for this meter ID"),
	PROFILE_NO_FOUND("No profile found for this profile name"),
	INVALID_METER_MONTH_VALUE("This meter has month reading lower than previous one"),
	CONSUMPTION_OUT_OF_RANGE("Consumption for months are out of tolerance of 25%"),
	ERROR("Error!"),
	SUCESSS("Success");

	private String code;

	private OperationStatusEnum(String code) {
		this.code = code;
	}

	/**
	 * @return number of month starting from 0 to 11
	 */
	public String getCode() {
		return code;
	}
}
