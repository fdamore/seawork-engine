package org.uario.seaworkengine.zkevent;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;

public class TaskComposer extends SelectorComposer<Component> {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private List				children;

	private Component			comp;

	@Override
	public void doAfterCompose(final Component comp) throws Exception {
		super.doAfterCompose(comp);
		this.comp = comp;
		this.children = comp.getChildren();

	}

	@Listen("onDrop = listitem")
	public void doDrop(final DropEvent event) {
		final Component droped = event.getTarget();
		final Component dragged = event.getDragged();
		if (this.children.indexOf(dragged) > this.children.indexOf(droped)) {
			this.comp.insertBefore(dragged, droped);
		} else {
			this.comp.insertBefore(dragged, droped.getNextSibling());
		}
	}

}
