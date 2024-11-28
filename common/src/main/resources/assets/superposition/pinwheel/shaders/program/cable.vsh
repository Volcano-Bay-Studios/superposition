#include veil:light
#include veil:fog
#include veil:camera

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler2;

uniform mat4 RenderModelViewMat;
uniform mat4 RenderProjMat;
uniform mat3 NormalMat;
uniform vec3 ChunkOffset;
uniform int FogShape;

out float vertexDistance;
out vec4 vertexColor;
out vec3 lightmapColor;
out vec2 texCoord0;
out vec2 texCoord2;
out vec3 normal;

// TODO use Veil block light API
float getBrightness() {
    float darkFromD = pow(clamp(-Normal.y, 0.0, 1.0), 3) * 0.5;
    float darkFromU = pow(clamp(Normal.y, 0.0, 1.0), 3) * 0.0;
    float darkFromN = pow(clamp(-Normal.z, 0.0, 1.0), 2) * 0.2;
    float darkFromS = pow(clamp(Normal.z, 0.0, 1.0), 2) * 0.2;
    float darkFromW = pow(clamp(-Normal.x, 0.0, 1.0), 2) * 0.4;
    float darkFromE = pow(clamp(Normal.x, 0.0, 1.0), 2) * 0.4;

    return 1.0 - (darkFromD + darkFromU + darkFromN + darkFromS + darkFromW + darkFromE);
}

void main() {
    vec3 pos = Position + ChunkOffset;
    gl_Position = RenderProjMat * RenderModelViewMat * vec4(pos, 1.0);

    vertexDistance = fog_distance(pos, FogShape);
    vertexColor = Color * getBrightness();
    lightmapColor = minecraft_sample_lightmap(Sampler2, UV2).rgb;
    texCoord0 = UV0;
    texCoord2 = minecraft_sample_lightmap_coords(UV2);
    normal = NormalMat * Normal;
}
