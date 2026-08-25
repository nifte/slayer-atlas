package com.slayeratlas.data;

public final class GearItem
{
	private final String name;
	private final String imageFile;

	public GearItem(String name, String imageFile)
	{
		this.name = name;
		this.imageFile = imageFile;
	}

	public String getName()
	{
		return name;
	}

	public String getImageFile()
	{
		return imageFile;
	}

	public static GearItem named(String name)
	{
		if (name == null || name.isEmpty())
		{
			return null;
		}
		return new GearItem(name, name + ".png");
	}
}
