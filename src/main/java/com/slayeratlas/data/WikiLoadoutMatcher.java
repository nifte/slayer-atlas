package com.slayeratlas.data;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class WikiLoadoutMatcher
{
	private WikiLoadoutMatcher()
	{
	}

	public static List<GearLoadout> match(Gson gson, SlayerMonster monster, List<WikiEquipmentRow> rows)
	{
		if (rows == null || rows.isEmpty() || monster == null)
		{
			return new ArrayList<>();
		}
		List<String> candidates = WikiPageNames.candidates(monster);
		Map<CombatStyle, WikiEquipmentTable> byStyle = new EnumMap<>(CombatStyle.class);
		for (WikiEquipmentRow row : rows)
		{
			if (row == null || !WikiPageNames.matches(row.getPageName(), candidates))
			{
				continue;
			}
			WikiEquipmentTable table = WikiEquipmentTable.parse(gson, row.getPageName(), row.getJson());
			if (table == null)
			{
				continue;
			}
			WikiEquipmentTable existing = byStyle.get(table.getStyle());
			if (existing == null || table.score() > existing.score())
			{
				byStyle.put(table.getStyle(), table);
			}
		}
		List<GearLoadout> matched = new ArrayList<>();
		for (CombatStyle style : CombatStyle.values())
		{
			WikiEquipmentTable table = byStyle.get(style);
			if (table != null)
			{
				matched.add(table.toLoadout());
			}
		}
		return matched;
	}
}
