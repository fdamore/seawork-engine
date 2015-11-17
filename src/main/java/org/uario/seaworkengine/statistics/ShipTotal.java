package org.uario.seaworkengine.statistics;

import java.io.Serializable;

public class ShipTotal implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Double				containerInvoice;
	private Double				containerRZ_TW_MCT;
	private Double				containerRZ_TW_SWS;
	private Double				handswork;
	private Double				handswork_program;

	private Integer				invoice_month;
	private Double				menwork;

	private Integer				month_date;
	private Integer				monthInvoice;
	private Integer				numberofcomplaint;

	private Integer				shift_month;
	private String				task_code;
	private Double				task_hour;

	private Integer				task_id;
	private Integer				totalProgramHands;

	private Integer				totalProgramMen;
	private Integer				totalReviewHands;
	private Integer				totalReviewMen;

	public Double getContainerInvoice() {
		return this.containerInvoice;
	}

	public Double getContainerRZ_TW_MCT() {
		return this.containerRZ_TW_MCT;
	}

	public Double getContainerRZ_TW_SWS() {
		return this.containerRZ_TW_SWS;
	}

	public Double getHandswork() {
		return this.handswork;
	}

	public Double getHandswork_program() {
		return this.handswork_program;
	}

	public Integer getInvoice_month() {
		return this.invoice_month;
	}

	public Double getMenwork() {
		return this.menwork;
	}

	public Integer getMonth_date() {
		return this.month_date;
	}

	public Integer getMonthInvoice() {
		return this.monthInvoice;
	}

	public Integer getNumberofcomplaint() {
		return this.numberofcomplaint;
	}

	public Integer getShift_month() {
		return this.shift_month;
	}

	public String getTask_code() {
		return this.task_code;
	}

	public Double getTask_hour() {
		return this.task_hour;
	}

	public Integer getTask_id() {
		return this.task_id;
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

	public void setContainerRZ_TW_MCT(final Double containerRZ_TW_MCT) {
		this.containerRZ_TW_MCT = containerRZ_TW_MCT;
	}

	public void setContainerRZ_TW_SWS(final Double containerRZ_TW_SWS) {
		this.containerRZ_TW_SWS = containerRZ_TW_SWS;
	}

	public void setHandswork(final Double handswork) {
		this.handswork = handswork;
	}

	public void setHandswork_program(final Double handswork_program) {
		this.handswork_program = handswork_program;
	}

	public void setInvoice_month(final Integer invoice_month) {
		this.invoice_month = invoice_month;
	}

	public void setMenwork(final Double menwork) {
		this.menwork = menwork;
	}

	public void setMonth_date(final Integer month_date) {
		this.month_date = month_date;
	}

	public void setMonthInvoice(final Integer monthInvoice) {
		this.monthInvoice = monthInvoice;
	}

	public void setNumberofcomplaint(final Integer numberofcomplaint) {
		this.numberofcomplaint = numberofcomplaint;
	}

	public void setShift_month(final Integer shift_month) {
		this.shift_month = shift_month;
	}

	public void setTask_code(final String task_code) {
		this.task_code = task_code;
	}

	public void setTask_hour(final Double task_hour) {
		this.task_hour = task_hour;
	}

	public void setTask_id(final Integer task_id) {
		this.task_id = task_id;
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
