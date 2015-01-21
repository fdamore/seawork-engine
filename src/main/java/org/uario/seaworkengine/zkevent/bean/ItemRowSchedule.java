package org.uario.seaworkengine.zkevent.bean;

import java.io.Serializable;

import org.uario.seaworkengine.model.Schedule;

public class ItemRowSchedule implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private String				anchor1;

	private String				anchor2;

	private String				anchor3;

	private String				anchor4;

	private Double				anchorValue1;

	private Double				anchorValue2;

	private Double				anchorValue3;

	private Double				anchorValue4;

	private final RowSchedule	currentRow;

	private Schedule			schedule;

	public ItemRowSchedule(final RowSchedule row) {
		this.currentRow = row;

	}

	public ItemRowSchedule(final RowSchedule row, final Schedule schedule) {
		this.schedule = schedule;
		this.currentRow = row;

	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof ItemRowSchedule)) {
			return false;
		}

		boolean check_1 = false;
		boolean check_2 = false;
		boolean check_3 = false;
		boolean check_4 = false;

		final ItemRowSchedule compare_itm = (ItemRowSchedule) obj;

		if ((this.getAnchor1() == null) && (compare_itm.getAnchor1() != null)) {
			return false;
		} else if (this.getAnchor1() != null) {
			check_1 = this.getAnchor1().equals(compare_itm.getAnchor1());
		} else if ((this.getAnchor1() == null) && (compare_itm.getAnchor1() == null)) {
			check_1 = true;
		}

		if ((this.getAnchor2() == null) && (compare_itm.getAnchor2() != null)) {
			return false;
		} else if (this.getAnchor2() != null) {
			check_2 = this.getAnchor2().equals(compare_itm.getAnchor2());
		} else if ((this.getAnchor2() == null) && (compare_itm.getAnchor2() == null)) {
			check_2 = true;
		}

		if ((this.getAnchor3() == null) && (compare_itm.getAnchor3() != null)) {
			return false;
		} else if (this.getAnchor3() != null) {
			check_3 = this.getAnchor3().equals(compare_itm.getAnchor3());
		} else if ((this.getAnchor3() == null) && (compare_itm.getAnchor3() == null)) {
			check_3 = true;
		}

		if ((this.getAnchor4() == null) && (compare_itm.getAnchor4() != null)) {
			return false;
		} else if (this.getAnchor4() != null) {
			check_4 = this.getAnchor4().equals(compare_itm.getAnchor4());
		} else if ((this.getAnchor4() == null) && (compare_itm.getAnchor4() == null)) {
			check_4 = true;
		}

		if (check_1 && check_2 && check_3 && check_4) {
			return true;
		} else {
			return false;
		}

	}

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

	public Double getAnchorValue1() {
		if(anchorValue1 == null)
			return 0.0;
		
		return this.anchorValue1;
	}

	public Double getAnchorValue2() {
		if(anchorValue2 == null)
			return 0.0;
		
		return this.anchorValue2;
	}

	public Double getAnchorValue3() {
		if(anchorValue3 == null)
			return 0.0;
		
		return this.anchorValue3;
	}

	public Double getAnchorValue4() {
		if(anchorValue4 == null)
			return 0.0;
		
		return this.anchorValue4;
	}

	/**
	 * Row Schedule
	 *
	 * @return
	 */
	public RowSchedule getRowSchedule() {
		return this.currentRow;
	}

	public Schedule getSchedule() {
		return this.schedule;
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

	public void setAnchorValue1(final Double anchorValue1) {
		this.anchorValue1 = anchorValue1;
	}

	public void setAnchorValue2(final Double anchorValue2) {
		this.anchorValue2 = anchorValue2;
	}

	public void setAnchorValue3(final Double anchorValue3) {
		this.anchorValue3 = anchorValue3;
	}

	public void setAnchorValue4(final Double anchorValue4) {
		this.anchorValue4 = anchorValue4;
	}

	public void setSchedule(final Schedule schedule) {
		this.schedule = schedule;
	}

}