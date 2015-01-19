package org.uario.seaworkengine.zkevent;

import org.springframework.security.core.context.SecurityContextHolder;
import org.uario.seaworkengine.model.Person;
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
	private Button				sw_home_button;

	@Wire
	private Button				sw_scheduler_button;

	@Wire
	private Button				sw_ship_button;

	@Wire
	private Button				sw_shipScheduler_button;

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

		this.showNavigationButton(false);

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

	/**
	 * Show navigation button... home... Schedules..... users...
	 *
	 * @param visible
	 */
	private void showNavigationButton(final Boolean visible) {
		this.sw_home_button.setVisible(visible);
		this.sw_scheduler_button.setVisible(visible);
		this.sw_ship_button.setVisible(visible);
		this.sw_shipScheduler_button.setVisible(visible);

		if (visible) {
			// show only for SUPERUSERS
			final Person person_logged = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if ((person_logged != null) && person_logged.isAdministrator()) {
				this.sw_user_button.setVisible(visible);
			}

		} else {
			this.sw_user_button.setVisible(visible);
		}

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

	@Listen("onClick = #sw_scheduler_button, #sw_scheduler_button_main")
	public void showProgramConfigurator(final Event event) {

		this.showNavigationButton(true);

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

	@Listen("onClick = #sw_shipScheduler_button, #sw_shipScheduler_button_main")
	public void showShipDetails(final Event event) {

		this.showNavigationButton(true);
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

	@Listen("onClick = #sw_ship_button, #sw_ship_button_main")
	public void showShipScheduler(final Event event) {

		this.showNavigationButton(true);
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

	@Listen("onClick = #sw_user_button, #sw_user_button_main")
	public void showUserDetais(final Event event) {

		this.showNavigationButton(true);

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
