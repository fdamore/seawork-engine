package org.uario.seaworkengine.statistics;

import java.io.Serializable;

public class ShipTotal implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Double	containerInvoice;
	private Double	handswork;

	private Double	menwork;
	private Integer	monthInvoice;

	private Integer	totalProgramHands;
	private Integer	totalProgramMen;

	private Integer	totalReviewHands;
	private Integer	totalReviewMen;

	public Double getContainerInvoice() {
		return this.containerInvoice;
	}

	public Double getHandswork() {
		return this.handswork;
	}

	public Double getMenwork() {
		return this.menwork;
	}

	public Integer getMonthInvoice() {
		return this.monthInvoice;
	}

	public Integer getTotalProgramHands() {
		return this.totalProgramHands;
	}

	public Integer getTotalProgramMen() {
		return this.totalProgramMen;
	}

	public Integer getTotalReviewHands() {
		return this.totalReviewHands;
	}

	public Integer getTotalReviewMen() {
		return this.totalReviewMen;
	}

	public void setContainerInvoice(final Double containerInvoice) {
		this.containerInvoice = containerInvoice;
	}

	public void setHandswork(final Double handswork) {
		this.handswork = handswork;
	}

	public void setMenwork(final Double menwork) {
		this.menwork = menwork;
	}

	public void setMonthInvoice(final Integer monthInvoice) {
		this.monthInvoice = monthInvoice;
	}

	public void setTotalProgramHands(final Integer totalProgramHands) {
		this.totalProgramHands = totalProgramHands;
	}

	public void setTotalProgramMen(final Integer totalProgramMen) {
		this.totalProgramMen = totalProgramMen;
	}

	public void setTotalReviewHands(final Integer totalReviewHands) {
		this.totalReviewHands = totalReviewHands;
	}

	public void setTotalReviewMen(final Integer totalReviewMen) {
		this.totalReviewMen = totalReviewMen;
	}

}
