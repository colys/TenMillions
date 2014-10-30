package com.colys.tenmillion;

import java.io.File;
import java.io.FileOutputStream; 
import java.io.InputStream; 
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date; 
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
 
import com.colys.tenmillion.Entity.ConfigItem;
import com.colys.tenmillion.Entity.User;
  
import DataAccess.BasicAccess;
import DataAccess.DefaultAccess;
import android.annotation.SuppressLint;  
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Message; 

@SuppressLint("SimpleDateFormat")
public class Utility {
	 
	public static boolean UseLocal = true;
	
	
	
	@SuppressLint("SimpleDateFormat")
	public static String GetNowString(){
		return GetNowString("yyyy-MM-dd");
	}
	
	public static boolean StringEquals(String s1,String s2){
		if(s1 ==null && s2 ==null) return true;
		if(s1 == null) return false;
		return s1.equals(s2);
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String GetNowString(String format){
		Date queryDate =new Date();
		 SimpleDateFormat sdf =new SimpleDateFormat(format);
		 return sdf.format(queryDate);
	}
	
	public static Calendar ConvertToCalendar(String dateStr){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		Calendar c = Calendar.getInstance() ;
		try {
			date = sdf.parse(dateStr);
			c.setTime(date);
		} catch (ParseException e) {			
			e.printStackTrace();
		}
		return c;
	}
	
	public static String CalendarToString(Calendar cal){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(cal.getTime());
		 
	}
	
	public static String CalendarToString(Calendar cal,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(cal.getTime());
		 
	}
	@SuppressLint("SimpleDateFormat")
	public static int getDateDays(Calendar date1, Calendar date2) {
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int days = 0;
		try {
			Date date = date1.getTime();// 通过日期格式的parse()方法将字符串转换成日期
			Date dateBegin = date2.getTime();
			// System.out.println("getDateDays:date" + date);
			// ;
			// System.out.println("dateBegin" + dateBegin);
			;
			long betweenTime = date.getTime() - dateBegin.getTime();
			days = (int) (betweenTime / 1000 / 60 / 60 / 24);
		} catch (Exception e) {
			// System.out.println(e.toString());
		}
		// System.out.println("day==" + days);
		return days;
	}
	
	
	@SuppressLint("SimpleDateFormat")
	public static int getDateDays(String date1, String date2) {
	        date1 = date1.split(" ")[0];
	        date2 = date2.split(" ")[0];
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        int days = 0;
	        try {
	                Date date = sdf.parse(date1);// 通过日期格式的parse()方法将字符串转换成日期
	                Date dateBegin = sdf.parse(date2);
	                // System.out.println("getDateDays:date" + date);
	                // ;
	                // System.out.println("dateBegin" + dateBegin);
	                ;
	                long betweenTime = date.getTime() - dateBegin.getTime();
	                days = (int) (betweenTime / 1000 / 60 / 60 / 24);
	        } catch (Exception e) {
	                // System.out.println(e.toString());
	        }
	        // System.out.println("day==" + days);
	        return days;
	}
	public static int SubStringDateDay(String strDate){
		String strday = strDate.substring(8,10);
		if(strday.charAt(0)=='0'){
			return Integer.valueOf(strday.substring(1));
		}else return Integer.valueOf(strday);
	}
	public static int getDays(String date1) {
        date1 = date1.split(" ")[0];
       
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int days = 0;
        try {
                Date date = new Date();
                Date dateBegin = sdf.parse(date1);// 通过日期格式的parse()方法将字符串转换成日期
                // System.out.println("getDateDays:date" + date);
                // ;
                // System.out.println("dateBegin" + dateBegin);
                ;
                long betweenTime = date.getTime() - dateBegin.getTime();
                days = (int) (betweenTime / 1000 / 60 / 60 / 24);
        } catch (Exception e) {
                // System.out.println(e.toString());
        }
        // System.out.println("day==" + days);
        return days;
}
	
	static HanyuPinyinOutputFormat hanyuPinyin;
	
	
	public static String GetPinYin(String str){
		String strResult = "";
		for(int i=0;i< str.length();i++){
			char hanzi  = str.charAt(i);
			if(hanyuPinyin == null){
				hanyuPinyin = new HanyuPinyinOutputFormat();
		        hanyuPinyin.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		        hanyuPinyin.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		        hanyuPinyin.setVCharType(HanyuPinyinVCharType.WITH_V);
			}
	        String[] pinyinArray=null;
	        try {
	            //是否在汉字范围内
	            if(hanzi>=0x4e00 && hanzi<=0x9fa5){
	                pinyinArray = PinyinHelper.toHanyuPinyinStringArray(hanzi, hanyuPinyin);
	            }
	        } catch (BadHanyuPinyinOutputFormatCombination e) {
	            e.printStackTrace();
	        }
	        //将获取到的拼音返回
	        if(pinyinArray!=null && pinyinArray.length > 0){
	        if(pinyinArray[0].length() > 1)
	        	strResult += pinyinArray[0].charAt(0);
	        else
	        	strResult += pinyinArray[0];
	        }else strResult +=hanzi;
		}
		return strResult;
	}
	
	/*static boolean HasAnyConnection = false;
	static boolean IsWifi =false;
	
	// 检查网络
     public static void CheckNetwork(Context icontext){
    	HasAnyConnection = false;
    	IsWifi =false;
        Context context = icontext.getApplicationContext();    
        ConnectivityManager connectivity = (ConnectivityManager) context    
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info;
        if (connectivity != null) {    
            info = connectivity.getAllNetworkInfo();    
            if (info != null) {    
                for (int i = 0; i < info.length; i++) {    
                	if( info[i].isConnected()){
                		HasAnyConnection = true;
                		if (info[i].getTypeName().equals("WIFI") ) IsWifi = true;   
                	}
                }    
            }    
        }    

    }*/
	
	public static boolean HasConnection(Context icontext){
    	boolean HasAnyConnection = false;   
        ConnectivityManager connectivity = (ConnectivityManager) icontext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info;
        if (connectivity != null) {    
            info = connectivity.getAllNetworkInfo();    
            if (info != null) {    
                for (int i = 0; i < info.length; i++) {    
                	if( info[i].isConnected()){
                		HasAnyConnection = true;
                		//if (info[i].getTypeName().equals("WIFI") ) IsWifi = true;   
                	}
                }    
            }    
        }
        return HasAnyConnection;
	}

    public static NetworkInfo GetAliveNetwork(Context icontext){
        Context context = icontext.getApplicationContext();    
        ConnectivityManager connectivity = (ConnectivityManager) context    
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info;
        if (connectivity != null) {    
            info = connectivity.getAllNetworkInfo();    
            if (info != null) {    
                for (int i = 0; i < info.length; i++) {    
                    if (info[i].isConnected()) {    
                        return info[i];    
                    }    
                }    
            }    
        }    
        return null;   
    }
    
    
    //求百分比
    public static String GetPercent(int y,int z){
    	   String baifenbi="";//接受百分比的值
    	   double baiy=y*1.0;
    	   double baiz=z*1.0;
    	   double fen=baiy/baiz;
    	   //NumberFormat nf   =   NumberFormat.getPercentInstance();     注释掉的也是一种方法
    	   //nf.setMinimumFractionDigits( 2 );        保留到小数点后几位
    	   DecimalFormat df1 = new DecimalFormat("##.00%");    //##.00%   百分比格式，后面不足2位的用0补齐
    	    //baifenbi=nf.format(fen);   
    	   baifenbi= df1.format(fen);  
    	    return baifenbi;
    	}
    
    
    public static String GetSDCardPath(){ 
        File sdDir = null; 
        boolean sdCardExist = Environment.getExternalStorageState()   
                            .equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在 
        if   (sdCardExist)   
        {                               
          sdDir = Environment.getExternalStorageDirectory();//获取跟目录 
       }   
        return sdDir.toString(); 
    }
    
    
    public static final int DOWN_START =100000001;
    public static final int DOWN_POSITION =100000002;
    public static final int DOWN_COMPLETE =100000003;
    public static final int Down_ERROR  =100000004;
    public static final int Sync_Content_To_Server  =9999;
    public static final int Sync_Server_Content = 9998;
    public static final int Sync_Query_Token = 9997;
    public static final int Sync_Query_APK_Version = 9996;
    public static final int PopWin_Daywork_Edit = 9995;
    
    public static void  DownFile(String url, String path, String fileName,Handler downloadHandler ) {
    	downloadHandler.sendEmptyMessage(DOWN_START);    	
    		 URL Url;
			try {
				Url = new URL(url);			
	    		URLConnection conn = Url.openConnection();
	    		conn.connect();
	    		InputStream is = conn.getInputStream();
	    		int fileSize = conn.getContentLength();// 根据响应获取文件大小
	    		if (fileSize <= 10) { // 获取内容长度为0	    			
	    			throw new RuntimeException("无法获知文件大小 ");    			
	    		}
	    		if (is == null) { // 没有下载流
	    			throw new RuntimeException("无法获取文件");	    			
	    		}
	    		
	    		FileOutputStream FOS = new FileOutputStream(path +"/" + fileName); // 创建写入文件内存流，
	    		
	    		//通过此流向目标写文件
	    		byte buf[] = new byte[1024];
	    		int downLoadFilePosition = 0;
	    		int numread;
	
	    		while ((numread = is.read(buf)) != -1) {
		    		FOS.write(buf, 0, numread);
		    		downLoadFilePosition += numread ; 
		    		downloadHandler.sendMessage(Message.obtain(downloadHandler, DOWN_POSITION, downLoadFilePosition,fileSize));
	    		}
	    		FOS.close();
	    		is.close();
	    		downloadHandler.sendEmptyMessage(DOWN_COMPLETE);
			}catch(Exception e){
				downloadHandler.sendMessage(Message.obtain(downloadHandler,Down_ERROR,e.getMessage()));
				e.printStackTrace();
			}
    		
    }
    
    public static int parseInt(String str){
    	if(str==null|| str.length() ==0 || str.isEmpty()) return 0;
    	 return Integer.parseInt(str);    	
    }
    
    
    
    public static void LogConfigs(BasicAccess access, MyApplication mApp) throws Exception{
    	
    	boolean inTrans = access.InTrans;
    	if(!inTrans){
    		access.Close(true);
    		access.OpenTransConnect();
    	}
    	access.Visit(DefaultAccess.class).ExecuteNonQuery("insert into configs(key) select '"+ ConfigItem.Last_Account_Json +"' where not exists(select 1 from configs where key ='"+ ConfigItem.Last_Account_Json+"');");
		//access.Visit(DefaultAccess.class).ExecuteNonQuery("insert into configs(key) select '"+ ConfigItem.Last_Account_Password +"' where not exists(select 1 from configs where key ='"+ ConfigItem.Last_Account_Password+"');");
		access.Visit(DefaultAccess.class).ExecuteNonQuery("insert into configs(key) select '"+ ConfigItem.Last_Account_group +"' where not exists(select 1 from configs where key ='"+ ConfigItem.Last_Account_group+"');");
		access.Visit(DefaultAccess.class).ExecuteNonQuery("insert into configs(key) select '"+ ConfigItem.Synch_Token +"' where not exists(select 1 from configs where key ='"+ ConfigItem.Synch_Token+"');");
    	//access.Visit(DefaultAccess.class).ExecuteNonQuery("insert into configs(key) select '"+ ConfigItem.Last_Account_user +"' where not exists(select 1 from configs where key ='"+ ConfigItem.Last_Account_user+"');");
    	access.Visit(DefaultAccess.class).ExecuteNonQuery("update configs set value ='"+  mApp.getCurrentUser().ToJson() +"' where key='"+ ConfigItem.Last_Account_Json +"'");
    	//access.Visit(DefaultAccess.class).ExecuteNonQuery("update configs set value ='"+ Utility.CurrentUser.ID +"' where key='"+ ConfigItem.Last_Account_user +"'");
    	access.Visit(DefaultAccess.class).ExecuteNonQuery("update configs set value ='"+ mApp.getCurrentGroupID() +"' where key='"+ ConfigItem.Last_Account_group +"'");
    	//access.Visit(DefaultAccess.class).ExecuteNonQuery("update configs set value ='"+ Utility.CurrentUser.Password +"' where key='"+ ConfigItem.Last_Account_Password +"'");
    	access.Visit(DefaultAccess.class).ExecuteNonQuery("update configs set value ='"+ mApp.getCurrentUser().SyncToken +"' where key='"+ ConfigItem.Synch_Token +"'");
    	if(!inTrans)access.Close(true);
    }
	
    
    public static String getVersionName(Context context)
    {
            // 获取packagemanager的实例
            PackageManager packageManager =context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo;
            String version = null;
			try {
				packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
				version = packInfo.versionName;
			} catch (NameNotFoundException e) {				
				e.printStackTrace();
			}
            
            return version;
    }
   

}
