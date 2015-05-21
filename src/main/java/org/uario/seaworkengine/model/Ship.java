package org.uario.seaworkengine.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
@XmlAccessorType(XmlAccessType.FIELD)
public class Ship implements Serializable {
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
	
	private Boolean nowork;
	
	private Boolean activityh;
	

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

	public int getId() {
		return this.id;
	}

	public String getLine() {
		return this.line;
	}

	public String getName() {
		
		return name; 
			
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

	@Override
	public String toString() {
		if (activityh!=null && nowork!=null){
			if (activityh || nowork){
				return this.name+"*";
			}else{
				return name; 
			}			
		}
		return name; 
	}

	public Boolean getNowork() {
		return nowork;
	}

	public void setNowork(Boolean nowork) {
		this.nowork = nowork;
	}

	public Boolean getActivityh() {
		return activityh;
	}

	public void setActivityh(Boolean activityh) {
		this.activityh = activityh;
	}
	
	
		
	

}
