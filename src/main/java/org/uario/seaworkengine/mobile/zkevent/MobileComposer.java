package org.uario.seaworkengine.mobile.zkevent;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.uario.seaworkengine.mobile.model.Badge;
import org.uario.seaworkengine.mobile.model.InitialSchedule;
import org.uario.seaworkengine.mobile.model.InitialScheduleSingleDetail;
import org.uario.seaworkengine.mobile.model.MobileUserDetail;
import org.uario.seaworkengine.model.Crane;
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailFinalScheduleShip;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.IScheduleShip;
import org.uario.seaworkengine.platform.persistence.dao.IShip;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.BoardTag;
import org.uario.seaworkengine.utility.Utility;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;

public class MobileComposer {

	@SuppressWarnings("rawtypes")
	private class MyDateFormatConverter implements Converter {
		/**
		 * Convert String to Date.
		 *
		 * @param val  date in string form
		 * @param comp associated component
		 * @param ctx  bind context for associate Binding and extra parameter (e.g.
		 *             format)
		 * @return the converted Date
		 */
		@Override
		public Object coerceToBean(final Object val, final Component comp, final BindContext ctx) {
			final String format = (String) ctx.getConverterArg("format");
			if (format == null) {
				throw new NullPointerException("format attribute not found");
			}
			final String date = (String) val;

			try {
				return date == null ? null : new SimpleDateFormat(format).parse(date);
			} catch (final ParseException e) {
				throw UiException.Aide.wrap(e);
			}

		}

		/**
		 * Convert Date to String.
		 *
		 * @param val  date to be converted
		 * @param comp associated component
		 * @param ctx  bind context for associate Binding and extra parameter (e.g.
		 *             format)
		 * @return the converted String
		 */
		@Override
		public Object coerceToUi(final Object val, final Component comp, final BindContext ctx) {
			// user sets format in annotation of binding or args when calling
			// binder.addPropertyBinding()
			final String format = (String) ctx.getConverterArg("format");
			if (format == null) {
				throw new NullPointerException("format attribute not found");
			}
			final Date date = (Date) val;
			return date == null ? null : new SimpleDateFormat(format).format(date);
		}
	}

	@SuppressWarnings("rawtypes")
	private class MyMobileTaskConverter implements Converter {

		@Override
		public Object coerceToBean(final Object val, final Component comp, final BindContext ctx) {

			return null;

		}

		@Override
		public Object coerceToUi(final Object val, final Component comp, final BindContext ctx) {

			if (!(val instanceof InitialScheduleSingleDetail)) {
				return null;
			}

			final InitialScheduleSingleDetail init_val = (InitialScheduleSingleDetail) val;

			if (BooleanUtils.isNotTrue(init_val.getDetail_schedule().getRevised())) {
				return MobileComposer.this.getTaskStringView(init_val);
			} else {
				return MobileComposer.this.getTaskStringViewProgrammed(init_val.getDetail_schedule());
			}

		}

	}

	@SuppressWarnings("rawtypes")
	private class MyShipConverter implements Converter {

		@Override
		public Object coerceToBean(final Object val, final Component comp, final BindContext ctx) {
			return null;

		}

		@Override
		public Object coerceToUi(final Object val, final Component comp, final BindContext ctx) {

			if ((val == null) || !(val instanceof DetailScheduleShip)) {
				return "";
			}

			final DetailScheduleShip op = (DetailScheduleShip) val;

			final String op_info = op.getOperation();

			String ship_name = op.getName();

			final Integer h_p = op.getHandswork_program();
			final Integer m_p = op.getMenwork_program();

			if (StringUtils.isEmpty(ship_name)) {
				return "";
			}

			// check if note exists
			if (StringUtils.isNotEmpty(op.getNotedetail())) {
				ship_name = "(*)" + ship_name;
			}

			// return
			String ret = "";

			String op_tag = "";

			if (op_info.equalsIgnoreCase("COMPLETA")) {
				op_tag = "CO";
			}

			if (op_info.equalsIgnoreCase("TWISTER")) {
				op_tag = "TW";
			}

			// ADDING OP
			if (StringUtils.isEmpty(op_tag)) {
				ret = ship_name;
			} else {
				ret = ship_name + "(" + op_tag + ")";
			}

			// ADDING MANI P
			if (h_p != null) {
				ret = ret + "(M.P:" + h_p + ")";
			}

			// ADDING PERSONE P
			if (m_p != null) {
				ret = ret + "(P.P:" + m_p + ")";
			}

			return ret;

		}
	}

	@SuppressWarnings("rawtypes")
	private class MyUserConverter implements Converter {

		@Override
		public Object coerceToBean(final Object val, final Component comp, final BindContext ctx) {
			return null;

		}

		@Override
		public Object coerceToUi(final Object val, final Component comp, final BindContext ctx) {

			if ((val == null) || !(val instanceof InitialScheduleSingleDetail)) {
				return "";
			}

			final InitialScheduleSingleDetail op = (InitialScheduleSingleDetail) val;

			final Person person = op.getPerson();

			String main = Utility.dottedName(person.toString());

			final MobileUserDetail detail_schedule = op.getDetail_schedule();

			// check if there is a note
			if (op.getSchedule() != null) {
				if (StringUtils.isNotEmpty(op.getSchedule().getMobile_note())) {
					main = "(*)" + main;
				}
			}

			// adding info about continue shift
			final Boolean cont_info = detail_schedule.getContinueshift();
			if (BooleanUtils.isTrue(cont_info)) {
				main = main + "{#}";
			}
			if (BooleanUtils.isNotTrue(detail_schedule.getRevised())) {
				return main;
			}

			// adding info board
			final String board = detail_schedule.getBoard();
			if (!StringUtils.isEmpty(board)) {
				main = main + "(" + board.toCharArray()[0] + ") ";
			}

			// adding info ship and crane
			final Integer id_ship = detail_schedule.getId_ship();
			final String crane = detail_schedule.getCrane();
			if ((id_ship != null) && (crane != null)) {

				final Ship ship = MobileComposer.this.shipdao.loadShip(id_ship);
				final String name = ship.getName();
				main = main + "(" + name + " - CR[" + crane + "])";
			}

			// adding info program
			main = main + "(";
			final String info = MobileComposer.this.getTaskStringView(op);
			main = main + info + ")";

			return main;

		}
	}

	private ConfigurationDAO					configurationDao;

	private Integer								crane_p_gru;

	private Boolean								crane_type;

	private DetailFinalScheduleShip				craneListSelected;

	private List<Crane>							cranes_entity;

	private Crane								cranes_entity_selected;

	private Date								date_selection;

	/**
	 * Instace of data converter
	 */
	private final MyDateFormatConverter			dateConverter			= new MyDateFormatConverter();

	private String								end_task;

	private String								h_date_from;

	private String								h_date_to;

	private Integer								h_menwork;

	private String								label_command_ok;

	private List<DetailFinalScheduleShip>		list_cranes;

	private List<InitialScheduleSingleDetail>	list_schedule_selected	= null;

	public List<DetailScheduleShip>				list_selected_ship;

	private List<DetailScheduleShip>			list_ship;

	private List<UserTask>						list_task;

	private String								n_positions;

	private String								note;

	private String								note_ship;

	private PersonDAO							person_dao;

	private ISchedule							schedule_dao;

	private IScheduleShip						schedule_ship_dao;

	private DetailScheduleShip					selectedDetailShip;

	private InitialScheduleSingleDetail			selectedSchedule;

	private Integer								shift_no;

	private String								ship_firstdown;

	private Boolean								ship_h					= Boolean.FALSE;

	private Integer								ship_handswork;

	private String								ship_lastdown;

	private Integer								ship_menwork;

	private String								ship_persondown;

	private String								ship_persononboard;

	private String								ship_rain;

	private Ship								ship_selected;

	private String								ship_sky;

	private String								ship_temperature;

	private String								ship_wind;

	private Boolean								ship_windyday;

	private Boolean								ship_worked;

	private final MyShipConverter				shipConverter			= new MyShipConverter();

	private IShip								shipdao;

	private List<Ship>							ships;

	private String								starting_task;

	private Integer								status_view				= 1;

	private TasksDAO							task_dao;

	/**
	 * Task converter
	 */
	private final MyMobileTaskConverter			taskConverter			= new MyMobileTaskConverter();

	private Boolean								user_continue;

	private String								user_crane_selected;

	private String								user_position;

	private final Integer[]						user_programmed			= new Integer[] { 0, 0, 0, 0 };

	private UserTask							user_task_selected;

	private Boolean								user_visible_adding		= Boolean.FALSE;

	private MyUserConverter						userConverter			= new MyUserConverter();

	private List<InitialScheduleSingleDetail>	users;

	@Command
	@NotifyChange({ "status_view", "list_task", "ships", "ship_selected", "user_crane_selected", "selectedSchedule", "starting_task", "end_task",
			"user_task_selected", "user_visible_adding", "n_positions", "user_continue" })
	public void addComponents() {

		if (this.status_view != 1) {
			return;

		}

		if (CollectionUtils.isEmpty(this.list_schedule_selected)) {
			return;
		}

		// define ships and ship selected
		this.ships = this.listShip(this.date_selection);

		// define the crane
		this.user_crane_selected = null;

		// define continue shift
		this.user_continue = Boolean.FALSE;

		// set starting and end task
		String end_info = "";
		switch (this.shift_no) {
		case 1: {
			end_info = "07:00";
			break;
		}
		case 2: {
			end_info = "13:00";
			break;
		}
		case 3: {
			end_info = "19:00";
			break;
		}
		case 4: {

			final SimpleDateFormat format_date = new SimpleDateFormat("dd/MM/YYYY");
			final Calendar now = Calendar.getInstance();
			now.add(Calendar.DAY_OF_YEAR, 1);
			end_info = format_date.format(now.getTime()) + " 01:00 ";

			break;
		}
		}

		this.starting_task = this.getCurrentInfoTime();
		this.end_task = end_info;

		if (this.list_schedule_selected.size() > 1) {

			// DEFINE INFO FOR MULTIPLE USER
			this.user_visible_adding = Boolean.FALSE;

			this.n_positions = "" + this.list_schedule_selected.size();

			this.list_task = this.task_dao.loadTasksForMobile();
			this.user_task_selected = null;

		} else {

			// DEFINE INFO FOR SINGLE USER
			this.user_visible_adding = Boolean.TRUE;

			// select this
			this.selectedSchedule = this.list_schedule_selected.get(0);

			this.list_task = this.task_dao.loadTasksByUserForMobile(this.selectedSchedule.getPerson().getId());

			// set task default
			final UserTask task_defaukt = this.task_dao.getDefault(this.selectedSchedule.getPerson().getId());
			this.user_task_selected = task_defaukt;

			if (this.user_task_selected != null) {

				Collections.sort(this.list_task, new Comparator<UserTask>() {

					@Override
					public int compare(final UserTask o1, final UserTask o2) {

						if (o1.equals(task_defaukt)) {
							return -1;
						}

						if (o1.equals(MobileComposer.this.user_task_selected)) {
							return -1;
						}
						if (o2.equals(MobileComposer.this.user_task_selected)) {
							return 1;
						} else {
							return o1.compareTo(o2);
						}

					}

				});
			}

		}

		this.status_view = 2;

	}

	@Command
	@NotifyChange({ "list_cranes", "status_view" })
	public void addCrane() {

		if (this.selectedDetailShip == null) {
			return;
		}

		if (this.cranes_entity_selected == null) {
			return;
		}

		Boolean crane_gtw = Boolean.FALSE;
		if (BooleanUtils.isTrue(this.crane_type)) {
			crane_gtw = Boolean.TRUE;
		}

		// define invoice cycle
		final Calendar cal = Calendar.getInstance();
		final int m = cal.get(Calendar.MONTH);

		final DetailFinalScheduleShip new_item = new DetailFinalScheduleShip();
		new_item.setIddetailscheduleship(this.selectedDetailShip.getId());
		new_item.setCrane_gtw(crane_gtw);
		new_item.setP_crane(this.crane_p_gru);
		new_item.setId_crane(this.cranes_entity_selected.getId());
		new_item.setInvoicing_cycle(m + 1);

		// set mobile_user
		final Person person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		new_item.setMobile_user(person_logged.getId());

		if (BooleanUtils.isTrue(this.ship_h)) {

			final Date date_from = this.parseDateString(this.h_date_from);
			final Date date_to = this.parseDateString(this.h_date_to);

			new_item.setActivity_end(date_to);
			new_item.setActivity_start(date_from);

			// set time work
			final Double time_worked = this.getDecimalValue(date_from, date_to);
			new_item.setTimework(time_worked);
			new_item.setMenwork_activityh(this.h_menwork);

		}

		this.schedule_ship_dao.createDetailFinalScheduleShip(new_item);
		this.showGru();
	}

	/**
	 * Add single detail ***SPOSTAMEMTO ***
	 */
	@Command
	@NotifyChange({ "users", "shift_no", "status_view", "selectedSchedule" })
	public void addDetailFinalSchedule() {

		// CHECK INFO
		if (CollectionUtils.isEmpty(this.list_schedule_selected)) {
			return;
		}

		final String starting = this.getStarting_task();
		final String end = this.getEnd_task();
		if (StringUtils.isEmpty(starting) || StringUtils.isEmpty(end)) {
			return;
		}

		if (this.user_task_selected == null) {
			return;
		}

		for (final InitialScheduleSingleDetail itm : this.list_schedule_selected) {

			final Date date_schedule = itm.getSchedule().getDate_schedule();

			final Date dt_starting = this.parseUserWorkTime(date_schedule, starting);
			final Date dt_end = this.parseUserWorkTime(date_schedule, end);
			if ((dt_starting == null) || (dt_end == null)) {
				return;
			}

			Ship ship_itm = this.ship_selected;
			if (this.ship_selected == Ship.EMPTY) {
				ship_itm = null;
			}

			String myposition = this.user_position;
			if (StringUtils.equals(this.user_position, BoardTag.EMPTY)) {
				myposition = null;
			}

			// Create info
			this.createDetailFinalSchedule(dt_starting, dt_end, itm, this.user_task_selected, this.user_crane_selected, ship_itm, myposition,
					this.user_continue);

		}

		// refresh view for user list (status 1)
		this.refreshDataAndCurrentShift();
	}

	/**
	 * Calculate shift and refresh view
	 */
	private void calculateShiftAndRefresh() {

		this.shift_no = 1;
		final Calendar now = Calendar.getInstance();

		final Calendar h1 = Calendar.getInstance();
		h1.set(Calendar.HOUR_OF_DAY, 1);
		h1.set(Calendar.MINUTE, 0);
		h1.set(Calendar.SECOND, 0);

		final Calendar h7 = Calendar.getInstance();
		h7.set(Calendar.HOUR_OF_DAY, 7);
		h7.set(Calendar.MINUTE, 0);
		h7.set(Calendar.SECOND, 0);

		final Calendar h13 = Calendar.getInstance();
		h13.set(Calendar.HOUR_OF_DAY, 13);
		h13.set(Calendar.MINUTE, 0);
		h13.set(Calendar.SECOND, 0);

		final Calendar h19 = Calendar.getInstance();
		h19.set(Calendar.HOUR_OF_DAY, 19);
		h19.set(Calendar.MINUTE, 0);
		h19.set(Calendar.SECOND, 0);

		if ((now.getTime().compareTo(h1.getTime()) >= 0) && (now.getTime().compareTo(h7.getTime()) <= 0)) {
			this.shift_no = 1;
		} else if ((now.getTime().compareTo(h7.getTime()) > 0) && (now.getTime().compareTo(h13.getTime()) <= 0)) {
			this.shift_no = 2;
		} else if ((now.compareTo(h13) > 0) && (now.compareTo(h19) <= 0)) {
			this.shift_no = 3;
		} else {
			this.shift_no = 4;
		}

		// refresh with shift_no
		this.refresh(this.shift_no);
	}

	/**
	 * C reate info fro task
	 *
	 * @param dt_starting
	 * @param dt_end
	 * @param programmedSchedule
	 * @param task
	 * @param crane
	 * @param ship
	 * @param position
	 */
	private void createDetailFinalSchedule(final Date dt_starting, final Date dt_end, final InitialScheduleSingleDetail programmedSchedule,
			final UserTask task, final String crane, final Ship ship, final String position, final Boolean continue_shift) {

		if ((dt_starting == null) || (dt_end == null)) {
			return;
		}

		// check time integrity
		final Double time = Utility.getTimeDifference(dt_starting, dt_end);
		if (time == null) {
			return;
		}

		final Integer shift_n = programmedSchedule.getDetail_schedule().getShift();

		final DetailFinalSchedule detail_schedule = new DetailFinalSchedule();

		detail_schedule.setId_schedule(programmedSchedule.getSchedule().getId());
		detail_schedule.setShift(shift_n);
		detail_schedule.setContinueshift(continue_shift);

		// task
		detail_schedule.setTask(task.getId());

		// define time
		detail_schedule.setTime_from(new java.sql.Timestamp(dt_starting.getTime()));
		detail_schedule.setTime_to(new java.sql.Timestamp(dt_end.getTime()));

		if (task.getIsabsence()) {
			detail_schedule.setTime_vacation(time);
			detail_schedule.setTime(0.0);

		} else {
			detail_schedule.setTime_vacation(0.0);
			detail_schedule.setTime(time);

		}

		// info ship...
		if (StringUtils.isNotEmpty(crane)) {
			detail_schedule.setCrane(crane);
		}

		if (StringUtils.isNotEmpty(position)) {
			detail_schedule.setBoard(position);
		}

		if (ship != null) {
			detail_schedule.setId_ship(ship.getId());

			// define rif_sws
			final DetailScheduleShip shipdetail = this.selectInitialShipSchedule(this.date_selection, shift_n, ship.getId());
			if (shipdetail == null) {
				detail_schedule.setRif_sws(null);
			} else {
				detail_schedule.setRif_sws(shipdetail.getIdscheduleship());
			}
		}

		// set controller
		final Person person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		detail_schedule.setMobile_user(person_logged.getId());

		this.schedule_dao.createDetailFinalSchedule(detail_schedule);

	}

	@Command
	@NotifyChange({ "status_view", "note", "note_ship", "selectedSchedule", "selectedDetailShip", "user_visible_adding", "n_positions" })
	public void editNote() {

		// note for "TURNI"
		if (this.status_view == 1) {

			if (CollectionUtils.isEmpty(this.list_schedule_selected)) {
				return;
			}

			if (this.list_schedule_selected.size() > 1) {

				// DEFINE INFO FOR MULTIPLE USER
				this.user_visible_adding = Boolean.FALSE;

				this.n_positions = "" + this.list_schedule_selected.size();

				this.note = "";

			} else {

				// DEFINE INFO FOR SINGLE USER
				this.user_visible_adding = Boolean.TRUE;

				this.selectedSchedule = this.list_schedule_selected.get(0);

				this.note = this.selectedSchedule.getSchedule().getMobile_note();

			}

			this.status_view = 3;

		}

		// note for "SHIP"
		if (this.status_view == 4) {

			if (this.list_selected_ship.size() > 1) {

				// DEFINE INFO FOR MULTIPLE USER
				this.user_visible_adding = Boolean.FALSE;

				this.n_positions = "" + this.list_selected_ship.size();

				this.note_ship = "";

			} else {

				// DEFINE INFO FOR SINGLE USER
				this.user_visible_adding = Boolean.TRUE;

				this.selectedDetailShip = this.getTheFirstSelectedShip();

				this.note_ship = this.schedule_ship_dao.getDetailScheduleShipNote(this.selectedDetailShip.getId());

			}

			this.status_view = 5;

		}

	}

	/**
	 * Extract the last info
	 *
	 * @param list
	 * @return
	 */
	private MobileUserDetail extractThelast(final List<MobileUserDetail> list) {

		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		MobileUserDetail ret = list.get(0);

		for (final MobileUserDetail itm : list) {
			if (itm.getTime_from() == null) {
				continue;
			}
			if (ret.getTime_from() == null) {
				ret = itm;
				continue;
			}

			if (itm.getTime_from().after(ret.getTime_from())) {
				ret = itm;
			}
		}

		return ret;

	}

	/**
	 * @param dt
	 * @return
	 */
	private String formatShipDateTime(final Date dt) {

		if (dt == null) {
			return null;
		}

		final SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy HH:mm");
		return format.format(dt);

	}

	public Integer getCrane_p_gru() {
		return this.crane_p_gru;
	}

	public Boolean getCrane_type() {
		return this.crane_type;
	}

	public DetailFinalScheduleShip getCraneListSelected() {
		return this.craneListSelected;
	}

	public List<Crane> getCranes_entity() {
		return this.cranes_entity;
	}

	public Crane getCranes_entity_selected() {
		return this.cranes_entity_selected;
	}

	/**
	 * get current info time for define time task
	 *
	 * @return
	 */
	private String getCurrentInfoTime() {
		final Calendar now = Calendar.getInstance();

		final Integer h = now.get(Calendar.HOUR_OF_DAY);
		final Integer m = now.get(Calendar.MINUTE);

		String info_m = m.toString();
		if (info_m.length() == 1) {
			info_m = "0" + info_m;
		}

		final String current = "" + h + ":" + info_m;
		return current;
	}

	public Date getDate_selection() {
		return this.date_selection;
	}

	public MyDateFormatConverter getDateConverter() {
		return this.dateConverter;
	}

	/**
	 * Get decimal value for time
	 *
	 * @return
	 */
	private Double getDecimalValue(final Date date_from, final Date date_to) {

		Double time = null;

		if ((date_from != null) && (date_to != null)) {
			time = (double) (date_to.getTime() - date_from.getTime());

			time = time / (1000 * 60 * 60);
		}

		return time;

	}

	public String getEnd_task() {
		return this.end_task;
	}

	public String getH_date_from() {
		return this.h_date_from;
	}

	public String getH_date_to() {
		return this.h_date_to;
	}

	public Integer getH_menwork() {
		return this.h_menwork;
	}

	public String getLabel_command_ok() {
		return this.label_command_ok;
	}

	public List<DetailFinalScheduleShip> getList_cranes() {
		return this.list_cranes;
	}

	public List<InitialScheduleSingleDetail> getList_schedule_selected() {
		return this.list_schedule_selected;
	}

	public List<DetailScheduleShip> getList_selected_ship() {
		return this.list_selected_ship;
	}

	public List<DetailScheduleShip> getList_ship() {
		return this.list_ship;
	}

	public List<UserTask> getList_task() {
		return this.list_task;
	}

	public String getN_positions() {
		return this.n_positions;
	}

	public String getNote() {
		return this.note;
	}

	public String getNote_ship() {
		return this.note_ship;
	}

	public DetailScheduleShip getSelectedDetailShip() {
		return this.selectedDetailShip;
	}

	public InitialScheduleSingleDetail getSelectedSchedule() {
		return this.selectedSchedule;
	}

	public Integer getShift_no() {
		return this.shift_no;
	}

	public String getShip_firstdown() {
		return this.ship_firstdown;
	}

	public Boolean getShip_h() {
		return this.ship_h;
	}

	public Integer getShip_handswork() {
		return this.ship_handswork;
	}

	public String getShip_lastdown() {
		return this.ship_lastdown;
	}

	public Integer getShip_menwork() {
		return this.ship_menwork;
	}

	public String getShip_persondown() {
		return this.ship_persondown;
	}

	public String getShip_persononboard() {
		return this.ship_persononboard;
	}

	public String getShip_rain() {
		return this.ship_rain;
	}

	public Ship getShip_selected() {
		return this.ship_selected;
	}

	public String getShip_sky() {
		return this.ship_sky;
	}

	public String getShip_temperature() {
		return this.ship_temperature;
	}

	public String getShip_wind() {
		return this.ship_wind;
	}

	public Boolean getShip_windyday() {
		return this.ship_windyday;
	}

	public Boolean getShip_worked() {
		return this.ship_worked;
	}

	public MyShipConverter getShipConverter() {
		return this.shipConverter;
	}

	public List<Ship> getShips() {
		return this.ships;
	}

	public String getStarting_task() {
		return this.starting_task;
	}

	public Integer getStatus_view() {
		return this.status_view;
	}

	public MyMobileTaskConverter getTaskConverter() {
		return this.taskConverter;
	}

	/**
	 * return task string view
	 *
	 * @param val
	 * @return
	 */
	private String getTaskStringView(final InitialScheduleSingleDetail val) {

		final SimpleDateFormat data_format = new SimpleDateFormat("HH:mm");

		final String code = val.getUser_task().getCode();

		final Date time_from = val.getDetail_schedule().getTime_from();
		final Date time_to = val.getDetail_schedule().getTime_to();

		if ((time_from != null) && (time_to != null)) {

			final String from = data_format.format(time_from);
			final String to = data_format.format(time_to);

			return code + " (" + from + " - " + to + ")";
		} else {
			return code;
		}
	}

	/**
	 * return task string view
	 *
	 * @param val
	 * @return
	 */
	private String getTaskStringViewProgrammed(final MobileUserDetail val) {

		if (val.getProgrammed() == null) {
			return "";
		}

		final SimpleDateFormat data_format = new SimpleDateFormat("HH:mm");

		final StringBuilder builder = new StringBuilder();

		for (final MobileUserDetail itm : val.getProgrammed()) {

			final UserTask task = this.task_dao.loadTask(itm.getTask());

			final String code = task.getCode();

			final Date time_from = itm.getTime_from();
			final Date time_to = itm.getTime_to();

			final String from = data_format.format(time_from);
			final String to = data_format.format(time_to);

			builder.append(code + " (" + from + " - " + to + ")\n");

		}

		return builder.toString();

	}

	/**
	 * return the first selection
	 *
	 * @return
	 */
	private DetailScheduleShip getTheFirstSelectedShip() {

		if (CollectionUtils.isEmpty(this.list_selected_ship)) {
			return null;
		}

		return this.list_selected_ship.get(0);

	}

	public Boolean getUser_continue() {
		return this.user_continue;
	}

	public String getUser_crane_selected() {
		return this.user_crane_selected;
	}

	public String getUser_position() {
		return this.user_position;
	}

	public Integer[] getUser_programmed() {
		return this.user_programmed;
	}

	public UserTask getUser_task_selected() {
		return this.user_task_selected;
	}

	public Boolean getUser_visible_adding() {
		return this.user_visible_adding;
	}

	public MyUserConverter getUserConverter() {
		return this.userConverter;
	}

	public List<InitialScheduleSingleDetail> getUsers() {
		return this.users;
	}

	@AfterCompose
	public void init(@ContextParam(ContextType.COMPONENT) final Component component) throws Exception {

		this.task_dao = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);
		this.shipdao = (IShip) SpringUtil.getBean(BeansTag.SHIP_DAO);
		this.schedule_ship_dao = (IScheduleShip) SpringUtil.getBean(BeansTag.SCHEDULE_SHIP_DAO);
		this.schedule_dao = (ISchedule) SpringUtil.getBean(BeansTag.SCHEDULE_DAO);
		this.configurationDao = (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);
		this.person_dao = (PersonDAO) SpringUtil.getBean(BeansTag.PERSON_DAO);

		// set selection at today
		this.date_selection = Calendar.getInstance().getTime();

		this.refreshDataAndCurrentShift();

	}

	/**
	 * Get ships in date
	 *
	 * @param date_request
	 * @return
	 */
	private List<Ship> listShip(final Date date_request) {

		final Date date_truncate = DateUtils.truncate(date_request, Calendar.DATE);

		final List<Ship> list_ret = this.schedule_ship_dao.loadShipInDate(new Timestamp(date_truncate.getTime()));

		// add empty ship
		list_ret.add(0, Ship.EMPTY);

		return list_ret;

	}

	@Command
	public void logout() {

		Executions.sendRedirect("/j_spring_security_logout");

	}

	@Command
	@NotifyChange({ "users", "shift_no", "status_view", "selectedSchedule" })
	public void modifyNote() {

		if (CollectionUtils.isEmpty(this.list_schedule_selected)) {
			return;
		}

		for (final InitialScheduleSingleDetail itm : this.list_schedule_selected) {

			String mynote = this.note;

			// massive?
			if (this.list_schedule_selected.size() > 1) {
				mynote = "" + mynote + "\n" + StringUtils.defaultString(itm.getSchedule().getMobile_note(), "");
			}

			this.schedule_dao.updateMobileUserNote(itm.getSchedule().getId(), mynote);
		}

		this.refreshDataAndCurrentShift();

	}

	@Command
	@NotifyChange({ "list_ship", "shift_no", "status_view", "selectedDetailShip" })
	public void modifyShipNote() {

		if (CollectionUtils.isEmpty(this.list_selected_ship)) {
			return;
		}

		for (final DetailScheduleShip itm : this.list_selected_ship) {

			String mynote = this.note_ship;

			// massive?
			if (this.list_selected_ship.size() > 1) {

				final String current_note = this.schedule_ship_dao.getDetailScheduleShipNote(itm.getId());

				mynote = "" + mynote + "\n" + StringUtils.defaultString(current_note, "");
			}

			this.schedule_ship_dao.updateDetailScheduleShipNote(itm.getId(), mynote);

		}

		this.refreshShipDataAndCurrentShift();

	}

	/**
	 * @param dt
	 * @return
	 */
	private Date parseDateString(final String dt) {

		try {
			if (dt == null) {
				return null;
			}

			final SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy HH:mm");
			return format.parse(dt);

		} catch (final ParseException ex1) {
			try {
				final SimpleDateFormat format_h = new SimpleDateFormat("HH:mm");
				return format_h.parse(dt);
			} catch (final ParseException e) {
				return null;
			}
		}

	}

	/**
	 * Parse duration time
	 *
	 * @param tm
	 * @return
	 */
	private Date parseUserWorkTime(final Date date, final String tm) {

		if (tm == null) {
			return null;
		}

		final SimpleDateFormat format_time = new SimpleDateFormat("HH:mm");
		final SimpleDateFormat format_date = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		Date dt = null;

		try {

			final Calendar current_calendar = Calendar.getInstance();
			current_calendar.setTime(date);

			dt = format_time.parse(tm);
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(dt);

			calendar.set(Calendar.YEAR, current_calendar.get(Calendar.YEAR));
			calendar.set(Calendar.MONTH, current_calendar.get(Calendar.MONTH));
			calendar.set(Calendar.DAY_OF_MONTH, current_calendar.get(Calendar.DAY_OF_MONTH));

			return calendar.getTime();

		} catch (final ParseException e) {
			try {
				dt = format_date.parse(tm);
			} catch (final ParseException e1) {
				return null;
			}
		}

		return dt;

	}

	/**
	 * PROCESS INITIAL USER DATA FOR VIEW USERS
	 *
	 * @param shift_no
	 * @param list
	 */
	private void processInitiaUserData(final Integer shift_no, final List<InitialSchedule> list) {

		if (this.users != null) {
			this.users.clear();
		} else {
			this.users = new ArrayList<>();
		}

		// POST PROCESSING
		for (final InitialSchedule insch : list) {
			if (insch.getDetail_schedule() == null) {
				continue;
			}

			for (final MobileUserDetail detail : insch.getDetail_schedule()) {

				if (detail.getShift() == null) {
					continue;
				}

				// filter on shift
				if (shift_no != null) {
					if (!shift_no.equals(detail.getShift())) {
						continue;
					}
				}

				final UserTask user_task = this.task_dao.loadTask(detail.getTask());

				final InitialScheduleSingleDetail itm = new InitialScheduleSingleDetail();
				itm.setDetail_schedule(detail);
				itm.setPerson(insch.getPerson());
				itm.setSchedule(insch.getSchedule());
				itm.setUser_task(user_task);

				// set badge info
				final List<Badge> badgeList = this.schedule_dao.loadBadgeByScheduleId(insch.getSchedule().getId());
				final String infob = Utility.getLabelListBadge(badgeList);
				itm.setBadgeInfo(infob);

				this.users.add(itm);

			}

		}
	}

	@Command
	@NotifyChange({ "users", "list_ship", "list_schedule_selected", "list_selected_ship", "selectedDetailShip", "user_programmed" })
	public void refresh(@BindingParam("shift_no") final Integer shift_no) {

		Date date_for_selection = this.date_selection;
		if (this.date_selection == null) {
			date_for_selection = Calendar.getInstance().getTime();
		}

		if (this.status_view == 1) {

			final List<InitialSchedule> list = this.selectInitialSchedule(date_for_selection);

			this.processInitiaUserData(shift_no, list);

			// set null selected
			this.list_schedule_selected = null;

		}

		if ((this.status_view == 4) || (this.status_view == 8)) {

			this.list_ship = this.selectInitialShipSchedule(date_for_selection, shift_no);

			this.list_selected_ship = null;

		}

	}

	@Command
	@NotifyChange({ "users", "shift_no", "status_view", "list_schedule_selected" })
	public void refreshDataAndCurrentShift() {

		// set view to status 1
		this.status_view = 1;

		this.calculateShiftAndRefresh();

	}

	@Command
	@NotifyChange({ "list_ship", "shift_no", "status_view", "list_selected_ship", "selectedDetailShip" })
	public void refreshShipDataAndCurrentShift() {

		// return to list of ship
		this.status_view = 4;

		this.calculateShiftAndRefresh();

	}

	@Command
	@NotifyChange({ "users", "shift_no", "status_view", "list_ship", "list_cranes" })
	public void removeComponent() {

		switch (this.status_view) {

		case 1: {

			// remove "USER LIST"
			if (this.list_schedule_selected == null) {
				return;
			}

			for (final InitialScheduleSingleDetail itm : this.list_schedule_selected) {

				if (BooleanUtils.isNotTrue(itm.getDetail_schedule().getRevised())) {
					continue;
				}

				final Integer id = itm.getDetail_schedule().getId();
				this.schedule_dao.removeDetailFinalSchedule(id);

			}

			this.refreshDataAndCurrentShift();

			break;
		}

		case 2: {
			// ***SPOSTAMENTO***
			this.refreshDataAndCurrentShift();

			break;
		}

		case 3: {
			// NOTE USER
			this.refreshDataAndCurrentShift();

			break;
		}

		case 4: {
			// remove "SHIP"
			this.refreshShipDataAndCurrentShift();

			break;
		}

		case 5: {
			// NOTE SHIP
			this.refreshShipDataAndCurrentShift();

			break;
		}

		case 6: {
			// REVIW FOR SHIP
			this.refreshShipDataAndCurrentShift();

			break;
		}

		case 7: {
			// ADD INFO USER FOR REVIEW *** ASSEGNA PROGRAMMATO***
			this.refreshShipDataAndCurrentShift();

			break;
		}

		case 8: {
			// LIST CRANES
			if (this.craneListSelected != null) {

				this.schedule_ship_dao.deleteDetailFinalScheduleShipById(this.craneListSelected.getId());

			}

			// remove crane
			this.showGru();

			break;
		}

		case 9: {
			// close adding crane
			this.showGru();

			break;
		}
		}

	}

	@Command
	@NotifyChange({ "ships", "status_view", "ship_operation", "ship_handswork", "ship_menwork", "ship_worked", "ship_temperature", "ship_rain",
			"ship_sky", "ship_wind", "ship_windyday", "ship_persononboard", "ship_firstdown", "ship_lastdown", "ship_persondown", "cranes_entity",
			"cranes_entity_selected", "crane_p_gru", "crane_type", "selectedDetailShip", "user_visible_adding", "n_positions", "h_date_from",
			"h_date_to", "h_menwork", "ship_h" })
	public void review() {

		// review "TURNI"
		if (this.status_view == 1) {

			if (CollectionUtils.isEmpty(this.list_schedule_selected)) {
				return;
			}

			this.ships = this.listShip(this.date_selection);

			// set view
			this.status_view = 7;

			return;

		}

		// review for ship - active review editor for ship
		if (this.status_view == 4) {

			this.selectedDetailShip = this.getTheFirstSelectedShip();

			if (this.selectedDetailShip == null) {
				return;
			}

			if (this.list_selected_ship.size() > 1) {

				// DEFINE INFO FOR MULTIPLE USER
				this.user_visible_adding = Boolean.FALSE;

				this.n_positions = "" + this.list_selected_ship.size();

				this.ship_handswork = null;
				this.ship_menwork = null;
				this.ship_worked = Boolean.TRUE;

				this.ship_temperature = null;
				this.ship_rain = null;
				this.ship_sky = null;
				this.ship_wind = null;
				this.ship_windyday = null;

				this.ship_persononboard = null;
				this.ship_firstdown = null;
				this.ship_lastdown = null;
				this.ship_persondown = null;

			} else {

				// DEFINE INFO FOR SINGLE USER
				this.user_visible_adding = Boolean.TRUE;

				this.ship_handswork = this.selectedDetailShip.getHandswork();
				this.ship_menwork = this.selectedDetailShip.getMenwork();
				this.ship_worked = this.selectedDetailShip.getWorked();

				this.ship_temperature = this.selectedDetailShip.getTemperature();
				this.ship_rain = this.selectedDetailShip.getRain();
				this.ship_sky = this.selectedDetailShip.getSky();
				this.ship_wind = this.selectedDetailShip.getWind();
				this.ship_windyday = this.selectedDetailShip.getWindyday();

				this.ship_persononboard = this.formatShipDateTime(this.selectedDetailShip.getPerson_onboard());
				this.ship_firstdown = this.formatShipDateTime(this.selectedDetailShip.getFirst_down());
				this.ship_lastdown = this.formatShipDateTime(this.selectedDetailShip.getLast_down());
				this.ship_persondown = this.formatShipDateTime(this.selectedDetailShip.getPerson_down());

			}

			// set view
			this.status_view = 6;

			return;

		}

		// add crane
		if (this.status_view == 8) {

			final ConfigurationDAO conf = (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);
			this.cranes_entity = conf.getCrane(null, null, null, null);

			this.cranes_entity_selected = null;
			this.crane_p_gru = null;
			this.crane_type = null;

			final Ship ship = this.shipdao.loadShip(this.selectedDetailShip.getId_ship());
			if ((ship != null) && (BooleanUtils.isTrue(ship.getActivityh()))) {
				this.h_date_from = null;
				this.h_date_to = null;
				this.h_menwork = null;
				this.ship_h = Boolean.TRUE;
			} else {
				this.ship_h = Boolean.FALSE;

			}

			// set view
			this.status_view = 9;

			return;
		}

	}

	@Command
	@NotifyChange({ "users", "shift_no", "status_view", "list_schedule_selected" })
	public void reviewUserCommand() {

		if (CollectionUtils.isEmpty(this.list_schedule_selected)) {
			return;
		}

		for (final InitialScheduleSingleDetail itm : this.list_schedule_selected) {

			// if already revised, no act
			final MobileUserDetail user_detail = itm.getDetail_schedule();

			// paranoia-check
			if (user_detail == null) {
				continue;
			}

			if (BooleanUtils.isTrue(user_detail.getRevised())) {
				continue;
			}

			final UserTask task = this.task_dao.loadTask(user_detail.getTask());

			Ship ship_itm = this.ship_selected;
			if (this.ship_selected == Ship.EMPTY) {
				ship_itm = null;
			}

			String myposition = this.user_position;
			if (StringUtils.equals(this.user_position, BoardTag.EMPTY)) {
				myposition = null;
			}

			// for program
			final Boolean cont_shift = itm.getDetail_schedule().getContinueshift();

			this.createDetailFinalSchedule(user_detail.getTime_from(), user_detail.getTime_to(), itm, task, this.user_crane_selected, ship_itm,
					myposition, cont_shift);

		}

		this.refreshDataAndCurrentShift();
	}

	public List<InitialSchedule> selectInitialSchedule(final Date date_request) {

		final List<InitialSchedule> ret = new ArrayList<>();

		final Date date_schedule = DateUtils.truncate(date_request, Calendar.DATE);

		// get special task
		final List<UserTask> list_special = this.configurationDao.listSpecialTaskMobile();

		final List<Person> list = this.person_dao.listAllPersonsForMobile(date_schedule);

		// reset programmed person
		for (int i = 0; i < 4; i++) {
			this.user_programmed[i] = 0;
		}

		for (final Person person : list) {

			final InitialSchedule item = new InitialSchedule();

			final Schedule schedule = this.schedule_dao.loadSchedule(date_schedule, person.getId());
			if (schedule == null) {
				continue;
			}

			final List<MobileUserDetail> merging_details = new ArrayList<>();

			// ADD SHIFT 1
			for (int i = 1; i <= 4; i++) {

				List<MobileUserDetail> list_details = null;

				final List<MobileUserDetail> final_details = this.schedule_dao.loadMobileUserFinalDetail(schedule.getId(), i);
				final List<MobileUserDetail> initial_details = this.schedule_dao.loadMobileUserInitialDetail(schedule.getId(), i);

				// sum for person programmed
				if (CollectionUtils.isNotEmpty(initial_details)) {
					this.user_programmed[i - 1]++;
				}

				if (CollectionUtils.isNotEmpty(final_details)) {

					final MobileUserDetail last = this.extractThelast(final_details);
					list_details = new ArrayList<>();
					list_details.add(last);

					if (CollectionUtils.isNotEmpty(initial_details)) {
						last.setProgrammed(initial_details);
					}

				} else {
					list_details = initial_details;
				}

				if (CollectionUtils.isNotEmpty(list_details)) {
					merging_details.addAll(list_details);
				}

			}

			// CHECK IF ANY ITEM TO ADD
			if (merging_details.size() == 0) {
				continue;
			}

			// set info about task
			final List<UserTask> list_tasks = this.task_dao.loadTasksByUserForMobile(person.getId());

			// add special task - add only if not exists
			for (final UserTask itm : list_special) {

				if (!list_tasks.contains(itm)) {
					list_tasks.add(itm);
				}

			}

			person.setUserTaskForMobile(list_tasks);

			// set current object
			item.setPerson(person);
			item.setDetail_schedule(merging_details);
			item.setSchedule(schedule);

			ret.add(item);

		}

		return ret;
	}

	/**
	 * return detail ship by date
	 *
	 * @param date_request
	 * @return
	 */

	public List<DetailScheduleShip> selectInitialShipSchedule(final Date date_request, final Integer shift) {

		final Date date_request_truncate = DateUtils.truncate(date_request, Calendar.DATE);
		final List<DetailScheduleShip> list = this.schedule_ship_dao.searchShipDetail(date_request_truncate, null, shift, Boolean.TRUE);
		return list;
	}

	/**
	 * return detail ship by date
	 *
	 * @param date_request
	 * @return
	 */

	public DetailScheduleShip selectInitialShipSchedule(final Date date_request, final Integer shift, final Integer id_ship) {

		if (id_ship == null) {
			return null;
		}

		final List<DetailScheduleShip> list = this.selectInitialShipSchedule(date_request, shift);

		for (final DetailScheduleShip itm : list) {
			if (id_ship.equals(itm.getId_ship())) {
				return itm;
			}
		}

		return null;

	}

	@Command
	@NotifyChange({ "shift_no", "users", "list_ship", "list_schedule_selected", "selectedDetailShip", "date_selection", "status_view" })
	public void selectToDay() {
		final Calendar calendar = Calendar.getInstance();
		this.date_selection = calendar.getTime();

		this.calculateShiftAndRefresh();

		// back to ship
		if (this.status_view == 8) {
			this.status_view = 4;
		}

	}

	@Command
	@NotifyChange({ "shift_no", "users", "list_ship", "list_schedule_selected", "list_selected_ship", "selectedDetailShip", "date_selection",
			"user_programmed", "status_view" })
	public void selectTomorrow() {

		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		this.date_selection = calendar.getTime();

		// tomorrow set shift number to 1.
		this.shift_no = 1;
		// refresh with shift_no
		this.refresh(this.shift_no);

		// back to ship
		if (this.status_view == 8) {
			this.status_view = 4;
		}

	}

	public void setCrane_p_gru(final Integer crane_p_gru) {
		this.crane_p_gru = crane_p_gru;
	}

	public void setCrane_type(final Boolean crane_type) {
		this.crane_type = crane_type;
	}

	public void setCraneListSelected(final DetailFinalScheduleShip craneListSelected) {
		this.craneListSelected = craneListSelected;
	}

	public void setCranes_entity(final List<Crane> cranes_entity) {
		this.cranes_entity = cranes_entity;
	}

	public void setCranes_entity_selected(final Crane cranes_entity_selected) {
		this.cranes_entity_selected = cranes_entity_selected;
	}

	/**
	 * @param filed 0 for initial, 1 for final
	 */
	@Command
	@NotifyChange({ "starting_task", "end_task" })
	public void setCurrentTaskTime(@BindingParam("field") final int field) {

		final String current = this.getCurrentInfoTime();

		if (field == 0) {
			this.starting_task = current;
		} else if (field == 1) {
			this.end_task = current;
		}

	}

	public void setDate_selection(final Date date_selection) {
		this.date_selection = date_selection;
	}

	public void setEnd_task(final String end_task) {
		this.end_task = end_task;
	}

	public void setH_date_from(final String h_date_from) {
		this.h_date_from = h_date_from;
	}

	public void setH_date_to(final String h_date_to) {
		this.h_date_to = h_date_to;
	}

	public void setH_menwork(final Integer h_menwork) {
		this.h_menwork = h_menwork;
	}

	public void setLabel_command_ok(final String label_command_ok) {
		this.label_command_ok = label_command_ok;
	}

	public void setList_cranes(final List<DetailFinalScheduleShip> list_cranes) {
		this.list_cranes = list_cranes;
	}

	public void setList_schedule_selected(final List<InitialScheduleSingleDetail> list_schedule_selected) {
		this.list_schedule_selected = list_schedule_selected;
	}

	public void setList_selected_ship(final List<DetailScheduleShip> list_selected_ship) {
		this.list_selected_ship = list_selected_ship;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setNote_ship(final String note_ship) {
		this.note_ship = note_ship;
	}

	public void setSelectedDetailShip(final DetailScheduleShip selectedDetailShip) {
		this.selectedDetailShip = selectedDetailShip;
	}

	public void setSelectedSchedule(final InitialScheduleSingleDetail selectedSchedule) {
		this.selectedSchedule = selectedSchedule;
	}

	public void setShip_firstdown(final String ship_firstdown) {
		this.ship_firstdown = ship_firstdown;
	}

	public void setShip_h(final Boolean ship_h) {
		this.ship_h = ship_h;
	}

	public void setShip_handswork(final Integer ship_handswork) {
		this.ship_handswork = ship_handswork;
	}

	public void setShip_lastdown(final String ship_lastdown) {
		this.ship_lastdown = ship_lastdown;
	}

	public void setShip_menwork(final Integer ship_menwork) {
		this.ship_menwork = ship_menwork;
	}

	public void setShip_persondown(final String ship_persondown) {
		this.ship_persondown = ship_persondown;
	}

	public void setShip_persononboard(final String ship_persononboard) {
		this.ship_persononboard = ship_persononboard;
	}

	public void setShip_rain(final String ship_rain) {
		this.ship_rain = ship_rain;
	}

	public void setShip_selected(final Ship ship_selected) {
		this.ship_selected = ship_selected;
	}

	public void setShip_sky(final String ship_sky) {
		this.ship_sky = ship_sky;
	}

	public void setShip_temperature(final String ship_temperature) {
		this.ship_temperature = ship_temperature;
	}

	public void setShip_wind(final String ship_wind) {
		this.ship_wind = ship_wind;
	}

	public void setShip_windyday(final Boolean ship_windyday) {
		this.ship_windyday = ship_windyday;
	}

	public void setShip_worked(final Boolean ship_worked) {
		this.ship_worked = ship_worked;
	}

	/**
	 * @param filed 0 for initial, 1 for final
	 */
	@Command
	@NotifyChange({ "h_date_from", "h_date_to" })
	public void setShipHTimeDefault(@BindingParam("field") final int field) {

		final Date[] period = Utility.getPeriodForShipWorkingProcess(this.selectedDetailShip);

		final Date dt_selected = period[field];

		final String ret = this.formatShipDateTime(dt_selected);

		if (field == 0) {
			this.h_date_from = ret;
		} else if (field == 1) {
			this.h_date_to = ret;
		}

	}

	public void setStarting_task(final String starting_task) {
		this.starting_task = starting_task;
	}

	public void setUser_continue(final Boolean user_continue) {
		this.user_continue = user_continue;
	}

	public void setUser_crane_selected(final String user_crane_selected) {
		this.user_crane_selected = user_crane_selected;
	}

	public void setUser_position(final String user_position) {
		this.user_position = user_position;
	}

	public void setUser_task_selected(final UserTask user_task_selected) {
		this.user_task_selected = user_task_selected;
	}

	public void setUserConverter(final MyUserConverter userConverter) {
		this.userConverter = userConverter;
	}

	@Command
	@NotifyChange({ "list_ship", "shift_no", "status_view", "selectedDetailShip" })
	public void shipReviewCommand() {

		if (CollectionUtils.isEmpty(this.list_selected_ship)) {
			return;
		}

		for (final DetailScheduleShip itm : this.list_selected_ship) {

			final Integer id = itm.getId();

			final Date dt_person_onboard = this.parseDateString(this.ship_persononboard);
			final Date dt_ship_firstdown = this.parseDateString(this.ship_firstdown);
			final Date dt_ship_lastdown = this.parseDateString(this.ship_lastdown);
			final Date dt_ship_persondown = this.parseDateString(this.ship_persondown);

			this.schedule_ship_dao.updateDetailScheduleShipForMobile(id, this.ship_handswork, this.ship_menwork, this.ship_worked,
					this.ship_temperature, this.ship_sky, this.ship_rain, this.ship_wind, this.ship_windyday, dt_person_onboard, dt_ship_firstdown,
					dt_ship_lastdown, dt_ship_persondown);

		}

		this.refreshShipDataAndCurrentShift();

	}

	/**
	 * Set ship time default
	 */
	@Command
	@NotifyChange({ "ship_persononboard", "ship_firstdown", "ship_lastdown", "ship_persondown" })
	public void shipTimeDefault() {

		if (this.selectedDetailShip == null) {
			return;
		}

		final Date[] period = Utility.getPeriodForShipWorkingProcess(this.selectedDetailShip);
		if (period == null) {
			return;
		}

		final Date begin = period[0];
		final Date end = period[1];

		// set values
		this.ship_persononboard = this.formatShipDateTime(begin);
		this.ship_firstdown = this.formatShipDateTime(begin);
		this.ship_lastdown = this.formatShipDateTime(end);
		this.ship_persondown = this.formatShipDateTime(end);

	}

	@Command
	@NotifyChange({ "users", "list_schedule_selected" })
	public void showFinalDetails() {

		if (!(this.status_view == 1)) {
			return;
		}

		if (CollectionUtils.isEmpty(this.list_schedule_selected)) {
			return;
		}

		final InitialScheduleSingleDetail itm = this.list_schedule_selected.get(0);

		// get shift no
		final Integer shift_info = itm.getDetail_schedule().getShift();

		final List<MobileUserDetail> list_details = this.schedule_dao.loadMobileUserFinalDetail(itm.getSchedule().getId(), shift_info);
		final List<MobileUserDetail> initial_details = this.schedule_dao.loadMobileUserInitialDetail(itm.getSchedule().getId(), shift_info);

		if (CollectionUtils.isNotEmpty(initial_details)) {
			for (final MobileUserDetail itm_d : list_details) {
				itm_d.setProgrammed(initial_details);
			}
		}

		final InitialSchedule item = new InitialSchedule();
		// set current object
		item.setPerson(itm.getPerson());
		item.setDetail_schedule(list_details);
		item.setSchedule(itm.getSchedule());

		final List<InitialSchedule> list = new ArrayList<>();
		list.add(item);
		this.processInitiaUserData(shift_info, list);

		// set null selected
		this.list_schedule_selected = null;

	}

	@Command
	@NotifyChange({ "list_cranes", "status_view" })
	public void showGru() {

		this.selectedDetailShip = this.getTheFirstSelectedShip();

		if (this.selectedDetailShip == null) {
			return;
		}

		final Integer id = this.selectedDetailShip.getId();

		this.list_cranes = this.schedule_ship_dao.loadDetailFinalScheduleShipByIdDetailScheduleShip(id);

		// go to gru view
		this.status_view = 8;

	}

	@Command
	@NotifyChange({ "users", "shift_no", "status_view" })
	public void signIn() {

		if (this.status_view == 1) {

			for (final InitialScheduleSingleDetail itm : this.list_schedule_selected) {

				final Schedule schedule = itm.getSchedule();
				final Date current_time = Calendar.getInstance().getTime();

				final Badge badge = new Badge();
				badge.setEventTime(current_time);
				badge.setEventType(Boolean.TRUE);
				badge.setIdschedule(schedule.getId());

				this.schedule_dao.createBadge(badge);

				// check if delay
				final Date time_from = itm.getDetail_schedule().getTime_from();
				if (current_time.after(time_from)) {

					// calculate minutes
					final int mins = Utility.getMinutesBetweenDate(time_from, current_time);
					if (mins > 1) {

						final UserTask delay = this.configurationDao.getDelayOperationTask();

						this.createDetailFinalSchedule(time_from, current_time, itm, delay, null, null, null, Boolean.FALSE);

					}

				}

			}

			this.refreshDataAndCurrentShift();
		}

	}

	@Command
	@NotifyChange({ "users", "shift_no", "status_view" })
	public void signOut() {

		if (this.status_view == 1) {

			for (final InitialScheduleSingleDetail itm : this.list_schedule_selected) {

				final Badge badge = new Badge();
				badge.setEventTime(Calendar.getInstance().getTime());
				badge.setEventType(Boolean.FALSE);
				badge.setIdschedule(itm.getSchedule().getId());

				this.schedule_dao.createBadge(badge);

			}

			this.refreshDataAndCurrentShift();
		}
	}

	@Command
	@NotifyChange({ "users", "list_ship", "shift_no", "status_view", "list_schedule_selected", "selectedDetailShip" })
	public void switchShipShift() {

		if ((this.status_view == 1) || (this.status_view == 7) || (this.status_view == 8) || (this.status_view == 9)) {

			this.refreshShipDataAndCurrentShift();

		} else {

			this.refreshDataAndCurrentShift();
		}

	}

}
