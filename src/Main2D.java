import java.util.ArrayList;

import mpi.*;
public class Main2D {
	public static void main(String[] args) throws Exception {
		MPI.Init(args);
		
		//hardcode clusters numbers for testing...
//		int numClusters = 2;
		
		int numClusters = Integer.parseInt(args[0]);
		
		String outFile2 = "../output/point_paral.csv";
		String inFile2 = "../input/point.csv";
		ArrayList<Point> pointList = new ArrayList<Point>();
		ReadCSV reader2 = new ReadCSV(inFile2, "point");
		pointList = reader2.read();
		new KMPoints_paral(pointList, outFile2, numClusters);
			

		
		MPI.Finalize();
	}
}
