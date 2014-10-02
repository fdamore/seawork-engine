package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.Contestation;

/**
 * @author Gabriele
 *
 */
public interface IContestation {

	public void createContestation(Contestation item);

	public Contestation loadContestation(int id);

	public List<Contestation> loadUserContestation(int id_user);

	public void removeContestation(int id);

	public void updateContestation(Contestation contestation);

}
