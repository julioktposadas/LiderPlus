package com.axum.mapa;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.axum.liderplus.Clientes;
import com.axum.liderplus.MenuPpal;
import com.axum.liderplus.R;
import com.axum.liderplus.XMLParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class FormMapaClientes2 extends FragmentActivity implements OnMarkerClickListener{

	// Google Map
	private GoogleMap googleMap;

	 private static final int REGISTRATION_TIMEOUT = 3 * 1000;
	 private static final int WAIT_TIMEOUT = 30 * 1000;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


	Bundle bundle ;
	static double longitute=-27.3849822;
	static double latitude=-55.9283694;
	static String latlon="";
	String vendId="",idCliente="";
	ProgressBar pb;
	
	String zona="Todas";
	ArrayList<String> listVisitas=new ArrayList<String>();
	ArrayList<String> listDatos=new ArrayList<String>();
	ArrayList<String> listaZonas=new ArrayList<String>();
	ArrayList<String> listaClientes=new ArrayList<String>();
	
	 private int year, month, day;
	 private DatePicker datePicker;
	 private Calendar calendar;
	 
	private TextView dateView,tex_zona;
	AlertDialog levelDialog;
	private CharSequence[] items; 
	String ubicacion="";
	  int inputSelection=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_mapa_google);
		pb = (ProgressBar) findViewById(R.id.progressGPSgoogle);
		 bundle = getIntent().getExtras();
			getActionBar().setIcon(R.drawable.ic_filter_list_white_24dp);
			getActionBar().setHomeButtonEnabled(true); 
			dateView = (TextView) findViewById(R.id.dateView);
			tex_zona= (TextView) findViewById(R.id.tex_zona);
			calendar = Calendar.getInstance();
		    year = calendar.get(Calendar.YEAR);
		    month = calendar.get(Calendar.MONTH);
		    day = calendar.get(Calendar.DAY_OF_MONTH);
			
		    showDate(year, month+1, day); 
			
			//idCliente=bundle.getString("idCliente");
		try {
			// Loading map
			initilizeMap();
			initializeUiSettings();
			initializeMapLocationSettings();
			//initializeMapTraffic();
			initializeMapType();
			
		
				vendId= bundle.getString("vendId");  //getConfig("vend");
				
				//String coor=bundle.getString("coordenada");
				
				String[] url={"http://gps.axumvm.com.ar/LocationService.asmx"};
			   // Log.w("URL", url[0]);
				new HttpAsyncTask().execute(url);
		
			googleMap.setOnMarkerClickListener(this);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		// initilizeMap();
	}
	
public boolean onCreateOptionsMenu(Menu menu) {
		 if(idCliente.length()==0)
			getMenuInflater().inflate(R.menu.m_gps, menu);
			return true;
}

public boolean onOptionsItemSelected(MenuItem item) {
	
	int id = item.getItemId();
	if (id == R.id.mi_gps_vend) {
		
		googleMap.clear();
		
		String[] url={"http://gps.axumvm.com.ar/LocationService.asmx",""};
		new HttpAsyncTask().execute(url);
		
		return true;
	}
	if (id == R.id.mi_date) {
		showDialog(999);
	}
	

if (id == android.R.id.home) {
	zonaDialog();
	//volver();
      return true;
}	
	
	return super.onOptionsItemSelected(item);
}

private void zonaDialog() {
final CharSequence[] items =getZonaClientes();

	for(int i=0; i<items.length; i++){
		if(items[i].toString().equals(getConfig("filtro"))){
			inputSelection=i;
			break;
		}
	}
	

    // Creating and Building the Dialog
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Zonas");

 
	builder.setSingleChoiceItems(items,inputSelection,
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    inputSelection = item;
                    zona=items[inputSelection].toString();
                   // guardarConfig("filtro",items[inputSelection].toString());
                    //dateView.setText(dateView.getText().toString().split("\n")[0] +"\nZona :" +items[inputSelection].toString());
                    tex_zona.setText("Zona : "+items[inputSelection].toString());
                    
                  mostrarClientes(zona,0,ubicacion);
                    levelDialog.dismiss();
                }
            });
    levelDialog = builder.create();
    levelDialog.show();
    
}

public CharSequence[]  getZonaClientes(){
	
	CharSequence[] items = null ;
	 items = new CharSequence[listaZonas.size()+1] ;
	 items[0]="Todas";
	for(int i=1; i<listaZonas.size()+1;i++){
		items[i]=listaZonas.get(i-1);
	}
	    return items;
}	

	private void initilizeMap() {

		googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment)).getMap();

		// check if map is created successfully or not
		if (googleMap == null) {
			Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
		}

		(findViewById(R.id.mapFragment)).getViewTreeObserver().addOnGlobalLayoutListener(
				new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {
						// gets called after layout has been done but before
						// display
						// so we can get the height then hide the view
						if (android.os.Build.VERSION.SDK_INT >= 16) {
							(findViewById(R.id.mapFragment)).getViewTreeObserver().removeOnGlobalLayoutListener(this);
						} else {
							(findViewById(R.id.mapFragment)).getViewTreeObserver().removeGlobalOnLayoutListener(this);
						}
						//setCustomMarkerOnePosition();
						//setCustomMarkerTwoPosition();
						// plotMarkers(markerList);
					}
				});
	}


	public void initializeUiSettings() {
		googleMap.getUiSettings().setCompassEnabled(true);
		googleMap.getUiSettings().setRotateGesturesEnabled(false);
		googleMap.getUiSettings().setTiltGesturesEnabled(true);
		googleMap.getUiSettings().setZoomControlsEnabled(true);
		googleMap.getUiSettings().setMyLocationButtonEnabled(true);
	}

	public void initializeMapLocationSettings() {
		googleMap.setMyLocationEnabled(true);
	}

	public void initializeMapTraffic() {
		googleMap.setTrafficEnabled(true);
	}

	public void initializeMapType() {
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	}

	public void initializeMapViewSettings() {
		googleMap.setIndoorEnabled(true);
		googleMap.setBuildingsEnabled(false);
	}

	
public void market(){
 LatLng HAMBURG = new LatLng(-27.3848497,-55.9272532);
 LatLng KIEL = new LatLng(-27.3846387,-55.9292121);
	 
 Marker melbourne = googleMap.addMarker(new MarkerOptions().position(KIEL)
		    .icon(BitmapDescriptorFactory
		        .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
 Marker otro = googleMap.addMarker(new MarkerOptions().position(HAMBURG)
		    .icon(BitmapDescriptorFactory
		        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN )));
		    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 1));

		    // Zoom in, animating the camera.
		    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
}



public  String getPOST(String[] url,String body ){
    InputStream inputStream = null;
    //String result = "";
    StringBuffer result = new StringBuffer(); 
    StringBuffer result1 = new StringBuffer(); 
    int responseE = -1;
    
  // Log.w("BODY", body);
    int t=1;
 
    HttpURLConnection connection = null;  
	    try {
	    //Log.w("DATO CANT", t+"");
	    
	    	 byte[] arrayOfByte=null;
	    	//Log.w("DATO", getBody()+"");
	    	
	    			arrayOfByte =body.getBytes();// getBody().getBytes();
	    			// Log.w("DATO", body+"");
		         URL localURL = new URL(url[0]);
		         connection = (HttpURLConnection)localURL.openConnection();
		         connection.setDoInput(true);
		         connection.setDoOutput(true);
		         connection.setReadTimeout(4000);
		         connection.setRequestMethod("POST");
		         connection.setRequestProperty("Content-Type","text/xml");
		         new DataOutputStream(connection.getOutputStream()).write(arrayOfByte);
		         responseE = connection.getResponseCode();                 
		         if (responseE == HttpURLConnection.HTTP_OK) {
		        	 InputStream is = connection.getInputStream();
		        	 BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		        	 String line;
		        	 while((line = rd.readLine()) != null) {
			        	
			        		 result.append(line);
			        	
		        	}
		        	
		        	 rd.close();
		      
		         	}
	    

    } catch (Exception e) {
    	return null;
        //Log.d("InputStream", e.getLocalizedMessage());
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
public String getBody(){
	String POST="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
		+"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
		  +"<soap:Body>"
		  +"<lastPositionFor xmlns=\"http://tempuri.org/\">"
		      +"<distriName>"+getConfig("empresa")+"</distriName>"
		     +" <vendedorId>"+vendId+"</vendedorId>"
		   +" </lastPositionFor>"
		  +"</soap:Body>"
		+"</soap:Envelope>";
	
	return POST;
}


public String getBody1(){
String POST="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
+"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
+" <soap:Body>"
    +"<allClientsPositionByVendedor xmlns=\"http://tempuri.org/\">"
     +" <userName>"+getConfig("empresa")+"</userName>"
     +" <idVendedor>"+vendId+"</idVendedor>"
    +"</allClientsPositionByVendedor>"
 +" </soap:Body>"
+"</soap:Envelope>";
	return POST;
}


public String getBody3(){
	String POST="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
+"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
  +"<soap:Body>"
    +"<FindLocationsOfVendedorIdEnDia xmlns=\"http://tempuri.org/\">"
      +"<userName>districerrepgps</userName>"
      +"<idVendedor>c61</idVendedor>"
      +"<dia>2015/11/26</dia>"
    +"</FindLocationsOfVendedorIdEnDia>"
  +"</soap:Body>"
+"</soap:Envelope>";

    return POST;
    }
public String getBody2(){

String s1 = dateView.getText().toString();
String POST="<?xml version=\"1.0\" encoding=\"utf-8\"?>" + 
		"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + 
		"  <soap:Body>" + 
		"    <vistisOnDate xmlns=\"http://tempuri.org/\">" + 
		"      <userName>"+getConfig("empresa")+"</userName>" + 
		"      <aDate>"+s1+"</aDate>" + 
		"      <sellerId>"+vendId+"</sellerId>" + 
		"    </vistisOnDate>" + 
		"  </soap:Body>" + 
		"</soap:Envelope>";
	return POST;
}



private String postXML(String xml) {
	  ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();
	 String s="";
	 String strDefinition = "";
if( xml.indexOf("lastPositionForResponse")>0){
	     XMLParser parser = new XMLParser();
	     Document doc = parser.getDomElement(xml); // getting DOM element
	     NodeList definitionElements = doc.getElementsByTagName("lastPositionForResponse");
	     //Log.w("Datos", definitionElements.getLength()+"");
	   for (int i = 0; i < definitionElements.getLength(); i++) {
	       Node itemNode = definitionElements.item(i);
	       if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
	         Element definitionElement = (Element) itemNode;
	         NodeList wordDefinitionElements = (definitionElement).getElementsByTagName("lastPositionForResult");
	         strDefinition = "";
	       //  Log.w("Datos2",wordDefinitionElements.getLength()+"");
	         for (int j = 0; j < wordDefinitionElements.getLength(); j++) {
	           Element wordDefinitionElement = (Element) wordDefinitionElements .item(j);
	           NodeList textNodes = ((Node) wordDefinitionElement).getChildNodes();
	           if(textNodes.getLength()>0)
	           strDefinition += ((Node) textNodes.item(0)).getNodeValue() ;
	         }
	     
	       }
	     }
	}
	    return strDefinition;
}
private class HttpAsyncTask extends AsyncTask<String, Void, String> {
 
  protected String doInBackground(String... urls) {
	  
	   	StringBuffer sb= new StringBuffer();
	    String allClintes =getPOST(urls,getBody1())+" ";
	  //  Log.w("BODY",urls+" "+getBody1());
	    String visitados= getPOST(urls,getBody2())+" ";
	    String r=getPOST(urls,getBody())+" ";
	    if(allClintes!=null&&visitados!=null&&r!=null)
	    sb.append(visitados+"@"+allClintes+"@"+r);
	 //  postXML1(visitados);
	  //Log.w("VISITAS",allClintes);
	  //Log.w("VISITAS",visitados+" datos");
	  
		//getZonasClientes1(1);
        return sb.toString();
    }
  
 protected void onPreExecute() {
    	pb.setVisibility(ProgressBar.VISIBLE);
    	inputSelection=0;
    	listVisitas.clear();
    	listDatos.clear();
    	listaZonas.clear();
    	listaClientes.clear();
    }
 
  protected void onPostExecute(String result) {
   	 pb.setVisibility(ProgressBar.INVISIBLE);
   	///googleMap.clear();
 if(result.length()>0){
	
   	
	 postXML1(result.split("@")[0],result.split("@")[1]);	
   if((result.split("@")).length>2)
    ubicacion=postXML(result.split("@")[2]);
    
 	mostrarClientes(zona,ubicacion.length(),ubicacion); 
 	
  
   
   }
    }
}

@SuppressLint("NewApi")
public void alertaSalir() {
	    new AlertDialog.Builder(this)
	    		.setTitle("Alerta")
	           .setMessage("La ubicacion del vendedor no esta disponible !" )
	           .setCancelable(false)
	            .setIconAttribute(android.R.attr.alertDialogIcon)
	           .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            
	            	   volver();
	               }
	           })
	       
	           .show();
	}



public String getConfig(String p1){
		SharedPreferences prefs =getSharedPreferences("config",MODE_PRIVATE);
			 return  prefs.getString(p1, "");
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

public void volver(){
	 Intent mainIntent = null;
	if(bundle.getString("m").equals("FormUbicacion")){
	
     mainIntent = new Intent().setClass(FormMapaClientes2.this, MenuPpal.class);
     mainIntent.putExtra("m","FormUbicacion");
	}
	if(bundle.getString("m").equals("Clientes")){
		
	     mainIntent = new Intent().setClass(FormMapaClientes2.this, Clientes.class);
	     mainIntent.putExtra("m","Clientes");
	     mainIntent.putExtra("idCliente",bundle.getString("idCliente"));
	     mainIntent.putExtra("id_encuesta",bundle.getString("id_encuesta"));
	   
		}
     startActivity(mainIntent);
     finish(); 
	
}



public boolean onKeyDown(int keyCode, KeyEvent event) {
  	super.onKeyDown(keyCode, event);
  	    	if (keyCode == KeyEvent.KEYCODE_BACK) {
  	    		
  	    		
  	    		volver();
  	    	}
  	    	return false;
  	    }

@Override
public boolean onMarkerClick(Marker arg0) {
	// TODO Auto-generated method stub
	return false;
}

private String postXML1(String xml, String xml1) {
	 String strDefinition = "";
	 LatLng point = null;
	 double lat = 0,lng = 0;
	 try {
	if( xml.indexOf("vistisOnDateResponse")>0){
		     XMLParser parser = new XMLParser();
		     Document doc = parser.getDomElement(xml); // getting DOM element
		     NodeList definitionElements = doc.getElementsByTagName("vistisOnDateResponse");
		     //Log.w("Datos", definitionElements.getLength()+"");
		   for (int i = 0; i < definitionElements.getLength(); i++) {
		       Node itemNode = definitionElements.item(i);
		       if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
		         Element definitionElement = (Element) itemNode;
		         NodeList wordDefinitionElements = (definitionElement).getElementsByTagName("vistisOnDateResult");
		         strDefinition = "";
		       //  Log.w("Datos2",wordDefinitionElements.getLength()+"");
		         for (int j = 0; j < wordDefinitionElements.getLength(); j++) {
		           Element wordDefinitionElement = (Element) wordDefinitionElements .item(j);
		           NodeList textNodes = ((Node) wordDefinitionElement).getChildNodes();
		           if(textNodes.getLength()>0)
			           strDefinition += ((Node) textNodes.item(0)).getNodeValue() ;
			      
		         }
		     }
		     }
		   //*********************************************
		  // listVisitas
	        try {
	        	JSONArray jsonArray = new JSONArray(strDefinition);
	        	//Log.w("resultado",strDefinition);
	        	//Log.w("resultado",jsonArray.length()+"");
	        	for (int i = 0; i < jsonArray.length(); i++) {
	        		//Log.w("resultado",jsonArray.length()+"/"+i);
	    		    JSONObject obj1 = jsonArray.getJSONObject(i);
	    		  //  "lat":-27.463183332747,"lng":-55.8570138770483
	    		    lat=obj1.getDouble("lat");
	    		    lng=obj1.getDouble("lng");
	    		    listVisitas.add(obj1.getString("clientId"));
	    		    listDatos.add("Hs :"+obj1.getString("start").split("T")[1]+" Tiempo : "+obj1.getString("durationInMinutes")+" min");
			 }
		       
	      
	        	} catch (JSONException e) {
	        		
	        	return "";
		   		//e.printStackTrace();
		   	}  
	        parser = new XMLParser();
		    doc = parser.getDomElement(xml1); // getting DOM element
		   // doc.is
		    definitionElements = doc.getElementsByTagName("allClientsPositionByVendedorResponse");
		     //Log.w("Datos", definitionElements.getLength()+"");
		   for (int i = 0; i < definitionElements.getLength(); i++) {
		       Node itemNode = definitionElements.item(i);
		       if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
		         Element definitionElement = (Element) itemNode;
		         NodeList wordDefinitionElements = (definitionElement).getElementsByTagName("string");
		         strDefinition = "";
		       //  Log.w("Datos2",wordDefinitionElements.getLength()+"");
		         for (int j = 0; j < wordDefinitionElements.getLength(); j++) {
		           Element wordDefinitionElement = (Element) wordDefinitionElements .item(j);
		           NodeList textNodes = ((Node) wordDefinitionElement).getChildNodes();
		           if(textNodes.getLength()>0){
		        	   strDefinition = ((Node) textNodes.item(0)).getNodeValue() ;
			           String split[]=strDefinition.split(",");
			           longitute=Double.parseDouble(split[1]);
						latitude=Double.parseDouble(split[2]);
			          
			           String datos="";
			           BitmapDescriptor defaultMarker=null;
			           int index=-1;
			           
			           if(listaZonas.indexOf(split[6])==-1)
			        	   listaZonas.add(split[6]);
			           
			          Log.w("Datos2",listaZonas.indexOf(split[6])+"");
			           
			              
					if((index=listVisitas.indexOf(split[0]))!=-1){
					    // defaultMarker=BitmapDescriptorFactory.fromResource(R.raw.gota_verde);
					     datos=listDatos.get(index);
					     listaClientes.add(longitute+","+latitude+","+split[0]+"-"+split[3]+","+datos+","+index+","+split[6]);
			           }
			           else{
			        	   //defaultMarker=BitmapDescriptorFactory.fromResource(R.raw.gota_azul);
			        	   datos=split[5];
			        	   listaClientes.add(longitute+","+latitude+","+split[0]+"-"+split[3]+","+datos+","+index+","+split[6]);
			           }
			           
						
			          
		           }
			      
		         }
		     }
		     }   
		 //   point = new LatLng(lat, lng);
			//googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
			// googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
	        
	        
	      //********************************************  
		   
		}
	 } catch (Exception e) {
		 return "";
	   		//e.printStackTrace();
	   	}  
	    return strDefinition;
}


public void mostrarClientes(String zona, int estado, String s1){
	 LatLng point = null,point2 = null;
	 googleMap.clear();
	 
	   BitmapDescriptor defaultMarker=null;
		Log.w("DATA CANTIDAD",listaClientes.size()+"");
	   
	for(int i=0; i< listaClientes.size();i++){
		String split[]=listaClientes.get(i).toString().split(",");
		String zonaS="Todas";
		if(split.length< 6)zona="Todas";
		else
		zonaS=split[5];
		
		if(zonaS.equals(zona)|| zona.equals("Todas")){
			
			    longitute=Double.parseDouble(split[0]);
				latitude=Double.parseDouble(split[1]);
		        point = new LatLng(longitute,latitude);
		        if(split[4].equals("-1")){
		        	defaultMarker=BitmapDescriptorFactory.fromResource(R.raw.gota_azul);
		        }
		        else{
		        	 defaultMarker=BitmapDescriptorFactory.fromResource(R.raw.gota_verde); 
		        	 point2 = new LatLng(longitute,latitude);
		        }
		        
			 Marker mapMarker = googleMap.addMarker(new MarkerOptions()
			    .position(point)					 								    
			    .title(split[2])
			    .snippet(split[3])
			    .icon(defaultMarker));
		}
		
		
	}
	if(estado==0){
		if(point2==null)point2=point;
		if(point2!=null){
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point2, 14));
			 googleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
		}
	}
	
	 if(s1.length()>0){
	   	  	
  		  // Log.w("DATO CANT", t+"");
		   	   	String s[]=s1.split(",");
		   	   	FormMapaClientes2.this.setTitle("  Vend: "+vendId+"  ["+s[3]+"]");
		    	longitute=Double.parseDouble(s[0]);
				latitude=Double.parseDouble(s[1]);
				 point = new LatLng(longitute,latitude);
				  defaultMarker=BitmapDescriptorFactory.fromResource(R.raw.vend);
				
				// String sd[]= getDatalleCliente(idCliente).split(",");
				   Marker mapMarker = googleMap.addMarker(new MarkerOptions()
				    .position(point)					 								    
				    .title("Vendedor : "+vendId)
				    .snippet("["+s[3]+"]")
				    .icon(defaultMarker));
				  googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
                 googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null); 
			
			 
	   	 }
}
protected Dialog onCreateDialog(int id) {
    // TODO Auto-generated method stub
    if (id == 999) {
       return new DatePickerDialog(this,myDateListener, year, month, day);
    }
    return null;
 }

 private DatePickerDialog.OnDateSetListener myDateListener = new 
    DatePickerDialog.OnDateSetListener() {
    @Override
    public void onDateSet(DatePicker arg0, 
       int arg1, int arg2, int arg3) {
       // TODO Auto-generated method stub
       // arg1 = year
       // arg2 = month
       // arg3 = day
       showDate(arg1, arg2+1, arg3);
       googleMap.clear();
		String coor=bundle.getString("coordenada");
		String[] url={"http://gps.axumvm.com.ar/LocationService.asmx",coor+""};
		new HttpAsyncTask().execute(url);
       
    }
 };

 private void showDate(int year, int month, int day) {
	 String s="",s1="";
	 if((month+"").length()==1)s="0";
	 if((day+"").length()==1)s1="0";
    dateView.setText(new StringBuilder().append(year).append("-").append(s+month).append("-").append(s1+day));
 }

}
