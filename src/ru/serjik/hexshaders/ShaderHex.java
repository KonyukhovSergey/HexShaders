package ru.serjik.hexshaders;

import java.nio.FloatBuffer;

import ru.serjik.engine.gles20.ShaderProgram;
import ru.serjik.utils.AssetsUtils;
import android.content.res.AssetManager;
import android.opengl.GLES20;

public class ShaderHex extends ShaderProgram
{
	public int u_size;
	public int u_time;
	public int u_offset;
	public int u_res;
	public int u_texture;
	public int a_pos;
	public int a_col;

	public ShaderHex(AssetManager am, String shaderName)
	{
		super(AssetsUtils.readText(shaderName, am));
		u_size = getUniformLocation("u_size");
		u_time = getUniformLocation("iGlobalTime");
		u_offset = getUniformLocation("u_offset");
		u_res = getUniformLocation("iResolution");
		u_texture = getUniformLocation("u_texture");
		a_pos = getAttribLocation("a_pos");
		a_col = getAttribLocation("a_col");
		releaseCompiler();
	}

	public void setupUniforms(float pointSizeInPixels, float time, int texture, float width, float height, float offset)
	{
		GLES20.glUniform1f(u_size, pointSizeInPixels);
		GLES20.glUniform1f(u_time, time);
		GLES20.glUniform1f(u_offset, offset);
		GLES20.glUniform1i(u_texture, texture);
		GLES20.glUniform2f(u_res, width, height);
	}

	@Override
	public void setupAttribPointers(FloatBuffer fb)
	{
		fb.position(0);
		GLES20.glVertexAttribPointer(a_pos, 2, GLES20.GL_FLOAT, false, 12, fb);
		GLES20.glEnableVertexAttribArray(a_pos);

		fb.position(2);
		GLES20.glVertexAttribPointer(a_col, 4, GLES20.GL_UNSIGNED_BYTE, true, 12, fb);
		GLES20.glEnableVertexAttribArray(a_col);
	}

	public void draw(int elements)
	{
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, elements);
	}
}
