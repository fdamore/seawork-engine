package org.uario.seaworkengine.zkevent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Scheduler;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.ISchedulerDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

public class SchedulerComposer extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long		serialVersionUID	= 1L;

	private ConfigurationDAO		configurationDAO;

	@Wire
	private Datebox					date_init_scheduler;

	// format
	private final SimpleDateFormat	formatter_ddmmm		= new SimpleDateFormat("dd/MMM");
	private final SimpleDateFormat	formatter_eeee		= new SimpleDateFormat("EEEE");

	@Wire
	private Listbox					grid_scheduler;

	@Wire
	private Div						info_scheduler;

	private final Logger			logger				= Logger.getLogger(SchedulerComposer.class);

	@Wire
	private Combobox				program_combo_task;

	@Wire
	private Comboitem				program_item;

	@Wire
	private Combobox				review;

	@Wire
	private Combobox				revision_combo_task;

	private ISchedulerDAO			schedulerDAO;

	@Listen("onChange = #date_init_scheduler")
	public void changeInitialDate() {

		this.setGridStructure(SchedulerComposer.this.date_init_scheduler.getValue());

	}

	@Override
	public void doFinally() throws Exception {

		SchedulerComposer.this.date_init_scheduler.setValue(Calendar.getInstance().getTime());

		this.schedulerDAO = (ISchedulerDAO) SpringUtil.getBean(BeansTag.SCHEDULER_DAO);
		this.configurationDAO = (ConfigurationDAO) SpringUtil.getBean(BeansTag.CONFIGURATION_DAO);

		this.getSelf().addEventListener(ZkEventsTag.onShowScheduler, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// set combo task
				final List<UserShift> list = SchedulerComposer.this.configurationDAO.loadShifts();
				SchedulerComposer.this.program_combo_task.setModel(new ListModelList<UserShift>(list));
				SchedulerComposer.this.revision_combo_task.setModel(new ListModelList<UserShift>(list));

				// set initial structure
				SchedulerComposer.this.setGridStructure(SchedulerComposer.this.date_init_scheduler.getValue());
				SchedulerComposer.this.setupValuesGrid();
			}
		});

		this.getSelf().addEventListener(ZkEventsTag.onShiftClick, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {
				SchedulerComposer.this.info_scheduler.setVisible(true);

				final String data_info = arg0.getData().toString();

				final int sss = 0;

			}
		});

	}

	@Listen("onClick = #refresh_command")
	public void refreshButton() {

		SchedulerComposer.this.date_init_scheduler.setValue(Calendar.getInstance().getTime());
		this.setGridStructure(this.date_init_scheduler.getValue());
		this.setupValuesGrid();
	}

	/**
	 * Save program
	 */
	@Listen("onChange = #ok_program")
	public void saveProgram() {

		if (this.grid_scheduler.getSelectedItem() == null) {
			return;
		}

	}

	/**
	 * initial structure
	 *
	 * @param initial_date
	 */
	private void setGridStructure(final Date initial_date) {
		if (initial_date == null) {
			return;
		}

		// set program version
		this.review.setSelectedItem(this.program_item);

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(initial_date);

		// set seven days
		for (int i = 0; i < 7; i++) {

			final int index_day = i + 1;

			final Auxheader month_head = (Auxheader) this.getSelf().getFellowIfAny("day_month_" + index_day);
			final Auxheader week_head = (Auxheader) this.getSelf().getFellowIfAny("day_week_" + index_day);
			if ((month_head == null) || (week_head == null)) {
				continue;
			}

			final Calendar current_calendar = Calendar.getInstance();
			current_calendar.setTime(calendar.getTime());
			current_calendar.add(Calendar.DAY_OF_YEAR, i);

			final String day_w = this.formatter_eeee.format(current_calendar.getTime());
			final String day_m = this.formatter_ddmmm.format(current_calendar.getTime());

			if (current_calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				week_head.setStyle("color:red");
				month_head.setStyle("color:red");
			} else {
				week_head.setStyle("color:black");
				month_head.setStyle("color:black");
			}

			week_head.setLabel(day_w.toUpperCase());
			month_head.setLabel(day_m.toUpperCase());

		}

	}

	/**
	 * setup values for grid
	 */
	private void setupValuesGrid() {

		final Date initial_date = DateUtils.truncate(this.date_init_scheduler.getValue(), Calendar.DATE);
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 7);
		final Date final_date = calendar.getTime();

		final List<Scheduler> list = this.schedulerDAO.selectSchedulers(initial_date, final_date);
		this.grid_scheduler.setModel(new ListModelList<Scheduler>(list));

	}
}
