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
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.uario.seaworkengine.mobile.model.Badge;
import org.uario.seaworkengine.mobile.model.InitialSchedule;
import org.uario.seaworkengine.mobile.model.InitialScheduleSingleDetail;
import org.uario.seaworkengine.mobile.model.MobileUserDetail;
import org.uario.seaworkengine.model.Crane;
import org.uario.seaworkengine.model.DetailFinalSchedule;
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
	private class MyOperationConverter implements Converter {

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

			if (StringUtils.isEmpty(op_info)) {
				return "";
			}

			if (op_info.equalsIgnoreCase("COMPLETA")) {
				return "CO";
			}

			if (op_info.equalsIgnoreCase("TWISTER")) {
				return "TW";
			}

			return "";

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
				main = main + "(" + name + " - CR" + crane + ")";
			}

			// adding info program
			main = main + "(";
			final String info = MobileComposer.this.getTaskStringView(op);
			main = main + info + ")";
			return main;

		}
	}

	private ConfigurationDAO					configurationDao;

	private Crane								crane_selected;

	private Date								date_selection;

	/**
	 * Instace of data converter
	 */
	private final MyDateFormatConverter			dateConverter			= new MyDateFormatConverter();

	private DetailScheduleShip					detail_schedule_ship_selected;

	private String								end_task;

	private List<Crane>							list_crane;

	private List<InitialScheduleSingleDetail>	list_schedule_selected	= null;

	private List<DetailScheduleShip>			list_ship;

	private List<UserTask>						list_task;

	private String								n_positions;

	private String								note;

	private String								note_ship;

	private final MyOperationConverter			operationConverter		= new MyOperationConverter();

	private PersonDAO							person_dao;

	private ISchedule							schedule_dao;

	private IScheduleShip						schedule_ship_dao;

	private InitialScheduleSingleDetail			selectedSchedule;

	private Integer								shift_no;

	private Integer								ship_handswork;

	private String								ship_operation;

	private Ship								ship_selected;

	private IShip								shipdao;

	private List<Ship>							ships;

	private String								starting_task;

	private Integer								status_view				= 1;

	private TasksDAO							task_dao;

	/**
	 * Task converter
	 */
	private final MyMobileTaskConverter			taskConverter			= new MyMobileTaskConverter();

	private String								user_position;

	private final Integer[]						user_programmed			= new Integer[] { 0, 0, 0, 0 };

	private UserTask							user_task_selected;

	private Boolean								user_visible_adding		= Boolean.FALSE;

	private MyUserConverter						userConverter			= new MyUserConverter();

	private List<InitialScheduleSingleDetail>	users;

	@Command
	@NotifyChange({ "status_view", "list_task", "ships", "ship_selected", "crane_selected", "selectedSchedule", "starting_task", "end_task",
			"user_task_selected", "user_visible_adding", "n_positions" })
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
		this.crane_selected = null;

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

		if (this.crane_selected == null) {
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
			if (this.user_position == "NESSUNA") {
				myposition = null;
			}

			// Create info
			this.createDetailFinalSchedule(dt_starting, dt_end, itm, this.user_task_selected, this.crane_selected, ship_itm, myposition);

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
			final UserTask task, final Crane crane, final Ship ship, final String position) {

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
		detail_schedule.setContinueshift(Boolean.FALSE);

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
		if (crane != null) {
			detail_schedule.setCrane(crane.getNumber().toString());
		}

		if (position != null) {
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
	@NotifyChange({ "status_view", "note", "note_ship", "selectedSchedule", "user_visible_adding", "n_positions" })
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

				this.note = this.getScheduleNote(this.selectedSchedule.getSchedule().getId());

			}

			this.status_view = 3;

		}

		// note for "SHIP"
		if (this.status_view == 4) {

			if (this.detail_schedule_ship_selected == null) {
				return;
			}

			this.note_ship = this.schedule_ship_dao.getDetailScheduleShipNote(this.detail_schedule_ship_selected.getId());

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

	public Crane getCrane_selected() {
		return this.crane_selected;
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

	public DetailScheduleShip getDetail_schedule_ship_selected() {
		return this.detail_schedule_ship_selected;
	}

	public String getEnd_task() {
		return this.end_task;
	}

	public List<Crane> getList_crane() {
		return this.list_crane;
	}

	public List<InitialScheduleSingleDetail> getList_schedule_selected() {
		return this.list_schedule_selected;
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

	public MyOperationConverter getOperationConverter() {
		return this.operationConverter;
	}

	private String getScheduleNote(final Integer id_schedule) {
		final Schedule schedule = this.schedule_dao.loadScheduleById(id_schedule);
		final String note = schedule.getNote();
		return note;
	}

	public InitialScheduleSingleDetail getSelectedSchedule() {
		return this.selectedSchedule;
	}

	public Integer getShift_no() {
		return this.shift_no;
	}

	public Integer getShip_handswork() {
		return this.ship_handswork;
	}

	public String getShip_operation() {
		return this.ship_operation;
	}

	public Ship getShip_selected() {
		return this.ship_selected;
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

		final String from = data_format.format(time_from);
		final String to = data_format.format(time_to);

		return code + " (" + from + " - " + to + ")";
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

		this.list_crane = this.configurationDao.getCrane(null, null, null, null);

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
				mynote = "" + mynote + "\n" + org.apache.commons.lang3.StringUtils.defaultString(itm.getSchedule().getNote(), "");
			}

			this.schedule_dao.updateScheduleNote(itm.getSchedule().getId(), mynote);
		}

		this.refreshDataAndCurrentShift();

	}

	@Command
	@NotifyChange({ "list_ship", "shift_no", "status_view", "detail_schedule_ship_selected" })
	public void modifyShipNote() {

		if (this.detail_schedule_ship_selected == null) {
			return;
		}

		this.schedule_ship_dao.updateDetailScheduleShipNote(this.detail_schedule_ship_selected.getId(), this.note_ship);

		this.refreshShipDataAndCurrentShift();

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
		final SimpleDateFormat format_date = new SimpleDateFormat("dd/MM/YYYY HH:mm");

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
	@NotifyChange({ "users", "list_ship", "list_schedule_selected", "detail_schedule_ship_selected", "user_programmed" })
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

		if (this.status_view == 4) {

			this.list_ship = this.selectInitialShipSchedule(date_for_selection, shift_no);

			// deselect
			this.detail_schedule_ship_selected = null;

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
	@NotifyChange({ "list_ship", "shift_no", "status_view", "detail_schedule_ship_selected" })
	public void refreshShipDataAndCurrentShift() {

		// return to list of ship
		this.status_view = 4;

		this.calculateShiftAndRefresh();

	}

	@Command
	@NotifyChange({ "users", "shift_no", "status_view", "list_ship" })
	public void removeComponent() {

		// remove "TURNI"
		if (this.status_view == 1) {

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

		}

		// remove "SHIP"
		if (this.status_view == 4) {

			this.refreshShipDataAndCurrentShift();

		}

		// CLOSE FOR "TURNI"
		if ((this.status_view == 2) || (this.status_view == 3)) {
			this.refreshDataAndCurrentShift();
		}

		// CLOSE FOR "SHIP"
		if ((this.status_view == 5) || (this.status_view == 6)) {
			this.refreshShipDataAndCurrentShift();
		}

	}

	@Command
	@NotifyChange({ "ships", "status_view", "ship_operation", "ship_handswork" })
	public void review() {

		// review "TURNI"
		if (this.status_view == 1) {

			if (CollectionUtils.isEmpty(this.list_schedule_selected)) {
				return;
			}

			this.ships = this.listShip(this.date_selection);

			// set view
			this.status_view = 7;

		}

		// review for ship - active review editor for ship
		if (this.status_view == 4) {

			if (this.detail_schedule_ship_selected == null) {
				return;
			}

			this.ship_operation = this.detail_schedule_ship_selected.getOperation();
			this.ship_handswork = this.detail_schedule_ship_selected.getHandswork();

			// set view
			this.status_view = 6;

		}

	}

	@Command
	@NotifyChange({ "users", "shift_no", "status_view", "ship_operation", "ship_handswork" })
	public void reviewUserCommand() {

		// get info about crane and ship
		if (this.crane_selected == null) {
			return;
		}

		if (CollectionUtils.isEmpty(this.list_schedule_selected)) {
			return;
		}

		for (final InitialScheduleSingleDetail itm : this.list_schedule_selected) {

			// if already revised, no act
			final MobileUserDetail user_detail = itm.getDetail_schedule();

			if (BooleanUtils.isTrue(user_detail.getRevised())) {
				continue;
			}

			final UserTask task = this.task_dao.loadTask(user_detail.getTask());

			Ship ship_itm = this.ship_selected;
			if (this.ship_selected == Ship.EMPTY) {
				ship_itm = null;
			}

			String myposition = this.user_position;
			if (this.user_position == "NESSUNA") {
				myposition = null;
			}

			this.createDetailFinalSchedule(user_detail.getTime_from(), user_detail.getTime_to(), itm, task, this.crane_selected, ship_itm,
					myposition);

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
		final List<DetailScheduleShip> list = this.schedule_ship_dao.searchDetailScheduleShipByDateshit(date_request_truncate, null, shift,
				null, null, null, null, null);
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
	@NotifyChange({ "shift_no", "users", "list_ship", "list_schedule_selected", "detail_schedule_ship_selected", "date_selection" })
	public void selectToDay() {
		final Calendar calendar = Calendar.getInstance();
		this.date_selection = calendar.getTime();

		this.calculateShiftAndRefresh();

	}

	@Command
	@NotifyChange({ "shift_no", "users", "list_ship", "list_schedule_selected", "detail_schedule_ship_selected", "date_selection",
			"user_programmed" })
	public void selectTomorrow() {

		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		this.date_selection = calendar.getTime();

		// tomorrow set shift number to 1.
		this.shift_no = 1;
		// refresh with shift_no
		this.refresh(this.shift_no);

	}

	public void setCrane_selected(final Crane crane_selected) {
		this.crane_selected = crane_selected;
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

	public void setDetail_schedule_ship_selected(final DetailScheduleShip detail_schedule_ship_selected) {
		this.detail_schedule_ship_selected = detail_schedule_ship_selected;
	}

	public void setEnd_task(final String end_task) {
		this.end_task = end_task;
	}

	public void setList_crane(final List<Crane> list_crane) {
		this.list_crane = list_crane;
	}

	public void setList_schedule_selected(final List<InitialScheduleSingleDetail> list_schedule_selected) {
		this.list_schedule_selected = list_schedule_selected;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setNote_ship(final String note_ship) {
		this.note_ship = note_ship;
	}

	public void setSelectedSchedule(final InitialScheduleSingleDetail selectedSchedule) {
		this.selectedSchedule = selectedSchedule;
	}

	public void setShip_handswork(final Integer ship_handswork) {
		this.ship_handswork = ship_handswork;
	}

	public void setShip_operation(final String ship_operation) {
		this.ship_operation = ship_operation;
	}

	public void setShip_selected(final Ship ship_selected) {
		this.ship_selected = ship_selected;
	}

	public void setStarting_task(final String starting_task) {
		this.starting_task = starting_task;
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
	@NotifyChange({ "list_ship", "shift_no", "status_view", "detail_schedule_ship_selected" })
	public void shipReviewCommand() {

		if (this.detail_schedule_ship_selected == null) {
			return;
		}

		final Integer id = this.detail_schedule_ship_selected.getId();
		this.schedule_ship_dao.updateDetailScheduleShipForMobile(id, this.getShip_operation(), this.getShip_handswork());

		this.refreshShipDataAndCurrentShift();

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

						this.createDetailFinalSchedule(time_from, current_time, itm, delay, null, null, null);

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
	@NotifyChange({ "users", "list_ship", "shift_no", "status_view", "list_schedule_selected", "detail_schedule_ship_selected" })
	public void switchShipShift() {

		if (this.status_view == 1) {

			this.refreshShipDataAndCurrentShift();

		} else {

			this.refreshDataAndCurrentShift();
		}

	}

}
