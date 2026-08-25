package com.slayeratlas.ui;

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

	@Test
	public void bindArrowsInvokesUpAndDownActions()
	{
		IconTextField searchBar = new IconTextField();
		SearchFieldSupport.configure(searchBar, "Search Tasks");
		int[] moves = {0};
		SearchFieldSupport.bindArrows(searchBar, () -> moves[0]--, () -> moves[0]++);
		JTextField input = SearchFieldSupport.findTextField(searchBar);
		assertNotNull(input);

		input.getActionMap()
			.get(SearchFieldSupport.DOWN_ACTION)
			.actionPerformed(new ActionEvent(input, ActionEvent.ACTION_PERFORMED, SearchFieldSupport.DOWN_ACTION));
		assertEquals(1, moves[0]);

		input.getActionMap()
			.get(SearchFieldSupport.UP_ACTION)
			.actionPerformed(new ActionEvent(input, ActionEvent.ACTION_PERFORMED, SearchFieldSupport.UP_ACTION));
		assertEquals(0, moves[0]);
	}

	@Test
	public void escapeClearsTheSearchText()
	{
		IconTextField searchBar = new IconTextField();
		SearchFieldSupport.configure(searchBar, "Search Tasks");
		JTextField input = SearchFieldSupport.findTextField(searchBar);
		assertNotNull(input);
		input.setText("dust");

		input.getActionMap()
			.get(SearchFieldSupport.ESCAPE_ACTION)
			.actionPerformed(new ActionEvent(input, ActionEvent.ACTION_PERFORMED, SearchFieldSupport.ESCAPE_ACTION));

		assertEquals("", input.getText());
	}
}
