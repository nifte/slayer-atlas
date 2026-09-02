package com.slayeratlas.ui;

final class WikiParenFileName
{
	private WikiParenFileName()
	{
	}

	static String alternate(String fileName)
	{
		if (fileName == null || fileName.isEmpty())
		{
			return "";
		}
		if (fileName.contains(" ("))
		{
			String tight = fileName.replaceAll(" \\((?!\\d+\\))", "(");
			return tight.equals(fileName) ? "" : tight;
		}
		String spaced = fileName.replaceAll("(?<!\\s)\\((?!\\d+\\))", " (");
		return spaced.equals(fileName) ? "" : spaced;
	}
}
