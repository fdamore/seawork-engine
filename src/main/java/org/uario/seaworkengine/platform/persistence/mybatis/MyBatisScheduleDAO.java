package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import org.uario.seaworkengine.model.DaySchedule;
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;

public class MyBatisScheduleDAO extends SqlSessionDaoSupport implements ISchedule {
	private static Logger	logger	= Logger.getLogger(MyBatisScheduleDAO.class);

	@Override
	public void createDetailFinalSchedule(final DetailFinalSchedule detail_schedule) {
		MyBatisScheduleDAO.logger.info("createDetailFinalSchedule");

		this.getSqlSession().insert("schedule.createDetailFinalSchedule", detail_schedule);

	}

	@Override
	public void createDetailInitialSchedule(final DetailInitialSchedule detail_schedule) {
		MyBatisScheduleDAO.logger.info("createDetailInitialSchedule");

		this.getSqlSession().insert("schedule.createDetailInitialSchedule", detail_schedule);

	}

	@Override
	public List<DaySchedule> loadDaySchedule(final Date date_scheduled) {
		MyBatisScheduleDAO.logger.info("loadDayScheduleByDate");

		final Date dt_arg = DateUtils.truncate(date_scheduled, Calendar.DATE);

		final HashMap<String, Date> map = new HashMap<String, Date>();
		map.put("dt_arg", dt_arg);

		return this.getSqlSession().selectList("schedule.loadDayScheduleByDate", map);

	}

	@Override
	public List<DetailFinalSchedule> loadDetailFinalScheduleByIdSchedule(final Integer id_schedule) {
		MyBatisScheduleDAO.logger.info("loadDetailFinalScheduleByIdSchedule");

		return this.getSqlSession().selectList("schedule.loadDetailFinalScheduleByIdSchedule", id_schedule);
	}

	@Override
	public List<DetailFinalSchedule> loadDetailFinalScheduleByIdScheduleAndShift(final Integer id_schedule, final Integer shift) {
		MyBatisScheduleDAO.logger.info("loadDetailFinalScheduleByIdScheduleAndShift");

		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("id_schedule", id_schedule);
		map.put("shift", shift);

		return this.getSqlSession().selectList("schedule.loadDetailFinalScheduleByIdScheduleAndShift", map);
	}

	@Override
	public List<DetailInitialSchedule> loadDetailInitialScheduleByIdSchedule(final Integer id_schedule) {
		MyBatisScheduleDAO.logger.info("loadDetailInitialScheduleByIdSchedule");

		return this.getSqlSession().selectList("schedule.loadDetailInitialScheduleByIdSchedule", id_schedule);
	}

	@Override
	public List<DetailInitialSchedule> loadDetailInitialScheduleByIdScheduleAndShift(final Integer id_schedule, final Integer shift) {
		MyBatisScheduleDAO.logger.info("loadDetailInitialScheduleByIdScheduleAndShift");

		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("id_schedule", id_schedule);
		map.put("shift", shift);

		return this.getSqlSession().selectList("schedule.loadDetailInitialScheduleByIdScheduleAndShift", map);
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
	public void removeAllDetailFinalScheduleByScheduleAndShift(final Integer id_schedule, final Integer shift) {
		MyBatisScheduleDAO.logger.info("removeAllDetailFinalScheduleByScheduleAndShift");

		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("id_schedule", id_schedule);
		map.put("shift", shift);

		// delete all detail
		this.getSqlSession().delete("schedule.removeAllDetailFinalScheduleByScheduleAndShift", map);

	}

	@Override
	public void removeAllDetailInitialScheduleByScheduleAndShift(final Integer id_schedule, final Integer shift) {
		MyBatisScheduleDAO.logger.info("removeAllDetailInitialScheduleByScheduleAndShift");

		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("id_schedule", id_schedule);
		map.put("shift", shift);

		// delete all detail
		this.getSqlSession().delete("schedule.removeAllDetailInitialScheduleByScheduleAndShift", map);

	}

	@Override
	public void removeDayScheduleUserFired(final Integer idUser, final Date firedDate) {
		MyBatisScheduleDAO.logger.info("remove day schedule user fired");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("idUser", idUser);
		map.put("firedDate", firedDate);

		this.getSqlSession().delete("schedule.removeDayScheduleUserFired", map);

	}

	@Override
	public void removeDayScheduleUserSuspended(final Integer idUser, final Date initialDate, final Date finalDate) {
		MyBatisScheduleDAO.logger.info("remove day schedule user suspended");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("idUser", idUser);
		map.put("initialDate", initialDate);
		map.put("finalDate", finalDate);

		this.getSqlSession().delete("schedule.removeDayScheduleUserSuspended", map);

	}

	@Override
	public void removeSchedule(final Date date_scheduler, final Integer id_user) {
		MyBatisScheduleDAO.logger.info("removeSchedule");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_schedule", date_scheduler);
		map.put("id_user", id_user);

		this.getSqlSession().delete("schedule.removeScheduleByDateAndUser", map);
	}

	@Override
	public void removeScheduleUserFired(final Integer idUser, final Date firedDate) {
		MyBatisScheduleDAO.logger.info("remove schedule user fired");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("idUser", idUser);
		map.put("firedDate", firedDate);

		this.getSqlSession().delete("schedule.removeScheduleUserFired", map);
	}

	@Override
	public void removeScheduleUserSuspended(final Integer idUser, final Date initialDate, final Date finalDate) {
		MyBatisScheduleDAO.logger.info("remove schedule user suspended");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("idUser", idUser);
		map.put("initialDate", initialDate);
		map.put("finalDate", finalDate);

		this.getSqlSession().delete("schedule.removeScheduleUserSuspended", map);

	}

	@Override
	public void saveListDetailFinalScheduler(final Integer id_schedule, final Integer shift, final List<DetailFinalSchedule> details) {
		MyBatisScheduleDAO.logger.info("saveListDetailFinalScheduler");

		// delete all detail
		this.removeAllDetailFinalScheduleByScheduleAndShift(id_schedule, shift);

		// add all details
		for (final DetailFinalSchedule item_detail : details) {
			this.createDetailFinalSchedule(item_detail);
		}

	}

	@Override
	@Transactional
	public void saveListDetailInitialScheduler(final Integer id_schedule, final Integer shift, final List<DetailInitialSchedule> details) {
		MyBatisScheduleDAO.logger.info("saveListDetailInitialScheduler");

		// delete all detail
		this.removeAllDetailInitialScheduleByScheduleAndShift(id_schedule, shift);

		// add all details
		for (final DetailInitialSchedule item_detail : details) {
			this.createDetailInitialSchedule(item_detail);
		}

	}

	@Override
	public void saveOrUpdateDaySchedule(final DaySchedule day_schedule) {
		if (day_schedule.getId() == null) {
			// save
			this.getSqlSession().insert("schedule.createDaySchedule", day_schedule);

		} else {
			this.getSqlSession().update("schedule.updateDaySchedule", day_schedule);

		}

	}

	@Override
	public void saveOrUpdateSchedule(final Schedule currentSchedule) {
		if (currentSchedule.getId() == null) {
			// save
			this.getSqlSession().insert("schedule.createSchedule", currentSchedule);

		} else {
			this.getSqlSession().update("schedule.updateSchedule", currentSchedule);

		}

	}

	@Override
	public List<Schedule> selectAggregateSchedulersProgram(final Date initial_date, final Date final_date, final String full_text_search) {
		MyBatisScheduleDAO.logger.info("selectSchedulers..");

		// set like
		String my_full_text_search = null;
		if (full_text_search != null) {
			my_full_text_search = "%" + full_text_search + "%";
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_from", initial_date);
		map.put("date_to", final_date);
		map.put("my_full_text_search", my_full_text_search);

		final List<Schedule> list = this.getSqlSession().selectList("schedule.selectAggregateScheduleProgram", map);
		return list;
	}

	@Override
	public List<Schedule> selectAggregateSchedulersProgram(final Date firstDateInGrid, final String full_text_search) {
		MyBatisScheduleDAO.logger.info("selectSchedulers..");

		// set like
		String my_full_text_search = null;
		if (full_text_search != null) {
			my_full_text_search = "%" + full_text_search + "%";
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_from", firstDateInGrid);
		map.put("my_full_text_search", my_full_text_search);

		final List<Schedule> list = this.getSqlSession().selectList("schedule.selectAggregateScheduleByDateProgram", map);
		return list;
	}

	@Override
	public List<Schedule> selectAggregateSchedulersRevision(final Date initial_date, final Date final_date, final String full_text_search) {
		MyBatisScheduleDAO.logger.info("selectSchedulers..");

		// set like
		String my_full_text_search = null;
		if (full_text_search != null) {
			my_full_text_search = "%" + full_text_search + "%";
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_from", initial_date);
		map.put("date_to", final_date);
		map.put("my_full_text_search", my_full_text_search);

		final List<Schedule> list = this.getSqlSession().selectList("schedule.selectAggregateScheduleRevision", map);
		return list;
	}

	@Override
	public List<Schedule> selectAggregateSchedulersRevision(final Date firstDateInGrid, final String full_text_search) {
		MyBatisScheduleDAO.logger.info("selectSchedulers..");

		// set like
		String my_full_text_search = null;
		if (full_text_search != null) {
			my_full_text_search = "%" + full_text_search + "%";
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_from", firstDateInGrid);
		map.put("my_full_text_search", my_full_text_search);

		final List<Schedule> list = this.getSqlSession().selectList("schedule.selectAggregateScheduleByDateRevision", map);
		return list;
	}

	@Override
	public List<DaySchedule> selectDaySchedulers(final Date initial_date, final Date final_date, final String full_text_search) {
		MyBatisScheduleDAO.logger.info("selectDaySchedulers..");

		// set like
		String my_full_text_search = null;
		if (full_text_search != null) {
			my_full_text_search = "%" + full_text_search + "%";
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_from", initial_date);
		map.put("date_to", final_date);
		map.put("my_full_text_search", my_full_text_search);

		final List<DaySchedule> list = this.getSqlSession().selectList("schedule.selectDaySchedulers", map);
		return list;
	}
}
