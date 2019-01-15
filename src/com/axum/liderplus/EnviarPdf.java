package com.axum.liderplus;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.axum.config.Db;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;



public class EnviarPdf extends AsyncTask<String, Void, String> {

   public  String respuesta =  "0";
   int contador=0;
    private boolean error = false;
    ProgressDialog progressDialog;
    private Activity mContext;
   String fecha_entrega="";
   String file_pdf="";
	 //ArrayList<String> list_Cli = new ArrayList<String>();
   
    Db db;
	
    public EnviarPdf(Activity context,String file_pdf){

        this.mContext =  context;
	    progressDialog = new ProgressDialog(mContext);
		progressDialog.setMessage("Enviando documentos Aguarde ...");
		progressDialog.setCancelable(false);
		this.file_pdf= file_pdf;
		openFile();
		db= new Db(context);
		
 }

    protected void onPreExecute() {
    	//Toast.makeText(mContext,"inicio", Toast.LENGTH_SHORT).show();
    	progressDialog.show();
    }

    protected String doInBackground(String... urls) {
    	
    	
    	  String root_sd = Environment.getExternalStorageDirectory().toString();
    	
    	    File file2 = new File( root_sd ); 
    	     File file3 = new File(file2,file_pdf ); 
    	    	 String c= uploadFile(file_pdf,file3);
    	    	   if(c.length()>0)
    	    		   contador++;
    	    
    	
    
        return contador+"";
    }



    protected void onPostExecute(String content) {
    	progressDialog.dismiss();
        if (error) {
        	showLoginError("Error "+content);
        
          //  createNotification("Hay un problema(1)!",content);
        } else {
        	 String sf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        	guardarConfig("fechaFoto",sf);
        	showLoginError(content);
        	
	      	  	
        	//miPedido.getPedidos();
       	// Log.d("Resultado",content);
       }
    }
	public void showLoginError(final String result)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) {
				if(file_pdf.length()==0) {
				    MenuPpal1 context= (MenuPpal1) mContext;
				    context.setMenu();
				}
				  dialog.cancel();
				
	        	//Intent intent = new Intent();
				//intent.setClass(mContext, MiPedido.class);
				//mContext.startActivity(intent);
				// mContext.finish();
			}
		});
		builder.setMessage(result+" Documentos fueron enviados con exito");
		AlertDialog alert = builder.create();
		alert.setCancelable(false);
		alert.show();
	}
	/*
	 public void setFlag(int i, String idOrden)
	  {
		 db.abrirBasedatos();
		 db.baseDatos.execSQL("update pedidos set flag='"+i+"',id_orden='"+idOrden+"', fecha_entrega='"+fecha_entrega+"',estado='1' where flag='0' "); 
		// db.baseDatos.execSQL("DELETE FROM pedidos "); 
		
		 db.baseDatos.close();
	  }
*/


	 public String getConfig(String p1){
			SharedPreferences prefs = mContext.getSharedPreferences("config",mContext.MODE_PRIVATE);
				 return  prefs.getString(p1, "");
		}
	 
//	outPutFile = new File(logDirectory,(getConfig("sucursal")+"_"+bundle.getString("cliente")+".jpg").trim());	 
	 public String uploadFile(String idCliente,File outPutFile) {
		    
		    //String s[]=idCliente.split("_");
		   // String codigo_ot[]=s[1].split(".");
		    String upLoadServerUri = "http://sd-1271357-h00005.ferozo.net/liderplus/up_pdf.php?empresa="+getConfig("empresa")+"&supervisor="+getConfig("super");
		    Log.w("uploadFile", upLoadServerUri);
		    int serverResponseCode=-1;
		    HttpURLConnection conn = null;
		    DataOutputStream dos = null;  
		    String lineEnd = "\r\n";
		    String twoHyphens = "--";
		    String boundary = "*****";
		    int bytesRead, bytesAvailable, bufferSize;
		    byte[] buffer;
		    String result = "-1";
		    StringBuffer sb = new StringBuffer();
		    InputStream is = null;
		    int maxBufferSize = 1 * 1024 * 1024; 
		    
		  
		        try { // open a URL connection to the Servlet
		         String fileName = idCliente;///getNombre(idCliente.substring(0,idCliente.indexOf(".")))+".pdf";
		        //File	file= getFile(fileName,sourceFileUri.substring(0,sourceFileUri.lastIndexOf("/")));
		         FileInputStream fileInputStream = new FileInputStream(outPutFile);
		         
		         URL url = new URL(upLoadServerUri);
		         conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
		         conn.setDoInput(true); // Allow Inputs
		         conn.setDoOutput(true); // Allow Outputs
		         conn.setUseCaches(false); // Don't use a Cached Copy
		         conn.setRequestMethod("POST");
		         conn.setRequestProperty("Connection", "Keep-Alive");
		         conn.setRequestProperty("ENCTYPE", "multipart/form-data");
		         conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		         conn.setRequestProperty("uploaded_file", fileName); 
		        /// conn.getResponseCode();
		       
		         dos = new DataOutputStream(conn.getOutputStream());
		         //**********************************************************
		      
		         //***************************************************************
		         dos.writeBytes(twoHyphens + boundary + lineEnd); 
		         dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);
		         dos.writeBytes(lineEnd);
		         bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size

		         bufferSize = Math.min(bytesAvailable, maxBufferSize);
		         buffer = new byte[bufferSize];

		         // read file and write it into form...
		         bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
		           
		         while (bytesRead > 0) {
		           dos.write(buffer, 0, bufferSize);
		           bytesAvailable = fileInputStream.available();
		           bufferSize = Math.min(bytesAvailable, maxBufferSize);
		           bytesRead = fileInputStream.read(buffer, 0, bufferSize);               
		          }

		         // send multipart form data necesssary after file data...
		         dos.writeBytes(lineEnd);
		         dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
		     
		         
		     
		         // Responses from the server (code and message)
		         serverResponseCode = conn.getResponseCode();
		         String serverResponseMessage = conn.getResponseMessage();
		          
		       //  Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
		         if(serverResponseCode == 200){
		        	 
		        	 is = new BufferedInputStream(conn.getInputStream());
		             BufferedReader br = new BufferedReader(new InputStreamReader(is));
		             String inputLine = "";
		             while ((inputLine = br.readLine()) != null) {
		                 sb.append(inputLine);
		             }
		             result = sb.toString();              
		         }    
		         
		         //close the streams //
		         fileInputStream.close();
		         dos.flush();
		         dos.close();
		          
		    } catch (MalformedURLException ex) {  
		       // dialog.dismiss();  
		      //  ex.printStackTrace();
		       // Toast.makeText(UploadImageDemo.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
		        Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
		    } catch (Exception e) {
		       // dialog.dismiss();  
		       /// e.printStackTrace();
		        //Toast.makeText(UploadImageDemo.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
		        Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);  
		    }
		   // dialog.dismiss();       
		    return result;  
		   } 
	 
 public void guardarConfig(String paramString1, String paramString2)
 {
	   SharedPreferences.Editor localEditor = mContext.getSharedPreferences("config", 0).edit();
	   localEditor.putString(paramString1, paramString2);
	   localEditor.commit();
}	 

 private void openFile() {
		
		File appDirectory = new File( Environment.getExternalStorageDirectory() + "/AX" );
		
		  if ( !appDirectory.exists() ) {
				appDirectory.mkdir();
			}
		  File logDirectory = new File( appDirectory + "/Pdf" );
			if ( !logDirectory.exists() ) {
				logDirectory.mkdir();
			}
			File logDirectory2 = new File( appDirectory + "/Pdf/backup" );
			  if ( !logDirectory2.exists() ) {
					logDirectory2.mkdir();
			 }
			//Log.d("ARCHIVO PATH ", logDirectory.getAbsolutePath());
			//return	new File(logDirectory, usuario);
	 
	}
 public static int compareTwoDates(Date date1, Date date2) {
	    if (date1 != null && date2 != null) {
	        int retVal = date1.compareTo(date2);

	        if (retVal > 0)
	            return 1; // date1 is greatet than date2
	        else if (retVal == 0) // both dates r equal
	            return 0;

	    }
	    return -1; // date1 is less than date2
	} 
 

	
}