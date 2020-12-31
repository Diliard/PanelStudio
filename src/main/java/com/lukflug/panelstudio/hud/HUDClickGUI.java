package com.lukflug.panelstudio.hud;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.lukflug.panelstudio.ClickGUI;
import com.lukflug.panelstudio.FixedComponent;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.settings.Toggleable;
import com.lukflug.panelstudio.theme.DescriptionRenderer;

/**
 * ClickGUI that only renders HUD components when closed.
 * @author lukflug
 */
public class HUDClickGUI extends ClickGUI implements Toggleable {
	/**
	 * List of all components.
	 */
	protected List<FixedComponent> allComponents=new ArrayList<FixedComponent>();
	/**
	 * List of HUD components.
	 */
	protected Set<FixedComponent> hudComponents=new HashSet<FixedComponent>();
	/**
	 * Whether the GUI components are shown or not.
	 */
	protected boolean guiOpen=false;
	
	/**
	 * Constructor.
	 * @param inter the interface for the ClickGUI
	 */
	public HUDClickGUI (Interface inter, DescriptionRenderer renderer) {
		super(inter,renderer);
	}
	
	/**
	 * Add component to {@link #allComponents} instead of the {@link ClickGUI} list.
	 */
	@Override
	public void addComponent (FixedComponent component) {
		allComponents.add(component);
		permanentComponents.add(component);
		if (guiOpen) components.add(component);
	}

	@Override
	public void showComponent(FixedComponent component) {
		if (!allComponents.contains(component)) {
			allComponents.add(component);
			if (guiOpen) {
				components.add(component);
				component.enter(getContext(component,false));
			}
		}
	}

	@Override
	public void hideComponent(FixedComponent component) {
		if (!permanentComponents.contains(component)) {
			allComponents.remove(component);
			if (components.remove(component)) component.exit(getContext(component,false));
		}
	}
	
	/**
	 * Add component to {@link #allComponents} and {@link #hudComponents}.
	 * @param component the new HUD component
	 */
	public void addHUDComponent (FixedComponent component) {
		hudComponents.add(component);
		allComponents.add(component);
		components.add(component);
		permanentComponents.add(component);
	}
	
	/**
	 * Handle the GUI being opened.
	 */
	@Override
	public void enter() {
		components=allComponents;
		guiOpen=true;
		doComponentLoop((context,component)->{
			if (!hudComponents.contains(component)) component.enter(context);
		});
	}
	
	/**
	 * Handle the GUI being closed.
	 */
	@Override
	public void exit() {
		guiOpen=false;
		doComponentLoop((context,component)->{
			if (!hudComponents.contains(component)) component.exit(context);
		});
		selectHUDComponents();
	}

	/**
	 * Toggles GUI open state and chooses right components to render.
	 */
	@Override
	public void toggle() {
		if (!guiOpen) enter();
		else exit();
	}

	/**
	 * Returns whether GUI is open.
	 */
	@Override
	public boolean isOn() {
		return guiOpen;
	}
	
	/**
	 * Add only HUD components to component list.
	 */
	protected void selectHUDComponents() {
		components=new ArrayList<FixedComponent>();
		for (FixedComponent component: allComponents) {
			if (hudComponents.contains(component)) components.add(component);
		}
	}

	@Override
	public Toggleable getComponentToggleable(FixedComponent component) {
		return new Toggleable() {
			@Override
			public void toggle() {
				if (isOn()) hideComponent(component);
				else showComponent(component);
			}

			@Override
			public boolean isOn() {
				return allComponents.contains(component);
			}
		};
	}
}
