import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import mpi.*;

public class DNACluster {
	public DNACluster(int numCluster, String outFile, ArrayList<String> dnaList){
		
	}
	
	public static void main(String[] args) {
		//sequential version
		
		/*
		 * arg0 : numCluster
		 * arg1	: outFile
		 * arg2 : inFile
		 * 
		 */
		if(args.length != 3) {
			System.out.println("You should give following args");
			System.out.println("arg0 : numCluster | arg1: outFile | arg2 : inFile");
		}
		
		int numCluster = Integer.parseInt(args[0]);
		String outFile = args[1];
		String inFile = args[2];
		ReadCSV reader = new ReadCSV(inFile, "dna");
		try {
			ArrayList<String> dnaList = reader.read();
			System.out.println(dnaList.size());
			//step1 select init centroids randomly from dnaList
			ArrayList<Integer> randomPool = generateRandom(numCluster, dnaList.size());
			String [] centroids = new String[numCluster];
			int n = 0;
			for(int i : randomPool) {
				if(n < numCluster){
					centroids[n] = dnaList.get(i).toString();
					n++;
				} else {
					break;
				}
			}
			//test
			System.out.println(Arrays.toString(centroids));
			
			//calculate dif of each dna strand
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	//Generate numCluster ints within range of 0 to listSize
	//return list
	public static ArrayList<Integer> generateRandom(int numCluster, int listSize){
		ArrayList randomPool = new ArrayList<Integer>();
		Random r = new Random();
		while(randomPool.size() < numCluster) {
			int random = r.nextInt(listSize);
			if(!randomPool.contains(random)){
				randomPool.add(random);
			}
		}
		
		return randomPool;
	}
	
	//calculate dif
	public static int calDif (String src, String dest) {
		int dif = 0;
		
		return 0;
	}
	
	
}
