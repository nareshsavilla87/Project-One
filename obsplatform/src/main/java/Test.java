import java.text.SimpleDateFormat;
import java.util.Date;

 
public class Test {
     
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
 
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        System.out.println(dateFormat.parse("26-03-2015"));
 
    }
}