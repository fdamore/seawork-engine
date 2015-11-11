package org.uario.seaworkengine.model;

import java.io.Serializable;

public class ReportItem implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Double				apr;
	private String				argument;
	private Double				aug;
	private Double				avg;
	private Double				dec;
	private Double				feb;
	private Double				gen;
	private Boolean				isTaskROW;
	private Double				jul;
	private Double				jun;
	private Double				mar;
	private Double				may;
	private Double				nov;

	private Double				oct;

	private Double				sep;
	private Double				tot;

	public Double getApr() {
		return this.apr;
	}

	public String getArgument() {
		return this.argument;
	}

	public Double getAug() {
		return this.aug;
	}

	public Double getAvg() {
		return this.avg;
	}

	public Double getDec() {
		return this.dec;
	}

	public Double getFeb() {
		return this.feb;
	}

	public Double getGen() {
		return this.gen;
	}

	public Boolean getIsTaskROW() {
		return this.isTaskROW;
	}

	public Double getJul() {
		return this.jul;
	}

	public Double getJun() {
		return this.jun;
	}

	public Double getMar() {
		return this.mar;
	}

	public Double getMay() {
		return this.may;
	}

	public Double getNov() {
		return this.nov;
	}

	public Double getOct() {
		return this.oct;
	}

	public Double getSep() {
		return this.sep;
	}

	public Double getTot() {
		return this.tot;
	}

	public void setApr(final Double apr) {
		this.apr = apr;
	}

	public void setArgument(final String argument) {
		this.argument = argument;
	}

	public void setAug(final Double aug) {
		this.aug = aug;
	}

	public void setAvg(final Double avg) {
		this.avg = avg;
	}

	public void setDec(final Double dec) {
		this.dec = dec;
	}

	public void setFeb(final Double feb) {
		this.feb = feb;
	}

	public void setGen(final Double gen) {
		this.gen = gen;
	}

	public void setIsTaskROW(final Boolean isTaskROW) {
		this.isTaskROW = isTaskROW;
	}

	public void setJul(final Double jul) {
		this.jul = jul;
	}

	public void setJun(final Double jun) {
		this.jun = jun;
	}

	public void setMar(final Double mar) {
		this.mar = mar;
	}

	public void setMay(final Double may) {
		this.may = may;
	}

	public void setNov(final Double nov) {
		this.nov = nov;
	}

	public void setOct(final Double oct) {
		this.oct = oct;
	}

	public void setSep(final Double sep) {
		this.sep = sep;
	}

	public void setTot(final Double tot) {
		this.tot = tot;
	}

}
