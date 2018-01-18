package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class TrainingCertificate implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Date				certificate_date;

	private String				description;

	private Double				duration;

	private Date				end_class;

	private Date				expiration_date;

	private Integer				id;

	private String				note;

	private Date				start_class;

	private String				title;

	private String				trainer;

	private String				trainer_type;

	private String				training_level;

	private String				training_task;

	private String				tutor;

	private String				typ;

	private Integer				user_id;

	public Date getCertificate_date() {
		return this.certificate_date;
	}

	public String getDescription() {
		return this.description;
	}

	public Double getDuration() {
		return this.duration;
	}

	public Date getEnd_class() {
		return this.end_class;
	}

	public Date getExpiration_date() {
		return this.expiration_date;
	}

	public Integer getId() {
		return this.id;
	}

	public String getNote() {
		return this.note;
	}

	public Date getStart_class() {
		return this.start_class;
	}

	public String getTitle() {
		return this.title;
	}

	public String getTrainer() {
		return this.trainer;
	}

	public String getTrainer_type() {
		return this.trainer_type;
	}

	public String getTraining_level() {
		return this.training_level;
	}

	public String getTraining_task() {
		return this.training_task;
	}

	public String getTutor() {
		return this.tutor;
	}

	public String getTyp() {
		return this.typ;
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

	public void setDuration(final Double duration) {
		this.duration = duration;
	}

	public void setEnd_class(final Date end_class) {
		this.end_class = end_class;
	}

	public void setExpiration_date(final Date expiration_date) {
		this.expiration_date = expiration_date;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setStart_class(final Date start_class) {
		this.start_class = start_class;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public void setTrainer(final String trainer) {
		this.trainer = trainer;
	}

	public void setTrainer_type(final String trainer_type) {
		this.trainer_type = trainer_type;
	}

	public void setTraining_level(final String training_level) {
		this.training_level = training_level;
	}

	public void setTraining_task(final String training_task) {
		this.training_task = training_task;
	}

	public void setTutor(final String tutor) {
		this.tutor = tutor;
	}

	public void setTyp(final String typ) {
		this.typ = typ;
	}

	public void setUser_id(final Integer user_id) {
		this.user_id = user_id;
	}

}
