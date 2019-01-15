package com.axum.liderplus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LicenseManager extends Activity {
	 private static final int REGISTRATION_TIMEOUT = 3 * 1000;
	 private static final int WAIT_TIMEOUT = 30 * 1000;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private GoogleCloudMessaging gcm = null;
	private String SENDER_ID = "495580131602";

	TextView b_login;
	EditText txt_empresa,txt_clave,licencia;
	//static ContentResolver   tm=null;
	private Menu optionsMenu;
	
	private String msg;
	TextView tv_msn;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_login2);
		setTitle("Licencia"); 
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		getActionBar().setHomeButtonEnabled(true); 
		getActionBar().setDisplayHomeAsUpEnabled(true);
	
	    b_login = (TextView) findViewById(R.id.b_login) ;
	    txt_empresa = (EditText) findViewById(R.id.txt_empresa) ;
	    txt_clave = (EditText) findViewById(R.id.txt_clave) ;
	    
	  //  tv_msn= (TextView) findViewById(R.id.tv_msn) ;
		//  Typeface miPropiaTypeFace = Typeface.createFromAsset(getAssets(),  "estre.ttf");
		//   tv_msn.setTypeface(miPropiaTypeFace);
	   
}
	public void login(View v){
		
		String s=txt_empresa.getText().toString();
	    String s1=txt_clave.getText().toString();		
		if(s.length()>0 && s1.length()>0){
			
			String[] url={"http://www.axum.com.ar/registrar.aspx?",s,s1};
         	  Licencia http= new Licencia(LicenseManager.this);
           http.execute(url);	
		}
	}
public void login(){
		
		String s=txt_empresa.getText().toString();
	    String s1=txt_clave.getText().toString();		
		if(s.length()>0 && s1.length()>0){
			
			String[] url={"http://www.axum.com.ar/registrar.aspx?",s,s1};
         	  Licencia http= new Licencia(LicenseManager.this);
           http.execute(url);	
		}
	}
	 private static String getID()
	 	{
		
		  return  "65465";// Secure.getString(tm, Secure.ANDROID_ID);
	 	}

	public class Licencia extends AsyncTask<String, Void, String> {

	    private final HttpClient httpclient = new DefaultHttpClient();
	    final HttpParams params = httpclient.getParams();
	    HttpResponse response;
	    private String content =  null;
	    public  String respuesta =  null;
	    private boolean error = false;
	  
	    private Context mContext;
	    StringBuffer response1 = new StringBuffer(); 
 public Licencia(Context context){

	        this.mContext = context;
		   
	 }

	    protected void onPreExecute() {
	    	setRefreshActionButtonState(true);
	    	//progressBarLogin.setVisibility(View.VISIBLE);
	    	//progressDialog.show();
	    }

	    protected String doInBackground(String... urls) {
	     	
			 int responseE = -1;
			HttpURLConnection connection = null;  
		    	    try {
		    	    	URL localURL = new URL(urls[0]);
		    	         connection = (HttpURLConnection)localURL.openConnection();
		    	         connection.setConnectTimeout(REGISTRATION_TIMEOUT);
		    	        
		    	         connection.setDoInput(true);
		    	         connection.setDoOutput(true);
		    	         connection.setRequestProperty("User-Agent", "Profile/MIDP-2.0 Configuration/CLDC-1.0");
		    	         connection.setRequestProperty("empresa",urls[1]);
		    	         connection.setRequestProperty("clave",urls[2]);
		    	         
		    	         connection.setRequestMethod("GET");
		    	         responseE = connection.getResponseCode();                 
		    	         if (responseE == HttpURLConnection.HTTP_OK) {
		    	        	 InputStream is = connection.getInputStream();
		    	        	 BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    	        	 String line;
		    	        	 while((line = rd.readLine()) != null) {
		    	        		 response1.append(line);
		    	       
		    	        	 	}
		    	        	 rd.close();
		    	        	 
		    	       //	 Log.w("test","contenido"+ response1.toString());
		    	      
		    	         	}
		

		    	    } catch (Exception e) {
		    	    	error =true;
		    	    	response1.append(e);
		    	    	
		    	    } finally {

		    	      if(connection != null) {
		    	        connection.disconnect(); 
		    	      }
		    	    }
			

	  return "";
	    }

protected void onPostExecute(String content) {
	setRefreshActionButtonState(false);
	        if (error) {
	        	showLoginError(response1.toString(),"Error");
	        	
	          //  createNotification("Hay un problema(1)!",content);
	        } else {
	        //	progressBarLogin.setVisibility(View.GONE);
	        	if(response1.toString().length()>0){
	        		
	        		String r[]=response1.toString().split(",");
	        		guardarConfig("registro",response1.toString());
	        		guardarConfig("empresa",txt_empresa.getText().toString());
	        		guardarConfig("clave",txt_clave.getText().toString());
	        		guardarConfig("id_tel",getID());
	        		
	        		
	        		try {
	        			String versionName =getPackageManager() .getPackageInfo(getPackageName(), 0).versionName;
	        			guardarConfig("v",versionName);
	        		} catch (NameNotFoundException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}
	        		
	        	
	        		
	        		Intent mainIntent = new Intent().setClass(LicenseManager.this, MenuPpal1.class);
	    	        mainIntent.putExtra("m","MenuPpal");
	    	        startActivity(mainIntent);
	    	        finish();
	        	}
	        	else
	        	showLoginError("Usuario o Clave incorrecta","Alerta");
	       	// Log.d("Resultado",content);
	       }
	    }
		public void showLoginError(String result,String titulo)
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
			alert.setTitle(titulo);
			//alert.setIconAttribute(android.R.attr.alertDialogIcon);
			alert.setCancelable(false);
			alert.show();
		}
		

		 public String getConfig(String p1){
				SharedPreferences prefs = mContext.getSharedPreferences("config",mContext.MODE_PRIVATE);
					 return  prefs.getString(p1, "");
			}
		 public void guardarConfig(String paramString1, String paramString2)
		 {
		   SharedPreferences.Editor localEditor =getSharedPreferences("config", 0).edit();
		   localEditor.putString(paramString1, paramString2);
		   localEditor.commit();
		   //Toast.makeText(this, "Impresora guardada con exito!!", Toast.LENGTH_LONG).show();
		 }   
	}
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	     	super.onKeyDown(keyCode, event);
	     	    	if (keyCode == KeyEvent.KEYCODE_BACK) {
	     	    		// connectToDevice();
	     	    		finish();              
	       	    		//System.runFinalization();
	       	    		//System.exit(0);
	     	    	}
	     	    	return false;
	     	    }

	 public boolean onCreateOptionsMenu(Menu menu) {
		    this.optionsMenu = menu;
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.menu_licencia, menu);
		    return super.onCreateOptionsMenu(menu);
		}
	 
	 public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
		        case R.id.menuLogin:
		        	login();
		            return true;
		       
		        default:
		            return super.onOptionsItemSelected(item);
		    }
		}
	 
	 public void setRefreshActionButtonState(final boolean refreshing) {
		    if (optionsMenu != null) {
		        final MenuItem refreshItem = optionsMenu.findItem(R.id.menuLogin);
		        if (refreshItem != null) {
		            if (refreshing) {
		               refreshItem.setActionView(R.layout.actionbar_progress);
		            } else {
		                refreshItem.setActionView(null);
		            }
		        }
		    }
		} 

	 
	 public static boolean checkPlayServices(Context context) {
		    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		    if (resultCode != ConnectionResult.SUCCESS) {
		        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
		            GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) context,PLAY_SERVICES_RESOLUTION_REQUEST).show();
		        } else {
		            Log.i("Developer", "This device is not supported.");
		        }
		        return false;
		    }
		    return true;
		}	 
	 
}
