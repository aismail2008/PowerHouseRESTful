package com.test.powerhouse.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import com.test.powerhouse.BaseService;
import com.test.powerhouse.OperationStatusEnum;
import com.test.powerhouse.model.MeterReadings;

public strictfp class MeterService extends BaseService{
	private static Map<String, MeterReadings> metersReadings = new HashMap<>();
	private static final int TOLERANCE = 25;
	private static final String SPLIT_BY = ",";
	private static final String INPUT_FOLDER = "C:\\input\\meterreadings\\";
	
	public MeterService() {
	}
	
	public static void main(String[] args) throws ParseException{
		FractionService.importData("C:\\input\\download\\Book1.csv");
		FractionService.getProfilesFractions();
		importData("C:\\input\\download\\Book2.csv");
	}
	
	public static void processDataInputDirectory() throws IOException, InterruptedException {
		Path pathFolder = Paths.get(INPUT_FOLDER);
		WatchService watchService = FileSystems.getDefault().newWatchService();
		pathFolder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

		boolean valid = true;
		do {
			WatchKey watchKey = watchService.take();

			for (WatchEvent event : watchKey.pollEvents()) {
				WatchEvent.Kind kind = event.kind();
				if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
					String fileName = event.context().toString();
					if(fileName.toLowerCase().endsWith(".csv"))
						importData(fileName);
					importData(fileName);
				}
			}
			valid = watchKey.reset();

		} while (valid);
	}

	public static String importData(String profilesFractionsFilePath){
		String line = "";
		String errorLog = profilesFractionsFilePath.substring(0, profilesFractionsFilePath.lastIndexOf(".")) + "_errorlog.log";
		
		try (BufferedReader br = new BufferedReader(new FileReader(profilesFractionsFilePath))) {
			FileHandler handler = new FileHandler(errorLog, true);
			Logger logger = Logger.getLogger(FractionService.class.getName());
			logger.addHandler(handler);
			boolean foundErrors = false;

			while ((line = br.readLine()) != null) {
				String[] fraction = line.split(SPLIT_BY);
				Date date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(fraction[2]);
			    Calendar cal = Calendar.getInstance();
			    cal.setTime(date);
			    
			    MeterReadings meter = addReading(fraction[0], fraction[1], getMonth(fraction[2]), Integer.valueOf(fraction[3]));
				if(meter.getCount() == 12){//Now all months are available
					String error = isValidMeterReadings(meter.getMeterId());
					if (error != null && error.length() > 0) {
						logger.severe("Meter Id : " + meter.getMeterId() + " : " + error);
						foundErrors = true;
						deleteMeter(meter.getMeterId());
					}
				}
			}
			
			if(!foundErrors){
				errorLog = null;
				File f = new File(errorLog);
				f.delete();
				f = new File(profilesFractionsFilePath);
				f.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return errorLog;
	}

	public static MeterReadings addReading(String meterId, String profileName, int month, int reading) {
		MeterReadings meter = metersReadings.get(meterId);
		if (meter == null) {
			meter = new MeterReadings(profileName, meterId);
			metersReadings.put(meterId, meter);
		}
		meter.getReadings()[month] =  reading;
		meter.setCount(meter.getCount() + 1);
		return meter;
	}

	public static void updateReading(String meterId, String profileName, int month, int reading) {
		MeterReadings meter = metersReadings.get(meterId);
		if (meter == null) {// this is add meter Value
			addReading(meterId, profileName, month, reading);
		} else {
			meter.getReadings()[month] =  reading;
		}
	}

	public static void deleteReading(String meterId, int month) {
		MeterReadings meter = metersReadings.get(meterId);
		if (meter != null) {
			meter.getReadings()[month] = null;
			meter.setCount(meter.getCount() - 1);
		}
	}

	public static void deleteMeter(String meterId) {
		metersReadings.remove(meterId);
	}

	public static Integer getMeterReadings(String meterId, int month) {
		Integer value = null;
		MeterReadings meter = metersReadings.get(meterId);
		if (meter != null) {
			value = meter.getReadings()[month];
		}
		return value;
	}
	
	/**
	 * If meter found then return values else null
	 * @param meterId
	 * @return array of integer contains 12 months reading; null if meter is not found
	 */
	public static Integer[] getAllMeterReadings(String meterId) {
		Integer[] value = null;
		MeterReadings meter = metersReadings.get(meterId);
		if (meter != null) {
			value = meter.getReadings();
		}
		return value;
	}

	/**
	 * Assumptions: 
	 * - Assume data always contain 12 lines per meter
	 * - The MeterID is unique. 
	 * - Every first day of the year, meters are reset to zero. 
	 * Validations :
	 * - Under every meter readings, Every month reading is not less than previous one
	 * - Fractions should be available for profiles
	 * - Consumption for every month should be with a tolerance of 25%
	 * @param meterId
	 * @return String value with true or error message
	 */
	public static String isValidMeterReadings(String meterId) {
		StringBuilder errors = new StringBuilder();
		MeterReadings readings = metersReadings.get(meterId);
		if (readings == null) {
			errors.append(OperationStatusEnum.METER_NO_FOUND.getCode()+";");
		}else{
    		//Under every meter readings, Every month reading is not less than previous one
    		for (int i = 1; i < readings.getReadings().length; i++) {
    			if (readings.getReadings()[i] < readings.getReadings()[i-1]) {
    				errors.append(OperationStatusEnum.INVALID_METER_MONTH_VALUE.getCode() + ";");
    				break;
    			}
    		}
		
    		//Fractions should be available for profiles
			if (!FractionService.isProfileExist(readings.getProfileName())) {
				errors.append(OperationStatusEnum.PROFILE_NO_FOUND.getCode() + ";");
			} else {// if profile exist then consumption for every month should be with a tolerance of 25%
				for (int i = 0; i < readings.getReadings().length; i++) {
					float expectedCons = readings.getReadings()[11] * FractionService.getFractionValue(readings.getProfileName(), i);
					float monthMax = expectedCons + (expectedCons * TOLERANCE / 100);
					float monthMin = expectedCons - (expectedCons * TOLERANCE / 100);
					float actualCons = getConsumption(meterId, i);
					if (actualCons < monthMin || actualCons > monthMax) {
						errors.append(OperationStatusEnum.CONSUMPTION_OUT_OF_RANGE.getCode() + ";");
						break;
					}
				}
			}
    	}
		return errors.toString();
	}
	
	/**
	 * 
	 * @param meterId
	 * @param month
	 * @return consumption if Meter exists and has value for both month and previous month; -1 otherwise
	 */
	public static float getConsumption(String meterId, int month) {
		MeterReadings readings = metersReadings.get(meterId);
		int consumption = -1;
		if (readings != null && readings.getReadings()[month] != null) {
			consumption = readings.getReadings()[month];
			if (month > 0) {
				if (readings.getReadings()[month - 1] == null)
					consumption = -1;
				else
					consumption -= readings.getReadings()[month - 1];
			}
		}
		return consumption;
	}
}
