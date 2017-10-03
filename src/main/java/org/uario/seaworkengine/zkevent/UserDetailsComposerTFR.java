package org.uario.seaworkengine.zkevent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.TfrUser;
import org.uario.seaworkengine.platform.persistence.dao.TfrDAO;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.utility.UtilityCSV;
import org.uario.seaworkengine.utility.ZkEventsTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
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

	private boolean				tsatus_add			= true;

	@Wire
	private Textbox				user_tfr;

	@Wire
	private Datebox				user_tfr_date;

	@Wire
	private Textbox				user_tfr_note;

	@Listen("onClick = #add_command")
	public void addItemToUser() {

		if (this.person_selected == null) {
			return;
		}

		if (this.tsatus_add) {

			final TfrUser item = new TfrUser();
			item.setTfr_destination(this.user_tfr.getValue());
			item.setTfr_selection_date(this.user_tfr_date.getValue());
			item.setNote(this.user_tfr_note.getValue());

			this.tfrDao.createTGRForUser(this.person_selected.getId(), item);
		} else {

			final TfrUser tfr_user = this.sw_list.getSelectedItem().getValue();

			tfr_user.setNote(this.user_tfr_note.getValue());
			tfr_user.setTfr_destination(this.user_tfr.getValue());
			tfr_user.setTfr_selection_date(this.user_tfr_date.getValue());

			this.tfrDao.updateTfr(tfr_user);

		}

		// Refresh list task
		this.setInitialView();

	}

	private void deleteItemToUser() {

		if (this.sw_list.getSelectedItem() == null) {
			return;
		}

		if (this.person_selected == null) {
			return;
		}

		final TfrUser item = this.sw_list.getSelectedItem().getValue();

		this.tfrDao.removeTFR(item.getId());

		final Map<String, String> params = new HashMap<>();
		params.put("sclass", "mybutton Button");
		final Messagebox.Button[] buttons = new Messagebox.Button[1];
		buttons[0] = Messagebox.Button.OK;

		Messagebox.show("Scelta TFR rimossa", "INFO", buttons, null, Messagebox.INFORMATION, null, null, params);

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

	@Listen("onClick = #user_tfr_csv")
	public void downloadCSV_user_tfr_csv() {

		if ((this.person_selected == null) || (this.person_selected.getId() == null)) {
			return;
		}

		final List<TfrUser> list = this.tfrDao.loadTFRByUser(this.person_selected.getId());
		final StringBuilder builder = UtilityCSV.downloadCSV_user_tfr(list);
		Filedownload.save(builder.toString(), "application/text", "info_tfr.csv");

	}

	@Listen("onClick = #sw_link_modify")
	public void modifyItem() {

		if (this.person_selected == null) {
			return;
		}

		if (this.sw_list.getSelectedItem() == null) {
			return;
		}

		final TfrUser tfr_user = this.sw_list.getSelectedItem().getValue();

		this.user_tfr.setValue(tfr_user.getTfr_destination());
		this.user_tfr_date.setValue(tfr_user.getTfr_selection_date());
		this.user_tfr_note.setValue(tfr_user.getNote());

		this.tsatus_add = false;

		this.grid_details.setVisible(true);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Listen("onClick = #sw_link_delete")
	public void removeItem() {

		final Map<String, String> params = new HashMap<>();
		params.put("sclass", "mybutton Button");

		final Messagebox.Button[] buttons = new Messagebox.Button[2];
		buttons[0] = Messagebox.Button.OK;
		buttons[1] = Messagebox.Button.CANCEL;

		Messagebox.show("Vuoi cancellare la voce selezionata?", "CONFERMA CANCELLAZIONE", buttons, null, Messagebox.EXCLAMATION, null,
				new EventListener() {
					@Override
					public void onEvent(final Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							UserDetailsComposerTFR.this.deleteItemToUser();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				}, params);

	}

	@Listen("onClick = #sw_refresh_list")
	public void setInitialView() {

		if (this.person_selected == null) {
			return;
		}

		final List<TfrUser> list = this.tfrDao.loadTFRByUser(this.person_selected.getId());
		this.sw_list.setModel(new ListModelList<>(list));

		this.grid_details.setVisible(false);
	}

	@Listen("onClick = #sw_add")
	public void showAdd() {

		this.user_tfr.setValue(null);
		this.user_tfr_date.setValue(null);
		this.user_tfr_note.setValue(null);

		this.tsatus_add = true;

		this.grid_details.setVisible(true);

	}

}
