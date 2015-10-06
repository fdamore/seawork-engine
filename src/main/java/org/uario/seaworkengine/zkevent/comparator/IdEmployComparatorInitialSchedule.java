package org.uario.seaworkengine.zkevent.comparator;

import java.util.Comparator;

import org.uario.seaworkengine.model.DetailInitialSchedule;

public class IdEmployComparatorInitialSchedule implements Comparator<DetailInitialSchedule> {

	private final boolean asc_dir;

	public IdEmployComparatorInitialSchedule(final boolean asc) {
		this.asc_dir = asc;
	}

	@Override
	public int compare(final DetailInitialSchedule item_1, final DetailInitialSchedule item_2) {

		final String id_1_info = item_1.getEmployee_identification();
		final String id_2_info = item_2.getEmployee_identification();

		Integer id_1 = null;
		Integer id_2 = null;

		try {
			id_1 = Integer.parseInt(id_1_info);
			id_2 = Integer.parseInt(id_2_info);
		} catch (final NumberFormatException e) {
			id_1 = null;
			id_2 = null;
		}

		if ((id_1 != null) && (id_2 != null)) {

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

		} else {

			if ((id_1_info != null) && (id_2_info != null)) {

				if (this.asc_dir) {
					return id_1_info.compareTo(id_2_info);
				} else {
					return id_2_info.compareTo(id_1_info);
				}
			}

			else {
				return -1;
			}

		}

	}

}
