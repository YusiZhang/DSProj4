import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


public class ReadCSV {
	public ReadCSV(String file, String opt) {
		this.file = file;
		this.opt = opt;
	}
	
	public ArrayList read() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(file));    
		
		String line = "";
		ArrayList list = new ArrayList();
		if(opt.equals("point")){
			
			while((line = reader.readLine()) != null) {
				String[] values = line.split(",");
				Point p = new Point(Double.parseDouble(values[0]),Double.parseDouble(values[1]));
				list.add(p);
			}
			
		} else if(opt.equals("dna")){
			
			while((line = reader.readLine()) != null) {
				String dna = line;
				list.add(dna);
			}
			
		} else {
			System.out.println("wrong opt !!!");
			return null;
		}
		return list;
	}
	
	private String file;
	private String opt;
	
//	public static void main(String[] args) {
//		try {
//			ArrayList list = new ReadCSV("./input/dna.csv", "dna").read();
//			Iterator it = list.iterator();
//			while(it.hasNext()) {
//				System.out.println(it.next().toString());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
