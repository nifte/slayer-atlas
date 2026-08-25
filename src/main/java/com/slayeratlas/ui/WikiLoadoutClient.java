package com.slayeratlas.ui;

import com.google.inject.ImplementedBy;
import com.slayeratlas.data.GearLoadout;
import com.slayeratlas.data.SlayerMonster;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@ImplementedBy(WikiBucketLoadoutClient.class)
public interface WikiLoadoutClient
{
	void load(SlayerMonster monster, Consumer<List<GearLoadout>> onLoaded);

	static WikiLoadoutClient none()
	{
		return (monster, onLoaded) -> onLoaded.accept(Collections.emptyList());
	}
}
