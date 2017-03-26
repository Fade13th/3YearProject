package Util;

import java.io.*;

/**
 * Created by matt on 26/03/17.
 */
public class Config {
    public static String CHROMA_CONFIG;
    public static String COLOUR_MAP;
    public static String CHRMOA;
    public static String V_A_SCORES;
    public static String FEATURES;
    public static String MFCC_CONFIG;

    public static void init() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File("config")));

            String line = "";

            while((line = in.readLine()) != null) {
                String[] l = line.split(":");

                switch (l[0]) {
                    case "colourMap" :
                        COLOUR_MAP = l[1];
                        break;

                    case "chroma" :
                        CHRMOA = l[1];
                        break;

                    case "VA" :
                        V_A_SCORES = l[1];
                        break;

                    case "features" :
                        FEATURES = l[1];
                        break;

                    case "chromaConf" :
                        CHROMA_CONFIG = l[1];
                        break;

                    case "mfccConfig" :
                        MFCC_CONFIG = l[1];
                        break;

                    default:
                        break;
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Could not find config file");
            System.exit(1);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not read config file");
            System.exit(1);
        }
    }
}
