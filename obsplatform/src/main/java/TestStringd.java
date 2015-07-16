
public class TestStringd {

	
	public static void main(String []arg) {
		
	String str="select a.id as clientId from m_client a, b_orders b where a.id=b.client_id and b.order_status  = 1 and (b.next_billable_day is null or b.next_billable_day<=Now())";
	if(str.toLowerCase().matches("now()".toLowerCase())){
		str=str.replace("now()", "hi");
	}
	System.out.println(str);
	}


}
