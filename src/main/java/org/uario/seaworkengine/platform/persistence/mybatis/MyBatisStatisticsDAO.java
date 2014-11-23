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
import org.uario.seaworkengine.platform.persistence.dao.IStatistics;
import org.uario.seaworkengine.statistics.RateShift;

public class MyBatisStatisticsDAO extends SqlSessionDaoSupport implements IStatistics {
	private static Logger	logger	= Logger.getLogger(MyBatisStatisticsDAO.class);

	@Override
	public RateShift[] getAverageForShift(final Integer user, final Date date) {
		MyBatisStatisticsDAO.logger.info("loadTFRByUser..");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id_user", user);
		map.put("date_schedule", date);

		List<RateShift> lists = null;

		lists = this.getSqlSession().selectList("statistics.selectRateShiftByUserReviewd", map);
		if (lists == null) {
			lists = this.getSqlSession().selectList("statistics.selectRateShiftByUserProgram", map);
		}

		if (lists != null) {
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

		} else {
			return null;
		}

	}

	@Override
	public Double getSundayWorkPercentage(final Integer id_user) {
		MyBatisStatisticsDAO.logger.info("loadTFRByUser..");

		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("id_user", id_user);

		final Double ret = this.getSqlSession().selectOne("statistics.selectPercentageSunday", map);

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
}
