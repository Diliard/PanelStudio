package com.lukflug.panelstudio.widget;

import com.lukflug.panelstudio.base.AnimatedToggleable;
import com.lukflug.panelstudio.base.Animation;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.component.CollapsibleComponent;
import com.lukflug.panelstudio.component.ComponentProxy;
import com.lukflug.panelstudio.component.FocusableComponentProxy;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.container.VerticalContainer;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.IContainerRenderer;
import com.lukflug.panelstudio.theme.IPanelRenderer;

/**
 * A panel that can be opened and closed.
 * @author lukflug
 */
public class Panel extends FocusableComponentProxy {
	/**
	 * The current collapsible component.
	 */
	protected CollapsibleComponent collapsible;
	
	/**
	 * Creates a generic panel.
	 * @param title the title component of the panel
	 * @param content the content of the panel
	 * @param active whether this panel is active
	 * @param open the toggleable to use for opening and closing the panel 
	 * @param animation the animation to use for opening and closing the panel
	 * @param panelRenderer the render to use for the overlay of this panel
	 * @return a vertical container having the functionality of a panel
	 */
	public Panel (IComponent title, IComponent content, IBoolean active, IToggleable open, Animation animation, IPanelRenderer panelRenderer) {
		super(null);
		VerticalContainer container=new VerticalContainer(new Labeled(title.getTitle(),null,()->content.isVisible()),new IContainerRenderer(){}) {
			@Override
			public void render (Context context) {
				super.render(context);
				panelRenderer.renderPanelOverlay(context,hasFocus(context),active.isOn());
			}
		};
		collapsible=new CollapsibleComponent(content,open,animation);
		container.addComponent(new ComponentProxy(title) {
			@Override
			public void handleButton (Context context, int button) {
				super.handleButton(context,button);
				if (button==IInterface.RBUTTON && context.isClicked()) {
					collapsible.getToggle().toggle();
				}
			}
		});
		container.addComponent(collapsible);
		this.component=container;
	}
	
	/**
	 * Returns the current toggleable used.
	 * @return the current toggle
	 */
	public AnimatedToggleable getToggle() {
		return collapsible.getToggle();
	}
}
