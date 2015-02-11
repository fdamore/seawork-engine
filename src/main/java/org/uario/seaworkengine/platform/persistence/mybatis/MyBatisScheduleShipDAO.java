package org.uario.seaworkengine.platform.persistence.mybatis;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.ScheduleShip;
import org.uario.seaworkengine.platform.persistence.dao.IScheduleShip;

public class MyBatisScheduleShipDAO extends SqlSessionDaoSupport implements IScheduleShip {

	private static Logger	logger	= Logger.getLogger(MyBatisScheduleShipDAO.class);

	public static Logger getLogger() {
		return MyBatisScheduleShipDAO.logger;
	}

	public static void setLogger(final Logger logger) {
		MyBatisScheduleShipDAO.logger = logger;
	}

	@Override
	public void createDetailScheduleShip(final DetailScheduleShip detailscheduleShip) {
		MyBatisScheduleShipDAO.logger.info("createDetailScheduleShip");

		this.getSqlSession().insert("scheduleship.createDetailScheduleShip", detailscheduleShip);

	}

	@Override
	public void createScheduleShip(final ScheduleShip scheduleShip) {
		MyBatisScheduleShipDAO.logger.info("createScheduleShip");

		this.getSqlSession().insert("scheduleship.createScheduleShip", scheduleShip);

	}

	@Override
	public void deleteDetailScheduleShip(final Integer id_detailScheduleShip) {
		MyBatisScheduleShipDAO.logger.info("deleteDetailScheduleShip");

		this.getSqlSession().delete("scheduleship.deleteDetailScheduleShip", id_detailScheduleShip);

	}

	@Override
	public void deleteScheduleShip(final Integer id_scheduleShip) {
		MyBatisScheduleShipDAO.logger.info("deleteScheduleShip");

		this.getSqlSession().delete("scheduleship.deleteScheduleShip", id_scheduleShip);

	}

	@Override
	public void deteleDetailSchedueleShipByIdSchedule(final Integer id_scheduleShip) {
		MyBatisScheduleShipDAO.logger.info("deteleDetailSchedueleShipByIdSchedule");

		this.getSqlSession().update("scheduleship.deteleDetailSchedueleShipByIdSchedule", id_scheduleShip);

	}

	@Override
	public List<ScheduleShip> loadAllScheduleShip() {
		MyBatisScheduleShipDAO.logger.info("loadAllScheduleShip");

		return this.getSqlSession().selectList("scheduleship.loadAllScheduleShip");
	}

	@Override
	public DetailScheduleShip loadDetailScheduleShip(final Integer id_detailScheduleShip) {
		MyBatisScheduleShipDAO.logger.info("loadDetailScheduleShip");

		return this.getSqlSession().selectOne("scheduleship.loadDetailScheduleShip", id_detailScheduleShip);
	}

	@Override
	public List<DetailScheduleShip> loadDetailScheduleShipByIdSchedule(final Integer id_scheduleShip) {
		MyBatisScheduleShipDAO.logger.info("loadDetailScheduleShipByIdSchedule");

		return this.getSqlSession().selectList("scheduleship.loadDetailScheduleShipByIdSchedule", id_scheduleShip);

		// DateUtils.truncate(date, Calendar.S
	}

	@Override
	public List<DetailScheduleShip> loadDetailScheduleShipByShiftDateAndShipName(final Date shiftdate, final String full_text_search) {
		MyBatisScheduleShipDAO.logger.info("load Detail ScheduleShip By Shift Date " + shiftdate);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("shiftdate", shiftdate);
		map.put("full_text_search", full_text_search);

		return this.getSqlSession().selectList("scheduleship.loadDetailScheduleShipByShiftDateAndShipName", map);
	}

	@Override
	public List<DetailScheduleShip> loadDetailScheduleWithShiftDateNull() {
		MyBatisScheduleShipDAO.logger.info("load Detail ScheduleShip where timeshift is null ");

		return this.getSqlSession().selectList("scheduleship.loadDetailScheduleWithShiftDateNull");
	}

	@Override
	public ScheduleShip loadScheduleShip(final Integer id_scheduleShip) {
		MyBatisScheduleShipDAO.logger.info("loadScheduleShip");

		return this.getSqlSession().selectOne("scheduleship.loadScheduleShip", id_scheduleShip);
	}

	@Override
	public List<ScheduleShip> loadScheduleShipByArrivalDate(final Timestamp arrivaldate) {
		MyBatisScheduleShipDAO.logger.info("loadScheduleShipByArrivalDAte");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("arrivaldate", arrivaldate);

		return this.getSqlSession().selectList("scheduleship.loadScheduleShipByArrivalDate", map);
	}

	@Override
	public ScheduleShip loadScheduleShipByIdShipAndArrivalDate(final Integer idship, final Date arrivaldate) {
		MyBatisScheduleShipDAO.logger.info("loadScheduleShipByIdShip");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("idship", idship);
		map.put("arrivaldate", arrivaldate);

		final ScheduleShip scheduleShip = this.getSqlSession().selectOne("scheduleship.loadScheduleShipByIdShipAndArrivalDate", map);

		return scheduleShip;
	}

	@Override
	public List<ScheduleShip> loadScheduleShipInDate(final Timestamp dateFrom, final Timestamp dateTo) {
		MyBatisScheduleShipDAO.logger.info("loadScheduleShipInDate");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("dateFrom", dateFrom);
		map.put("dateTo", dateTo);

		return this.getSqlSession().selectList("scheduleship.loadScheduleShipInDate", map);
	}

	@Override
	public List<ScheduleShip> selectAllScheduleShipFulltextSearchLike(final String full_text_search) {
		MyBatisScheduleShipDAO.logger.info("selectAllScheduleShipFulltextSearchLike");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("my_full_text_search", full_text_search);

		return this.getSqlSession().selectList("scheduleship.selectAllScheduleShipFulltextSearchLike", map);
	}

	@Override
	public void updateDetailScheduleShip(final DetailScheduleShip detailScheduleShip) {
		MyBatisScheduleShipDAO.logger.info("updateDetailScheduleShip");

		this.getSqlSession().update("scheduleship.updateDetailScheduleShip", detailScheduleShip);

	}

	@Override
	public void updateScheduleShip(final ScheduleShip scheduleShip) {
		MyBatisScheduleShipDAO.logger.info("updateScheduleShip");

		this.getSqlSession().update("scheduleship.updateScheduleShip", scheduleShip);

	}
}
