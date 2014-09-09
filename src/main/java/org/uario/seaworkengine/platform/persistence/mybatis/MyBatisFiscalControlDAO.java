/*
 * MyBatisPersonDAO.java
 * Created on 09/mag/2012
 */
package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.FiscalControl;
import org.uario.seaworkengine.platform.persistence.dao.FiscalControlDAO;

public class MyBatisFiscalControlDAO extends SqlSessionDaoSupport implements FiscalControlDAO {
	private static Logger	logger	= Logger.getLogger(MyBatisFiscalControlDAO.class);

	@Override
	public void createFCForUser(final Integer id_user, final FiscalControl fiscalcontrol) {
		MyBatisFiscalControlDAO.logger.info("createFCForUser");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id_user", id_user.toString());
		map.put("control_date", fiscalcontrol.getControl_date());
		map.put("note", fiscalcontrol.getNote());
		map.put("request_date", fiscalcontrol.getRequest_date());
		map.put("result", fiscalcontrol.getResult());
		map.put("result_comunication_type", fiscalcontrol.getResult_comunication_type());
		map.put("sede_inps", fiscalcontrol.getSede_inps());
		map.put("sikness_from", fiscalcontrol.getSikness_from());
		map.put("sikness_to", fiscalcontrol.getSikness_to());

		this.getSqlSession().insert("fc.createItemForUser", map);

	}

	@Override
	public List<FiscalControl> loadFiscalControlByUser(final Integer id_user) {
		MyBatisFiscalControlDAO.logger.info("loadFiscalControlByUser..");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("id_user", id_user.toString());

		final List<FiscalControl> list_tfrs = this.getSqlSession().selectList("fc.loadItemsByUser", map);
		return list_tfrs;
	}

	@Override
	public void removeFiscalControl(final Integer id_fc) {
		MyBatisFiscalControlDAO.logger.info("removeFiscalControl");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("id_item", id_fc.toString());

		this.getSqlSession().delete("fc.removeItem", map);

	}

	@Override
	public void updateFC(final FiscalControl fiscalcontrol) {
		MyBatisFiscalControlDAO.logger.info("updateFC");
		this.getSqlSession().update("fc.udapteItem", fiscalcontrol);
	}

}
