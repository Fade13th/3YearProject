package frontEnd;

import Util.Config;
import backEnd.Algorithm;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLCanvas;
import frontEnd.Audio.AudioManager;
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
    Algorithm algorithm;

    GLCanvasPane canvas1;
    GLCanvasPane canvas2;
    GLCanvasPane canvas3;

    Timer t= new Timer();

    public void setup() {
        Config.init();
        ColourMapping.init();

        setupFrame();
    }

    public void setup(Algorithm algorithm) {
        this.algorithm = algorithm;

        setup();
    }

    public void setupFrame() {
        final JFrame jframe = new JFrame( "V A visualisation" );
        jframe.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                jframe.dispose();
                System.exit( 0 );
            }
        });

        jframe.setJMenuBar(setupMenu());

        Container pane = jframe.getContentPane();

        JPanel playOptions = setupPlayOptions();

        Container canvases = new JPanel();

        canvas1 = new GLCanvasPane(algorithm);
        canvas2 = new GLCanvasPane(algorithm);
        canvas3 = new GLCanvasPane(algorithm);

        //pane.add( canvas1.getCanvas(), BorderLayout.CENTER );
        canvases.add( canvas2.getCanvas());
        canvases.add( canvas3.getCanvas(), BorderLayout.CENTER );

        canvas1.getCanvas();
        //canvas2.getCanvas();
        //canvas3.getCanvas();

        canvases.setLayout(new GridLayout(1,2));

        pane.add(canvases);

        pane.add(playOptions, BorderLayout.SOUTH);

        jframe.setSize( 1280, 480 );
        jframe.setVisible( true );
    }

    private JPanel setupPlayOptions() {
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

    private JMenuBar setupMenu() {
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
                canvas1.setPanelType(Panels.BAR_PANEL);
                canvas2.setPanelType(Panels.BAR_PANEL);
                canvas3.setPanelType(Panels.BAR_PANEL);
            }
        });
        JMenuItem barsMirror = new JMenuItem("Bars Mirrored");
        barsMirror.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas1.setPanelType(Panels.BAR_PANEL_MIRROR);
                canvas2.setPanelType(Panels.BAR_PANEL_MIRROR);
                canvas3.setPanelType(Panels.BAR_PANEL_MIRROR);
            }
        });
        JMenuItem spline = new JMenuItem("Waves");
        spline.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas1.setPanelType(Panels.SPLINE_PANEL);
                canvas2.setPanelType(Panels.SPLINE_PANEL);
                canvas3.setPanelType(Panels.SPLINE_PANEL);
            }
        });
        JMenuItem splineMirror = new JMenuItem("Waves Mirrored");
        splineMirror.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas1.setPanelType(Panels.SPLINE_PANEL_MIRROR);
                canvas2.setPanelType(Panels.SPLINE_PANEL);
                canvas3.setPanelType(Panels.BAR_PANEL_MIRROR);
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

    public void loadSong(File songFile) throws FileNotFoundException {
        canvas1.songLoaded = false;
        canvas2.songLoaded = false;
        canvas3.songLoaded = false;

        t.cancel();
        t.purge();
        t = new Timer();

        canvas1.guiPanel.clearPrevious();
        canvas2.guiPanel.clearPrevious();
        canvas3.guiPanel.clearPrevious();

        String name = songFile.getName().replace(".wav", "");

        File va = new File(Config.V_A_SCORES + File.separator + name + ".csv");
        File chroma = new File(Config.CHRMOA + File.separator + name + ".csv");

        if (!va.exists()) {
            if (algorithm == null) {
                throw new FileNotFoundException("Song has not been through valence arousal analysis");
            }
            else {
                algorithm.SVMTest(name);
            }
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

        canvas1.guiPanel.values = song.get(0);
        canvas2.guiPanel.values = song.get(0);
        canvas3.guiPanel.values = song.get(0);

        canvas1.guiPanel.colour = new EmotionColour(vaScores.get(0).get(0), vaScores.get(0).get(1));
        canvas2.guiPanel.colour = new EmotionColour(vaScores.get(0).get(0), vaScores.get(0).get(1));
        canvas3.guiPanel.colour = new EmotionColour(vaScores.get(0).get(0), vaScores.get(0).get(1));

        canvas1.guiPanel.targetColour = new EmotionColour(vaScores.get(0).get(0), vaScores.get(0).get(1));
        canvas2.guiPanel.targetColour = new EmotionColour(vaScores.get(0).get(0), vaScores.get(0).get(1));
        canvas3.guiPanel.targetColour = new EmotionColour(vaScores.get(0).get(0), vaScores.get(0).get(1));

        Iterator<ArrayList<Float>> iter = song.iterator();

        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (iter.hasNext()) {
                    ArrayList<Float> i = iter.next();
                    canvas1.guiPanel.values = i;
                    canvas2.guiPanel.values = i;
                    canvas3.guiPanel.values = i;

                    canvas1.glCanvas.display();
                    canvas2.glCanvas.display();
                    canvas3.glCanvas.display();

                    canvas1.guiPanel.updatePreviousValues(i);
                    canvas2.guiPanel.updatePreviousValues(i);
                    canvas3.guiPanel.updatePreviousValues(i);
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
                    canvas1.guiPanel.targetColour = c;
                    canvas2.guiPanel.targetColour = c;
                    canvas3.guiPanel.targetColour = c;

                    canvas1.guiPanel.updatePreviousColours(c);
                    canvas2.guiPanel.updatePreviousColours(c);
                    canvas3.guiPanel.updatePreviousColours(c);
                }
                else t.cancel();
            }
        }, 0, 500);

        canvas1.songLoaded = true;
        canvas2.songLoaded = true;
        canvas3.songLoaded = true;

        AudioManager.play(songFile);
    }

    private void extractChroma(String fileName) throws IOException {
        Runtime rt = Runtime.getRuntime();

        Process pr = rt.exec("./openSMILE-2.1.0/SMILExtract -C " + Config.CHROMA_CONFIG + " -I "
                + fileName + " -O " + Config.CHRMOA + File.separator + fileName.substring(fileName.lastIndexOf(File.separator), fileName.lastIndexOf(".")) + ".csv");
        try {
            pr.waitFor();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<ArrayList<Float>> loadChroma(File file) throws FileNotFoundException {
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

    private ArrayList<ArrayList<Float>> loadVA(File file) throws FileNotFoundException {
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
