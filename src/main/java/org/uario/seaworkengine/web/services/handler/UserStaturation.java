package org.uario.seaworkengine.web.services.handler;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserStaturation {

	private Integer	id_user;

	private Double	saturation;

	public Integer getId_user() {
		return this.id_user;
	}

	public Double getSaturation() {
		return this.saturation;
	}

	public void setId_user(final Integer id_user) {
		this.id_user = id_user;
	}

	public void setSaturation(final Double saturation) {
		this.saturation = saturation;
	}

}
