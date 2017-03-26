package frontEnd;

import Util.Config;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by matt on 26/03/17.
 */
public class ColourMapping {
    private static ArrayList<ArrayList<Colour>> mapping;

    private static int columns, rows;
    private static float columnWidth, rowWidth;

    public static void init() {
        mapping = readColours();

        columns = mapping.size();
        rows = mapping.get(0).size();

        columnWidth = 2.0f/columns;
        rowWidth = 2.0f/rows;
    }

    public static Colour getColour(float x, float y) {
        float xacc = -1 + columnWidth, yacc = 1 - rowWidth;
        int xPos = 0, yPos = 0;

        while (x > xacc + columnWidth) {
            xacc += columnWidth;
            xPos++;
        }

        while (y < yacc - rowWidth) {
            yacc -= rowWidth;
            yPos++;
        }
        yacc -= rowWidth;

        float xDiff = (x - xacc)/columnWidth;
        float yDiff = (y - yacc)/rowWidth;

        Colour tl = mapping.get(yPos).get(xPos);
        Colour tr = mapping.get(yPos).get(xPos+1);
        Colour bl = mapping.get(yPos+1).get(xPos);
        Colour br = mapping.get(yPos+1).get(xPos+1);

        Colour acrossT = new Colour((tl.R * (1-xDiff)) + (tr.R * xDiff),
                (tl.G * (1-xDiff)) + (tr.G * xDiff),
                (tl.B * (1-xDiff)) + (tr.B * xDiff));

        Colour acrossB = new Colour((bl.R * (1-xDiff)) + (br.R * xDiff),
                (bl.G * (1-xDiff)) + (br.G * xDiff),
                (bl.B * (1-xDiff)) + (br.B * xDiff));

        Colour blend = new Colour((acrossT.R * yDiff) + (acrossB.R * (1-yDiff)),
                (acrossT.G * yDiff) + (acrossB.G * (1-yDiff)),
                (acrossT.B * yDiff) + (acrossB.B * (1-yDiff)));

        return blend;
    }

    private static ArrayList<ArrayList<Colour>> readColours() {
        ArrayList<ArrayList<Colour>> map = new ArrayList<>();

        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(Config.COLOUR_MAP)));

            String line = "";

            while ((line = in.readLine()) != null) {
                ArrayList<Colour> row = new ArrayList<>();

                String[] colours = line.replaceAll(" ", "").split(";");

                for (String colour : colours) {
                    String[] rgb = colour.replace("(", "").replace(")", "").split(",");

                    row.add(new Colour(Float.parseFloat(rgb[0]), Float.parseFloat(rgb[1]), Float.parseFloat(rgb[2])));
                }

                map.add(row);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }
}
