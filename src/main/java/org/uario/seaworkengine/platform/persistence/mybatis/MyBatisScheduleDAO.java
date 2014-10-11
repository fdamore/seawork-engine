package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.Detail_Schedule;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;

public class MyBatisScheduleDAO extends SqlSessionDaoSupport implements ISchedule {
	private static Logger	logger	= Logger.getLogger(MyBatisScheduleDAO.class);

	// Schedule

	@Override
	public void createDetail_Schedule(final Detail_Schedule detail_schedule) {
		MyBatisScheduleDAO.logger.info("createDetail_Schedule");

		this.getSqlSession().insert("schedule.createDetail_Schedule", detail_schedule);

	}

	@Override
	public void createSchedule(final Schedule schedule) {
		MyBatisScheduleDAO.logger.info("createSchedule");

		this.getSqlSession().insert("schedule.createSchedule", schedule);

	}

	@Override
	public Detail_Schedule loadDetail_Schedule(final Integer id) {
		MyBatisScheduleDAO.logger.info("loadDetail_Schedule");

		return this.getSqlSession().selectOne("schedule.loadDetail_Schedule", id);
	}

	@Override
	public List<Detail_Schedule> loadDetail_ScheduleByIdSchedule(final Integer id_schedule) {
		MyBatisScheduleDAO.logger.info("loadDetail_ScheduleByIdSchedule");

		return this.getSqlSession().selectList("schedule.loadDetail_ScheduleByIdSchedule", id_schedule);
	}

	@Override
	public Schedule loadSchedule(final Integer id_schedule) {
		MyBatisScheduleDAO.logger.info("loadSchedule");

		return this.getSqlSession().selectOne("schedule.loadSchedule", id_schedule);
	}

	// Detail_Schedule

	@Override
	public List<Schedule> loadScheduleByDate(final Date date_schedule) {
		MyBatisScheduleDAO.logger.info("loadScheduleByDate");

		final List<Schedule> list_schedules = this.getSqlSession().selectList("schedule.loadScheduleByDate", date_schedule);
		return list_schedules;
	}

	@Override
	public void removeDetail_Schedule(final Integer id_detail_schedule) {
		MyBatisScheduleDAO.logger.info("removeDetail_Schedule");

		this.getSqlSession().delete("schedule.removeDetail_Schedule", id_detail_schedule);

	}

	@Override
	public void removeSchedule(final Integer id) {
		MyBatisScheduleDAO.logger.info("removeSchedule");

		this.getSqlSession().delete("schedule.removeSchedule", id);
	}

	@Override
	public void updateDetailSchedule(final Detail_Schedule detail_schedule) {
		MyBatisScheduleDAO.logger.info("updateDetailSchedule");

		this.getSqlSession().update("schedule.updateDetailSchedule", detail_schedule);
	}

	@Override
	public void updateSchedule(final Schedule schedule) {
		MyBatisScheduleDAO.logger.info("updateSchedule");

		this.getSqlSession().update("schedule.updateSchedule", schedule);

	}

}
