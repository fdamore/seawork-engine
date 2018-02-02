package org.uario.seaworkengine.utility;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.statistics.IBankHolidays;
import org.uario.seaworkengine.statistics.ShipTotal;
import org.zkoss.spring.SpringUtil;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * Utility methods for GeoInt....
 *
 * @author francesco
 *
 */
public class Utility {

	private static SimpleDateFormat			dateFormat		= new SimpleDateFormat("yyyy-MM-dd");

	private static SimpleDateFormat			dateFormat_it	= new SimpleDateFormat("dd-MM-yyyy");

	private static SimpleDateFormat			dateTimeformat	= new SimpleDateFormat("dd-MM-yyyy HH:mm");
	private static final SimpleDateFormat	formatDay		= new SimpleDateFormat("EEE", Locale.ITALIAN);

	private static SimpleDateFormat			timeFormat		= new SimpleDateFormat("HH:mm");

	public static String convertToDateAndTime(final Date date) {
		return Utility.dateTimeformat.format(date);
	}

	public static String convertToDateAndTime(final Timestamp date) {

		return Utility.dateTimeformat.format(date);

	}

	/**
	 * Decimal to time
	 *
	 * @param source
	 * @return
	 */
	public static String decimatToTime(final Double source) {
		if (source == null) {
			return "";
		}
		final int hours = (int) source.doubleValue();
		final double decimal = source - hours;
		final int minuts = (int) Utility.roundOne(decimal * 60);

		return "" + hours + "h " + minuts + "m";
	}

	/**
	 * Return dotted name
	 *
	 * @param name
	 * @return
	 */
	public static final String dottedName(final String name) {

		if (name == null) {
			return "";
		}

		final String def_string = name.replaceAll("\\s+", "__");

		final String[] info = def_string.split("__");
		if (info.length >= 2) {

			String surname = info[0];
			String info_name = info[1];

			if ((surname.length() == 2) && (info.length >= 3)) {
				surname = info[0] + " " + info[1];
				info_name = info[2];
			}

			return surname + " " + info_name.toCharArray()[0] + ".";

		} else {
			return name;
		}
	}

	/**
	 * Encode string on sha
	 *
	 * @param str
	 * @param salt
	 *            salting parameter
	 * @return
	 */
	public static String encodeSHA256(final String str, final String salt) {
		final String saltedPassword = str + "{" + salt + "}";

		final String digest = DigestUtils.sha256Hex(saltedPassword);
		return digest;
	}

	public final static Date findAfterEaster(final int year) {
		final Date easter = Utility.findEaster(year);
		final Calendar calendar_easter = DateUtils.toCalendar(easter);
		calendar_easter.add(Calendar.DAY_OF_YEAR, 1);
		return calendar_easter.getTime();
	}

	/**
	 * Find easter for year
	 *
	 * @param year
	 * @return
	 */
	public final static Date findEaster(final int year) {

		if ((year < 1573) || (year > 2499)) {
			return null;
		}

		final int a = year % 19;
		final int b = year % 4;
		final int c = year % 7;

		int m = 0;
		int n = 0;

		if ((year >= 1583) && (year <= 1699)) {
			m = 22;
			n = 2;
		}
		if ((year >= 1700) && (year <= 1799)) {
			m = 23;
			n = 3;
		}
		if ((year >= 1800) && (year <= 1899)) {
			m = 23;
			n = 4;
		}
		if ((year >= 1900) && (year <= 2099)) {
			m = 24;
			n = 5;
		}
		if ((year >= 2100) && (year <= 2199)) {
			m = 24;
			n = 6;
		}
		if ((year >= 2200) && (year <= 2299)) {
			m = 25;
			n = 0;
		}
		if ((year >= 2300) && (year <= 2399)) {
			m = 26;
			n = 1;
		}
		if ((year >= 2400) && (year <= 2499)) {
			m = 25;
			n = 1;
		}

		final int d = ((19 * a) + m) % 30;
		final int e = ((2 * b) + (4 * c) + (6 * d) + n) % 7;

		final Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.YEAR, year);

		if ((d + e) < 10) {
			calendar.set(Calendar.MONTH, Calendar.MARCH);
			calendar.set(Calendar.DAY_OF_MONTH, d + e + 22);
		} else {
			calendar.set(Calendar.MONTH, Calendar.APRIL);
			int day = (d + e) - 9;
			if (26 == day) {
				day = 19;
			}
			if ((25 == day) && (28 == d) && (e == 6) && (a > 10)) {
				day = 18;
			}
			calendar.set(Calendar.DAY_OF_MONTH, day);
		}

		return calendar.getTime();
	}

	public static String getDataAsString(final Date date) {
		if (date == null) {
			return "";
		}
		return Utility.getDateFormat().format(date);
	}

	public static String getDataAsString_it(final Date date) {
		if (date == null) {
			return "";
		}
		return Utility.dateFormat_it.format(date);
	}

	/**
	 * Return the default date format
	 *
	 * @return
	 */
	public static SimpleDateFormat getDateFormat() {
		return Utility.dateFormat;
	}

	/**
	 * Return the default date format
	 *
	 * @return
	 */
	public static SimpleDateFormat getDateFormatIT() {
		return Utility.dateFormat_it;
	}

	public static String getDay(final Date date) {

		return Utility.formatDay.format(date);
	}

	/**
	 * Get day between date
	 *
	 * @param date_to
	 * @param date_from
	 * @return
	 */
	public static int getDayBetweenDate(final Date date_from, final Date date_to) {

		if (date_from.after(date_to)) {
			return 0;
		}

		final DateTime dt_from = new DateTime(date_from);
		final DateTime dt_to = new DateTime(date_to);

		final Days days = Days.daysBetween(dt_from, dt_to);

		return days.getDays() + 1;
	}

	public static Integer getMonthNumber(final Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * Return the default time format
	 *
	 * @return
	 */
	public static SimpleDateFormat getTimeFormat() {
		return Utility.timeFormat;
	}

	/**
	 * Create type saturation
	 *
	 * @param sat
	 * @return the type of saturation
	 */
	public static String getTypeSaturation(final Double sat) {
		String type_sat = "";
		// set saturation style
		if (sat < 0) {
			type_sat = "REC";
		} else {
			type_sat = "OT";
		}
		return type_sat;
	}

	public static Integer getWeekNumber(final Date date) {
		final Calendar cal = Calendar.getInstance(Locale.ITALIAN);
		cal.setTime(date);
		return cal.get(Calendar.WEEK_OF_YEAR);
	}

	public static Integer getYear(final Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	/**
	 * Return if is holiday
	 *
	 * @param dt
	 * @return
	 */
	public static Boolean isHoliday(final Date dt) {

		// check if Sunday
		final Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
			return true;
		}

		final SimpleDateFormat format = new SimpleDateFormat("MM-dd");
		final String inp_info = format.format(dt);

		final IBankHolidays hld = (IBankHolidays) SpringUtil.getBean(BeansTag.BANK_HOLIDAYS);
		final List<String> list_h = hld.getDays();
		final boolean check = list_h.contains(inp_info);
		return check;
	}

	/**
	 * Return this this detail is refereed to working day
	 *
	 * @param detail
	 * @return
	 */
	public static Boolean isWorkingDay(final DetailFinalSchedule detail) {

		if (detail == null) {
			return Boolean.FALSE;
		}

		final Boolean shift_continous = detail.getContinueshift();

		if ((shift_continous != null) && shift_continous.booleanValue()) {
			return Boolean.FALSE;
		}

		final IShiftCache task_cache = (IShiftCache) SpringUtil.getBean(BeansTag.SHIFT_CACHE);
		final UserShift shift = task_cache.getUserShift(detail.getShift_type());

		if ((detail.getTime() == null) || detail.getTime().equals(0.0)) {
			return Boolean.FALSE;
		}

		// if not task assigned..
		if (shift == null) {
			return Boolean.TRUE;
		}

		if ((shift.getPresence() != null) && shift.getPresence()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}

	}

	/**
	 * Convert list total in map by month
	 *
	 * @param list_total
	 * @return
	 */
	public static HashMap<Integer, ShipTotal> mapShipTotal(final List<ShipTotal> list_total) {
		final HashMap<Integer, ShipTotal> ret = new HashMap<>();

		for (final ShipTotal itm : list_total) {
			if (ret.containsKey(itm.getMonthInvoice())) {
				continue;
			}
			ret.put(itm.getMonthInvoice(), itm);
		}

		return ret;

	}

	/**
	 * Generate output stream
	 *
	 * @param myCodeText
	 * @return
	 */
	public static ByteArrayOutputStream QRCodeGen(final String myCodeText) {
		try {

			final ByteArrayOutputStream ret = new ByteArrayOutputStream();

			final QRCodeWriter qrCodeWriter = new QRCodeWriter();
			final int size = 125;
			final String fileType = "png";

			final Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			final BitMatrix byteMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);

			final int CrunchifyWidth = byteMatrix.getWidth();
			final BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth, BufferedImage.TYPE_INT_RGB);
			image.createGraphics();

			final Graphics2D graphics = (Graphics2D) image.getGraphics();
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
			graphics.setColor(Color.BLACK);

			for (int i = 0; i < CrunchifyWidth; i++) {
				for (int j = 0; j < CrunchifyWidth; j++) {
					if (byteMatrix.get(i, j)) {
						graphics.fillRect(i, j, 1, 1);
					}
				}
			}
			ImageIO.write(image, fileType, ret);

			return ret;

		} catch (final WriterException e) {
			return null;
		} catch (final IOException e) {
			return null;
		}
	}

	/**
	 * Round to second decimal digit
	 *
	 * @param x
	 * @return
	 */
	public static double roundOne(final double x) {
		final double temp = Math.pow(10, 1);
		return Math.round(x * temp) / temp;
	}

	/**
	 * Round to second decimal digit
	 *
	 * @param x
	 * @return
	 */
	public static double roundSix(final Double x) {
		if (x == null) {
			return 0.0;
		}
		final double temp = Math.pow(10, 6);
		return Math.round(x.doubleValue() * temp) / temp;
	}

	/**
	 * Round to second decimal digit
	 *
	 * @param x
	 * @return
	 */
	public static double roundTwo(final Double x) {
		if (x == null) {
			return 0.0;
		}
		final double temp = Math.pow(10, 2);
		return Math.round(x.doubleValue() * temp) / temp;
	}

	/**
	 * sum_dpouble (multiple)
	 *
	 * @param d
	 * @return
	 */
	public static Double sum_double(final Double... d) {

		Double ret = 0.0;

		for (final Double element : d) {
			if (element == null) {
				continue;
			} else {
				ret += element;

			}
		}

		return ret;

	}

	/**
	 * sum double
	 *
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static Double sum_double(final Double d1, final Double d2) {

		if (d1 == null) {
			return d2;
		}

		if (d2 == null) {
			return d1;
		}

		return d1 + d2;

	}
}
