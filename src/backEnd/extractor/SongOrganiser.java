package backEnd.extractor;

import backEnd.data.Matrix;
import backEnd.data.Song;
import backEnd.data.TrainingSong;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by matt on 12/02/17.
 */
public class SongOrganiser {
    Map<String, TrainingSong> songMap;

    public SongOrganiser() {
        try {
            FeatureExtractor.extractAll();

            File featureDir = new File("features/default");
            Map<String, TrainingSong> songMap = new HashMap<>();

            for (File file : featureDir.listFiles()) {
                String name = file.getName().substring(0, file.getName().lastIndexOf("."));
                TrainingSong song = new TrainingSong(name);
                songMap.put(name, song);
            }

            ResultPairer rp = new ResultPairer(songMap);
            rp.pairArousal();
            rp.pairValence();

            this.songMap = songMap;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RealMatrix[] getMatrices() {
        RealMatrix songData = null, valenceData = null, arousalData = null;

        Set<String> keys = songMap.keySet();
        Iterator<String> iter = keys.iterator();
        while (iter.hasNext()) {
            TrainingSong song = songMap.get(iter.next());

            if (song.getArousalScores() != null && song.getValenceScores() != null) {
                if (songData == null) {
                    songData = song.getData();
                    valenceData = song.getValenceScores();
                    arousalData = song.getArousalScores();
                }
                else {
                    try {
                        songData = Matrix.concat(songData, song.getData());
                        valenceData = Matrix.concat(valenceData, song.getValenceScores());
                        arousalData = Matrix.concat(arousalData, song.getArousalScores());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return new RealMatrix[]{songData, valenceData, arousalData};
    }
}
