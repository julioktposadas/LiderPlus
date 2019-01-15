package com.axum.liderplus;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import com.axum.config.Db;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class EnviarTask extends AsyncTask<String, Void, String> {

    private final HttpClient httpclient = new DefaultHttpClient();
   Db db;
    final HttpParams params = httpclient.getParams();
    HttpResponse response;
    private String content =  null;
   public  String respuesta =  null;
    private boolean error = false;
    ProgressDialog progressDialog;
    private Context mContext;

	 //ArrayList<String> list_Cli = new ArrayList<String>();

	
    public EnviarTask(Context context){
        db= new Db(context);
        this.mContext = context;
	    progressDialog = new ProgressDialog(mContext);
		progressDialog.setMessage("Enviando datos ....");
		progressDialog.setCancelable(false);
 }

    protected void onPreExecute() {
    	//Toast.makeText(mContext,"inicio", Toast.LENGTH_SHORT).show();
    	progressDialog.show();
    }

    protected String doInBackground(String... urls) {
       String URL = null;
       		int responseE = -1;
       		StringBuffer respuesta = new StringBuffer(); 
       		String sTrack=getConfig("super")+"\r\n"+getTrackDB();
       //	 Log.w("Data",sTrack );
	    	if (sTrack.length()>0 ){
	    		
	    		//Toast.makeText(getBaseContext()," :track Enviado..",  Toast.LENGTH_SHORT).show();
			    		
			    	   HttpURLConnection connection = null;  
			    	    try {
			    	    	
			    	    	 byte[] arrayOfByte = sTrack.getBytes();
			    	         URL localURL = new URL(urls[0]);
			    	         connection = (HttpURLConnection)localURL.openConnection();
			    	         connection.setDoInput(true);
			    	         connection.setDoOutput(true);
			    	         connection.setRequestMethod("POST");
			    	         connection.setRequestProperty("Vence","2018-06-04");
			    	         connection.setRequestProperty("Empresa","kochtschirsch");
			    	         connection.setRequestProperty("Fecha","2015-02-04");
			    	         
			    	         new DataOutputStream(connection.getOutputStream()).write(arrayOfByte);
			    	         responseE = connection.getResponseCode();                 
			    	         if (responseE == HttpURLConnection.HTTP_OK) {
			    	        	 InputStream is = connection.getInputStream();
			    	        	 BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			    	        	 String line;
			    	        	 while((line = rd.readLine()) != null) {
			    	        		 respuesta.append(line);
			    	       
			    	        	 	}
			    	        	 rd.close();
			    	      
			    	         	}
			    	     
			    	    //  return null;// response.toString();
		
			    	    } catch (Exception e) {
			    	    	
			    	    } finally {
		
			    	      if(connection != null) {
			    	        connection.disconnect(); 
			    	      }
			    	    }
				
	    	}
        return respuesta.toString();
    }

    

    protected void onPostExecute(String content) {
    	progressDialog.dismiss();
        if (error) {
        	showLoginError("Error "+content);
        
          //  createNotification("Hay un problema(1)!",content);
        } else {
        	//limpiarDato();
        	showLoginError(content);
        	setFlag(1);
        	String sf= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        	guardarConfig("lblUltimoEnvio",sf);
       	// Log.d("Resultado",content);
       }
    }
	public void showLoginError(String result)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.setMessage(result);
		AlertDialog alert = builder.create();
		alert.setCancelable(false);
		alert.show();
	}
	
	 public void setFlag(int i)
	  {
		 db.abrirBasedatos();
		 db.baseDatos.execSQL("update respuestas set flag='"+i+"' "); 
		
		 db.baseDatos.close();
	  }

	
public String getTrackDB(){
	   ArrayList<String> list_Res = new ArrayList<String>();
	  ArrayList<String> list_Data = new ArrayList<String>();

	  String aux="";
		 String s="\r\n";
		 db.abrirBasedatos();
		 StringBuffer strBuf=new StringBuffer();
		  Cursor localCursor = db.baseDatos.rawQuery("select * from respuestas where flag =0 ", null);
		    while (true)
		    {
		      if (!localCursor.moveToNext())
		      {
		        localCursor.close();
		        db.baseDatos.close();
		        return strBuf.toString();
		      }
		
		    
	
		      strBuf.append(localCursor.getString(0).trim()+","+localCursor.getString(1)+","+localCursor.getString(2)+"|"+localCursor.getString(3)+","+localCursor.getString(4)+",0"+s);
		     
		    }
	 }
	 
	 public String getConfig(String p1){
			SharedPreferences prefs = mContext.getSharedPreferences("config",mContext.MODE_PRIVATE);
				 return  prefs.getString(p1, "");
		}
	 public void guardarConfig(String paramString1, String paramString2)
	 {
	   SharedPreferences.Editor localEditor = mContext.getSharedPreferences("config", 0).edit();
	   localEditor.putString(paramString1, paramString2);
	   localEditor.commit();
	 }
}