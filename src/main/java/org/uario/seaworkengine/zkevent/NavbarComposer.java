package org.uario.seaworkengine.zkevent;

import java.text.NumberFormat;

import org.springframework.security.core.context.SecurityContextHolder;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.A;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Progressmeter;

public class NavbarComposer extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	@Wire
	private Progressmeter		allocated_mem_pc;

	@Wire
	private Label				allocated_memoery_pop_value;

	@Wire
	private A					atask, anoti, amsg;

	@Wire
	private Progressmeter		free_mem_pc;

	@Wire
	private Label				free_memoery_pop_value;

	@Wire
	private Label				max_memoery_pop_value;

	private final NumberFormat	numberFormat		= NumberFormat.getInstance();

	private final Runtime		runtime				= Runtime.getRuntime();

	@Wire
	private Menu				user_welcome;

	@Override
	public void doFinally() throws Exception {

		// set info about user
		final Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.user_welcome.setLabel(person.getIndividualName());
	}

	@Listen("onClick = #sw_logout")
	public void logoutApplication(final Event event) {

		Executions.sendRedirect("/j_spring_security_logout");
	}

	@Listen("onClick = #gs_preferences")
	public void showPreferences() {

		final Component comp = Path.getComponent("//index/main");

		// send event to show users
		Events.sendEvent(ZkEventsTag.onShowPreferences, comp, null);

		this.toggleOpenClass(false, this.atask);

	}

	@Listen("onClick = #user_profile")
	public void showUserProfile() {

		final Component comp = Path.getComponent("//index/main");

		// send event to show users
		Events.sendEvent(ZkEventsTag.onShowMyProfile, comp, null);

	}

	// Toggle open class to the component
	private void toggleOpenClass(final Boolean open, final Component component) {
		final HtmlBasedComponent comp = (HtmlBasedComponent) component;
		final String scls = comp.getSclass();
		if (open) {
			comp.setSclass(scls + " open");
		} else {
			comp.setSclass(scls.replace(" open", ""));
		}
	}

	@Listen("onOpen = #taskpp")
	public void toggleTaskPopup(final OpenEvent event) {

		final long maxMemory = this.runtime.maxMemory();
		final long freeMemory = this.runtime.freeMemory();
		final long allocatedMemory = this.runtime.totalMemory();

		// check for percentage:
		final long free_pc = (100 * freeMemory) / maxMemory;
		final long allocated_pc = (100 * allocatedMemory) / maxMemory;

		Integer free_pc_info = 100;
		if (free_pc < 100) {
			free_pc_info = (int) free_pc;
		}

		Integer allocated_pc_info = 100;
		if (allocated_pc < 100) {
			allocated_pc_info = (int) allocated_pc;
		}
		this.free_mem_pc.setValue(free_pc_info);
		this.allocated_mem_pc.setValue(allocated_pc_info);

		// set value
		final String max_memory_info = this.numberFormat.format(maxMemory / 1024);
		final String free_memory_info = this.numberFormat.format(freeMemory / 1024);
		final String allocated_memory_info = this.numberFormat.format(allocatedMemory / 1024);

		this.max_memoery_pop_value.setValue(max_memory_info);
		this.free_memoery_pop_value.setValue(free_memory_info);
		this.allocated_memoery_pop_value.setValue(allocated_memory_info);

		this.toggleOpenClass(event.isOpen(), this.atask);
	}

}
