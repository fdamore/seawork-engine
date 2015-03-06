package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class LockTable implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	public static long getSerialversionuid() {
		return LockTable.serialVersionUID;
	}

	private Integer		id;
	private Integer		id_user;

	private Integer		id_user_closer;

	private String		table_type;

	private Timestamp	time_start;

	private Timestamp	time_to;

	public Integer getId() {
		return this.id;
	}

	public Integer getId_user() {
		return this.id_user;
	}

	public Integer getId_user_closer() {
		return this.id_user_closer;
	}

	public String getTable_type() {
		return this.table_type;
	}

	public Timestamp getTime_start() {
		return this.time_start;
	}

	public Timestamp getTime_to() {
		return this.time_to;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setId_user(final Integer id_user) {
		this.id_user = id_user;
	}

	public void setId_user_closer(final Integer id_user_closer) {
		this.id_user_closer = id_user_closer;
	}

	public void setTable_type(final String table_type) {
		this.table_type = table_type;
	}

	public void setTime_start(final Timestamp time_start) {
		this.time_start = time_start;
	}

	public void setTime_to(final Timestamp time_to) {
		this.time_to = time_to;
	}

}
