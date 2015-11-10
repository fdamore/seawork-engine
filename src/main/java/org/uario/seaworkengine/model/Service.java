package org.uario.seaworkengine.model;

import java.io.Serializable;

public class Service implements Serializable, Comparable<Service> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private String				description;
	private Integer				id;
	private Boolean				isRZ;
	private String				name;

	@Override
	public int compareTo(final Service o) {
		if (o == null) {
			return -1;
		}
		if (!(o instanceof Service)) {
			return -1;
		}
		if (this.getName() == null) {
			return -1;
		}
		final Service item = o;
		if (item.getName() == null) {
			return 1;
		}
		return this.getName().compareTo(item.getName());
	}

	public String getDescription() {
		return this.description;
	}

	public Integer getId() {
		return this.id;
	}

	public Boolean getIsRZ() {
		return this.isRZ;
	}

	public String getName() {
		return this.name;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setIsRZ(final Boolean isRZ) {
		this.isRZ = isRZ;
	}

	public void setName(final String name) {
		this.name = name;
	}

}
