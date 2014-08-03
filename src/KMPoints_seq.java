import java.util.ArrayList;
import java.util.Random;

//
//
//
public class KMPoints_seq {
	Point[] centroids;
	Point[] newCentroids;
	boolean close = false;
	int[] point2Centroid = null;

	public KMPoints_seq(int numCluster, String inputFile, String outputFile)
			throws Exception {
		int iterations = 10;

		ReadCSV reader = new ReadCSV(inputFile, "point");
		ArrayList<Point> dataset = reader.read();
		// generate centroids randomly
		centroids = new Point[numCluster];

		newCentroids = new Point[numCluster];
		point2Centroid = new int[dataset.size()];
		Random r = new Random();
		for (int i = 0; i < numCluster; i++) {
			centroids[i] = dataset.get(r.nextInt(dataset.size()));
		}

		for (int i = 0; i < iterations && !close; i++) {
			
			System.out.println("round "+i);
			for (Point p : centroids) {
				System.out.println(p.getX() + " " + p.getY());
			}
			for (int j = 0; j < dataset.size(); j++) {
				point2Centroid[j] = getNearestCentroid(dataset.get(j));
			}

			int[] xSum = new int[numCluster];
			int[] ySum = new int[numCluster];
			int[] clusterSize = new int[numCluster];
			// calculate the new centroid of every cluster
			for (int j = 0; j < numCluster; j++) {
				xSum[j] = 0;
				ySum[j] = 0;
			}

			for (int j = 0; j < dataset.size(); j++) {
				int index = point2Centroid[j];
				xSum[index] += dataset.get(j).getX();
				ySum[index] += dataset.get(j).getY();
				clusterSize[index]++;
			}

			for (int j = 0; j < numCluster; j++) {
				if (clusterSize[j] == 0) newCentroids[j] = new Point(0,0);
				else newCentroids[j] = new Point(xSum[j] / clusterSize[j], ySum[j]
						/ clusterSize[j]);
			}
			System.out.println("new cen");
			for (Point p : newCentroids) {
				System.out.println(p.getX() + " " + p.getY());
			}
			// System.out.println();
			calculateDifference(centroids, newCentroids);
			if (close) {
				break;
			} else {
				for (int j = 0; j < numCluster; j++) {
					centroids[j] = newCentroids[j];
				}
			}
		}
	}

	public int getNearestCentroid(Point p) {
		int res = 0;
		double mindistance = Double.MAX_VALUE;
		double distance = 0;
		for (int i = 0; i < centroids.length; i++) {
			Point c = centroids[i];
			distance = Math.pow(Math.abs(p.getX() - c.getX()), 2)
					+ Math.pow(Math.abs(p.getY() - c.getY()), 2);
			if (distance < mindistance) {
				mindistance = distance;
				res = i;
			}
		}
		return res;
	}

	private void calculateDifference(Point[] centroids,
			Point[] newCentroids) {
		double maxdistance = 50;
		double distance = 0;
		for (int i = 0; i < centroids.length; i++) {
			distance += Math.pow(Math.abs(centroids[i].getX()
					- newCentroids[i].getX()), 2)
					+ Math.pow(Math.abs(centroids[i].getY()
							- newCentroids[i].getY()), 2);
			if (distance > maxdistance)
				break;
		}
		System.out
				.println("distance between old centroids and new centroids = "
						+ distance);

		if (distance < maxdistance) {
			close = true;
		}
		
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.println("You should give following args");
			System.out
					.println("arg0 : numCluster | arg1: outFile | arg2 : inFile");
		}
		//		
		int numCluster = Integer.parseInt(args[0]);
		String outFile = args[1];
		String inFile = args[2];
		// //read data from file
		new KMPoints_seq(numCluster, inFile, outFile);
	}
}