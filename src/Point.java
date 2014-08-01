import java.io.Serializable;

public class Point implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -730436252048376483L;
	public double x;
    public double y;
    public Point(double x, double y) {
    	this.x = x;
    	this.y = y;
    }
    
    public Point(){
    	
    }
}
