import java.util.ArrayList;

import mpi.MPI;
import mpi.MPIException;


public class Kmeans {	
	public static void main(String[] args) throws MPIException{
		MPI.Init(args);
		
		int numOfClusters = 3;
		if (args.length > 0)
			numOfClusters = Integer.parseInt(args[0]);
		
		String output = ".outputKMPoints.csv";
		String input = "./inputKMPoints.csv";
		
		ArrayList<Point> dataset = new ArrayList<Point>();
//		ReadCSV inputCsv ;
//		= new ReadCSV(input, Point);
//		dataset = inputCsv.read();
		new KMPoints(dataset, output, numOfClusters);
	}
	
	
	
}
