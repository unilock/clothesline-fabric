#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec4 colour = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (colour.a < 0.1) {
        discard; //Don't think we'd ever end up translucent, but shouldn't hurt
    }
    fragColor = linear_fog(colour, vertexDistance, FogStart, FogEnd, FogColor);
}
