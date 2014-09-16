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
import org.uario.seaworkengine.model.Scheduler;
import org.uario.seaworkengine.platform.persistence.dao.ISchedulerDAO;

public class MyBatisSchedulerDAO extends SqlSessionDaoSupport implements ISchedulerDAO {
	private static Logger	logger	= Logger.getLogger(MyBatisSchedulerDAO.class);

	@Override
	public List<Scheduler> selectSchedulers(final Date date_from, final Date date_to) {
		MyBatisSchedulerDAO.logger.info("selectSchedulers..");

		final HashMap<String, Date> map = new HashMap<String, Date>();
		map.put("date_from", date_from);
		map.put("date_to", date_to);

		final List<Scheduler> list = this.getSqlSession().selectList("scheduler.selectScheduler", map);
		return list;
	}

}
