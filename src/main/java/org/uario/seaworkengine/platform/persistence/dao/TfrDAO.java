package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.TfrUser;

public interface TfrDAO {

	public void createTGRForUser(Integer id_user, TfrUser tfr);

	public List<TfrUser> loadTFRByUser(Integer id_user);

	public void removeTFR(Integer id_tfr);

}
