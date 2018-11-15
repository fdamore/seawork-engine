package org.uario.seaworkengine.platform.persistence.mybatis;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.DetailFinalScheduleShip;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.ScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.platform.persistence.dao.IScheduleShip;

public class MyBatisScheduleShipDAO extends SqlSessionDaoSupport implements IScheduleShip {

	private static Logger logger = Logger.getLogger(MyBatisScheduleShipDAO.class);

	public static Logger getLogger() {
		return MyBatisScheduleShipDAO.logger;
	}

	public static void setLogger(final Logger logger) {
		MyBatisScheduleShipDAO.logger = logger;
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
	public String getDetailScheduleShipNote(final Integer id) {
		MyBatisScheduleShipDAO.logger.info("getDetailScheduleShipNote");

		return this.getSqlSession().selectOne("scheduleship.loadDetailScheduleShipNote", id);

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
	public List<ScheduleShip> loadScheduleShipByIdShipAndArrivalDate(final Integer idship, final Date arrivaldate) {
		MyBatisScheduleShipDAO.logger.info("loadScheduleShipByIdShip");

		final Date inf_date = DateUtils.truncate(arrivaldate, Calendar.DATE);

		final HashMap<String, Object> map = new HashMap<>();
		map.put("idship", idship);
		map.put("arrivaldate", inf_date);

		return this.getSqlSession().selectList("scheduleship.loadScheduleShipByIdShipAndArrivalDate", map);

	}

	@Override
	public List<Ship> loadShipInDate(final Timestamp shipDate) {
		MyBatisScheduleShipDAO.logger.info("load Ship In Date" + shipDate);

		final HashMap<String, Object> map = new HashMap<>();
		map.put("shipDate", shipDate);

		return this.getSqlSession().selectList("scheduleship.loadShipInDate", map);
	}

	@Override
	public List<DetailScheduleShip> searchDetailScheduleShip(final Date datefrom, final Date dateto, final Date dateshift,
							final Boolean period_on_dateshift, final String full_text_search, final Integer shift, final Integer idCustomer,
							final Boolean nowork, final Boolean activityh, final Boolean worked, final Integer serviceId, final String shipType,
							final String shipLine, final String shipCondition, final String operation_type, final String invoice_period) {

		MyBatisScheduleShipDAO.logger.info("load DetailScheduleShip in inteval Date And ShipName");

		String full_text_search_arg = full_text_search;
		if ((full_text_search != null) && full_text_search.isEmpty()) {
			full_text_search_arg = null;
		}

		Date date_from_arg = null;
		Date date_to_arg = null;
		if (datefrom != null) {
			date_from_arg = DateUtils.truncate(datefrom, Calendar.DATE);
		}
		if (dateto != null) {
			date_to_arg = DateUtils.truncate(dateto, Calendar.DATE);
		}

		final HashMap<String, Object> map = new HashMap<>();
		map.put("datefrom", date_from_arg);
		map.put("dateto", date_to_arg);
		map.put("dateshift", dateshift);
		map.put("full_text_search", full_text_search_arg);
		map.put("shift", shift);
		map.put("idCustomer", idCustomer);
		map.put("nowork", nowork);
		map.put("activityh", activityh);
		map.put("worked", worked);
		map.put("serviceId", serviceId);
		map.put("shipType", shipType);
		map.put("shipLine", shipLine);
		map.put("shipCondition", shipCondition);
		map.put("period_on_dateshift", period_on_dateshift);
		map.put("operation_type", operation_type);
		map.put("invoice_period", invoice_period);

		return this.getSqlSession().selectList("scheduleship.searchDetailScheduleShipByPeriodOrDateshift", map);

	}

	@Override
	public List<DetailScheduleShip> searchDetailScheduleShip(final Date datefrom, final Date dateto, final Date dateshift,
							final Boolean period_on_dateshift, final String full_text_search, final Integer shift, final Integer idCustomer,
							final Boolean nowork, final Boolean activityh, final Boolean worked, final Integer serviceId, final String shipType,
							final String shipLine, final String shipCondition, final String operation_type, final String invoice_period,
							final boolean invoice) {

		MyBatisScheduleShipDAO.logger.info("load DetailScheduleShip in inteval Date And ShipName");

		String full_text_search_arg = full_text_search;
		if ((full_text_search != null) && full_text_search.isEmpty()) {
			full_text_search_arg = null;
		}

		Date date_from_arg = null;
		Date date_to_arg = null;
		if (datefrom != null) {
			date_from_arg = DateUtils.truncate(datefrom, Calendar.DATE);
		}
		if (dateto != null) {
			date_to_arg = DateUtils.truncate(dateto, Calendar.DATE);
		}

		final HashMap<String, Object> map = new HashMap<>();
		map.put("datefrom", date_from_arg);
		map.put("dateto", date_to_arg);
		map.put("dateshift", dateshift);
		map.put("full_text_search", full_text_search_arg);
		map.put("shift", shift);
		map.put("idCustomer", idCustomer);
		map.put("nowork", nowork);
		map.put("activityh", activityh);
		map.put("worked", worked);
		map.put("serviceId", serviceId);
		map.put("shipType", shipType);
		map.put("shipLine", shipLine);
		map.put("shipCondition", shipCondition);
		map.put("period_on_dateshift", period_on_dateshift);
		map.put("operation_type", operation_type);
		map.put("invoice_period", invoice_period);

		// add invocie
		map.put("no_invoice", invoice);

		return this.getSqlSession().selectList("scheduleship.searchDetailScheduleShipByPeriodOrDateshift", map);
	}

	@Override
	public List<DetailScheduleShip> searchDetailScheduleShipByDateshit(final Date shiftdate, final String full_text_search, final Integer shift,
							final Integer idCustomer, final Boolean nowork, final Boolean activityh, final Boolean worked, final Integer serviceId) {

		MyBatisScheduleShipDAO.logger.info("load Detail ScheduleShip By Shift Date " + shiftdate);

		String full_text_search_arg = full_text_search;
		if ((full_text_search != null) && full_text_search.isEmpty()) {
			full_text_search_arg = null;
		}

		final HashMap<String, Object> map = new HashMap<>();
		map.put("shiftdate", shiftdate);
		map.put("full_text_search", full_text_search_arg);
		map.put("shift", shift);
		map.put("idCustomer", idCustomer);
		map.put("nowork", nowork);
		map.put("activityh", activityh);
		map.put("worked", worked);
		map.put("serviceId", serviceId);

		final List<DetailScheduleShip> list = this.getSqlSession().selectList("scheduleship.searchDetailScheduleShip", map);

		return list;
	}

	@Override
	public List<DetailScheduleShip> searchDetailScheduleShipRif_MCT_SWS(final Integer rif_sws, final String rif_mct) {
		MyBatisScheduleShipDAO.logger.info("searchDetailScheduleShipRif_MCT_SWS ");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("rif_sws", rif_sws);
		map.put("rif_mct", rif_mct);

		final List<DetailScheduleShip> list = this.getSqlSession().selectList("scheduleship.searchDetailScheduleShipRif_MCT_SWS", map);

		return list;
	}

	@Override
	public List<ScheduleShip> searchScheduleShip(final Date datefrom, final Date dateto, final Integer sws, final String mct,
							final Integer idCustomer, final Integer idService, final String textSearch, final String shipType, final String shipLine,
							final String shipCondition, final Boolean intial_support) {

		MyBatisScheduleShipDAO.logger.info("load ScheduleShip");

		String mct_arg = mct;
		if ((mct != null) && mct.equals("")) {
			mct_arg = null;
		}

		String textSearch_arg = textSearch;
		if ((textSearch != null) && textSearch.isEmpty()) {
			textSearch_arg = null;
		}

		// define date arg
		Date date_from_arg = null;
		Date date_to_arg = null;

		if (datefrom != null) {
			date_from_arg = DateUtils.truncate(datefrom, Calendar.DATE);
		}

		if (dateto != null) {
			date_to_arg = DateUtils.truncate(dateto, Calendar.DATE);
		}

		final HashMap<String, Object> map = new HashMap<>();
		map.put("datefrom", date_from_arg);
		map.put("dateto", date_to_arg);
		map.put("sws", sws);
		map.put("mct", mct_arg);
		map.put("idCustomer", idCustomer);
		map.put("idService", idService);
		map.put("textSearch", textSearch_arg);
		map.put("shipType", shipType);
		map.put("shipLine", shipLine);
		map.put("shipCondition", shipCondition);
		map.put("intial_support", intial_support);

		return this.getSqlSession().selectList("scheduleship.searchScheduleShip", map);
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
	public void updateDetailScheduleShipForMobile(final Integer id, final Integer handswork, final Integer manwork, final Boolean worked) {
		MyBatisScheduleShipDAO.logger.info("updateDetailScheduleShipForMobile");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("handswork", handswork);
		map.put("menwork", manwork);
		map.put("worked", worked);

		this.getSqlSession().update("scheduleship.updateDetailScheduleShipForMobile", map);

	}

	@Override
	public void updateDetailScheduleShipNote(final Integer id_schedule, final String note) {
		MyBatisScheduleShipDAO.logger.info("updateDetailScheduleShipNote");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("id", id_schedule);
		map.put("note", note);

		this.getSqlSession().update("scheduleship.updateDetailScheduleShipNote", map);

	}

	@Override
	public void updateRifMCT(final Integer id, final String rif_mct) {
		MyBatisScheduleShipDAO.logger.info("updateRifMCT");

		final HashMap<String, Object> map = new HashMap<>();
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
