package com.slayerguide.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JTextField;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import net.runelite.client.ui.components.IconTextField;

public final class SearchFieldSupport
{
	private SearchFieldSupport()
	{
	}

	public static void configure(IconTextField searchBar, String placeholder)
	{
		JTextField input = findTextField(searchBar);
		if (input != null)
		{
			input.putClientProperty("JTextField.placeholderText", placeholder);
			silenceEmptyBackspace(input);
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

	static JTextField findTextField(Container parent)
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
