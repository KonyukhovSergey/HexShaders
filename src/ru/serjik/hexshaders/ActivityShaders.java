package ru.serjik.hexshaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.serjik.engine.EngineView;
import ru.serjik.engine.gles20.EngineView20;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;

public class ActivityShaders extends Activity
{
	private RelativeLayout layoutContainer;
	private EngineView viewRenderer;

	private Spinner spinnerShader;
	private SeekBar seekDetailLevel;
	private SeekBar seekTimeScale;
	private SeekBar seekFadeLevel;

	private ShaderConfig cfg;
	private List<String> shaders;

	private OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
		{
			onUpdateSettings();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent)
		{
			// TODO Auto-generated method stub

		}
	};

	private OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener()
	{
		@Override
		public void onStopTrackingTouch(SeekBar seekBar)
		{
			onUpdateSettings();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar)
		{
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
		{
		}
	};

	private OnClickListener onClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			WallpaperManager wm = WallpaperManager.getInstance(getApplicationContext());

			try
			{
				wm.clear();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Intent intent = new Intent();
			intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);

			String packageName = LiveWallpaper.class.getPackage().getName();
			String className = LiveWallpaper.class.getCanonicalName();
			intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(packageName, className));

			startActivity(intent);
			finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		cfg = new ShaderConfig(this);

		setContentView(R.layout.activity_shaders);

		spinnerShader = (Spinner) findViewById(R.id.spinner_shader);
		shaders = getShaders();
		spinnerShader.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, shaders));

		for (int i = 0; i < shaders.size(); i++)
		{
			if (cfg.shaderName().equals(shaders.get(i)))
			{
				spinnerShader.setSelection(i);

			}
		}

		spinnerShader.setOnItemSelectedListener(onItemSelectedListener);

		seekDetailLevel = (SeekBar) findViewById(R.id.seek_detail_level);
		seekDetailLevel.setProgress(cfg.detailLevel());
		seekDetailLevel.setOnSeekBarChangeListener(onSeekBarChangeListener);
		seekTimeScale = (SeekBar) findViewById(R.id.seek_time_scale);
		seekTimeScale.setProgress(cfg.timeScale());
		seekTimeScale.setOnSeekBarChangeListener(onSeekBarChangeListener);
		seekFadeLevel = (SeekBar) findViewById(R.id.seek_fade_level);
		seekFadeLevel.setProgress(cfg.fadeLevel());
		seekFadeLevel.setOnSeekBarChangeListener(onSeekBarChangeListener);

		layoutContainer = (RelativeLayout) findViewById(R.id.layout_container);

		findViewById(R.id.button_set_wallpaper).setOnClickListener(onClickListener);
	}

	private void onUpdateSettings()
	{
		cfg.shaderName((String) spinnerShader.getSelectedItem());
		cfg.detailLevel(seekDetailLevel.getProgress());
		cfg.fadeLevel(seekFadeLevel.getProgress());
		cfg.timeScale(seekTimeScale.getProgress());
		removeView();
		addView();
	}

	private void addView()
	{
		viewRenderer = new EngineView20(this, new HexRenderer(getAssets(), cfg.shaderName(),
				cfg.detailLevel() * 16 + 16, cfg.timeScale() * 0.2f + 0.2f, cfg.fadeLevel() * 0.1f + 0.2f));
		layoutContainer.addView(viewRenderer, 0);
		viewRenderer.onResume();
	}

	private void removeView()
	{
		viewRenderer.onPause();
		layoutContainer.removeView(viewRenderer);
	}

	private List<String> getShaders()
	{
		List<String> shaders = new ArrayList<String>();

		try
		{
			for (String assetName : getAssets().list(""))
			{
				if (assetName.endsWith(".sp"))
				{
					shaders.add(assetName);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return shaders;
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		addView();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		removeView();
	}

}
