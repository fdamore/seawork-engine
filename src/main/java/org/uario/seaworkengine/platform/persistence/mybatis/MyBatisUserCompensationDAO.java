package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.UserCompensation;
import org.uario.seaworkengine.platform.persistence.dao.UserCompensationDAO;

public class MyBatisUserCompensationDAO extends SqlSessionDaoSupport implements UserCompensationDAO {

	private static Logger	logger	= Logger.getLogger(MyBatisUserCompensationDAO.class);

	@Override
	public void createUserCompensation(final UserCompensation userCompensation) {
		MyBatisUserCompensationDAO.logger.info("Create User Compensation id " + userCompensation.getId());

		getSqlSession().insert("usercompensation.createUserCompensation", userCompensation);

	}

	@Override
	public void deleteUserCompensation(final Integer id) {
		MyBatisUserCompensationDAO.logger.info("Delete UserCompensation id " + id);

		getSqlSession().delete("usercompensation.deleteUserCompensation", id);

	}

	@Override
	public Double getTotalHoursInDateYear(final Integer idUser, final Date date_submit) {
		MyBatisUserCompensationDAO.logger.info("get Total Hours In DateYear");

		final Date dt_arg = DateUtils.truncate(date_submit, Calendar.DATE);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("date_submit", dt_arg);
		map.put("id_user", idUser);

		return getSqlSession().selectOne("usercompensation.getTotalHoursInDateYear", map);

	}

	@Override
	public List<UserCompensation> loadAllUserCompensationByUserId(final Integer id_user) {
		MyBatisUserCompensationDAO.logger.info("Load All UserCompensationById iduser " + id_user);

		return getSqlSession().selectList("usercompensation.loadAllUserCompensationByUserId", id_user);
	}

	@Override
	public List<UserCompensation> loadAllUserCompensationByUserId(final Integer id_user, final Integer year) {
		MyBatisUserCompensationDAO.logger.info("load All User Compensation By User Id in date");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("yearDate", year);
		map.put("id_user", id_user);

		return getSqlSession().selectList("usercompensation.loadAllUserCompensationByUserIdInDate", map);
	}

	@Override
	public UserCompensation loadUserCompensationById(final Integer id) {
		MyBatisUserCompensationDAO.logger.info("Load UserCompensationById id " + id);

		return getSqlSession().selectOne("usercompensation.loadUserCompensationById", id);
	}

	@Override
	public void updateUserCompensation(final UserCompensation userCompensation) {
		MyBatisUserCompensationDAO.logger.info("Update UserCompensationById id " + userCompensation.getId());

		getSqlSession().update("usercompensation.updateUserCompensation", userCompensation);

	}

}
