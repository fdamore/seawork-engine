package org.uario.seaworkengine.model;

import java.io.Serializable;

import org.uario.seaworkengine.utility.Utility;

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
	private Double				nov					= 0.0;
	
	private Double				oct					= 0.0;
	
	private Double				sep					= 0.0;
	private Double				tot;
	
	public Double calculateAvg() {
		
		final Double total = this.getTotalMonth();
		int num_month = 0;
		
		if ((this.getGen() != null) && !this.getGen().equals(0.0)) {
			num_month++;
		}
		if ((this.getFeb() != null) && !this.getFeb().equals(0.0)) {
			num_month++;
		}
		if ((this.getMar() != null) && !this.getMar().equals(0.0)) {
			num_month++;
		}
		if ((this.getApr() != null) && !this.getApr().equals(0.0)) {
			num_month++;
		}
		if ((this.getMay() != null) && !this.getMay().equals(0.0)) {
			num_month++;
		}
		if ((this.getJun() != null) && !this.getJun().equals(0.0)) {
			num_month++;
		}
		if ((this.getJul() != null) && !this.getJul().equals(0.0)) {
			num_month++;
		}
		if ((this.getAug() != null) && !this.getAug().equals(0.0)) {
			num_month++;
		}
		if ((this.getSep() != null) && !this.getSep().equals(0.0)) {
			num_month++;
		}
		if ((this.getOct() != null) && !this.getOct().equals(0.0)) {
			num_month++;
		}
		if ((this.getNov() != null) && !this.getNov().equals(0.0)) {
			num_month++;
		}
		if ((this.getDec() != null) && !this.getDec().equals(0.0)) {
			num_month++;
		}
		
		Double current_avg = 0.0;
		
		if (num_month != 0) {
			current_avg = total / num_month;
		}
		
		return current_avg;
	}
	
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
	
	/**
	 * Set global month
	 *
	 * @param month
	 * @param value
	 */
	public Double getMonth(Integer month) {

		switch (month) {
			case 1:
				return this.getGen();
			case 2:
				return this.getFeb();
			case 3:
				return this.getMar();
			
			case 4:
				return this.getApr();
			
			case 5:
				return this.getMay();
			
			case 6:
				return this.getJun();
			
			case 7:
				return this.getJul();
			
			case 8:
				return this.getAug();
			
			case 9:
				return this.getSep();
			
			case 10:
				return this.getOct();
			
			case 11:
				return this.getNov();
			
			case 12:
				return this.getDec();
			
			default:
				return 0.0;
			
		}

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
	
	/**
	 * Calculate totale
	 *
	 * @return
	 */
	public Double getTotalMonth() {
		
		final Double ret = Utility.sum_double(this.getGen(), this.getFeb(), this.getMar(), this.getApr(), this.getMay(), this.getJun(), this.getJul(),
				this.getAug(), this.getSep(), this.getOct(), this.getNov(), this.getDec());
		
		if (ret == null) {
			return 0.0;
		}
		return ret;
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
	
	public void setIsService(final Integer isService) {
		this.isService = isService;
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
	
	/**
	 * Set global month
	 *
	 * @param month
	 * @param value
	 */
	public void setMonth(Integer month, Double value) {

		switch (month) {
			case 1:
				this.setGen(value);
				break;
			case 2:
				this.setFeb(value);
				break;
			case 3:
				this.setMar(value);
				break;
			case 4:
				this.setApr(value);
				break;
			case 5:
				this.setMay(value);
				break;
			case 6:
				this.setJun(value);
				break;
			case 7:
				this.setJul(value);
				break;
			case 8:
				this.setAug(value);
				break;
			case 9:
				this.setSep(value);
				break;
			case 10:
				this.setOct(value);
				break;
			case 11:
				this.setNov(value);
				break;
			case 12:
				this.setDec(value);
				break;
		}

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
