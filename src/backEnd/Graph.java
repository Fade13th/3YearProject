package backEnd;

import javafx.scene.chart.NumberAxis;
import org.apache.commons.math3.linear.RealMatrix;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

/**
 * Created by Matt on 2016-11-15.
 */
public class Graph extends ApplicationFrame {
    public Graph (RealMatrix xv, RealMatrix yv, RealMatrix xa, RealMatrix ya) {
        super("a");
        JFreeChart xyChart = ChartFactory.createScatterPlot("Least squares regression", "valence", "arousal", createDataset(xv, yv, xa, ya));

        ChartPanel chartPanel = new ChartPanel( xyChart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 800 , 800 ) );
        final XYPlot plot = xyChart.getXYPlot( );
        XYShapeRenderer renderer = new XYShapeRenderer( );

        ValueAxis domain = plot.getDomainAxis();
        domain.setRange(-1, 1);

        ValueAxis range = plot.getRangeAxis();
        range.setRange(-1, 1);

        plot.setRenderer(renderer);
        setContentPane(chartPanel);
    }

    public Graph(String title) {
        super(title);
    }

    private static XYDataset createDataset(RealMatrix xv, RealMatrix yv, RealMatrix xa, RealMatrix ya) {
        double[] xvD = xv.getColumn(0);
        double[] yvD = yv.getColumn(0);
        double[] xaD = xa.getColumn(0);
        double[] yaD = ya.getColumn(0);

        final XYSeriesCollection dataset = new XYSeriesCollection( );
        final XYSeries valence = new XYSeries("real");
        final XYSeries arousal = new XYSeries("predicted");
        for (int i = 0; i < xvD.length; i++) {
            valence.add(xvD[i], yvD[i]);
        }
        for (int i = 0; i < xaD.length; i++) {
            arousal.add(xaD[i], yaD[i]);
        }
        dataset.addSeries(valence);
        dataset.addSeries(arousal);

        return dataset;
    }
}
