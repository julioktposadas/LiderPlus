package com.axum.liderplus;

import java.util.Timer;
import java.util.TimerTask;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
 
public class SplashScreenActivity extends Activity {
 
    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 2000;
    String l=" Uso sujeto a los términos y condiciones de la licencia del software.\nID:";
   
    TextView t_empresa;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
         requestWindowFeature(Window.FEATURE_NO_TITLE);
         setContentView(R.layout.a_splashscreen);
         TextView licencia,v_version ;
         t_empresa = (TextView) findViewById(R.id.t_empresa);
         licencia= (TextView) findViewById(R.id.licencia) ;
         v_version= (TextView) findViewById(R.id.version) ;
        // abrirDb();
         if(getConfig("registro").length()==0){
       	   setLicencia();
          }else{
        	  t_empresa.setText(getConfig("empresa"));
        	    licencia.setText(l+getConfig("id_tel"));
        		try {
        			String versionName =getPackageManager() .getPackageInfo(getPackageName(), 0).versionName;
        			  v_version.setText("Android- v: "+versionName);
        		} catch (NameNotFoundException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	  
		        TimerTask task = new TimerTask() {
		            @Override
		            public void run() {
		 
		            	
		                Intent mainIntent = new Intent().setClass( SplashScreenActivity.this, MenuPpal1.class);
		                mainIntent.putExtra("m","MenuPpal");
		                startActivity(mainIntent);
		                finish();
		            }
		        };
        
		        Timer timer = new Timer();
            timer.schedule(task, SPLASH_SCREEN_DELAY);
          }
    }
    
    
    public void setLicencia(){
		 Intent intent = new Intent();
		 intent.putExtra("m","MenuPpal");
	  	 intent.setClass(SplashScreenActivity.this, LicenseManager.class);
	  	startActivity(intent);
	  	 finish(); 
	 }
 public String getConfig(String p1){
		SharedPreferences prefs = getSharedPreferences("config",getBaseContext().MODE_PRIVATE);
			 return  prefs.getString(p1, "");
	}	
 
 public void abrirDb() {
	 String query = "select sqlite_version() AS sqlite_version";
	    SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(":memory:", null);
	    Cursor cursor = db.rawQuery(query, null);
	    String sqliteVersion = "";
	    if (cursor.moveToNext()) {
	        sqliteVersion = cursor.getString(0);
	        t_empresa.setText(sqliteVersion);
	    }
	 
 }

 
}