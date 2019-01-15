package com.axum.liderplus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.axum.config.Db;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


//************************** 
 
///***************************************************

@SuppressLint("NewApi")
public class MenuPpal1 extends Activity {

ArrayList<HashMap<String, String>> menuItems ;
 Bundle bundle ;
SimpleAdapter adapter;
String menuId="";
ListView lv ;

TextView msg;
int var[];
private Calendar _calendar;
String msgG;
private static final int RESULT_SETTINGS = 1;
public static final String LOG_TAG = "Downloader";
private static final String dateTemplate = "d-MMMM-yyyy";
//initialize our progress dialog/bar
private ProgressDialog mProgressDialog;
public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
boolean servicio_ok=false;
//initialize root directory
File rootDir = Environment.getExternalStorageDirectory();
boolean cancelDownload = false;

public String fileName = "argentina.map";
String fecha_calenario="";
//Servicios servicio;
Db db;	
int xx[]= {0};
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_menuppal);
	     //servicio=new Servicios(this);
		db= new Db(this);
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		getActionBar().setHomeButtonEnabled(true); 
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//String myVersion = android.os.Build.VERSION.RELEASE;
		TextView lblCantClientes = (TextView) findViewById(R.id.lblCantClientes);
		lblCantClientes.setText(this.getCantidadClientes()+"");
		TextView lblUltAct = (TextView) findViewById(R.id.lblUltAct);
		lblUltAct.setText(this.getConfig("lblUltAct"));
		TextView lblUltimoEnvio = (TextView) findViewById(R.id.lblUltimoEnvio);
		lblUltimoEnvio.setText(this.getConfig("lblUltimoEnvio"));
		
		//
		 
	       // String value = intent.getStringExtra("run");
		 bundle = getIntent().getExtras();
		 
		 if(bundle!=null) 
		 menuId=bundle.getString("m");
		 else
		 menuId="MenuPpal";
		 
		 Intent intent = getIntent();
		 /*
		 if(intent.getStringExtra("msg")!=null){
		
			 msgG=intent.getStringExtra("msg");
			String ms[]= msgG.split(":");
			if(!ms[0].equals("v")){
				alertaMsg(msgG,1);
			}
		 } 
		 */
		/* myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(GPSTracker.ACTION_MyIntentService);	 
		intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		registerReceiver(myBroadcastReceiver, intentFilter);
		*/
		// db.db.abrirBasedatos();
		// db.baseDatos.close();
	
		 /*
		 Uri packageURI = Uri.parse("package:"+MenuPpal1.class.getPackage().getName());
		 Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		 startActivity(uninstallIntent);
		 */
		// mostraDatos();
		 SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    	 	servicio_ok = sharedPreferences.getBoolean("key_servicio", true);
    	 	
    	    ///Log.e("ESTADO SERVICIO","Valor: " + servicio_ok);

	}
	
	public void mostraDatos()
	{
	   Cursor localCursor = null;
   int c=0;
	    db.abrirBasedatos();
	 localCursor = db.baseDatos.rawQuery("SELECT * FROM ramos", null);
	  
				    while (true)
				    {
				      if (!localCursor.moveToNext())
				      {
				    	 localCursor.close();
				        db.baseDatos.close();
				        
				        break;
				      }
				      Log.w("DATOS:",c+" > "+localCursor.getString(0)+";"+localCursor.getString(1));
				 //   Log.w("DATOS:",localCursor.getString(0)+";"+localCursor.getString(1)+";"+localCursor.getString(2)+";"+localCursor.getString(3)+";"+localCursor.getString(4)+";"+localCursor.getString(5)+";"+localCursor.getString(6)+";"+localCursor.getString(7)+";"+localCursor.getString(8)+";"+localCursor.getString(21));
				   c++;
			 }
				   // Log.w("CANTIDAD:",c+"");
	}		
	
	
 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        /*
	        if (requestCode == RESULT_SETTINGS) {
		    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		    	 servicio_ok = sharedPreferences.getBoolean("key_servicio", true);
		    	 servicio.activarServicios();
		    	// Toast.makeText(this, "Inicio de tarea.."+servicio_ok, Toast.LENGTH_SHORT).show();
		    	 if(servicio_ok )
		   			servicio.activarServicios();
		   		 else
		   			servicio.cancelarServicios();	
		        // should be getting called now
		    }
		    */
	        if(getConfig("registro").length()==0){
	        	finish();
	        }
	      
	    }
 

@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		 if(menuId.equals("MenuPpal"))
		getMenuInflater().inflate(R.menu.menu_ppal, menu);
		 
		 if(menuId.equals("EnviarDatos"))
				getMenuInflater().inflate(R.menu.m_enviar, menu);
		
		return true;
	}

public void irClientes(View v){
		Intent intent = new Intent();
		intent.putExtra("idCliente","");
	    intent.setClass(MenuPpal1.this, Clientes.class);
	    startActivity(intent);
		finish();
	
}
public void irAgenda(View v){
	Intent intent = new Intent();
	intent.putExtra("m","Agenda");
	intent.setClass(MenuPpal1.this, MyCalendarActivity.class);
    startActivity(intent);
   finish();

}
public void irActualizar(View v){
		Intent intent = new Intent();
	    intent.setClass(MenuPpal1.this, FormActualizar.class);
		startActivity(intent);
		finish();

}
public void irEnviar(View v){
		Intent intent = new Intent();
		intent.putExtra("m","EnviarDatos");
	    intent.setClass(MenuPpal1.this, MenuPpal.class);
	    startActivity(intent);
		finish();

}
public void irGestionVentas(View v){
	Intent intent = new Intent();
	intent.putExtra("m","GestionVentas");
	    intent.setClass(MenuPpal1.this, MenuPpal.class);
		startActivity(intent);
		finish();

}
public void irResumen(View v){
		Intent intent = new Intent();
		intent.putExtra("m","Censos");
	    intent.setClass(MenuPpal1.this, MenuPpal.class);
		startActivity(intent);
		finish();

}
public void irConfiguracion(View v){
	  Intent i = new Intent(this, Preferences.class);
	 startActivityForResult(i, RESULT_SETTINGS);

}
	public boolean onOptionsItemSelected(MenuItem item) {
	
		int id = item.getItemId();
		
		if (id == android.R.id.home) {
			atras();
	          return true;
		}
		
		if (id == R.id.mi_resetear) {
			alertaResetear();
	          return true;
		}
		
		
		

         
		if (id == R.id.mi_menu_salir) {
			alertaSalir();
		}
		/*
		if (id == R.id.mi_borrarLicencia) {
			alertaLicencia();
		}
		if (id == R.id.mi_preferencias) {
		  Intent i = new Intent(this, Preferences.class);
	
          startActivityForResult(i, RESULT_SETTINGS);
          
		}
		
		if (id == R.id.mi_descargarMapa) {
			
			 // checkAndCreateDirectory("/AxumMapa");
		     
			///  downloadFileAsync =  (DownloadFileAsync) new DownloadFileAsync().execute(fileURL);
	          
			}	
		*/
		return super.onOptionsItemSelected(item);
	}
	
	public void setMenu()
	  {
		
		     HashMap map = new HashMap();
		     
		     if(menuId.equals("MenuPpal")){
				map.put("imagen",R.drawable.user);
				map.put("id","Clientes");
				menuItems.add(map);
				 map = new HashMap();
				map.put("imagen",R.drawable.upload);
				map.put("id","Enviar Datos");
				menuItems.add(map);map = new HashMap();
				map.put("imagen",R.drawable.web);
				map.put("id","Actualizar Datos");
				menuItems.add(map); map = new HashMap();
				map.put("imagen",R.drawable.barchart);
				map.put("id","Gestion Ventas");
				menuItems.add(map);
				map = new HashMap();
				map.put("imagen",R.drawable.planilla2);
				map.put("id","Resumen Censos");
				menuItems.add(map);
				map = new HashMap();
				map.put("imagen",R.drawable.agenda);
				map.put("id","Agenda");
				int tareas=tieneTareas(fecha_calenario);
				map.put("tarea",""+tareas);
				menuItems.add(map);
				//if(tareas>0)
				//	myToast("hay "+tareas+" registrada para el dia de hoy!",fecha_calenario);
				//Toast.makeText(getBaseContext(),tieneTareas(fecha_calenario)+"", Toast.LENGTH_SHORT).show();
			
		     }
		   if(menuId.equals("EnviarDatos")){
					map.put("imagen",R.drawable.ic_upload);
					map.put("id","Enviar x Internet");
					menuItems.add(map);
					map = new HashMap();
					map.put("imagen",R.drawable.corro);
					map.put("id","Enviar x Correo");
					menuItems.add(map);
					
			     }
		   
	 if(menuId.equals("GestionVentas")){
				map.put("imagen",R.drawable.chart_bar);
				map.put("id","Avance Diario");
				menuItems.add(map);
		
				map = new HashMap();
				map.put("imagen",R.drawable.sorting12_icon);
				map.put("id","Ranking");
				menuItems.add(map);
				
			
				map = new HashMap();
				map.put("imagen",R.drawable.maps_marker);
				map.put("id","Ubicacion GPS");
				menuItems.add(map);
				/*
				map = new HashMap();
				map.put("imagen",R.drawable.maps_marker);
				map.put("id","Vendedores");
				menuItems.add(map);
				*/
			
		     }
	 if(menuId.equals("FormUbicacion")){
		  db.abrirBasedatos();
		  String v= this.getConfig("vend");
			 Cursor localCursor = db.baseDatos.rawQuery("select *  from vendedores order by codigo desc ", null);
				    while (true)
				    {
				      if (!localCursor.moveToNext())
				      { 
				        localCursor.close();
				        db.baseDatos.close();
				        break;
				      }
				  	map = new HashMap();
				      if(v.equals(localCursor.getString(0)))
				      	   map.put("imagen",R.drawable.maps_location1);
				      else
				    	  map.put("imagen",R.drawable.maps_location);
				      
				      String s=localCursor.getString(1);
				      if(s.length()>0)
				      		map.put("id",localCursor.getString(0)+"-"+s);
				      else
				    	  map.put("id",localCursor.getString(0)+"- Vendedor");
				      
				    	 menuItems.add(map);
				    
				 }
				    adapter.notifyDataSetChanged();
		
	     }
	 if(menuId.equals("FormRelevamiento")){
		 
		 String msg1=getObservacion(bundle.getString("idCliente"));
		   if(msg1.length()>0){
		    msg.setVisibility(View.VISIBLE);
		    msg.setText(msg1);
		   }
		 db.abrirBasedatos();
		 List<String> relList = Arrays.asList(bundle.getString("id_encuesta").split(";"));

			Cursor localCursor = db.baseDatos.rawQuery("select nombre,id_encuesta  from encuesta  group by id_encuesta", null);
			    while (true)
			    {
			      if (!localCursor.moveToNext())
			      { 
			        localCursor.close();
			        db.baseDatos.close();
			        break;
			      }
			     
			    ///  Log.w("data1:",localCursor.getString(1).length()+"/"+localCursor.getString(1)+"="+relList.indexOf(localCursor.getString(1))+"");
			      if(relList.indexOf(localCursor.getString(1))!=-1){
			   
			    	  map = new HashMap();
			    	 if( tieneRespuestas(localCursor.getString(1))>0)
			    	  map.put("imagen",R.drawable.encuesta2);
			    	 else
			    	  map.put("imagen",R.drawable.relevamiento);
			    	 
			    	  map.put("id",localCursor.getString(0));
			    	  map.put("id_encuesta",localCursor.getString(1));
			    	  map.put("id_encuestas",bundle.getString("id_encuesta"));
			    	  menuItems.add(map);
			      }
			 
			    }
	     }
	 
	 	if(menuId.equals("Censos")){
	 		db.abrirBasedatos();
			 Cursor localCursor = db.baseDatos.rawQuery("select id_encuesta,dato9  from respuestas  group by id_encuesta", null);
				    while (true)
				    {
				      if (!localCursor.moveToNext())
				      { 
				        localCursor.close();
				        db.baseDatos.close();
				        break;
				      }
				  
				          map = new HashMap();
				    	  map.put("imagen",R.drawable.survey);
				    	  map.put("id",localCursor.getString(1));
				    	  map.put("id_encuesta",localCursor.getString(0));
				    	 menuItems.add(map);
				 }
			
	     }
}
	
	public void openUpdate() throws Exception
	{
		String link = "http://www.axum.com.ar/jar/sup/a/Lider.apk";
		Intent intent = null;
		intent = new Intent(intent.ACTION_VIEW,Uri.parse(link));
		startActivity(intent);
	}
	
	 public void alertaSalir() {
		    new AlertDialog.Builder(this)
		    		.setTitle("SALIR")
		           .setMessage("Desea salir?")
		           .setCancelable(false)
		            .setIconAttribute(android.R.attr.alertDialogIcon)
		           .setPositiveButton("Si", new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		            	  
	       	    		finish();              
	       	    	
		               }
		           })
		           .setNegativeButton("No", null)
		           .show();
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
		               }
		           })
		           .setNegativeButton("No", null)
		           .show();
		}
	 
	 public void alertaResetear() {
		    new AlertDialog.Builder(this)
		    		.setTitle("ALERTA!")
		           .setMessage("Esta seguro de desea poner como NO ENVIADO todas las respuestas?")
		           .setCancelable(false)
		            .setIconAttribute(android.R.attr.alertDialogIcon)
		           .setPositiveButton("Si", new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		            	         
		            	   setFlagFespuestas();
		               }
		           })
		           .setNegativeButton("No", null)
		           .show();
		}
	 public void alertaMsg(String msgg, final int i) {
	
		    new AlertDialog.Builder(this)
		    		.setTitle("Alerta")
		           .setMessage(msgg)
		           .setCancelable(false)
		            .setIconAttribute(android.R.attr.alertDialogIcon)
		           .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		            if(i==0){
			            	   try {
								openUpdate();
								finish(); 
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
		            }  
		            
	       	    
		            	 //  Toast.makeText(this, "el usuario no permitió a su GPS", Toast.LENGTH_LONG).show();
		               }
		           })
		          .setNegativeButton("No Ahora", null) 
		           .show();
		}
	 
 public void atras(){
		alertaSalir();
	 /*  if(menuId.equals("MenuPpal"))
	    		alertaSalir();
	    		
	    	if(menuId.equals("EnviarDatos")){
	    	    Intent mainIntent = null;
	  	        mainIntent = new Intent().setClass(MenuPpal1.this, MenuPpal1.class);
	  	        mainIntent.putExtra("m","MenuPpal");
	  	    
	  	        startActivity(mainIntent);
	  	        finish();
	    		}
	    	if(menuId.equals("FormRelevamiento")){
	    	    Intent mainIntent = null;
	  	        mainIntent = new Intent().setClass(MenuPpal1.this, Clientes.class);
	  	        mainIntent.putExtra("idCliente",bundle.getString("idCliente"));
	  	        startActivity(mainIntent);
	  	        finish();
	    		}
	    	
	    	if(menuId.equals("GestionVentas")){
	    	    Intent mainIntent = null;
	  	        mainIntent = new Intent().setClass(MenuPpal1.this, MenuPpal1.class);
	  	        mainIntent.putExtra("m","MenuPpal");
	  	        startActivity(mainIntent);
	  	        finish();
	    		}
	    	if(menuId.equals("Censos")){
	    	    Intent mainIntent = null;
	  	        mainIntent = new Intent().setClass(MenuPpal1.this, MenuPpal1.class);
	  	        mainIntent.putExtra("m","MenuPpal");
	  	        startActivity(mainIntent);
	  	        finish();
	    		}
	  	if(menuId.equals("FormUbicacion")){
	           Intent mainIntent = null;
	           mainIntent = new Intent().setClass(MenuPpal1.this, MenuPpal1.class);
	           mainIntent.putExtra("m","GestionVentas");
	           startActivity(mainIntent);
	           finish();
  		}
  		*/
	 }
	 
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
		  	super.onKeyDown(keyCode, event);
		  	    	if (keyCode == KeyEvent.KEYCODE_BACK) {
		  	    		
		  	    		atras();
		  	    		
		  	    	}
		  	    	return false;
		  	    }
	
	 
 public String getConfig(String p1){
			SharedPreferences prefs = getSharedPreferences("config",getBaseContext().MODE_PRIVATE);
				 return  prefs.getString(p1, "");
		}	
	 
 public void guardarConfig(String paramString1, String paramString2)
	 {
	   SharedPreferences.Editor localEditor = getSharedPreferences("config", 0).edit();
	   localEditor.putString(paramString1, paramString2);
	   localEditor.commit();
	 }	
 public int getCountPedidos(int i)
	{	
		db.abrirBasedatos();
		Cursor localCursor = db.baseDatos.rawQuery("select count(id_cliente) from respuestas where flag =0 ", null);
		localCursor.moveToFirst();
		int count= localCursor.getInt(0);
		localCursor.close();
		db.baseDatos.close();
		return count;
	}	
 public int getCountPedidos()
	{	
		db.abrirBasedatos();
		Cursor localCursor = db.baseDatos.rawQuery("select count(id_cliente) from respuestas ", null);
		localCursor.moveToFirst();
		int count= localCursor.getInt(0);
		localCursor.close();
		db.baseDatos.close();
		return count;
	}	
 
 
 public int tieneRespuestas(String id_encuesta)
	{	
		db.abrirBasedatos();
		Cursor localCursor = db.baseDatos.rawQuery("select count(id_pregunta) from respuestas where id_encuesta ='"+id_encuesta+"' and id_cliente="+bundle.getString("idCliente")+" ", null);
		localCursor.moveToFirst();
		int count= localCursor.getInt(0);
		localCursor.close();
		db.baseDatos.close();
		return count;
	}	
public void  setFlagFespuestas(){
	
	Date today = Calendar.getInstance().getTime();
	SimpleDateFormat formatter = new SimpleDateFormat("MM");
  String periodo = formatter.format(today);
	db.abrirBasedatos();
	 db.baseDatos.execSQL("update respuestas set flag='0' where  strftime('%m', fecha)='"+periodo+"' ");
	
	 db.baseDatos.close();
}
 
public int getPosicionCli(String vend)
	{	
		   int count= 0;
			db.abrirBasedatos();
			Cursor localCursor = db.baseDatos.rawQuery("select count(codigo) from clientesposicion where vend='"+vend+"'  ", null);
			localCursor.moveToFirst();
			if(localCursor.getString(0)!=null)
			 count= localCursor.getInt(0);
			localCursor.close();
			db.baseDatos.close();
		if(vend.equals(getConfig("vend")))
			count=1;
			return count;
	}
public int getCantidadClientes()
{	
	   int count= 0;

		db.abrirBasedatos();
		Cursor localCursor = db.baseDatos.rawQuery("select count(codigo) from clientes ", null);
		localCursor.moveToFirst();
		if(localCursor.getCount()>0){
		 	localCursor.moveToFirst();
		 	count=localCursor.getInt(0);
		 	}
		localCursor.close();
		db.baseDatos.close();
	   
		return count;
}
 public String getObservacion(String id_cliente)
 {	
	 String ct="";
 	db.abrirBasedatos();
	// Log.d("base error","select codigo,descripcion from articulos where codigo="+id_articulo.trim()+" " );
 	Cursor localCursor = db.baseDatos.rawQuery("select dato10 from clientes where codigo="+id_cliente.trim()+" ", null);
 
 	if(localCursor.getCount()>0){
 	    localCursor.moveToFirst();
 	    ct=localCursor.getString(0);
 	}
 	localCursor.close();
 	db.baseDatos.close();
 	return ct;
 }

 @SuppressWarnings("deprecation")
public Dialog alerta(String s) {
 	 
     AlertDialog.Builder builder = new AlertDialog.Builder(this);
  
     builder.setTitle("Informacion")
             .setIcon(
                     getResources().getDrawable(
                             android.R.drawable.ic_dialog_info))
             .setMessage(s)
             .setNeutralButton(android.R.string.ok, new OnClickListener() {
  
                 @Override
                 public void onClick(DialogInterface arg0, int arg1) {
  
                 }
             });
  
     return builder.create();
 }

 public void borrarLicencia()
	  {
		 guardarConfig("registro","");
	     guardarConfig("vend","");
	     guardarConfig("super","");
	     guardarConfig("estandar","0");
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
 
 public void borrarRespuestas()
 {
	 db.abrirBasedatos();
	 db.baseDatos.execSQL(" DELETE FROM respuestas WHERE id_cliente IN (SELECT id_cliente FROM respuestas ORDER BY fecha DESC  LIMIT 1000)"); 
	
	 db.baseDatos.close();
	 finish();
 }
 
 public void borrarCoordenadas()
 {
	 db.abrirBasedatos();
	 db.baseDatos.execSQL("DELETE FROM clientesposicion where dato2 <> '"+getFecha()+"'"); 
	db.baseDatos.close();
	
 }
 
 public String getFecha()
 {
 	        Calendar calendar = Calendar.getInstance();
 	        StringBuffer stringbuffer = new StringBuffer(10);
 	        int i1 = calendar.get(1);
 	        int j1 = calendar.get(2);
 	        int k1 = calendar.get(5);
 	        stringbuffer.append(i1);
 	        stringbuffer.append('-');
 	        if(j1 < 9)
 	            stringbuffer.append('0');
 	        stringbuffer.append(++j1).append('-');
 	        if(k1 < 10)
 	            stringbuffer.append('0');
 	        stringbuffer.append(k1);
 	        return stringbuffer.toString();
 }

  
 //function to verify if directory exists
 public void checkAndCreateDirectory(String dirName){
     File new_dir = new File( rootDir + dirName );
     if( !new_dir.exists() ){
         new_dir.mkdirs();
     }
 }
  
 //our progress bar settings


 private void copyFile(InputStream in, OutputStream out) throws IOException {
     byte[] buffer = new byte[1024];
     int read;
     while((read = in.read(buffer)) != -1){
       out.write(buffer, 0, read);
     }
 }
 public Bitmap getRoundedBitmap(Bitmap bitmap) {
	    final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
	    final Canvas canvas = new Canvas(output);

	    final int color = Color.RED;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, 24, 24);
	    final RectF rectF = new RectF(rect);

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawOval(rectF, paint);
	    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    bitmap.recycle();
	    return output;
	    }
 
 public int tieneTareas(String fecha)
	{	
		db.abrirBasedatos();
		// Log.d("SQL", "select count(codigo) from tareas where fecha like '"+fecha+"' ");
		Cursor localCursor = db.baseDatos.rawQuery("select count(codigo) from tareas where fecha like '"+fecha+"' ", null);
		localCursor.moveToFirst();
		int count= localCursor.getInt(0);
		localCursor.close();
		db.baseDatos.close();
		return count;
	}



}
