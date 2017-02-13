package backEnd.extractor;

import backEnd.data.Song;
import backEnd.data.TrainingSong;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
}
