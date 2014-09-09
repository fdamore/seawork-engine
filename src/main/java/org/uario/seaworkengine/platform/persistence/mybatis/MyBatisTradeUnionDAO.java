/*
 * MyBatisPersonDAO.java
 * Created on 09/mag/2012
 */
package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.TradeUnion;
import org.uario.seaworkengine.platform.persistence.dao.TradeUnionDAO;

public class MyBatisTradeUnionDAO extends SqlSessionDaoSupport implements TradeUnionDAO {
	private static Logger	logger	= Logger.getLogger(MyBatisTradeUnionDAO.class);

	@Override
	public void createTradeUnionForUser(final Integer id_user, final TradeUnion tradeunion) {
		MyBatisTradeUnionDAO.logger.info("createTradeUnionForUser");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id_user", id_user.toString());
		map.put("name", tradeunion.getName());
		map.put("note", tradeunion.getNote());
		map.put("registration", tradeunion.getRegistration());
		map.put("cancellation", tradeunion.getCancellation());

		this.getSqlSession().insert("td.createItemForUser", map);

	}

	@Override
	public List<TradeUnion> loadTradeUnionsByUser(final Integer id_user) {
		MyBatisTradeUnionDAO.logger.info("loadTradeUnionsByUser..");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("id_user", id_user.toString());

		final List<TradeUnion> list_items = this.getSqlSession().selectList("td.loadItemsByUser", map);
		return list_items;
	}

	@Override
	public void removeTradeUnion(final Integer id_tu) {
		MyBatisTradeUnionDAO.logger.info("removeTradeUnion");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("id_item", id_tu.toString());

		this.getSqlSession().delete("td.removeItem", map);

	}

	@Override
	public void updateTradeUnion(final TradeUnion TradeUnion) {
		MyBatisTradeUnionDAO.logger.info("updateTradeUnion");
		this.getSqlSession().update("td.udapteItem", TradeUnion);
	}

}
