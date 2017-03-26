package frontEnd;

/**
 * Created by matt on 12/03/17.
 */
public class EmotionColour {
    public Colour bgColour;
    public Colour fgColour;

    public EmotionColour(float valence, float arousal) {
        Colour colour = ColourMapping.getColour(valence, arousal);

        fgColour = colour;
        bgColour = new Colour(Math.min(0.2f, Math.max(0, colour.R-(0.2f*(arousal+1)))),
                              Math.min(0.2f, Math.max(0, colour.G-(0.2f*(arousal+1)))),
                              Math.min(0.2f, Math.max(0, colour.B-(0.2f*(arousal+1)))));
    }
}
