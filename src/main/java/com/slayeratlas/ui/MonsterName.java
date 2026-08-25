package com.slayeratlas.ui;

public final class MonsterName
{
	private MonsterName()
	{
	}

	public static String display(String name)
	{
		if (name == null || name.isEmpty())
		{
			return "";
		}
		StringBuilder builder = new StringBuilder(name.length());
		boolean capitalize = true;
		for (int index = 0; index < name.length(); index++)
		{
			char letter = name.charAt(index);
			if (capitalize && Character.isLetter(letter))
			{
				builder.append(Character.toUpperCase(letter));
				capitalize = false;
				continue;
			}
			builder.append(letter);
			capitalize = !Character.isLetter(letter) && letter != '\'';
		}
		return builder.toString();
	}
}
