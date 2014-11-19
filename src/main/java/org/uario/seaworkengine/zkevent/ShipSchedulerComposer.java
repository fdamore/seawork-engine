package org.uario.seaworkengine.zkevent;

import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;

public class ShipSchedulerComposer extends SelectorComposer<Component> {

	@Override
	public void doFinally() throws Exception {

		this.getSelf().addEventListener(ZkEventsTag.onShowScheduler, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

			}
		});

		// SHOW SHIFT CONFIGURATOR
		this.getSelf().addEventListener(ZkEventsTag.onShowShipScheduler, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// final String data_info = arg0.getData().toString();

			}

		});

		// SHOW SHIFT CONFIGURATOR
		this.getSelf().addEventListener(ZkEventsTag.onShiftClickReview, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				final String data_info = arg0.getData().toString();

			}

		});

	}

}
