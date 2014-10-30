package CustomViews;

import java.lang.reflect.Type;
import java.util.LinkedList;
 
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SynContent
{
	public static final int Add = 0,Delete=2,Update=1;
	public String Table;
	public int Action;
	public String[] Fields;
	public String[] Values;
	public String Where;
	public void SetFieldCount(int count){
		Fields =new String[count];
		Values=new String[count];
	}
	
	public String ToJson(){
		Gson gson=new Gson();
		return gson.toJson(this, SynContent.class);
	}
	

public static LinkedList<SynContent> ListFromJson(String json) {
       Type listType = new TypeToken<LinkedList<SynContent>>(){}.getType();
       Gson gson=new Gson();
       return gson.fromJson(json, listType);
}

}
