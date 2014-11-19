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
	public List<AverageShift> getAverageForShift(final Integer user, final Date date) {
		MyBatisStatisticsDAO.logger.info("loadTFRByUser..");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id_user", user);
		map.put("date_schedule", date);

		List<AverageShift> lists = null;

		lists = this.getSqlSession().selectList("statistics.selectAvgShiftByUserReviewd", map);
		if (lists == null) {
			lists = this.getSqlSession().selectList("statistics.selectAvgShiftByUserProgram", map);
		}

		return lists;
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
