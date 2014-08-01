import java.util.ArrayList;
import java.util.Random;

import mpi.MPI;
import mpi.MPIException;


public class KMPoints {
//	double minX = Double.MAX_VALUE;
//	double maxX = Double.MIN_VALUE;
//	double minY = Double.MAX_VALUE;
//	double maxY = Double.MIN_VALUE;
	int interactions = 10;
	Point[] centroids = null;
	ArrayList<Point>[] clusters = null;
	int numOfPointsPerCluster = 0;
	Point[] oneCentroid = new Point[1];
	ArrayList<Point>[] oneCluster = new ArrayList[1];
	
	public KMPoints(ArrayList<Point> dataset, String output,
			int numOfClusters) throws MPIException {
		
		//1. generate random centroids
		centroids = new Point[numOfClusters];
		clusters = new ArrayList[numOfClusters];
		Random r  = new Random();
		for (int i = 0; i < numOfClusters; i++) {
			centroids[i] = dataset.get(r.nextInt(dataset.size())); 
		}
		
		//divide the dataset into different clusters
		numOfPointsPerCluster = dataset.size() / numOfClusters;
//		int i = 0;
//		while (i < dataset.size()){
//			for (int j = 0; j < numOfPointsPerCluster; j++) {
//				
//			}
//		}
		int rank;
		int size;
		int senderRank;
		int[] pointsToCentroids = new int[numOfPointsPerCluster];
		rank = MPI.COMM_WORLD.Rank();
		size = MPI.COMM_WORLD.Size();
		
		for (int i = 0; i < interactions; i++) {
			//Slave process
			if (rank > 0) {
				MPI.COMM_WORLD.Recv(centroids, 0, numOfClusters, MPI.OBJECT, 0, 0);
				
				//calculate the nearest centroid for every point
				for(int j = 0; j < numOfPointsPerCluster; j++) {
					//the index of point = dataset.size() / size * (rank - 1)
					pointsToCentroids[j] = getNearestCentroid(dataset.get(dataset.size() / size * (rank - 1)));
				}
				
				MPI.COMM_WORLD.Send(pointsToCentroids, 0, numOfPointsPerCluster, MPI.INT, 0, 100);
				
				MPI.COMM_WORLD.Recv(oneCluster, 0, 1, MPI.OBJECT, 0, 1);
				oneCentroid[0] = recalculateCentroid(oneCluster[0]);
				MPI.COMM_WORLD.Send(oneCentroid, 0, 1, MPI.OBJECT, 0, 99);
			}
			
			//Master process
			else{
				//send centroids to every slave
				for (int k = 1; k < size; k++) {
					MPI.COMM_WORLD.Send(centroids, 0, numOfClusters, MPI.OBJECT, k, 0);
					
				}
			}
		}
		
	
		
	}

	private Point recalculateCentroid(ArrayList<Point> arrayList) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNearestCentroid(Point point) {
		int index = -1;
		int len = this.centroids.length;
		double minDis = Double.MAX_VALUE;
		double distance;
		
		for (int i = 0; i < len; i++) {
			distance = Math.pow((point.getX() - centroids[i].getX()), 2)
			+ Math.pow((point.getY() - centroids[i].getY()), 2);
			if (distance < minDis) {
				minDis = distance;
				index = i;
			}
		}
		return index;
	}

}
