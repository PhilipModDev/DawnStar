#version 430

layout(location = 0) in vec3 a_position;
layout(location = 1) in vec2 a_texCoord0;
layout(location = 2) in float a_normal;

out vec2 texels;
out float light;

uniform mat4 combined;

void main(void) {
    texels = a_texCoord0;
    light = a_normal;
    gl_Position = combined * vec4(a_position,1.0);
}