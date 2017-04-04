import Util.Config;
import backEnd.Algorithm;
import com.sun.org.apache.xpath.internal.operations.Bool;
import frontEnd.GUI;

/**
 * Created by matt on 04/04/17.
 */
public class Wrapper {
    public static void main(String[] args) {
        Algorithm algorithm;
        GUI gui = new GUI();

        if (args.length > 0 && Boolean.parseBoolean(args[0])) {
            Config.init();
            algorithm = new Algorithm();
            gui.setup(algorithm);
        }
        else {
            gui.setup();
        }
    }
}
