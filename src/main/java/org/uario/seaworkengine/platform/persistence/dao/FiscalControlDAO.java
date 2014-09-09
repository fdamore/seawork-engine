package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.FiscalControl;

public interface FiscalControlDAO {

	public void createFCForUser(Integer id_user, FiscalControl fiscalcontrol);

	public List<FiscalControl> loadFiscalControlByUser(Integer id_user);

	public void removeFiscalControl(Integer id_fc);

	public void updateFC(FiscalControl fiscalcontrol);

}
