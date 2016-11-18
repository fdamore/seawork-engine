package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.Contestation;
import org.uario.seaworkengine.platform.persistence.dao.IContestation;

public class MyBatisContestationDAO extends SqlSessionDaoSupport implements IContestation {
	private static Logger logger = Logger.getLogger(MyBatisContestationDAO.class);

	@Override
	public void createContestation(final Contestation item) {
		MyBatisContestationDAO.logger.info("createContestation");

		this.getSqlSession().insert("contestation.createContestation", item);
	}

	@Override
	public List<Contestation> loadUserContestation(final int id_user) {
		MyBatisContestationDAO.logger.info("loadContestationByUserId =" + id_user);

		final List<Contestation> list_items = this.getSqlSession().selectList("contestation.loadUserContestation",
				id_user);
		return list_items;
	}

	@Override
	public List<Contestation> loadUserContestationByDatePenalty(final Integer id_user, Date date) {
		MyBatisContestationDAO.logger.info("loadUserContestationByDatePenalty =" + id_user);

		final Date date_info = DateUtils.truncate(date, Calendar.DATE);

		final HashMap<String, Object> map = new HashMap<>();
		map.put("id_user", id_user);
		map.put("date_penalty", date_info);

		final List<Contestation> list_items = this.getSqlSession()
				.selectList("contestation.loadUserContestationByDatePenalty", map);
		return list_items;
	}

	@Override
	public List<Contestation> loadUserContestationByYearPenalty(final Integer id_user, Integer year) {
		MyBatisContestationDAO.logger.info("loadUserContestationByYearPenalty =" + id_user);

		final HashMap<String, Integer> map = new HashMap<>();
		map.put("id_user", id_user);
		map.put("year", year);

		final List<Contestation> list_items = this.getSqlSession()
				.selectList("contestation.loadUserContestationByYearPenalty", map);
		return list_items;
	}

	@Override
	public void removeContestation(final int id) {
		MyBatisContestationDAO.logger.info("removeContestation " + id);

		final HashMap<String, Integer> map = new HashMap<>();
		map.put("id", id);

		this.getSqlSession().delete("contestation.removeContestation", map);
	}

	@Override
	public void updateContestation(final Contestation contestation) {
		MyBatisContestationDAO.logger.info("updateContestation");
		this.getSqlSession().update("contestation.updateContestation", contestation);

	}

}
