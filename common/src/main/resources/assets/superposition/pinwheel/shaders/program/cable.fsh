#include veil:fog

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
// #veil:light_color
in vec3 lightmapColor;
in vec2 texCoord0;
// #veil:light_uv
in vec2 texCoord2;
// #veil:normal
in vec3 normal;

out vec4 fragColor;

void main() {
    // #veil:albedo
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    color.rgb *= vec3(1,0,1);
    fragColor = linear_fog(color * vec4(lightmapColor, 1.0), vertexDistance, FogStart, FogEnd, FogColor);
}

