package org.uario.seaworkengine.zkevent.comparator;

import java.util.Comparator;

import org.uario.seaworkengine.model.DetailFinalSchedule;

public class IdEmployComparator implements Comparator<DetailFinalSchedule> {

	private final boolean	asc_dir;

	public IdEmployComparator(final boolean asc) {
		this.asc_dir = asc;
	}

	@Override
	public int compare(final DetailFinalSchedule item_1, final DetailFinalSchedule item_2) {

		final String id_1 = item_1.getEmployee_identification();
		final String id_2 = item_2.getEmployee_identification();

		if ((id_1 != null) && (id_2 != null)) {

			if (this.asc_dir) {
				return id_1.compareTo(id_2);
			} else {
				return id_2.compareTo(id_1);
			}
		}

		else {
			return -1;
		}

	}

}
