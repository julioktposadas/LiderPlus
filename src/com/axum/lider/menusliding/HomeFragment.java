package com.axum.lider.menusliding;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.axum.config.Db;
import com.axum.liderplus.Avance;
import com.axum.liderplus.FormAvance;
import com.axum.liderplus.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class HomeFragment extends Fragment {


	 private static Context context;
	
	 String data1="";
	 //Spinner spinTema ;
	 int b=0;
	 WebView myWebView ;
	
	 private LayoutInflater mInflater;
	 private View mCustomView;
	 private TextView mTitleTextView;
	 String vend="",sup="";
	 ProgressBar pb;
	 double max=0;
	String[] dataItem;
   LinearLayout ll,lg ;	
   int index=1;
   Db db ;
   DecimalFormat df = new DecimalFormat("#");
	
	public HomeFragment(Context c, String data, String v, int i){
		context =c;	
		data1 = data;
		db= new Db(context);
	
		vend=v;
		index=i;
	}
	
	@SuppressLint("NewApi")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.form_html_avance, container, false);
        
	    myWebView = (WebView) rootView.findViewById(R.id.webResumen);
	   
	    myWebView.getSettings().setBuiltInZoomControls(true);
	    myWebView.getSettings().setDisplayZoomControls(false);
	    myWebView.setWebViewClient(new WebViewClient());
	    myWebView.clearCache(true);
		
	    myWebView.getSettings().setJavaScriptEnabled(true);
	    myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
	    myWebView.getSettings().setAllowFileAccessFromFileURLs(true);
	    myWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
	    myWebView.getSettings().setDomStorageEnabled(true);
	    myWebView.getSettings().setSupportZoom(true);

		 

	  //  myWebView.loadDataWithBaseURL(null, getHtml1(data1), "text/html","utf-8", null);
	//	pb = (ProgressBar) rootView.findViewById(R.id.progressCarga);
	
	   myWebView.loadDataWithBaseURL("file:///android_res/raw/", getHtml(data1), "text/html", "utf-8", null);
			
			// Log.w("DATO:", data1);
	 //--------------------------------------------------
		// myWebView.getSettings().setJavaScriptEnabled(true);
		//myWebView.setWebChromeClient(new WebChromeClient());
	
	 return rootView;
    }
	
	public  String getHtml1(String data){
		
		StringBuffer sf = new StringBuffer().append("");
		
			sf.append("<html ><body >");
			
			for(int f=index;f<120;f++){
				
		
			 sf.append(f+"</br>");
			
			}
			sf.append("</html ></body >");
			return ( sf.toString() );	
		}
	
	
	 
public  String getHtml(String data){
	double d =0;

	String[] s;String color="";
	// Log.w("DATO:",data+"");
	StringBuffer sf = new StringBuffer().append("");
	String[] separated = data.split(",");
	
	 Log.w("DATO:", separated.length+"");
	 Log.w("DATO:", separated[2]+"");
	
	if(separated.length>1){
		sf.append("<html ><body bgcolor=\"#FFFFFF\"> <font size=\"-1\" ><table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\" bgcolor=\"#8DA8CB\">");
		
		for(int f=index;f<index+1;f++){
			
		 String[] fila = separated[f].split("\n");
		 
	
			 for(int i=0; i<fila.length;i++){
				// Log.w("DATO:", fila[i]);
			 if(i==0){
				      if(fila[0].indexOf(";")>0) s = fila[0].split(";");
				      else{
				    	 s = fila[0].split(" ");
				     }
					sf.append(" <tr bgcolor='#6495ED' height='5' > <td colspan='5'></td> </tr>");  
					sf.append(" <tr bgcolor='#F0F0F0' > <td colspan='5'><center>"+s[0].toUpperCase()+"</center></td> </tr>");
					sf.append(" <tr bgcolor='#8DA8CB' > <td width='75%'><font color='#FFFFFF'>"+s[0]+"</font></td>");
				    sf.append(" <td><font color='#FFFFFF'>Ult</font></td>  <td><font color='#FFFFFF'>Hoy</font></td>  <td><font color='#FFFFFF'>%</font></td> </tr>"); 	 
				 }
				 else{
					
					 String[] info = fila[i].split(";"); 
					 d=0;
					
					 if(isNumeric(info[0].trim())) info[0]=getArticulo(info[0]);
					  if(Double.parseDouble((info[2].split("/")[0]))!=0)
					 d =((Double.parseDouble((info[2].split("/")[0]))/Double.parseDouble(info[1].split("/")[0])-1)*100);
					if(d>0) color="verde.png"; if(d==0) color="amarillo.png";if(d<0) color="rojo.png";
					 sf.append(" <tr bgcolor='#FFFFFF' > <td width='85%'>"+info[0].toLowerCase()+"</td>");
					 sf.append(" <td><P ALIGN=right>"+info[1].split("/")[0]+"</td> <td><P ALIGN=right>"+info[2].split("/")[0]+"</td> <td><P ALIGN=right>"+(df.format(d))+"%</td> <td><center><img src='"+color+"' /></td></tr>");
					
					   
				 }
			 }
			
		 }
		 
			 sf.append("</table></font></body></html>");
		}
	
		return ( sf.toString() );	
	}	
public String getArticulo(String id_articulo)
{	
	 String ct=id_articulo.trim();
	db.abrirBasedatos();
	// Log.d("base error","select codigo,descripcion from articulos where codigo="+id_articulo.trim()+" " );
	Cursor localCursor = db.baseDatos.rawQuery("select codigo,descripcion from articulos where codigo="+id_articulo.trim()+" ", null);

	if(localCursor.getCount()>0){
	localCursor.moveToFirst();
	ct+="-"+localCursor.getString(1);
	}
	localCursor.close();
	db.baseDatos.close();
	return ct;
}

public static boolean isNumeric(String string) {
    return string.matches("^[-+]?\\d+(\\.\\d+)?$");
}

	

	

	//@Override
/*	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.avance, menu);
		return true;
	}
	*/


	

		
 public String getConfig(String p1){
				SharedPreferences prefs = context.getSharedPreferences("config",context.MODE_PRIVATE);
					 return  prefs.getString(p1, "");
			}
	
		
	
}
