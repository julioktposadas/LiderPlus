// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.axum.liderplus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.axum.config.Db;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

public class Preferences extends PreferenceActivity {
Db db;

	 public Preferences() {
	     
	        
	    }
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
       db = new Db(this);
      addPreferencesFromResource(R.xml.preferences);
      getActionBar().setIcon(R.drawable.menu);
      this.setTitle("Preferencia");
  	getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
	getActionBar().setHomeButtonEnabled(true); 
	getActionBar().setDisplayHomeAsUpEnabled(true);
	
	  Preference myPref = (Preference) findPreference("key_login");
	  myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	               public boolean onPreferenceClick(Preference preference) {
	            	   alertaLicencia();
					return false;
	                   //open browser or intent here
	               }
	           });
	  Preference myPref2 = (Preference) findPreference("key_resetear");
	  myPref2.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	               public boolean onPreferenceClick(Preference preference) {
	            	   alertaResetear();
					return false;
	                   //open browser or intent here
	               }
	           });
	
			
	  
    
    }
    public void alertaLicencia() {
	    new AlertDialog.Builder(this)
	    		.setTitle("ALERTA!")
	           .setMessage("Si borrar la licencia perdera todos los datos\nDesea eliminar de todos modo?")
	           .setCancelable(false)
	            .setIconAttribute(android.R.attr.alertDialogIcon)
	           .setPositiveButton("Si", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	         
	            	   borrarLicencia();
	            	  // finish();
	               }
	           })
	           .setNegativeButton("No", null)
	           .show();
	}
    public void alertaResetear() {
	    new AlertDialog.Builder(this)
	    		.setTitle("ALERTA!")
	           .setMessage("Desea desmarcar a todos los clientes como no relevados? ")
	           .setCancelable(false)
	            .setIconAttribute(android.R.attr.alertDialogIcon)
	           .setPositiveButton("Si", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	         
	            	   setFlagFespuestas();
	            	  // finish();
	               }
	           })
	           .setNegativeButton("No", null)
	           .show();
	}
    public void borrarLicencia()
	  {
		 guardarConfig("registro","");
	     guardarConfig("vend","");
	     guardarConfig("super","");
	     guardarConfig("estandar","0");
	     guardarConfig("lblUltAct","");
	     guardarConfig("lblUltimoEnvio","");
		 db.abrirBasedatos();
		 db.baseDatos.execSQL("DELETE FROM clientes"); 
		 db.baseDatos.execSQL("DELETE FROM articulos"); 
		 db.baseDatos.execSQL("DELETE FROM encuesta"); 
		 db.baseDatos.execSQL("DELETE FROM respuestas"); 
		 db.baseDatos.execSQL("DELETE FROM ramos"); 
		 db.baseDatos.execSQL("DELETE FROM vendedores"); 
		 db.baseDatos.execSQL("DELETE FROM clientesposicion"); 
		 db.baseDatos.execSQL("DELETE FROM tareas"); 
		 
		// db.baseDatos.execSQL("DELETE FROM grupo"); 
		 db.baseDatos.close();
		 finish();
	  }


public void  setFlagFespuestas(){
	
	Date today = Calendar.getInstance().getTime();
	SimpleDateFormat formatter = new SimpleDateFormat("MM");
  String periodo = formatter.format(today);
	db.abrirBasedatos();
	 db.baseDatos.execSQL("update respuestas set flag='2' ");
	
	 db.baseDatos.close();
	 finish();
}

public void setFlag()
{
	db.abrirBasedatos();
	db.baseDatos.execSQL("UPDATE clientes SET dado15='0' ");
  db.baseDatos.close();

}
    public void guardarConfig(String paramString1, String paramString2)
	 {
	   SharedPreferences.Editor localEditor = getSharedPreferences("config", 0).edit();
	   localEditor.putString(paramString1, paramString2);
	   localEditor.commit();
	 }	
}
