package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.HashMap;
import java.util.List;

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
	public List<String> listAllNameShip() {
		MyBatisShipDAO.logger.info("listAllNameShip");

		return this.getSqlSession().selectList("ship.listAllNameShip");

	}

	@Override
	public List<Ship> listAllShip(final String full_text_search) {
		MyBatisShipDAO.logger.info("selectAllShipFulltextSearchLike");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("my_full_text_search", full_text_search);

		return this.getSqlSession().selectList("ship.selectAllShipFulltextSearchLike", map);
	}

	@Override
	public List<Integer> listIdShipByName(final String shipName) {
		MyBatisShipDAO.logger.info("listIdShipByName");

		return this.getSqlSession().selectList("ship.listIdShipByName", shipName);
	}

	@Override
	public List<Ship> loadAllShip() {
		MyBatisShipDAO.logger.info("loadAllShip");

		return this.getSqlSession().selectList("ship.loadAllShip");

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
