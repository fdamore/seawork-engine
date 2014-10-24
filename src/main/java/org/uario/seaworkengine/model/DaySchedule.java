package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class DaySchedule implements Comparable<DaySchedule>, Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;
	private Date				date_scheduled;
	private Integer				id;
	private Integer				id_user;

	// used form visualize
	private String				name_user;

	private Integer				shift;

	private String				shiftcode;

	@Override
	public int compareTo(final DaySchedule arg0) {
		if (arg0 == null) {
			return -1;
		}
		if (!(arg0 instanceof DaySchedule)) {
			return -1;
		}
		if (this.getDate_scheduled() == null) {
			return -1;
		}
		final DaySchedule item = arg0;

		if (item.getDate_scheduled() == null) {
			return 1;
		}
		return this.getDate_scheduled().compareTo(item.getDate_scheduled());
	}

	public Date getDate_scheduled() {
		return this.date_scheduled;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getId_user() {
		return this.id_user;
	}

	public String getName_user() {
		return this.name_user;
	}

	public Integer getShift() {
		return this.shift;
	}

	public String getShiftcode() {
		return this.shiftcode;
	}

	public void setDate_scheduled(final Date date_scheduled) {
		this.date_scheduled = date_scheduled;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setId_user(final Integer id_user) {
		this.id_user = id_user;
	}

	public void setName_user(final String name_user) {
		this.name_user = name_user;
	}

	public void setShift(final Integer shift) {
		this.shift = shift;
	}

	public void setShiftcode(final String shiftcode) {
		this.shiftcode = shiftcode;
	}

	@Override
	public String toString() {
		if (this.shiftcode == null) {
			return "_";
		}
		else {
			return this.shiftcode;
		}
	}

}
