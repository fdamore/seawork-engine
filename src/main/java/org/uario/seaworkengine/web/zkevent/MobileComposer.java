package org.uario.seaworkengine.web.zkevent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.ITaskCache;
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
	private final MyDateFormatConverter dateConverter = new MyDateFormatConverter();

	private IWebServiceController service;

	private ITaskCache task_cask;

	private List<InitialScheduleSingleDetail> users;

	public MyDateFormatConverter getDateConverter() {
		return this.dateConverter;
	}

	public List<InitialScheduleSingleDetail> getUsers() {
		return this.users;
	}

	@AfterCompose
	public void init(@ContextParam(ContextType.COMPONENT) final Component component) throws Exception {
		this.service = (IWebServiceController) SpringUtil.getBean(BeansTag.WEBCONTROLLER);
		this.task_cask = (ITaskCache) SpringUtil.getBean(BeansTag.TASK_CACHE);

		this.refresh(null);

	}

	@Command
	@NotifyChange({ "users" })
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

				final UserTask user_task = this.task_cask.getUserTask(detail.getTask());

				final InitialScheduleSingleDetail itm = new InitialScheduleSingleDetail();
				itm.setDetail_schedule(detail);
				itm.setPerson(insch.getPerson());
				itm.setSchedule(insch.getSchedule());
				itm.setUser_task(user_task);

				this.users.add(itm);

			}

		}
	}

}
