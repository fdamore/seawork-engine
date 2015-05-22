package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.platform.persistence.cache.IShipCache;
import org.uario.seaworkengine.platform.persistence.dao.IShip;

public class MyBatisShipDAO extends SqlSessionDaoSupport implements IShip {

	private static Logger	logger	= Logger.getLogger(MyBatisShipDAO.class);

	public static Logger getLogger() {
		return MyBatisShipDAO.logger;
	}

	public static void setLogger(final Logger logger) {
		MyBatisShipDAO.logger = logger;
	}

	private IShipCache	ship_cache;

	@Override
	public void createShip(final Ship ship) {
		MyBatisShipDAO.logger.info("createShip");

		this.getSqlSession().insert("ship.createShip", ship);

		// recreate cache
		final List<Ship> list = this.loadAllShip();
		this.ship_cache.buildCache(list);

	}

	@Override
	public void deleteShip(final Integer id_ship) {
		MyBatisShipDAO.logger.info("deleteShip");

		this.getSqlSession().delete("ship.deleteShip", id_ship);

		// recreate cache
		final List<Ship> list = this.loadAllShip();
		this.ship_cache.buildCache(list);

	}

	@Override
	public Ship getActivityHShip() {
		MyBatisShipDAO.logger.info("getActivityHShip");
		final Ship ship = this.getSqlSession().selectOne("ship.getActivityHShip");
		return ship;
	}

	@Override
	public Ship getNoWorkShip() {
		MyBatisShipDAO.logger.info("getNoWorkShip");
		final Ship ship = this.getSqlSession().selectOne("ship.getNoWorkShip");
		return ship;
	}

	public IShipCache getShip_cache() {
		return this.ship_cache;
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
	public void removeShipActivityH() {
		final Ship ship = this.getActivityHShip();
		if (ship != null) {
			ship.setActivityh(false);
			this.updateShip(ship);
		}
	}

	@Override
	public void removeShipNoWork() {
		final Ship ship = this.getNoWorkShip();
		if (ship != null) {
			ship.setNowork(false);
			this.updateShip(ship);
		}

	}

	public void setShip_cache(final IShipCache ship_cache) {
		this.ship_cache = ship_cache;
	}

	@Override
	public void setShipAsActivityH(final Integer shipId) {
		MyBatisShipDAO.logger.info("setShipAsNoWork id " + shipId);

		// remove old nowork ship if exist
		this.removeShipActivityH();

		this.getSqlSession().update("ship.setShipAsActivityH", shipId);

	}

	@Override
	public void setShipAsNoWork(final Integer shipId) {
		MyBatisShipDAO.logger.info("setShipAsNoWork id " + shipId);

		// remove old nowork ship if exist
		this.removeShipNoWork();

		this.getSqlSession().update("ship.setShipAsNoWork", shipId);

	}

	@Override
	public void updateShip(final Ship ship) {
		MyBatisShipDAO.logger.info("updateShip");

		this.getSqlSession().update("ship.updateShip", ship);

		// recreate cache
		final List<Ship> list = this.loadAllShip();
		this.ship_cache.buildCache(list);

	}

	@Override
	public Boolean verifyIfShipExistByName(final String name, final Integer idShipNoCheck) {
		MyBatisShipDAO.logger.info("verifyIfShipExistByName: " + name);

		final List<Ship> ships = this.loadAllShip();

		if (ships != null) {
			for (final Ship item : ships) {
				if (item != null) {
					if (idShipNoCheck != null) {
						if ((item.getId() != idShipNoCheck) && (item.getName() != null) && item.getName().equals(name)) {
							return true;
						}
					} else if ((item.getName() != null) && item.getName().equals(name)) {
						return true;
					}
				}

			}
		}

		return false;

	}

}
