package com.axum.liderplus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.axum.config.Db;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;

public class FormResumenCensos extends Activity {
	
Db db;
Bundle bundle ;
String id_encuesta="";
String  meses[] = {"","Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
List<String> mesList = Arrays.asList(meses);
ArrayList<String> stringArray = new ArrayList<String>();
WebView myWebView ;
ArrayAdapter<String> adapter;
@SuppressWarnings("deprecation")
protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new Db(this);
		setContentView(R.layout.form_historico);
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		getActionBar().setHomeButtonEnabled(true); 
		getActionBar().setDisplayHomeAsUpEnabled(true);
		this.setTitle("Mes: ");
		bundle = getIntent().getExtras();
        id_encuesta=bundle.getString("id_encuesta");
      	 myWebView = (WebView) this.findViewById(R.id.webResumen);
   	 
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, getPeriodos());
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
   
     

 actionBar.setListNavigationCallbacks(adapter, new OnNavigationListener() {

		
		 public boolean onNavigationItemSelected(int itemPosition, long itemId) {

				 
				// Log.w("base error", "i:"+itemPosition );
				 Log.w("base error", "item:"+ adapter.getItem(itemPosition) );
				String meid= mesList.indexOf(adapter.getItem(itemPosition).trim())+"";
				 Log.w("Error", "i:"+meid );
				if(meid.length()==1)meid="0"+meid;
				 myWebView.loadData(getResumen(meid), "text/html", "UTF-8");
			return true;
			 }
     });

		
	
	}

		 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == android.R.id.home) {
			volver();
	          return true;
		}
		return super.onOptionsItemSelected(item);
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

public String getResumen(String periodo){
	StringBuffer sf0 = new StringBuffer().append("");
	StringBuffer sf1 = new StringBuffer().append("");
	int c=1,hoy =0;String fecha=getFecha();
		 db.abrirBasedatos();
		 List<String> vendList  = new ArrayList<String>();
		 List<String> pdvList  = new ArrayList<String>();
		 //Log.w("SQL:","select respuestas.vend, respuestas.fecha from respuestas where  strftime('%m', fecha)='"+periodo+"' and id_encuesta='"+id_encuesta+"' group by id_cliente ORDER BY fecha ASC");
		  Cursor localCursor = db.baseDatos.rawQuery("select respuestas.vend, respuestas.fecha from respuestas where  strftime('%m', fecha)='"+periodo+"' and id_encuesta='"+id_encuesta+"' group by id_cliente ORDER BY fecha ASC", null);
		  
		//  Log.w("SQL:","select respuestas.vend, respuestas.fecha from respuestas where  strftime('%m', fecha)='"+periodo+"' and id_encuesta='"+id_encuesta+"' group by id_cliente ORDER BY fecha ASC");
			    while (true)
			    {
			      if (!localCursor.moveToNext())
			      { 
			        localCursor.close();
			        db.baseDatos.close();
			      
			      
				   
			        break;
			      }
			 
			      int id=vendList.indexOf(localCursor.getString(0));
			      if(id==-1){
			    	  c=1;
			    	  vendList.add(localCursor.getString(0));
			    	  if(fecha.equals(localCursor.getString(1)))
			    		  hoy++;
			    	  pdvList.add(localCursor.getString(0)+";ACUMULADO;"+c+";HOY;"+hoy);
			       
			      }
			      else{
			    	  if(fecha.equals(localCursor.getString(1)))
			    		  hoy++;
			    	  pdvList.set(id,localCursor.getString(0)+";ACUMULADO;"+c+";HOY;"+hoy);
			    	
			      }
			     
			      c++;
			       
		      }
			    for(int i=0; i< pdvList.size();i++){
			    	String s[]=pdvList.get(i).split(";");
			    	sf0.append(s[0]+";"+s[3]+";"+s[4]+"-");
			    	sf1.append(s[0]+";"+s[1]+";"+s[2]+"-");
			    }
			
			    return getHtml(sf0.toString()+sf1.toString());
	     }


public  String getHtml(String data){
	int o=0,a=0,totalhoy=0,total=0;
	 String[] separated = data.split("-");
     StringBuffer sf = new StringBuffer().append("");
    /// Log.w("data1:",data+"/"+separated.length);
	 if(separated.length>0){
		 sf.append("<html ><body bgcolor=\"#FFFFFF\"> <font size=\"-1\" ><table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\" bgcolor=\"#585858\">");
	
		 for(int i=0; i<separated.length;i++){
			 String[] s1=separated[i].split(";");
			if(s1[1].equals("HOY")){
				if(o==0){
					o=1;
					sf.append(" <tr bgcolor='#6495ED' height='5' > <td colspan='2'></td> </tr>");  
					sf.append(" <tr bgcolor='#F0F0F0' > <td colspan='2'><center>"+s1[1]+"</center></td> </tr>");  
					sf.append(" <tr bgcolor='#585858' > <td width='75%'><font color='#FFFFFF'>Vend</font></td> <td><font color='#FFFFFF'>Pdvs</font></td> </tr>"); 	
				}
				totalhoy+=Integer.parseInt(s1[2]);
				if(Integer.parseInt(s1[2])>0 )
				 sf.append(" <tr bgcolor='#FFFFFF' > <td width='85%'>"+s1[0]+"</td> <td>"+s1[2]+"</td> </tr>"); 
				
			}else{
				if(o==1){
					sf.append(" <tr bgcolor='#FFFFFF' > <td width='85%'><font color='#6495ED'><strong>TOTAL</strong></font></td> <td><font color='#6495ED'><strong>"+totalhoy+"<strong></font></td> </tr>");
					sf.append(" <tr bgcolor='#ffffff' height='20' > <td colspan='2'></td> </tr>");  
					o++;
				}
				
				if(a==0){
					a=1;
					sf.append(" <tr bgcolor='#6495ED' height='5' > <td colspan='2'></td> </tr>");  
					sf.append(" <tr bgcolor='#F0F0F0' > <td colspan='2'><center>"+s1[1]+"</center></td> </tr>"); 
					sf.append(" <tr bgcolor='#585858' > <td width='75%'><font color='#FFFFFF'>Vend</font></td> <td><font color='#FFFFFF'>Pdvs</font></td> </tr>"); 	
				}
				total+=Integer.parseInt(s1[2]);
				 sf.append(" <tr bgcolor='#FFFFFF' > <td width='85%'>"+s1[0]+"</td> <td>"+s1[2]+"</td> </tr>"); 
		}
			 
		 }
			sf.append(" <tr bgcolor='#FFFFFF' > <td width='85%'><font color='#6495ED'><strong>TOTAL</strong></font></td> <td><font color='#6495ED'><strong>"+total+"<strong></font></td> </tr>");
		 sf.append("</table></font></body></html>");
	}

	return ( sf.toString() );	
}


public  ArrayList<String> getPeriodos(){
	
	 db.abrirBasedatos();
	 ArrayList<String> stringArray = new ArrayList<String>();
	//Log.w("data1:","select strftime('%m', fecha), count(*) from respuestas where id_encuesta='"+id_encuesta+"'  group by strftime('%m', fecha) ORDER BY fecha DESC");
	  Cursor localCursor = db.baseDatos.rawQuery("select strftime('%m', fecha), count(*) from respuestas where id_encuesta='"+id_encuesta+"'  group by strftime('%m', fecha) ORDER BY fecha DESC", null);
		    while (true)
		    {
		      if (!localCursor.moveToNext())
		      { 
		        localCursor.close();
		        db.baseDatos.close();
		        break;
		      }
		  /// Log.w("INFO:",meses[localCursor.getInt(0)]);
		     stringArray.add(meses[Integer.parseInt(localCursor.getString(0))]);
		   
		      }
		 
		    return stringArray;
    }

public boolean onKeyDown(int keyCode, KeyEvent event) {
 	super.onKeyDown(keyCode, event);
 	    	if (keyCode == KeyEvent.KEYCODE_BACK) {
 	    		volver();
 	    	}
 	    	return false;
 	    }

public void volver(){
	   Intent intent = new Intent().setClass(FormResumenCensos.this, MenuPpal.class);
       intent.putExtra("m","Censos");
       startActivity(intent);
       finish(); 
	}
	
}
