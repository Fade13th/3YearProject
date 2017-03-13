package frontEnd;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import java.util.ArrayList;

/**
 * Created by matt on 12/03/17.
 */
public class BarPanel {
    private static int bars = 12;

    protected static ArrayList<Float> values;

    protected static EmotionColour colour;
    protected static  EmotionColour targetColour;
    private static float blendStep = 0.005f;

    protected static void setup(GL2 gl2, int width, int height ) {
        gl2.glMatrixMode( GL2.GL_PROJECTION );
        gl2.glLoadIdentity();

        // coordinate system origin at lower left with width and height same as the window
        GLU glu = new GLU();
        glu.gluOrtho2D( 0.0f, width, 0.0f, height );

        gl2.glMatrixMode( GL2.GL_MODELVIEW );
        gl2.glLoadIdentity();

        gl2.glViewport( 0, 0, width, height );
    }
    protected static void setup(GL2 gl2, int width, int height, int b ) {
        bars = b;
        setup(gl2, width, height);
    }

    protected static void render( GL2 gl2, int screenWidth, int screenHeight) {
        blendColours();

        gl2.glClearColor(colour.bgColour.R, colour.bgColour.G, colour.bgColour.B, 1.0f);
        gl2.glClear(GL.GL_COLOR_BUFFER_BIT);

        float barWidth = screenWidth/12;

        // draw a triangle filling the window
        gl2.glLoadIdentity();
        for (int i = 0; i < values.size(); i++) {
            float height = values.get(i) * screenHeight;

            gl2.glBegin(GL.GL_TRIANGLES);

            gl2.glColor3f(colour.fgColour.R, colour.fgColour.G, colour.fgColour.B);
            gl2.glVertex2f(0 + (barWidth * i), 0);
            gl2.glColor3f(colour.fgColour.R, colour.fgColour.G, colour.fgColour.B);
            gl2.glVertex2f(0 + (barWidth * i), height);
            gl2.glColor3f(colour.fgColour.R, colour.fgColour.G, colour.fgColour.B);
            gl2.glVertex2f(barWidth + (barWidth * i), height);

            gl2.glColor3f(colour.fgColour.R, colour.fgColour.G, colour.fgColour.B);
            gl2.glVertex2f(0 + (barWidth * i), 0);
            gl2.glColor3f(colour.fgColour.R, colour.fgColour.G, colour.fgColour.B);
            gl2.glVertex2f(barWidth + (barWidth * i), height);
            gl2.glColor3f(colour.fgColour.R, colour.fgColour.G, colour.fgColour.B);
            gl2.glVertex2f(barWidth + (barWidth * i), 0);

            gl2.glEnd();
        }
    }

    private static void blendColours() {
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
    }
}
