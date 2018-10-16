package org.uario.seaworkengine.web.zkevent;

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
import org.springframework.util.StringUtils;
import org.uario.seaworkengine.model.Crane;
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.IShip;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.Utility;
import org.uario.seaworkengine.web.services.IWebServiceController;
import org.uario.seaworkengine.web.services.handler.Badge;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;
import org.uario.seaworkengine.web.services.handler.InitialScheduleSingleDetail;
import org.uario.seaworkengine.web.services.handler.MobileUserDetail;
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

		SimpleDateFormat data_format = new SimpleDateFormat("HH:mm");

		@Override
		public Object coerceToBean(final Object val, final Component comp, final BindContext ctx) {

			return null;

		}

		@Override
		public Object coerceToUi(final Object val, final Component comp, final BindContext ctx) {

			if (!(val instanceof InitialScheduleSingleDetail)) {
				return null;
			}

			final InitialScheduleSingleDetail itm = (InitialScheduleSingleDetail) val;

			final String code = itm.getUser_task().getCode();

			final Date time_from = itm.getDetail_schedule().getTime_from();
			final Date time_to = itm.getDetail_schedule().getTime_to();

			final String from = this.data_format.format(time_from);
			final String to = this.data_format.format(time_to);

			return code + " (" + from + " - " + to + ")";

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

			String main = person.toString() + " ";

			final String board = op.getDetail_schedule().getBoard();
			if (!StringUtils.isEmpty(board)) {
				main = main + "(" + board.toCharArray()[0] + ") ";
			}

			final Integer id_ship = op.getDetail_schedule().getId_ship();
			final String crane = op.getDetail_schedule().getCrane();
			if ((id_ship != null) && (crane != null)) {

				final Ship ship = MobileComposer.this.shipdao.loadShip(id_ship);
				final String name = ship.getName();
				main = main + "(" + name + " - CR" + crane + ")";
			}

			return main;

		}
	}

	private ConfigurationDAO configurationDao;

	private Crane crane_selected;

	private Date date_selection;

	/**
	 * Instace of data converter
	 */
	private final MyDateFormatConverter dateConverter = new MyDateFormatConverter();

	private DetailScheduleShip detail_schedule_ship_selected;

	private String end_task;

	private List<Crane> list_crane;

	private List<InitialScheduleSingleDetail> list_schedule_selected = null;

	private List<DetailScheduleShip> list_ship;

	private List<UserTask> list_task;

	private String note;

	private String note_ship;

	private final MyOperationConverter operationConverter = new MyOperationConverter();

	private ISchedule schedule_dao;

	private Boolean select_position_on = Boolean.TRUE;

	private InitialScheduleSingleDetail selectedSchedule;

	private IWebServiceController service;

	private Integer shift_no;

	private Integer ship_handswork;

	private String ship_operation;

	private Ship ship_selected;

	private IShip shipdao;

	private List<Ship> ships;

	private String starting_task;

	/**
	 * STATUS SELECTION. EDIT ZUL FOR INFO
	 */
	private Integer status_schedule_selection = 3;

	private Integer status_view = 1;

	private TasksDAO task_dao;

	/**
	 * Task converter
	 */
	private final MyMobileTaskConverter taskConverter = new MyMobileTaskConverter();

	private String user_position;

	private UserTask user_task_selected;

	private MyUserConverter userConverter = new MyUserConverter();

	private List<InitialScheduleSingleDetail> users;

	@Command
	@NotifyChange({ "status_view", "list_task", "ships", "selectedSchedule" })
	public void addComponents() {

		if (this.status_view != 1) {
			return;

		}

		if (CollectionUtils.isEmpty(this.list_schedule_selected)) {
			return;
		}

		this.selectedSchedule = this.list_schedule_selected.get(0);

		this.list_task = this.task_dao.loadTasksByUserForMobile(this.selectedSchedule.getPerson().getId());

		// get default task
		final UserTask def_task = this.task_dao.getDefault(this.selectedSchedule.getPerson().getId());

		if (def_task != null) {

			Collections.sort(this.list_task, new Comparator<UserTask>() {

				@Override
				public int compare(final UserTask o1, final UserTask o2) {
					if (o1.equals(def_task)) {
						return -1;
					}
					if (o2.equals(def_task)) {
						return 1;
					} else {
						return o1.compareTo(o2);
					}

				}

			});
		}

		this.status_view = 2;

	}

	/**
	 * Add single detail
	 */
	@Command
	@NotifyChange({ "users", "shift_no", "status_view", "selectedSchedule" })
	public void addDetailFinalSchedule() {

		if (CollectionUtils.isEmpty(this.list_schedule_selected)) {
			return;
		}

		this.selectedSchedule = this.list_schedule_selected.get(0);

		// parse time
		final String starting = this.getStarting_task();
		final String end = this.getEnd_task();
		final Date dt_starting = this.parseUserWorkTime(this.selectedSchedule.getSchedule().getDate_schedule(),
				starting);
		final Date dt_end = this.parseUserWorkTime(this.selectedSchedule.getSchedule().getDate_schedule(), end);
		if ((dt_starting == null) || (dt_end == null)) {
			return;
		}

		final DetailFinalSchedule detail_schedule = new DetailFinalSchedule();

		detail_schedule.setId_schedule(this.selectedSchedule.getSchedule().getId());
		detail_schedule.setShift(this.selectedSchedule.getDetail_schedule().getShift());
		detail_schedule.setContinueshift(Boolean.FALSE);

		// task
		if (this.user_task_selected != null) {
			detail_schedule.setTask(this.user_task_selected.getId());
		}

		// define time
		detail_schedule.setTime_from(new java.sql.Timestamp(dt_starting.getTime()));
		detail_schedule.setTime_to(new java.sql.Timestamp(dt_end.getTime()));
		final Double time = Utility.getTimeDifference(dt_starting, dt_end);

		if (this.user_task_selected.getIsabsence()) {
			detail_schedule.setTime_vacation(time);
			detail_schedule.setTime(0.0);

		} else {
			detail_schedule.setTime_vacation(0.0);
			detail_schedule.setTime(time);

		}

		this.schedule_dao.createDetailFinalSchedule(detail_schedule);

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

	@Command
	@NotifyChange({ "status_view", "note", "note_ship", "selectedSchedule" })
	public void editNote() {

		// note for "TURNI"
		if (this.status_view == 1) {

			if (CollectionUtils.isEmpty(this.list_schedule_selected)) {
				return;
			}

			this.selectedSchedule = this.list_schedule_selected.get(0);

			this.note = this.service.getScheduleNote(this.selectedSchedule.getSchedule().getId());

			this.status_view = 3;

		}

		// note for "SHIP"
		if (this.status_view == 4) {

			if (this.detail_schedule_ship_selected == null) {
				return;
			}

			this.note_ship = this.service.getDetailScheduleShipNote(this.detail_schedule_ship_selected.getId());

			this.status_view = 5;

		}

	}

	public Crane getCrane_selected() {
		return this.crane_selected;
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

	public String getNote() {
		return this.note;
	}

	public String getNote_ship() {
		return this.note_ship;
	}

	public MyOperationConverter getOperationConverter() {
		return this.operationConverter;
	}

	public Boolean getSelect_position_on() {
		return this.select_position_on;
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

	public String getUser_position() {
		return this.user_position;
	}

	public UserTask getUser_task_selected() {
		return this.user_task_selected;
	}

	public MyUserConverter getUserConverter() {
		return this.userConverter;
	}

	public List<InitialScheduleSingleDetail> getUsers() {
		return this.users;
	}

	@AfterCompose
	public void init(@ContextParam(ContextType.COMPONENT) final Component component) throws Exception {
		this.service = (IWebServiceController) SpringUtil.getBean(BeansTag.WEBCONTROLLER);

		this.task_dao = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);
		this.shipdao = (IShip) SpringUtil.getBean(BeansTag.SHIP_DAO);
		this.schedule_dao = (ISchedule) SpringUtil.getBean(BeansTag.SCHEDULE_DAO);
		this.configurationDao = (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);
		this.list_crane = this.configurationDao.getCrane(null, null, null, null);

		// set selection at today
		this.date_selection = Calendar.getInstance().getTime();

		// select position
		this.select_position_on = Boolean.TRUE;
		this.status_schedule_selection = 3;

		this.refreshDataAndCurrentShift();

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

		this.selectedSchedule = this.list_schedule_selected.get(0);

		this.service.updateScheduleNote(this.selectedSchedule.getSchedule().getId(), this.note);

		this.refreshDataAndCurrentShift();

	}

	@Command
	@NotifyChange({ "list_ship", "shift_no", "status_view", "detail_schedule_ship_selected" })
	public void modifyShipNote() {

		if (this.detail_schedule_ship_selected == null) {
			return;
		}

		this.service.updateDetailScheduleShipNote(this.detail_schedule_ship_selected.getId(), this.note_ship);

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

	@Command
	@NotifyChange({ "users", "list_ship", "list_schedule_selected", "detail_schedule_ship_selected" })
	public void refresh(@BindingParam("shift_no") final Integer shift_no) {

		Date date_for_selection = this.date_selection;
		if (this.date_selection == null) {
			date_for_selection = Calendar.getInstance().getTime();
		}

		if (this.status_view == 1) {

			if (this.users != null) {
				this.users.clear();
			} else {
				this.users = new ArrayList<>();
			}

			final List<InitialSchedule> list = this.service.selectInitialSchedule(date_for_selection,
					this.status_schedule_selection);

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
					final List<Badge> badgeList = this.service.loadListBadge(insch.getSchedule().getId());
					final String infob = Utility.getLabelListBadge(badgeList);
					itm.setBadgeInfo(infob);

					this.users.add(itm);

				}

			}

			// set null selected
			this.list_schedule_selected = null;

		}

		if (this.status_view == 4) {

			this.list_ship = this.service.selectInitialShipSchedule(date_for_selection, shift_no);

			// deselect
			this.detail_schedule_ship_selected = null;

		}

	}

	@Command
	@NotifyChange({ "users", "shift_no", "status_view", "list_schedule_selected", "select_position_on" })
	public void refreshDataAndCurrentShift() {

		// set view to status 1
		this.status_view = 1;

		// active command about schedule position
		this.select_position_on = Boolean.TRUE;

		this.calculateShiftAndRefresh();

	}

	@Command
	@NotifyChange({ "list_ship", "shift_no", "status_view", "detail_schedule_ship_selected", "select_position_on" })
	public void refreshShipDataAndCurrentShift() {

		// return to list of ship
		this.status_view = 4;

		// active command about schedule position
		this.select_position_on = Boolean.FALSE;

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

			this.ships = this.service.listShip(this.date_selection);

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
		if ((this.crane_selected == null) || (this.ship_selected == null) || (this.user_position == null)) {
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

			final DetailFinalSchedule detail_schedule = new DetailFinalSchedule();

			detail_schedule.setTime_from(new Timestamp(user_detail.getTime_from().getTime()));
			detail_schedule.setTime_to(new Timestamp(user_detail.getTime_to().getTime()));
			detail_schedule.setId_schedule(itm.getSchedule().getId());
			detail_schedule.setShift(user_detail.getShift());
			detail_schedule.setContinueshift(Boolean.FALSE);
			detail_schedule.setTask(user_detail.getTask());
			detail_schedule.setTime(user_detail.getTime());
			detail_schedule.setTime_vacation(user_detail.getTime_vacation());

			// info ship
			detail_schedule.setCrane(this.crane_selected.getNumber().toString());
			detail_schedule.setId_ship(this.ship_selected.getId());
			detail_schedule.setBoard(this.user_position);

			// define rif_sws
			final DetailScheduleShip shipdetail = this.service.selectInitialShipSchedule(this.date_selection,
					user_detail.getShift(), this.ship_selected.getId());
			if (shipdetail == null) {
				detail_schedule.setRif_sws(null);
			} else {
				detail_schedule.setRif_sws(shipdetail.getIdscheduleship());
			}

			this.schedule_dao.createDetailFinalSchedule(detail_schedule);

		}

		this.refreshDataAndCurrentShift();
	}

	@Command
	@NotifyChange({ "list_schedule_selected", "users", "shift_no", "status_view", "list_schedule_selected" })
	public void selectAllPosition() {

		this.status_schedule_selection = 3;
		this.refreshDataAndCurrentShift();

	}

	@Command
	@NotifyChange({ "list_schedule_selected", "users", "shift_no", "status_view", "list_schedule_selected" })
	public void selectOnlyProgram() {

		this.status_schedule_selection = 1;
		this.refreshDataAndCurrentShift();

	}

	@Command
	@NotifyChange({ "list_schedule_selected", "users", "shift_no", "status_view", "list_schedule_selected" })
	public void selectOnlyReview() {

		this.status_schedule_selection = 2;
		this.refreshDataAndCurrentShift();

	}

	@Command
	@NotifyChange({ "shift_no", "users", "list_ship", "list_schedule_selected", "detail_schedule_ship_selected",
			"date_selection" })
	public void selectToDay() {
		final Calendar calendar = Calendar.getInstance();
		this.date_selection = calendar.getTime();

		this.calculateShiftAndRefresh();

	}

	@Command
	@NotifyChange({ "shift_no", "users", "list_ship", "list_schedule_selected", "detail_schedule_ship_selected",
			"date_selection" })
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

		final Calendar now = Calendar.getInstance();

		final Integer h = now.get(Calendar.HOUR_OF_DAY);
		final Integer m = now.get(Calendar.MINUTE);

		String info_m = m.toString();
		if (info_m.length() == 1) {
			info_m = "0" + info_m;
		}

		final String current = "" + h + ":" + info_m;

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

	public void setSelect_position_on(final Boolean select_position_on) {
		this.select_position_on = select_position_on;
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

		this.service.updateDetailScheduleShipForMobile(this.detail_schedule_ship_selected.getId(),
				this.getShip_operation(), this.getShip_handswork());

		this.refreshShipDataAndCurrentShift();

	}

	@Command
	@NotifyChange({ "users", "shift_no", "status_view" })
	public void signIn() {

		if (this.status_view == 1) {

			for (final InitialScheduleSingleDetail itm : this.list_schedule_selected) {

				final Badge badge = new Badge();
				badge.setEventTime(Calendar.getInstance().getTime());
				badge.setEventType(Boolean.TRUE);
				badge.setIdschedule(itm.getSchedule().getId());

				this.service.createBadge(badge);
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

				this.service.createBadge(badge);

			}

			this.refreshDataAndCurrentShift();
		}
	}

	@Command
	@NotifyChange({ "users", "list_ship", "shift_no", "status_view", "list_schedule_selected",
			"detail_schedule_ship_selected", "select_position_on" })
	public void switchShipShift() {

		if (this.status_view == 1) {

			this.refreshShipDataAndCurrentShift();

		} else {

			this.refreshDataAndCurrentShift();
		}

	}

}
