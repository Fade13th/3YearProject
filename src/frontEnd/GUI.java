package frontEnd;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by matt on 12/03/17.
 */
public class GUI {
    public static void main( String [] args ) {
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );

        glcanvas.addGLEventListener( new GLEventListener() {

            @Override
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
                BarPanel.setup( glautodrawable.getGL().getGL2(), width, height );
            }

            @Override
            public void init( GLAutoDrawable glautodrawable ) {
            }

            @Override
            public void dispose( GLAutoDrawable glautodrawable ) {
            }

            @Override
            public void display( GLAutoDrawable glautodrawable ) {
                BarPanel.render( glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight() );
            }
        });

        ArrayList<ArrayList<Float>> song = loadChroma();
        ArrayList<ArrayList<Float>> vaScores = loadVAtemp();

        BarPanel.values = song.get(0);
        BarPanel.colour = new EmotionColour(vaScores.get(0).get(0), vaScores.get(0).get(1));
        BarPanel.targetColour = new EmotionColour(vaScores.get(0).get(0), vaScores.get(0).get(1));

        final JFrame jframe = new JFrame( "One Triangle Swing GLCanvas" );
        jframe.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                jframe.dispose();
                System.exit( 0 );
            }
        });

        jframe.getContentPane().add( glcanvas, BorderLayout.CENTER );
        jframe.setSize( 640, 480 );
        jframe.setVisible( true );

        Timer t= new Timer();

        Iterator<ArrayList<Float>> iter = song.iterator();

        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (iter.hasNext()) {
                    BarPanel.values = iter.next();
                    glcanvas.display();
                }
                else t.cancel();
            }
        }, 0, 25);

        Iterator<ArrayList<Float>> iterator = vaScores.iterator();

        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (iterator.hasNext()) {
                    ArrayList<Float> scores = iterator.next();
                    BarPanel.targetColour = new EmotionColour(scores.get(0), scores.get(1));
                }
                else t.cancel();
            }
        }, 15000, 500);
    }

    private static ArrayList<ArrayList<Float>> loadChroma() {
        ArrayList<ArrayList<Float>> song = new ArrayList<>();

        try {
            BufferedReader in = new BufferedReader(new FileReader(new File("chroma.csv")));

            String line = null;

            while ((line = in.readLine()) != null) {
                ArrayList<Float> timing = new ArrayList<>();

                String[] split = line.split(";");
                for (String s : split) {
                    timing.add(Float.parseFloat(s));
                }
                song.add(timing);
            }

            in.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return song;
    }

    private static ArrayList<ArrayList<Float>> loadVAtemp() {
        ArrayList<ArrayList<Float>> song = new ArrayList<>();

        float valenceMin = Float.MAX_VALUE;
        float valenceMax = Float.MIN_VALUE;

        float arousalMin = Float.MAX_VALUE;
        float arousalMax = Float.MIN_VALUE;

        try {
            BufferedReader in = new BufferedReader(new FileReader(new File("VAtemp.csv")));

            String line = in.readLine();

            while ((line = in.readLine()) != null) {
                ArrayList<Float> l = new ArrayList<>();
                String[] split = line.split(";");
                float val = Float.parseFloat(split[0]);
                float aro = Float.parseFloat(split[1]);

                l.add(val);
                l.add(aro);

                if (val < valenceMin)
                    valenceMin = val;
                else if (val > valenceMax)
                    valenceMax = val;

                if (aro < arousalMin)
                    arousalMin = aro;
                else if (aro > arousalMax)
                    arousalMax = aro;

                song.add(l);
            }

            in.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        for (ArrayList<Float> row : song) {
            row.set(0, ((2*(row.get(0) - valenceMin)/(valenceMax - valenceMin))) - 1.0f);
            row.set(1, ((2*(row.get(1) - arousalMin)/(arousalMax - arousalMin))) - 1.0f);
        }

        return song;
    }
}
