package backEnd.io;

import backEnd.data.Features;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matt on 2016-11-14.
 */
public class XMLParser {
    public static Features extractFeatures(String filename) {
        Features result = new Features();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filename));

            Element root = doc.getDocumentElement();

            NodeList songs = root.getElementsByTagName("data_set");

            for (int h = 0; h < songs.getLength(); h++) {
                HashMap<String, ArrayList<ArrayList<Double>>> songData = new HashMap<>();

                Element e = (Element) songs.item(h);
                String name = e.getElementsByTagName("data_set_id").item(0).getTextContent();
                name = name.substring(name.lastIndexOf(File.separator)+1).replace(".wav","");

                NodeList frames = e.getElementsByTagName("section");

                for (int i = 0; i < frames.getLength(); i++) {
                    Node frame = frames.item(i);

                    if (frame.getNodeType() == Node.ELEMENT_NODE) {
                        Element e1 = (Element) frame;

                        NodeList features = e1.getElementsByTagName("feature");

                        for (int j = 0; j < features.getLength(); j++) {
                            Node feature = features.item(j);

                            if (feature.getNodeType() == Node.ELEMENT_NODE) {
                                Element e2 = (Element) feature;

                                String featureName = e2.getElementsByTagName("name").item(0).getTextContent();

                                NodeList values = e2.getElementsByTagName("v");

                                ArrayList<Double> featureVals = new ArrayList<>();

                                for (int k = 0; k < values.getLength(); k++) {
                                    Double d = Double.valueOf(values.item(k).getTextContent());
                                    featureVals.add(d);
                                }

                                if (songData.containsKey(featureName)) {
                                    songData.get(featureName).add(featureVals);
                                }
                                else {
                                    ArrayList<ArrayList<Double>> featureList = new ArrayList<>();
                                    featureList.add(featureVals);
                                    songData.put(featureName, featureList);
                                }
                            }
                        }
                    }
                    result.addSongInfo(name, songData);
                }
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        catch (SAXException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
