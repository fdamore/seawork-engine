package org.uario.seaworkengine.model;

import java.io.Serializable;

public class ReportItem implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Double				apr					= 0.0;
	private String				argument;
	private Double				aug					= 0.0;
	private Double				avg;
	private Double				dec					= 0.0;
	private Double				feb					= 0.0;
	private Double				gen					= 0.0;
	private Integer				isService			= null;
	private Boolean				isTaskROW			= false;
	private Double				jul					= 0.0;
	private Double				jun					= 0.0;
	private Double				mar					= 0.0;
	private Double				may					= 0.0;

	private Integer				notZeroElement		= 0;

	private Double				nov					= 0.0;
	private Double				oct					= 0.0;
	private Double				sep					= 0.0;
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

	public Integer getIsService() {
		return this.isService;
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

	public Integer getNotZeroElement() {
		return this.notZeroElement;
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
		if ((apr != null) && !apr.equals(0.0)) {
			this.notZeroElement++;
		}
	}

	public void setArgument(final String argument) {
		this.argument = argument;
	}

	public void setAug(final Double aug) {
		this.aug = aug;
		if ((aug != null) && !aug.equals(0.0)) {
			this.notZeroElement++;
		}
	}

	public void setAvg(final Double avg) {
		this.avg = avg;
	}

	public void setDec(final Double dec) {
		this.dec = dec;
		if ((dec != null) && !dec.equals(0.0)) {
			this.notZeroElement++;
		}
	}

	public void setFeb(final Double feb) {
		this.feb = feb;
		if ((feb != null) && !feb.equals(0.0)) {
			this.notZeroElement++;
		}
	}

	public void setGen(final Double gen) {
		this.gen = gen;
		if ((gen != null) && !gen.equals(0.0)) {
			this.notZeroElement++;
		}
	}

	public void setIsService(final Integer isService) {
		this.isService = isService;
	}

	public void setIsTaskROW(final Boolean isTaskROW) {
		this.isTaskROW = isTaskROW;
	}

	public void setJul(final Double jul) {
		this.jul = jul;
		if ((jul != null) && !jul.equals(0.0)) {
			this.notZeroElement++;
		}
	}

	public void setJun(final Double jun) {
		this.jun = jun;
		if ((jun != null) && !jun.equals(0.0)) {
			this.notZeroElement++;
		}
	}

	public void setMar(final Double mar) {
		this.mar = mar;
		if ((mar != null) && !mar.equals(0.0)) {
			this.notZeroElement++;
		}
	}

	public void setMay(final Double may) {
		this.may = may;
		if ((may != null) && !may.equals(0.0)) {
			this.notZeroElement++;
		}
	}

	public void setNov(final Double nov) {
		this.nov = nov;
		if ((nov != null) && !nov.equals(0.0)) {
			this.notZeroElement++;
		}
	}

	public void setOct(final Double oct) {
		this.oct = oct;
		if ((oct != null) && !oct.equals(0.0)) {
			this.notZeroElement++;
		}
	}

	public void setSep(final Double sep) {
		this.sep = sep;
		if ((sep != null) && !sep.equals(0.0)) {
			this.notZeroElement++;
		}
	}

	public void setTot(final Double tot) {
		this.tot = tot;
	}

}
