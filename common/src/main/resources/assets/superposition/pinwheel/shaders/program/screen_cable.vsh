#version 150

in vec3 Position;
in vec4 Color;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec2 Screen;

out vec3 FragPos;
out vec4 vertexColor;

void main() {
    FragPos = vec3(ModelViewMat * vec4(Position, 1.0));
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vertexColor = Color;
}
