package frontEnd.Panels;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import frontEnd.EmotionColour;

/**
 * Created by matt on 12/03/17.
 */
public class BarPanel extends GUIPanel {
    public void render( GL2 gl2, int screenWidth, int screenHeight) {
        EmotionColour colour = blendColours();

        float barWidth = screenWidth/36;

        float prevHeight = 0;
        float nextHeight = 0;

        // draw a triangle filling the window
        gl2.glLoadIdentity();
        int k = 0;
        for (int i = 0; i < values.size(); i++) {
            float height = values.get(i) * screenHeight;

            if (i + 1 < values.size())
                nextHeight = values.get(i+1) * screenHeight;
            else
                nextHeight = 0;

            gl2.glBegin(GL.GL_TRIANGLES);

            gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            gl2.glEnable(GL.GL_BLEND);

            gl2.glColor4f(colour.fgColour.R, colour.fgColour.G, colour.fgColour.B, colour.fgColour.alpha);

            /* == Left bar == */
            gl2.glVertex2f(0 + (barWidth * k), 0);
            gl2.glVertex2f(0 + (barWidth * k), height - ((height - prevHeight)/3));
            gl2.glVertex2f(barWidth + (barWidth * k), height - ((height - prevHeight)/3));

            gl2.glVertex2f(0 + (barWidth * k), 0);
            gl2.glVertex2f(barWidth + (barWidth * k), height - ((height - prevHeight)/3));
            gl2.glVertex2f(barWidth + (barWidth * k), 0);

            k++;

            /* == Center bar == */
            gl2.glVertex2f(0 + (barWidth * k), 0);
            gl2.glVertex2f(0 + (barWidth * k), height);
            gl2.glVertex2f(barWidth + (barWidth * k), height);

            gl2.glVertex2f(0 + (barWidth * k), 0);
            gl2.glVertex2f(barWidth + (barWidth * k), height);
            gl2.glVertex2f(barWidth + (barWidth * k), 0);

            k++;

            /* == Right bar == */
            gl2.glVertex2f(0 + (barWidth * k), 0);
            gl2.glVertex2f(0 + (barWidth * k), height - ((height - nextHeight)/3));
            gl2.glVertex2f(barWidth + (barWidth * k), height - ((height - nextHeight)/3));

            gl2.glVertex2f(0 + (barWidth * k), 0);
            gl2.glVertex2f(barWidth + (barWidth * k), height - ((height - nextHeight)/3));
            gl2.glVertex2f(barWidth + (barWidth * k), 0);

            k++;

            prevHeight = height;

            gl2.glEnd();
        }
    }

}
