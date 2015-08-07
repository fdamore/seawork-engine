package org.uario.seaworkengine.statistics;

import java.io.Serializable;

public class ShipTotal implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Integer totalProgramHands;
	private Integer totalProgramMen;

	private Integer totalReviewHands;
	private Integer totalReviewMen;

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
