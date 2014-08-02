import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import mpi.*;
public class DNA {
	public String outFile;
	public int numCluster;
	public ArrayList<String>dnaList;
	
	public int numStrandsSlave;
	public int [] resultCluster;
	public int [] resultDif ;
	public String [] centroids;
	public int numInter;
	public int myRank;
	public int size;
	public int dnaLength;
	public int [][][] sum;
	public HashMap<Integer,ArrayList<String>> map; //for computing...
	public DNA(int numCluster, String outFile, ArrayList<String> dnaList) throws MPIException{
		this.numCluster = numCluster;
		this.outFile = outFile;
		this.dnaList = dnaList;
		this.numInter = 2;
		this.centroids = new String[numCluster];
		this.dnaLength = dnaList.get(0).length();
		this.sum = new int [numCluster][dnaLength][4]; //cluster,dnalength,4bases
		this.resultCluster =  new int[dnaList.size()];
		this.resultDif = new int[dnaList.size()];
		
		
		
		myRank = MPI.COMM_WORLD.Rank();
		size = MPI.COMM_WORLD.Size();
		numStrandsSlave = dnaList.size() / (size -1);
		
		System.out.println("Rank : " + myRank + "is working...");
		for(int start = 0; start <numInter; start++){
			
			if(myRank == 0) {
				if(start==0) centroids = initCentroids(); //first time
				else {//master cal new centroids
					centroids = recalCen();
				}
				//send centriod to every slave
				for (int slaveRank = 1; slaveRank < size ; slaveRank++) {
					System.out.println("Send centroids " + Arrays.toString(centroids));
					MPI.COMM_WORLD.Send(centroids, 0, numCluster, MPI.OBJECT, slaveRank, 99);
				}
			}
			
			else{
				MPI.COMM_WORLD.Recv(centroids, 0, numCluster, MPI.OBJECT, 0, 99); //others has the same centroids
				compute();
			}
			
			//this is where all should work the same way...
			//all reduce
			System.out.println("Start all reduce!!!  " + myRank);
			for (int i = 0; i < numCluster; i++) {
				for (int j = 0 ; j < dnaLength; j++) {
					int [] xSum = new int[4], xSumNew = new int[4];
					xSum = sum[i][j];
					MPI.COMM_WORLD.Allreduce(xSum, 0, xSumNew, 0, xSum.length, MPI.INT, MPI.SUM);
					sum[i][j] = xSum;
					//testing...
//					System.out.println("Rank " + myRank + "xSum " + Arrays.toString(sum[i][j]));
				}
			}
			
			//abort condition..
			if(false) {
				break;
			}
		}
		//assume finish...
		if(myRank != 0){ //slave send results to master
			System.out.println("Rank " +myRank + "send " + Arrays.toString(resultCluster));
			MPI.COMM_WORLD.Send(resultCluster, 0, resultCluster.length, MPI.OBJECT, 0, 0);
		}
		else {//master receive and glue them
			int[] clusters = glue();
			writeFile(dnaList, clusters, outFile);
		}
		System.out.println("Program ends");
		//writeFile(dnaList, clusters, outFile);
		
	}
	
	private void writeFile(ArrayList<String> dnaList, int[] clusters,String outFile) {
		try {
			PrintWriter writer = new PrintWriter(new File(outFile));
			System.out.println(outFile);
			for(int i = 0; i < dnaList.size(); i++) {
				writer.println(dnaList.get(i) + "," + clusters[i]);
				System.out.println(dnaList.get(i) + "," + clusters[i]);
				writer.flush();
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private int [] glue() throws MPIException {
		int [] clusters = new int[dnaList.size()];
		for(int slaveRank = 1; slaveRank < size; slaveRank++){
			int[] tempClusters = new int[dnaList.size()];
			MPI.COMM_WORLD.Recv(tempClusters, 0, tempClusters.length, MPI.INT,slaveRank, 0);
			for(int i = 0 ; i < tempClusters.length;i++){
				if(tempClusters[i]!=-1) clusters[i] = tempClusters[i];
			}
			//testing...
//			System.out.println("receive clusters" + Arrays.toString(tempClusters)+ "from " + slaveRank);
		}
		return clusters;
	}

	private String[] recalCen() {
		for(int cluster = 0; cluster < numCluster; cluster++) {
			StringBuilder newCentroid = new StringBuilder();
			
			char [] bases = {'A','C','T','G'};
			for(int pos = 0; pos < dnaLength; pos++){
				int index = -1;
				int [] temp = sum[cluster][pos];
				int max = Integer.MIN_VALUE;
				for(int i = 0; i < 4; i++){
					if(temp[i] > max) {
						max = temp[i];
						index = i;
					}
				}
				newCentroid.append(bases[index]);
			}
			centroids[cluster] = new String(newCentroid);
		}
		System.out.println("Master cal new centroids " + Arrays.toString(centroids));
		return centroids;
	}

	private void compute() {
		//init data
		for(int i = 0 ;i < dnaList.size();i++){
			resultCluster[i] = -1;
		}
		System.out.println("Rank " + myRank + " compute result:");
		for (int i = 0; i < numStrandsSlave; i++) {
			int dif = Integer.MAX_VALUE;
			int cluster = -1;
			for (int j = 0; j < numCluster; j++) {
				//compare strand with centroid one by one
				int tempdif = calDif(dnaList.get((myRank - 1) * numStrandsSlave + i),centroids[j]);
				if(tempdif < dif ){
					dif = tempdif;
					cluster = j;
				}else {
					continue;
				}
			}
			resultCluster[(myRank - 1) * numStrandsSlave + i] = cluster;
			resultDif[(myRank - 1) * numStrandsSlave + i] = dif;
		}
			
			System.out.println(myRank+Arrays.toString(resultDif));
			System.out.println(myRank+Arrays.toString(resultCluster));
			
			String[] tempCluster = new String[dnaList.size()];
			map = new HashMap<Integer,ArrayList<String>>();
			for (int count = 0; count < numCluster; count++) {
				map.put(count,new ArrayList<String>());
			}
			for (int count = 0; count < numStrandsSlave; count++) {
				map.get(resultCluster[count]).add(dnaList.get((myRank - 1) * numStrandsSlave + count));
			}//now we have arraylists of each cluster
			
			for (int count = 0; count < numCluster; count++) {
				for (int j = 0 ; j < dnaLength; j++) {
					int [] frequence = new int[4];
					for (String strand : map.get(count)) {
						char cur = strand.charAt(j);
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
					
					sum[count][j] = frequence;
//					for (int k = 0; k < 4; k++ ) {
//						sum[count][j][k] = frequence[k]; 	
//					} 
					//testing...
					
					System.out.println("Slave "+myRank+" frequence : " + count+"," + j +Arrays.toString(frequence));
				}
			}
			
		
		
	}

	private int calDif(String src, String dest) {
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

	public String[]	initCentroids(){
		String [] centroids = new String[this.numCluster];
		ArrayList<Integer> randomPool = generateRandom(this.numCluster, this.dnaList.size());
		int n = 0;
		for(int i : randomPool) {
			if(n < numCluster){
				centroids[n] = dnaList.get(i).toString();
				n++;
			} else {
				break;
			}
		}
		return centroids;
	}

	private ArrayList<Integer> generateRandom(int numCluster, int listSize) {
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
}
