#include veil:space_helper

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;

in vec2 texCoord;

out vec4 fragColor;


void main() {
    vec4 source = texture(DiffuseSampler0,texCoord.xy);
//    float depth = distance(vec3(0,0,0),screenToLocalSpace(texCoord,texture(DiffuseDepthSampler,texCoord).r).rgb/10);
    fragColor = source;
}