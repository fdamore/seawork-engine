package org.uario.seaworkengine.model;

import java.io.Serializable;

public class Service implements Serializable, Comparable<Service> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private String				description;
	private Double				franchise_timework;
	// rif. volume - Volume
	private Double				franchise_volume;
	// rif. volume_tw_mct - Volumi TW MCT
	private Double				franchise_volume_tw_mct;
	// rif. volumeunderboard - Volumi Rizz. da Bordo (x Cliente)
	private Double				franchise_volumeunderboard;
	// rif. volumeunderboard_sws - Volumi Rizz. da Bordo (x SWS)
	private Double				franchise_volumeunderboard_sws;
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

	public Double getFranchise_timework() {
		return this.franchise_timework;
	}

	public Double getFranchise_volume() {
		return this.franchise_volume;
	}

	public Double getFranchise_volume_tw_mct() {
		return this.franchise_volume_tw_mct;
	}

	public Double getFranchise_volumeunderboard() {
		return this.franchise_volumeunderboard;
	}

	public Double getFranchise_volumeunderboard_sws() {
		return this.franchise_volumeunderboard_sws;
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

	public void setFranchise_timework(final Double franchise_timework) {
		this.franchise_timework = franchise_timework;
	}

	public void setFranchise_volume(final Double franchise_volume) {
		this.franchise_volume = franchise_volume;
	}

	public void setFranchise_volume_tw_mct(final Double franchise_volume_tw_mct) {
		this.franchise_volume_tw_mct = franchise_volume_tw_mct;
	}

	public void setFranchise_volumeunderboard(final Double franchise_volumeunderboard) {
		this.franchise_volumeunderboard = franchise_volumeunderboard;
	}

	public void setFranchise_volumeunderboard_sws(final Double franchise_volumeunderboard_sws) {
		this.franchise_volumeunderboard_sws = franchise_volumeunderboard_sws;
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
