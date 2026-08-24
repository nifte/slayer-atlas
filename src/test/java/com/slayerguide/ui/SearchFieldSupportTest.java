package com.slayerguide.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.text.DefaultEditorKit;
import net.runelite.client.ui.components.IconTextField;
import org.junit.Test;

public class SearchFieldSupportTest
{
	@Test
	public void setsPlaceholderOnInnerTextField()
	{
		IconTextField searchBar = new IconTextField();
		SearchFieldSupport.configure(searchBar, "Search Tasks");
		JTextField input = SearchFieldSupport.findTextField(searchBar);
		assertNotNull(input);
		assertEquals("Search Tasks", input.getClientProperty("JTextField.placeholderText"));
	}

	@Test
	public void emptyBackspaceDoesNotChangeText()
	{
		IconTextField searchBar = new IconTextField();
		SearchFieldSupport.configure(searchBar, "Search Tasks");
		JTextField input = SearchFieldSupport.findTextField(searchBar);
		assertNotNull(input);
		input.setText("");
		input.getActionMap()
			.get(DefaultEditorKit.deletePrevCharAction)
			.actionPerformed(new ActionEvent(input, ActionEvent.ACTION_PERFORMED, DefaultEditorKit.deletePrevCharAction));
		assertEquals("", input.getText());
	}
}
