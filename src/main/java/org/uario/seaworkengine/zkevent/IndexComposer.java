package org.uario.seaworkengine.zkevent;

import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;

public class IndexComposer extends SelectorComposer<Component> {

	private static String		HOME_PAGE_NAME				= "Home";

	private static String		MY_PROFILE_PAGE_NAME		= "Profilo utente";

	private static String		PREFERENCES_PAGE_NAME		= "Preferenze";

	private static String		PROGRAM_PAGE_NAME			= "Programma Lavoro";

	/**
	 *
	 */
	private static final long	serialVersionUID			= 1L;

	private static String		SHIP_SCHEDULER_PAGE_NAME	= "Schedulatore Navi";

	private static String		SHIPDETAILS_PAGE_NAME		= "Dettagli Navi";

	private static String		USERDETAILS_PAGE_NAME		= "Dettagli Utente";

	@Wire
	private Include				include_home;

	@Wire
	private Include				include_myprofile;

	@Wire
	private Include				include_preferences;

	@Wire
	private Include				include_scheduler;

	@Wire
	private Include				include_ship_detail;

	@Wire
	private Include				include_ship_scheduler;

	@Wire
	private Include				include_user_detail;

	@Wire
	private Label				sw_brec_position;

	@Wire
	private Button				sw_user_button;

	@Override
	public void doFinally() throws Exception {
		this.getPage().addEventListener(ZkEventsTag.onShowPreferences, new EventListener<Event>() {

			@Override
			public void onEvent(final Event event) throws Exception {
				IndexComposer.this.showPreferences(event);

			}
		});

		this.getPage().addEventListener(ZkEventsTag.onShowMyProfile, new EventListener<Event>() {

			@Override
			public void onEvent(final Event event) throws Exception {
				IndexComposer.this.showMyProfile(event);

			}
		});

	}

	@Listen("onClick = #sw_home_button")
	public void showHome(final Event event) {

		this.include_home.setVisible(true);
		this.include_user_detail.setVisible(false);
		this.include_preferences.setVisible(false);
		this.include_myprofile.setVisible(false);
		this.include_scheduler.setVisible(false);
		this.include_ship_scheduler.setVisible(false);
		this.include_ship_detail.setVisible(false);

		this.sw_brec_position.setValue(IndexComposer.HOME_PAGE_NAME);

	}

	/**
	 * Show profile
	 *
	 * @param event
	 */
	private void showMyProfile(final Event event) {

		this.include_home.setVisible(false);
		this.include_user_detail.setVisible(false);
		this.include_preferences.setVisible(false);
		this.include_myprofile.setVisible(true);
		this.include_scheduler.setVisible(false);
		this.include_ship_scheduler.setVisible(false);
		this.include_ship_detail.setVisible(false);

		final Component comp = Path.getComponent("//myprofile/page_user_detail");

		// send event to show users
		Events.sendEvent(ZkEventsTag.onShowUsers, comp, null);

		this.sw_brec_position.setValue(IndexComposer.MY_PROFILE_PAGE_NAME);

	}

	private void showPreferences(final Event event) {

		this.include_home.setVisible(false);
		this.include_user_detail.setVisible(false);
		this.include_preferences.setVisible(true);
		this.include_myprofile.setVisible(false);
		this.include_scheduler.setVisible(false);
		this.include_ship_scheduler.setVisible(false);
		this.include_ship_detail.setVisible(false);

		final Component comp = Path.getComponent("//preferences/page_preferences");

		// send event to show users
		Events.sendEvent(ZkEventsTag.onShowPreferences, comp, null);

		this.sw_brec_position.setValue(IndexComposer.PREFERENCES_PAGE_NAME);

	}

	@Listen("onClick = #sw_scheduler_button")
	public void showProgramConfigurator(final Event event) {

		this.include_home.setVisible(false);
		this.include_user_detail.setVisible(false);
		this.include_preferences.setVisible(false);
		this.include_myprofile.setVisible(false);
		this.include_scheduler.setVisible(true);
		this.include_ship_scheduler.setVisible(false);
		this.include_ship_detail.setVisible(false);

		final Component comp = Path.getComponent("//scheduler/page_panel");
		// send event to show users
		Events.sendEvent(ZkEventsTag.onShowScheduler, comp, null);

		this.sw_brec_position.setValue(IndexComposer.PROGRAM_PAGE_NAME);

	}

	@Listen("onClick = #sw_shipScheduler_button")
	public void showShipDetails(final Event event) {
		this.include_home.setVisible(false);
		this.include_user_detail.setVisible(false);
		this.include_preferences.setVisible(false);
		this.include_myprofile.setVisible(false);
		this.include_scheduler.setVisible(false);
		this.include_ship_scheduler.setVisible(true);
		this.include_ship_detail.setVisible(false);

		final Component comp = Path.getComponent("//shipscheduler/page_ship_scheduler");
		// send event to show users
		Events.sendEvent(ZkEventsTag.onShowShipScheduler, comp, null);

		this.sw_brec_position.setValue(IndexComposer.SHIP_SCHEDULER_PAGE_NAME);
	}

	@Listen("onClick = #sw_ship_button")
	public void showShipScheduler(final Event event) {
		this.include_home.setVisible(false);
		this.include_user_detail.setVisible(false);
		this.include_preferences.setVisible(false);
		this.include_myprofile.setVisible(false);
		this.include_scheduler.setVisible(false);
		this.include_ship_scheduler.setVisible(false);
		this.include_ship_detail.setVisible(true);

		final Component comp = Path.getComponent("//ship/page_ship_detail");
		// send event to show users
		Events.sendEvent(ZkEventsTag.onShowShips, comp, null);

		this.sw_brec_position.setValue(IndexComposer.SHIPDETAILS_PAGE_NAME);

	}

	@Listen("onClick = #sw_user_button")
	public void showUserDetais(final Event event) {

		this.include_home.setVisible(false);
		this.include_user_detail.setVisible(true);
		this.include_preferences.setVisible(false);
		this.include_myprofile.setVisible(false);
		this.include_scheduler.setVisible(false);
		this.include_ship_scheduler.setVisible(false);
		this.include_ship_detail.setVisible(false);

		final Component comp = Path.getComponent("//user/page_user_detail");

		// send event to show users
		Events.sendEvent(ZkEventsTag.onShowUsers, comp, null);

		this.sw_brec_position.setValue(IndexComposer.USERDETAILS_PAGE_NAME);

	}

}
