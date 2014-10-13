package org.uario.seaworkengine.zkevent.bean;

import java.io.Serializable;
import java.util.Date;

public class ItemRowScheduler implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private String				anchor1;

	private String				anchor2;

	private String				anchor3;

	private String				anchor4;

	private Date				date_scheduler;

	private Integer				id;

	public String getAnchor1() {
		return this.anchor1;
	}

	public String getAnchor2() {
		return this.anchor2;
	}

	public String getAnchor3() {
		return this.anchor3;
	}

	public String getAnchor4() {
		return this.anchor4;
	}

	public Date getDate_scheduler() {
		return this.date_scheduler;
	}

	public Integer getId() {
		return this.id;
	}

	public void setAnchor1(final String anchor1) {
		this.anchor1 = anchor1;
	}

	public void setAnchor2(final String anchor2) {
		this.anchor2 = anchor2;
	}

	public void setAnchor3(final String anchor3) {
		this.anchor3 = anchor3;
	}

	public void setAnchor4(final String anchor4) {
		this.anchor4 = anchor4;
	}

	public void setDate_scheduler(final Date date_scheduler) {
		this.date_scheduler = date_scheduler;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

}