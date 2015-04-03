package org.uario.seaworkengine.platform.persistence.dao;

import org.uario.seaworkengine.model.LockTable;

public interface LockTableDAO {

	public void createLockTable(LockTable lockTable);

	public LockTable loadLastLockTableByTableType(String tableType);

	public LockTable loadLockTableById(Integer idLockTable);

	public LockTable loadLockTableByTableType(String tableType);

	public void removeLockTable(Integer idLockTable);

	public void updateLockTable(LockTable lockTable);

}
