/*
 * MyBatisPersonDAO.java
 * Created on 09/mag/2012
 */
package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.platform.persistence.dao.IStatistics;
import org.uario.seaworkengine.statistics.AverageShift;

public class MyBatisStatisticsDAO extends SqlSessionDaoSupport implements IStatistics {
	private static Logger	logger	= Logger.getLogger(MyBatisStatisticsDAO.class);

	@Override
	public AverageShift[] getAverageForShift(final Integer user, final Date date) {
		MyBatisStatisticsDAO.logger.info("loadTFRByUser..");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id_user", user);
		map.put("date_schedule", date);

		List<AverageShift> lists = null;

		lists = this.getSqlSession().selectList("statistics.selectAvgShiftByUserReviewd", map);
		if (lists == null) {
			lists = this.getSqlSession().selectList("statistics.selectAvgShiftByUserProgram", map);
		}

		if (lists != null) {
			final AverageShift[] ret = new AverageShift[4];

			// signal
			boolean shift_1 = false;
			boolean shift_2 = false;
			boolean shift_3 = false;
			boolean shift_4 = false;

			int i = 0;
			for (; (i < lists.size()) && (i < 4); i++) {
				final AverageShift averageShift = lists.get(i);

				ret[i] = averageShift;

				if (averageShift.getShift() == 1) {
					shift_1 = true;
				}
				else
					if (averageShift.getShift() == 2) {
						shift_2 = true;
					}
					else
						if (averageShift.getShift() == 3) {
							shift_3 = true;
						}
						else
							if (averageShift.getShift() == 4) {
								shift_4 = true;
							}

			}

			for (; i < 4; i++) {
				final AverageShift averageShift = new AverageShift();
				averageShift.setAvg_program_time(0.0);

				if (!shift_1) {
					averageShift.setShift(1);
					shift_1 = true;
				}
				else
					if (!shift_2) {
						averageShift.setShift(2);
						shift_2 = true;
					}
					else
						if (!shift_3) {
							averageShift.setShift(3);
							shift_3 = true;
						}
						else
							if (!shift_4) {
								averageShift.setShift(4);
								shift_4 = true;
							}

				ret[i] = averageShift;

			}

			return ret;

		}
		else {
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

}
