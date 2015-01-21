package org.uario.seaworkengine.zkevent.bean;

import java.io.Serializable;

public class ItemRowTotalSchedule implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Double				anchor1;

	private Double				anchor2;

	private Double				anchor3;

	private Double				anchor4;

	public ItemRowTotalSchedule() {

		this.anchor1 = 0.0;
		this.anchor2 = 0.0;
		this.anchor3 = 0.0;
		this.anchor4 = 0.0;

	}

	public Double getAnchor1() {
		return this.anchor1;
	}

	public Double getAnchor2() {
		return this.anchor2;
	}

	public Double getAnchor3() {
		return this.anchor3;
	}

	public Double getAnchor4() {
		return this.anchor4;
	}

	public void setAnchor1(final Double anchor1) {
		this.anchor1 = anchor1;
	}

	public void setAnchor2(final Double anchor2) {
		this.anchor2 = anchor2;
	}

	public void setAnchor3(final Double anchor3) {
		this.anchor3 = anchor3;
	}

	public void setAnchor4(final Double anchor4) {
		this.anchor4 = anchor4;
	}

}