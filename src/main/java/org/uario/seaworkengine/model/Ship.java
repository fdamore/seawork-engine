package org.uario.seaworkengine.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
@XmlAccessorType(XmlAccessType.FIELD)
public class Ship implements Serializable {

	public static Ship EMPTY = new Ship();

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	static {

		Ship.EMPTY.setName("NESSUNA");

	}

	public static long getSerialversionuid() {
		return Ship.serialVersionUID;
	}

	private Boolean activityh;

	private Integer id;

	private String line;

	private String name;

	private String note;

	private Boolean nowork;

	// using for mobile app
	private Integer rifSWS;

	private String shipcondition;

	private String shiptype;

	private String twtype;

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Ship)) {
			return false;
		}

		final Ship item = (Ship) obj;

		return this.getName().equals(item.getName());

	}

	public Boolean getActivityh() {
		return this.activityh;
	}

	public int getId() {
		return this.id;
	}

	public String getLine() {
		return this.line;
	}

	public String getName() {

		return this.name;

	}

	public String getNote() {
		return this.note;
	}

	public Boolean getNowork() {
		return this.nowork;
	}

	public Integer getRifSWS() {
		return this.rifSWS;
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

	public void setActivityh(final Boolean activityh) {
		this.activityh = activityh;
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

	public void setNote(final String note) {
		this.note = note;
	}

	public void setNowork(final Boolean nowork) {
		this.nowork = nowork;
	}

	public void setRifSWS(final Integer rifSWS) {
		this.rifSWS = rifSWS;
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

	@Override
	public String toString() {
		if ((this.activityh != null) && (this.nowork != null)) {
			if (this.activityh || this.nowork) {
				return this.name + "*";
			} else {
				return this.name;
			}
		}
		return this.name;
	}

}
