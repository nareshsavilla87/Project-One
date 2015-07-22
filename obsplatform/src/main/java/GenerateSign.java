

public class GenerateSign {
	
	static char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	
	public static void main(String []arg) {
		
	String str="select a.id as clientId from m_client a, b_orders b where a.id=b.client_id and b.order_status  = 1 and (b.next_billable_day is null or b.next_billable_day<=Now())";
	if(str.contains("now()")){
		str=str.replace("now()", "hi");
	}
	System.out.println(str);
	}
	
}


