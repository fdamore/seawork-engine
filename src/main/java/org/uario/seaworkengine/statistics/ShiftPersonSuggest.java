package org.uario.seaworkengine.statistics;

/**
 * @author francesco Suggestion about number of person inlude in shift
 *         re-programmin
 */
public class ShiftPersonSuggest {

	private int[] shift_number = new int[4];

	public ShiftPersonSuggest(int shift_1, int shift_2, int shift_3, int shift_4) {
		super();
		this.shift_number[0] = shift_1;
		this.shift_number[1] = shift_2;
		this.shift_number[2] = shift_3;
		this.shift_number[3] = shift_4;

	}

	public int getMostSuggestedShift(int minumum_shift, boolean decrese) {

		if (minumum_shift < 1)
			minumum_shift = 1;

		if (minumum_shift > 4)
			minumum_shift = 4;

		int index_max_person = minumum_shift;

		for (int i = minumum_shift; i <= 4; i++) {
			if (this.shift_number[i - 1] > 0) {
				index_max_person = i;
				break;
			}

		}

		if (decrese) {
			this.shift_number[index_max_person - 1]--;
		}

		return index_max_person;
	}

	public int getPersonSuggested(int shift_no) {
		return this.shift_number[shift_no];
	}

}
