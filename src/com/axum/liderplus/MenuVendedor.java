package com.axum.liderplus;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.axum.config.Db;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MenuVendedor extends Activity implements SearchView.OnQueryTextListener{

	
    Bundle bundle;
    String httpString="";
    private GridView gridView;
   // String http_resu="";

    private ArrayList<String> listCod;
    private ArrayList<String> listCountry;
    private ArrayList<Integer> listFlag;
    private  SimpleAdapter  mAdapter;
    private SearchView mSearchView;
    String menuId;
	ArrayList<HashMap<String, String>> menuItems ;
    static ContentResolver  tm=null;
    Dialog dialog;
    ProgressBar pb;
   
    Db  db =new Db(this);
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_menu_camiones);
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		getActionBar().setHomeButtonEnabled(true); 
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		pb = (ProgressBar) findViewById(R.id.progressBar1);
	    gridView = (GridView) findViewById(R.id.gridView1);	
		tm = getContentResolver();
		if(getConfig("tiempo_max").length()==0)
			 guardarConfig("tiempo_max","30");
		if(getConfig("hora_inicio").length()==0)
			 guardarConfig("hora_inicio","08:00");
		if(getConfig("hora_fin").length()==0)
			 guardarConfig("hora_fin","16:00");
		
		
		 menuItems = new ArrayList<HashMap<String, String>>();
		mAdapter = new SimpleAdapter(this, menuItems,
					R.layout.gridview_row,new String[] {"imagen","row_codigo" ,"row_tiempo","row_pdvs","sellerID"}, new int[] {R.id.iconvend,
					R.id.tex_codigo,R.id.t_tiempo,R.id.t_pdvs,R.id.text_patente1});
		
   gridView.setOnItemClickListener(new OnItemClickListener() 
     {
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
		
         }

     });
   
 // int p = getResources().getIdentifier("cam_"+i, "raw", getPackageName());
   mAdapter.setViewBinder(new SimpleAdapter.ViewBinder() { 
		   
			public boolean setViewValue(View view, Object data,
		            String textRepresentation) {
				int num,hor,min,seg,p;
				int tiempo_max=Integer.parseInt(getConfig("tiempo_max"));
				
				   if (view.getId() == R.id.iconvend) {
					 String dato[]=textRepresentation.split(";");
			         // Log.w("DATA",textRepresentation);
			        ImageView i = ((ImageView) view);
			        num=Integer.parseInt(dato[1]);
			        hor=num/3600;
			        min=(num-(3600*hor))/60;
			       // Log.w("MINUTOS",min+"");
			        seg=num-((hor*3600)+(min*60));
			        if(min>tiempo_max || dato[1].equals("-1") || dato[1].equals("-2")) {
			        	
			        	 p= getResources().getIdentifier("male", "raw", getPackageName());
			        	if(dato[1].equals("-2"))
			        	   p= getResources().getIdentifier("male1", "raw", getPackageName());
			        }
			        else
			         p= getResources().getIdentifier("male31", "raw", getPackageName());
			        
			        i.setImageResource(p);
		        	 return true;	
		        }
				   
				   if (view.getId() == R.id.text_patente1) {
						 
				         // Log.w("DATA",textRepresentation);
					   TextView e = ((TextView) view);
				        num=Integer.parseInt(textRepresentation);
				        hor=num/3600;
				        min=(num-(3600*hor))/60;
				      //  Log.w("MINUTOS",min+"");
				        seg=num-((hor*3600)+(min*60));
				        if(min>tiempo_max || textRepresentation.equals("-1")  || textRepresentation.equals("-2") )
				        	e.setBackgroundColor(Color.parseColor("#F44336"));
				        if(textRepresentation.equals("-2"))
				        e.setBackgroundColor(Color.parseColor("#B7B6C1"));
				        
				        	// p= getResources().getIdentifier("male", "raw", getPackageName());
				       e.setText(textRepresentation);
				       if(textRepresentation.equals("-2"))
				    	   e.setText("");
			        	 return true;	
			        }
		    return false;
		    }
		    
		});
   
  
	final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	Switch sw = (Switch) findViewById(R.id.switch1);
	sw.setChecked( prefs.getBoolean("key_servicio", true));
   
   sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
           if (isChecked) {
        	   Editor editor = prefs.edit();
        		editor.putBoolean("key_servicio", true);
        		editor.commit();
        		Toast.makeText(MenuVendedor.this, "Alerta activada!", Toast.LENGTH_LONG).show();
        		
             
           } else {
        	    Editor editor = prefs.edit();
       		   editor.putBoolean("key_servicio", false);
       		  editor.commit();
       		Toast.makeText(MenuVendedor.this, "Alerta desactivada!", Toast.LENGTH_LONG).show();
       	
               // The toggle is disabled
           }
       }
   });	  
        bundle = getIntent().getExtras();
        menuId=bundle.getString("m");
	 

    	String[] url={"http://gps.axumvm.com.ar/LocationService.asmx"};
		new HttpAsyncTask().execute(url);
  
 
}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.m_vendedores_online, menu);
		 MenuItem searchItem = menu.findItem(R.id.buscar);
	    mSearchView = (SearchView) searchItem.getActionView();
	        setupSearchView(searchItem);
		return true;
	}
 private void setupSearchView(MenuItem searchItem) {

	        if (isAlwaysExpanded()) {
	            mSearchView.setIconifiedByDefault(false);
	        } else {
	            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
	                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
	        }

	        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
	       mSearchView.setOnQueryTextListener(this);
	    }
	 protected boolean isAlwaysExpanded() {
	        return false;
	    }
	public boolean onOptionsItemSelected(MenuItem item) {
	
		int id = item.getItemId();
		if (id == android.R.id.home) {
			
			volver();
			return true;
		}
   if (id == R.id.mi_refresh) {
			
	   String[] url={"http://gps.axumvm.com.ar/LocationService.asmx"};
		new HttpAsyncTask().execute(url);
			return true;
		}
		
		
		return super.onOptionsItemSelected(item);
	}
public String getConfig(String s)
	{
	     return getSharedPreferences("config", 0).getString(s,"");
	}
public  String getPOST(String url){
	   
    StringBuffer result = new StringBuffer(); 
    int responseE = -1;
   
 HttpURLConnection connection = null;  
	    try {
	    	
	    	 byte[] arrayOfByte = getBody().getBytes();
	         URL localURL = new URL(url);
	         connection = (HttpURLConnection)localURL.openConnection();
	         connection.setDoInput(true);
	         connection.setDoOutput(true);
	         connection.setRequestMethod("POST");
	         connection.setRequestProperty("Content-Type","text/xml");
	        new DataOutputStream(connection.getOutputStream()).write(arrayOfByte);
	         responseE = connection.getResponseCode();  
	        // Log.w("responseE",responseE+"");
	         if (responseE == HttpURLConnection.HTTP_OK) {
	        	 InputStream is = connection.getInputStream();
	        	 BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	        	 String line;
	        	 while((line = rd.readLine()) != null) {
	        		//Log.w("resultado",line);
	        		 result.append(line);
	       
	        	 	}
	        	 rd.close();
	      
	         	}
   	    

    } catch (Exception e) {
        Log.d("InputStream", e.getLocalizedMessage());
    }
	    finally {
	        try {
	        	connection.disconnect();
	        } catch (Exception e) {
	            e.printStackTrace(); //If you want further info on failure...
	        }
	    }
    // 11. return result
    return result.toString();
}


private class HttpAsyncTask extends AsyncTask<String, Void, String> {
	  
	   protected String doInBackground(String... urls) {
		  // String r=getPOST(urls[0]);
		   httpString=getPOST(urls[0]);
		   
		   return httpString;
	     }
	   
	  protected void onPreExecute() {
	     	pb.setVisibility(ProgressBar.VISIBLE);
	     }
	  

	     protected void onPostExecute(String result) {
	    	 pb.setVisibility(ProgressBar.INVISIBLE);
	    	 //Log.w("JSON",result);
	    	 postXML1(result);
	    	//mostrar( postXML(result),"");
	    }
	 }


public String getBody1()
	{
	  String fecha = (new SimpleDateFormat("yyyy/MM/dd")).format(new Date());
     String s="<?xml version=\"1.0\" encoding=\"utf-8\"?>" + 
     		"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + 
     		"  <soap:Body>" + 
     		"    <vendedoresClientsTimeoff xmlns=\"http://tempuri.org/\">" + 
     		"      <userName>"+getConfig("empresa")+"</userName>" + 
     		"    </vendedoresClientsTimeoff>" + 
     		"  </soap:Body>" + 
     		"</soap:Envelope>";
     //getConfig("empresa")
	 return s;
} 

public String getBody()
{
  String fecha = (new SimpleDateFormat("yyyy/MM/dd")).format(new Date());
 String s="<?xml version=\"1.0\" encoding=\"utf-8\"?>" + 
 		"<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" + 
 		"  <soap12:Body>" + 
 		"    <lastPositions xmlns=\"http://tempuri.org/\">" + 
 		"      <userName>"+getConfig("empresa")+"</userName>" + 
 		"    </lastPositions>" + 
 		"  </soap12:Body>" + 
 		"</soap12:Envelope>";
 //getConfig("empresa")
 return s;
} 
private void postXML1(String xml) {
	
	
    XMLParser parser = new XMLParser();
    Document doc = parser.getDomElement(xml);
    NodeList definitionElements = doc.getElementsByTagName("lastPositionsResult");
    String strDefinition = "";
    ArrayList<String> vendedores= new ArrayList<String>();
    ArrayList<String> vendedor= new ArrayList<String>();// listarVendedor();
    menuItems.clear();
    if(definitionElements.getLength()>0){
   	 
		     for (int i = 0; i < definitionElements.getLength(); i++) {
		       Node itemNode = definitionElements.item(i);
		       if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
		         Element definitionElement = (Element) itemNode;
		         NodeList wordDefinitionElements = (definitionElement).getElementsByTagName("string");
		         strDefinition = "";float max1=wordDefinitionElements.getLength();
		        // Log.w("Datos2",wordDefinitionElements.getLength()+"");
		         for (int j = 0; j < wordDefinitionElements.getLength(); j++) {
		           Element wordDefinitionElement = (Element) wordDefinitionElements .item(j);
		           NodeList textNodes = ((Node) wordDefinitionElement).getChildNodes();
		          // Log.d("data00",j+" "+ ((Node) textNodes.item(0)).getNodeValue());
		          // mostrar( ((Node) textNodes.item(0)).getNodeValue(),vendedor,j);
		           vendedores.add(((Node) textNodes.item(0)).getNodeValue());
		           String s[]=((Node) textNodes.item(0)).getNodeValue().toString().split(",");
		           vendedor.add(s[2]);
		           //Log.d("data00",j+" "+ ((Node) textNodes.item(0)).getNodeValue());
		           
		          
		         }
		       
		       }
		     }
		     mostrar(vendedores,vendedor);
		     
		
    }   
    
    
}



public void mostrar(ArrayList<String> vendedores,ArrayList<String> vendedor)
{
    ArrayList<String> listaVend=  listarVendedor();
	   
	   int num,hor,min,seg;
	   
	   String tiermpo="-1";
	   String s="-1";
	   
	  for(int i=0;i<listaVend.size();i++) {
		  
		  int index=vendedor.indexOf(listaVend.get(i));
		 if(index!=-1) {
			 
			 String dato[]= vendedores.get(index).split(",");  
			     HashMap map = new HashMap();
			     map.put("row_codigo",dato[2]);
			     if(!dato[4].equals("nnn")) {
			        num=Integer.parseInt(dato[4]);
			        hor=num/3600;
			        min=(num-(3600*hor))/60;
			        seg=num-((hor*3600)+(min*60));
			        tiermpo=String.format("%02d",hor)+":"+String.format("%02d",min)+":"+String.format("%02d",seg);
			        s=dato[4];
		         }
			       // System.out.println(hor+"h "+min+"m "+seg+"s");
			     map.put("row_pdvs",dato[3]);
			     map.put("row_tiempo",tiermpo+"");
			     map.put("sellerID",s);
			     map.put("imagen",i+";"+s);
			    // Log.w("INDICE",v+"");
			     menuItems.add(map);
			    
		     }else {
		    	 tiermpo="-2";
		    	 HashMap map = new HashMap();
			     map.put("row_codigo",listaVend.get(i));
			     map.put("row_pdvs","");
			     map.put("row_tiempo",tiermpo+"");
			     map.put("sellerID","-2");
			     map.put("imagen",i+";"+"-2");
			     menuItems.add(map);
		     }
	  }
		 gridView.setAdapter(mAdapter);
		    gridView.deferNotifyDataSetChanged();  
    
}
public boolean onKeyDown(int keyCode, KeyEvent event) {
     	super.onKeyDown(keyCode, event);
     	    	if (keyCode == KeyEvent.KEYCODE_BACK) {
     	    	
     	    		volver();
     	    		
     	    	}
     	    	return false;
    }	
	
public void volver(){
	
	Intent mainIntent = new Intent().setClass(MenuVendedor.this, MenuPpal.class);
     mainIntent.putExtra("m","GestionVentas");
     startActivity(mainIntent);
     finish();
	}


@Override
public boolean onQueryTextSubmit(String query) {
	// TODO Auto-generated method stub
	return false;
}


@Override

public boolean onQueryTextChange(String s) {
	
	postXML1(httpString);
	return false;
}	


public boolean isOnline() {
	ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo, mobileInfo;
    boolean connected = false;
    try {
        connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    connected = networkInfo != null && networkInfo.isAvailable() &&
            networkInfo.isConnected();
    return connected;


    } catch (Exception e) {
        //System.out.println("Compruebe Conectividad " + e.getMessage());
        //L//og.v("connectivity", e.toString());
    }
    return connected;
}	



private static String getID()
	{

  return  Secure.getString(tm, Secure.ANDROID_ID);
	}

public ArrayList<String>  listarVendedor(){
	ArrayList<String> vendedores= new ArrayList<String>();
	
		 db.abrirBasedatos();
		 Cursor localCursor = db.baseDatos.rawQuery("select codigo from vendedores  ", null);
			    while (true)
			    {
			      if (!localCursor.moveToNext())
			      { 
			        localCursor.close();
			        db.baseDatos.close();
			        break;
			      }
			  
			      vendedores.add(localCursor.getString(0));
			    	 // map.put("eficiencia",localCursor.getString(2)+"%"); 
			    	
			    	
			 }
		return  vendedores;
    } 

public void irAjustes(View view){
	  AlertDialog.Builder builder = null;

dialog = new Dialog(this);
LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
final View v = inflater.inflate(R.layout.dialogo_listo2, null);


final  EditText tx_tiempo_max = (EditText) v.findViewById(R.id.tx_tiempo_max);
if(getConfig("tiempo_max").length()>0)
	tx_tiempo_max.setText(getConfig("tiempo_max"));

//s_trabajo.setBackgroundResource(R.drawable.spinner_selector);
final  EditText tx_hora_inicio = (EditText) v.findViewById(R.id.tx_hora_inicio);
tx_hora_inicio.setBackgroundResource(android.R.drawable.edit_text);
final  EditText tx_hora_fin = (EditText) v.findViewById(R.id.tx_hora_fin);
tx_hora_fin.setBackgroundResource(android.R.drawable.edit_text);

if(getConfig("hora_inicio").length()>0)
	tx_hora_inicio.setText(getConfig("hora_inicio"));
if(getConfig("hora_fin").length()>0)
	tx_hora_fin.setText(getConfig("hora_fin"));

tx_tiempo_max.setBackgroundResource(android.R.drawable.edit_text);


tx_hora_inicio.setOnClickListener(new View.OnClickListener() {
    public void onClick(View v) {
    	   
        Calendar mcurrentTime = Calendar.getInstance();
        String tiempo[] =getConfig("hora_inicio").split(":");
        int hour = Integer.parseInt(tiempo[0]);
        int minute =  Integer.parseInt(tiempo[1]);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(MenuVendedor.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            	
            	tx_hora_inicio.setText(String.format("%02d",selectedHour) + ":" + String.format("%02d",selectedMinute));
            	//showHora(parent,id,String.format("%02d",selectedHour) + ":" + String.format("%02d",selectedMinute));
               // eReminderTime.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        //mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

});


tx_hora_inicio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
  	 if( hasFocus){
  		 ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(tx_hora_inicio.getWindowToken(), 0);
  		    Calendar mcurrentTime = Calendar.getInstance();
  		     String tiempo[] =getConfig("hora_inicio").split(":");
             int hour = Integer.parseInt(tiempo[0]);
             int minute =  Integer.parseInt(tiempo[1]);
	            TimePickerDialog mTimePicker;
	            mTimePicker = new TimePickerDialog(MenuVendedor.this, new TimePickerDialog.OnTimeSetListener() {
	                @Override
	                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
	                	tx_hora_inicio.setText(String.format("%02d",selectedHour) + ":" + String.format("%02d",selectedMinute));
	                   // eReminderTime.setText( selectedHour + ":" + selectedMinute);
	                }
	            }, hour, minute, true);//Yes 24 hour time
	            //mTimePicker.setTitle("Select Time");
	            mTimePicker.show();
  	 }
      
    }
  });


tx_hora_fin.setOnClickListener(new View.OnClickListener() {
    public void onClick(View v) {
    	   
        Calendar mcurrentTime = Calendar.getInstance();
        String tiempo[] =getConfig("hora_fin").split(":");
        int hour = Integer.parseInt(tiempo[0]);
        int minute =  Integer.parseInt(tiempo[1]);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(MenuVendedor.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            	
            	tx_hora_fin.setText(String.format("%02d",selectedHour) + ":" + String.format("%02d",selectedMinute));
            	//showHora(parent,id,String.format("%02d",selectedHour) + ":" + String.format("%02d",selectedMinute));
               // eReminderTime.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        //mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

});


tx_hora_fin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
  	 if( hasFocus){
  		 ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(tx_hora_fin.getWindowToken(), 0);
  		    Calendar mcurrentTime = Calendar.getInstance();
  		     String tiempo[] =getConfig("hora_fin").split(":");
  	         int hour = Integer.parseInt(tiempo[0]);
  	         int minute =  Integer.parseInt(tiempo[1]);
	            TimePickerDialog mTimePicker;
	            mTimePicker = new TimePickerDialog(MenuVendedor.this, new TimePickerDialog.OnTimeSetListener() {
	                @Override
	                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
	                	tx_hora_fin.setText(String.format("%02d",selectedHour) + ":" + String.format("%02d",selectedMinute));
	                   // eReminderTime.setText( selectedHour + ":" + selectedMinute);
	                }
	            }, hour, minute, true);//Yes 24 hour time
	            //mTimePicker.setTitle("Select Time");
	            mTimePicker.show();
  	 }
      
    }
  });

builder = new AlertDialog.Builder(this);
builder.setView(v);
builder.setTitle("Ajustes");






Button cancelar = (Button) v.findViewById(R.id.cancelar);

cancelar.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick(View v) {
        // Close dialog
    	dialog.dismiss();
    }
});

Button save = (Button) v.findViewById(R.id.save);
// if decline button is clicked, close the custom dialog
save.setOnClickListener(new OnClickListener() {
   @Override
   public void onClick(View v) {
	
	  if( tx_tiempo_max.getText().toString().length()>0 && tx_hora_inicio.getText().toString().length()>0 && tx_hora_fin.getText().toString().length()>0  ){
	      guardarConfig("tiempo_max",tx_tiempo_max.getText().toString());
	      guardarConfig("hora_inicio",tx_hora_inicio.getText().toString());
	      guardarConfig("hora_fin",tx_hora_fin.getText().toString());
	      Toast.makeText(MenuVendedor.this, "El Valor se guardo en exito!", Toast.LENGTH_LONG).show();
	  }else {
			Toast.makeText(MenuVendedor.this, "ERROR AL GUARDAR DATO\nLOS CAMPOS NO DEBEN ESTAR VACIOS", Toast.LENGTH_LONG).show();
	  }
		       dialog.dismiss();
	  
	   }
   
});


dialog = builder.create();
dialog.show();


}



public void guardarConfig(String paramString1, String paramString2)
{
SharedPreferences.Editor localEditor =getSharedPreferences("config", 0).edit();
localEditor.putString(paramString1, paramString2);
localEditor.commit();
//Toast.makeText(this, "Impresora guardada con exito!!", Toast.LENGTH_LONG).show();
}   
}
