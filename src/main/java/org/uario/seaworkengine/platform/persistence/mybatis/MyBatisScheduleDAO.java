package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.web.services.handler.Badge;

public class MyBatisScheduleDAO extends SqlSessionDaoSupport implements ISchedule {
	private static Logger	logger	= Logger.getLogger(MyBatisScheduleDAO.class);

	private IShiftCache		shift_cache;

	@Override
	public void createBadge(final Badge badge) {

		MyBatisScheduleDAO.logger.info("createBadge");

		this.getSqlSession().insert("schedule.createBadge", badge);

	}

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
	public void deleteBadge(final Integer id_badge) {
		MyBatisScheduleDAO.logger.info("deleteBadge");

		this.getSqlSession().delete("schedule.deleteBadge", id_badge);

	}

	@Override
	public Integer getFirstShift(final Date date_scheduled, final Integer user) {
		MyBatisScheduleDAO.logger.info("getFirstShift");

		final Date dt_arg = DateUtils.truncate(date_scheduled, Calendar.DATE);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_schedule", dt_arg);
		map.put("id_user", user);

		final Schedule schedule = this.loadSchedule(dt_arg, user);
		if (schedule != null) {

			final Integer shift_id = schedule.getShift();
			final UserShift shift = this.shift_cache.getUserShift(shift_id);
			if ((shift != null) && shift.isDefaultBreak()) {
				return -9999;
			}
		}

		final Integer ret_int = this.getSqlSession().selectOne("schedule.getFirstShiftRevision", map);
		if (ret_int != null) {
			return ret_int;
		} else {
			return this.getSqlSession().selectOne("schedule.getFirstShiftProgram", map);
		}

	}

	@Override
	public Integer getLastShift(final Date date_scheduled, final Integer user) {
		MyBatisScheduleDAO.logger.info("getLastShift");

		final Date dt_arg = DateUtils.truncate(date_scheduled, Calendar.DATE);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_schedule", dt_arg);
		map.put("id_user", user);

		final Schedule schedule = this.loadSchedule(dt_arg, user);
		if (schedule != null) {

			final Integer shift_id = schedule.getShift();
			final UserShift shift = this.shift_cache.getUserShift(shift_id);
			if ((shift != null) && shift.isDefaultBreak()) {
				return -9999;
			}
		}

		final Integer ret_int = this.getSqlSession().selectOne("schedule.getLastShiftRevision", map);
		if (ret_int != null) {
			return ret_int;
		} else {
			return this.getSqlSession().selectOne("schedule.getLastShiftProgram", map);
		}

	}

	public IShiftCache getShift_cache() {
		return this.shift_cache;
	}

	@Override
	public List<Badge> loadBadgeByScheduleId(final Integer id_schedule) {
		MyBatisScheduleDAO.logger.info("loadBadgeByScheduleId");

		return this.getSqlSession().selectList("schedule.loadBadgeByScheduleId", id_schedule);
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
	public List<DetailInitialSchedule> loadDetailFinalScheduleForMobileByIdScheduleAndNoShift(final Integer id_schedule, final Integer no_shift) {
		MyBatisScheduleDAO.logger.info("loadDetailFinalScheduleForMobileByIdScheduleAndNoShift");

		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("id_schedule", id_schedule);
		map.put("no_shift", no_shift);

		return this.getSqlSession().selectList("schedule.loadDetailFinalScheduleForMobileByIdScheduleAndNoShift", map);
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
	public List<DetailInitialSchedule> loadDetailInitialScheduleForMobileByIdScheduleAndNoShift(final Integer id_schedule, final Integer no_shift) {
		MyBatisScheduleDAO.logger.info("loadDetailInitialScheduleForMobileByIdScheduleAndNoShift");

		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("id_schedule", id_schedule);
		map.put("no_shift", no_shift);

		return this.getSqlSession().selectList("schedule.loadDetailInitialScheduleForMobileByIdScheduleAndNoShift", map);
	}

	@Override
	public List<Schedule> loadSchedule(final Date date_scheduled) {
		MyBatisScheduleDAO.logger.info("loadSchedule by date");

		final Date dt_arg = DateUtils.truncate(date_scheduled, Calendar.DATE);

		final HashMap<String, Date> map = new HashMap<String, Date>();
		map.put("dt_arg", dt_arg);

		return this.getSqlSession().selectList("schedule.loadScheduleByDate", map);

	}

	@Override
	public Schedule loadSchedule(final Date date_schedule, final Integer id_user) {
		MyBatisScheduleDAO.logger.info("loadSchedule by date and id_user..");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_schedule", date_schedule);
		map.put("id_user", id_user);

		final Schedule ret = this.getSqlSession().selectOne("schedule.selectScheduleByDateAndUser", map);
		return ret;
	}

	@Override
	public Schedule loadScheduleById(final Integer id) {
		MyBatisScheduleDAO.logger.info("loadScheduleById");

		return this.getSqlSession().selectOne("schedule.loadScheduleById", id);
	}

	@Override
	public void removeAllDetailFinalScheduleBySchedule(final Integer id_schedule) {
		MyBatisScheduleDAO.logger.info("removeAllDetailFinalScheduleBySchedule");

		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("id_schedule", id_schedule);

		// delete all detail
		this.getSqlSession().delete("schedule.removeAllDetailFinalScheduleBySchedule", map);

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
	public void removeAllDetailInitialScheduleBySchedule(final Integer id_schedule) {
		MyBatisScheduleDAO.logger.info("removeAllDetailInitialScheduleBySchedule");

		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("id_schedule", id_schedule);

		// delete all detail
		this.getSqlSession().delete("schedule.removeAllDetailInitialScheduleBySchedule", map);

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
	public void removeSchedule(final Date date_scheduler, final Integer id_user) {
		MyBatisScheduleDAO.logger.info("removeSchedule");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_schedule", date_scheduler);
		map.put("id_user", id_user);

		this.getSqlSession().delete("schedule.removeScheduleByDateAndUser", map);
	}

	@Override
	public void removeScheduleUser(final Integer idUser, final Date initialDate, final Date finalDate) {
		MyBatisScheduleDAO.logger.info("remove schedule user suspended");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("idUser", idUser);
		map.put("initialDate", initialDate);
		map.put("finalDate", finalDate);

		this.getSqlSession().delete("schedule.removeScheduleUser", map);

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

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_from", initial_date);
		map.put("date_to", final_date);
		map.put("my_full_text_search", full_text_search);

		final List<Schedule> list = this.getSqlSession().selectList("schedule.selectAggregateScheduleProgram", map);
		return list;
	}

	@Override
	public List<Schedule> selectAggregateSchedulersProgram(final Date firstDateInGrid, final String full_text_search) {
		MyBatisScheduleDAO.logger.info("selectSchedulers..");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_from", firstDateInGrid);
		map.put("my_full_text_search", full_text_search);

		final List<Schedule> list = this.getSqlSession().selectList("schedule.selectAggregateScheduleByDateProgram", map);
		return list;
	}

	@Override
	public List<Schedule> selectAggregateSchedulersRevision(final Date initial_date, final Date final_date, final String full_text_search) {
		MyBatisScheduleDAO.logger.info("selectSchedulers..");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_from", initial_date);
		map.put("date_to", final_date);
		map.put("my_full_text_search", full_text_search);

		final List<Schedule> list = this.getSqlSession().selectList("schedule.selectAggregateScheduleRevision", map);
		return list;
	}

	@Override
	public List<Schedule> selectAggregateSchedulersRevision(final Date firstDateInGrid, final String full_text_search) {
		MyBatisScheduleDAO.logger.info("selectSchedulers..");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_from", firstDateInGrid);
		map.put("my_full_text_search", full_text_search);

		final List<Schedule> list = this.getSqlSession().selectList("schedule.selectAggregateScheduleByDateRevision", map);
		return list;
	}

	@Override
	public List<Schedule> selectScheduleInIntervalDateByUserId(final Integer user, final Date date_from, final Date date_to) {
		MyBatisScheduleDAO.logger.info("selectScheduleInIntervalDate");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_from", date_from);
		map.put("date_to", date_to);
		map.put("user", user);

		final List<Schedule> list = this.getSqlSession().selectList("schedule.selectScheduleInIntervalDateByUserId", map);
		return list;
	}

	@Override
	public List<Schedule> selectSchedulersForPreprocessing(final Date initial_date, final Date final_date, final String my_full_text_search) {
		MyBatisScheduleDAO.logger.info("selectSchedulersForPreprocessing..");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_from", initial_date);
		map.put("date_to", final_date);
		map.put("my_full_text_search", my_full_text_search);

		final List<Schedule> list = this.getSqlSession().selectList("schedule.selectSchedulersForPreprocessing", map);
		return list;
	}

	@Override
	public List<Schedule> selectSchedulersForPreprocessingOnUserId(final Date initial_date, final Date final_date, final Integer userid) {
		MyBatisScheduleDAO.logger.info("selectSchedulersForPreprocessing..");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_from", initial_date);
		map.put("date_to", final_date);
		map.put("userid", userid);

		final List<Schedule> list = this.getSqlSession().selectList("schedule.selectSchedulersForPreprocessingOnUserId", map);
		return list;
	}

	public void setShift_cache(final IShiftCache shift_cache) {
		this.shift_cache = shift_cache;
	}

	@Override
	public void updateMobileSynch(final Integer id_schedule, final boolean synch, final Integer shift_no) {

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("sync_mobile", new Boolean(synch));
		map.put("id", id_schedule);

		if (shift_no.intValue() == 1) {
			this.getSqlSession().update("schedule.updateMobileSynch1", map);
		}

		if (shift_no.intValue() == 2) {
			this.getSqlSession().update("schedule.updateMobileSynch2", map);
		}

		if (shift_no.intValue() == 3) {
			this.getSqlSession().update("schedule.updateMobileSynch3", map);
		}

		if (shift_no.intValue() == 4) {
			this.getSqlSession().update("schedule.updateMobileSynch4", map);
		}

	}

	@Override
	public void updateScheduleNote(final Integer id_schedule, final String note) {

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id_schedule", id_schedule);
		map.put("note", note);

		this.getSqlSession().update("schedule.updateScheduleNote", map);

	}

}
