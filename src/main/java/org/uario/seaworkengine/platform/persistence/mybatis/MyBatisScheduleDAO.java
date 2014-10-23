package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import org.uario.seaworkengine.model.DaySchedule;
import org.uario.seaworkengine.model.DetailSchedule;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;

public class MyBatisScheduleDAO extends SqlSessionDaoSupport implements ISchedule {
	private static Logger	logger	= Logger.getLogger(MyBatisScheduleDAO.class);

	// Schedule

	@Override
	public void createDaySchedule(final DaySchedule daySchedule) {
		MyBatisScheduleDAO.logger.info("createDaySchedule");

		this.getSqlSession().insert("schedule.createDaySchedule", daySchedule);

	}

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
	public DaySchedule loadDaySchedule(final Integer id) {
		MyBatisScheduleDAO.logger.info("loadDaySchedule");

		return this.getSqlSession().selectOne("schedule.loadDaySchedule", id);

	}

	@Override
	public void loadDaySchedule(final Integer id_user, final Date date_scheduled) {
		MyBatisScheduleDAO.logger.info("loadDaySchedule By id_user and date_scheduled");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("id_user", id_user.toString());
		map.put("date_scheduled", date_scheduled.toString());

		this.getSqlSession().selectOne("schedule.loadDayScheduleByIdUserShift", map);

	}

	@Override
	public DetailSchedule loadDetailSchedule(final Integer id) {
		MyBatisScheduleDAO.logger.info("loadDetail_Schedule");

		return this.getSqlSession().selectOne("schedule.loadDetail_Schedule", id);
	}

	@Override
	public List<DetailSchedule> loadDetailScheduleByIdScheduleAndShift(final Integer id_schedule, final Integer shift) {
		MyBatisScheduleDAO.logger.info("loadDetail_ScheduleByIdSchedule");

		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("id_schedule", id_schedule);
		map.put("shift", shift);

		return this.getSqlSession().selectList("schedule.loadDetail_ScheduleByIdSchedule", map);
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
	public void removeDaySchedule(final Integer id) {
		MyBatisScheduleDAO.logger.info("removeDaySchedule");

		this.getSqlSession().delete("schedule.removeDaySchedule", id);

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
	public void saveListDetailScheduler(final Integer id_schedule, final Integer shift, final List<DetailSchedule> details) {

		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("id_schedule", id_schedule);
		map.put("shift", shift);

		// delete all detail
		this.getSqlSession().delete("schedule.removeAllDetailScheduleOnSchedule", map);

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
	public void updateDaySchedule(final DaySchedule daySchedule) {
		MyBatisScheduleDAO.logger.info("updateDayScheduled");

		this.getSqlSession().update("schedule.updateDaySchedule", daySchedule);

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
