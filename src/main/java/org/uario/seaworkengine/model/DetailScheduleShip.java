package org.uario.seaworkengine.model;

public class DetailScheduleShip {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	public static long getSerialversionuid() {
		return DetailScheduleShip.serialVersionUID;
	}

	private Integer	id;
	private Integer	idscheduleship;
	private Integer	iduser;
	private String	operation;
	private Integer	shift;

	public Integer getId() {
		return this.id;
	}

	public Integer getIdscheduleship() {
		return this.idscheduleship;
	}

	public Integer getIduser() {
		return this.iduser;
	}

	public String getOperation() {
		return this.operation;
	}

	public Integer getShift() {
		return this.shift;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setIdscheduleship(final Integer idscheduleship) {
		this.idscheduleship = idscheduleship;
	}

	public void setIduser(final Integer iduser) {
		this.iduser = iduser;
	}

	public void setOperation(final String operation) {
		this.operation = operation;
	}

	public void setShift(final Integer shift) {
		this.shift = shift;
	}

}
