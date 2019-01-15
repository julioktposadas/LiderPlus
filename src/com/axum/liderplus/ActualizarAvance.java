package com.axum.liderplus;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class ActualizarAvance extends AsyncTask<String, Void, String> {


    private final HttpClient httpclient = new DefaultHttpClient();

    final HttpParams params = httpclient.getParams();
    HttpResponse response;
    private String content =  null;
   public  String respuesta =  null;
    private boolean error = false;
    ProgressDialog progressDialog;
    private Context mContext;

	 Activity mActivity;
	 String sucu="";
	 int menu=0;
	 String vend2="";
	
    public ActualizarAvance(Context context, Activity activity, int menu){

        this.mContext = context;
        this.mActivity= activity;
        this.menu=menu;
	    progressDialog = new ProgressDialog(mContext);
		progressDialog.setMessage("Actualizando datos....");
		progressDialog.setCancelable(false);
 }

    protected void onPreExecute() {
    	//Toast.makeText(mContext,"inicio", Toast.LENGTH_SHORT).show();
    	progressDialog.show();
    }

    protected String doInBackground(String... urls) {
       String URL = null;
  
        try {
        	 //Log.w("D:",);
            if(urls[2].length()>0)
        	vend2="V:"+urls[2];
            else
        	vend2="S:"+urls[1];
        		
            URL = urls[0]+"&Vend="+urls[1]+"&Vend2="+urls[2];
         
            Log.w("URL:",URL);
            HttpPost httpPost = new HttpPost(URL);
           
        response = httpclient.execute(httpPost);

            StatusLine statusLine = response.getStatusLine();
            //Check the Http Request for success
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                content = out.toString();
               // Log.w("test","contenido"+ content);
            }
            else{
                //Closes the connection.
               // Log.w("HTTP1:",statusLine.getReasonPhrase());
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
           
            	content = content.replace('\n', ',').trim();
            	//content =separated[2].replace('\n', ',');
    
        } catch (ClientProtocolException e) {
            Log.w("HTTP2:",e );
            content = e.getMessage();
            error = true;
            //cancel(true);
        } catch (IOException e) {
            Log.w("HTTP3:",e );
            content = e.getMessage();
            error = true;
          //  cancel(true);
        }catch (Exception e) {
            Log.w("HTTP4:",e );
            content = e.getMessage();
            error = true;
           // cancel(true);
        }

        return content;
    }

    

    protected void onPostExecute(String content) {
    	progressDialog.dismiss();
        if (error) {
        	showLoginError("Error "+content);
          //  createNotification("Hay un problema(1)!",content);
        } else {
        	// Log.w("HTTP r",content);
        	//showLoginError(content);
        	 Intent intent = new Intent();
        	 intent.putExtra("data",content);
        	 intent.putExtra("vend2",vend2);
        	  Log.w("HTTP15:",vend2);
        	 if(this.menu==0)
        	//	intent.setClass(mContext, Avance.class);
        	 
        	 if(this.menu==1)
               intent.setClass(mContext, FormAvance.class);
        	 mActivity.finish();
             mContext.startActivity(intent);
            
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
	

	
	 public void guardarConfig(String paramString1, String paramString2)
	 {
	   SharedPreferences.Editor localEditor = mContext.getSharedPreferences("config", 0).edit();
	   localEditor.putString(paramString1, paramString2);
	   localEditor.commit();
	 }
}