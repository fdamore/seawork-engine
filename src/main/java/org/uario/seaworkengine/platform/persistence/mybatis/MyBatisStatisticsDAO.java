/*
 * MyBatisPersonDAO.java
 * Created on 09/mag/2012
 */
package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.Complaint;
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.ReviewShipWork;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.TerminalProductivity;
import org.uario.seaworkengine.platform.persistence.dao.IStatistics;
import org.uario.seaworkengine.statistics.IBankHolidays;
import org.uario.seaworkengine.statistics.RateShift;
import org.uario.seaworkengine.statistics.ReviewShipWorkAggregate;
import org.uario.seaworkengine.statistics.ShipOverview;
import org.uario.seaworkengine.statistics.ShipTotal;
import org.uario.seaworkengine.statistics.impl.MonitorData;

public class MyBatisStatisticsDAO extends SqlSessionDaoSupport implements IStatistics {

	private static Logger	logger	= Logger.getLogger(MyBatisStatisticsDAO.class);

	private IBankHolidays	bank_holiday;

	/**
	 * Calculate work percentage on list rateshift
	 *
	 * @param lists
	 * @return
	 */
	private RateShift[] calculateWorkPercentage(final List<RateShift> lists) {

		if (lists == null) {
			return null;
		}
		final RateShift[] ret = new RateShift[4];

		// signal
		boolean shift_1 = false;
		boolean shift_2 = false;
		boolean shift_3 = false;
		boolean shift_4 = false;

		int i = 0;
		for (; (i < lists.size()) && (i < 4); i++) {
			final RateShift averageShift = lists.get(i);

			ret[i] = averageShift;

			if (averageShift.getShift() == 1) {
				shift_1 = true;
			} else if (averageShift.getShift() == 2) {
				shift_2 = true;
			} else if (averageShift.getShift() == 3) {
				shift_3 = true;
			} else if (averageShift.getShift() == 4) {
				shift_4 = true;
			}

		}

		for (; i < 4; i++) {
			final RateShift averageShift = new RateShift();
			averageShift.setRate(0.0);

			if (!shift_1) {
				averageShift.setShift(1);
				shift_1 = true;
			} else if (!shift_2) {
				averageShift.setShift(2);
				shift_2 = true;
			} else if (!shift_3) {
				averageShift.setShift(3);
				shift_3 = true;
			} else if (!shift_4) {
				averageShift.setShift(4);
				shift_4 = true;
			}

			ret[i] = averageShift;

		}

		// sort array
		Arrays.sort(ret);

		return ret;

	}

	@Override
	public HashMap<Integer, ShipTotal> countComplaintByCustomer(final Integer year, final Integer id_customer) {
		MyBatisStatisticsDAO.logger.info("countComplaintByCustomer..");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("year", year);
		map.put("id_customer", id_customer);

		final List<ShipTotal> list = this.getSqlSession().selectList("statistics.countComplaintByCustomer", map);

		if (list == null) {
			return null;
		}

		final HashMap<Integer, ShipTotal> shipTotal = new HashMap<>();
		for (final ShipTotal sT : list) {
			shipTotal.put(sT.getMonth_date(), sT);
		}

		return shipTotal;
	}

	@Override
	public void createComplaint(final Complaint complaint) {
		MyBatisStatisticsDAO.logger.info("createComplaint..");

		this.getSqlSession().insert("statistics.createComplaint", complaint);

	}

	@Override
	public void createTerminalProductivity(final TerminalProductivity terminalProductivity) {
		MyBatisStatisticsDAO.logger.info("createTerminalProductivity");
		this.getSqlSession().insert("statistics.createTerminalProductivity", terminalProductivity);
	}

	/**
	 * @param user_id
	 * @param date_from
	 * @param date_to
	 * @return
	 */
	@Override
	public Integer daysToRemoveFromSaturation(final Integer user_id, final Date date_from, final Date date_to) {

		final HashMap<String, Object> map = new HashMap<>();
		map.put("user_id", user_id);
		map.put("date_from", date_from);
		map.put("date_to", date_to);

		Integer ret = this.getSqlSession().selectOne("statistics.daysToRemoveFromSaturation", map);

		if (ret == 0) {
			ret = 0;
		}

		return ret;

	}

	@Override
	public void deleteComplaint(final Integer id) {
		MyBatisStatisticsDAO.logger.info("deleteComplaint");
		this.getSqlSession().delete("statistics.deleteComplaint", id);
	}

	@Override
	public void deleteTerminalProductivity(final Integer id) {
		MyBatisStatisticsDAO.logger.info("deleteTerminalProductivity");
		this.getSqlSession().delete("statistics.deleteTerminalProductivity", id);
	}

	@Override
	public RateShift[] getAverageForShift(final Integer user, final Date date, final Date date_from) {
		MyBatisStatisticsDAO.logger.info("getAverageForShift..");

		// truncate date
		final Date date_truncate = DateUtils.truncate(date, Calendar.DATE);
		final Date date_from_truncate = DateUtils.truncate(date_from, Calendar.DATE);

		final HashMap<String, Object> map = new HashMap<>();
		map.put("id_user", user);
		map.put("date_schedule", date_truncate);
		map.put("date_from", date_from_truncate);

		List<RateShift> lists = null;

		lists = this.getSqlSession().selectList("statistics.selectRateShiftByUserReviewd", map);
		if (lists == null) {
			lists = this.getSqlSession().selectList("statistics.selectRateShiftByUserProgram", map);
		}

		return this.calculateWorkPercentage(lists);

	}

	@Override
	public RateShift[] getAverageForShiftOnProgram(final Integer user, final Date date, final Date date_from) {
		MyBatisStatisticsDAO.logger.info("getAverageForShiftOnProgram..");

		// truncate date
		final Date date_truncate = DateUtils.truncate(date, Calendar.DATE);
		final Date date_from_truncate = DateUtils.truncate(date_from, Calendar.DATE);

		final HashMap<String, Object> map = new HashMap<>();
		map.put("id_user", user);
		map.put("date_schedule", date_truncate);
		map.put("date_from", date_from_truncate);

		List<RateShift> lists = null;

		lists = this.getSqlSession().selectList("statistics.selectRateShiftByUserProgram", map);

		return this.calculateWorkPercentage(lists);

	}

	public IBankHolidays getBank_holiday() {
		return this.bank_holiday;
	}

	@Override
	public RateShift[] getCountSundayForShift(final Integer user, final Date date, final Date date_from) {
		MyBatisStatisticsDAO.logger.info("getCountSundayForShift..");

		// truncate date
		final Date date_to_truncate = DateUtils.truncate(date, Calendar.DATE);
		final Date date_from_truncate = DateUtils.truncate(date_from, Calendar.DATE);

		final HashMap<String, Object> map = new HashMap<>();
		map.put("id_user", user);
		map.put("date_from", date_from_truncate);
		map.put("date_to", date_to_truncate);

		List<RateShift> lists = null;

		lists = this.getSqlSession().selectList("statistics.selectShiftSundayWorkCountReview", map);
		if (lists == null) {
			lists = this.getSqlSession().selectList("statistics.selectShiftSundayWorkCountProgram", map);
		}

		return this.calculateWorkPercentage(lists);
	}

	@Override
	public List<Date> getDateAtWork(final Integer id_user, final Date date_from, final Date date_to) {
		MyBatisStatisticsDAO.logger.info("loadTFRByUser..");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("id_user", id_user);
		map.put("date_schedule_from", DateUtils.truncate(date_from, Calendar.DATE));
		map.put("date_schedule_to", DateUtils.truncate(date_to, Calendar.DATE));

		final List<Date> ret = this.getSqlSession().selectList("statistics.dateAtWork", map);

		return ret;

	}

	@Override
	public Date getDatesBreak(final Integer id_user, final Date date_from, final Date date_to) {
		MyBatisStatisticsDAO.logger.info("getDatesBreak");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("id_user", id_user);
		map.put("date_schedule_from", DateUtils.truncate(date_from, Calendar.DATE));
		map.put("date_schedule_to", DateUtils.truncate(date_to, Calendar.DATE));

		return this.getSqlSession().selectOne("statistics.datesBreak", map);

	}

	@Override
	public Integer getHolidaysWork(final Integer id_user, final Date date_from, final Date date_to) {
		MyBatisStatisticsDAO.logger.info("getSundayAndHolidaysWorkPercentage..");

		// count holidays until now

		final Date date_from_truncate = DateUtils.truncate(date_from, Calendar.DATE);
		final Date date_to_truncate = DateUtils.truncate(date_to, Calendar.DATE);

		/*
		 * ITALY BANK HOLIDAYS '01-01' , '01-06', '04-25', '05-01', '06-02',
		 * '08-15', '11-01', '12-08', '12-25', '12-26', '08-13'
		 */

		final StringBuilder build = new StringBuilder();

		// define holiday string
		final List<String> holidays = this.bank_holiday.getDays();
		for (int i = 0; i < holidays.size(); i++) {
			final String item = holidays.get(i);
			build.append("'" + item + "'");
			if (i != (holidays.size() - 1)) {
				build.append(",");
			}
		}

		final HashMap<String, Object> map = new HashMap<>();
		map.put("id_user", id_user);
		map.put("date_from", date_from_truncate);
		map.put("date_to", date_to_truncate);
		map.put("days_hol", build.toString());

		final Integer ret = this.getSqlSession().selectOne("statistics.getSundayAndHolidaysWork", map);

		if (ret == null) {
			return 0;
		}

		return ret;
	}

	@Override
	public List<MonitorData> getMonitorData(final Date date_request) {
		MyBatisStatisticsDAO.logger.info("getMonitorData..");

		final Date date_from = DateUtils.truncate(date_request, Calendar.DATE);
		final Calendar date_to_cal = DateUtils.toCalendar(date_from);
		date_to_cal.add(Calendar.DATE, 1);

		final HashMap<String, Date> map = new HashMap<>();
		map.put("date_from", date_from);
		map.put("date_to", date_to_cal.getTime());

		final List<MonitorData> ret = this.getSqlSession().selectList("statistics.monitorWork", map);

		return ret;

	}

	@Override
	public List<ShipTotal> getShipNumber(final String operation, final Integer year, final Boolean by_invoice) {
		MyBatisStatisticsDAO.logger.info("getTotalInvoiceContainer...");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("year", year);
		map.put("operation", operation);

		if (by_invoice) {
			return this.getSqlSession().selectList("statistics.countShipByInvoce", map);
		} else {
			return this.getSqlSession().selectList("statistics.countShipByShiftDate", map);
		}
	}

	@Override
	public Integer getSundayWork(final Integer id_user, final Date date_from, final Date date_to) {
		MyBatisStatisticsDAO.logger.info("getSundayWorkPercentage..");

		final Date date_from_truncate = DateUtils.truncate(date_from, Calendar.DATE);
		final Date date_to_truncate = DateUtils.truncate(date_to, Calendar.DATE);

		final HashMap<String, Object> map = new HashMap<>();
		map.put("id_user", id_user);
		map.put("date_from", date_from_truncate);
		map.put("date_to", date_to_truncate);

		final Integer ret = this.getSqlSession().selectOne("statistics.selectSundayWork", map);

		if (ret == 0) {
			return 0;
		}

		return ret;

	}

	@Override
	public Double getTimeWorkCountByUser(final Integer user, final Date date_from, final Date date_to) {
		MyBatisStatisticsDAO.logger.info("getWorkCountByUser..");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("id_user", user);
		map.put("date_from", date_from);
		map.put("date_to", date_to);

		final Double ret = this.getSqlSession().selectOne("statistics.getWorkTimeCountByUser", map);
		if ((ret == null) || (ret == 0.0)) {
			return 0.0;
		}

		return ret;
	}

	@Override
	public Double getTimeWorked(final Integer id_user, final Date date_from, final Date date_to) {
		MyBatisStatisticsDAO.logger.info("loadTFRByUser..");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("id_user", id_user);
		map.put("date_schedule_from", DateUtils.truncate(date_from, Calendar.DATE));
		map.put("date_schedule_to", DateUtils.truncate(date_to, Calendar.DATE));

		final Double ret = this.getSqlSession().selectOne("statistics.timeWorkedReviewd", map);

		if (ret == null) {
			return 0.0;
		} else {
			return ret;
		}

	}

	@Override
	public List<ShipTotal> getTotalHandsMen(final Integer year, final Integer shift, final Integer idService,
			final Boolean by_invoice) {
		MyBatisStatisticsDAO.logger.info("getTotalHandsMen...");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("year", year);
		map.put("shift", shift);
		map.put("idService", idService);

		if (by_invoice) {
			return this.getSqlSession().selectList("statistics.getTotalHandsMenByInvoce", map);
		} else {
			return this.getSqlSession().selectList("statistics.getTotalHandsMenByShiftDate", map);
		}
	}

	@Override
	public List<ShipTotal> getTotalHoursByTask(final Integer year) {
		MyBatisStatisticsDAO.logger.info("getTotalHoursByTask...");

		return this.getSqlSession().selectList("statistics.getTotalHoursByTask", year);
	}

	@Override
	public List<ShipTotal> getTotalContainer(final Integer year, final Integer idService,
			final Boolean by_invoice) {
		MyBatisStatisticsDAO.logger.info("getTotalInvoiceContainer...");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("year", year);
		map.put("idService", idService);

		if (by_invoice) {
			return this.getSqlSession().selectList("statistics.getTotalInvoiceContainerByInvoce", map);
		} else {
			return this.getSqlSession().selectList("statistics.getTotalInvoiceContainerByShiftDate", map);
		}

	}

	@Override
	public List<DetailFinalSchedule> listDetailFinalSchedule(final String full_text_search, final Integer shift_number,
			final Integer shift_type, final Integer task_id, final Date date_from, final Date date_to,
			final Boolean reviewshift, final Integer idShip, final String craneId) {
		MyBatisStatisticsDAO.logger.info("listDetailFinalSchedule..");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("my_full_text_search", full_text_search);
		map.put("shift_number", shift_number);
		map.put("shift_type", shift_type);
		map.put("task_id", task_id);
		map.put("reviewshift", reviewshift);
		map.put("idShip", idShip);
		map.put("craneId", craneId);

		if ((date_from != null) && (date_to != null)) {
			map.put("date_from", DateUtils.truncate(date_from, Calendar.DATE));
			map.put("date_to", DateUtils.truncate(date_to, Calendar.DATE));
		}

		return this.getSqlSession().selectList("statistics.overviewFinalSchedule", map);
	}

	@Override
	public List<DetailInitialSchedule> listDetailInitialSchedule(final String full_text_search,
			final Integer shift_number, final Integer shift_type, final Integer task_id, final Date date_from,
			final Date date_to) {
		MyBatisStatisticsDAO.logger.info("listDetailFinalSchedule..");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("my_full_text_search", full_text_search);
		map.put("shift_number", shift_number);
		map.put("shift_type", shift_type);
		map.put("task_id", task_id);

		if ((date_from != null) && (date_to != null)) {
			map.put("date_from", DateUtils.truncate(date_from, Calendar.DATE));
			map.put("date_to", DateUtils.truncate(date_to, Calendar.DATE));
		}

		return this.getSqlSession().selectList("statistics.overviewInitalSchedule", map);
	}

	@Override
	public List<Schedule> listSchedule(final String full_text_search, final Integer shift, final Date date_from,
			final Date date_to) {
		MyBatisStatisticsDAO.logger.info("listSchedule..");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("my_full_text_search", full_text_search);
		map.put("shift_number", shift);

		if ((date_from != null) && (date_to != null)) {
			map.put("date_from", DateUtils.truncate(date_from, Calendar.DATE));
			map.put("date_to", DateUtils.truncate(date_to, Calendar.DATE));
		}

		return this.getSqlSession().selectList("statistics.listSchedule", map);
	}

	@Override
	public Complaint loadComplaint(final Integer idCustomer, final Integer year, final Integer month) {
		MyBatisStatisticsDAO.logger.info("listSchedule..");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("idCustomer", idCustomer);
		map.put("year", year);
		map.put("month", month);

		return this.getSqlSession().selectOne("statistics.loadComplaint", map);
	}

	@Override
	public Complaint loadComplaintById(final Integer id) {
		MyBatisStatisticsDAO.logger.info("loadComplaintById..");
		return this.getSqlSession().selectOne("statistics.loadComplaintById", id);
	}

	@Override
	public List<Complaint> loadComplaintByYear(final Integer year) {
		MyBatisStatisticsDAO.logger.info("loadComplaintByYear..");
		return this.getSqlSession().selectList("statistics.loadComplaintByYear", year);
	}

	@Override
	public List<ReviewShipWork> loadReviewShipWork(final Date date_from, final Date date_to, String searchText,
			final Integer rifSWS, final String rifMCT, final Integer shift, final Integer invoicing_cycle,
			final Integer idService, final String shipType, final String shipLine, final String shipCondition) {

		MyBatisStatisticsDAO.logger.info("loadReviewShipWork");

		Integer rif_sws_arg = rifSWS;
		String rif_mct_arg = rifMCT;
		if ((rifMCT != null) && rifMCT.equals("")) {
			rif_mct_arg = null;
		}
		if ((rifSWS != null) && rifSWS.equals("")) {
			rif_sws_arg = null;
		}

		Date dt_arg_from = null;
		if (date_from != null) {
			dt_arg_from = DateUtils.truncate(date_from, Calendar.DATE);
		}

		Date dt_arg_to = null;
		if (date_to != null) {
			dt_arg_to = DateUtils.truncate(date_to, Calendar.DATE);
		}

		if ((searchText != null) && (searchText.trim() == "")) {
			searchText = null;
		}

		final HashMap<String, Object> map = new HashMap<>();
		map.put("dt_arg_from", dt_arg_from);
		map.put("dt_arg_to", dt_arg_to);
		map.put("searchText", searchText);
		map.put("rifSWS", rif_sws_arg);
		map.put("rifMCT", rif_mct_arg);
		map.put("shift", shift);
		map.put("invoicing_cycle_search", invoicing_cycle);
		map.put("idService", idService);
		map.put("shipType", shipType);
		map.put("shipLine", shipLine);
		map.put("shipCondition", shipCondition);

		return this.getSqlSession().selectList("statistics.reviewShipWork", map);

	}

	@Override
	public List<ReviewShipWorkAggregate> loadReviewShipWorkAggregate(final Date date_from, final Date date_to,
			final Integer rifSWS, final String rifMCT, final Integer working_cycle, String searchText,
			final Integer serviceId, final String shipType, final String shipLine, final String shipCondition) {

		MyBatisStatisticsDAO.logger.info("loadReviewShipWorkAggregate by date");

		Date dt_arg_from = null;
		if (date_from != null) {
			dt_arg_from = DateUtils.truncate(date_from, Calendar.DATE);
		}

		Date dt_arg_to = null;
		if (date_to != null) {
			dt_arg_to = DateUtils.truncate(date_to, Calendar.DATE);
		}

		if ((searchText != null) && (searchText.trim() == "")) {
			searchText = null;
		}

		final HashMap<String, Object> map = new HashMap<>();
		map.put("dt_arg_from", dt_arg_from);
		map.put("dt_arg_to", dt_arg_to);
		map.put("searchText", searchText);
		map.put("rifSWS", rifSWS);
		map.put("rifMCT", rifMCT);
		map.put("working_cycle", working_cycle);
		map.put("serviceId", serviceId);
		map.put("shipType", shipType);
		map.put("shipLine", shipLine);
		map.put("shipCondition", shipCondition);

		return this.getSqlSession().selectList("statistics.reviewShipWorkAggregate", map);
	}

	@Override
	public TerminalProductivity loadTerminalProductivity(final Integer id) {
		MyBatisStatisticsDAO.logger.info("loadTerminalProductivity");
		return this.getSqlSession().selectOne("statistics.loadTerminalProductivity", id);
	}

	@Override
	public List<TerminalProductivity> loadTerminalProductivityYear(final Integer year) {
		MyBatisStatisticsDAO.logger.info("loadTerminalProductivity");
		return this.getSqlSession().selectList("statistics.loadTerminalProductivityYear", year);
	}

	@Override
	public List<ShipOverview> overviewFinalScheduleByShip(final String text_search, final Date date_from,
			final Date date_to, final String shipType, final String shipLine, final String shipCondition) {
		MyBatisStatisticsDAO.logger.info("listDetailFinalSchedule..");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("shipname", text_search);

		if ((date_from != null) && (date_to != null)) {
			map.put("date_from", DateUtils.truncate(date_from, Calendar.DATE));
			map.put("date_to", DateUtils.truncate(date_to, Calendar.DATE));
		}

		map.put("shipType", shipType);
		map.put("shipLine", shipLine);
		map.put("shipCondition", shipCondition);

		return this.getSqlSession().selectList("statistics.overviewFinalScheduleByShip", map);
	}

	public void setBank_holiday(final IBankHolidays bank_holiday) {
		this.bank_holiday = bank_holiday;
	}

	@Override
	public void updateComplaint(final Complaint complaint) {
		MyBatisStatisticsDAO.logger.info("updateComplaint");
		this.getSqlSession().update("statistics.updateComplaint", complaint);

	}

	@Override
	public void updateTerminalProductivity(final TerminalProductivity terminalProductivity) {
		MyBatisStatisticsDAO.logger.info("updateTerminalProductivity");
		this.getSqlSession().update("statistics.updateTerminalProductivity", terminalProductivity);
	}

}
