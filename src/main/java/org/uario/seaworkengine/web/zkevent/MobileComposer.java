package org.uario.seaworkengine.web.zkevent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.DetailInitialSchedule;
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

	private InitialScheduleSingleDetail			schedule_selected	= null;

	private IWebServiceController				service;

	private Integer								shift_no;

	private Integer								status_view			= 1;

	private TasksDAO							task_dao;

	private List<InitialScheduleSingleDetail>	users;

	@Command
	@NotifyChange({ "status_view" })
	public void addSchedule() {
		if (this.schedule_selected == null) {
			return;
		}
		this.status_view = 2;
	}

	public MyDateFormatConverter getDateConverter() {
		return this.dateConverter;
	}

	public InitialScheduleSingleDetail getSchedule_selected() {
		return this.schedule_selected;
	}

	public Integer getShift_no() {
		return this.shift_no;
	}

	public Integer getStatus_view() {
		return this.status_view;
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
	@NotifyChange({ "users", "status_view" })
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

		// set view to status 1
		this.status_view = 1;

	}

	@Command
	@NotifyChange({ "users", "shift_no", "status_view" })
	public void refreshDataAndCurrentShift() {
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

		// define status view
		this.status_view = 1;

		// refresh with shift_no
		this.refresh(this.shift_no);
	}

	public void setSchedule_selected(final InitialScheduleSingleDetail schedule_selected) {
		this.schedule_selected = schedule_selected;
	}

}
