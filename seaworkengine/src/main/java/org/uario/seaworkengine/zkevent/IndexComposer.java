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

public class IndexComposer extends SelectorComposer<Component>
{

    private static String     HOME_PAGE_NAME        = "Home";

    private static String     MY_PROFILE_PAGE_NAME  = "Profilo utente";

    private static String     PREFERENCES_PAGE_NAME = "Preferenze";

    /**
     *
     */
    private static final long serialVersionUID      = 1L;

    private static String     USERDETAILS_PAGE_NAME = "Dettagli Utente";

    @Wire
    private Include           include_home;

    @Wire
    private Include           include_myprofile;

    @Wire
    private Include           include_preferences;

    @Wire
    private Include           include_user_detail;

    @Wire
    private Label             sw_brec_position;

    @Wire
    private Button            sw_user_button;

    /** Show profile
     * @param event
     */
    private void showMyProfile(final Event event)
    {

        this.include_home.setVisible(false);
        this.include_user_detail.setVisible(false);
        this.include_preferences.setVisible(false);
        this.include_myprofile.setVisible(true);

        final Component comp = Path.getComponent("//myprofile/page_user_detail");

        // send event to show users
        Events.sendEvent(ZkEventsTag.onShowUsers, comp, null);

        this.sw_brec_position.setValue(IndexComposer.MY_PROFILE_PAGE_NAME);

    }

    private void showPreferences(final Event event)
    {

        this.include_home.setVisible(false);
        this.include_user_detail.setVisible(false);
        this.include_preferences.setVisible(true);
        this.include_myprofile.setVisible(false);

        final Component comp = Path.getComponent("//preferences/page_preferences");

        // send event to show users
        Events.sendEvent(ZkEventsTag.onShowPreferences, comp, null);

        this.sw_brec_position.setValue(IndexComposer.PREFERENCES_PAGE_NAME);

    }

    @Override
    public void doFinally() throws Exception
    {
        this.getPage().addEventListener(ZkEventsTag.onShowPreferences, new EventListener<Event>()
            {

                @Override
                public void onEvent(final Event event) throws Exception
                {
                    IndexComposer.this.showPreferences(event);

                }
            });

        this.getPage().addEventListener(ZkEventsTag.onShowMyProfile, new EventListener<Event>()
            {

                @Override
                public void onEvent(final Event event) throws Exception
                {
                    IndexComposer.this.showMyProfile(event);

                }
            });

    }

    @Listen("onClick = #sw_home_button")
    public void showHome(final Event event)
    {

        this.include_home.setVisible(true);
        this.include_user_detail.setVisible(false);
        this.include_preferences.setVisible(false);
        this.include_myprofile.setVisible(false);
        this.sw_brec_position.setValue(IndexComposer.HOME_PAGE_NAME);

    }

    @Listen("onClick = #sw_user_button")
    public void showUserDetais(final Event event)
    {

        this.include_home.setVisible(false);
        this.include_user_detail.setVisible(true);
        this.include_preferences.setVisible(false);
        this.include_myprofile.setVisible(false);

        final Component comp = Path.getComponent("//user/page_user_detail");

        // send event to show users
        Events.sendEvent(ZkEventsTag.onShowUsers, comp, null);

        this.sw_brec_position.setValue(IndexComposer.USERDETAILS_PAGE_NAME);

    }
}
