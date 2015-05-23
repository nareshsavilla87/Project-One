

import java.util.ArrayList;
import java.util.List;


public class GenerateSign {
    
    public static void main(String[] args)
    {
    	try{
    	List<String> strings = new ArrayList<String>();
    	strings.add("A");
    	strings.add("B");
    	strings.add("C");
    	
    	List<String> strings2=new ArrayList<String>(strings);
    	//strings.clear();
    	System.out.println(strings2);
    	strings2.remove("C");
    	strings.add("D");
    	System.exit(0); 
    	System.out.println(strings);
    	}finally{
    		System.out.println("finally");
    	}
    }
    
   
} 

