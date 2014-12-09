package org.uario.seaworkengine.platform.persistence.mybatis;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.platform.persistence.dao.IShip;

public class MyBatisShipDAO extends SqlSessionDaoSupport implements IShip {
	private static Logger	logger	= Logger.getLogger(MyBatisShipDAO.class);

	public static Logger getLogger() {
		return MyBatisShipDAO.logger;
	}

	public static void setLogger(final Logger logger) {
		MyBatisShipDAO.logger = logger;
	}

	@Override
	public void createShip(final Ship ship) {
		MyBatisShipDAO.logger.info("createShip");

		this.getSqlSession().insert("ship.createShip", ship);

	}

	@Override
	public void deleteShip(final Integer id_ship) {
		MyBatisShipDAO.logger.info("deleteShip");

		this.getSqlSession().delete("ship.deleteShip", id_ship);

	}

	@Override
	public Ship loadShip(final Integer id_ship) {
		MyBatisShipDAO.logger.info("loadShip");

		return this.getSqlSession().selectOne("ship.loadShip", id_ship);
	}

	@Override
	public void updateShip(final Ship ship) {
		MyBatisShipDAO.logger.info("updateShip");

		this.getSqlSession().update("ship.updateShip", ship);

	}

}
