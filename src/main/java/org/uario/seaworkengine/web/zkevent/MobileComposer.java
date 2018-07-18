package org.uario.seaworkengine.web.zkevent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.web.services.IWebServiceController;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;
import org.uario.seaworkengine.web.services.handler.InitialScheduleSingleDetail;
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
import org.zkoss.zk.ui.UiException;

public class MobileComposer {

	@SuppressWarnings("rawtypes")
	private class MyDateFormatConverter implements Converter {
		/**
		 * Convert String to Date.
		 *
		 * @param val
		 *            date in string form
		 * @param comp
		 *            associated component
		 * @param ctx
		 *            bind context for associate Binding and extra parameter (e.g.
		 *            format)
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
		 * @param val
		 *            date to be converted
		 * @param comp
		 *            associated component
		 * @param ctx
		 *            bind context for associate Binding and extra parameter (e.g.
		 *            format)
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

	/**
	 * Instace of converter
	 */
	private final MyDateFormatConverter			dateConverter		= new MyDateFormatConverter();

	private DetailScheduleShip					detail_schedule_ship_selected;

	private String								end_task;

	private List<DetailScheduleShip>			list_ship;

	private List<UserTask>						list_task;

	private String								note;

	private String								note_ship;

	private InitialScheduleSingleDetail			schedule_selected	= null;

	private IWebServiceController				service;

	private Integer								shift_no;

	private Ship								ship_selected;

	private List<Ship>							ships;

	private String								starting_task;

	private Integer								status_view			= 1;

	private TasksDAO							task_dao;

	private UserTask							user_task_selected;

	private List<InitialScheduleSingleDetail>	users;

	/**
	 * Add single detail
	 */
	@Command
	@NotifyChange({
	        "users", "shift_no", "status_view"
	})
	public void addRowSingleDetail() {

		this.refreshDataAndCurrentShift();
	}

	@Command
	@NotifyChange({
	        "status_view", "list_task"
	})
	public void addSchedule() {

		if (this.status_view == 1) {
			if (this.schedule_selected == null) {
				return;
			}

			this.list_task = this.task_dao.loadTasksByUserForMobile(this.schedule_selected.getPerson().getId());

			// get default task
			final UserTask def_task = this.task_dao.getDefault(this.schedule_selected.getPerson().getId());

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
	}

	/**
	 * Calculate Shift no for shift bar
	 */
	private void calculateShiftNo() {
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
	}

	@Command
	@NotifyChange({
	        "status_view", "note", "note_ship"
	})
	public void editNote() {

		// note for "TURNI"
		if (this.status_view == 1) {

			if (this.schedule_selected == null) {
				return;
			}

			this.note = this.service.getScheduleNote(this.schedule_selected.getSchedule().getId());

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

	public MyDateFormatConverter getDateConverter() {
		return this.dateConverter;
	}

	public DetailScheduleShip getDetail_schedule_ship_selected() {
		return this.detail_schedule_ship_selected;
	}

	public String getEnd_task() {
		return this.end_task;
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

	public InitialScheduleSingleDetail getSchedule_selected() {
		return this.schedule_selected;
	}

	public Integer getShift_no() {
		return this.shift_no;
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

	public UserTask getUser_task_selected() {
		return this.user_task_selected;
	}

	public List<InitialScheduleSingleDetail> getUsers() {
		return this.users;
	}

	@AfterCompose
	public void init(@ContextParam(ContextType.COMPONENT) final Component component) throws Exception {
		this.service = (IWebServiceController) SpringUtil.getBean(BeansTag.WEBCONTROLLER);
		this.task_dao = (TasksDAO) SpringUtil.getBean(BeansTag.TASK_DAO);

		this.refreshDataAndCurrentShift();

	}

	@Command
	@NotifyChange({
	        "users", "shift_no", "status_view"
	})
	public void modifyNote() {

		if (this.schedule_selected == null) {
			return;
		}

		this.service.updateScheduleNote(this.schedule_selected.getSchedule().getId(), this.note);

		this.refreshDataAndCurrentShift();

	}

	@Command
	@NotifyChange({
	        "list_ship", "shift_no", "status_view"
	})
	public void modifyShipNote() {

		if (this.detail_schedule_ship_selected == null) {
			return;
		}

		this.service.updateDetailScheduleShipNote(this.detail_schedule_ship_selected.getId(), this.note_ship);

		this.refreshShipDataAndCurrentShift();

	}

	@Command
	@NotifyChange({
	        "users", "status_view"
	})
	public void refresh(@BindingParam("shift_no") final Integer shift_no) {

		if (this.users != null) {
			this.users.clear();
		} else {
			this.users = new ArrayList<>();
		}

		final List<InitialSchedule> list = this.service.selectInitialSchedule(Calendar.getInstance().getTime());

		// POST PROCESSING
		for (final InitialSchedule insch : list) {
			if (insch.getDetail_schedule() == null) {
				continue;
			}

			for (final DetailInitialSchedule detail : insch.getDetail_schedule()) {

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

				this.users.add(itm);

			}

		}

		// refresh ship list
		this.ships = this.service.listShip(Calendar.getInstance().getTime());

		// set view to status 1
		this.status_view = 1;

	}

	@Command
	@NotifyChange({
	        "users", "shift_no", "status_view"
	})
	public void refreshDataAndCurrentShift() {

		this.calculateShiftNo();

		// define status view
		this.status_view = 1;

		// refresh with shift_no
		this.refresh(this.shift_no);
	}

	@Command
	@NotifyChange({
	        "list_ship", "shift_no", "status_view"
	})
	public void refreshShipDataAndCurrentShift() {

		this.calculateShiftNo();

		// return to list of ship
		this.status_view = 4;

		this.list_ship = this.service.selectDetailScheduleShipByShiftDate(Calendar.getInstance().getTime());

	}

	/**
	 * @param filed
	 *            0 for initial, 1 for final
	 */
	@Command
	@NotifyChange({
	        "starting_task", "end_task"
	})
	public void setCurrentTaskTime(@BindingParam("field") final int field) {

		final Calendar now = Calendar.getInstance();

		final Integer h = now.get(Calendar.HOUR_OF_DAY);
		final Integer m = now.get(Calendar.MINUTE);

		final String current = "" + h + ":" + m;

		if (field == 0) {
			this.starting_task = current;
		} else if (field == 1) {
			this.end_task = current;
		}

	}

	public void setDetail_schedule_ship_selected(final DetailScheduleShip detail_schedule_ship_selected) {
		this.detail_schedule_ship_selected = detail_schedule_ship_selected;
	}

	public void setEnd_task(final String end_task) {
		this.end_task = end_task;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setNote_ship(final String note_ship) {
		this.note_ship = note_ship;
	}

	public void setSchedule_selected(final InitialScheduleSingleDetail schedule_selected) {
		this.schedule_selected = schedule_selected;
	}

	public void setShip_selected(final Ship ship_selected) {
		this.ship_selected = ship_selected;
	}

	public void setStarting_task(final String starting_task) {
		this.starting_task = starting_task;
	}

	public void setUser_task_selected(final UserTask user_task_selected) {
		this.user_task_selected = user_task_selected;
	}

	@Command
	@NotifyChange({
	        "users", "shift_no", "status_view", "list_ship"
	})
	public void switchShipShift() {

		if (this.status_view == 1) {

			this.refreshShipDataAndCurrentShift();

		} else {

			this.refreshDataAndCurrentShift();
		}

	}

}
