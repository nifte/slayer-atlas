package com.slayeratlas.data;

public final class WikiEquipmentRow
{
	private final String pageName;
	private final String json;

	public WikiEquipmentRow(String pageName, String json)
	{
		this.pageName = pageName;
		this.json = json;
	}

	public String getPageName()
	{
		return pageName;
	}

	public String getJson()
	{
		return json;
	}
}
