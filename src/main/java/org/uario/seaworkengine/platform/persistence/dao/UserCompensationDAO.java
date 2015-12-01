package org.uario.seaworkengine.platform.persistence.dao;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.UserCompensation;

public interface UserCompensationDAO {

	public void createUserCompensation(UserCompensation userCompensation);

	public void deleteUserCompensation(Integer id);

	public Double getTotalHoursInDateYear(Integer idUser, Date date_submit);

	public Double getTotalHoursInPeriod(Integer idUser, Date date_from, Date date_to);

	public List<UserCompensation> loadAllUserCompensationByUserId(Integer id_user);

	public List<UserCompensation> loadAllUserCompensationByUserId(Integer id_user, Integer year);

	public UserCompensation loadUserCompensationById(Integer id);

	public void updateUserCompensation(UserCompensation userCompensation);

}
