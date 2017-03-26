package frontEnd;

/**
 * Created by matt on 26/03/17.
 */
public class Colour {
    public float R;
    public float G;
    public float B;
    public float alpha = 1;

    public Colour(float R, float G, float B) {
        this.R = Math.max(R, 0);
        this.G = Math.max(G, 0);
        this.B = Math.max(B, 0);
    }
}
