#version 150

in vec4 vertexColor;
in vec3 FragPos;

uniform vec2 Screen;
uniform vec4 ColorModulator;
uniform vec2 BoxMin;
uniform vec2 BoxMax;
uniform sampler2D LineTexture;
uniform sampler2D Sampler0;

out vec4 fragColor;
float sdSegment(vec2 p, vec2 a, vec2 b) {
    vec2 pa = p - a, ba = b - a;
    float h = clamp(dot(pa, ba) / max(dot(ba, ba), 0.0001), 0.0, 1.0);
    return length(pa - ba * h);
}

vec2 getBezier(float t, vec2 p0, vec2 p1, vec2 p2, vec2 p3) {
    float u = 1.0 - t;
    return u*u*u*p0 + 3.0*u*u*t*p1 + 3.0*u*t*t*p2 + t*t*t*p3;
}

void main() {
    vec2 pixelPos = gl_FragCoord.xy;

    vec2 pStart = BoxMin * Screen;
    vec2 pEnd   = BoxMax * Screen;

    float easeWidth = 0.45 * (pEnd.x - pStart.x);
    vec2 cp1 = pStart + vec2(easeWidth, 0.0);
    vec2 cp2 = pEnd - vec2(easeWidth, 0.0);


    float minDist = 1e10;
    float segmentLengthSum = 0.0;
    float finalArcPos = 0.0;
    const int samples = 64;

    vec2 lastP = pStart;
    for(int i = 1; i <= samples; i++) {
        float t = float(i) / float(samples);
        vec2 currentP = getBezier(t, pStart, cp1, cp2, pEnd);
        float segLen = length(currentP - lastP);

        vec2 pa = FragPos.xy - lastP, ba = currentP - lastP;
        float h = clamp(dot(pa, ba) / max(dot(ba, ba), 0.0001), 0.0, 1.0);
        float d = length(pa - ba * h);

        if (d < minDist) {
            minDist = d;
            finalArcPos = segmentLengthSum + (h * segLen);
        }

        segmentLengthSum += segLen;
        lastP = currentP;
    }

    if (smoothstep(8.0, 6.5, minDist) > 0.5) {
        float distFromEnd = segmentLengthSum - finalArcPos;
        float u = distFromEnd / 13;
        float v = (minDist / 8.0) * 0.5 + 0.5;

        vec4 texColor = texture(Sampler0, vec2(u, v));
        fragColor = texColor * vertexColor;
    } else {
        discard;
    }
}