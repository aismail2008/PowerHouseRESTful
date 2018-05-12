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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import com.test.powerhouse.BaseService;
import com.test.powerhouse.OperationStatusEnum;
import com.test.powerhouse.model.Profile;

public class FractionService extends BaseService{
	private static Map<String, Profile> profilesFractions = new HashMap<>();
	private static final String SPLIT_BY = ",";
	private static final String INPUT_FOLDER = "C:\\input\\profiles\\";
	
	public static void main(String[] args){
		try {
			processDataInputDirectory();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		("C:\\input\\download\\Book1.csv");
	}

	public FractionService() {
	}
	
	public static  Map<String, Profile> getProfilesFractions(){
		return profilesFractions;
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
			    
				Profile prof = addFractionValue(getMonth(fraction[0]), fraction[1], Float.valueOf(fraction[2]));
				if(prof.getCount() == 12 && !isValidProfile(prof.getProfileName())){//Now all months are available
					logger.severe("Profile name : " + prof.getProfileName() + " : " + OperationStatusEnum.ERROR_FRACTIONS_SUM.getCode());
					foundErrors = true;
					deleteProfile(prof.getProfileName());
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

	public static Profile addFractionValue(int month, String profileName, Float fraction) {
		Profile prof = profilesFractions.get(profileName);
		if (prof == null) {
			prof = new Profile(profileName);
			profilesFractions.put(profileName, prof);
		}
		prof.getFractionValues()[month] =  fraction;
		prof.setCount(prof.getCount() + 1);
		return prof;
	}

	public static void updateFractionValue(int month, String profileName, Float fraction) {
		Profile prof = profilesFractions.get(profileName);
		if (prof == null) {// this is add fraction Value
			addFractionValue(month, profileName, fraction);
		} else {
			prof.getFractionValues()[month] = fraction;
		}
	}
	
	public static void deleteFractionValue(String profileName, int month) {
		Profile prof = profilesFractions.get(profileName);
		if (prof != null) {
			prof.getFractionValues()[month] = null;
			prof.setCount(prof.getCount() - 1);
		}
	}
	
	public static void deleteProfile(String profileName){
		profilesFractions.remove(profileName);
	}
	
	public static Float getFractionValue(String profileName, int month) {
		Float value = null;
		Profile prof = profilesFractions.get(profileName);
		if (prof != null) {
			value = prof.getFractionValues()[month];
		}
		return value;
	}
	
	public static Float[] getAllFractionValues(String profileName) {
		Float[] value = null;
		Profile prof = profilesFractions.get(profileName);
		if (prof != null) {
			value = prof.getFractionValues();
		}
		return value;
	}
	
	public static boolean isProfileExist(String profileName){
		return profilesFractions.containsKey(profileName);
	}
	
	/**
	 * Assumptions: 
	 * -There is no specific order for the rows.  
	 * -There is only one value per Month-Profile combination. 
	 * -Input data always contains ratios for the twelve months for a given profile (assume no missing data). 
	 * Validations : 
	 * - For a given Profile (A) the sum of all fractions should be 1. If input data is not fulfilling this condition 
	 *   then an error should be raised.   
	 * 
	 * @param pofileName
	 * @return String value with true or error message
	 */
	public static boolean isValidProfile(String pofileName){
		boolean success = true;
		Profile profile = profilesFractions.get(pofileName);
		if (profile == null) {
			success = false;
		} else {
			if (profile.getCount() != 12) {
				success = false;
			} else {
				float sum = 0;
				for (int i = 0; i < profile.getFractionValues().length; i++) {
					sum += profile.getFractionValues()[i];
				}
				if (sum != 1) {
					success = false;
				}
			}
		}
		return success;
	}
}