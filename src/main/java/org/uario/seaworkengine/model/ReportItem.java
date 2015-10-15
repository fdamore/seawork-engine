package org.uario.seaworkengine.model;

import java.io.Serializable;

public class ReportItem implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Integer	apr;
	private String	argument;
	private Integer	aug;
	private Integer	dec;
	private Integer	feb;
	private Integer	gen;
	private Integer	jul;
	private Integer	jun;
	private Integer	mar;
	private Integer	may;
	private Integer	nov;
	private Integer	oct;
	private Integer	sep;

	public Integer getApr() {
		return this.apr;
	}

	public String getArgument() {
		return this.argument;
	}

	public Integer getAug() {
		return this.aug;
	}

	public Integer getDec() {
		return this.dec;
	}

	public Integer getFeb() {
		return this.feb;
	}

	public Integer getGen() {
		return this.gen;
	}

	public Integer getJul() {
		return this.jul;
	}

	public Integer getJun() {
		return this.jun;
	}

	public Integer getMar() {
		return this.mar;
	}

	public Integer getMay() {
		return this.may;
	}

	public Integer getNov() {
		return this.nov;
	}

	public Integer getOct() {
		return this.oct;
	}

	public Integer getSep() {
		return this.sep;
	}

	public void setApr(final Integer apr) {
		this.apr = apr;
	}

	public void setArgument(final String argument) {
		this.argument = argument;
	}

	public void setAug(final Integer aug) {
		this.aug = aug;
	}

	public void setDec(final Integer dec) {
		this.dec = dec;
	}

	public void setFeb(final Integer feb) {
		this.feb = feb;
	}

	public void setGen(final Integer gen) {
		this.gen = gen;
	}

	public void setJul(final Integer jul) {
		this.jul = jul;
	}

	public void setJun(final Integer jun) {
		this.jun = jun;
	}

	public void setMar(final Integer mar) {
		this.mar = mar;
	}

	public void setMay(final Integer may) {
		this.may = may;
	}

	public void setNov(final Integer nov) {
		this.nov = nov;
	}

	public void setOct(final Integer oct) {
		this.oct = oct;
	}

	public void setSep(final Integer sep) {
		this.sep = sep;
	}

}
