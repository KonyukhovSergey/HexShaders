package ru.serjik.hexshaders;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ru.serjik.engine.gles20.Texture;
import ru.serjik.engine.utils.BufferAllocator;
import ru.serjik.engine.utils.ColorTools;
import ru.serjik.utils.AssetsUtils;
import ru.serjik.utils.FrameRateCalculator;
import ru.serjik.utils.FrameRateCalculator.FrameRateUpdater;
import ru.serjik.utils.HexUtils;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;
import android.util.Log;

public class HexRenderer implements Renderer, FrameRateUpdater
{
	private float pointsInTheRow;
	private AssetManager am;
	private String shaderName;
	private FrameRateCalculator frc = new FrameRateCalculator(this);

	private Texture texture;
	private ShaderHex shaderHex;

	private float scaleX;
	private float scaleY;
	private float width, height;
	private float pointSize;
	private float pointPixelsSize;
	private int pointsCount = 0;
	private FloatBuffer fb;

	private static float time = 0;
	private long prevClock = SystemClock.elapsedRealtime();
	private float timeScale;
	public float offset = 0;

	public HexRenderer(AssetManager am, String shaderName, float pointsInTheRow, float timeScale)
	{
		this.am = am;
		this.shaderName = shaderName;
		this.timeScale = timeScale;
		this.pointsInTheRow = pointsInTheRow;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		texture = new Texture(AssetsUtils.loadBitmap("hex.png", am));
		shaderHex = new ShaderHex(am, shaderName);
		gl.glClearColor(0, 0, 0, 0);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		gl.glViewport(0, 0, width, height);

		pointSize = 2.0f / pointsInTheRow;

		if (width < height)
		{
			scaleX = 1.0f;
			scaleY = (float) width / (float) height;
			pointPixelsSize = 0.5f * (float) height / pointsInTheRow;
		}
		else
		{
			scaleX = (float) height / (float) width;
			scaleY = 1.0f;
			pointPixelsSize = 0.5f * (float) width / pointsInTheRow;
		}

		this.width = width;
		this.height = height;

		pointsCount = 0;

		int limitWidth = (int) (0.6f * (float) width / pointPixelsSize);
		int limitHeigth = (int) (0.6f * (float) height / pointPixelsSize);

		fb = BufferAllocator.createFloatBuffer(3 * (limitHeigth * 2 + 1) * (limitWidth * 2 + 1));
		fb.position(0);

		for (int r = -limitHeigth; r <= limitHeigth; r++)
		{
			for (int q = -limitWidth - r / 2; q <= limitWidth - r / 2; q++)
			{
				float x = scaleX * HexUtils.x(q, r) * pointSize;
				float y = scaleY * HexUtils.y(r) * pointSize;
				if (Math.abs(x) <= 1.0f + pointSize && Math.abs(y) <= 1.0f + pointSize)
				{
					fb.put(x);
					fb.put(y);
					fb.put(ColorTools.WHITE_XFFFF);
					pointsCount++;
				}
			}
		}

		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		texture.bind();
		Texture.filter(GLES20.GL_LINEAR, GLES20.GL_LINEAR);
		Texture.wrap(GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		frc.frameBegin();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		shaderHex.use();
		time += (float) (SystemClock.elapsedRealtime() - prevClock) * 0.001f * timeScale;
		if (time > 600)
		{
			time = 0;
		}
		prevClock = SystemClock.elapsedRealtime();
		shaderHex.setupUniforms(pointPixelsSize, time, 0, width, height, offset * scaleX);
		shaderHex.setupAttribPointers(fb);

		shaderHex.draw(pointsCount);

		frc.frameDone();
	}

	@Override
	public void onFrameRateUpdate(FrameRateCalculator frameRateCalculator)
	{
//		Log.i("fps", frameRateCalculator.frameString());
	}

}
