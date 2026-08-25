package com.slayeratlas.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import net.runelite.client.ui.components.IconTextField;

public final class SearchFieldSupport
{
	public static final String UP_ACTION = "search-preview-up";
	public static final String DOWN_ACTION = "search-preview-down";
	public static final String ESCAPE_ACTION = "search-clear";

	private SearchFieldSupport()
	{
	}

	public static void configure(IconTextField searchBar, String placeholder)
	{
		JTextField input = findTextField(searchBar);
		if (input != null)
		{
			input.putClientProperty("JTextField.placeholderText", placeholder);
			input.setFont(PanelFonts.body());
			silenceEmptyBackspace(input);
			bindKey(input, KeyEvent.VK_ESCAPE, ESCAPE_ACTION, () -> input.setText(""));
		}
		searchBar.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent event)
			{
				if (event.getKeyCode() != KeyEvent.VK_BACK_SPACE)
				{
					return;
				}
				String text = searchBar.getText();
				if (text == null || text.isEmpty())
				{
					event.consume();
				}
			}
		});
	}

	public static void bindArrows(IconTextField searchBar, Runnable onUp, Runnable onDown)
	{
		JTextField input = findTextField(searchBar);
		if (input == null)
		{
			return;
		}
		bindKey(input, KeyEvent.VK_UP, UP_ACTION, onUp);
		bindKey(input, KeyEvent.VK_DOWN, DOWN_ACTION, onDown);
	}

	private static void bindKey(JTextField input, int keyCode, String action, Runnable onRun)
	{
		InputMap keys = input.getInputMap(JComponent.WHEN_FOCUSED);
		keys.put(KeyStroke.getKeyStroke(keyCode, 0), action);
		input.getActionMap().put(action, runnableAction(onRun));
	}

	public static JTextField findTextField(Container parent)
	{
		for (Component child : parent.getComponents())
		{
			if (child instanceof JTextField)
			{
				return (JTextField) child;
			}
			if (child instanceof Container)
			{
				JTextField nested = findTextField((Container) child);
				if (nested != null)
				{
					return nested;
				}
			}
		}
		return null;
	}

	private static Action runnableAction(Runnable onRun)
	{
		return new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				onRun.run();
			}
		};
	}

	private static void silenceEmptyBackspace(JTextField input)
	{
		ActionMap actions = input.getActionMap();
		Object key = DefaultEditorKit.deletePrevCharAction;
		Action original = actions.get(key);
		if (original == null)
		{
			return;
		}
		actions.put(key, new TextAction(DefaultEditorKit.deletePrevCharAction)
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				JTextComponent field = getTextComponent(event);
				if (field == null)
				{
					return;
				}
				if (field.getCaretPosition() > 0 || field.getSelectionStart() != field.getSelectionEnd())
				{
					original.actionPerformed(event);
				}
			}
		});
	}
}
