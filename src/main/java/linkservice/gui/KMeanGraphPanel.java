package linkservice.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import mdsj.MDSJ;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.clustering.UncommonDistributions;
import org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable;
import org.apache.mahout.clustering.classify.WeightedVectorWritable;
import org.apache.mahout.clustering.iterator.ClusterWritable;
import org.apache.mahout.common.iterator.sequencefile.PathType;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileDirValueIterable;
import org.apache.mahout.math.AbstractVector;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.utils.clustering.AbstractClusterWriter;
import org.apache.mahout.utils.clustering.ClusterDumper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class KMeanGraphPanel extends JPanel {
	Logger logger = LoggerFactory.getLogger(KMeanGraphPanel.class);

	protected int res; // screen resolution
	protected final int SIZE = 8; // screen size in inches
	protected final int DS = 72; // default scale = 72 pixels per inch
	protected final List<VectorWritable> vectorData = Lists.newArrayList();

	public void KMeanGraphPanel() {
		// Get screen resolution
		res = Toolkit.getDefaultToolkit().getScreenResolution();

		// Set Frame size in inches
		this.setSize(SIZE * res, SIZE * res);
		this.setVisible(true);
	}

	@Override
	public void paintComponent(Graphics g) {
		logger.info("paint");
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		try {
			generateSamples();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// plotSampleData(g2);
	}

	protected void generateSamples() throws IOException {
		Configuration conf = new Configuration();
		Iterable<ClusterWritable> iterable = new SequenceFileDirValueIterable<ClusterWritable>(
				new Path("output/final_clusters/clusters-2-final", "part-*"),
				PathType.GLOB, conf);
		Iterator<ClusterWritable> iterator = iterable.iterator();
		// iterator of clusters
		while (iterator.hasNext()) {
			// handle each clusters
			write(iterator.next());
		}
	}

	public void write(ClusterWritable clusterWritable) {
		Configuration conf = new Configuration();
		Map<Integer, List<WeightedPropertyVectorWritable>> clusterIdToPoints = ClusterDumper
				.readPoints(new Path("output/final_clusters/clusteredPoints"),
						Long.MAX_VALUE, conf);
		// get list of points for the cluster
		List<WeightedPropertyVectorWritable> points = clusterIdToPoints
				.get(clusterWritable.getValue().getId());

		// loop the list
		for (Iterator<WeightedPropertyVectorWritable> iterator = points
				.iterator(); iterator.hasNext();) {
			// the point
			WeightedVectorWritable point = iterator.next();
			point.getVector();
		}
	}

	protected void plotSampleData(Graphics2D g2) {
		double sx = (double) res / DS;
		g2.setTransform(AffineTransform.getScaleInstance(sx, sx));

		// plot the axes
		g2.setColor(Color.BLACK);
		Vector dv = new DenseVector(2).assign(SIZE / 2.0);
		plotRectangle(g2, new DenseVector(2).assign(2), dv);
		plotRectangle(g2, new DenseVector(2).assign(-2), dv);

		// plot the sample data
		g2.setColor(Color.DARK_GRAY);
		dv.assign(0.03);
		for (VectorWritable v : vectorData) {
			plotRectangle(g2, v.get(), dv);
		}
	}

	/**
	 * Draw a rectangle on the graphics context
	 * 
	 * @param g2
	 *            a Graphics2D context
	 * @param v
	 *            a Vector of rectangle center
	 * @param dv
	 *            a Vector of rectangle dimensions
	 */
	protected void plotRectangle(Graphics2D g2, Vector v, Vector dv) {
		double[] flip = { 1, -1 };
		Vector v2 = v.times(new DenseVector(flip));
		v2 = v2.minus(dv.divide(2));
		int h = SIZE / 2;
		double x = v2.get(0) + h;
		double y = v2.get(1) + h;
		g2.draw(new Rectangle2D.Double(x * DS, y * DS, dv.get(0) * DS, dv
				.get(1) * DS));
	}
}
