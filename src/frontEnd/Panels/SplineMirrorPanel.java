package frontEnd.Panels;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import frontEnd.EmotionColour;

/**
 * Created by matt on 19/03/17.
 */
public class SplineMirrorPanel extends GUIPanel {
    @Override
    public void render(GL2 gl2, int screenWidth, int screenHeight) {
        EmotionColour colour = blendColours();

        float center = screenHeight/2;

        float prevPoint= center;
        float nextPoint = center;

        float valWidth = screenWidth/12;


        // draw a triangle filling the window
        gl2.glLoadIdentity();

        for (int i = 0; i < values.size(); i++) {
            float point = values.get(i) * (screenHeight/2) + center;

            if (i + 1 < values.size())
                nextPoint = (values.get(i + 1) * (screenHeight/2)) + center;
            else
                nextPoint = center;

            float diffBefore = -(point - prevPoint);
            float diffAfter = -(point - nextPoint);

            float posA = 0 + (valWidth * i);
            float posB = (valWidth/2) + (valWidth * i);
            float posC = valWidth + (valWidth * i);

            float space = (posB - posA)/6;

            gl2.glBegin(GL.GL_LINE_STRIP);

            gl2.glEnable(GL.GL_BLEND);
            gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

            gl2.glColor4f(colour.fgColour.R, colour.fgColour.G, colour.fgColour.B, colour.fgColour.alpha);

            gl2.glVertex2f(posA, point + (diffBefore * 0.5f));

            gl2.glVertex2f(posA + (space), point + (diffBefore * 0.45f));
            gl2.glVertex2f(posA + (space * 2), point + (diffBefore * 0.4f));
            gl2.glVertex2f(posA + (space * 3), point + (diffBefore * 0.25f));
            gl2.glVertex2f(posA + (space * 4), point + (diffBefore * 0.1f));
            gl2.glVertex2f(posA + (space * 5), point + (diffBefore * 0.05f));

            gl2.glVertex2f(posB, point);

            gl2.glVertex2f(posB + (space), point + (diffAfter * 0.05f));
            gl2.glVertex2f(posB + (space * 2), point + (diffAfter * 0.1f));
            gl2.glVertex2f(posB + (space * 3), point + (diffAfter * 0.25f));
            gl2.glVertex2f(posB + (space * 4), point + (diffAfter * 0.4f));
            gl2.glVertex2f(posB + (space * 5), point + (diffAfter * 0.45f));

            gl2.glVertex2f(posC, point + (diffAfter * 0.5f));

            gl2.glEnd();

            prevPoint = point;
            point = center - values.get(i) * (screenHeight/2);


            gl2.glBegin(GL.GL_LINE_STRIP);

            gl2.glVertex2f(posC, point - (diffAfter * 0.5f));

            gl2.glVertex2f(posB + (space * 5), point - (diffAfter * 0.45f));
            gl2.glVertex2f(posB + (space * 4), point - (diffAfter * 0.4f));
            gl2.glVertex2f(posB + (space * 3), point - (diffAfter * 0.25f));
            gl2.glVertex2f(posB + (space * 2), point - (diffAfter * 0.1f));
            gl2.glVertex2f(posB + (space), point - (diffAfter * 0.05f));

            gl2.glVertex2f(posB, point);

            gl2.glVertex2f(posA + (space * 5), point - (diffBefore * 0.05f));
            gl2.glVertex2f(posA + (space * 4), point - (diffBefore * 0.1f));
            gl2.glVertex2f(posA + (space * 3), point - (diffBefore * 0.25f));
            gl2.glVertex2f(posA + (space * 2), point - (diffBefore * 0.4f));
            gl2.glVertex2f(posA + (space), point - (diffBefore * 0.45f));

            gl2.glVertex2f(posA, point - (diffBefore * 0.5f));


            gl2.glEnd();
        }
    }
}
