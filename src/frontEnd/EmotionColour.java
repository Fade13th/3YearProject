package frontEnd;

/**
 * Created by matt on 12/03/17.
 */
public class EmotionColour {
    public class Colour {
        public float R;
        public float G;
        public float B;

        public Colour(float R, float G, float B) {
            this.R = R;
            this.G = G;
            this.B = B;
        }
    }

    public Colour bgColour;
    public Colour fgColour;

    public EmotionColour(float valence, float arousal) {
        int red   = Math.round(arousal > 0 ? (256 * arousal) : Math.max((-256 * arousal) - (256 * Math.abs(valence)), 0));
        int green = Math.round(Math.max((200 * valence) + (56 * arousal), 0));
        int blue  = Math.round(Math.max((128 * valence) - (128 * arousal), 0));

        if (valence < 0 && arousal < 0) {
            int grey = Math.round(40-(40 * (Math.abs(valence) + Math.abs(arousal)/2)));

            red += grey;
            green += grey;
            blue += grey;
        }

        red *= (arousal+1)/2;
        green += (arousal+1)/2;
        blue *= (arousal+1)/2;

        fgColour = new Colour(red/255.0f, green/255.0f, blue/255.0f);
        bgColour = new Colour(Math.min(128, Math.max(0, red-(20*(arousal+1))))/255.0f, Math.min(128, Math.max(0, green-(20*(arousal+1))))/255.0f, Math.min(128, Math.max(0, blue-(20*(arousal+1))))/255.0f);
    }
}
