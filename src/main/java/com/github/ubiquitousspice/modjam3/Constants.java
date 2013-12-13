package com.github.ubiquitousspice.modjam3;

import com.google.common.base.Throwables;

import java.io.InputStream;
import java.util.Properties;

public class Constants
{
	public static final String MODID = "@MODID@";

	static
	{

		Properties prop = new Properties();

		try
		{
			InputStream stream = Constants.class.getClassLoader().getResourceAsStream("version.properties");
			prop.load(stream);
			stream.close();
		}
		catch (Exception e)
		{
			System.out.print("Error loading version for " + MODID);
			Throwables.propagate(e);
		}

		VERSION = prop.getProperty("version");
	}

	public static final String VERSION;
}
