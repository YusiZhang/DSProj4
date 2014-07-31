import mpi.*;
public class MPIHello {
	public static void main(String[] args) throws MPIException{
		MPI.Init(args);
		int myRank = MPI.COMM_WORLD.Rank();
		System.out.println("My rank is : " + myRank);
		if(myRank == 0) {
			char[] message = "Hello,there".toCharArray();
			MPI.COMM_WORLD.Send(message,0,message.length,MPI.CHAR,1,99);
		}else {
			char [] message = new char [20];
			//message : buf | 0 : offset | 20 : count | MPI.CHAR : datatype | 0 : source | 99 : tag 
			MPI.COMM_WORLD.Recv(message, 0, 20, MPI.CHAR, 0, 99);
			System.out.println("Rank: "+myRank+" received:" + new String(message) + ":");
	
		}	
		System.out.println("Size is" + MPI.COMM_WORLD.Size());
		MPI.Finalize();
	
	}
}




