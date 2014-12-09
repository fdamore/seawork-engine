package org.uario.seaworkengine.platform.persistence.mybatis;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.ScheduleShip;
import org.uario.seaworkengine.platform.persistence.dao.IScheduleShip;

public class MyBatisScheduleShip extends SqlSessionDaoSupport implements IScheduleShip {

	private static Logger	logger	= Logger.getLogger(MyBatisScheduleShip.class);

	public static Logger getLogger() {
		return MyBatisScheduleShip.logger;
	}

	public static void setLogger(final Logger logger) {
		MyBatisScheduleShip.logger = logger;
	}

	@Override
	public void createDetailScheduleShip(final DetailScheduleShip detailscheduleShip) {
		MyBatisScheduleShip.logger.info("createDetailScheduleShip");

		this.getSqlSession().insert("scheduleship.createDetailScheduleShip", detailscheduleShip);

	}

	@Override
	public void createScheduleShip(final ScheduleShip scheduleShip) {
		MyBatisScheduleShip.logger.info("createScheduleShip");

		this.getSqlSession().insert("scheduleship.createScheduleShip", scheduleShip);

	}

	@Override
	public void deleteDetailScheduleShip(final Integer id_detailScheduleShip) {
		MyBatisScheduleShip.logger.info("deleteDetailScheduleShip");

		this.getSqlSession().delete("scheduleship.deleteDetailScheduleShip", id_detailScheduleShip);

	}

	@Override
	public void deleteScheduleShip(final Integer id_scheduleShip) {
		MyBatisScheduleShip.logger.info("deleteScheduleShip");

		this.getSqlSession().delete("scheduleship.deleteScheduleShip", id_scheduleShip);

	}

	@Override
	public DetailScheduleShip loadDetailScheduleShip(final Integer id_detailScheduleShip) {
		MyBatisScheduleShip.logger.info("loadDetailScheduleShip");

		return this.getSqlSession().selectOne("scheduleship.loadDetailScheduleShip", id_detailScheduleShip);
	}

	@Override
	public List<DetailScheduleShip> loadDetailScheduleShipByIdSchedule(final Integer id_scheduleShip) {
		MyBatisScheduleShip.logger.info("loadDetailScheduleShipByIdSchedule");

		return this.getSqlSession().selectList("scheduleship.loadDetailScheduleShipByIdSchedule", id_scheduleShip);
	}

	@Override
	public ScheduleShip loadScheduleShip(final Integer id_scheduleShip) {
		MyBatisScheduleShip.logger.info("loadScheduleShip");

		return this.getSqlSession().selectOne("scheduleship.loadScheduleShip", id_scheduleShip);
	}

	@Override
	public List<ScheduleShip> loadScheduleShipByIdShipAndArrivalDate(final Integer id_Ship, final Timestamp arrivalDate) {
		MyBatisScheduleShip.logger.info("loadScheduleShipByIdShip");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id_Ship", id_Ship);
		map.put("arrivalDate", arrivalDate);

		return this.getSqlSession().selectOne("scheduleship.loadScheduleShipByIdShipAndArrivalDate", map);
	}

	@Override
	public void updateDetailScheduleShip(final DetailScheduleShip detailScheduleShip) {
		MyBatisScheduleShip.logger.info("updateDetailScheduleShip");

		this.getSqlSession().update("scheduleship.updateDetailScheduleShip", detailScheduleShip);

	}

	@Override
	public void updateScheduleShip(final ScheduleShip scheduleShip) {
		MyBatisScheduleShip.logger.info("updateScheduleShip");

		this.getSqlSession().update("scheduleship.updateScheduleShip", scheduleShip);

	}

}
