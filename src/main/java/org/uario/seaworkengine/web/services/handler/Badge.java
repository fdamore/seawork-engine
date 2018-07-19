package org.uario.seaworkengine.web.services.handler;

import java.io.Serializable;
import java.util.Date;

public class Badge implements Serializable, Comparable<Badge> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Date				eventTime;

	// true if the user entry, false if the user exit
	private Boolean				eventType;

	private Integer				id;

	private Integer				idschedule;

	@Override
	public int compareTo(final Badge o) {
		if (o == null) {
			return -1;
		}
		if (!(o instanceof Badge)) {
			return -1;
		}
		if (this.getEventTime() == null) {
			return -1;
		}
		final Badge item = o;
		if (item.getEventTime() == null) {
			return 1;
		}
		return this.getEventTime().compareTo(item.getEventTime());
	}

	public Date getEventTime() {
		return this.eventTime;
	}

	public Boolean getEventType() {
		return this.eventType;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getIdschedule() {
		return this.idschedule;
	}

	public void setEventTime(final Date eventTime) {
		this.eventTime = eventTime;
	}

	public void setEventType(final Boolean eventType) {
		this.eventType = eventType;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setIdschedule(final Integer idschedule) {
		this.idschedule = idschedule;
	}

}
