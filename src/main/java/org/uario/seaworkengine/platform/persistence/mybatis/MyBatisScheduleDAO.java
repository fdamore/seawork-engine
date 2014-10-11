package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;

public class MyBatisScheduleDAO extends SqlSessionDaoSupport implements ISchedule {
	private static Logger	logger	= Logger.getLogger(MyBatisScheduleDAO.class);

	@Override
	public void createSchedule(final Schedule schedule) {
		MyBatisScheduleDAO.logger.info("createSchedule");

		this.getSqlSession().insert("schedule.createSchedule", schedule);

	}

	@Override
	public Schedule loadSchedule(final Integer id_schedule) {
		MyBatisScheduleDAO.logger.info("loadSchedule");

		return this.getSqlSession().selectOne("schedule.loadSchedule", id_schedule);
	}

	@Override
	public List<Schedule> loadScheduleByDate(final Date date_schedule) {
		MyBatisScheduleDAO.logger.info("loadScheduleByDate");

		final List<Schedule> list_schedules = this.getSqlSession().selectList("schedule.loadScheduleByDate", date_schedule);
		return list_schedules;
	}

	@Override
	public void removeSchedule(final Integer id) {
		MyBatisScheduleDAO.logger.info("removeSchedule");

		this.getSqlSession().delete("schedule.removeSchedule", id);
	}

	@Override
	public void updateSchedule(final Schedule schedule) {
		MyBatisScheduleDAO.logger.info("updateSchedule");

		this.getSqlSession().update("schedule.updateSchedule", schedule);

	}

}
