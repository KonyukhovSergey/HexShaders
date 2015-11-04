// vertex shader

#ifdef GL_ES
precision mediump float;
#endif

uniform mediump float u_size;
uniform float iGlobalTime;
uniform mediump vec2 iResolution;

attribute mediump vec2 a_pos;
attribute lowp vec4 a_col;

varying lowp vec4 v_col;


#define MAX_ITER 10


void main()
{
	vec2 sp = a_pos;
	vec2 p = sp*8.0- vec2(20.0);
	vec2 i = p;
	float c = 1.0;
	float inten = .05;

	for (int n = 0; n < MAX_ITER; n++) 
	{
		float t = iGlobalTime * (1.0 - (3.0 / float(n+1)));
		i = p + vec2(cos(t - i.x) + sin(t + i.y), sin(t - i.y) + cos(t + i.x));
		c += 1.0/length(vec2(p.x / (sin(i.x+t)/inten),p.y / (cos(i.y+t)/inten)));
	}
	c /= float(MAX_ITER);
	c = 1.5-sqrt(c);
	v_col = vec4(vec3(c*c*c*c*c), 19.0) + vec4(0.0, 0.3, 0.5, 1.0);

	//v_col = vec4(color, 1.0);
	gl_PointSize = u_size;
	gl_Position =  vec4(a_pos.x, a_pos.y, 1.0, 1.0);
}

====
// fragment shader
uniform lowp sampler2D u_texture;
varying lowp vec4 v_col;

void main()
{
	gl_FragColor = v_col * texture2D(u_texture, gl_PointCoord);
}
