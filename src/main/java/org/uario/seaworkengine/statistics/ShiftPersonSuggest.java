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

	public void decreasePersonOnShift(int shift_n) {
		if (this.shift_number[shift_n] > 0) {
			this.shift_number[shift_n]--;
		}
	}

	public int getMostSuggestedShift(int minumum_shift, boolean decrese) {

		if (minumum_shift < 1)
			minumum_shift = 1;

		if (minumum_shift > 4)
			minumum_shift = 4;

		int res = 1;
		int max_person = 0;

		for (int i = minumum_shift; i <= 4; i++) {
			if (this.shift_number[i - 1] > max_person) {
				res = i;
				max_person = this.shift_number[i - 1];
			}

		}

		if (decrese) {
			this.shift_number[res]--;
		}

		return res;
	}

	public int getPersonSuggested(int shift_no) {
		return this.shift_number[shift_no];
	}

}
