package com.christ.job.services.common;

import jakarta.persistence.NonUniqueResultException;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.text.DateFormatSymbols;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"unchecked","rawtypes"})
public class Utils {
    public static void log(String message) {
        try {
            String method = Thread.currentThread().getStackTrace()[2].getMethodName();
            if(method != null && method.trim().length() > 0) {

            }
        }
        catch(Exception ex) { }
    }
    public static boolean isNullOrEmpty(Object value) {
        return (value == null);
    }
    public static boolean isNullOrEmpty(String value) {
        return (value == null || value.trim().isEmpty());
    }
    public static <T> boolean isNullOrEmpty(Set<T> value) {
        return (value == null || value.isEmpty() || value.size() == 0);
    }
	public static boolean isNullOrEmpty(List value) {
        return (value == null || value.isEmpty() || value.size() == 0);
    }
	public static boolean isNullOrEmpty(Map value) {
        return (value == null || value.isEmpty() || value.size() == 0);
    }
    public static boolean isNullOrWhitespace(String value) {
        return ((Utils.isNullOrEmpty(value)) || value.trim().length() == 0);
    }
    public static boolean isNullOrEmpty(Integer value) {
        return (value == null || value == 0);
    }
    public static boolean isNullOrEmpty(Long value) {
        return (value == null || value == 0);
    }
    public static boolean isNullOrEmpty(Boolean value) {
        return (value == null);
    }
    public static String setDefaultIfEmpty(String value, String defaultValue) {
        return Utils.isNullOrEmpty(value) == true ? defaultValue : value;
    }
    public static String setDefaultIfWhitespace(String value, String defaultValue) {
        return Utils.isNullOrWhitespace(value) == true ? defaultValue : value;
    }
    public static boolean isNullOrEmpty(BigDecimal value) {
        return (value == null || value.compareTo(BigDecimal.ZERO) == 0);
    }
    
	public static String getMonthName(Integer monthId) {
        String month = "";
     	DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (monthId >= 1 && monthId <= 12 ) {
            month = months[monthId-1];
        }
        return month;   	
   }
	
   public static String[] GetStringArray(ArrayList<String> arr) { 
       String str[] = new String[arr.size()]; 
       for (int j = 0; j < arr.size(); j++) {  
           str[j] =   arr.get(j); 
       } 
       return str; 
   }

    public static Object getUniqueResult(List<Object> list) throws Exception {
        if(!Utils.isNullOrEmpty(list)) {
            if (list.size() == 1) return list.get(0);
            throw new NonUniqueResultException();
        }
        else {
            return null;
        }
    }
    
    public static long getDaysDifference(String startDate,String endDate) throws Exception{
        LocalDate stDate = convertStringDateTimeToLocalDate(startDate);
        LocalDate edDate = convertStringDateTimeToLocalDate(endDate);
        long diff = Duration.between(edDate.atStartOfDay(),stDate.atStartOfDay()).toDays();
		return diff;
	}

    public static byte[] getMD5CheckSumSalt() throws NoSuchAlgorithmException, NoSuchProviderException {
	    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
	    byte[] salt = new byte[16];
	    sr.nextBytes(salt);
	    return salt;
	 }
	 
	public static String createMD5CheckSum(String fileName, byte[] salt) throws NoSuchAlgorithmException {
		 String md5HashFileName = null;
	     try {
	         MessageDigest md = MessageDigest.getInstance("MD5");
	         md.update(salt);
	         byte[] bytes = md.digest(fileName.getBytes());
	         StringBuilder sb = new StringBuilder();
	         for(int i=0; i< bytes.length ;i++) {
	        	 sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	         }
	         md5HashFileName = sb.toString();
	     } 
	     catch (NoSuchAlgorithmException e) {
	    	 e.printStackTrace();
	     }
	     return md5HashFileName;
	}
	
	public static String removeFileExtension(String fileName) { 
		if(null != fileName && fileName.contains(".")) {
			return fileName.substring(0, fileName.lastIndexOf("."));
		}
		return null;
	}
	
	public static String getFileExtension(String fileName) { 
		if(null != fileName && fileName.contains(".")) {
			return fileName.substring(fileName.lastIndexOf("."),fileName.length());
		}
		return null;
	}
	
	public static Set<Integer> GetCampusDepartmentMappingIds(String[] dataArray, Set<Integer> ids) {
		for (String data : dataArray) {
			if (data!=null && !data.isEmpty()) {
				StringBuffer id = new StringBuffer();										
				for (int i=0;i<data.length();i++) {
					char ch = data.charAt(i);
					if(ch=='-') {
						 break;
					}													
					id.append(ch);
				}
				if (!Utils.isNullOrEmpty(id)) {
					ids.add(Integer.valueOf(id.toString().trim()));
				}
			}
		}
		return ids;
	}

    public static boolean isValidEmail(String email) {
        String mailPattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        return Pattern.compile(mailPattern).matcher(email).matches();
    }
             
    public static LocalDate convertStringDateToLocalDate(String date) {
    	DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
    			.appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("dd/MMM/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("d/M/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("dd/M/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("MM/d/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    			.appendOptional(DateTimeFormatter.ofPattern("DD/MM/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("MM/DD/YYYY"))
    			.appendOptional(DateTimeFormatter.ofPattern("M/dd/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("M/dd/yyy"))
    			.appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyy"))
    			.appendOptional(DateTimeFormatter.ISO_LOCAL_DATE)
    			.toFormatter();
    	return LocalDate.parse(date, dateTimeFormatter);
    }

    public static String convertLocalDateToStringDate(LocalDate localDate) {
    	return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); 
    }
    
    public static LocalTime convertStringTimeToLocalTime(String time) {
    	DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
    			.appendOptional(DateTimeFormatter.ofPattern("hh:mm a"))
    			.appendOptional(DateTimeFormatter.ISO_LOCAL_TIME)
    			.toFormatter(Locale.ENGLISH);
    	return LocalTime.parse(time, dateTimeFormatter);	
    }

    public static String convertLocalTimeToStringTime(LocalTime localTime) {
    	return localTime.format(DateTimeFormatter.ofPattern("hh:mm a")).toUpperCase();
    }
    
    public static String convertLocalTimeToStringTime1(LocalTime localTime) {
    	return localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static LocalDateTime convertStringDateTimeToLocalDateTime(String date) {
    	DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
    			.appendOptional(DateTimeFormatter.ISO_DATE_TIME)
    			.appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"))
    			.appendOptional(DateTimeFormatter.ofPattern("dd/MMM/yyyy hh:mm a"))
    			.appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd K:mm"))
    			.toFormatter();
    	LocalDateTime localDateTime;
    	if(date.contains("Z")) {
    		localDateTime = ZonedDateTime.parse(date, dateTimeFormatter).withZoneSameInstant(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
    	}else {
    		localDateTime = LocalDateTime.parse(date,dateTimeFormatter);
    	}
    	return localDateTime;
    }

    public static String convertLocalDateTimeToStringDateTime(LocalDateTime localDateTime) {
    	return localDateTime.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy hh:mm a")).toUpperCase();  
    }	

    public static LocalDate  convertStringDateTimeToLocalDate(String dateTime) {  	
    	return (convertStringDateTimeToLocalDateTime(dateTime)).toLocalDate();
    }

    public static String convertLocalDateTimeToStringDate(LocalDateTime localDateTime) {
    	return localDateTime.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy"));
    }

    public static String convertLocalDateTimeToStringTime(LocalDateTime localDateTime) {
    	return localDateTime.format(DateTimeFormatter.ofPattern("hh:mm a")).toUpperCase();
    }
    
	  public static String convertLocalDateToStringDate1(LocalDate localDate) {
	    	return localDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
	    }
	  
	  public static String convertLocalDateToStringDate2(LocalDate localDate) {
	    	return localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	    }
	  
	  public static String convertLocalDateToStringDate3(LocalDate localDate) {
	    	return localDate.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy")); 
	    }
	  
	  public static String convertLocalDateToStringDate4(LocalDate localDate) {
	    	return localDate.format(DateTimeFormatter.ofPattern("dd MMMM")); 
	    }
	  
	  public static String convertLocalDateToStringDate5(LocalDate localDate) {
	    	return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")); 
	    }
	  
	public static int calculateSundaysForDateRange(LocalDate startDate, LocalDate endDate) {
		int noOfSunday = Stream.iterate(startDate, d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate) + 1).filter(localDate3 -> {
                    DayOfWeek d = localDate3.getDayOfWeek();
                    return  d == DayOfWeek.SUNDAY;
                }).collect(Collectors.toList()).size();
		System.out.println("sunday count1" +noOfSunday);
		return noOfSunday;
	}

	public static boolean checkISSunday(LocalDate localDate) {
		DayOfWeek d = localDate.getDayOfWeek();
		if (d == DayOfWeek.SUNDAY) {
			return true;
		} else {
			return false;
		}
	}
	
	//"Mon Nov 09 2015 00:00:00 GMT+0530 (India Standard Time)" to date
	public static LocalDate convertStringDateTimeToLocalDate1(String date) {
		DateTimeFormatter dtfInput = new DateTimeFormatterBuilder()
				.parseCaseInsensitive()
				.appendPattern("EEE MMM dd yyyy HH:mm:ss 'GMT' SSSS")
				.appendLiteral(" ")
				.appendZoneId()
				.appendPattern("X")
				.appendLiteral(" ")
				.appendLiteral("(")
				.appendGenericZoneText(TextStyle.FULL)
				.appendLiteral(')')
				.toFormatter(Locale.ENGLISH);
		ZonedDateTime zdt = ZonedDateTime.parse(date, dtfInput);
		OffsetDateTime odt = zdt.toOffsetDateTime();
		LocalDate localDate = odt.toLocalDate();
		return localDate;
	}
	
	public static LocalDateTime convertStringDateTimeToLocalDateTime1(String date) {
		DateTimeFormatter dtfInput = new DateTimeFormatterBuilder()
				.parseCaseInsensitive()
				.appendPattern("E MMM d uuuu H:m:s")
				.appendLiteral(" ")
				.appendZoneId()
				.appendPattern("X")
				.appendLiteral(" ")
				.appendLiteral("(")
				.appendGenericZoneText(TextStyle.FULL)
				.appendLiteral(')')
				.toFormatter(Locale.ENGLISH);
		ZonedDateTime zdt = ZonedDateTime.parse(date, dtfInput);
		OffsetDateTime odt = zdt.toOffsetDateTime();
        LocalDateTime localDateTime = odt.toLocalDateTime();
		return localDateTime;
	}
	public static String convertLocalDateTimeToStringDate1(LocalDateTime localDateTime) {
		return localDateTime.format(DateTimeFormatter.ofPattern("YYYY-MM-DD"));
	}	
	
	public static LocalDate convertStringDateToLocalDate1(String date) {
	    	DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
	    			.appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
	    			.appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
	    			.appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
	    			.appendOptional(DateTimeFormatter.ISO_LOCAL_DATE)
	    			.toFormatter();
	    	return LocalDate.parse(date, dateTimeFormatter);
	 }
	
	 public static String convertLocalDateToStringDate6(LocalDate localDate) {
	    return localDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")); 
	  }
  }

