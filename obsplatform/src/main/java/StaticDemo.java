/**
 *
 * Java program which demonstrate that we can not override static method in Java.
 * Had Static method can be overridden, with Super class type and sub class object
 * static method from sub class would be called in our example, which is not the case.
 * @author
 */
public class StaticDemo {
 
    public static void main(String args[]) {
     try{
   int i=1;
   int j=0;
   int k =i/j;
   System.out.println(k);
     }catch(ArithmeticException exception){
    	 System.out.println("Pl.....enter valid num");
    	// exception.printStackTrace();
    	 
     }
     
     String s="Ranjith";
     String rev="";
     for(int i=s.length()-1; i>=0; i--){
    	 rev +=s.charAt(i);
     }
    System.out.println(rev);

    } 
 
}

