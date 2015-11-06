package ru.serjik.hexshaders;

import android.content.Context;
import android.content.SharedPreferences;

public class ShaderConfig
{
	private static final String TIME_SCALE = "time_scale";
	private static final String DETAIL_LEVEL = "detail_level";
	private static final String FADE_LEVEL = "fade_level";
	private static final String SHADER_NAME = "shader_index";
	private SharedPreferences prefs;

	public ShaderConfig(Context context)
	{
		prefs = context.getSharedPreferences("hswp", Context.MODE_PRIVATE);
	}

	public String shaderName()
	{
		return prefs.getString(SHADER_NAME, "03 Rainbow.sp");
	}

	public void shaderName(String value)
	{
		prefs.edit().putString(SHADER_NAME, value).commit();
	}

	public int detailLevel()
	{
		return prefs.getInt(DETAIL_LEVEL, 4);
	}

	public void detailLevel(int value)
	{
		prefs.edit().putInt(DETAIL_LEVEL, value).commit();
	}

	public int fadeLevel()
	{
		return prefs.getInt(FADE_LEVEL, 4);
	}

	public void fadeLevel(int value)
	{
		prefs.edit().putInt(FADE_LEVEL, value).commit();
	}

	public int timeScale()
	{
		return prefs.getInt(TIME_SCALE, 4);
	}

	public void timeScale(int value)
	{
		prefs.edit().putInt(TIME_SCALE, value).commit();
	}
}
