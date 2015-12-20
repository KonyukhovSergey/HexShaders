package ru.serjik.hexshaders;

import ru.serjik.wallpaper.GLWallpaperService;
import ru.serjik.wallpaper.WallpaperOffsetsListener;
import android.opengl.GLSurfaceView;

public class LiveWallpaper extends GLWallpaperService
{
	private HexRenderer renderer;

	@Override
	public WallpaperOffsetsListener onRendererAcquire(GLSurfaceView view)
	{
		ShaderConfig cfg = new ShaderConfig(this);
		view.setEGLContextClientVersion(2);
		renderer = new HexRenderer(getAssets(), cfg.shaderName(), cfg.detailLevel() * 16 + 16,
				cfg.timeScale() * 0.2f + 0.2f);
		view.setRenderer(renderer);
		return renderer;
	}

}
