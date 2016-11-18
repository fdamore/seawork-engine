package org.uario.seaworkengine.platform.persistence.dao;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.Contestation;

/**
 * @author Gabriele
 *
 */
public interface IContestation {

	public void createContestation(Contestation item);

	public List<Contestation> loadUserContestation(int id_user);

	public List<Contestation> loadUserContestationByDatePenalty(Integer id_user, Date date);

	public List<Contestation> loadUserContestationByYearPenalty(Integer id_user, Integer year);

	public void removeContestation(int id);

	public void updateContestation(Contestation contestation);

}
