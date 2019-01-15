package com.axum.liderplus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.axum.config.Db;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class FormRanking extends Activity {
	
	ListView lv ;
	SimpleAdapter adapter;
	ArrayList<HashMap<String, String>> menuItems ;
	static  Db db;
	ProgressBar pb;
	String URL="";
	 private static Context mContext;
	static ArrayList<String> list_vend = new ArrayList<String>();
	static ArrayList<String> list_Efi = new ArrayList<String>();
	
protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db= new Db(this);
		setContentView(R.layout.menu_lista);
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		getActionBar().setHomeButtonEnabled(true); 
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mContext= this.getApplicationContext();
		pb = (ProgressBar) findViewById(R.id.progressCargando);
		this.setTitle("Ranking Eficiencia");
		 menuItems = new ArrayList<HashMap<String, String>>();
		
		  adapter = new SimpleAdapter(this, menuItems,R.layout.row_simple1,new String[] {"imagen","id","posicion","eficiencia" }, new int[] {R.id.icon,R.id.row_toptext,R.id.t_puesto,R.id.t_efic});
		   lv = (ListView) findViewById(R.id.listR2);
		    lv.setAdapter(adapter);
		    URL ="http://www.axum.com.ar/"+getConfig("empresa")+"/ControllerJME.aspx?cmd=B_Supervisor&vend="+getConfig("super");
			 new HttpAsyncTask(0).execute(URL);	  
			 

				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
							//Intent intent = new Intent();
							//intent.putExtra("idCliente","");
			      	  	   // intent.setClass(FormRanking.this, LocationActivity.class);
			      	  	   // startActivity(intent);
			      	  		//finish();
				               }
				});
		
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.form_ranking, menu);
		return true;
	}


public void listarVendedor(){
 		 
	 HashMap map;int p=1;
		 db.abrirBasedatos();
		 Cursor localCursor = db.baseDatos.rawQuery("select *  from vendedores order by flag desc ", null);
			    while (true)
			    {
			      if (!localCursor.moveToNext())
			      { 
			        localCursor.close();
			        db.baseDatos.close();
			        break;
			      }
			  
			          map = new HashMap();
			     if(p==1)
			          map.put("imagen",R.drawable.ranking1);
			     else
			    	 map.put("imagen",R.drawable.ranking2);
			     
			      if(localCursor.getString(1).length()>0)
			    	  map.put("id",localCursor.getString(1));
			      else
			    	  map.put("id","Vendedor "+localCursor.getString(0));
			      
			    	  map.put("posicion",""+p);
			    	  map.put("eficiencia",localCursor.getString(2)+"%"); 
			    	 menuItems.add(map);
			    	 p++;
			 }
			    adapter.notifyDataSetChanged();
     } 
	 
public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
	  // TODO Auto-generated method stub
	  int targetWidth = 100;
	  int targetHeight = 100;
	  Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, 
	                            targetHeight,Bitmap.Config.ARGB_8888);
	  
	                Canvas canvas = new Canvas(targetBitmap);
	  Path path = new Path();
	  path.addCircle(((float) targetWidth - 1) / 2,
	  ((float) targetHeight - 1) / 2,
	  (Math.min(((float) targetWidth), 
	                ((float) targetHeight)) / 2),
	          Path.Direction.CCW);
	  
	                canvas.clipPath(path);
	  Bitmap sourceBitmap = scaleBitmapImage;
	  canvas.drawBitmap(sourceBitmap, 
	                                new Rect(0, 0, sourceBitmap.getWidth(),
	    sourceBitmap.getHeight()), 
	                                new Rect(0, 0, targetWidth,
	    targetHeight), null);
	  return targetBitmap;
	 }

	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
	
		if (id == android.R.id.home) {
			Intent mainIntent = new Intent().setClass(FormRanking.this, MenuPpal.class);
 	        mainIntent.putExtra("m","GestionVentas");
 	        startActivity(mainIntent);
 	        finish();
		}
		
		return super.onOptionsItemSelected(item);
	}
public static String GET(String url){
	    InputStream inputStream = null;
	    String result = "";
	    HttpClient httpclient=null;
	    try {

	       httpclient = new DefaultHttpClient();

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
public static   String convertInputStreamToString(InputStream inputStream) throws IOException{
	    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
	    String line = ""; int linea=0;
	    db.abrirBasedatos();
	   String sql = "UPDATE vendedores SET flag= ? WHERE codigo like ?";
	  String v="";	
	 	SQLiteStatement statement = db.baseDatos.compileStatement(sql);
	     db.baseDatos.beginTransaction();
	   while((line = bufferedReader.readLine()) != null){
	    	if(line.startsWith(" ")){
	    		
	    		if(line.startsWith(" ----- V:"))
	    			v=line.split(":")[1].toString().trim();
	    		
	    	
	    		if(line.startsWith(" Avance:")){
	    			
	    			String value = (line.toString().trim()).split(":")[1];
	    	            String array_res[]=value.split("/");
	    	        	int p=(int) ((Integer.parseInt(array_res[0].trim()) / (float) Integer.parseInt(array_res[1].trim()) * 100));
	    			   // list_Efi.add(p+"");
	    			    cargarVendedor(v,p,statement);
	    		}
	    		///Log.w("DATO",line+"=="+line.startsWith(" Avance:"));
	     //sf.append(line+"\n");
	    }
	        
	}
		db.baseDatos.setTransactionSuccessful();	
		db.baseDatos.endTransaction();
		db.baseDatos.close();
	    inputStream.close();
	    return "ok" ;

	}
public boolean onKeyDown(int keyCode, KeyEvent event) {
     	super.onKeyDown(keyCode, event);
     	    	if (keyCode == KeyEvent.KEYCODE_BACK) {
     	    		
     	    		Intent mainIntent = new Intent().setClass(FormRanking.this, MenuPpal.class);
     	    	        mainIntent.putExtra("m","GestionVentas");
     	    	        startActivity(mainIntent);
     	    	        finish();
     	    	}
     	    	return false;
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
	   	 listarVendedor();
	 
	 }
	  
	    protected void onPreExecute() {
	    	pb.setVisibility(ProgressBar.VISIBLE);
	    }
  
	    
	    
	}

	public String getConfig(String p1){
		SharedPreferences prefs = getSharedPreferences("config",MODE_PRIVATE);
			 return  prefs.getString(p1, "");
	}
	
	
	private static void cargarVendedor(String vend ,int efi, SQLiteStatement statement) {
		Log.w("DATO",vend+"=="+efi);
		  statement.clearBindings();
		   statement.bindString(1,efi+"");
           statement.bindString(2, vend.trim());
           statement.execute();
} 
	

	
}
