import java.io.Serializable;


public class Point implements Serializable {
	
	private static final long serialVersionUID = 2799358754855503228L;
	private double x;
	private double y;
	
	public Point(double x, double y) {
		this.setX(x);
		this.setY(y);
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getX() {
		return x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getY() {
		return y;
	}

}
