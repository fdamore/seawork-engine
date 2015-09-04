package org.uario.seaworkengine.utility;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.uario.seaworkengine.model.Customer;
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.ReviewShipWork;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.ScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.platform.persistence.cache.IShipCache;
import org.uario.seaworkengine.platform.persistence.cache.ITaskCache;
import org.uario.seaworkengine.platform.persistence.dao.ICustomerDAO;
import org.uario.seaworkengine.statistics.ReviewShipWorkAggregate;
import org.uario.seaworkengine.statistics.ShipOverview;
import org.uario.seaworkengine.statistics.UserStatistics;
import org.uario.seaworkengine.zkevent.converter.CraneTypeConverter;
import org.uario.seaworkengine.zkevent.converter.ProductivityConverter;
import org.uario.seaworkengine.zkevent.converter.UserEnableConverter;
import org.uario.seaworkengine.zkevent.converter.UserRoleConverter;

public class UtilityCSV {

	private static final SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.ITALIAN);

	private static final SimpleDateFormat formatDateOverview = new SimpleDateFormat("dd/MM/yyyy");

	private static final SimpleDateFormat formatTimeOverview = new SimpleDateFormat("dd/MM/yyyy hh:mm");

	private static final NumberFormat number_format = NumberFormat.getInstance(Locale.ITALIAN);

	public static StringBuilder downloadCSV_DetailProgramShip(final List<DetailScheduleShip> modelListDetailScheduleShip,
			final ICustomerDAO customerDAO) {
		if (modelListDetailScheduleShip == null) {
			return null;
		}

		final StringBuilder builder = new StringBuilder();

		final String header = "Nome Nave;Cliente;Rif Cliente;Rif SWS;Data Turno;Turno;Operazione;Primo Preposto;Secondo Preposto;Lavorata;Mani P;Mani C;Persone P;Persone C;Data Inizio;Data Fine;\n";
		builder.append(header);

		for (final DetailScheduleShip item : modelListDetailScheduleShip) {

			String shipName = "";
			String customerName = "";
			String rif_mct = "";
			String rif_sws = "";
			String shiftDate = "";
			String shiftNumber = "";
			String operation = "";
			String firstUser = "";
			String secondUser = "";
			String worked = "NO";
			String hands_program = "";
			String hands_review = "";
			String persons_program = "";
			String persons_review = "";
			String startDate = "";
			String endDate = "";

			if (item.getName() != null) {
				shipName = item.getName();
			}

			if (item.getCustomer_id() != null) {
				final Customer customer = customerDAO.loadCustomer(item.getCustomer_id());
				if (customer != null) {
					customerName = customer.getName();
				}
			}

			if (item.getRif_mct() != null) {
				rif_mct = item.getRif_mct();
			}

			if (item.getIdscheduleship() != null) {
				rif_sws = item.getIdscheduleship().toString();
			}

			if (item.getShiftdate() != null) {
				shiftDate = Utility.getDataAsString_it(item.getShiftdate());
			}

			if (item.getShift() != null) {
				shiftNumber = item.getShift().toString();
			}

			if (item.getOperation() != null) {
				operation = item.getOperation();
			}

			if (item.getFirstOperativeName() != null) {
				firstUser = item.getFirstOperativeName();
			}

			if (item.getSecondOperativeName() != null) {
				secondUser = item.getSecondOperativeName();
			}

			if ((item.getWorked() != null) && item.getWorked()) {
				worked = "SI";
			}

			if (item.getHandswork_program() != null) {
				hands_program = item.getHandswork_program().toString();
			}

			if (item.getHandswork() != null) {
				hands_review = item.getHandswork().toString();
			}

			if (item.getMenwork() != null) {
				persons_review = item.getMenwork().toString();
			}

			if (item.getMenwork_program() != null) {
				persons_program = item.getMenwork_program().toString();
			}

			if (item.getArrivaldate() != null) {
				startDate = Utility.getDataAsString_it(item.getArrivaldate());
			}

			if (item.getDeparturedate() != null) {
				endDate = Utility.getDataAsString_it(item.getDeparturedate());
			}

			final String line = "" + shipName + ";" + customerName + ";" + rif_mct + ";" + rif_sws + ";" + shiftDate + ";" + shiftNumber + ";"
					+ operation + ";" + firstUser + ";" + secondUser + ";" + worked + ";" + hands_program + ";" + hands_review + ";"
					+ persons_program + ";" + persons_review + ";" + startDate + ";" + endDate + ";\n";
			builder.append(line);

		}

		return builder;
	}

	public static StringBuilder downloadCSV_PersonList(final List<Person> personList) {
		if (personList == null) {
			return null;
		}

		final StringBuilder builder = new StringBuilder();

		final String header = "Id utente;Rapporto Lavorativo;Livello Contrattuale;Abilitato;Nome;Città;Telefono;Ruolo;Qualifica;\n";
		builder.append(header);

		for (final Person item : personList) {

			String idUser = "";
			String status = "";
			String contractualLevel = "";
			String enabled = "";
			String name = "";
			String city = "";
			String tel = "";
			String role = "";
			String currentPosition = "";

			if (item.getId() != null) {
				idUser = item.getId().toString();
			}

			if (item.getStatus() != null) {
				status = item.getStatus();
			}

			if (item.getContractual_level() != null) {
				contractualLevel = item.getContractual_level().toString();
			}

			if (item.getCurrent_position() != null) {
				enabled = UserEnableConverter.converterValue(item.isEnabled());
			}

			if ((item.getFirstname() != null) && (item.getLastname() != null)) {
				name = item.getLastname() + " " + item.getFirstname();
			}

			if (item.getCity() != null) {
				city = item.getCity();
			}

			if (item.getPhone() != null) {
				tel = item.getPhone();
			}

			if (item.getAuthorities() != null) {
				role = UserRoleConverter.convertRole(item.getAuthorities().toString());
			}

			if (item.getCurrent_position() != null) {
				currentPosition = item.getCurrent_position();
			}

			final String line = "" + idUser + ";" + status + ";" + contractualLevel + ";" + enabled + ";" + name + ";" + city + ";" + tel + ";"
					+ role + ";" + currentPosition + ";\n";
			builder.append(line);

		}

		return builder;
	}

	public static StringBuilder downloadCSV_ScheduleProgramShip(final List<ScheduleShip> modelListScheduleShip, final ICustomerDAO customerDAO) {
		if (modelListScheduleShip == null) {
			return null;
		}
		final StringBuilder builder = new StringBuilder();

		final String header = "Data Inizio Attività;Data Fine Attività;Nome Nave;Cliente;Volume Atteso;Rif MCT;Rif SWS;Note Programmazione;\n";
		builder.append(header);

		for (final ScheduleShip item : modelListScheduleShip) {
			String startDate = "";
			String endDate = "";
			String shipName = "";
			String customerName = "";
			String volume = "";
			String mct = "";
			String sws = "";
			String note = "";

			if (item.getArrivaldate() != null) {
				startDate = Utility.getDataAsString_it(item.getArrivaldate());
			}

			if (item.getDeparturedate() != null) {
				endDate = Utility.getDataAsString_it(item.getDeparturedate());
			}

			if (item.getName() != null) {
				shipName = item.getName();
			}

			if (item.getCustomer_id() != null) {
				final Customer customer = customerDAO.loadCustomer(item.getCustomer_id());
				if (customer != null) {
					customerName = customer.getName();
				}
			}

			if (item.getVolume() != null) {
				volume = item.getVolume().toString();
			}

			if (item.getRif_mct() != null) {
				mct = item.getRif_mct().toString();
			}

			if (item.getId() != null) {
				sws = item.getId().toString();
			}

			if (item.getNote() != null) {
				note = item.getNote();
			}

			final String line = "" + startDate + ";" + endDate + ";" + shipName + ";" + customerName + ";" + volume + ";" + mct + ";" + sws + ";"
					+ note + ";\n";
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

			String time_info = "00:00";
			String time_vacation_info = "00:00";
			if ((item.getDateTo() != null) && (item.getDateFrom() != null)) {

				final Long milliseconds = item.getDateTo().getTime() - item.getDateFrom().getTime();

				if (!task.getIsabsence()) {

					time_info = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds), TimeUnit.MILLISECONDS.toMinutes(milliseconds)
							- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)));
				} else {

					time_vacation_info = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds),
							TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)));
				}
			}

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
			final IShiftCache shift_cache, final IShipCache ship_cache) {
		final StringBuilder builder = new StringBuilder();
		final String header = "anno;mese;settimana;giorno;nome;matricola;data;tipoturno;turno;mansione;ore;ore_chiusura;nome nave;crane;postazione;ingresso;uscita;consuntiva fascia oraria\n";
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
			if (item.getId_ship() != null) {

				final Ship ship = ship_cache.getShip(item.getId_ship());
				if (ship != null) {
					nameShip = ship.getName();
				} else {
					nameShip = "NULL";
				}

			}

			String crane = "";
			if (item.getCrane() != null) {
				crane = item.getCrane();
			}

			String board = "";
			if (item.getBoard() != null) {
				board = item.getBoard();
			}

			String time_info = "00:00";
			String time_vacation_info = "00:00";
			if ((item.getDateTo() != null) && (item.getDateFrom() != null)) {

				final Long milliseconds = item.getDateTo().getTime() - item.getDateFrom().getTime();

				if (!task.getIsabsence()) {

					time_info = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds), TimeUnit.MILLISECONDS.toMinutes(milliseconds)
							- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)));
				} else {

					time_vacation_info = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds),
							TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)));
				}
			}

			final Integer shift_no = item.getShift();
			String shift_no_info = "";
			if (shift_no != null) {
				shift_no_info = shift_no.toString();
			}

			String employee_identification = "";
			if ((item.getEmployee_identification() != null) && (item.getEmployee_identification().trim() != "")) {
				employee_identification = item.getEmployee_identification();
			}

			String reviewshift = "No";
			if ((item.getReviewshift() != null) && item.getReviewshift()) {
				reviewshift = "Si";
			}

			final String line = "" + year + ";" + mouth + ";" + weekDate + ";" + day + ";" + item.getUser() + ";" + employee_identification + ";"
					+ date + ";" + code_shift + ";" + shift_no_info + ";" + code_task + ";" + time_info + ";" + time_vacation_info + ";" + nameShip
					+ ";" + crane + ";" + board + ";" + time_from + ";" + time_to + ";" + reviewshift + ";\n";
			builder.append(line);
		}
		return builder;
	}

	public static StringBuilder downloadCSVReviewShipWork(final List<ReviewShipWork> reviewShipWorkList) {
		final StringBuilder builder = new StringBuilder();

		String header = "Settimana;Giorno;Data;Nome Nave;Rif SWS;Rif MCT;Turno;Gru;Tempo Lavorato;Volumi Attesi;";
		header = header + "Volumi Rizz. da Bordo (x Cliente);Volumi Rizz. da Bordo (x SWS);Volumi TW MTC;Periodo di Lavorazione;";
		header = header + "Cielo;Vento;Temperatura;Primo a Terra; Ultimo a Terra";
		header = header + "\n";

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

			String sky_item = "";
			String wind_item = "";
			String temperature_item = "";
			String date_first_down = "";
			String date_last_down = "";

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

			if (item.getSky() != null) {
				sky_item = "" + item.getSky();
			}

			if (item.getWind() != null) {
				wind_item = "" + item.getWind();
			}

			if (item.getTemperature() != null) {
				temperature_item = "" + item.getTemperature();
			}

			if (item.getFirst_down() != null) {
				date_first_down = "" + item.getFirst_down();
			}

			if (item.getLast_down() != null) {
				date_last_down = "" + item.getLast_down();
			}

			final String line = "" + week + ";" + day + ";" + date + ";" + shipName + ";" + rif_sws + ";" + rif_mct + ";" + shift + ";" + crane + ";"
					+ workedTime + ";" + volume + ";" + volumeOnBoard + ";" + volumeOnBoard_sws + ";" + volumeTW + ";" + inovoice_cycle + ";"
					+ sky_item + ";" + wind_item + ";" + temperature_item + ";" + date_first_down + ";" + date_last_down + ";" + "\n";
			builder.append(line);

		}

		return builder;
	}

	public static StringBuilder downloadCSVReviewShipWorkAggregate(final List<ReviewShipWorkAggregate> reviewShipWorkList) {
		final StringBuilder builder = new StringBuilder();

		final String header = "Nome Nave;Tempo Lavorato;Volumi Attesi;Produttività;Volumi Rizz. da Bordo (x Cliente);Volumi Rizz. da Bordo (x SWS);Volumi TW MTC;\n";
		builder.append(header);

		for (final ReviewShipWorkAggregate item : reviewShipWorkList) {

			String shipName = "";
			String workedTime = "";
			String volume = "";
			String productivity = "";
			String volumeOnBoard = "";
			String volumeOnBoard_sws = "";
			String volumeTW = "";

			if (item.getShipname() != null) {
				shipName = item.getShipname();
			}

			if (item.getTime_work() != null) {
				workedTime = Utility.decimatToTime(item.getTime_work());
			}

			if (item.getVolume() != null) {
				volume = item.getVolume().toString();
			}

			if ((item.getTime_work() != null) && (item.getVolume() != null)) {
				productivity = ProductivityConverter.getProductivity(item);
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

			final String line = shipName + ";" + workedTime + ";" + volume + ";" + productivity + ";" + volumeOnBoard + ";" + volumeOnBoard_sws + ";"
					+ volumeTW + ";\n";

			builder.append(line);

		}

		return builder;
	}

	public static StringBuilder downloadCSVShipStatistics(final List<ShipOverview> userStatisticsList) {
		final StringBuilder builder = new StringBuilder();

		final String header = "Nave;TempoLavorato;Saturazione;\n";
		builder.append(header);

		for (final ShipOverview item : userStatisticsList) {

			String name = "";
			String time = "";
			String saturation = "";

			name = item.getName();
			time = "" + Utility.roundTwo(item.getTime());
			saturation = "" + Utility.roundTwo(item.getTime_vacation());

			final String line = "" + name + ";" + time + ";" + saturation + ";\n";

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
