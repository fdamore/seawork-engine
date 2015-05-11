package org.uario.seaworkengine.utility;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.ReviewShipWork;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.platform.persistence.cache.ITaskCache;
import org.uario.seaworkengine.statistics.UserStatistics;
import org.uario.seaworkengine.zkevent.converter.CraneTypeConverter;

public class UtilityCSV {

	private static final SimpleDateFormat	dayFormat			= new SimpleDateFormat("EEE", Locale.ITALIAN);

	private static final SimpleDateFormat	formatDateOverview	= new SimpleDateFormat("dd/MM/yyyy");

	private static final SimpleDateFormat	formatTimeOverview	= new SimpleDateFormat("dd/MM/yyyy hh:mm");

	private static final NumberFormat		number_format		= NumberFormat.getInstance(Locale.ITALIAN);

	public static StringBuilder downloadCSV_ReviewShipWork(final List<ReviewShipWork> reviewShipWorkList) {
		final StringBuilder builder = new StringBuilder();

		final String header = "Settimana;Giorno;Data;Nome Nave;Rif SWS;Rif MCT;Turno;Gru;Tempo Lavorato;Volumi Attesi;Volumi Rizz. da Bordo (x Cliente);Volumi Rizz. da Bordo (x SWS);Volumi TW MTC;Periodo di Lavorazione;\n";
		builder.append(header);

		final CraneTypeConverter craneConverter = new CraneTypeConverter();

		for (final ReviewShipWork item : reviewShipWorkList) {

			String week = "";
			String day = "";
			String date = "";
			String shipName = "";
			String rif_sws = "";
			String rif_mct = "";
			String shift = "";
			String crane = "";
			String workedTime = "";
			String volume = "";
			String volumeOnBoard = "";
			String volumeOnBoard_sws = "";
			String volumeTW = "";
			String inovoice_cycle = "";

			if (item.getDate_request() != null) {
				week = Utility.getWeekNumber(item.getDate_request()).toString();
			}

			if (item.getDate_request() != null) {
				day = Utility.getDay(item.getDate_request()).toString();
			}

			if (item.getDate_request() != null) {
				date = Utility.getDataAsString_it(item.getDate_request());
			}

			if (item.getShipname() != null) {
				shipName = item.getShipname();
			}

			if (item.getRif_sws() != null) {
				rif_sws = item.getRif_sws().toString();
			}

			if (item.getRif_mct() != null) {
				rif_mct = item.getRif_mct().toString();
			}

			if (item.getShift() != null) {
				shift = item.getShift().toString();
			}

			if ((item.getCrane() != null) && (item.getCrane_gtw() != null)) {
				final Boolean crane_gtw = item.getCrane_gtw();
				final String craneId = item.getCrane();

				crane = (String) craneConverter.defineCraneString(crane_gtw, craneId);
			}

			if (item.getTime_work() != null) {

				workedTime = Utility.decimatToTime(item.getTime_work());

			}

			if (item.getVolume() != null) {
				volume = item.getVolume().toString();
			}

			if (item.getVolumeunderboard() != null) {
				volumeOnBoard = item.getVolumeunderboard().toString();
			}

			if (item.getVolumeunderboard_sws() != null) {
				volumeOnBoard_sws = item.getVolumeunderboard_sws().toString();
			}

			if (item.getVolume_tw_mct() != null) {
				volumeTW = item.getVolume_tw_mct().toString();
			}

			if (item.getInvoicing_cycle() != null) {
				inovoice_cycle = "" + item.getInvoicing_cycle();
			}

			final String line = "" + week + ";" + day + ";" + date + ";" + shipName + ";" + rif_sws + ";" + rif_mct + ";" + shift + ";" + crane + ";"
					+ workedTime + ";" + volume + ";" + volumeOnBoard + ";" + volumeOnBoard_sws + ";" + volumeTW + ";" + inovoice_cycle + ";\n";
			builder.append(line);

		}

		return builder;
	}

	public static StringBuilder downloadCSVPreprocessing(final List<Schedule> listSchedule, final IShiftCache shift_cache) {
		final StringBuilder builder = new StringBuilder();
		final String header = "anno;mese;settimana;giorno;nome;matricola;data;turno;\n";
		builder.append(header);

		for (final Schedule item : listSchedule) {
			String date = "";
			String year = "";
			String weekDate = "";
			String day = "";
			String mouth = "";
			if (item.getDate_schedule() != null) {
				weekDate = (Utility.getWeekNumber(item.getDate_schedule())).toString();
				date = UtilityCSV.formatDateOverview.format(item.getDate_schedule());
				day = UtilityCSV.dayFormat.format(item.getDate_schedule());
				mouth = Utility.getMonthNumber(item.getDate_schedule()).toString();
				year = Utility.getYear(item.getDate_schedule()).toString();
			}

			String code_shift = "";
			final UserShift task = shift_cache.getUserShift(item.getShift());
			if (task != null) {
				code_shift = task.getCode();
			}

			String employee_identification = "";
			if ((item.getEmployee_identification() != null) && (item.getEmployee_identification().trim() != "")) {
				employee_identification = item.getEmployee_identification();
			}

			final String line = "" + year + ";" + mouth + ";" + weekDate + ";" + day + ";" + item.getName_user() + ";" + employee_identification
					+ ";" + date + ";" + code_shift + ";\n";
			builder.append(line);
		}
		return builder;
	}

	public static StringBuilder downloadCSVProgram(final List<DetailInitialSchedule> listDetailProgram, final ITaskCache task_cache,
			final IShiftCache shift_cache) {
		final StringBuilder builder = new StringBuilder();
		final String header = "anno;mese;settimana;giorno;nome;matricola;data;tipoturno;turno;mansione;ore;ore_chiusura;ingresso;uscita\n";
		builder.append(header);

		for (final DetailInitialSchedule item : listDetailProgram) {
			String date = "";
			String year = "";
			String weekDate = "";
			String mouth = "";
			String day = "";
			if (item.getDate_schedule() != null) {
				weekDate = (Utility.getWeekNumber(item.getDate_schedule())).toString();
				date = UtilityCSV.formatDateOverview.format(item.getDate_schedule());
				day = UtilityCSV.dayFormat.format(item.getDate_schedule());
				mouth = Utility.getMonthNumber(item.getDate_schedule()).toString();
				year = Utility.getYear(item.getDate_schedule()).toString();
			}

			String time_from = "";
			if (item.getTime_from() != null) {
				time_from = UtilityCSV.formatTimeOverview.format(item.getTime_from());
			}

			String time_to = "";
			if (item.getTime_to() != null) {
				time_to = UtilityCSV.formatTimeOverview.format(item.getTime_to());
			}

			String code_task = "";
			final UserTask task = task_cache.getUserTask(item.getTask());
			if (task != null) {
				code_task = task.getCode();
			}

			String code_shift = "";
			final UserShift shift_type = shift_cache.getUserShift(item.getShift_type());
			if (shift_type != null) {
				code_shift = shift_type.getCode();
			}

			Double time = item.getTime();
			if (time == null) {
				time = new Double(0.0);
			}
			final String time_info = UtilityCSV.number_format.format(time);

			Double time_vacation = item.getTime_vacation();
			if (time_vacation == null) {
				time_vacation = new Double(0.0);
			}
			final String time_vacation_info = UtilityCSV.number_format.format(time_vacation);

			final Integer shift_no = item.getShift();
			String shift_no_info = "";
			if (shift_no != null) {
				shift_no_info = shift_no.toString();
			}

			String employee_identification = "";
			if ((item.getEmployee_identification() != null) && (item.getEmployee_identification().trim() != "")) {
				employee_identification = item.getEmployee_identification();
			}

			final String line = "" + year + ";" + mouth + ";" + weekDate + ";" + day + ";" + item.getUser() + ";" + employee_identification + ";"
					+ date + ";" + mouth + ";" + weekDate + ";" + code_shift + ";" + shift_no_info + ";" + code_task + ";" + time_info + ";"
					+ time_vacation_info + ";" + time_from + ";" + time_to + ";\n";
			builder.append(line);
		}
		return builder;
	}

	public static StringBuilder downloadCSVReview(final List<DetailFinalSchedule> listDetailRevision, final ITaskCache task_cache,
			final IShiftCache shift_cache) {
		final StringBuilder builder = new StringBuilder();
		final String header = "anno;mese;settimana;giorno;nome;matricola;data;tipoturno;turno;mansione;ore;ore_chiusura;nome nave;crane;ingresso;uscita\n";
		builder.append(header);

		for (final DetailFinalSchedule item : listDetailRevision) {
			String date = "";
			String year = "";
			String mouth = "";
			String weekDate = "";
			String day = "";
			if (item.getDate_schedule() != null) {
				weekDate = (Utility.getWeekNumber(item.getDate_schedule())).toString();
				date = UtilityCSV.formatDateOverview.format(item.getDate_schedule());
				day = UtilityCSV.dayFormat.format(item.getDate_schedule());
				mouth = Utility.getMonthNumber(item.getDate_schedule()).toString();
				year = (Utility.getYear(item.getDate_schedule()).toString());
			}

			String time_from = "";
			if (item.getTime_from() != null) {
				time_from = UtilityCSV.formatTimeOverview.format(item.getTime_from());
			}

			String time_to = "";
			if (item.getTime_to() != null) {
				time_to = UtilityCSV.formatTimeOverview.format(item.getTime_to());
			}

			String code_task = "";
			final UserTask task = task_cache.getUserTask(item.getTask());
			if (task != null) {
				code_task = task.getCode();
			}

			String code_shift = "";
			final UserShift shift_type = shift_cache.getUserShift(item.getShift_type());
			if (shift_type != null) {
				code_shift = shift_type.getCode();
			}

			String nameShip = "";
			if (item.getNameShip() != null) {
				nameShip = item.getNameShip();
			}

			String crane = "";
			if (item.getCrane() != null) {
				crane = item.getCrane();
			}

			Double time = item.getTime();
			if (time == null) {
				time = new Double(0.0);
			}
			final String time_info = UtilityCSV.number_format.format(time);

			Double time_vacation = item.getTime_vacation();
			if (time_vacation == null) {
				time_vacation = new Double(0.0);
			}
			final String time_vacation_info = UtilityCSV.number_format.format(time_vacation);

			final Integer shift_no = item.getShift();
			String shift_no_info = "";
			if (shift_no != null) {
				shift_no_info = shift_no.toString();
			}

			String employee_identification = "";
			if ((item.getEmployee_identification() != null) && (item.getEmployee_identification().trim() != "")) {
				employee_identification = item.getEmployee_identification();
			}

			final String line = "" + year + ";" + mouth + ";" + weekDate + ";" + day + ";" + item.getUser() + ";" + employee_identification + ";"
					+ date + ";" + code_shift + ";" + shift_no_info + ";" + code_task + ";" + time_info + ";" + time_vacation_info + ";" + nameShip
					+ ";" + crane + ";" + time_from + ";" + time_to + ";\n";
			builder.append(line);
		}
		return builder;
	}

	public static StringBuilder downloadCSVStatistics(final List<UserStatistics> userStatisticsList) {
		final StringBuilder builder = new StringBuilder();

		final String header = "Nome;Saturazione;Ore Lavorate;Lavoro Domenicale;Festivi;Turno 1;Turno 2;Turno 3;Turno 4;\n";
		builder.append(header);

		for (final UserStatistics item : userStatisticsList) {

			String name = "";
			String saturation = "";
			String work_current = "";

			String work_sunday_perc = "";
			String work_holiday_perc = "";
			String shift_perc_1 = "";
			String shift_perc_2 = "";
			String shift_perc_3 = "";
			String shift_perc_4 = "";

			if (item.getPerson() != null) {
				final Person p = item.getPerson();
				name = p.getLastname() + " " + p.getFirstname();
			}

			if (item.getSaturation() != null) {
				final Double sat = Utility.roundTwo(item.getSaturation());

				if (sat < 0) {
					saturation = "REC " + Utility.roundTwo(Math.abs(sat));

				} else {
					saturation = "OT " + Utility.roundTwo(sat);
				}
			}

			if (item.getWork_current() != null) {
				work_current = item.getWork_current();
			}

			if (item.getWork_sunday_perc() != null) {
				work_sunday_perc = item.getWork_sunday_perc();
			}

			if (item.getWork_holiday_perc() != null) {
				work_holiday_perc = item.getWork_holiday_perc();
			}

			if (item.getShift_perc_1() != null) {
				shift_perc_1 = item.getShift_perc_1();
			}

			if (item.getShift_perc_2() != null) {
				shift_perc_2 = item.getShift_perc_2();
			}

			if (item.getShift_perc_3() != null) {
				shift_perc_3 = item.getShift_perc_3();
			}

			if (item.getShift_perc_4() != null) {
				shift_perc_4 = item.getShift_perc_4();
			}

			final String line = "" + name + ";" + saturation + ";" + work_current + ";" + work_sunday_perc + ";" + work_holiday_perc + ";"
					+ shift_perc_1 + ";" + shift_perc_2 + ";" + shift_perc_3 + ";" + shift_perc_4 + ";\n";

			builder.append(line);

		}

		return builder;
	}

}
