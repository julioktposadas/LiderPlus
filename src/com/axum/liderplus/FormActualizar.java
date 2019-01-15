package com.axum.liderplus;



import android.os.Bundle;


import com.axum.liderplus.MenuPpal.DownloadFileAsync;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class FormActualizar extends Activity {

	Button b_actualizar;
	EditText txt_vendedor,txt_clave;
	EditText txt_supervisor;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_actualizar);
		setTitle("Actualizar Datos"); 
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		getActionBar().setHomeButtonEnabled(true); 
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		txt_vendedor = (EditText) findViewById(R.id.t_vendedor) ;
		txt_supervisor = (EditText) findViewById(R.id.t_supervisor) ;
		txt_clave = (EditText) findViewById(R.id.t_clave) ;
		b_actualizar = (Button) findViewById(R.id.b_actualizar);
		b_actualizar.setEnabled(true);
		txt_vendedor.setText(getConfig("vend"));
		txt_supervisor.setText(getConfig("super"));
	
	}
	 public void actualizar(View v){
		 if(txt_vendedor.length()>0 && txt_supervisor.length() >0){
		 String[] url={"B_Clientes","B_Articulos","B_Relevamientos","B_Clientes","B_Relevamientos","B_Vendedores"};
		 b_actualizar.setEnabled(false);
			ActualizarBase http= new ActualizarBase(FormActualizar.this, FormActualizar.this);
			http.execute(url);
		 }
		 else{
			 Toast.makeText(this, "Complete todos los campos! ", Toast.LENGTH_LONG).show(); 
		 }
	 }

	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	  	super.onKeyDown(keyCode, event);
	  	    	if (keyCode == KeyEvent.KEYCODE_BACK) {
	  	    		
	  	    		Intent mainIntent = new Intent().setClass(FormActualizar.this, MenuPpal1.class);
	  	    	    	mainIntent.putExtra("m","MenuPpal");
	  	    	        startActivity(mainIntent);
	  	    	        finish();
	  	    	}
	  	    	return false;
	  	    }
	 
		public boolean onOptionsItemSelected(MenuItem item) {
			
			int id = item.getItemId();
			
			if (id == android.R.id.home) {
				Intent mainIntent = new Intent().setClass(FormActualizar.this, MenuPpal1.class);
	    	    	mainIntent.putExtra("m","MenuPpal");
	    	        startActivity(mainIntent);
	    	        finish();
		          return true;
			}

	   
			return super.onOptionsItemSelected(item);
		}	 
	 
	 
	 public String getConfig(String p1){
			SharedPreferences prefs = getSharedPreferences("config",getBaseContext().MODE_PRIVATE);
				 return  prefs.getString(p1, "");
		}
}
