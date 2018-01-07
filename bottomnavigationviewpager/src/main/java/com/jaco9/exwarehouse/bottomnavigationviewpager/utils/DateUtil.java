package com.jaco9.exwarehouse.bottomnavigationviewpager.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	/**
	 * 获取系统时间
	 * <p>
	 * Title:getSystemTime
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return date类型
	 * @throws ParseException
	 */
	
	public static Date getSystemDate(){
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date date=new Date();
		String sysDate=format.format(date);
		try {
			return format.parse(sysDate);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date getSystemTimeDate() {
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=new Date();
		String sysDate=format.format(date);
		try {
			return format.parse(sysDate);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date getSysDateTime(){
		return new Date();
	}
	
	public static Date getSysDate(){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String sysDate = format.format(date);
		try {
			return format.parse(sysDate);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date getSysTime(){
		DateFormat format = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		String sysDate = format.format(date);
		try {
			return format.parse(sysDate);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取系统时间
	 * <p>
	 * Title:getSystemTime
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return String类型
	 * @throws ParseException
	 */
	public static String getSystemTimeStr() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String sysDate = format.format(date);
		return sysDate;
	}
	
	public static String date2String(Date date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}
	
	public static Date str2Date(String dateStr, String format) {
		DateFormat df = new SimpleDateFormat(format);
		Date dateTime=null;
		try
		{
			dateTime=df.parse(dateStr);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateTime;
	}
}
