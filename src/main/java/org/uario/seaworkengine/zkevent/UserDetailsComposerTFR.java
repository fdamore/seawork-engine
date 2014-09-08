package org.uario.seaworkengine.zkevent;

import java.util.List;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.TfrUser;
import org.uario.seaworkengine.platform.persistence.dao.TfrDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class UserDetailsComposerTFR extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	@Wire
	private Component			grid_details;

	private final Logger		logger				= Logger.getLogger(UserDetailsComposerTFR.class);

	private Person				person_selected;

	@Wire
	private Listbox				sw_list;

	// dao interface
	private TfrDAO				tfrDao;

	@Wire
	private Textbox				user_tfr;

	@Wire
	private Datebox				user_tfr_date;

	@Listen("onClick = #add_command")
	public void addTaskUser() {

		if (this.person_selected == null) {
			return;
		}

		final TfrUser item = new TfrUser();
		item.setTfr_destination(this.user_tfr.getValue());
		item.setTfr_selection_date(this.user_tfr_date.getValue());

		this.tfrDao.createTGRForUser(this.person_selected.getId(), item);

		Messagebox.show("Mansione aggiunta all'utente", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		// Refresh list task
		this.setInitialView();

	}

	@Listen("onClick = #delete_command")
	public void deleteTaskUser() {

		if (this.sw_list.getSelectedItem() == null) {
			return;
		}

		if (this.person_selected == null) {
			return;
		}

		final TfrUser item = this.sw_list.getSelectedItem().getValue();

		this.tfrDao.removeTFR(item.getId());
		Messagebox.show("Scelta TFR rimossa", "INFO", Messagebox.OK, Messagebox.INFORMATION);

		// Refresh list task
		this.setInitialView();

	}

	@Override
	public void doFinally() throws Exception {

		this.getSelf().addEventListener(ZkEventsTag.onShowUsers, new EventListener<Event>() {

			@Override
			public void onEvent(final Event arg0) throws Exception {

				// get selected person
				if ((arg0.getData() == null) || !(arg0.getData() instanceof Person)) {
					return;
				}
				UserDetailsComposerTFR.this.person_selected = (Person) arg0.getData();

				// get the person dao
				UserDetailsComposerTFR.this.tfrDao = (TfrDAO) SpringUtil.getBean(BeansTag.TFR_DAO);

				UserDetailsComposerTFR.this.setInitialView();

			}
		});

	}

	@Listen("onClick = #sw_refresh_list")
	public void setInitialView() {

		if (this.person_selected == null) {
			return;
		}

		final List<TfrUser> list = this.tfrDao.loadTFRByUser(this.person_selected.getId());
		this.sw_list.setModel(new ListModelList<TfrUser>(list));

		this.grid_details.setVisible(false);
	}

}
