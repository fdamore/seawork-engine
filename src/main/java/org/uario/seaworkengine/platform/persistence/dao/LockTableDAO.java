package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.LockTable;

public interface LockTableDAO {

	void createLockTable(LockTable lockTable);

	LockTable loadLastLockTableByTableType(String tableType);

	LockTable loadLockTableById(Integer idLockTable);

	LockTable loadLockTableByTableType(String tableType);

	List<LockTable> loadOpenLockTable();

	void removeLockTable(Integer idLockTable);

	void updateLockTable(LockTable lockTable);

}
