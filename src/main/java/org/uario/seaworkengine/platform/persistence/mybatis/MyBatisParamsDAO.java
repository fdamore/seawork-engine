package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.platform.persistence.dao.IParams;

public class MyBatisParamsDAO extends SqlSessionDaoSupport implements IParams {
	private static Logger	logger	= Logger.getLogger(MyBatisParamsDAO.class);

	@Override
	public String getParam(final String name) {
		MyBatisParamsDAO.logger.info("getParam");

		final String item = this.getSqlSession().selectOne("params.getParam", name);
		return item;
	}

	@Override
	public void setParam(final String name, final String val) {
		MyBatisParamsDAO.logger.info("setParam");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("name", name);
		map.put("val", val);

		this.getSqlSession().insert("params.setParam", map);

	}

}
