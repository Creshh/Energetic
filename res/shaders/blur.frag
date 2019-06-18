uniform sampler2D texture;

void main()
{

    vec4 color = texture2D(texture, gl_TexCoord[0]);

    int i;
    int j;
    vec4 sum = vec4(0);
    for(i = -5; i <= 5; i++){
        for(j = -5; j <= 5; j++){
            vec2 offset = vec2(i,j) * 0.0005;
            sum += texture2D(texture, gl_TexCoord[0] + offset);
        }
    }

    gl_FragColor = (sum / 100) + color;
}