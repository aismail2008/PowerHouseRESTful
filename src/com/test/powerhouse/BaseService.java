package com.test.powerhouse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public abstract class BaseService {

	protected static int getMonth(String monthName) throws ParseException{
		Date date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(monthName);
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    return cal.get(Calendar.MONTH);
	}
}
