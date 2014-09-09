package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.TradeUnion;

public interface TradeUnionDAO {

	public void createTradeUnionForUser(Integer id_user, TradeUnion tradeunion);

	public List<TradeUnion> loadTradeUnionsByUser(Integer id_user);

	public void removeTradeUnion(Integer id_tu);

	public void updateTradeUnion(TradeUnion TradeUnion);

}
