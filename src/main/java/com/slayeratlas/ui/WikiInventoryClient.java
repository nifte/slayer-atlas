package com.slayeratlas.ui;

import com.google.inject.ImplementedBy;
import com.slayeratlas.data.GearItem;
import java.util.List;
import java.util.function.Consumer;

@ImplementedBy(WikiPageInventoryClient.class)
public interface WikiInventoryClient
{
	void load(String pageName, Consumer<List<GearItem>> onLoaded);

	static WikiInventoryClient none()
	{
		return (pageName, onLoaded) -> onLoaded.accept(List.of());
	}
}
