package com.axum.liderplus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.axum.camara.PhotoCapture;
import com.axum.config.Db;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;





@SuppressLint("NewApi")
public class FormEncuesta extends Activity {

    TextView latTx;
    LinearLayout ll;
    TextView lonTx;
    int motivoVenta= 0;
    Spinner spin;
  
   String id_pregunta="",s_bloque="",id_ramo="",ramo="",nombre_rel="";
    String idfecha="";
    Button bt_aceptar,bt_atras;
    Bundle bundle ;
    LinearLayout parent=null;
    private Calendar calendar;
    InputMethodManager imm ;
   String id_cliente="",id_encuesta="";
    private int year, month, day;
    List<String> listaLLave= new ArrayList<String>();
    List<String> titulosArray= new ArrayList<String>();
    private LocationManager locationManager;
    double longitudeGPS, latitudeGPS;//
   // latitudeGPS
    AlertDialog alertDialogGPS;
    private static final int ACTIVITY_SELECT_IMAGE = 1020;
    Db db;
    ProgressBar pb_avence;
    TextView tex_pb;
    int preguntas=0;
    boolean inicio =false;
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db= new Db(this);
      
		 setContentView(R.layout.a_form_relev2);
		 getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		 getActionBar().setHomeButtonEnabled(true); 
		 getActionBar().setDisplayHomeAsUpEnabled(true);
			
		 pb_avence = (ProgressBar) this.findViewById(R.id.pb_avence);
		 tex_pb = (TextView) this.findViewById(R.id.tex_pb);
		 
		alertDialogGPS =  new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog)).create();  
		
		bundle = getIntent().getExtras();
	
        setTitle(bundle.getString("cliente"));
        id_cliente=bundle.getString("cliente").split("-")[0];
        id_encuesta=bundle.getString("id_encuesta");
        mostrarPreguntas();
        calendar = Calendar.getInstance();
        
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        
       //locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
       //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);
              
         preguntas=getContPreguntas(id_encuesta);
       id_ramo=getRamos();
   	    nombre_rel=getNombrePegunta(id_encuesta);	
      // 	ocultarTeclado();
        //**********************************************************************
  
   	actualizarAvence();
   	 
   	 //*************************************************************************
   	this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
   	inicio= true;
  // 	mostraDatos();
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
			    Log.w("OT:",localCursor.getString(0)+";"+localCursor.getString(1)+";"+localCursor.getString(2)+";"+localCursor.getString(3)+";"+localCursor.getString(4)+";"+localCursor.getString(5)+";"+localCursor.getString(6)+";"+localCursor.getString(7)+";"+localCursor.getString(8));
			   c++;
		 }
			   // Log.w("CANTIDAD:",c+"");
}	

public void actualizarAvence() {
	
    int respuesta=tieneRespuestas(id_encuesta);
    tex_pb.setText(respuesta+"/"+preguntas);
    int p=(int) ((respuesta / (float) preguntas * 100));
    pb_avence.setProgress(p);
}

public int getContPreguntas(String  data)
{	
	int ct=0;
	db.abrirBasedatos();
	Cursor localCursor = db.baseDatos.rawQuery("select  count(id_pregunta) from encuesta where id_encuesta='"+data+"' and LOWER(tipodato) NOT LIKE 'titulo' and LOWER(tipodato) NOT LIKE 'llave' and LOWER(tipodato) NOT LIKE 'subtitulo' ", null);
	if(localCursor.getCount()>0){
	localCursor.moveToFirst();
	ct= localCursor.getInt(0);
	}
	localCursor.close();
	db.baseDatos.close();
	return ct;
}

public int tieneRespuestas(String id_encuesta)
	{	
		db.abrirBasedatos();
		Cursor localCursor = db.baseDatos.rawQuery("select count(id_pregunta) from respuestas where id_encuesta ='"+id_encuesta+"' and id_cliente="+bundle.getString("cliente")+" ", null);
		localCursor.moveToFirst();
		int count= localCursor.getInt(0);
		localCursor.close();
		db.baseDatos.close();
		return count;
	}
private void ocultarTeclado() {
	View view = this.getCurrentFocus();
	//view.clearFocus();
	if (view != null) {  
	    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
}


public boolean onCreateOptionsMenu(Menu menu) {
	// if(menuId.equals("MenuPpal"))
	getMenuInflater().inflate(R.menu.m_encuesta, menu);
	return true;
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {

	int id = item.getItemId();
	if (id == android.R.id.home) {
		//atras();
		alertaSalir();
	}
	if (id == R.id.menu_carga) {
		//guardarDatos();
		atras();
		
	}
	if (id == R.id.mi_borrar_todo) {
		inicio = false ;
	   	eliminarTodo();
		 mostrarPreguntas();
		 inicio = true;
		// actualizarAvence() ;
		
	}
	
	
	
	
	
	return super.onOptionsItemSelected(item);
}

protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	
    if (resultCode == RESULT_OK) {
    	
    	Bundle res = data.getExtras();
        String foto= res.getString("result");
        showHora(parent,id_pregunta,foto.substring(foto.lastIndexOf("/")+1,foto.length()));
      
    }
 

}

@SuppressLint("NewApi")
public void mostrarPreguntas()
{

	int paddingtop =3;
	float density = getResources().getDisplayMetrics().density;
	int paddingDp = (int)(paddingtop * density);
	int espacios = (int)(10 * density);
	titulosArray= new ArrayList<String>();
	 Cursor localCursor = null;
    db.abrirBasedatos();
   // Log.w("SQL","select *  from encuesta where id_encuesta like '"+bundle.getString("id_encuesta")+"' ");
	 localCursor=db.baseDatos.rawQuery("select * from encuesta where id_encuesta like '"+bundle.getString("id_encuesta")+"' ", null);
	// Log.w("SQL","select *  from clientes where "+col+" like '%"+s1+"%'  and zonaid like '"+getConfig("filtro")+"%' "+and);
	 while (true)
	    {
	      if (!localCursor.moveToNext())
	      {
	    	 
	    	 localCursor.close();
	         db.baseDatos.close();
	         actualizarAvence() ;
	      
	        break;
	      }
	      
          //********
          LinearLayout l1 = (LinearLayout) findViewById(R.id.linearLayout_Relev_Views);
        
          
          LinearLayout inner = null;
          LinearLayout fila;
          int ancho=(int) (getWindowManager().getDefaultDisplay().getWidth());
          //int dp = (int)(ancho*getResources().getDisplayMetrics().density);
          int alto=(int) (getWindowManager().getDefaultDisplay().getHeight());
      
         
          	 //*************************************
             String id_pregunta=localCursor.getString(3);
     	     String pregunta=localCursor.getString(4);
          	 String tipodato=localCursor.getString(5).toLowerCase();
          	 String opciones=localCursor.getString(6);
          	 int habilita = localCursor.getInt(9);
          	 String array_opciones[]=opciones.replace("|",",").split(",");
          	// String and=pregunta.substring(pregunta.length()-1, pregunta.length());
          	//****************************************** 
          	
       if(pregunta.indexOf("-")!=-1) {
    	   
    	   String p[]=pregunta.split("-");
    	   
    	   if(p.length>2 && titulosArray.indexOf(p[0])==-1) {
    		   inner = new LinearLayout(this);
               inner.addView(titulo(p[0]),new LinearLayout.LayoutParams((int)(ancho)-DipToPixels(12),LayoutParams.WRAP_CONTENT));
               inner.setPadding(0,paddingDp,0,paddingDp);
    			l1.addView(inner, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    			titulosArray.add(p[0]);
    	   }
    		   
       }
          	 
    if(tipodato.equals("ubicacion")){
        	 
        	 inner = new LinearLayout(this);
             inner.setOrientation(LinearLayout.VERTICAL);
             inner.addView(etiqueta(pregunta));
             inner.setTag(pregunta);
             EditText t=ubicacion(id_pregunta);
  			 t.setText(this.getRespuestas(id_pregunta));
  		      inner.addView(t);
  		      l1.addView(inner);
  		      l1.addView(separador(), new LayoutParams(LayoutParams.FILL_PARENT, 1));
           	 }
         
       if(tipodato.equals("foto")){
    	   
    	   inner = new LinearLayout(this);
    	   inner.setTag(pregunta);
           inner.setOrientation(LinearLayout.VERTICAL);
           inner.addView(etiqueta(pregunta));
           EditText t=foto(id_pregunta);
		   t.setText(this.getRespuestas(id_pregunta));
		    inner.addView(t);
		    l1.addView(inner);
		    l1.addView(separador(), new LayoutParams(LayoutParams.FILL_PARENT, 1));
    	 }	 
          	 
       if(tipodato.equals("fecha")){
        	   inner = new LinearLayout(this);
        	   inner.setTag(pregunta);
               inner.setOrientation(LinearLayout.VERTICAL);
               inner.addView(etiqueta(pregunta));
               EditText t=fecha(id_pregunta);
    		   t.setText(this.getRespuestas(id_pregunta));
    		    inner.addView(t);
    		    l1.addView(inner);
    		    l1.addView(separador(), new LayoutParams(LayoutParams.FILL_PARENT, 1));
          }
     if(tipodato.equals("hora")){
    	   inner = new LinearLayout(this);
    	   inner.setTag(pregunta);
           inner.setOrientation(LinearLayout.VERTICAL);
           inner.addView(etiqueta(pregunta));
           EditText t=hora(id_pregunta);
		   t.setText(this.getRespuestas(id_pregunta));
		    inner.addView(t);
		    l1.addView(inner);
		    l1.addView(separador(), new LayoutParams(LayoutParams.FILL_PARENT, 1));
      }
     
    if(tipodato.equals("fechahora")){
  	     inner = new LinearLayout(this);
  	     inner.setTag(pregunta);
         inner.setOrientation(LinearLayout.VERTICAL);
         inner.addView(etiqueta(pregunta));
         EditText t=fechaHora(id_pregunta);
		 t.setText(this.getRespuestas(id_pregunta));
		 inner.addView(t);
		 l1.addView(inner);
		 l1.addView(separador(), new LayoutParams(LayoutParams.FILL_PARENT, 1));
    }  
       
          	 
  if(tipodato.equals("texto")){
	 
          	        inner = new LinearLayout(this);
          			inner.setTag(pregunta);
                     inner.setOrientation(LinearLayout.VERTICAL);
          			 inner.addView(etiqueta(pregunta),new LinearLayout.LayoutParams((int)(ancho-DipToPixels(12)),LayoutParams.WRAP_CONTENT));
          			 EditText t=combo(0,0,true,id_pregunta,"responder aquí");
          			 t.setText(this.getRespuestas(id_pregunta));
          			 inner.addView(t,new LinearLayout.LayoutParams((int)(ancho-DipToPixels(12)),LayoutParams.WRAP_CONTENT));
          			 inner.setTag(habilita);
          			 l1.addView(inner, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
          			 l1.addView(separador(), new LayoutParams(LayoutParams.FILL_PARENT, 1));
          			
          		 
          	 }
  
  
        if(tipodato.equals("opciones")){
        	
        	if(array_opciones.length>3) {
        		
	            inner = new LinearLayout(this);
	            inner.setTag(pregunta);
	            inner.setOrientation(LinearLayout.VERTICAL);
	            inner.addView(etiqueta(pregunta));
	 		    inner.addView(spinner(array_opciones,id_pregunta));
	 		    l1.addView(inner);
	 		     l1.addView(separador(), new LayoutParams(LayoutParams.FILL_PARENT, 1));
        	}else {
        		
        		 inner = new LinearLayout(this);
 	            inner.setTag(pregunta);
 	            inner.setOrientation(LinearLayout.VERTICAL);
 	            inner.addView(etiqueta(pregunta));
 	 		    inner.addView(radio(array_opciones,id_pregunta));
 	 		     l1.addView(inner);
 	 		     l1.addView(separador(), new LayoutParams(LayoutParams.FILL_PARENT, 1));
           	   
        	}
        	
         }
        if(tipodato.equals("boton3estados")){
        	 inner = new LinearLayout(this);
	          inner.setTag(pregunta);
	          inner.setOrientation(LinearLayout.VERTICAL);
	          inner.addView(etiqueta(pregunta));
	 		  inner.addView(radio(array_opciones,id_pregunta));
	 		  l1.addView(inner);
	 		  l1.addView(separador(), new LayoutParams(LayoutParams.FILL_PARENT, 1));
        	
        }
        
        
       if(tipodato.startsWith("cantidad")){
          		 
    	        inner = new LinearLayout(this);
    	        inner.setTag(pregunta);
                inner.setOrientation(LinearLayout.VERTICAL);
                inner.addView(etiqueta(pregunta));
                EditText t=combo(1,5,true,id_pregunta,"");
                t.setText(this.getRespuestas(id_pregunta));
     		    inner.addView(t);
     		    l1.addView(inner);
     		     l1.addView(separador(), new LayoutParams(LayoutParams.FILL_PARENT, 1));
    	  }
          	 
      if(tipodato.startsWith("precio")){
          		
          		inner = new LinearLayout(this);
          		inner.setTag(pregunta);
                inner.setOrientation(LinearLayout.VERTICAL);
                inner.addView(etiqueta(pregunta));
                EditText t=combo(2,5,true,id_pregunta,"");
                t.setText(this.getRespuestas(id_pregunta));
          		inner.addView(t);
          		l1.addView(inner);
          		 l1.addView(separador(), new LayoutParams(LayoutParams.FILL_PARENT, 1));
          	 }
      //Log.w("TIPO", tipodato);
      if(tipodato.equals("titulo")){
    	//  Log.w("TIPO ENTRO", tipodato);
             		inner = new LinearLayout(this);
                    inner.addView(titulo(pregunta),new LinearLayout.LayoutParams((int)(ancho)-DipToPixels(12),LayoutParams.WRAP_CONTENT));
                    inner.setPadding(0,paddingDp,0,paddingDp);
         			 l1.addView(inner, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
         			
             	 }   
     if(tipodato.startsWith("boton2estados")){
    		
    	 
 		    inner = new LinearLayout(this);
 		   inner.setTag(pregunta);
            inner.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.FILL_PARENT);
            param1.weight = 0.9f;
            TextView tv=etiqueta(pregunta);
            tv.setLayoutParams(param1);
            inner.addView(tv);
			 //**********************
			 inner.addView(boton2estados(id_pregunta,array_opciones));
			 inner.setTag(habilita);
			 //inner.setPadding(0,espacios,0,espacios);
			l1.addView(inner);
			l1.addView(separador(), new LayoutParams(LayoutParams.FILL_PARENT, 1)); 
    	   
    	   
    	 }  
      
      
      
if(tipodato.equals("llave")){
           	boolean b=false;	
    	  String respuesta=getRespuestas(id_pregunta);
   	     if(respuesta.equals("1"))b=true;
   	     
        	Switch llave = llave(id_pregunta,b);
        		 inner = new LinearLayout(this);
                 inner.setOrientation(LinearLayout.HORIZONTAL);
                 inner.addView(etiqueta(pregunta),new LinearLayout.LayoutParams((int)(ancho-DipToPixels(80)),LayoutParams.MATCH_PARENT));
     			 //**********************
     			 inner.addView(llave,new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT));
    			 l1.addView(inner, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
    			 inner.setPadding(0,espacios,0,espacios);
    			 l1.addView(separador(), new LayoutParams(LayoutParams.FILL_PARENT, 1));
    	  }
   
    if(tipodato.startsWith("check")){
     		
    	     CheckBox check = checkBox("",id_pregunta, false);
    	  	  	 
    	     String respuesta=getRespuestas(id_pregunta);
    	   //  Log.w("checkBox",id_pregunta+" : "+respuesta); 
    	     if(respuesta.equals("1"))check.setChecked(true);
    	     inner = new LinearLayout(this);
             inner.setOrientation(LinearLayout.HORIZONTAL);
             LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.FILL_PARENT);
             param1.weight = 0.9f;
             TextView tv=etiqueta(pregunta);
             tv.setLayoutParams(param1);
             inner.addView(tv);
			 //**********************
			 inner.addView(check);
			 inner.setTag(habilita);
			 inner.setPadding(0,espacios,0,espacios);
			 inner.setTag(pregunta);
			  l1.addView(inner);
			
		 l1.addView(separador(), new LayoutParams(LayoutParams.FILL_PARENT, 1));
	  }
        	 
  if(tipodato.startsWith("texto2")){
	  
	    inner= comboText2(array_opciones,"2]"+id_pregunta,pregunta);
	    inner.setTag(habilita);
         l1.addView(inner);
         l1.addView(separador(),new LayoutParams(LayoutParams.FILL_PARENT, 1));		
     			 
       	}
  if(tipodato.startsWith("texto3")){
	  
	    inner= comboText3(array_opciones,"3]"+id_pregunta,pregunta);
	    inner.setTag(habilita);
        l1.addView(inner);
        l1.addView(separador(),new LayoutParams(LayoutParams.FILL_PARENT, 1));		
   			 
     	}  
  
  if(tipodato.startsWith("texto4")){
	  
	    inner= comboText4(array_opciones,"4]"+id_pregunta,pregunta);
	    inner.setTag(habilita);
        l1.addView(inner);
        l1.addView(separador(),new LayoutParams(LayoutParams.FILL_PARENT, 1));		
 			 
   	}   
  
 if(tipodato.startsWith("rating")){
		
		 inner = new LinearLayout(this);
		 inner.setTag(pregunta);
         inner.setOrientation(LinearLayout.VERTICAL);
         inner.addView(etiqueta(pregunta));
         RatingBar rt = rating(id_pregunta);
         String res=getRespuestas(id_pregunta);
         if(res.length()>0)
         rt.setRating(Float.parseFloat(res));
		 inner.addView(rt);
		 l1.addView(inner,new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		 l1.addView(separador(), new LayoutParams(LayoutParams.FILL_PARENT, 1));
	 }       	
          
 if(tipodato.equals("opciones multiples")){
	   inner = new LinearLayout(this);
	   inner.setTag(pregunta);
 	  inner.setOrientation(LinearLayout.HORIZONTAL);
	  inner.addView(etiqueta(pregunta),new LinearLayout.LayoutParams((int)(ancho/1.15),LayoutParams.WRAP_CONTENT));
	  l1.addView(inner, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	  inner = new LinearLayout(this);
  	  inner.setOrientation(LinearLayout.VERTICAL);
  	  String id_ch="";boolean c= false;
  	  String res=getRespuestas(id_pregunta).replace("|"," ,");
  	    String array_res[]= res.split(",");
  //	Log.w("checkBox",id_pregunta+"....."+array_res.length+".."+res); 	 
	 for(int  ch=0; ch< array_opciones.length;ch++){
		if(ch==0)id_ch=array_opciones.length+"";else id_ch=id_pregunta;
		if(array_res.length>1)
		if (!array_res[ch].equals(" "))c=true;else c= false;
		//Log.w("checkBox",id_pregunta+"....."+array_res[ch]+"...");
       inner.addView(checkBox(array_opciones[ch],"M"+id_ch, c),new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
     
	 }

l1.addView(inner, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	 
}
 	
 if(habilita>0)
   		inner.setVisibility(View.GONE);
     }
	   parent = (LinearLayout) findViewById(R.id.linearLayout_Relev_Views);
	 for(int i =0; i< listaLLave.size();i++)
		 activarLLave(parent,listaLLave.get(i).toString(),true);
	    
}   
   
public TextView separador(){
	 TextView t = new TextView(this);
 	  t.setBackgroundColor(Color.parseColor("#CCCCCC")); 
   return t;
	}
public TextView etiqueta(String texto){
	
	 
	  if(texto.indexOf("-")!=-1) {
  	      String p[]=texto.split("-");
  	       if(p.length==3 ) 
  	    	 texto=p[2];
  		   }
	 TextView t = new TextView(this);
	  t.setText(texto);
	  t.setTextColor(Color.parseColor("#000000"));
	  t.setGravity(Gravity.CENTER_VERTICAL);  
    return t;
	}

public TextView titulo(String texto){
	 TextView t = new TextView(this);
	  t.setText(texto);
	  t.setTextColor(Color.parseColor("#FFFFFF"));
	  t.setTypeface(null, Typeface.BOLD);
	  t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
	  t.setBackgroundResource(R.drawable.borde_perfil_titulo);
	  t.setGravity(Gravity.CENTER_VERTICAL);  
   return t;
	}

//***
public EditText combo(int tipo, int ems,boolean activo,String id,String hint){
	  EditText t = new EditText(this);
	    t.setTag(id);
	   
		//Log.w("Item:",InputType.TYPE_CLASS_NUMBER+","+InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		if(tipo==1){
		t.setInputType(InputType.TYPE_CLASS_NUMBER);
		}
		if(tipo==2){
			t.setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL|EditorInfo.TYPE_CLASS_NUMBER);
			}
		
	   t.setBackgroundResource(android.R.drawable.edit_text);
		t.setHint(hint);
		t.addTextChangedListener(new TextWatcher() {

			   public void afterTextChanged(Editable s) {
			   }

			   public void beforeTextChanged(CharSequence s, int start, 
			     int count, int after) {
			   }

			   public void onTextChanged(CharSequence s, int start, 
			     int before, int count) {
				   if(inicio) 
					   guardarDatos("combo");
					   
			 //  TextView myOutputBox = (TextView) findViewById(R.id.myOutputBox);
			  // myOutputBox.setText(s);
			   }
			  });
	  t.setEnabled(activo);
	 return t;
}


public LinearLayout comboText2 (String array_opciones[], String id_pregunta,String pregunta ){
	
	int paddingPixel =3;
	int paddingtop =2;
	String respuesta[]={"",""};
	float density = getResources().getDisplayMetrics().density;
	int paddingDp = (int)(paddingPixel * density);
	int paddingTop = (int)(paddingtop * density);
	   // Log.w("DATOS:","D:"+ getRespuestas(id_pregunta.split("-")[1]));
	  respuesta=getRespuestas(id_pregunta.split("]")[1],2).split("#");
	
	 LinearLayout inner = new LinearLayout(this);
	 inner.setPadding(0,paddingTop,0,paddingDp);
         inner.setOrientation(LinearLayout.HORIZONTAL);
         LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.FILL_PARENT);
         childParam1.weight = 0.4f;
         TextView tv=etiqueta(pregunta);
        tv.setLayoutParams(childParam1);
     
        inner.addView(tv);
		 //**********************
         LinearLayout.LayoutParams childParam2 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
         childParam2.weight = 0.3f;
         EditText et1=combo(2,0,true,id_pregunta,array_opciones[0].toLowerCase());
         et1.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.text_size_tiny));
         et1.setLayoutParams(childParam2);
         if(respuesta.length>0)
         et1.setText(respuesta[0].trim());
         //et1.setTag(getTag1());
		 inner.addView(et1);
		 
		 LinearLayout.LayoutParams childParam3 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
         childParam3.weight = 0.3f;
         EditText et2=combo(2,0,true,id_pregunta,array_opciones[1].toLowerCase());
         et2.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.text_size_tiny));
         et2.setLayoutParams(childParam3);
         if(respuesta.length>1)
         et2.setText(respuesta[1].trim());
        // et2.setTag(getTag1());
         //setPadding
		 inner.addView(et2);
		// inner.setTag(llave);
	 return inner;
}
public LinearLayout comboText3 (String array_opciones[], String id_pregunta,String pregunta ){
	
	int paddingPixel =3;
	int paddingtop =2;
	float density = getResources().getDisplayMetrics().density;
	int paddingDp = (int)(paddingPixel * density);
	int paddingTop = (int)(paddingtop * density);
	String respuesta[]={"","",""};
	  respuesta=getRespuestas(id_pregunta.split("]")[1],3).split("#");
	 LinearLayout inner = new LinearLayout(this);
	 inner.setPadding(0,paddingTop,0,paddingDp);
         inner.setOrientation(LinearLayout.HORIZONTAL);
         LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.FILL_PARENT);
         childParam1.weight = 0.25f;
         TextView tv=etiqueta(pregunta);
        tv.setLayoutParams(childParam1);
       inner.addView(tv);
		 //**********************
         LinearLayout.LayoutParams childParam2 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
         childParam2.weight = 0.25f;
         EditText et1=combo(2,0,true,id_pregunta,array_opciones[0].toLowerCase());
         et1.setLayoutParams(childParam2);
         if(respuesta.length>0)
         et1.setText(respuesta[0].trim());
         et1.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.text_size_tiny));
		 inner.addView(et1);
		
		 LinearLayout.LayoutParams childParam3 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
         childParam3.weight = 0.25f;
         EditText et2=combo(2,0,true,id_pregunta,array_opciones[1].toLowerCase());
         et2.setLayoutParams(childParam3);
         et2.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.text_size_tiny));
         if(respuesta.length>1)
         et2.setText(respuesta[1].trim());
         inner.addView(et2);
         
         
         childParam3.weight = 0.25f;
         EditText et3=combo(2,0,true,id_pregunta,array_opciones[2].toLowerCase());
         et3.setLayoutParams(childParam3);
         et3.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.text_size_tiny));
         if(respuesta.length>2)
         et3.setText(respuesta[2].trim());
         inner.addView(et3);
		// inner.setTag(llave);
	 return inner;
}
public LinearLayout comboText4 (String array_opciones[], String id_pregunta,String pregunta ){
	
	int paddingPixel =3;
	int paddingtop =2;
	float density = getResources().getDisplayMetrics().density;
	int paddingDp = (int)(paddingPixel * density);
	int paddingTop = (int)(paddingtop * density);
	
	String respuesta[]={"","","",""};
	respuesta=getRespuestas(id_pregunta.split("]")[1],4).split("#");
	
	 LinearLayout inner = new LinearLayout(this);
	 inner.setPadding(0,paddingTop,0,paddingDp);
         inner.setOrientation(LinearLayout.HORIZONTAL);
         LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.FILL_PARENT);
         childParam1.weight = 0.2f;
         TextView tv=etiqueta(pregunta);
        tv.setLayoutParams(childParam1);
      
        inner.addView(tv);
		 //**********************
         LinearLayout.LayoutParams childParam2 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
         childParam2.weight = 0.2f;
         EditText et1=combo(2,0,true,id_pregunta,array_opciones[0].toLowerCase());
         et1.setLayoutParams(childParam2);
         et1.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.text_size_tiny));
         if(respuesta.length>0)
         et1.setText(respuesta[0].trim());
		 inner.addView(et1);
		 
		 LinearLayout.LayoutParams childParam3 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
         childParam3.weight = 0.2f;
         EditText et2=combo(2,0,true,id_pregunta,array_opciones[1].toLowerCase());
         et2.setLayoutParams(childParam3);
         et2.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.text_size_tiny));
         if(respuesta.length>1)
         et2.setText(respuesta[1].trim());
         inner.addView(et2);
         
         
         childParam3.weight = 0.2f;
         EditText et3=combo(2,0,true,id_pregunta,array_opciones[2].toLowerCase());
         et3.setLayoutParams(childParam3);
         et3.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.text_size_tiny));
         if(respuesta.length>2)
         et3.setText(respuesta[2].trim());
         inner.addView(et3);
         
         childParam3.weight = 0.2f;
         EditText et4=combo(2,0,true,id_pregunta,array_opciones[3].toLowerCase());
         et4.setLayoutParams(childParam3);
         et4.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.text_size_tiny));
         if(respuesta.length>3)
         et4.setText(respuesta[3].trim());
         inner.addView(et4);
         
		// inner.setTag(llave);
	 return inner;
}
@SuppressLint("NewApi")
public Switch llave (final String id,boolean activo){
	Switch t = new Switch(this);
	    t.setTag(id);
	    t.setChecked(activo);
	 	 
    	if(activo)
    		listaLLave.add(id);
    	
	    t.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	   
	    	
	    	   @Override
	    	   public void onCheckedChanged(CompoundButton buttonView,
	    	     boolean isChecked) {
	    			
	    		
	    	    if(isChecked){
	    	    	
	    	    	activarLLave(parent,id,true);
	    	    }else{
	    	    	//Log.w("LLAVE", "false");
	    	    	activarLLave(parent,id,false);
	    	    }
	    	 
	    	   }
	    	  });

	 
	 
	 return t;
}


public EditText foto(final String id){
	  EditText t = new EditText(this);
	    t.setTag(id);
	    t.setKeyListener(null);
	    t.setBackgroundResource(android.R.drawable.edit_text);
	
		 Drawable dr = getResources().getDrawable(R.drawable.ic_menu_camera2);
		    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		    Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap,50, 50, true));
			t.setCompoundDrawablesWithIntrinsicBounds( null, null, d, null );
			t.setCompoundDrawablesWithIntrinsicBounds( null, null, d, null );
			final String fotoId="v1_c"+id_cliente+"_r"+id_encuesta+"_"+id+".jpg";
		    // final GPSTracker gps= new GPSTracker(this);
				 t.setOnClickListener(new View.OnClickListener() {
			        public void onClick(View v) {
			        	id_pregunta=id;
			        	Intent intent = new Intent();
			        	  Log.w("FOTO", "F"+fotoId+"f");
			    		  intent.putExtra("fotoid",fotoId);
			    	      intent.setClass(FormEncuesta.this, PhotoCapture.class);
			    		 startActivityForResult(intent, 107);
			          
			        }

			    });
				 t.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				      @Override
				      public void onFocusChange(View v, boolean hasFocus) {
				    	 if( hasFocus){
				    		 id_pregunta=id;
				    		 Intent intent = new Intent();
					        	//  Log.w("FOTO", "F"+fotoId+"f");
					    		  intent.putExtra("fotoid",fotoId);
					    	      intent.setClass(FormEncuesta.this, PhotoCapture.class);
					    		 startActivityForResult(intent, 107);
				    
				    	 }
				        
				      }
				    });
					t.addTextChangedListener(new TextWatcher() {

						   public void afterTextChanged(Editable s) {
						   }

						   public void beforeTextChanged(CharSequence s, int start, 
						     int count, int after) {
						   }

						   public void onTextChanged(CharSequence s, int start, 
						     int before, int count) {
							   if(inicio) 
								   guardarDatos("foto");
								   
						 //  TextView myOutputBox = (TextView) findViewById(R.id.myOutputBox);
						  // myOutputBox.setText(s);
						   }
						  });
		
		 
	 return t;
}

public EditText ubicacion(String id){
	  final EditText t = new EditText(this);
	    t.setTag(id);
	    t.clearFocus();
	    t.setBackgroundResource(android.R.drawable.edit_text);
	    t.setKeyListener(null);
	    Drawable dr = getResources().getDrawable(R.drawable.ic_menu_mylocation);
	    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
	    Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap,50, 50, true));
		t.setCompoundDrawablesWithIntrinsicBounds( null, null, d, null );
	    // final GPSTracker gps= new GPSTracker(this);
		/*
			 t.setOnClickListener(new View.OnClickListener() {
		        public void onClick(View v) {
		        	  if(!checkLocation())
		                  return;
		        	  ubicacionGPS(t);
		          
		        }

		    });
				t.addTextChangedListener(new TextWatcher() {

					   public void afterTextChanged(Editable s) {
					   }

					   public void beforeTextChanged(CharSequence s, int start, 
					     int count, int after) {
					   }

					   public void onTextChanged(CharSequence s, int start, 
					     int before, int count) {
						   if(inicio) 
							   guardarDatos("ubicacion");
							   
					 //  TextView myOutputBox = (TextView) findViewById(R.id.myOutputBox);
					  // myOutputBox.setText(s);
					   }
					  });
					  */
	   
	 return t;
}

public ToggleButton  boton2estados(String id,String arry[]){
	//if(arry.length<2)
		arry= new String[]{"NO","SI"};
	     final ToggleButton bt= new ToggleButton(this); 
	     bt.setTag(id);
	     String respuesta=getRespuestas(id);
	     bt.setTextOn(arry[1]);
	     bt.setTextOff(arry[0]);
	  //   Log.w("DATO DEL RESPUESTA:",id+ " id " + respuesta);
	     if(respuesta.equals("SI")) {
	      bt.setChecked(true);
	      bt.setMaxEms(4743);
	     }
	     else
	      bt.setChecked(false);
	     Log.w("DATO DEL TOBLE:",id+ " id " + bt.getText());
	     bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	    	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	    	    	 if(inicio) {
	    	    	      // bt.setId(999999999);
	    	    		 bt.setMaxEms(4743);
						   guardarDatos("boton2estados");
	    	    	 }
	    	    }
	    	});
	    	
	 
return bt;
}


public Spinner spinner(String arr[],String id){
	Spinner s = new Spinner(this);
	String arry[]= addEspacio(arr);
	 s.setBackgroundResource(R.drawable.spinner_selector);
	 s.setTag(id);
	
	 String array_res[]=getRespuestas(id).replace("|",",").split(",");
	
	ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arry);
	spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
	s.setAdapter(spinnerArrayAdapter);
 	s.setSelection(maxID(array_res));
 	s.setOnItemSelectedListener(
             new OnItemSelectedListener() {
                 public void onItemSelected(
                         AdapterView<?> parent, View view, int position, long id) {
                	 if(inicio) {
                		 if(position!=0)
             			  guardarDatos("spinner");
                	 }
                 }

                 public void onNothingSelected(AdapterView<?> parent) {
                    // showToast("Spinner2: unselected");
                 }
             });
 
 
	 return s;
}

public RadioGroup  radio(String arr[],String id){
	
	int ind=-1;
	
	 String array_res[]=getRespuestas(id).replace("|",",").split(",");
	 for(int r=0;r< array_res.length; r++ ) {
		 
		 if(array_res[r].length()>1) {
			 ind=r;
		 }
	 }
	
      final RadioButton[] rb = new RadioButton[arr.length];
	    RadioGroup rg = new RadioGroup(this); //create the RadioGroup
	    rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
	    for(int i=0; i<arr.length; i++){
	       rb[i]  = new RadioButton(this);          
	       rb[i].setText(arr[i]);
	       rb[i].setId(i);
	       if(ind==i) {
	    	  // Log.w("RESPUESTA",ind+"=="+i);
	    	   rb[i].setChecked(true);
	       }
	       rg.addView(rb[i]);
	    }
	    rg.setTag(id);
	
	    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
	        public void onCheckedChanged(RadioGroup group, int checkedId) {
	           
                 if(inicio)
	            	guardarDatos("radio");
	            
	        }

	    });
	
	
	 return rg;
}
private  String[] addEspacio(String array[]) {
	String[] a = new String[array.length+1];
	a[0]="";int x=1;
    for(int i=0; i < array.length; i++){
    	
         a[x] = array[i];
         x++;
    }
   
    return a;
}
public CheckBox checkBox(String etiqueta,String id, boolean c){
	CheckBox ch = new CheckBox(this);
	ch.setTag(id);
	ch.setText(etiqueta);
	ch.setChecked(c);
	ch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	    	 if(inicio)
	            guardarDatos("checkBox");
	    }
	});
	return ch;
}

public EditText fecha(final String id){
	 final EditText t = new EditText(this);
	    t.setTag(id);
	    t.setHint("dd/mm/aaaa");
	    t.setBackgroundResource(android.R.drawable.edit_text);
	    Drawable dr = getResources().getDrawable(R.drawable.ic_menu_today);
	    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
	    Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap,50, 50, true));
		t.setCompoundDrawablesWithIntrinsicBounds( null, null, d, null );
		 t.setKeyListener(null);
		
		t.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		      @Override
		      public void onFocusChange(View v, boolean hasFocus) {
		    	 if( hasFocus){
		    		 ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(t.getWindowToken(), 0);
		    	  idfecha= id;
	        	     new DatePickerDialog(FormEncuesta.this,  myDateListener, year, month, day) .show();
		    	 }
		        
		      }
		    });
	 
		 t.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	       
	        	    idfecha= id;
	        	     new DatePickerDialog(FormEncuesta.this,  myDateListener, year, month, day) .show();
	        	     
	        }

	    });
		 t.addTextChangedListener(new TextWatcher() {

			   public void afterTextChanged(Editable s) {
			   }

			   public void beforeTextChanged(CharSequence s, int start, 
			     int count, int after) {
			   }

			   public void onTextChanged(CharSequence s, int start, 
			     int before, int count) {
				   if(inicio) 
					   guardarDatos("");
					   
			 //  TextView myOutputBox = (TextView) findViewById(R.id.myOutputBox);
			  // myOutputBox.setText(s);
			   }
			  });
		  
//	DatePicker d= new DatePicker(this);
	//d.setTag(id);
  return t;
}
public EditText hora(final String id){
	 final EditText t = new EditText(this);
	    t.setTag(id);
	   
	    t.setBackgroundResource(android.R.drawable.edit_text);
	    Drawable dr = getResources().getDrawable(R.drawable.ic_dialog_time);
	    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
	    Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap,50, 50, true));
		t.setCompoundDrawablesWithIntrinsicBounds( null, null, d, null );
	    t.setKeyListener(null);
	    t.setHint("hh:mm");
		 t.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	   
	            Calendar mcurrentTime = Calendar.getInstance();
	            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
	            int minute = mcurrentTime.get(Calendar.MINUTE);
	            TimePickerDialog mTimePicker;
	            mTimePicker = new TimePickerDialog(FormEncuesta.this, new TimePickerDialog.OnTimeSetListener() {
	                @Override
	                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
	                	showHora(parent,id,String.format("%02d",selectedHour) + ":" + String.format("%02d",selectedMinute));
	                   // eReminderTime.setText( selectedHour + ":" + selectedMinute);
	                }
	            }, hour, minute, true);//Yes 24 hour time
	            //mTimePicker.setTitle("Select Time");
	            mTimePicker.show();
	        }

	    });
		 t.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		      @Override
		      public void onFocusChange(View v, boolean hasFocus) {
		    	 if( hasFocus){
		    		 ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(t.getWindowToken(), 0);
		    		    Calendar mcurrentTime = Calendar.getInstance();
			            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
			            int minute = mcurrentTime.get(Calendar.MINUTE);
			            TimePickerDialog mTimePicker;
			            mTimePicker = new TimePickerDialog(FormEncuesta.this, new TimePickerDialog.OnTimeSetListener() {
			                @Override
			                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
			                	showHora(parent,id,String.format("%02d",selectedHour) + ":" + String.format("%02d",selectedMinute));
			                   // eReminderTime.setText( selectedHour + ":" + selectedMinute);
			                }
			            }, hour, minute, true);//Yes 24 hour time
			            //mTimePicker.setTitle("Select Time");
			            mTimePicker.show();
		    	 }
		        
		      }
		    });
		 t.addTextChangedListener(new TextWatcher() {

			   public void afterTextChanged(Editable s) {
			   }

			   public void beforeTextChanged(CharSequence s, int start, 
			     int count, int after) {
			   }

			   public void onTextChanged(CharSequence s, int start, 
			     int before, int count) {
				   if(inicio) 
					   guardarDatos("fecha");
					   
			 //  TextView myOutputBox = (TextView) findViewById(R.id.myOutputBox);
			  // myOutputBox.setText(s);
			   }
			  });
	
//	DatePicker d= new DatePicker(this);
	//d.setTag(id);
 return t;
}


public EditText fechaHora(final String id){
	 final EditText t = new EditText(this);
	    t.setTag(id);
	    t.setHint("dd/mm/aaaa hh:mm");
	    t.setBackgroundResource(android.R.drawable.edit_text);
	    Drawable dr = getResources().getDrawable(R.drawable.ic_menu_today);
	    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
	    Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap,50, 50, true));
		t.setCompoundDrawablesWithIntrinsicBounds( null, null, d, null );
		 t.setKeyListener(null);
		
		t.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		      @Override
		      public void onFocusChange(View v, boolean hasFocus) {
		    	 if( hasFocus){
		    		 ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(t.getWindowToken(), 0);
		    		   // Get Current Date
		             final Calendar c = Calendar.getInstance();
		             final int mYear = c.get(Calendar.YEAR);
		             final int mMonth = c.get(Calendar.MONTH);
		             final int mDay = c.get(Calendar.DAY_OF_MONTH);
		             final int mHour = c.get(Calendar.HOUR_OF_DAY);
		             final int mMinute = c.get(Calendar.MINUTE);

		             DatePickerDialog datePickerDialog = new DatePickerDialog(FormEncuesta.this,new DatePickerDialog.OnDateSetListener() {

		                         public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {

		                            // txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

		                             TimePickerDialog timePickerDialog = new TimePickerDialog(FormEncuesta.this,
		                                     new TimePickerDialog.OnTimeSetListener() {

		                                         @Override
		                                         public void onTimeSet(TimePicker view, int hourOfDay,
		                                                               int minute) {
		                                        	 showHora(parent,id,String.format("%02d",mDay)+"/"+String.format("%02d",mMonth)+"/"+String.format("%02d",mYear)+" "+String.format("%02d",mHour) + ":" + String.format("%02d",mMinute));
		                                            // txtTime.setText(hourOfDay + ":" + minute);
		                                         }
		                                     }, mHour, mMinute, false);
		                             timePickerDialog.show();

		                         }
		                     }, mYear, mMonth, mDay);
		             datePickerDialog.show();
		    	 }
		        
		      }
		    });
	 
		 t.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	       
	        	    idfecha= id;
	        	     new DatePickerDialog(FormEncuesta.this,  myDateListener, year, month, day) .show();
	        	     
	        }

	    });
		 t.addTextChangedListener(new TextWatcher() {

			   public void afterTextChanged(Editable s) {
			   }

			   public void beforeTextChanged(CharSequence s, int start, 
			     int count, int after) {
			   }

			   public void onTextChanged(CharSequence s, int start, 
			     int before, int count) {
				   if(inicio) 
					   guardarDatos("fechaHora");
					   
			 //  TextView myOutputBox = (TextView) findViewById(R.id.myOutputBox);
			  // myOutputBox.setText(s);
			   }
			  });
//	DatePicker d= new DatePicker(this);
	//d.setTag(id);
 return t;
}


public RatingBar rating(String id){
	
	RatingBar rb = new RatingBar(this);
	rb.setTag(id);
	  rb.setNumStars(5);
	  rb.setStepSize(1.0F);
	 //  rb.setRating(2);
	  rb.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
				boolean fromUser) {
				 if(inicio)
		            	guardarDatos("RatingBar");
                // Log.w("VALOR",String.valueOf(rating));
				//txtRatingValue.setText(String.valueOf(rating));

			}
		});
	 
	
	
	
return rb;
}


    public String getConfig(String p1) {
        return getSharedPreferences("config", 0).getString(p1, "");
    }

  

 

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if ((keyCode == KeyEvent.KEYCODE_BACK)) 
        	alertaSalir();
        return true;
    }
   
  public  int DipToPixels(float dips)
    {
        return (int) (dips * getResources().getDisplayMetrics().density + 0.5f);
    }



public void atras(){
	
	 Intent intent = new Intent().setClass(FormEncuesta.this, MenuPpal.class);
    intent.putExtra("m","FormRelevamiento");
    intent.putExtra("idCliente",id_cliente);
    intent.putExtra("id_encuesta",bundle.getString("id_encuestas"));
    startActivity(intent);
     finish(); 
	}

public void alertaSalir() {
    new AlertDialog.Builder(this)
    		.setTitle("SALIR")
           .setMessage("Desea salir?")
           .setCancelable(false)
            .setIconAttribute(android.R.attr.alertDialogIcon)
           .setPositiveButton("Si", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
            	   atras();
   	    		//  finish();              
   	    	
               }
           })
           .setNegativeButton("No", null)
           .show();
}
private void activarLLave(ViewGroup parent,String id,boolean visible) {
	

	View view =null; 
   for(int i = 0; i < parent.getChildCount(); i++) {
     	
	   view = parent.getChildAt(i);
        if(view instanceof LinearLayout) {
        	 String s1=view.getTag()+"";
        	
	    	  if(s1.trim().equals(id.trim())){ 
	    		  
	    		  if(!visible)
					   view.setVisibility(View.GONE);
				   else
					   view.setVisibility(View.VISIBLE);
	         
	        }   
        }
        
      if(view instanceof ViewGroup) {
         ViewGroup group = (ViewGroup)view;
         activarLLave(group,id,visible);
       }
   }
   
}


private DatePickerDialog.OnDateSetListener myDateListener = new 
DatePickerDialog.OnDateSetListener() {
@Override
public void onDateSet(DatePicker arg0, 
   int arg1, int arg2, int arg3) {
 
   showDate(parent,idfecha,arg1, arg2+1, arg3);
   
}
};


private void showDate(ViewGroup parent,String id,int year, int month, int day ) {
	

	View view =null; 
   for(int i = 0; i < parent.getChildCount(); i++) {
     	
	   view = parent.getChildAt(i);
       
       
        
        if(view instanceof EditText) {
        	 String s1=view.getTag()+"";
        	
	    	  if(s1.trim().equals(id.trim())){ 
	    		  EditText et = (EditText)view;
	              
	              et.setText(new StringBuilder().append(String.format("%02d", day)).append("/").append(String.format("%02d", month)).append("/").append(String.format("%02d", year)));
	    		//  new StringBuilder().append(day).append("/").append(month).append("/").append(year)
	         
	        }   
        }
        
      if(view instanceof ViewGroup) {
         ViewGroup group = (ViewGroup)view;
         showDate(group,id,year,  month,  day);
       }
   }
   
}
private void showHora(ViewGroup parent,String id,String hora ) {
	

	View view =null; 
   for(int i = 0; i < parent.getChildCount(); i++) {
     	
	   view = parent.getChildAt(i);
       
       
        
        if(view instanceof EditText) {
        	 String s1=view.getTag()+"";
        	
	    	  if(s1.trim().equals(id.trim())){ 
	    		  EditText et = (EditText)view;
	              
	              et.setText(hora);
	    		//  new StringBuilder().append(day).append("/").append(month).append("/").append(year)
	         
	        }   
        }
        
      if(view instanceof ViewGroup) {
         ViewGroup group = (ViewGroup)view;
         showHora(group,id,hora);
       }
   }
   
}


@SuppressLint("NewApi")
private void guardarEncuesta(ViewGroup parent,SQLiteStatement statement) {
	
	//10477,2017-03-09 15:54:06.845,16-1|3,3.0,codigo
	 String s1="",s2="";
	  View child =null;  
   for(int i = 0; i < parent.getChildCount(); i++) {
   	String datos="";
        child = parent.getChildAt(i);
        
   
    if(child instanceof Spinner ) {
           Spinner spinner = (Spinner)child;
           spinner.getTag();
           s1=spinner.getTag()+"";String item="";int in=0;
       	if(!spinner.getSelectedItem().equals(" ")){
       	  for(int sp=0;sp<spinner.getAdapter().getCount();sp++){
       		 if(spinner.getSelectedItem().equals(spinner.getAdapter().getItem(sp))){
       				item=spinner.getAdapter().getItem(sp).toString();
       				in=sp;
       		 }
       		  s2+=item+"|";
       		  item="";
       	     }
       	  if(in!=0)
       	   datos=id_cliente+","+getFecha()+","+id_encuesta.trim()+","+s1+","+s2+",0";
       	  else
       		  eliminarRespu(s1);
       	 // Log.w("DATOS:", datos);
       }
             
       
       }
    if(child instanceof RadioGroup ) {
    	RadioGroup radio = (RadioGroup)child;
    	radio.getTag();
        s1=radio.getTag()+"";String item="";
        int selectedRadioButtonID = radio.getCheckedRadioButtonId();
        if (selectedRadioButtonID != -1) {
        	RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadioButtonID);
        for(int r=0; r<radio.getChildCount();r++) {
        	if(selectedRadioButton.getId()==r)
        	//  item=selectedRadioButton.getText().toString();
        	  s2+=selectedRadioButton.getText().toString()+"|";
        	else
        	s2+=item+"|";
        }
        	
        datos=id_cliente+","+getFecha()+","+id_encuesta.trim()+","+s1+","+s2+",0";
        
        } else
     		  eliminarRespu(s1);
        	
    }
  if(child instanceof CheckBox) {
         CheckBox cb = (CheckBox)child;
         String item="";int b=0;
         if(cb.getTag().toString().startsWith("M")){
			       	 int cbCant=Integer.parseInt(cb.getTag().toString().substring(1, cb.getTag().toString().length()));
			            
			             
			       	 for(int z=i; z < cbCant;z++){
			       		  child = parent.getChildAt(z);
			       		  cb = (CheckBox)child;
			       	      if(cb.isChecked()){
			                  item+=cb.getText()+"|";b=1;
			       	      }
			       	      else
			       	       item+="|";
			       	     }
			       	 i=i+cbCant;
			       	 s1=cb.getTag().toString().substring(1, cb.getTag().toString().length());
       	 }else{
       	    
   		     if(cb.isChecked()){
   		        item="1";b=1;
   		       s1=cb.getTag()+"";
   		     }
       	 }
           
            s2=item;
       	if(b==1 && s2.length()>0){
            datos=id_cliente+","+getFecha()+","+id_encuesta.trim()+","+s1+","+s2+",0";
          // Log.w("DATOS:", datos);
       	} else
     		  eliminarRespu(s1);
       
       }
   if(child instanceof EditText) {
           //Support for EditText
           EditText et = (EditText)child;
          s1=child.getTag()+"";
      //  Log.w("ITEMSelec", s1);
          // if(et.getText().toString().length()>0){
			         if(s1.indexOf("]")>0){
			        	 int cant=Integer.parseInt(s1.split("]")[0])+i;
			        	// Log.w("DATO", "VALOR: " + et.getText()+ " ITEM: "+cant+ "ID:"+s1);
			        	 s1=s1.split("]")[1];
			        	 for(int t=i;t<cant;t++){
			        		 child = parent.getChildAt(t);
			        		  et = (EditText)child;
			        		//  if(et.getText().toString().length()>0)
			        		  s2+=et.getText().toString()+"#";
			        		  
			        	   //datos=id_cliente+","+getFecha()+","+id_encuesta.trim()+","+s1+","+s2+",0";
			        	 }
			        	// Log.w("DATO:", "EdiText: " +s2+"  : "+s2.length()+"  item: "+cant );
			        	 if(cant==3)if(s2.length()==2)  s2="";
			        	 if(cant==4)if(s2.length()==3)  s2="";
			        	 if(cant==5)if(s2.length()==4)  s2="";
			        	 i=i+(cant-1);
			        	 if(s2.length()>0)
			        	   datos=id_cliente+","+getFecha()+","+id_encuesta.trim()+","+s1+","+s2+",0";
			        	 else
			        		 eliminarRespu(s1);
			        	 
			        	   
			         }else{
			        	 s2=et.getText().toString().replace(",", "");
			        	 if(s2.length()>0)
			             datos=id_cliente+","+getFecha()+","+id_encuesta.trim()+","+s1+","+s2+",0";
			        	 else
			        		 eliminarRespu(s1);
			         }
	       
	          //  } else
	      		 // eliminarRespu(s1);
       
          // Log.w("ANDROID DYNAMIC VIEWS:", "EdiText: " + et.getText());
       } 
   if(child instanceof ToggleButton) {
       	
           ToggleButton tb = (ToggleButton)child;
          // Log.w("ANDROID DYNAMIC VIEWS:", "ToggleButton: " + tb.getText());
           s1= tb.getTag()+"";
           
          // Log.w("ANDROID DYNAMIC VIEWS:", "ToggleButton: " + tb.getText());
           if(tb.getMaxEms()== 4743) {
        	   
        	if(tb.isChecked())
        		s2=tb.getTextOn().toString();
        	else
        		s2=tb.getTextOff().toString();
        	
               datos=id_cliente+","+getFecha()+","+id_encuesta.trim()+","+s1+","+s2+",0";
             
            // else
     		  // eliminarRespu(s1);
          }
       }

    
 if(child instanceof Switch) {
	 Switch sw = (Switch)child;
   	    
   	    s1= sw.getTag()+"";
   	   if(sw.isChecked()){
   		s2="1";
   		datos=id_cliente+","+getFecha()+","+id_encuesta.trim()+","+s1+","+s2+",0,";
   	   }
   	   else
   	  eliminarRespu(s1);   
   	
   		 
 }
 if(child instanceof RatingBar) {
	 RatingBar rt = (RatingBar)child;
   	    
   	    s1= rt.getTag()+"";
   	   if(rt.getRating()!=0.0){
   		s2=""+rt.getRating();
   		datos=id_cliente+","+getFecha()+","+id_encuesta.trim()+","+s1+","+s2+",0,";
   	   }
   	   else
   	  eliminarRespu(s1);   
   	
   		 
 }
    
           //Support for other controls
       

 if(child instanceof ViewGroup) {
         ViewGroup group = (ViewGroup)child;
        guardarEncuesta(group,statement);
       }
     
      
      if(datos.length()>0){
   	   //	String encu[]=datos.split(",");
    	  //Log.w("DATOS", datos);
    	// Log.w("DATOS GUARDAR:", datos+"");
   	      String r=datos+","+getConfig("vend")+","+s_bloque+","+id_ramo+","+nombre_rel;
   		   cargarEncuenta(r,statement);
   	 }
      else
		  eliminarRespu(s1);
   }
   //this.db.baseDatos.execSQL("update formularios set flag='1' where id="+id_cliente.split("-")[0].trim()); 

}

public void guardarDatos(String s) {

	//Log.w("LLAMO DESDE",s);
	//Log.w("ENCUESTA",id_encuesta);
	LinearLayout root=	(LinearLayout)findViewById(R.id.linearLayout_Relev_Views);
	// d_cliente text,fecha text,id_encuesta text,id_pregunta text,respuesta text,flag text
	db.abrirBasedatos();
	String sql = "INSERT OR REPLACE INTO respuestas (id_cliente,fecha,id_encuesta,id_pregunta,respuesta,flag,vend,grupo,destino,dato9) VALUES (?,?,?,?,?,?,?,?,?,?) ";
	//String sql = "INSERT OR REPLACE INTO respuestas (id_cliente,fecha,id_encuesta,id_pregunta,respuesta,flag) VALUES (?,?,?,?,?,?)";
    SQLiteStatement statement =db.baseDatos.compileStatement(sql);
	db.baseDatos.beginTransaction();
	guardarEncuesta(root,statement);
   
    db.baseDatos.setTransactionSuccessful();	
    db.baseDatos.endTransaction();
    db.baseDatos.close();
 //   mostraDatos();
   this.actualizarAvence();
   // mostraDatos();
    //Log.w("DATOS GUARDAR:", "INSERT OR REPLACE INTO respuestas (id_cliente,fecha,id_encuesta,id_pregunta,respuesta,flag,vend,grupo,destino,dato9) VALUES (?,?,?,?,?,?,?,?,?,?)");

}
public void  irSalir(View v) {
	  Intent r = new Intent();
	  setResult(Activity.RESULT_CANCELED,r);
	  finish();
}
public void eliminarRespu(String id)
{
	//db.abrirdb.baseDatos();
	db.baseDatos.execSQL("DELETE FROM respuestas where id_cliente='"+id_cliente.split("-")[0].trim()+"' and id_pregunta='"+id+"' and id_encuesta like '"+id_encuesta+"' ");
	
   //this.db.baseDatos.close();

}

public void eliminarTodo()
{
	db.abrirBasedatos();
	db.baseDatos.execSQL("DELETE FROM respuestas where id_cliente='"+id_cliente.split("-")[0].trim()+"' and id_encuesta like '"+id_encuesta+"' ");
	
    db.baseDatos.close();
    LinearLayout l1 = (LinearLayout) findViewById(R.id.linearLayout_Relev_Views);
    if(l1.getChildCount() > 0) 
  	    l1.removeAllViews(); 

}

private void cargarEncuenta(String e,SQLiteStatement statement) {
	
	   	String encu[]=e.split(",");
	  //	Log.w("Item:",encu[4]);
	   			 statement.bindString(1, encu[0]);
	               statement.bindString(2, encu[1]);
	               statement.bindString(3, encu[2]);
	               //------------------------------
	               statement.bindString(4, encu[3]);
	               statement.bindString(5, encu[4]);
	               statement.bindString(6, encu[5]);
	               statement.bindString(7, encu[6]);
	               statement.bindString(8, encu[7]);
	               statement.bindString(9, encu[8]);
	               statement.bindString(10, encu[9]);
	           statement.execute();
	           
	          // Log.w("Item:",e);	
	} 




public String getFecha(){
	
	String s1 = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
	return s1;
}

public void openDialogGPS(String s) {
    final Dialog dialog = new Dialog(this); // Context, this, etc.
    dialog.setContentView(R.layout.dialog_gps);
    final TextView t_info = (TextView) dialog.findViewById(R.id.dialog_info);
    t_info.setText(s);
    dialog.setTitle("Ubicacion");
    dialog.show();
}


public String getRespuestas(String  id_pregunta)
{	
	String r="";
	
	Cursor localCursor = db.baseDatos.rawQuery("select respuesta from respuestas where id_cliente="+id_cliente+" and id_encuesta ='"+id_encuesta.trim()+"' and id_pregunta='"+id_pregunta+"'", null);
	//Log.w("SQL","select respuesta from respuestas where id_cliente="+id_cliente+" and id_encuesta ='"+id_encuesta.trim()+"' and id_pregunta='"+id_pregunta+"'");
	if(localCursor.getCount()>0){
	localCursor.moveToFirst();
	r= localCursor.getString(0);
	}
	localCursor.close();

	return r;
}

public String getRespuestas(String  id_pregunta, int i)
{	
	String r="";
	
	Cursor localCursor =db.baseDatos.rawQuery("select respuesta from respuestas where id_cliente="+id_cliente+" and id_encuesta ='"+id_encuesta.trim()+"' and id_pregunta='"+id_pregunta+"'", null);
	//Log.w("SQL","select respuesta from respuestas where id_cliente="+id_cliente+" and id_encuesta ='"+id_encuesta.trim()+"' and id_pregunta='"+id_pregunta+"'");
	if(localCursor.getCount()>0){
	localCursor.moveToFirst();
	r= localCursor.getString(0);
	}
	localCursor.close();
  if(r.length()==0){
	  for(int x=0; x<i;x++)
		  r+="#";
	  r+=" ";
  }
	return r;
}

public String getRamos()
{	
	String r="";
	db.abrirBasedatos();
	Cursor localCursor = db.baseDatos.rawQuery("select ramo from clientes where codigo like '"+id_cliente.split("-")[0].trim()+"' ", null);
	if(localCursor.getCount()>0){
	localCursor.moveToFirst();
	r= localCursor.getString(0);
	}
	localCursor.close();
	db.baseDatos.close();
	return r;
}

public String getNombrePegunta(String  data)
{	
	String r="";
	db.abrirBasedatos();
	Cursor localCursor = db.baseDatos.rawQuery("select  nombre from encuesta where id_encuesta='"+data+"' ", null);
	if(localCursor.getCount()>0){
	localCursor.moveToFirst();
	r= localCursor.getString(0);
	}
	localCursor.close();
	db.baseDatos.close();
	return r;
}

public String getTag1(){
	
	String s1 = (new SimpleDateFormat("ssSSS")).format(new Date());
	return s1;
}


private boolean checkLocation() {
    if(!isLocationEnabled())
        showAlert();
    return isLocationEnabled();
}

private void showAlert() {
    final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    dialog.setTitle("Habilitar ubicación")
            .setMessage("Por favor, habilite la ubicación")
            .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            })
            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                }
            });
    dialog.show();
}


private boolean isLocationEnabled() {
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
}


@SuppressWarnings("deprecation")
public void ubicacionGPS(final EditText t){
	  
	        if(!checkLocation())
	            return;
	      
	
	   alertDialogGPS.setTitle("ubicación GPS.");

       // Setting Dialog Message
	   alertDialogGPS.setMessage("Aguarde....");

       // Setting Icon to Dialog
	   alertDialogGPS.setIcon(R.drawable.ic_menu_mylocation);

       // Setting Positive "Yes" Button
	   alertDialogGPS.setButton(DialogInterface.BUTTON_POSITIVE,
	            "Agregar", new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialogInterface, int i) {
	                	if(longitudeGPS!=0)
	                	t.setText( longitudeGPS+";"+latitudeGPS);
	                }
	            });

	   alertDialogGPS.setButton(DialogInterface.BUTTON_NEGATIVE,
	            "Cancelar", new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialogInterface, int i) {

	                }
	            });
	

       // Showing Alert Message
	   alertDialogGPS.show();
	 
	         //   button.setText(R.string.pause);
}




private final LocationListener locationListenerGPS = new LocationListener() {
    public void onLocationChanged(final Location location) {
       longitudeGPS = location.getLongitude();
        latitudeGPS = location.getLatitude();
    	
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	
        		String londitude = "Longitud: " + location.getLongitude();
    			String latitude = "Latitud: " + location.getLatitude();
    			String altitiude = "Altitud: " + location.getAltitude()+" m";
    			String accuracy = "Exactitud: " + location.getAccuracy()+ " m";
    			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    			Date date = new Date(location.getTime());
    			String formatted = format.format(date);
    			String time = "Hora: " + formatted;
              alertDialogGPS.setMessage(londitude + "\n" + latitude + "\n"
     					+ altitiude + "\n" + accuracy + "\n" + time);
            	
            }
        });
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
};

public int maxID(String[] a) {
	 int id=0;
	 for (int i = 1; i <= a.length - 1; i++) {
		if (!a[i].equals("")) 
				id=i;
		}
		return 	id;
	}

}






