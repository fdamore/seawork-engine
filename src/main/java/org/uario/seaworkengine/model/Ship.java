package org.uario.seaworkengine.model;

public class Ship {
	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	public static long getSerialversionuid() {
		return Ship.serialVersionUID;
	}

	private Integer	id;
	private String	line;
	private String	name;
	private String	shipcondition;
	private String	shiptype;
	private String	twtype;

	public int getId() {
		return this.id;
	}

	public String getLine() {
		return this.line;
	}

	public String getName() {
		return this.name;
	}

	public String getShipcondition() {
		return this.shipcondition;
	}

	public String getShiptype() {
		return this.shiptype;
	}

	public String getTwtype() {
		return this.twtype;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setLine(final String line) {
		this.line = line;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setShipcondition(final String shipcondition) {
		this.shipcondition = shipcondition;
	}

	public void setShiptype(final String shiptype) {
		this.shiptype = shiptype;
	}

	public void setTwtype(final String twtype) {
		this.twtype = twtype;
	}

}
