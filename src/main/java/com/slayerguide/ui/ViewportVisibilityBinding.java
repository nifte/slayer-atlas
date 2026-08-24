package com.slayerguide.ui;

import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;

public final class ViewportVisibilityBinding
{
	private static final String KEY = "slayerAtlasViewportBinding";

	private ViewportVisibilityBinding()
	{
	}

	public static void bind(JComponent component, Runnable onMaybeVisible)
	{
		if (component == null || onMaybeVisible == null)
		{
			return;
		}
		if (component.getClientProperty(KEY) != null)
		{
			return;
		}
		Runnable check = () ->
		{
			if (isVisible(component))
			{
				onMaybeVisible.run();
			}
		};
		ChangeListener viewportListener = event -> SwingUtilities.invokeLater(check);
		component.putClientProperty(KEY, viewportListener);
		component.addHierarchyListener(event ->
		{
			if ((event.getChangeFlags() & (HierarchyEvent.PARENT_CHANGED | HierarchyEvent.SHOWING_CHANGED | HierarchyEvent.DISPLAYABILITY_CHANGED)) == 0)
			{
				return;
			}
			syncViewport(component, viewportListener);
			SwingUtilities.invokeLater(check);
		});
		component.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent event)
			{
				check.run();
			}

			@Override
			public void componentShown(ComponentEvent event)
			{
				check.run();
			}
		});
		SwingUtilities.invokeLater(check);
	}

	public static boolean isVisible(JComponent component)
	{
		if (component == null || !component.isShowing())
		{
			return false;
		}
		JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, component);
		if (viewport == null)
		{
			return true;
		}
		Rectangle local = new Rectangle(component.getWidth(), component.getHeight());
		Rectangle inView = SwingUtilities.convertRectangle(component, local, viewport.getView());
		return ViewportVisibility.intersectsView(viewport.getViewRect(), inView);
	}

	private static void syncViewport(JComponent component, ChangeListener listener)
	{
		JViewport next = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, component);
		Object previous = component.getClientProperty("slayerAtlasViewport");
		if (previous instanceof JViewport && previous != next)
		{
			((JViewport) previous).removeChangeListener(listener);
		}
		if (next != null && previous != next)
		{
			next.addChangeListener(listener);
			component.putClientProperty("slayerAtlasViewport", next);
		}
	}
}
