package org.uario.seaworkengine.web.services.webcontroller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.DetailFinalScheduleShip;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.IScheduleShip;
import org.uario.seaworkengine.platform.persistence.dao.IStatistics;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.statistics.IStatProcedure;
import org.uario.seaworkengine.statistics.impl.MonitorData;
import org.uario.seaworkengine.web.services.IWebServiceController;
import org.uario.seaworkengine.web.services.handler.Badge;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;
import org.uario.seaworkengine.web.services.handler.MobileUserDetail;
import org.uario.seaworkengine.web.services.handler.UserStaturation;

public class WebControllerImpl implements IWebServiceController {

	public static Integer		STATUS_SELECTION_SCHEDULE_ALL			= 3;

	public static Integer		STATUS_SELECTION_SCHEDULE_ONLY_PROGRAM	= 1;

	public static Integer		STATUS_SELECTION_SCHEDULE_ONLY_REVIEW	= 2;

	private ConfigurationDAO	configurationDAO;

	private final Logger		logger									= Logger.getLogger(WebControllerImpl.class);

	private PersonDAO			personDAO;

	private ISchedule			scheduleDAO;

	private IShiftCache			shiftCache;

	private IScheduleShip		ship_dao;

	private IStatProcedure		stat_procedure;

	private IStatistics			statistics;

	private TasksDAO			taskDAO;

	@Override
	public List<UserStaturation> calculateUserSaturation(final Date date_request) {

		final ArrayList<UserStaturation> ret = new ArrayList<>();

		final Date date_schedule = DateUtils.truncate(date_request, Calendar.DATE);

		// set saturation month label
		final Calendar cal_saturation_month = DateUtils.toCalendar(date_schedule);
		cal_saturation_month.set(Calendar.DAY_OF_MONTH, cal_saturation_month.getActualMaximum(Calendar.DAY_OF_MONTH));
		final Date date_to = cal_saturation_month.getTime();

		cal_saturation_month.set(Calendar.DAY_OF_MONTH, cal_saturation_month.getMinimum(Calendar.DAY_OF_MONTH));
		final Date date_from = cal_saturation_month.getTime();

		final List<Person> list = this.personDAO.listAllPersonsForMobile(date_schedule);

		for (final Person person : list) {

			if (person.isBackoffice()) {
				continue;
			}

			if (person.isOperative()) {
				continue;
			}

			final Double sat = this.stat_procedure.calculeHourSaturation(person, date_from, date_to);

			final UserStaturation item = new UserStaturation();
			item.setId_user(person.getId());
			item.setSaturation(sat);

			ret.add(item);
		}

		return ret;
	}

	@Override
	public Boolean checkUser(final String username, final String password) {
		final Person person = this.personDAO.loadPerson(username, password);

		if (person == null) {
			return Boolean.FALSE;
		}

		if (person.isEnabled() && person.isOperative()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	@Override
	public void createBadge(final Badge badge) {
		this.scheduleDAO.createBadge(badge);
	}

	@Override
	public void createDetailFinalScheduleShip(final DetailFinalScheduleShip detailFinalScheduleShip) {
		this.ship_dao.createDetailFinalScheduleShip(detailFinalScheduleShip);

	}

	@Override
	public void deleteBadge(final Integer id_badge) {
		this.scheduleDAO.deleteBadge(id_badge);
	}

	@Override
	public void deleteDetailFinalScheduleShipById(final Integer id_detail_final) {

		this.ship_dao.deleteDetailFinalScheduleShipById(id_detail_final);

	}

	public ConfigurationDAO getConfigurationDAO() {
		return this.configurationDAO;
	}

	@Override
	public UserTask getCTTask() {
		return this.configurationDAO.getChangeshiftTask();
	}

	@Override
	public UserTask getDelayOperation() {
		return this.configurationDAO.getDelayOperationTask();
	}

	@Override
	public String getDetailScheduleShipNote(final Integer id_schedule) {
		return this.ship_dao.getDetailScheduleShipNote(id_schedule);
	}

	@Override
	public UserTask getEndOperationTask() {
		return this.configurationDAO.getEndOperationTask();

	}

	@Override
	public List<MonitorData> getMonitorData(final Date request) {
		return this.statistics.getMonitorData(request);
	}

	@Override
	public UserTask getOverflowTask() {
		return this.configurationDAO.getOverflowTask();
	}

	public PersonDAO getPersonDAO() {
		return this.personDAO;
	}

	public ISchedule getScheduleDAO() {
		return this.scheduleDAO;
	}

	@Override
	public String getScheduleNote(final Integer id_schedule) {
		final Schedule schedule = this.scheduleDAO.loadScheduleById(id_schedule);
		final String note = schedule.getNote();
		return note;
	}

	@Override
	public List<UserShift> getUserShiftConfiguration() {
		return new ArrayList<>(this.shiftCache.getHash().values());

	}

	@Override
	public List<Ship> listShip(final Date date_request) {

		final Date date_truncate = DateUtils.truncate(date_request, Calendar.DATE);

		return this.ship_dao.loadShipInDate(new Timestamp(date_truncate.getTime()));
	}

	@Override
	public List<DetailFinalScheduleShip> loadDetailFinalScheduleShipByIdDetailScheduleShip(final Integer idDetailScheduleShip) {

		final List<DetailFinalScheduleShip> final_details = this.ship_dao.loadDetailFinalScheduleShipByIdDetailScheduleShip(idDetailScheduleShip);

		return final_details;
	}

	@Override
	public List<Badge> loadListBadge(final Integer id_schedule) {
		return this.scheduleDAO.loadBadgeByScheduleId(id_schedule);

	}

	@Override
	public List<InitialSchedule> selectInitialSchedule(final Date date_request, Integer status_selection) {

		if (status_selection == null) {
			status_selection = 3;
		}

		final List<InitialSchedule> ret = new ArrayList<>();

		final Date date_schedule = DateUtils.truncate(date_request, Calendar.DATE);

		// get special task
		final List<UserTask> list_special = this.configurationDAO.listSpecialTaskMobile();

		final List<Person> list = this.personDAO.listAllPersonsForMobile(date_schedule);

		for (final Person person : list) {

			final InitialSchedule item = new InitialSchedule();

			final Schedule schedule = this.scheduleDAO.loadSchedule(date_schedule, person.getId());
			if (schedule == null) {
				continue;
			}

			final List<MobileUserDetail> merging_details = new ArrayList<>();

			// ADD SHIFT 1
			for (int i = 1; i <= 4; i++) {

				List<MobileUserDetail> list_details = null;

				switch (status_selection) {
				case 1: {
					list_details = this.scheduleDAO.loadMobileUserInitialDetail(schedule.getId(), i);

					break;
				}

				case 2: {
					list_details = this.scheduleDAO.loadMobileUserFinalDetail(schedule.getId(), i);

					break;
				}

				case 3: {
					list_details = this.scheduleDAO.loadMobileUserInitialDetail(schedule.getId(), i);
					list_details.addAll(this.scheduleDAO.loadMobileUserFinalDetail(schedule.getId(), i));
					break;
				}

				}

				if (!CollectionUtils.isEmpty(list_details)) {
					merging_details.addAll(list_details);
				}

			}

			// CHECK IF ANY ITEM TO ADD
			if (merging_details.size() == 0) {
				continue;
			}

			// set info about task
			final List<UserTask> list_tasks = this.taskDAO.loadTasksByUserForMobile(person.getId());

			// add special task - add only if not exists
			for (final UserTask itm : list_special) {

				if (!list_tasks.contains(itm)) {
					list_tasks.add(itm);
				}

			}

			person.setUserTaskForMobile(list_tasks);

			// set current object
			item.setPerson(person);
			item.setDetail_schedule(merging_details);
			item.setSchedule(schedule);

			ret.add(item);

		}

		return ret;
	}

	/**
	 * return detail ship by date
	 *
	 * @param date_request
	 * @return
	 */
	@Override
	public List<DetailScheduleShip> selectInitialShipSchedule(final Date date_request, final Integer shift) {

		final Date date_request_truncate = DateUtils.truncate(date_request, Calendar.DATE);

		final List<DetailScheduleShip> list = this.ship_dao.searchDetailScheduleShipByDateshit(date_request_truncate, null, shift, null, null, null,
		        null, null);
		return list;
	}

	/**
	 * return detail ship by date
	 *
	 * @param date_request
	 * @return
	 */
	@Override
	public DetailScheduleShip selectInitialShipSchedule(final Date date_request, final Integer shift, final Integer id_ship) {

		if (id_ship == null) {
			return null;
		}

		final List<DetailScheduleShip> list = this.selectInitialShipSchedule(date_request, shift);

		for (final DetailScheduleShip itm : list) {
			if (id_ship.equals(itm.getId_ship())) {
				return itm;
			}
		}

		return null;

	}

	public void setConfigurationDAO(final ConfigurationDAO configurationDAO) {
		this.configurationDAO = configurationDAO;
	}

	public void setPersonDAO(final PersonDAO personDAO) {
		this.personDAO = personDAO;
	}

	public void setScheduleDAO(final ISchedule scheduleDAO) {
		this.scheduleDAO = scheduleDAO;
	}

	public void setShiftCache(final IShiftCache shiftCache) {
		this.shiftCache = shiftCache;
	}

	public void setShip_dao(final IScheduleShip ship_dao) {
		this.ship_dao = ship_dao;
	}

	public void setStat_procedure(final IStatProcedure stat_procedure) {
		this.stat_procedure = stat_procedure;
	}

	public void setStatistics(final IStatistics statistics) {
		this.statistics = statistics;
	}

	public void setTaskDAO(final TasksDAO taskDAO) {
		this.taskDAO = taskDAO;
	}

	@Override
	public void updateDetailScheduleShipForMobile(final Integer id, final String operation, final Integer handswork) {

		this.ship_dao.updateDetailScheduleShipForMobile(id, operation, handswork);

	}

	@Override
	public void updateDetailScheduleShipNote(final Integer id_schedule, final String note) {
		this.ship_dao.updateDetailScheduleShipNote(id_schedule, note);

	}

	@Override
	public void updateScheduleNote(final Integer id_schedule, final String note) {
		this.scheduleDAO.updateScheduleNote(id_schedule, note);
	}

}
