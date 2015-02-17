package org.uario.seaworkengine.web.services.synchmodel;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "org.uario.seaworkengine.web.services.synchmodel")
@XmlAccessorType(XmlAccessType.FIELD)
public class Badged implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Date				entrata;
	private Date				uscita;

	public Date getEntrata() {
		return this.entrata;
	}

	public Date getUscita() {
		return this.uscita;
	}

	public void setEntrata(final Date entrata) {
		this.entrata = entrata;
	}

	public void setUscita(final Date uscita) {
		this.uscita = uscita;
	}

}
