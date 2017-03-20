package frontEnd;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLCanvas;
import frontEnd.Panels.*;
import javafx.stage.FileChooser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by matt on 12/03/17.
 */
public class GUI {

    static GUIPanel guiPanel;
    static GLCanvas glCanvas;

    static boolean songLoaded = false;

    static Timer t= new Timer();

    public static void main( String [] args ) {

        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        glCanvas = new GLCanvas( glcapabilities );

        setPanelType(Panels.SPLINE_PANEL_MIRROR);

        glCanvas.addGLEventListener( new GLEventListener() {

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
                if (songLoaded) {
                    guiPanel.clear(glautodrawable.getGL().getGL2());
                    guiPanel.render(glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight());
                    guiPanel.renderPrevious(glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight(), guiPanel);
                }
            }
        });

        final JFrame jframe = new JFrame( "One Triangle Swing GLCanvas" );
        jframe.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                jframe.dispose();
                System.exit( 0 );
            }
        });

        jframe.setJMenuBar(setupMenu());

        Container pane = jframe.getContentPane();

        JPanel playOptions = setupPlayOptions();

        pane.add( glCanvas, BorderLayout.CENTER );
        pane.add(playOptions, BorderLayout.SOUTH);

        jframe.setSize( 640, 480 );
        jframe.setVisible( true );
    }

    private static JPanel setupPlayOptions() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 3));

        JPanel volume = new JPanel();
        JSlider vol = new JSlider();
        volume.add(vol);

        JPanel playButtons = new JPanel();

        JButton rewind = new JButton("<<");
        rewind.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        JButton playPause = new JButton("> ||");
        playPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        JButton stop = new JButton("[]");
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        playButtons.add(rewind);
        playButtons.add(playPause);
        playButtons.add(stop);

        panel.add(volume);
        panel.add(playButtons);
        panel.add(new JPanel());

        return panel;
    }

    private static JMenuBar setupMenu() {
        JMenuBar menu = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenuItem load = new JMenuItem("Load...");
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("clips_45seconds"));

                FileNameExtensionFilter ff = new FileNameExtensionFilter("Wav files", "wav");
                chooser.setFileFilter(ff);

                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File selected = chooser.getSelectedFile();
                    try {
                        loadSong(selected);
                    }
                    catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        JMenu panelSubmenu = new JMenu("Visualisation");

        JMenuItem bars = new JMenuItem("Bars");
        bars.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPanelType(Panels.BAR_PANEL);
            }
        });
        JMenuItem barsMirror = new JMenuItem("Bars Mirrored");
        barsMirror.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPanelType(Panels.BAR_PANEL_MIRROR);
            }
        });
        JMenuItem spline = new JMenuItem("Waves");
        spline.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPanelType(Panels.SPLINE_PANEL);
            }
        });
        JMenuItem splineMirror = new JMenuItem("Waves Mirrored");
        splineMirror.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPanelType(Panels.SPLINE_PANEL_MIRROR);
            }
        });

        panelSubmenu.add(bars);
        panelSubmenu.add(barsMirror);
        panelSubmenu.add(spline);
        panelSubmenu.add(splineMirror);

        fileMenu.add(load);
        fileMenu.add(panelSubmenu);

        menu.add(fileMenu);
        return menu;
    }

    private static void setPanelType(Panels type) {
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

    private static void extractChroma(String fileName) throws IOException {
        Runtime rt = Runtime.getRuntime();

        Process pr = rt.exec("./openSMILE-2.1.0/SMILExtract -C openSMILE-2.1.0/chroma_fft.conf -I "
                + fileName + " -O chroma" + File.separator + fileName.substring(fileName.lastIndexOf(File.separator), fileName.lastIndexOf(".")) + ".csv");
        try {
            pr.waitFor();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void loadSong(File songFile) throws FileNotFoundException {
        songLoaded = false;

        t.cancel();
        t.purge();
        t = new Timer();

        guiPanel.clearPrevious();

        String name = songFile.getName().replace(".wav", "");

        File va = new File("VA" + File.separator + name + ".csv");
        File chroma = new File("chroma" + File.separator + name + ".csv");

        if (!va.exists()) {
            throw new FileNotFoundException("Song has not been through valence arousal analysis");
        }

        if (!chroma.exists()) {
            try {
                extractChroma(songFile.getAbsolutePath());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        ArrayList<ArrayList<Float>> song = loadChroma(chroma);
        ArrayList<ArrayList<Float>> vaScores = loadVA(va);

        guiPanel.values = song.get(0);
        guiPanel.colour = new EmotionColour(vaScores.get(0).get(0), vaScores.get(0).get(1));
        guiPanel.targetColour = new EmotionColour(vaScores.get(0).get(0), vaScores.get(0).get(1));

        Iterator<ArrayList<Float>> iter = song.iterator();

        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (iter.hasNext()) {
                    ArrayList<Float> i = iter.next();
                    guiPanel.values = i;
                    glCanvas.display();
                    guiPanel.updatePreviousValues(i);
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
                    EmotionColour c = new EmotionColour(scores.get(0), scores.get(1));
                    guiPanel.targetColour = c;
                    guiPanel.updatePreviousColours(c);
                }
                else t.cancel();
            }
        }, 0, 500);

        songLoaded = true;
    }

    private static ArrayList<ArrayList<Float>> loadChroma(File file) throws FileNotFoundException {
        ArrayList<ArrayList<Float>> song = new ArrayList<>();

        if (!file.exists()) {
            throw new FileNotFoundException("Chroma features for file "  + file.getName() + " not found");
        }

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));

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

    private static ArrayList<ArrayList<Float>> loadVA(File file) throws FileNotFoundException {
        ArrayList<ArrayList<Float>> song = new ArrayList<>();

        if (!file.exists()) {
            throw new FileNotFoundException("Chroma features for file "  + file.getName() + " not found");
        }

        float valenceMin = Float.MAX_VALUE;
        float valenceMax = Float.MIN_VALUE;

        float arousalMin = Float.MAX_VALUE;
        float arousalMax = Float.MIN_VALUE;

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));

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
