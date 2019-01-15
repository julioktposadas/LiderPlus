package com.axum.liderplus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.axum.config.Db;
import com.axum.mapa.FormMapaClientes;
import com.axum.mapa.FormMapaClientes2;
import com.axum.mapa.FormMapaGoogle;






import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
//************************** 
import android.widget.Toast;
 
///***************************************************

@SuppressLint("NewApi")
public class MenuPpal extends Activity {
	
 String  meses[] = {"","Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
ArrayList<HashMap<String, String>> menuItems ;
 Bundle bundle ;
SimpleAdapter adapter;
String menuId="";
ListView lv ;
Db db;

int var[];
private Calendar _calendar;
String msgG;
private static final int RESULT_SETTINGS = 1;
public static final String LOG_TAG = "Downloader";
private static final String dateTemplate = "d-MMMM-yyyy";
//initialize our progress dialog/bar
private ProgressDialog mProgressDialog;
public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
 
//initialize root directory
File rootDir = Environment.getExternalStorageDirectory();
boolean cancelDownload = false;
DownloadFileAsync downloadFileAsync= null;
public String fileName = "argentina.map";
public String fileURL = "http://mk000193.ferozo.com/argentina.map";
String fecha_calenario="";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new Db(this);
		setContentView(R.layout.menu_lista);
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		getActionBar().setHomeButtonEnabled(true); 
		getActionBar().setDisplayHomeAsUpEnabled(true);
		String myVersion = android.os.Build.VERSION.RELEASE;
	// Log.d("VERSION", var[5]+"");
	
		
	
		
		 menuItems = new ArrayList<HashMap<String, String>>();
		 bundle = getIntent().getExtras();
		 menuId=bundle.getString("m");
		/* 
		 if(bundle.getString("msg")!=null){
			 msgG=bundle.getString("msg");
			String ms[]= msgG.split(":");
			if(!ms[0].equals("v")){
				alertaMsg(msgG,1);
			}
		 }
		 */
		 if(menuId.equals("MenuPpal"))
		 setTitle("Menu  S:"+this.getConfig("super")+" V:"+this.getConfig("vend"));
		 if(menuId.equals("FormRelevamiento")){
			 setTitle(getRamo(bundle.getString("idCliente"))+"    (C:"+bundle.getString("idCliente")+" )"); 
		}
		 if(menuId.equals("EnviarDatos"))
			  setTitle("Enviar Datos"); 
		 if(menuId.equals("Censos"))
			  setTitle("Resumen Censos"); 
		 if(menuId.equals("GestionVentas"))
			  setTitle("Gestion Ventas");
		 
		 if(menuId.equals("FormUbicacion"))
			  setTitle("Ubicacion x Vend"); 
		 
	    //adapter = new SimpleAdapter(this, menuItems,R.layout.row_simple_relv,new String[] {"imagen","id","tarea","progress", "progress2" }, new int[] {R.id.icon,R.id.row_toptext,R.id.t_tarea,R.id.tex_avance,R.id.pb_relv});
	  
	    
		 adapter = new SimpleAdapter(this, menuItems,
	             R.layout.row_simple_icono_a_la_derecha, new String[] {
	                     "item", "progress", "progress","censo"}, new int[] {
	                     R.id.row_toptext, R.id.text_progress, R.id.progress1,R.id.ly_item});
		
adapter.setViewBinder(new SimpleAdapter.ViewBinder() { 
		   public boolean setViewValue(View view, Object data, String textRepresentation) {
			   String value="";
			  if(data!=null)
			   value = data.toString();
			   int b=0;
			 
			//  Log.w("LOG", textRepresentation);
	           if (view.getId() == R.id.progress1) {
	        	   ProgressBar progressbar=((ProgressBar) view);
	        	   
	        	   if(value.indexOf("/")!=-1) {
	                   String array_res[]=value.split("/");
	                   int p=(int) ((Integer.parseInt(array_res[0]) / (float) Integer.parseInt(array_res[1]) * 100));
	                  
	                   progressbar.setProgress(p);
	                   Resources res = getResources();
	                   Rect bounds = progressbar.getProgressDrawable().getBounds();
		                 
		               if(p == 100)
		               {
		            	   progressbar.setProgressDrawable(res.getDrawable(R.drawable.progressbar1));
		               }
		               else
		               {
		            	   progressbar.setProgressDrawable(res.getDrawable(R.drawable.progressbar0));
		               }
		               progressbar.getProgressDrawable().setBounds(bounds);
		             
	        	   }else {
	        		   progressbar.setVisibility(View.GONE);
	        		 
	        	}
	        	   
	        	   return true;
	           }
	         if (view.getId() == R.id.text_progress) {
	        	  TextView tvitem=((TextView) view);
				   
	        	 tvitem.setText(value);
        		   if(value.indexOf("/")==-1)
        			   tvitem.setVisibility(View.GONE);
        		   
        		   return true;
        	       }
	       if (view.getId() == R.id.row_toptext) {
	    	   
	    	   TextView tvpb=((TextView) view);
	    	  if(value.indexOf("*")!=-1) {
      		    tvpb.setGravity(Gravity.CENTER);
      		   }
      		 tvpb.setText(value.replace("*",""));
      		 return true;
      	       }
       if (view.getId() == R.id.ly_item) {
	    	   
    	   RelativeLayout ly=((RelativeLayout) view);
    	   if(value.contains("EEV"))
    	   ly.setBackground(ContextCompat.getDrawable(MenuPpal.this, R.drawable.item_selector_menu_claro));
    	   else
    		ly.setBackground(ContextCompat.getDrawable(MenuPpal.this, R.drawable.item_selector_menu));
    		   
	    	
      		 return true;
      	       }
	           return false;
	       
	       }
	   });
	  
	    
	  // mostraDatos();	 
lv = (ListView) findViewById(R.id.listR2);
lv.setAdapter(adapter);

lv.setOnItemClickListener(new OnItemClickListener() {
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if( menuId.equals("MenuPpal")){
			
		if(position==0){
				Intent intent = new Intent();
				intent.putExtra("idCliente","");
      	  	    intent.setClass(MenuPpal.this, Clientes.class);
      	  	    startActivity(intent);
      	  		finish();
			}
		if(position==1){
				Intent intent = new Intent();
				intent.putExtra("m","EnviarDatos");
      	  	    intent.setClass(MenuPpal.this, MenuPpal.class);
      	  	   startActivity(intent);
      	  		finish();
			}
			
		if(position==2){
				Intent intent = new Intent();
      	  	    intent.setClass(MenuPpal.this, FormActualizar.class);
      	  		startActivity(intent);
      	  		finish();
			}
		if(position==3){
				Intent intent = new Intent();
				intent.putExtra("m","GestionVentas");
      	  	    intent.setClass(MenuPpal.this, MenuPpal.class);
      	  		startActivity(intent);
      	  		finish();
			}
	   if(position==4){
			Intent intent = new Intent();
			intent.putExtra("m","Censos");
  	  	    intent.setClass(MenuPpal.this, MenuPpal.class);
  	  		startActivity(intent);
  	  		finish();
		}
	   
	   if(position==5){
				Intent intent = new Intent();
				intent.putExtra("m","Agenda");
	  	  	    intent.setClass(MenuPpal.this, MyCalendarActivity.class);
	  	  		startActivity(intent);
	  	  		finish();
			}
		
		}
	if( menuId.equals("FormUbicacion")){
		
	//	if(getConfig("empresa").contains("GPS")) {
				lv.setEnabled(false);
			    lv.setClickable(false);
			    String s1 ="";
			    if(menuItems.get(position).get("item").indexOf("-")>0)
		         s1 = menuItems.get(position).get("item").split("-")[0];
			    else
			     s1 = menuItems.get(position).get("item").split(" ")[1];
				Intent intent = new Intent();
				intent.putExtra("m","FormUbicacion");
				intent.putExtra("vendId",s1);
				intent.putExtra("idCliente","");
			   intent.putExtra("coordenada",getPosicionCli(s1)+"");
				intent.setClass(MenuPpal.this, FormMapaClientes2.class);
	  	  		startActivity(intent);
	  	  		finish();
	//}else {
	//	alertaGPS();
          
	//}
	}
	
	if( menuId.equals("GestionVentas")){
			
			if(position==0){
				
				Intent intent = new Intent();
				intent.putExtra("m","GestionVentas");
				intent.putExtra("data","");
	  	  	    intent.setClass(MenuPpal.this, FormAvance.class);
	  	  		startActivity(intent);
	  	  		finish();
          }
	
	   if(position==1){
		   Intent intent = new Intent();
			intent.putExtra("m","GestionVentas");
			intent.putExtra("data","");
 	  	    intent.setClass(MenuPpal.this, FormRanking.class);
 	  		startActivity(intent);
 	  		finish();

           }
	   if(position==2){
		   
		   Intent intent = new Intent();
			intent.putExtra("m","FormUbicacion");
 	  	    intent.setClass(MenuPpal.this, MenuPpal.class);
 	  		startActivity(intent);
 	  		finish();
	   }
   if(position==3){
		   
		   Intent intent = new Intent();
			intent.putExtra("m","FormUbicacion");
 	  	    intent.setClass(MenuPpal.this, MenuVendedor.class);
 	  		startActivity(intent);
 	  		finish();
	   }

			
	
	}
	
	if( menuId.equals("EnviarDatos")){
		
		if(position==0){

			if( getCountPedidos(0)>0){
				String[] url={"http://www.axum.com.ar//"+getConfig("empresa")+"/ControllerJME.aspx?Sup=&Ver=11.76&IMEI=&cmd=S_Respuestas&Vend="+getConfig("super")};
				EnviarTask http= new EnviarTask(MenuPpal.this);
                 http.execute(url);
           	 }else{
           		
           		 alerta("No hay datos para enviar").show();
           	}
		}
	 if(position==1){
		// CopyFile http= new CopyFile(MenuPpal.this);
       //  http.execute("");
	}
	 
	 
	
	 
}	
	
	if( menuId.equals("FormRelevamiento")){
		
		   String s2 = menuItems.get(position).get("id_encuesta");
		   
		   if(s2.contains("ENCUESTA")) {
			String id_encuenta=tieneEDV(s2);
				 if(id_encuenta.length()>0 && !s2.equals(id_encuenta) ) {
					 showError("El cliente ya tiene cargado un estandar! " );
					//Log.d("MENU1", d+ "");
					
					return;
				}
		   }
		  
		   Intent intent = new Intent();
		   //intent.putExtra("idCliente",bundle.getString("idCliente"));
		   intent.putExtra("cliente",bundle.getString("idCliente"));
	       intent.putExtra("id_encuesta",s2);
		   intent.setClass(MenuPpal.this, FormEncuesta.class);
		   startActivity(intent);
		   finish();

			}
	if( menuId.equals("Censos")){
		
		   String s2 = menuItems.get(position).get("id_encuesta");
		   Intent intent = new Intent();
		  intent.putExtra("id_encuesta",s2);
	       intent.setClass(MenuPpal.this, FormResumenCensos.class);
		   startActivity(intent);
		   finish();

			}	
	
	}
	});
				
	int rtas_dias= Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("guardar_rtas_dias", "9000")).intValue();
	int  rta=getCountPedidos();
	if(rta>=rtas_dias)
	borrarRespuestas();   
	
	borrarCoordenadas();
	/// getFecha()
	
	//**************************
	_calendar = Calendar.getInstance(Locale.getDefault());

	fecha_calenario=(String) DateFormat.format(dateTemplate,_calendar.getTime());
	
	  
	  

 
 setMenu();
 //CopyAssets();

 

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		 if(menuId.equals("MenuPpal"))
		getMenuInflater().inflate(R.menu.menu_ppal, menu);
		 
		 if(menuId.equals("EnviarDatos"))
				getMenuInflater().inflate(R.menu.m_enviar, menu);
		
		return true;
	}

	@Override
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
		/*
		if (id == R.id.mi_log) {
			  String appDirectory = Environment.getExternalStorageDirectory().toString();
			  File logFile = new File( appDirectory ,"LiderLog.txt");
		if(logFile.exists())	
		new EnviarPdf(this,"LiderLog.txt").execute(new String[]{"200"});
		else
		Toast.makeText(this, "No Hay Archivo para enviar!", Toast.LENGTH_LONG).show();
		
		
		}
		*/
		

         
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
			
			  checkAndCreateDirectory("/AxumMapa");
		     
			  downloadFileAsync =  (DownloadFileAsync) new DownloadFileAsync().execute(fileURL);
	          
			}	
		*/
		return super.onOptionsItemSelected(item);
	}
	
	public void setMenu()
	  {
		
		     HashMap map = new HashMap();
		     
	
		   if(menuId.equals("EnviarDatos")){
					
                    map.put("item","Enviar x Internet*");
                    map.put("progress","0");
					menuItems.add(map);
					map = new HashMap();
					//map.put("imagen",null);
					//map.put("id","Enviar x Correo");
					//menuItems.add(map);
					
			     }
		   
	 if(menuId.equals("GestionVentas")){
		       map.put("progress","0");
				map.put("item","Avance Diario*");
				
				menuItems.add(map);
		
				map = new HashMap();
				 map.put("progress","0");
				map.put("item","Ranking*");
				menuItems.add(map);
				
			
				map = new HashMap();
				 map.put("progress","0");
				map.put("item","Ubicacion GPS*");
				menuItems.add(map);
			/*	
				map = new HashMap();
				 map.put("progress","0");
				map.put("item","Vendedores Online*");
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
				  	 map.put("progress","0");
				
				      
				      String s=localCursor.getString(1);
				      if(s.length()>0)
				      		map.put("item",localCursor.getString(0)+"-"+s+"*");
				      else
				    	  map.put("item",localCursor.getString(0)+"- Vendedor*");
				      
				    	 menuItems.add(map);
				    
				 }
				    adapter.notifyDataSetChanged();
		
	     }
	 if(menuId.equals("FormRelevamiento")){
		 
		
		 db.abrirBasedatos();
		
		 menuItems.clear();
			Cursor localCursor = db.baseDatos.rawQuery("select nombre,id_encuesta  from encuesta  group by id_encuesta", null);
			    while (true)
			    {
			      if (!localCursor.moveToNext())
			      { 
			        localCursor.close();
			        db.baseDatos.close();
			        adapter.notifyDataSetChanged(); 
			        break;
			      }
			      
			    int preguntas=getContPreguntas(localCursor.getString(1));
			    int respuesta=tieneRespuestas(localCursor.getString(1));
			    String nombre=localCursor.getString(0);
			 	 if(nombre.indexOf("-")!=-1) {
		    		 String n[]=nombre.split("-");
		    		 if(n.length==3)
		    			 nombre=n[1]+"-"+n[2];
		    	 }
			     map = new HashMap();
			     map.put("item",nombre.toLowerCase());
		      	 map.put("progress",respuesta+"/"+preguntas);
		      	 map.put("censo",nombre);
		      	
		      	 map.put("id_encuesta",localCursor.getString(1));
		    	 map.put("id_encuestas",bundle.getString("id_encuesta"));
		      	 menuItems.add(map);
			
			 
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
				      String nombre=localCursor.getString(1);
				 	 if(nombre.indexOf("-")!=-1) {
			    		 String n[]=nombre.split("-");
			    		 if(n.length==3)
			    			 nombre=n[1]+"-"+n[2];
			    	 }
				          map = new HashMap();
				          map.put("progress","0");
				    	  map.put("item",nombre+"*");
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
	 public void alertaGPS() {
		    new AlertDialog.Builder(this)
		    		.setTitle("SERVICIO GPS INACTIVO")
		           .setMessage("Para activar el servicio GPS debe comunicarse con Axum vm !")
		           .setCancelable(false)
		            .setIconAttribute(android.R.attr.alertDialogIcon)
		           .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		            	  
	       	    		          
	       	    	
		               }
		           })
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
			
	   if(menuId.equals("MenuPpal"))
	    		alertaSalir();
	    		
	    	if(menuId.equals("EnviarDatos")){
	    	    Intent mainIntent = null;
	  	        mainIntent = new Intent().setClass(MenuPpal.this, MenuPpal1.class);
	  	        mainIntent.putExtra("m","MenuPpal");
	  	    
	  	        startActivity(mainIntent);
	  	        finish();
	    		}
	    	if(menuId.equals("FormRelevamiento")){
	    	    Intent mainIntent = null;
	  	        mainIntent = new Intent().setClass(MenuPpal.this, Clientes.class);
	  	        mainIntent.putExtra("idCliente",bundle.getString("idCliente"));
	  	        startActivity(mainIntent);
	  	        finish();
	    		}
	    	
	    	if(menuId.equals("GestionVentas")){
	    	    Intent mainIntent = null;
	  	        mainIntent = new Intent().setClass(MenuPpal.this, MenuPpal1.class);
	  	        mainIntent.putExtra("m","MenuPpal");
	  	        startActivity(mainIntent);
	  	        finish();
	    		}
	    	if(menuId.equals("Censos")){
	    	    Intent mainIntent = null;
	  	        mainIntent = new Intent().setClass(MenuPpal.this, MenuPpal1.class);
	  	        mainIntent.putExtra("m","MenuPpal");
	  	        startActivity(mainIntent);
	  	        finish();
	    		}
	  	if(menuId.equals("FormUbicacion")){
	           Intent mainIntent = null;
	           mainIntent = new Intent().setClass(MenuPpal.this, MenuPpal.class);
	           mainIntent.putExtra("m","GestionVentas");
	           startActivity(mainIntent);
	           finish();
  		}
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
		//db.abrirBasedatos();
		Cursor localCursor = db.baseDatos.rawQuery("select count(id_pregunta) from respuestas where id_encuesta ='"+id_encuesta+"' and id_cliente="+bundle.getString("idCliente")+" ", null);
		localCursor.moveToFirst();
		int count= localCursor.getInt(0);
		localCursor.close();
		//db.baseDatos.close();
		return count;
	}
 public String tieneEDV(String id_encuesta)
	{	
	    String nombre="";
		db.abrirBasedatos();
		java.util.Date date= new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = (cal.get(Calendar.MONTH)+1);
		
		//and LOWER(dato9) like '%"+meses[month].toLowerCase()+"%' 
		Cursor localCursor = db.baseDatos.rawQuery("select id_encuesta,LOWER(dato9) from respuestas where id_encuesta like '%ENCUESTA%'  and id_cliente="+bundle.getString("idCliente")+" and LOWER(dato9) like '%"+meses[month].toLowerCase()+"%' ", null);
		
		  while (true)
		    {
		      if (!localCursor.moveToNext())
		      { 
		        localCursor.close();
		        db.baseDatos.close();
		        break;
		      }
		       nombre=localCursor.getString(0);
		       Log.w("MES",localCursor.getString(1)+"=="+meses[month].toLowerCase());
		 }
	
		return nombre;
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
 class DownloadFileAsync extends AsyncTask<String, String, String> {
     
     @Override
     protected void onPreExecute() {
         super.onPreExecute();
         showDialog(DIALOG_DOWNLOAD_PROGRESS);
     }
     
     
     public void onCancel(DialogInterface dialog) {
    	    // TODO Auto-generated method stub
    	    DownloadFileAsync.this.cancel(true);
    	    Log.d("on click cancel true","true");
    }
      
     @Override
     protected String doInBackground(String[] aurl) {

         try {
             //connecting to url
             URL u = new URL(fileURL);
             HttpURLConnection c = (HttpURLConnection) u.openConnection();
             c.setRequestMethod("GET");
             c.setDoOutput(true);
             c.connect();
              
             //lenghtOfFile is used for calculating download progress
             int lenghtOfFile = c.getContentLength();
              
             //this is where the file will be seen after the download
             FileOutputStream f = new FileOutputStream(new File(rootDir + "/AxumMapa/", fileName));
             //file input is from the url
             InputStream in = c.getInputStream();

             //here’s the download code
             byte[] buffer = new byte[1024];
             int len1 = 0;
             long total = 0;
              
             while ((len1 = in.read(buffer)) > 0) {

            	 if (isCancelled()) {
            		   f.close();
            		    return null;
            		}
            	 
                 total += len1; //total = total + len1
                 publishProgress("" + (int)((total*100)/lenghtOfFile));
                 f.write(buffer, 0, len1);
             }
             f.close();
              
         } catch (Exception e) {
             Log.d(LOG_TAG, e.getMessage());
         }
          
         return null;
     }
      
     protected void onProgressUpdate(String[] progress) {
          //Log.d(LOG_TAG,progress[0]+"");
          mProgressDialog.setProgress(Integer.parseInt(progress[0]));
     }

     @Override
     protected void onPostExecute(String unused) {
         //dismiss the dialog after the file was downloaded
         dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
     }
 }
  
 //function to verify if directory exists
 public void checkAndCreateDirectory(String dirName){
     File new_dir = new File( rootDir + dirName );
     if( !new_dir.exists() ){
         new_dir.mkdirs();
     }
 }
  
 //our progress bar settings
 @Override
 protected Dialog onCreateDialog(int id) {
     switch (id) {
         case DIALOG_DOWNLOAD_PROGRESS: //we set this to 0
             mProgressDialog = new ProgressDialog(this);
             mProgressDialog.setMessage("Descargando Mapa. Por favor espere...");
             mProgressDialog.setIndeterminate(false);
             mProgressDialog.setMax(100);
             mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
             mProgressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar2));
            mProgressDialog.setCancelable(false);
             
             
             mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            	    @Override
            	    public void onClick(DialogInterface dialog, int which) {
            	    	 downloadFileAsync.cancel(true);
            	    	 File file =new File(rootDir + "/AxumMapa/", fileName);
            	    	 file.delete();
            	    	 mProgressDialog.dismiss();
            	    }
            	});
            
             mProgressDialog.show();
             
             return mProgressDialog;
         default:
             return null;
     }
 }
 /*
 private void CopyAssets() {
     AssetManager assetManager = getAssets();
     String[] files = null;
     try {
         files = assetManager.list("Files");
     } catch (IOException e) {
         Log.e("tag", e.getMessage());
     }

     for(String filename : files) {
         System.out.println("File name => "+filename);
         InputStream in = null;
         OutputStream out = null;
         try {
           in = assetManager.open("Files/"+filename);   // if files resides inside the "Files" directory itself
           out = new FileOutputStream(getExternalFilesDir(null)+"/" + filename);
           copyFile(in, out);
           in.close();
           in = null;
           out.flush();
           out.close();
           out = null;
         } catch(Exception e) {
             Log.e("tag", e.getMessage());
         }
     }
 }
 */
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

 public String getRamo(String codigo)
	{	
		db.abrirBasedatos();
		String count="";
		// Log.d("SQL", "select count(codigo) from tareas where fecha like '"+fecha+"' ");
		Cursor localCursor = db.baseDatos.rawQuery("select ramos.descripcion,clientes.*  from clientes, ramos where clientes.ramo= ramos.codigo and clientes.codigo ='"+codigo+"' group by clientes.codigo", null);
		if(localCursor.getCount()>0){
		localCursor.moveToFirst();
		 count= localCursor.getString(0);
		}
		localCursor.close();
		db.baseDatos.close();
		return count;
	}
 
 public int getContPreguntas(String  data)
 {	
 	int ct=0;
 	//db.abrirBasedatos();
 	Cursor localCursor = db.baseDatos.rawQuery("select  count(id_pregunta) from encuesta where id_encuesta='"+data+"' and LOWER(tipodato) NOT LIKE 'titulo' and LOWER(tipodato) NOT LIKE 'llave'  and LOWER(tipodato) NOT LIKE 'subtitulo' ", null);
 	if(localCursor.getCount()>0){
 	localCursor.moveToFirst();
 	ct= localCursor.getInt(0);
 	}
 	localCursor.close();
 	//db.baseDatos.close();
 	return ct;
 }
 public void mostraDatos()
	{
	   Cursor localCursor = null;
      int c=0;
	    db.abrirBasedatos();
	 localCursor = db.baseDatos.rawQuery("SELECT * FROM respuestas", null);
	  
				    while (true)
				    {
				      if (!localCursor.moveToNext())
				      {
				    	 localCursor.close();
				        db.baseDatos.close();
				        
				        break;
				      }
				    Log.w("RES:",localCursor.getString(0)+";"+localCursor.getString(1)+";"+localCursor.getString(2)+";"+localCursor.getString(3)+";"+localCursor.getString(4)+";"+localCursor.getString(5)+";"+localCursor.getString(6));
				   c++;
			 }
				   // Log.w("CANTIDAD:",c+"");
	}	
 
 public void showError(String result)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.cancel();
			}
		});
		builder.setMessage(result);
		builder.setTitle("ERROR");
		builder.setIconAttribute(android.R.attr.alertDialogIcon);
		AlertDialog alert = builder.create();
		alert.setCancelable(false);
		alert.show();
	}
}
