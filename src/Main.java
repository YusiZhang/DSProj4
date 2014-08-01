import java.util.ArrayList;

import mpi.*;
public class Main {
	public static void main(String[] args) throws Exception {
		MPI.Init(args);
		
		//hardcode clusters numbers for testing...
		int numClusters = 3;
		
//		int numClusters = Integer.parseInt(args[0]);
		String outFile = "./output/out.csv";
		String inFile = "./input/dna.csv";
		ArrayList<String> dnaList = new ArrayList<String>();
		ReadCSV reader = new ReadCSV(inFile, "dna");
		
		
		dnaList = reader.read();
		new DNACluster(numClusters, outFile, dnaList);
			
			
	
		
		
		
		MPI.Finalize();
	}
}
