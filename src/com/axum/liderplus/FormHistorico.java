package com.axum.liderplus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.axum.config.Db;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FormHistorico extends Activity {

	 //WebView myWebView ;
	 Bundle bundle ;
	 String idCliente="", URL ="";
	 TextView txt_respuesta ;
	Db db;
	 ProgressBar pb ;
	 
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_historico);
	     db = new Db(this);
	     getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		getActionBar().setHomeButtonEnabled(true); 
		getActionBar().setDisplayHomeAsUpEnabled(true);
	   bundle = getIntent().getExtras();
	   idCliente=	bundle.getString("idCliente");
	   this.setTitle("Historico:"+idCliente);
		 
	/// myWebView = (WebView) this.findViewById(R.id.webResumen);
	   txt_respuesta = (TextView) this.findViewById(R.id.txt_respuesta);
	
		pb = (ProgressBar) findViewById(R.id.progressCarga);
		// pb.setVisibility(ProgressBar.VISIBLE);
		 // run a background job and once complete
		// pb.setVisibility(ProgressBar.INVISIBLE);
		 
		 URL ="http://www.axum.com.ar/"+getConfig("empresa")+"/ControllerJME.aspx?&cmd=B_Historicos&Cliente="+idCliente;
		 Log.d("URL", URL);
		 new HttpAsyncTask().execute(URL);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
     	super.onKeyDown(keyCode, event);
     	    	if (keyCode == KeyEvent.KEYCODE_BACK) {
     	    		
     	    		volver();	
     	    	}
     	    	return false;
     }
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		
		if (id == android.R.id.home) {
			volver();	
	          return true;
		}
return super.onOptionsItemSelected(item);
	}
	
	
	public void volver(){
		     Intent mainIntent = null;
  	        mainIntent = new Intent().setClass(this, Clientes.class);
  	        mainIntent.putExtra("idCliente",bundle.getString("idCliente"));
  	        startActivity(mainIntent);
  	        finish();
}
	
	public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
 
            HttpClient httpclient = new DefaultHttpClient();
 
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
 
            inputStream = httpResponse.getEntity().getContent();
 
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        return result;
    }
 
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null){
        	
            result += line+"\n";
        }
 
        inputStream.close();
        return result;
 
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
        @Override
        protected String doInBackground(String... urls) {
 
            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        
          // myWebView.loadData(getHtml(result), "text/html", "UTF-8");
           txt_respuesta.setText(result);
       	pb.setVisibility(ProgressBar.INVISIBLE);
          // Log.w("DATO:", htm+getFila(result)+pie);
       }
      
        protected void onPreExecute() {
        	pb.setVisibility(ProgressBar.VISIBLE);
        }
    }
	public  String getHtml(String data){
		/*
		 String[] separated = data.split(";");
	
		StringBuffer sf = new StringBuffer().append("");
		 if(separated.length>1){
			 sf.append("<html ><body bgcolor=\"#FFFFFF\"> <font size=\"-1\" ><table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\" bgcolor=\"#97e08f\">");
			
		
			 for(int i=0; i<separated.length;i++){
				 if(separated[i].indexOf("-")>0 && separated[i].indexOf("Fcha")==-1 ){
					 
					 String[] s1=separated[i].split("	");
					 if(!s1[0].equals("Art  ")){
						 s1[0]=getArticulo(s1[0]);
						 sf.append(" <tr bgcolor='#FFFFFF' > <td width='85%'>"+s1[0]+"</td> <td>"+s1[1]+"</td> </tr>"); 
					 }else{
						 sf.append(" <tr bgcolor='#97e08f' > <td width='75%'><font color='#FFFFFF'>"+s1[0]+"</font></td> <td><font color='#FFFFFF'>"+s1[1]+"</font></td> </tr>"); 	 
					 }
				
				 }
				 else{
					 if(separated[i].startsWith("FECHA:")||separated[i].startsWith("CTACTE:"))
							sf.append(" <tr bgcolor='#6495ED' height='5' > <td colspan='2'></td> </tr>");  
					sf.append(" <tr bgcolor='#F0F0F0' > <td colspan='2'><center>"+separated[i]+"</center></td> </tr>");  
					
				 }
			 }
			 sf.append("</table></font></body></html>");
		}
	*/
		return ( data );	
	}
    
	
	 
	 public String getArticulo(String id_articulo)
	 {	
		 String ct=id_articulo.trim();
	 	db.abrirBasedatos();
		 Log.d("base error","select codigo,descripcion from articulos where codigo="+id_articulo.trim() );
	 	Cursor localCursor = db.baseDatos.rawQuery("select codigo,descripcion from articulos where codigo="+id_articulo.trim(), null);
	 
	 	if(localCursor.getCount()>0){
	 	localCursor.moveToFirst();
	 	ct+="-"+localCursor.getString(1);
	 	}
	 	localCursor.close();
	 	db.baseDatos.close();
	 	return ct;
	 }

	 
	 
    public String getConfig(String p1){
		SharedPreferences prefs = getSharedPreferences("config",MODE_PRIVATE);
			 return  prefs.getString(p1, "");
	}

}
