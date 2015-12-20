package ru.serjik.hexshaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.serjik.engine.EngineView;
import ru.serjik.engine.gles20.EngineView20;
import ru.serjik.wallpaper.WallpaperOffsetsListener;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

public class ActivityShaders extends Activity
{
	protected static final int REQUEST_SET_LIVE_WALLPAPER = 20000;
	private RelativeLayout layoutContainer;
	private EngineView viewRenderer;

	private Spinner spinnerShader;
	private SeekBar seekDetailLevel;
	private SeekBar seekTimeScale;

	private ShaderConfig cfg;
	private List<String> shaders;

	private float startX;
	private float offset = 0;

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != 0)
		{
			finish();
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
			WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

			try
			{
				wallpaperManager.clear();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			try
			{
				String packageName = LiveWallpaper.class.getPackage().getName();
				String className = LiveWallpaper.class.getCanonicalName();
				ComponentName component = new ComponentName(packageName, className);
				Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
				intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, component);
				startActivityForResult(intent, REQUEST_SET_LIVE_WALLPAPER);
			}
			catch (ActivityNotFoundException e3)
			{
				try
				{
					Intent intent = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
					finish();
					startActivity(intent);
				}
				catch (ActivityNotFoundException e2)
				{
					try
					{
						Intent intent = new Intent();
						intent.setAction("com.bn.nook.CHANGE_WALLPAPER");
						finish();
						startActivity(intent);
					}
					catch (ActivityNotFoundException e)
					{
						Toast.makeText(getBaseContext(), R.string.app_name, Toast.LENGTH_LONG).show();
					}
				}
			}
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

		layoutContainer = (RelativeLayout) findViewById(R.id.layout_container);

		findViewById(R.id.button_set_wallpaper).setOnClickListener(onClickListener);
	}

	private void onUpdateSettings()
	{
		cfg.shaderName((String) spinnerShader.getSelectedItem());
		cfg.detailLevel(seekDetailLevel.getProgress());
		cfg.timeScale(seekTimeScale.getProgress());
		removeView();
		addView();
	}

	private void addView()
	{
		viewRenderer = new EngineView20(this, (Renderer) (wallpaperOffsetsListener = new HexRenderer(getAssets(), cfg.shaderName(),
				cfg.detailLevel() * 16 + 16, cfg.timeScale() * 0.2f + 0.2f)));
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

	private WallpaperOffsetsListener wallpaperOffsetsListener;

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			startX = event.getX();
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP)
		{
			float dx = (event.getX() - startX);
			startX = event.getX();

			offset -= dx / (float) findViewById(R.id.layout_container).getWidth();

			if (offset < -0.5f)
			{
				offset = -0.5f;
			}
			if (offset > 0.5f)
			{
				offset = 0.5f;
			}
			wallpaperOffsetsListener.onOffsetChanged(offset, 0);
		}

		return super.onTouchEvent(event);
	}

}
