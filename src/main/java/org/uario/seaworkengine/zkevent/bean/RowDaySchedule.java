package org.uario.seaworkengine.zkevent.bean;

import java.util.ArrayList;
import java.util.List;

import org.uario.seaworkengine.model.Schedule;

public class RowDaySchedule {

	private Schedule	item1;

	private Schedule	item10;

	private Schedule	item11;
	private Schedule	item12;
	private Schedule	item13;
	private Schedule	item14;
	private Schedule	item15;
	private Schedule	item16;
	private Schedule	item17;
	private Schedule	item18;
	private Schedule	item19;
	private Schedule	item2;
	private Schedule	item20;
	private Schedule	item21;
	private Schedule	item22;
	private Schedule	item23;
	private Schedule	item24;
	private Schedule	item25;
	private Schedule	item26;
	private Schedule	item27;
	private Schedule	item28;
	private Schedule	item29;
	private Schedule	item3;
	private Schedule	item30;
	private Schedule	item31;
	private Schedule	item4;
	private Schedule	item5;
	private Schedule	item6;
	private Schedule	item7;
	private Schedule	item8;
	private Schedule	item9;

	// used to visualize it
	private String		name_user;

	private Integer		user;

	public Schedule getItem1() {
		return this.item1;
	}

	public Schedule getItem10() {
		return this.item10;
	}

	public Schedule getItem11() {
		return this.item11;
	}

	public Schedule getItem12() {
		return this.item12;
	}

	public Schedule getItem13() {
		return this.item13;
	}

	public Schedule getItem14() {
		return this.item14;
	}

	public Schedule getItem15() {
		return this.item15;
	}

	public Schedule getItem16() {
		return this.item16;
	}

	public Schedule getItem17() {
		return this.item17;
	}

	public Schedule getItem18() {
		return this.item18;
	}

	public Schedule getItem19() {
		return this.item19;
	}

	public Schedule getItem2() {
		return this.item2;
	}

	public Schedule getItem20() {
		return this.item20;
	}

	public Schedule getItem21() {
		return this.item21;
	}

	public Schedule getItem22() {
		return this.item22;
	}

	public Schedule getItem23() {
		return this.item23;
	}

	public Schedule getItem24() {
		return this.item24;
	}

	public Schedule getItem25() {
		return this.item25;
	}

	public Schedule getItem26() {
		return this.item26;
	}

	public Schedule getItem27() {
		return this.item27;
	}

	public Schedule getItem28() {
		return this.item28;
	}

	public Schedule getItem29() {
		return this.item29;
	}

	public Schedule getItem3() {
		return this.item3;
	}

	public Schedule getItem30() {
		return this.item30;
	}

	public Schedule getItem31() {
		return this.item31;
	}

	public Schedule getItem4() {
		return this.item4;
	}

	public Schedule getItem5() {
		return this.item5;
	}

	public Schedule getItem6() {
		return this.item6;
	}

	public Schedule getItem7() {
		return this.item7;
	}

	public Schedule getItem8() {
		return this.item8;
	}

	public Schedule getItem9() {
		return this.item9;
	}

	public String getName_user() {
		return this.name_user;
	}

	public Schedule getSchedule(final Integer day) {
		if ((day < 1) || (day > 31)) {
			return null;
		}

		switch (day) {

		case 1: {
			return this.getItem1();
		}

		case 2: {
			return this.getItem2();
		}

		case 3: {
			return this.getItem3();
		}

		case 4: {
			return this.getItem4();
		}

		case 5: {
			return this.getItem5();
		}

		case 6: {
			return this.getItem6();
		}

		case 7: {
			return this.getItem7();
		}

		case 8: {
			return this.getItem8();
		}

		case 9: {
			return this.getItem9();
		}

		case 10: {
			return this.getItem10();
		}

		case 11: {
			return this.getItem11();
		}

		case 12: {
			return this.getItem12();
		}

		case 13: {
			return this.getItem13();
		}

		case 14: {
			return this.getItem14();
		}

		case 15: {
			return this.getItem15();
		}

		case 16: {
			return this.getItem16();
		}

		case 17: {
			return this.getItem17();
		}

		case 18: {
			return this.getItem18();
		}

		case 19: {
			return this.getItem19();
		}

		case 20: {
			return this.getItem20();
		}

		case 21: {
			return this.getItem21();
		}

		case 22: {
			return this.getItem22();
		}

		case 23: {
			return this.getItem23();
		}

		case 24: {
			return this.getItem24();
		}

		case 25: {
			return this.getItem25();
		}

		case 26: {
			return this.getItem26();
		}

		case 27: {
			return this.getItem27();
		}

		case 28: {
			return this.getItem28();
		}

		case 29: {
			return this.getItem29();
		}

		case 30: {
			return this.getItem30();
		}

		case 31: {
			return this.getItem31();
		}

		}

		return null;
	}

	public List<Schedule> getSchedule(final Integer dateBegin, final Integer dateEnd) {

		if ((dateBegin < 1) || (dateBegin > 31) || (dateEnd < 1) || (dateEnd > 31)) {
			return null;
		}

		final List<Schedule> schedules = new ArrayList<Schedule>();

		for (int i = dateBegin; i <= dateEnd; i++) {
			schedules.add(this.getSchedule(i));
		}

		return schedules;

	}

	public Integer getUser() {
		return this.user;
	}

	public void setItem1(final Schedule item1) {
		this.item1 = item1;
	}

	public void setItem10(final Schedule item10) {
		this.item10 = item10;
	}

	public void setItem11(final Schedule item11) {
		this.item11 = item11;
	}

	public void setItem12(final Schedule item12) {
		this.item12 = item12;
	}

	public void setItem13(final Schedule item13) {
		this.item13 = item13;
	}

	public void setItem14(final Schedule item14) {
		this.item14 = item14;
	}

	public void setItem15(final Schedule item15) {
		this.item15 = item15;
	}

	public void setItem16(final Schedule item16) {
		this.item16 = item16;
	}

	public void setItem17(final Schedule item17) {
		this.item17 = item17;
	}

	public void setItem18(final Schedule item18) {
		this.item18 = item18;
	}

	public void setItem19(final Schedule item19) {
		this.item19 = item19;
	}

	public void setItem2(final Schedule item2) {
		this.item2 = item2;
	}

	public void setItem20(final Schedule item20) {
		this.item20 = item20;
	}

	public void setItem21(final Schedule item21) {
		this.item21 = item21;
	}

	public void setItem22(final Schedule item22) {
		this.item22 = item22;
	}

	public void setItem23(final Schedule item23) {
		this.item23 = item23;
	}

	public void setItem24(final Schedule item24) {
		this.item24 = item24;
	}

	public void setItem25(final Schedule item25) {
		this.item25 = item25;
	}

	public void setItem26(final Schedule item26) {
		this.item26 = item26;
	}

	public void setItem27(final Schedule item27) {
		this.item27 = item27;
	}

	public void setItem28(final Schedule item28) {
		this.item28 = item28;
	}

	public void setItem29(final Schedule item29) {
		this.item29 = item29;
	}

	public void setItem3(final Schedule item3) {
		this.item3 = item3;
	}

	public void setItem30(final Schedule item30) {
		this.item30 = item30;
	}

	public void setItem31(final Schedule item31) {
		this.item31 = item31;
	}

	public void setItem4(final Schedule item4) {
		this.item4 = item4;
	}

	public void setItem5(final Schedule item5) {
		this.item5 = item5;
	}

	public void setItem6(final Schedule item6) {
		this.item6 = item6;
	}

	public void setItem7(final Schedule item7) {
		this.item7 = item7;
	}

	public void setItem8(final Schedule item8) {
		this.item8 = item8;
	}

	public void setItem9(final Schedule item9) {
		this.item9 = item9;
	}

	public void setName_user(final String name_user) {
		this.name_user = name_user;
	}

	public void setUser(final Integer user) {
		this.user = user;
	}

}
