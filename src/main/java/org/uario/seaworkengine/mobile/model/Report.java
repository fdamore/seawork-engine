package org.uario.seaworkengine.mobile.model;

public class Report {

	private String	board;

	private Integer	shift_no;

	private Integer	ship_crane_number;

	/**
	 * MANI DI LAVORO
	 */
	private Integer	ship_m_c;

	private String	ship_name;

	private String	ship_note;

	private String	typ_op;

	private String	user_crane;

	private String	user_name;

	private String	user_tag_continue;

	private String	user_tag_task;

	public String getBoard() {
		return this.board;
	}

	public Integer getShift_no() {
		return this.shift_no;
	}

	public Integer getShip_crane_number() {
		return this.ship_crane_number;
	}

	public Integer getShip_m_c() {
		return this.ship_m_c;
	}

	public String getShip_name() {
		return this.ship_name;
	}

	public String getShip_note() {
		return this.ship_note;
	}

	public String getTyp_op() {
		return this.typ_op;
	}

	public String getUser_crane() {
		return this.user_crane;
	}

	public String getUser_name() {
		return this.user_name;
	}

	public String getUser_tag_continue() {
		return this.user_tag_continue;
	}

	public String getUser_tag_task() {
		return this.user_tag_task;
	}

	public void setBoard(final String board) {
		this.board = board;
	}

	public void setShift_no(final Integer shift_no) {
		this.shift_no = shift_no;
	}

	public void setShip_crane_number(final Integer ship_crane_number) {
		this.ship_crane_number = ship_crane_number;
	}

	public void setShip_m_c(final Integer ship_m_c) {
		this.ship_m_c = ship_m_c;
	}

	public void setShip_name(final String ship_name) {
		this.ship_name = ship_name;
	}

	public void setShip_note(final String ship_note) {
		this.ship_note = ship_note;
	}

	public void setTyp_op(final String typ_op) {
		this.typ_op = typ_op;
	}

	public void setUser_crane(final String user_crane) {
		this.user_crane = user_crane;
	}

	public void setUser_name(final String user_name) {
		this.user_name = user_name;
	}

	public void setUser_tag_continue(final String user_tag_continue) {
		this.user_tag_continue = user_tag_continue;
	}

	public void setUser_tag_task(final String user_tag_task) {
		this.user_tag_task = user_tag_task;
	}

}
