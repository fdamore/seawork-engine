package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import org.uario.seaworkengine.model.DetailSchedule;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;

public class MyBatisScheduleDAO extends SqlSessionDaoSupport implements ISchedule {
	private static Logger	logger	= Logger.getLogger(MyBatisScheduleDAO.class);

	// Schedule

	@Override
	public void createDetailSchedule(final DetailSchedule detail_schedule) {
		MyBatisScheduleDAO.logger.info("createDetail_Schedule");

		this.getSqlSession().insert("schedule.createDetail_Schedule", detail_schedule);

	}

	@Override
	public void createSchedule(final Schedule schedule) {
		MyBatisScheduleDAO.logger.info("createSchedule");

		this.getSqlSession().insert("schedule.createSchedule", schedule);

	}

	@Override
	public DetailSchedule loadDetailSchedule(final Integer id) {
		MyBatisScheduleDAO.logger.info("loadDetail_Schedule");

		return this.getSqlSession().selectOne("schedule.loadDetail_Schedule", id);
	}

	@Override
	public List<DetailSchedule> loadDetailScheduleByIdSchedule(final Integer id_schedule) {
		MyBatisScheduleDAO.logger.info("loadDetail_ScheduleByIdSchedule");

		return this.getSqlSession().selectList("schedule.loadDetail_ScheduleByIdSchedule", id_schedule);
	}

	@Override
	public Schedule loadSchedule(final Date date_schedule, final Integer id_user) {
		MyBatisScheduleDAO.logger.info("selectSchedulers..");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_schedule", date_schedule);
		map.put("id_user", id_user);

		final Schedule ret = this.getSqlSession().selectOne("schedule.selectScheduleByDateAndUser", map);
		return ret;
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
	public void removeDetailSchedule(final Integer id_detail_schedule) {
		MyBatisScheduleDAO.logger.info("removeDetail_Schedule");

		this.getSqlSession().delete("schedule.removeDetail_Schedule", id_detail_schedule);

	}

	@Override
	public void removeSchedule(final Integer id) {
		MyBatisScheduleDAO.logger.info("removeSchedule");

		this.getSqlSession().delete("schedule.removeSchedule", id);
	}

	@Override
	@Transactional
	public void saveListDetailScheduler(final Integer id_schedule, final List<DetailSchedule> details) {

		// delete all detail
		this.getSqlSession().delete("schedule.removeAllDetailScheduleOnSchedule", id_schedule);

		// add all details
		for (final DetailSchedule item_detail : details) {
			this.createDetailSchedule(item_detail);
		}

	}

	@Override
	public void saveOrUpdate(final Schedule currentSchedule) {
		if (currentSchedule.getId() == null) {
			// save
			this.getSqlSession().insert("schedule.createSchedule", currentSchedule);

		} else {
			this.getSqlSession().update("schedule.updateSchedule", currentSchedule);

		}

	}

	@Override
	public List<Schedule> selectSchedulers(final Date initial_date, final Date final_date) {
		MyBatisScheduleDAO.logger.info("selectSchedulers..");

		final HashMap<String, Date> map = new HashMap<String, Date>();
		map.put("date_from", initial_date);
		map.put("date_to", final_date);

		final List<Schedule> list = this.getSqlSession().selectList("schedule.selectSchedule", map);
		return list;
	}

	@Override
	public void updateDetailSchedule(final DetailSchedule detail_schedule) {
		MyBatisScheduleDAO.logger.info("updateDetailSchedule");

		this.getSqlSession().update("schedule.updateDetailSchedule", detail_schedule);
	}

	@Override
	public void updateSchedule(final Schedule schedule) {
		MyBatisScheduleDAO.logger.info("updateSchedule");

		this.getSqlSession().update("schedule.updateSchedule", schedule);

	}

}
