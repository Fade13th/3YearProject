package frontEnd;

import Util.Config;
import backEnd.Algorithm;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import frontEnd.Panels.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by matt on 07/04/17.
 */
public class GLCanvasPane {
    GUIPanel guiPanel;
    GLCanvas glCanvas;

    Algorithm algorithm;

    boolean songLoaded = false;


    public GLCanvasPane(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public GLCanvas getCanvas() {
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        glCanvas = new com.jogamp.opengl.awt.GLCanvas( glcapabilities );

        setPanelType(Panels.SPLINE_PANEL_MIRROR);

        glCanvas.addGLEventListener( new GLEventListener() {

            @Override
            public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
                guiPanel.setup( glautodrawable.getGL().getGL2(), width, height);
            }

            @Override
            public void init( GLAutoDrawable glautodrawable ) {
            }

            @Override
            public void dispose( GLAutoDrawable glautodrawable ) {
            }

            @Override
            public void display( GLAutoDrawable glautodrawable ) {
                if (songLoaded) {
                    guiPanel.clear(glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight());
                    guiPanel.render(glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight());
                    guiPanel.renderPrevious(glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight(), guiPanel);
                }
            }
        });

        return glCanvas;
    }

    public void setPanelType(Panels type) {
        switch (type) {
            case BAR_PANEL:
                guiPanel = new BarPanel();
                break;

            case BAR_PANEL_MIRROR:
                guiPanel = new BarPanelMirrored();
                break;

            case SPLINE_PANEL:
                guiPanel = new SplinePanel();
                break;

            case SPLINE_PANEL_MIRROR:
                guiPanel = new SplineMirrorPanel();
                break;

            default:
                guiPanel = new SplineMirrorPanel();
                break;
        }
    }
}
