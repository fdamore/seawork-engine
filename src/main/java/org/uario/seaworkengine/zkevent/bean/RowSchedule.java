package org.uario.seaworkengine.zkevent.bean;

public class RowSchedule implements Comparable<RowSchedule> {

	private ItemRowSchedule	item_1	= new ItemRowSchedule(this);

	private ItemRowSchedule	item_2	= new ItemRowSchedule(this);

	private ItemRowSchedule	item_3	= new ItemRowSchedule(this);

	private ItemRowSchedule	item_4	= new ItemRowSchedule(this);

	private ItemRowSchedule	item_5	= new ItemRowSchedule(this);

	// used to visualize it
	private String			name_user;

	private Integer			user;

	private String			user_status;

	@Override
	public int compareTo(final RowSchedule o) {
		if (this.getUser_status() == null) {
			return 1;
		}
		if (this.getUser_status().equals(o.getUser_status())) {
			return this.getName_user().compareTo(o.getName_user());
		}

		return this.getUser_status().compareTo(o.getUser_status());

	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ItemRowSchedule)) {
			return false;
		}

		final RowSchedule item = (RowSchedule) obj;

		if ((item.user == null) || (this.user == null)) {
			return false;
		}

		return this.user.equals(item.user);

	}

	public ItemRowSchedule getItem_1() {
		return this.item_1;
	}

	public ItemRowSchedule getItem_2() {
		return this.item_2;
	}

	public ItemRowSchedule getItem_3() {
		return this.item_3;
	}

	public ItemRowSchedule getItem_4() {
		return this.item_4;
	}

	public ItemRowSchedule getItem_5() {
		return this.item_5;
	}

	public String getName_user() {
		return this.name_user;
	}

	public Integer getUser() {
		return this.user;
	}

	public String getUser_status() {
		return this.user_status;
	}

	public void setItem_1(final ItemRowSchedule item_1) {
		this.item_1 = item_1;
	}

	public void setItem_2(final ItemRowSchedule item_2) {
		this.item_2 = item_2;
	}

	public void setItem_3(final ItemRowSchedule item_3) {
		this.item_3 = item_3;
	}

	public void setItem_4(final ItemRowSchedule item_4) {
		this.item_4 = item_4;
	}

	public void setItem_5(final ItemRowSchedule item_5) {
		this.item_5 = item_5;
	}

	public void setName_user(final String name_user) {
		this.name_user = name_user;
	}

	public void setUser(final Integer user) {
		this.user = user;
	}

	public void setUser_status(final String user_status) {
		this.user_status = user_status;
	}

}
