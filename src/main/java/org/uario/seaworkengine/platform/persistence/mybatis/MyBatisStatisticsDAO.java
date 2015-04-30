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
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.platform.persistence.dao.IStatistics;
import org.uario.seaworkengine.statistics.IBankHolidays;
import org.uario.seaworkengine.statistics.RateShift;

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
	public RateShift[] getAverageForShift(final Integer user, final Date date, final Date date_from) {
		MyBatisStatisticsDAO.logger.info("getAverageForShift..");

		// truncate date
		final Date date_truncate = DateUtils.truncate(date, Calendar.DATE);
		final Date date_from_truncate = DateUtils.truncate(date_from, Calendar.DATE);

		final HashMap<String, Object> map = new HashMap<String, Object>();
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

		final HashMap<String, Object> map = new HashMap<String, Object>();
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
		final Date date_truncate = DateUtils.truncate(date, Calendar.DATE);
		final Date date_from_truncate = DateUtils.truncate(date_from, Calendar.DATE);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id_user", user);
		map.put("date_schedule", date_truncate);
		map.put("date_from", date_from_truncate);

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

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id_user", id_user);
		map.put("date_schedule_from", DateUtils.truncate(date_from, Calendar.DATE));
		map.put("date_schedule_to", DateUtils.truncate(date_to, Calendar.DATE));

		final List<Date> ret = this.getSqlSession().selectList("statistics.dateAtWork", map);

		return ret;

	}

	@Override
	public Date getDatesBreak(final Integer id_user, final Date date_from, final Date date_to) {
		MyBatisStatisticsDAO.logger.info("getDatesBreak");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id_user", id_user);
		map.put("date_schedule_from", DateUtils.truncate(date_from, Calendar.DATE));
		map.put("date_schedule_to", DateUtils.truncate(date_to, Calendar.DATE));

		return this.getSqlSession().selectOne("statistics.datesBreak", map);

	}

	@Override
	public Integer getHolidaysWork(final Integer id_user, final Date date_from) {
		MyBatisStatisticsDAO.logger.info("getSundayAndHolidaysWorkPercentage..");

		// count holidays until now

		final Date date_from_truncate = DateUtils.truncate(date_from, Calendar.DATE);

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

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id_user", id_user);
		map.put("date_from", date_from_truncate);
		map.put("days_hol", build.toString());

		final Integer ret = this.getSqlSession().selectOne("statistics.getSundayAndHolidaysWork", map);

		if (ret == null) {
			return 0;
		}

		return ret;
	}

	@Override
	public Integer getSundayWork(final Integer id_user, final Date date_from) {
		MyBatisStatisticsDAO.logger.info("getSundayWorkPercentage..");

		final Date date_from_truncate = DateUtils.truncate(date_from, Calendar.DATE);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id_user", id_user);
		map.put("date_from", date_from_truncate);

		final Integer ret = this.getSqlSession().selectOne("statistics.selectSundayWork", map);

		if (ret == 0) {
			return 0;
		}

		return ret;

	}

	@Override
	public Integer getTimeWorked(final Integer id_user, final Date date_from, final Date date_to) {
		MyBatisStatisticsDAO.logger.info("loadTFRByUser..");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id_user", id_user);
		map.put("date_schedule_from", DateUtils.truncate(date_from, Calendar.DATE));
		map.put("date_schedule_to", DateUtils.truncate(date_to, Calendar.DATE));

		final Integer ret = this.getSqlSession().selectOne("statistics.timeWorkedReviewd", map);

		if (ret != null) {
			return ret;
		} else {

			return this.getSqlSession().selectOne("statistics.timeWorkedProgram", map);
		}

	}

	@Override
	public Integer getWorkCountByUser(final Integer user, final Date date_from, final Date date_to) {
		MyBatisStatisticsDAO.logger.info("getWorkCountByUser..");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id_user", user);
		map.put("date_from", date_from);
		map.put("date_to", date_to);

		final Integer ret = this.getSqlSession().selectOne("statistics.getWorkCountByUser", map);
		if (ret == 0) {
			return 0;
		}

		return ret;
	}

	@Override
	public List<DetailFinalSchedule> listDetailFinalSchedule(final String full_text_search, final Integer shift_number, final Integer shift_type,
			final Integer task_id, final Date date_from, final Date date_to) {
		MyBatisStatisticsDAO.logger.info("listDetailFinalSchedule..");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("my_full_text_search", full_text_search);
		map.put("shift_number", shift_number);
		map.put("shift_type", shift_type);
		map.put("task_id", task_id);

		if ((date_from != null) && (date_to != null)) {
			map.put("date_from", DateUtils.truncate(date_from, Calendar.DATE));
			map.put("date_to", DateUtils.truncate(date_to, Calendar.DATE));
		}

		return this.getSqlSession().selectList("statistics.overviewFinalSchedule", map);
	}

	@Override
	public List<DetailInitialSchedule> listDetailInitialSchedule(final String full_text_search, final Integer shift_number, final Integer shift_type,
			final Integer task_id, final Date date_from, final Date date_to) {
		MyBatisStatisticsDAO.logger.info("listDetailFinalSchedule..");

		final HashMap<String, Object> map = new HashMap<String, Object>();
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
	public List<Schedule> listSchedule(final String full_text_search, final Integer shift, final Date date_from, final Date date_to) {
		MyBatisStatisticsDAO.logger.info("listSchedule..");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("my_full_text_search", full_text_search);
		map.put("shift_number", shift);

		if ((date_from != null) && (date_to != null)) {
			map.put("date_from", DateUtils.truncate(date_from, Calendar.DATE));
			map.put("date_to", DateUtils.truncate(date_to, Calendar.DATE));
		}

		return this.getSqlSession().selectList("statistics.listSchedule", map);
	}

	public void setBank_holiday(final IBankHolidays bank_holiday) {
		this.bank_holiday = bank_holiday;
	}

}
