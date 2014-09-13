package org.uario.seaworkengine.zkevent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Datebox;

public class SchedulerComposer extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long		serialVersionUID	= 1L;

	@Wire
	private Datebox					date_init_scheduler;

	private final SimpleDateFormat	formatter_ddmmm		= new SimpleDateFormat("dd/MMM");
	private final SimpleDateFormat	formatter_eeee		= new SimpleDateFormat("EEEE");

	private final Logger			logger				= Logger.getLogger(SchedulerComposer.class);

	@Listen("onChange = #date_init_scheduler")
	public void changeInitialDate() {

		this.setGridStructure(SchedulerComposer.this.date_init_scheduler.getValue());

	}

	@Override
	public void doFinally() throws Exception {

		SchedulerComposer.this.date_init_scheduler.setValue(Calendar.getInstance().getTime());

		this.getSelf().addEventListener(ZkEventsTag.onShowScheduler, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// set initial structure
				SchedulerComposer.this.setGridStructure(SchedulerComposer.this.date_init_scheduler.getValue());
			}
		});

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

			week_head.setLabel(day_w.toUpperCase());
			month_head.setLabel(day_m.toUpperCase());

		}

	}

}
