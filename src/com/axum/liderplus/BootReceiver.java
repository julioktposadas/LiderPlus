package com.axum.liderplus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class BootReceiver extends BroadcastReceiver
{
	 
	 AlarmManager amanager=null;
	 PendingIntent pi=null;
	 Context mContext;
	

	
		boolean servicio_ok=true;
	// private static final Uri STATUS_URI = Uri.parse("content://sms");
            public void onReceive(Context context, Intent in)
            {
            	  this.mContext=context;
            	
            	  // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
               	 	//servicio_ok = sharedPreferences.getBoolean("key_servicio", true);
               	 	
               	    ///Log.e("ESTADO SERVICIO","Valor: " + servicio_ok);
             	//	 if(servicio_ok )
             			// new Servicios(mContext).activarServicios(); 
             			
             		 
             		//Toast.makeText(mContext, "VALOR DE LA VARIABLE ...."+servicio_ok, Toast.LENGTH_SHORT).show();
            }
            
            
            
      	   
          
  }
