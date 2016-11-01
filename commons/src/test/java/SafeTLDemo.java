import com.kii.beehive.portal.common.utils.SafeThreadLocal;

public class SafeTLDemo {

	static SafeThreadLocal<String> local1=SafeThreadLocal.getInstance();


	static SafeThreadLocal<String>  local2=SafeThreadLocal.getInstance();


	public static void setData(String data){
		local1.set(data);

		local2.set(data+"2");
	}

	public static  String getData(){
		return local1.get();
	}

	public static  String getData2(){
		return local2.get();
	}


}
