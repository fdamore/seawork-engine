package org.uario.seaworkengine.platform.persistence.mybatis;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.DetailFinalScheduleShip;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.ScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.platform.persistence.dao.IScheduleShip;
import org.uario.seaworkengine.statistics.ShipTotal;

public class MyBatisScheduleShipDAO extends SqlSessionDaoSupport implements IScheduleShip {

	private static Logger logger = Logger.getLogger(MyBatisScheduleShipDAO.class);

	public static Logger getLogger() {
		return MyBatisScheduleShipDAO.logger;
	}

	public static void setLogger(final Logger logger) {
		MyBatisScheduleShipDAO.logger = logger;
	}

	@Override
	public ShipTotal calculateHandsWorkAndMensByArrivalDateAndShipName(final Date arrivaldate, final String full_text_search, final Integer shift) {
		MyBatisScheduleShipDAO.logger.info("calculateHandsWorkAndMensByArrivalDateAndShipName");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("arrivaldate", arrivaldate);
		map.put("full_text_search", full_text_search);
		map.put("shift", shift);

		return this.getSqlSession().selectOne("scheduleship.calculateHandsWorkAndMensByArrivalDateAndShipName", map);
	}

	@Override
	public ShipTotal calculateHandsWorkInDate(final Timestamp dateFrom, final Timestamp dateTo, final Integer shift) {
		MyBatisScheduleShipDAO.logger.info("calculateHandsWorkInDate");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("dateFrom", dateFrom);
		map.put("dateTo", dateTo);
		map.put("shift", shift);

		return this.getSqlSession().selectOne("scheduleship.calculateHandsWorkInDateAndShipName", map);
	}

	@Override
	public Integer calculateNumberOfShipByArrivalDateAndShipName(final Date arrivaldate, final String full_text_search, final Integer shift) {
		MyBatisScheduleShipDAO.logger.info("calculateNumberOfShipByArrivalDateAndShipName" + arrivaldate + " " + full_text_search);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("arrivaldate", arrivaldate);
		map.put("full_text_search", full_text_search);
		map.put("shift", shift);

		return this.getSqlSession().selectOne("scheduleship.calculateNumberOfShipByArrivalDateAndShipName", map);
	}

	@Override
	public Integer calculateNumberOfShipInDate(final Timestamp dateFrom, final Timestamp dateTo, final Integer shift) {
		MyBatisScheduleShipDAO.logger.info("calculateNumberOfShipInDate");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("dateFrom", dateFrom);
		map.put("dateTo", dateTo);
		map.put("shift", shift);

		return this.getSqlSession().selectOne("scheduleship.calculateNumberOfShipInDateAndShipName", map);
	}

	@Override
	public Integer calculateVolumeByArrivalDateAndShipName(final Date arrivaldate, final String full_text_search, final Integer shift) {
		MyBatisScheduleShipDAO.logger.info("calculateVolumeByArrivalDateAndShipName" + arrivaldate + " " + full_text_search);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("arrivaldate", arrivaldate);
		map.put("full_text_search", full_text_search);
		map.put("shift", shift);

		return this.getSqlSession().selectOne("scheduleship.calculateVolumeByArrivalDateAndShipName", map);
	}

	@Override
	public Integer calculateVolumeInDate(final Timestamp dateFrom, final Timestamp dateTo, final Integer shift) {
		MyBatisScheduleShipDAO.logger.info("calculateVolumeInDateAndShipName");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("dateFrom", dateFrom);
		map.put("dateTo", dateTo);
		map.put("shift", shift);

		return this.getSqlSession().selectOne("scheduleship.calculateVolumeInDateAndShipName", map);
	}

	@Override
	public void createDetailFinalScheduleShip(final DetailFinalScheduleShip detailFinalScheduleShip) {
		MyBatisScheduleShipDAO.logger.info("createDetailFinalScheduleShip");

		this.getSqlSession().insert("scheduleship.createDetailFinalScheduleShip", detailFinalScheduleShip);

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
	public void deleteDetailFinalScheduleShipById(final Integer id) {
		MyBatisScheduleShipDAO.logger.info("deleteDetailFinalScheduleShipById");

		this.getSqlSession().insert("scheduleship.deleteDetailFinalScheduleShipById", id);

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
	public DetailFinalScheduleShip loadDetailFinalScheduleShipById(final Integer id) {
		MyBatisScheduleShipDAO.logger.info("loadDetailFinalScheduleShipById");

		return this.getSqlSession().selectOne("scheduleship.loadDetailFinalScheduleShipById", id);
	}

	@Override
	public List<DetailFinalScheduleShip> loadDetailFinalScheduleShipByIdDetailScheduleShip(final Integer idDetailScheduleShip) {
		MyBatisScheduleShipDAO.logger.info("load detailFinalScheduleShip by IdDetailScheduleShip " + idDetailScheduleShip);

		return this.getSqlSession().selectList("scheduleship.loadDetailFinalScheduleShipByIdDetailScheduleShip", idDetailScheduleShip);
	}

	@Override
	public List<DetailScheduleShip> loadDetailScheduleShipByIdSchedule(final Integer id_scheduleShip) {
		MyBatisScheduleShipDAO.logger.info("loadDetailScheduleShipByIdSchedule");

		return this.getSqlSession().selectList("scheduleship.loadDetailScheduleShipByIdSchedule", id_scheduleShip);

		// DateUtils.truncate(date, Calendar.S
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
	public List<ScheduleShip> loadScheduleShipByIdShipAndArrivalDate(final Integer idship, final Date arrivaldate) {
		MyBatisScheduleShipDAO.logger.info("loadScheduleShipByIdShip");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("idship", idship);
		map.put("arrivaldate", arrivaldate);

		return this.getSqlSession().selectList("scheduleship.loadScheduleShipByIdShipAndArrivalDate", map);

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
	public List<Ship> loadShipInDate(final Timestamp shipDate) {
		MyBatisScheduleShipDAO.logger.info("load Ship In Date" + shipDate);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("shipDate", shipDate);

		return this.getSqlSession().selectList("scheduleship.loadShipInDate", map);
	}

	@Override
	public List<DetailScheduleShip> searchDetailScheduleShip(final Date datefrom, final Date dateto, final String full_text_search,
			final Integer shift, final Integer idCustomer) {
		MyBatisScheduleShipDAO.logger.info("load DetailScheduleShip in inteval Date And ShipName");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("datefrom", datefrom);
		map.put("dateto", dateto);
		map.put("full_text_search", full_text_search);
		map.put("shift", shift);
		map.put("idCustomer", idCustomer);

		return this.getSqlSession().selectList("scheduleship.searchDetailScheduleShipPeriod", map);

	}

	@Override
	public List<DetailScheduleShip> searchDetailScheduleShip(final Date shiftdate, final String full_text_search, final Integer shift,
			final Integer idCustomer) {
		MyBatisScheduleShipDAO.logger.info("load Detail ScheduleShip By Shift Date " + shiftdate);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("shiftdate", shiftdate);
		map.put("full_text_search", full_text_search);
		map.put("shift", shift);
		map.put("idCustomer", idCustomer);

		return this.getSqlSession().selectList("scheduleship.searchDetailScheduleShip", map);
	}

	@Override
	public List<ScheduleShip> selectAllScheduleShipFulltextSearchLike(final String full_text_search) {
		MyBatisScheduleShipDAO.logger.info("selectAllScheduleShipFulltextSearchLike");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("my_full_text_search", full_text_search);

		return this.getSqlSession().selectList("scheduleship.selectAllScheduleShipFulltextSearchLike", map);
	}

	@Override
	public void updateDetailFinalScheduleShip(final DetailFinalScheduleShip detailFinalScheduleShip) {
		MyBatisScheduleShipDAO.logger.info("updateDetailFinalScheduleShip");

		this.getSqlSession().update("scheduleship.updateDetailFinalScheduleShip", detailFinalScheduleShip);

	}

	@Override
	public void updateDetailScheduleShip(final DetailScheduleShip detailScheduleShip) {
		MyBatisScheduleShipDAO.logger.info("updateDetailScheduleShip");

		this.getSqlSession().update("scheduleship.updateDetailScheduleShip", detailScheduleShip);

	}

	@Override
	public void updateDetailScheduleShipForMobile(final DetailScheduleShip sch) {
		MyBatisScheduleShipDAO.logger.info("updateDetailScheduleShip");

		this.getSqlSession().update("scheduleship.updateDetailScheduleShipForMobile", sch);

	}

	@Override
	public void updateRifMCT(final Integer id, final Integer rif_mct) {
		MyBatisScheduleShipDAO.logger.info("updateRifMCT");

		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("id", id);
		map.put("rif_mct", rif_mct);

		this.getSqlSession().update("scheduleship.updateRifMCT", map);

	}

	@Override
	public void updateScheduleShip(final ScheduleShip scheduleShip) {
		MyBatisScheduleShipDAO.logger.info("updateScheduleShip");

		this.getSqlSession().update("scheduleship.updateScheduleShip", scheduleShip);

	}
}
