package DataAccess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope; 
import org.xmlpull.v1.XmlPullParserException;

import com.colys.tenmillion.Utility;
 


public class WSHelper  {	
	
	 	public	static boolean IsInQuery ;
	
		final static String WSUrl="http://onemillion.apphb.com/WebServiceJson.asmx";
	  
	    private static String namespace = "http://tempuri.org/";
	    /*************************************
	     * 鑾峰彇web services鍐呭
	     * @param url
	     * @param params
	     * @return
	     * @throws IOException 
	     * @throws ClientProtocolException 
	     * @throws ServerException 
	     *************************************/
	    
	    public static String executeHttpPost(String method,List<BasicNameValuePair> params) throws ClientProtocolException, IOException, ServerException {

	    	 String uriAPI = "http://millions.sinaapp.com/?method="+method;  
	    	    /*建立HTTP Post连线*/  
	    	    HttpPost httpRequest =new HttpPost(uriAPI);  
	    	    //Post运作传送变数必须用NameValuePair[]阵列储存  
	    	    //传参数 服务端获取的方法为request.getParameter("name")  
	    	    IsInQuery = true;
    	   
	    	     //发出HTTP request  
	    	     httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));  
	    	     //取得HTTP response  
	    	     HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest);   
	    	     String strResult;
	    	     //若状态码为200 ok   
	    	     if(httpResponse.getStatusLine().getStatusCode()==200){  
		    	      //取出回应字串  
	    	    	 HttpEntity entity = httpResponse.getEntity();  
	    	         if (entity != null) {  
	    	        	 strResult=EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8);
	    	        	 if(strResult.length() > 1){
	    	        		 strResult = strResult.substring(1);
	    	        		 if(strResult.length() > 4 && strResult.substring(0, 4).equals("err:")){
	    	        			 throw new ServerException(strResult.substring(4));
	    	        		 }
	    	        	 }	    	        	 
	    	        	 else strResult ="";
	    	         }else   strResult=null; 
		    	      
	    	     }else{  
	    	    	 IsInQuery = false;
	    	    	 throw new ServerException("Error Response"+httpResponse.getStatusLine().toString());
	    	     }  
	    	  
	    	    IsInQuery = false;
	    	    return strResult;
	       }
	   
	    
	    
	    public static String GetResponse(String method,List<BasicNameValuePair> params) throws ServerException, ClientProtocolException, IOException{	         
	    		return executeHttpPost(method,params);
	    		//if(Utility.UseLocal) throw n
	    		/*ew ServerException("褰撳墠涓虹绾挎ā寮忥紝涓嶅厑璁歌闂綉缁�");
	    		if(!ConnectivityReceiver.hasConnection()) throw new ServerException("娌℃湁浠讳綍鐨勭綉缁滆繛鎺ワ紒");
	            String url = WSUrl;
	            SoapObject request = new SoapObject(namespace, method);
	            for(int i=0,len=params.size();i<len;i++){
	                request.addProperty(params.get(i).getName(), params.get(i).getValue());
	            }
	            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	            envelope.bodyOut =request;
	            envelope.dotNet = true;
	            envelope.setOutputSoapObject(request);  
	            MyAndroidHttpTransport transport = new MyAndroidHttpTransport(url);  	 
	            IsInQuery = true;
                // 璋冪敤WebService  
                try {
					transport.call(namespace + method, envelope);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new ServerException("IOErr:"+e.getMessage());
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new ServerException("XmlPullParserErr:"+e.getMessage());
				}  	          
	            //AndroidHttpTransport androidHttpTransport = new AndroidHttpTransport(url); 
	            //androidHttpTransport.call(namespace + method, envelope); 
	             
	            //return envelope.getResponse();
	            // 鑾峰彇杩斿洖鐨勬暟鎹� 
                if(envelope.bodyIn.getClass()==SoapObject.class){
                	 SoapObject object = (SoapObject) envelope.bodyIn;  
     	            // 鑾峰彇杩斿洖鐨勭粨鏋� 
                	 String result;
                	 if(object.getPropertyCount()>0)
                		 result = object.getProperty(0).toString();	
                	 else result="";
	     	          IsInQuery = false;
	     	          return result;
                	 
                }else{
                	IsInQuery = false;
                	throw new ServerException(envelope.bodyIn.toString());
                }*/
	                       
	      
	    }
}