#version 150

#moj_import <fog.glsl>
#moj_import <light.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;
in ivec2 texCoord2;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec4 colour = texture(Sampler0, texCoord0) * ColorModulator;
    colour = vec4(colour.rgb * MINECRAFT_AMBIENT_LIGHT, colour.a); //TODO: Actually apply given lighting
    //colour = minecraft_sample_lightmap(Sampler0, texCoord2);
    fragColor = linear_fog(colour, vertexDistance, FogStart, FogEnd, FogColor);
}
