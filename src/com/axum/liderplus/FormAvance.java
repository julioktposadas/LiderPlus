package com.axum.liderplus;




import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import com.axum.config.Db;
import com.axum.lider.menusliding.MainActivity;
import com.axum.liderplus.MenuPpal.DownloadFileAsync;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;


public class FormAvance extends Activity {
   Db db;
	 private static Context context;
	 Bundle bundle ;
	 String data="";
	 Spinner spinTema ;
	 String s1="";
	 TextView myTextview;
	 String cmd="B_SupervisorDos";
	 LinearLayout layout1,layout2,layout3;
	 double max=0;
	 
	String html0="<html> <head>  <script type='text/javascript' src='file:///android_res/raw/raphael.min.js'> </script><script type='text/javascript' src='file:///android_res/raw/justgage.min.js'></script><script>";
	String htm="<body> ";
	String[] dataItem;
	String pie="</body></html>";
	LinearLayout ll,lg ;
	
	ArrayList<HashMap<String, String>> menuItems ;
	SimpleAdapter adapter;
	ListView lv ;

	ProgressBar progressBar,pb;
	String URL="";
	@SuppressLint("NewApi")
protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db= new Db(this);
	setContentView(R.layout.menu_lista2);
	getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
	getActionBar().setHomeButtonEnabled(true); 
	getActionBar().setDisplayHomeAsUpEnabled(true);
	this.setTitle("Ventas Diarias");
	 bundle = getIntent().getExtras();
	  data=bundle.getString("data");
	pb = (ProgressBar) findViewById(R.id.progressCargando);
	menuItems = new ArrayList<HashMap<String, String>>();
	//-------------------------------------------------

	
adapter = new SimpleAdapter(this, menuItems,
            R.layout.row_avance, new String[] {
                    "progress", "progress", "vendedor","totalpedidos", "avance", "zona", "direccion", "fechahora"}, new int[] {
                    R.id.t_avance, R.id.progresAvance, R.id.t_vendedor, R.id.t_total_ped, R.id.txt_avance,R.id.titulo_cliente, R.id.t_direccion, R.id.t_fecha_hora});

	//RecordAdapter RecordAdapter = new RecordAdapter(FormAvance.this,setMenu (dataItem[0]));

lv = (ListView) findViewById(R.id.listR2);
lv.setAdapter(adapter);




adapter.setViewBinder(new SimpleAdapter.ViewBinder() { 
	   @SuppressWarnings("null")
	public boolean setViewValue(View view, Object data,
            String textRepresentation) {
		  
        if (view.getId() == R.id.progresAvance) {
        
        	 int p=0;
     	   String value = (data.toString().trim()).split(":")[1];
            String array_res[]=value.split("/");
        	//Log.d("Data", array_res[0]+"*"+array_res[1]);
          
             p=(int) ((Integer.parseInt(array_res[0].trim()) / (float) Integer.parseInt(array_res[1].trim()) * 100));
        	
        	//Log.d("Data1", p+"");
           // int value = Integer.parseInt(data.toString());
            ProgressBar progressbar=((ProgressBar) view);
            progressbar.setProgress(p);
            Resources res = getResources();
            Rect bounds = progressbar.getProgressDrawable().getBounds();
              
            if(p < 40)
            {
         	   progressbar.setProgressDrawable(res.getDrawable(R.drawable.a_progressbar3));
            }
            else{
	            if(p < 75)
	            {
	         	   progressbar.setProgressDrawable(res.getDrawable(R.drawable.a_progressbar2));
	            }else{
	            	progressbar.setProgressDrawable(res.getDrawable(R.drawable.a_progressbar1));
	            }
	            progressbar.getProgressDrawable().setBounds(bounds);
            }
 return true;
   }
  /*     
  if (view.getId() == R.id.t_vendedor) {
	
	  
	       TextView t = ((TextView) view); 
        	if(data.toString().indexOf("S")>0){
        	t.setTextColor(Color.parseColor("#ff0000"));
        	t.setBackgroundColor(Color.parseColor("#cccccc"));
        	}
        	t.setText(data.toString()); 
        	 return true;	
        }
        */
    return false;
    }
    
});

lv.setOnItemClickListener(new OnItemClickListener() {
	
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		lv.setEnabled(false);
	    lv.setClickable(false);
		if(position==0){
			
			Intent intent = new Intent();
			intent.putExtra("data",data);
			intent.putExtra("datavend","");
			intent.putExtra("vend","S:"+getConfig("super"));
  	  	    intent.setClass(FormAvance.this, MainActivity.class);
  	  		startActivity(intent);
  	  		finish();
			
		}else
			{
			//String s=getConfig("super");
			//if(s.equals("0")) cmd="B_SupervisorUno";
			 s1 = menuItems.get(position).get("vendedor").trim().split(":")[1];
			 s1=s1.split("-")[0].trim();
			// B_SupervisorDos
			String URL ="http://www.axum.com.ar/"+getConfig("empresa")+"/ControllerJME.aspx?cmd="+cmd+"&Vend="+getConfig("super")+"&vend2="+s1.trim();
			 new HttpAsyncTask(1).execute(URL);	
			
		}
	}
});

//Log.w("DATA:","C:"+data.length());

if(data.length()>0){
		setMenu(data.split(",")[0]);	
	 }
if(data.length()==0){
		///String s=getConfig("super");
		 //  if(s.equals("0")) cmd="B_SupervisorUno";
			 URL ="http://www.axum.com.ar/"+getConfig("empresa")+"/ControllerJME.aspx?cmd="+cmd+"&vend="+getConfig("super");
			 new HttpAsyncTask(0).execute(URL);	
	}
}
	
public void setMenu(String data){
	if(data.length()>0) {
	int acu1=0,acu2=0,ped1=0,ped2=0;
	String[] separated = data.split("\n");
		 int cant=separated.length/7;
			 if(cant>0){
			//Log.w("DATA LISTA",cant+"");
			 HashMap map=new HashMap();menuItems.add(map);
			 int id=0;int efi=0;
			 for(int i=0; i<cant;i++){
				 
					 	map = new HashMap();
					    String[] s=separated[id+2].trim().split(":");
					 	String[] s1 = s[1].split("/");
						acu1+=Integer.parseInt(s1[0].trim());
						acu2+=Integer.parseInt(s1[1].trim());
						int tpdv=Integer.parseInt(s1[1].trim());
					efi=((Integer.parseInt(s1[0].trim())*100)/Integer.parseInt(s1[1].trim()));
					map.put("vendedor",separated[id++].trim());
					String ss=separated[id++].trim();
					ped1+=Integer.parseInt(ss.split(":")[1]);
				   map.put("totalpedidos",ss);
				    map.put("avance",efi+"%");
					map.put("progress",separated[id++].trim());
					map.put("direccion",separated[id++].trim());
					map.put("zona","No Vsitados:"+(tpdv-Integer.parseInt(ss.split(":")[1])));id++;
					map.put("fechahora","Hora: "+separated[id++].split(" ")[3]);
					menuItems.add(map);
						id++;
				}
			   efi=((acu1*100)/acu2);
				map = new HashMap();
		    	map.put("vendedor","----- S: "+getConfig("super"));
				map.put("totalpedidos","Pdvs Visitados: "+ped1);
			    map.put("avance",efi+"%");;
				map.put("progress","Eficiencia: "+acu1+"/"+acu2);
				map.put("zona","No visitados:"+(acu2-ped1));
				map.put("direccion"," ");
				map.put("fechahora"," ");
				menuItems.set(0, map);
			 }
	 adapter.notifyDataSetChanged();
	}
		
		}	
	



public boolean onKeyDown(int keyCode, KeyEvent event) {
	     	super.onKeyDown(keyCode, event);
	     	    	if (keyCode == KeyEvent.KEYCODE_BACK) {
	     	    		
	     	    		Intent mainIntent = new Intent().setClass(FormAvance.this, MenuPpal.class);
	     	    	        mainIntent.putExtra("m","GestionVentas");
	     	    	        startActivity(mainIntent);
	     	    	        finish();
	     	    	}
	     	    	return false;
	     	    }
public void actualizar(View view){
			String[] url={"http://www.axum.com.ar/"+getConfig("empresa")+"/ControllerJME.aspx?cmd="+cmd+"&Vend="+getConfig("vend")+"&Clave=2222"};
		//	ActualizarTask http= new ActualizarTask(AvanceVend1.this, AvanceVend1.this,1);
			//http.execute(url);	
			
			
		}
		
		
public static String GET(String url){
    InputStream inputStream = null;
    String result = "";
  //  Log.d("ok1", "ok");
    try {

        HttpClient httpclient = new DefaultHttpClient();

        HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

        inputStream = httpResponse.getEntity().getContent();
      //  Log.d("ok2", "ok2");
        if(inputStream != null){
        	  
        	 result =convertInputStreamToString(inputStream);
        }
        else
            result = "Did not work!";

    } catch (Exception e) {
        Log.d("Error", e.getLocalizedMessage());
    }

    return result;
}
private static String convertInputStreamToString(InputStream inputStream) throws IOException{
   	StringBuffer sf = new StringBuffer().append("");
try {
    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
    String line = ""; int linea=0;
    sf = new StringBuffer().append("");
    String result = "";
    String aux="";String l="";	int cont=0;
    while((line = bufferedReader.readLine()) != null){

   sf.append(line+"\n");
        
    }

    inputStream.close();
} catch (Exception e) {
	return "";
		//e.printStackTrace();
	}  
    return sf.toString() ;

}
public boolean onOptionsItemSelected(MenuItem item) {
	
	int id = item.getItemId();
	
	if (id == android.R.id.home) {
		   Intent mainIntent = new Intent().setClass(FormAvance.this, MenuPpal.class);
	        mainIntent.putExtra("m","GestionVentas");
	        startActivity(mainIntent);
	        finish();
          return true;
	}
return super.onOptionsItemSelected(item);
}

public boolean isConnected(){
    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) 
            return true;
        else
            return false;   
}

private class HttpAsyncTask extends AsyncTask<String, Void, String> {
  int accion =0;
    
    public  HttpAsyncTask(int i){
    	accion=i;
    }
    protected String doInBackground(String... urls) {

    	//Log.d("URL", urls[0]);
        return GET(urls[0]);
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
  	//Log.d("URL", result);
    	pb.setVisibility(ProgressBar.INVISIBLE);
    	if(accion==0 ){
    	   data=result;
    	  setMenu(result.split(",")[0]);
    	}
    	if(accion==1){
    	//	Log.d("URL", result);
    		Intent intent = new Intent();
			intent.putExtra("datavend",result);
			intent.putExtra("data",data);
			intent.putExtra("vend","V:"+s1);
  	  	    intent.setClass(FormAvance.this, MainActivity.class);
  	  		startActivity(intent);
  	  		finish();	
        	
    	}
 
 }
  
    protected void onPreExecute() {
    	pb.setVisibility(ProgressBar.VISIBLE);
    }
}


 
public String getConfig(String p1){
	SharedPreferences prefs = getSharedPreferences("config",MODE_PRIVATE);
		 return  prefs.getString(p1, "");
}


}
