package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class TrainingCertificate implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Date certificate_date;

	private String description;

	private Date expiration_date;

	private Integer id;

	private String title;

	private Integer user_id;

	public Date getCertificate_date() {
		return this.certificate_date;
	}

	public String getDescription() {
		return this.description;
	}

	public Date getExpiration_date() {
		return this.expiration_date;
	}

	public Integer getId() {
		return this.id;
	}

	public String getTitle() {
		return this.title;
	}

	public Integer getUser_id() {
		return this.user_id;
	}

	public void setCertificate_date(final Date certificate_date) {
		this.certificate_date = certificate_date;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setExpiration_date(final Date expiration_date) {
		this.expiration_date = expiration_date;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public void setUser_id(final Integer user_id) {
		this.user_id = user_id;
	}

}
