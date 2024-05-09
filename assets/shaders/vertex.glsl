#version 430

layout(location = 0) in float data;
layout(location = 1) in vec2 a_texCoord0;
layout(location = 2) in float a_normal;

out vec2 texels;
out float light;

uniform mat4 combined;
uniform vec3 chunkPos;

void main(void) {
    //Sets light level.
    light = a_normal;
    //Sets the texture.
    texels = a_texCoord0;
    //Converts the float bits to int bits.
    int packedData = floatBitsToInt(data);
    //Sets the position.
    vec4 worldPos = vec4(chunkPos,1.0) + vec4(
    (packedData & 1023),
    (packedData >> 10) & 1023,
    (packedData >> 20) & 1023,
    1.0);

    gl_Position = combined * worldPos;
}