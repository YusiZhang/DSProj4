import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

// import mpi.*;

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
		
		// int numCluster = Integer.parseInt(args[0]);
		// String outFile = args[1];
		// String inFile = args[2];

		int numCluster = 4;
		String outFile = "./output/out.csv";
		String inFile = "./input/dna.csv";


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
			
			//step2 calculate dif of each dna strand
			int [] resultCluster = new int[dnaList.size()];
			int [] resultDif = new int[dnaList.size()];
			for (int i = 0; i < dnaList.size(); i++) {
				int dif = Integer.MAX_VALUE;
				int cluster = -1;
				for (int j = 0; j < numCluster; j++) {
					//compare strand with centroid one by one
					int tempdif = calDif(dnaList.get(i),centroids[j]);
					if(tempdif < dif ){
						dif = tempdif;
						cluster = j;
					}else {
						continue;
					}
				}
				resultCluster[i] = cluster;
				resultDif[i] = dif;
			}

			System.out.println(Arrays.toString(resultDif));
			System.out.println(Arrays.toString(resultCluster));

			//step 3 recalculate centriod
			String[] tempCluster = new String[dnaList.size()];
			HashMap<Integer,ArrayList<String>> map = new HashMap<Integer,ArrayList<String>>();
			
			for (int i = 0; i < numCluster; i++) {
				map.put(i,new ArrayList<String>());
			}

			for (int i = 0; i < dnaList.size(); i++) {
				map.get(resultCluster[i]).add(dnaList.get(i));
			}//now we have arraylists of each cluster

			//do recalculate
			String[] newCentroids = new String[numCluster];
			for (int i = 0; i < numCluster; i++) {
				newCentroids[i] = reCal(map.get(resultCluster[i]));
			}//now we have new centroids
			
			//test
			System.out.println(Arrays.toString(newCentroids));

			//step 4 recalcualte cluster
			int [] resultCluster2 = new int[dnaList.size()];
			int [] resultDif2 = new int[dnaList.size()];
			for (int i = 0; i < dnaList.size(); i++) {
				int dif = Integer.MAX_VALUE;
				int cluster = -1;
				for (int j = 0; j < numCluster; j++) {
					//compare strand with centroid one by one
					int tempdif = calDif(dnaList.get(i),newCentroids[j]);
					if(tempdif < dif ){
						dif = tempdif;
						cluster = j;
					}else {
						continue;
					}
				}
				resultCluster2[i] = cluster;
				resultDif2[i] = dif;
			}
			System.out.println(Arrays.toString(resultDif2));
			System.out.println(Arrays.toString(resultCluster2));

			
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
		char [] str1 = src.toCharArray();
		char []	str2 = dest.toCharArray();

		for(int i = 0; i < str1.length ; i++) {
			if (str1[i] != str2[i]) {
				dif++;
			}
		}

		return dif;
	}

	//re-calculate centroids
	public static String reCal(ArrayList<String> strands) {
		StringBuilder newCentroid = new StringBuilder();
		int dnaLength = strands.get(0).length();
		for (int i = 0; i < dnaLength; i++) {
			int [] frequence = new int[4];
			for (String strand : strands) {
				char cur = strand.charAt(i);
				switch (cur) {
					case 'A' : 
						frequence[0]++;
						break;
					case 'C' :
						frequence[1]++;
						break;
					case 'T' : 
						frequence[2]++;
						break;
					case 'G' : 
						frequence[3]++;
						break;
				}
			}

			//compare ACTG frequence and decide which one is the most
			int max = Integer.MIN_VALUE;
			int index = -1;
			for(int j = 0; j < 4; j++) {
				if (frequence[j] > max) {
					max = frequence[j];
					index = j;
				}
			}
			char [] bases = {'A','C','T','G'};
			newCentroid.append(bases[index]);
		}


		return new String(newCentroid);
	} 
	
	
}