package org.uario.seaworkengine.platform.persistence.dao;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.Contestation;

public interface IContestation {

	public void createContestation(int id, String typ, Date date_contestation, String note, int id_user, String stop_from, String stop_to);

	public Contestation loadContestation(int id);

	public List<Contestation> loadUserContestation(int id_user);

	public void removeContestation(int id);

	public void updateContestation(Contestation contestation);

}
