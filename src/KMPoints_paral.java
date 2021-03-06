import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import mpi.*;

public class KMPoints_paral {
	public String outFile;
	public ArrayList<Point>dataset;
	public double[] xSum;
	public double[] ySum;
	public double[] xSumNew;
	public double[] ySumNew;
	public int numInter;
	public int myRank;
	public int size;
	public Point[]	centroids;
	public Point[]	newCentroids;
	public int numOfClusters;
	public int[] clusterSize;
	public int[] newClusterSize;
	public int [] pointsToCentroids;
	public int [] pointsToIndex;
	
	public KMPoints_paral(ArrayList<Point> dataset, String output,
			int numOfClusters) throws MPIException{
		
//		System.out.println("Rank "+myRank+" Start...");
		
		this.myRank = MPI.COMM_WORLD.Rank();
		this.size = MPI.COMM_WORLD.Size();
		
		this.numInter = 500;
		this.numOfClusters = numOfClusters;
		this.xSum = new double[numOfClusters];
		this.ySum = new double[numOfClusters];
		this.xSumNew = new double[numOfClusters];
		this.ySumNew = new double[numOfClusters];
		
		this.centroids = new Point[numOfClusters];
		this.newCentroids = new Point[numOfClusters];
		this.clusterSize  = new int[numOfClusters];
		this.newClusterSize  = new int[numOfClusters];
		this.pointsToCentroids = new int[dataset.size() / size];
		this.pointsToIndex = new int[dataset.size() / size];
		this.dataset = dataset;
		
		this.outFile = output;
		
		
		
		//System.out.println("Rank : " + myRank + "is working...");
		
		for(int start = 0; start < numInter; start++ ){
			
			//master
			if(myRank == 0){
				
				if(start==0) centroids = initCentroids();//first time
				else {//recalCen
					centroids = recalCen();
				}
				
				//send centriod to every slave
				for (int slaveRank = 1; slaveRank < size ; slaveRank++) {
					//System.out.println("Send centroids " + Arrays.toString(centroids));
					for(Point p : centroids){
						//System.out.println(p.getX() + "\t" + p.getY());
					}
					MPI.COMM_WORLD.Send(centroids, 0, numOfClusters, MPI.OBJECT, slaveRank, 99);
				}
			} 
			
			//slave
			else {
				MPI.COMM_WORLD.Recv(centroids, 0, numOfClusters, MPI.OBJECT, 0, 99); //others has the same centroids
				compute();
			}
			//this is where all should work the same way...
			//all reduce
			//System.out.println("Start all reduce!!!  " + myRank);
			
			xSumNew = new double [xSum.length];
			ySumNew = new double [ySum.length];
			
			MPI.COMM_WORLD.Allreduce(xSum, 0, xSumNew, 0, xSum.length, MPI.DOUBLE, MPI.SUM);
			MPI.COMM_WORLD.Allreduce(ySum, 0, ySumNew, 0, ySum.length, MPI.DOUBLE, MPI.SUM);
			MPI.COMM_WORLD.Allreduce(clusterSize, 0, newClusterSize, 0, clusterSize.length, MPI.INT, MPI.SUM);
			
			//System.out.println("xSum : " + Arrays.toString(xSum));
			//System.out.println("ySum : " + Arrays.toString(ySum));
			
			//System.out.println("xSumNew : " + Arrays.toString(xSumNew));
			//System.out.println("ySumNew : " + Arrays.toString(ySumNew));
			//System.out.println("clusterSizeNew : " + Arrays.toString(newClusterSize));
			
			//calculate the difference between the old centroids and the new centroids
			//..........
		}
		
		//finish...
		
		//System.out.println("Program " + myRank + "ends");
		if(myRank != 0) {
			MPI.COMM_WORLD.Send(pointsToCentroids, 0, pointsToCentroids.length, MPI.INT, 0, 0);
		} else {
			int clusters [] = glue();
			//System.out.println("final clusters are : "+Arrays.toString(clusters));
			writeFile(dataset, clusters, outFile);
			System.out.println("Checkout ../output/point_paral.csv for result");
		}
		
		
	}
	private void writeFile(ArrayList<Point> dataset, int[] clusters,
			String outFile) {
		try {
			PrintWriter writer = new PrintWriter(new File(outFile));
			//System.out.println(outFile);
			for(int i = 0; i < dataset.size(); i++) {
				writer.println(dataset.get(i).getX() + ","+dataset.get(i).getY() + "," + clusters[i]);
				//System.out.println(dataset.get(i).getX() + ","+dataset.get(i).getY()  + "," + clusters[i]);
				writer.flush();
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	private int[] glue() throws MPIException {
		int [] clusters = new int[dataset.size()];
		for(int slaveRank = 1; slaveRank < size; slaveRank++){
			int[] tempClusters = new int[dataset.size() / (size-1)];
			MPI.COMM_WORLD.Recv(tempClusters, 0, tempClusters.length, MPI.INT,slaveRank, 0);
			for(int i = 0 ; i < tempClusters.length;i++){
				clusters[(slaveRank-1)*size + i] = tempClusters[i];
			}
			//testing...
			//System.out.println("receive clusters" + Arrays.toString(tempClusters)+ "from " + slaveRank);
		}
		return clusters;
	}
	private void compute() {
		pointsToCentroids = new int[dataset.size() / (size -1)];
		pointsToIndex = new int[dataset.size() / (size - 1)];
		//calculate the nearest centroid for every point
		int start = dataset.size() / (size-1) * (myRank - 1);
		int end = dataset.size() / (size-1) * myRank;
		//System.out.println("start "  + start);
		//System.out.println("end " + end);
		
		for (int j = start; j < end; j++){
			pointsToIndex[j - start] = j;
			pointsToCentroids[j - start] = getNearestCentroid(dataset.get(j));
		}
		
		xSum = new double[numOfClusters];
		ySum = new double[numOfClusters];
		clusterSize = new int[numOfClusters];

		//System.out.println("pointstocentroids: "+Arrays.toString(pointsToCentroids));
		//System.out.println("pointstoindex: "+Arrays.toString(pointsToIndex));
		
		for(int j = 0; j < pointsToIndex.length; j++) {
			
			int cluster = pointsToCentroids[j];
			//System.out.print("j: \t"+j + "\t"+"cluster:\t"+cluster + "\t");
			xSum[cluster] += dataset.get(pointsToIndex[j]).getX();
			ySum[cluster] += dataset.get(pointsToIndex[j]).getY();
			
			
			clusterSize[cluster]++;
		}
		//System.out.println("xSum"+Arrays.toString(xSum));

		//System.out.println("ySum"+Arrays.toString(ySum));
		
		
		
		
	}
	private int getNearestCentroid(Point point) {
		int cluster = -1;
//		int len = this.centroids.length;
		double minDis = Double.MAX_VALUE;
		double distance;
		
		for (int i = 0; i < numOfClusters; i++) {
			distance = Math.pow(Math.abs(point.getX() - centroids[i].getX()), 2)
			+ Math.pow(Math.abs(point.getY() - centroids[i].getY()), 2);
			
			//System.out.println("minDis: " + minDis + "\t" + "dis: " + distance);
			
			if (distance < minDis) {
				minDis = distance;
				cluster = i;
			}
		}
		//System.out.println("cluster: " + cluster);
		return cluster;
	}
	private Point[] recalCen() {
		Point[]	newCentroids = new Point[numOfClusters];
		for(int j = 0; j < numOfClusters; j++){
			newCentroids[j] = new Point(xSumNew[j] / newClusterSize[j], ySumNew[j]
					/ newClusterSize[j]);
		}
		
		return newCentroids;
	}
	private Point[] initCentroids() {
		Point [] centroids = new Point[numOfClusters];
		Random r  = new Random();
		ArrayList<Integer> randomPool = new ArrayList<Integer>();
		while(randomPool.size() < numOfClusters) {
			int random = r.nextInt(dataset.size());
			if(!randomPool.contains(random)){
				randomPool.add(random);
			}
		}
		int n = 0;
		for(int i : randomPool) {
			if(n < numOfClusters){
				centroids[n] = dataset.get(i);
				n++;
			} else {
				break;
			}
		}
		return centroids;
	}
}
