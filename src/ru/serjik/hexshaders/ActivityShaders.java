package ru.serjik.hexshaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.serjik.engine.EngineView;
import ru.serjik.engine.gles20.EngineView20;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ActivityShaders extends Activity
{
	private RelativeLayout layoutContainer;
	private EngineView viewRenderer;
	private ListView listViewShaders;
	private List<String> shaders;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shaders);

		shaders = getShaders();

		listViewShaders = (ListView) findViewById(R.id.list_shaders);
		listViewShaders.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, shaders));
		listViewShaders.setOnItemClickListener(onItemClickListener);

		layoutContainer = (RelativeLayout) findViewById(R.id.layout_container);
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			viewRenderer = new EngineView20(ActivityShaders.this, new HexRenderer(getAssets(), shaders.get(position)));
			layoutContainer.addView(viewRenderer);
			viewRenderer.onResume();
		}
	};

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
		if (viewRenderer != null)
		{
			viewRenderer.onResume();
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (viewRenderer != null)
		{
			viewRenderer.onPause();
		}
	}

	@Override
	public void onBackPressed()
	{
		if (viewRenderer != null)
		{
			//viewRenderer.onPause();
			layoutContainer.removeView(viewRenderer);
			viewRenderer = null;
			return;
		}
		super.onBackPressed();
	}
}
