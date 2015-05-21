package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.platform.persistence.dao.IShip;

public class MyBatisShipDAO extends SqlSessionDaoSupport implements IShip {
	private static Logger logger = Logger.getLogger(MyBatisShipDAO.class);

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

		return this.getSqlSession().selectList(
				"ship.selectAllShipFulltextSearchLike", map);
	}

	@Override
	public List<Integer> listIdShipByName(final String shipName) {
		MyBatisShipDAO.logger.info("listIdShipByName");

		return this.getSqlSession().selectList("ship.listIdShipByName",
				shipName);
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

	@Override
	public Boolean verifyIfShipExistByName(final String name, Integer idShipNoCheck) {
		MyBatisShipDAO.logger.info("verifyIfShipExistByName: " + name);

		final List<Ship> ships = this.loadAllShip();

		if (ships != null) {
			for (final Ship item : ships) {
				if (item != null) {
					if (idShipNoCheck != null) {
						if (item.getId() != idShipNoCheck
								&& item.getName() != null
								&& item.getName().equals(name)) {
							return true;
						}
					} else if (item.getName() != null
							&& item.getName().equals(name)) {
						return true;
					}
				}

			}
		}

		return false;

	}

	@Override
	public void setShipAsNoWork(Integer shipId) {
		MyBatisShipDAO.logger.info("setShipAsNoWork id " + shipId);

		// remove old nowork ship if exist
		removeShipNoWork();

		this.getSqlSession().update("ship.setShipAsNoWork", shipId);

	}

	@Override
	public Ship getNoWorkShip() {
		MyBatisShipDAO.logger.info("getNoWorkShip");
		Ship ship = this.getSqlSession().selectOne("ship.getNoWorkShip");
		return ship;
	}

	@Override
	public void setShipAsActivityH(Integer shipId) {
		MyBatisShipDAO.logger.info("setShipAsNoWork id " + shipId);

		// remove old nowork ship if exist
		removeShipActivityH();

		this.getSqlSession().update("ship.setShipAsActivityH", shipId);

	}

	@Override
	public Ship getActivityHShip() {
		MyBatisShipDAO.logger.info("getActivityHShip");
		Ship ship = this.getSqlSession().selectOne("ship.getActivityHShip");
		return ship;
	}

	@Override
	public void removeShipNoWork() {
		Ship ship = getNoWorkShip();
		if (ship!=null){
			ship.setNowork(false);
			updateShip(ship);
		}

	}

	@Override
	public void removeShipActivityH() {
		Ship ship = getActivityHShip();
		if (ship != null) {
			ship.setActivityh(false);
			updateShip(ship);
		}
	}

}
