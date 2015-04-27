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
import java.util.Hashtable;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.time.DateUtils;

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

	public static String convertToDateAndTime(final Timestamp date) {

		return Utility.dateTimeformat.format(date);

	}

	/**
	 * days betwewn date
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int daysBetweenDate(final Date date1, final Date date2) {

		final long time_m_1 = date1.getTime();
		final long time_m_2 = date2.getTime();

		final long diff = Math.abs(time_m_2 - time_m_1);

		final int ret = (int) diff / (1000 * 60 * 60 * 24);

		return ret;

	}

	/**
	 * Decimal to time
	 *
	 * @param source
	 * @return
	 */
	public static String decimatToTime(final Double source) {
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

		final String[] info = name.split(" ");
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

	public static Integer getWeekNumber(final Date date) {
		final Calendar cal = Calendar.getInstance(Locale.ITALIAN);
		cal.setTime(date);
		return cal.get(Calendar.WEEK_OF_YEAR);
	}

	public static Integer getWorkAmount(final Date date_from, final Date date_to, final Integer hours_per_week, final Integer hours_per_day) {

		// number of week from date_from to date_to
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date_from);

		final long diff = date_to.getTime() - date_from.getTime();

		final Integer numberOfMillsecInWeek = 7 * 24 * 60 * 60 * 1000;

		final Integer weekCount = (int) (diff / numberOfMillsecInWeek);

		final Integer numberOfMillsecInDay = 24 * 60 * 60 * 1000;

		final Integer dayCount = (int) ((diff - weekCount * numberOfMillsecInWeek) / (numberOfMillsecInDay));

		return weekCount * hours_per_week + dayCount * hours_per_day;
	}

	public static Integer getYear(final Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	// public static void main(final String args[]) {
	// final Calendar cal = Calendar.getInstance();
	//
	// final Date date_from = cal.getTime();
	//
	// cal.add(Calendar.DAY_OF_YEAR, 25);
	//
	// final Date date_to = cal.getTime();
	//
	// final Integer tot = Utility.getWorkAmount(date_from, date_to, 11, 2);
	//
	// }

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

			final Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
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
	public static double roundTwo(final Double x) {
		if (x == null) {
			return 0.0;
		}
		final double temp = Math.pow(10, 2);
		return Math.round(x.doubleValue() * temp) / temp;
	}
}
