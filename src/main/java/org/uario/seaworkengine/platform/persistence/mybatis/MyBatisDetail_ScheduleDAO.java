package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.Detail_Schedule;
import org.uario.seaworkengine.platform.persistence.dao.IDetail_Schedule;

public class MyBatisDetail_ScheduleDAO extends SqlSessionDaoSupport implements IDetail_Schedule {
	private static Logger	logger	= Logger.getLogger(MyBatisDetail_ScheduleDAO.class);

	@Override
	public void createDetail_Schedule(final Detail_Schedule detail_schedule) {
		MyBatisDetail_ScheduleDAO.logger.info("createDetail_Schedule");

		this.getSqlSession().insert("detail_schedule.createDetail_Schedule", detail_schedule);

	}

	@Override
	public Detail_Schedule loadDetail_Schedule(final Integer id) {
		MyBatisDetail_ScheduleDAO.logger.info("loadDetail_Schedule");

		return this.getSqlSession().selectOne("detail_schedule.loadDetail_Schedule", id);
	}

	@Override
	public List<Detail_Schedule> loadDetail_ScheduleByIdSchedule(final Integer id_schedule) {
		MyBatisDetail_ScheduleDAO.logger.info("loadDetail_ScheduleByIdSchedule");

		return this.getSqlSession().selectList("detail_schedule.loadDetail_ScheduleByIdSchedule", id_schedule);
	}

	@Override
	public void removeDetail_Schedule(final Integer id_detail_schedule) {
		MyBatisDetail_ScheduleDAO.logger.info("removeDetail_Schedule");

		this.getSqlSession().delete("detail_schedule.removeDetail_Schedule", id_detail_schedule);

	}

	@Override
	public void updateDetailSchedule(final Detail_Schedule detail_schedule) {
		MyBatisDetail_ScheduleDAO.logger.info("updateDetailSchedule");

		this.getSqlSession().update("detail_schedule.updateDetailSchedule", detail_schedule);
	}

}
