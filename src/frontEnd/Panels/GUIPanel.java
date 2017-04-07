package frontEnd.Panels;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import frontEnd.Colour;
import frontEnd.EmotionColour;

import java.util.ArrayList;

/**
 * Created by matt on 15/03/17.
 */
public abstract class GUIPanel {
    public static EmotionColour targetColour;
    protected static float blendStep = 0.005f;

    protected static ArrayList<ArrayList<Float>> previousValues;
    protected static ArrayList<EmotionColour> previousColours;
    protected static final int previous = 5;

    public static ArrayList<Float> values;
    public static EmotionColour colour;

    public static void setup(GL2 gl2, int width, int height) {
        previousColours = new ArrayList<>();
        previousValues = new ArrayList<>();

        gl2.glMatrixMode( GL2.GL_PROJECTION );
        gl2.glLoadIdentity();

        // coordinate system origin at lower left with width and height same as the window
        GLU glu = new GLU();
        glu.gluOrtho2D( 0.0f, width, 0.0f, height );

        gl2.glMatrixMode( GL2.GL_MODELVIEW );
        gl2.glLoadIdentity();

        gl2.glViewport( 0, 0, width, height );
    }

    public static void clear(GL2 gl2,int screenWidth, int screenHeight) {
        Colour bg = averageBackground();

        gl2.glBegin(GL.GL_TRIANGLES);
        gl2.glColor4f(bg.R, bg.G, bg.B, 1.0f);

        gl2.glVertex2f(0,0);
        gl2.glVertex2f(0, screenHeight);
        gl2.glVertex2f(screenWidth, screenHeight);

        gl2.glVertex2f(screenWidth, screenHeight);
        gl2.glVertex2f(0, 0);
        gl2.glVertex2f(screenWidth, 0);

        gl2.glEnd();

        //gl2.glClearColor(bg.R, bg.G, bg.B, 1.0f);
        //gl2.glClear(GL.GL_COLOR_BUFFER_BIT);
    }

    public static void clearPrevious() {
        previousColours.clear();
        previousValues.clear();
    }

    public abstract void render( GL2 gl2, int screenWidth, int screenHeight);

    public static void renderPrevious(GL2 gl2, int screenWidth, int screenHeight, GUIPanel guiPanel) {
        for (int i = 0; i < previousColours.size(); i++) {
            guiPanel.values = previousValues.get(i);
            guiPanel.colour = previousColours.get(i);
            guiPanel.render(gl2, screenWidth, screenHeight);
        }
    }

    public static void updatePreviousValues(ArrayList<Float> nextValues) {
        if (previousValues.size() < previous) {
            previousValues.add(nextValues);
        }
        else  {
            previousValues.remove(0);
            previousValues.add(nextValues);
        }
    }

    public static void updatePreviousColours(EmotionColour nextColour) {
        if (previousColours.size() < previous) {
            previousColours.add(nextColour);
        }
        else  {
            previousColours.remove(0);
            previousColours.add(nextColour);
        }

        for (int i = 0; i < previousColours.size(); i++) {
            previousColours.get(i).bgColour.alpha = 0.0f;
            previousColours.get(i).fgColour.alpha = (float)(i)*0.1f;
        }
    }

    protected static EmotionColour blendColours() {
        float fg_rDiff = colour.fgColour.R - targetColour.fgColour.R;
        if (fg_rDiff > blendStep)
            colour.fgColour.R -= blendStep;
        else if (fg_rDiff < -blendStep)
            colour.fgColour.R += blendStep;
        else
            colour.fgColour.R = targetColour.fgColour.R;

        float fg_gDiff = colour.fgColour.G - targetColour.fgColour.G;
        if (fg_gDiff > blendStep)
            colour.fgColour.G -= blendStep;
        else if (fg_gDiff < -blendStep)
            colour.fgColour.G += blendStep;
        else
            colour.fgColour.G = targetColour.fgColour.G;

        float fg_bDiff = colour.fgColour.B - targetColour.fgColour.B;
        if (fg_bDiff > blendStep)
            colour.fgColour.B -= blendStep;
        else if (fg_bDiff < -blendStep)
            colour.fgColour.B += blendStep;
        else
            colour.fgColour.B = targetColour.fgColour.B;


        float bg_rDiff = colour.bgColour.R - targetColour.bgColour.R;
        if (bg_rDiff > blendStep)
            colour.bgColour.R -= blendStep;
        else if (bg_rDiff < -blendStep)
            colour.bgColour.R += blendStep;
        else
            colour.bgColour.R = targetColour.bgColour.R;

        float bg_gDiff = colour.bgColour.G - targetColour.bgColour.G;
        if (bg_gDiff > blendStep)
            colour.bgColour.G -= blendStep;
        else if (bg_gDiff < -blendStep)
            colour.bgColour.G += blendStep;
        else
            colour.bgColour.G = targetColour.bgColour.G;

        float bg_bDiff = colour.bgColour.B - targetColour.bgColour.B;
        if (bg_bDiff > blendStep)
            colour.bgColour.B -= blendStep;
        else if (bg_bDiff < -blendStep)
            colour.bgColour.B += blendStep;
        else
            colour.bgColour.B = targetColour.bgColour.B;

        return colour;
    }

    private static Colour averageBackground() {
        float r = 0;
        float g = 0;
        float b = 0;

        for (int i = 0; i < previousColours.size(); i++) {
            r += previousColours.get(i).bgColour.R;
            g += previousColours.get(i).bgColour.G;
            b += previousColours.get(i).bgColour.B;
        }

        return new Colour(r/previousColours.size(), g/previousColours.size(), b/previousColours.size());
    }
}

