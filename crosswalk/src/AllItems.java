/**
 * 
 */


import java.io.Serializable;
import java.util.*;
/**
 * @author alex
 *
 */
public class AllItems implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public AllItems() {
		// TODO Auto-generated constructor stub
	}
	
	public Vector<String[]> readData() {
		Vector<String[]> vec = new Vector<String[]>();
		vec.add(new String[]{"fdgdfg","dfgdfg","hgjghjhk","jhkjlik"});
		vec.add(new String[]{"fdgdfg","dfgdfg","hgjghjhk","jhkjlik"});
		return vec;
		
		
	}
	
	

}
