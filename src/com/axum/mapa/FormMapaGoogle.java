package com.axum.mapa;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.axum.config.Db;
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


@SuppressLint("NewApi")
public class FormMapaGoogle extends FragmentActivity implements OnMarkerClickListener{

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
	Db db= new Db(this);
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_mapa_google);
		pb = (ProgressBar) findViewById(R.id.progressGPSgoogle);
		 bundle = getIntent().getExtras();
			getActionBar().setIcon(R.drawable.menu);
			getActionBar().setHomeButtonEnabled(true); 
			idCliente=bundle.getString("idCliente");
		try {
			// Loading map
			initilizeMap();
			initializeUiSettings();
			initializeMapLocationSettings();
			//initializeMapTraffic();
			initializeMapType();
			
			if(idCliente.length()>0){
				this.setTitle("Cliente: "+idCliente);
				latlon=bundle.getString("latlog");
				if(latlon.length()>0){
					longitute=Double.parseDouble(latlon.split(";")[0]);
				   latitude=Double.parseDouble(latlon.split(";")[1]);
				}
				
				
				
				 BitmapDescriptor defaultMarker=BitmapDescriptorFactory.fromResource(R.raw.gota_rojo);
				 LatLng point = new LatLng(longitute,latitude);
			    String s[]= getDatalleCliente(idCliente).split(",");
				 
				   Marker mapMarker = googleMap.addMarker(new MarkerOptions()
				    .position(point)					 								    
				    .title(s[0])
				    .snippet(s[1])
				    .icon(defaultMarker));
				   
				   googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));

				    // Zoom in, animating the camera.
				    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null); 
				   getZonasClientes(0);
			
			}else{
				vendId=bundle.getString("vendId");
				String coor=bundle.getString("coordenada");
				
				String[] url={"http://gps.axumvm.com.ar/LocationService.asmx",coor+""};
				new HttpAsyncTask().execute(url);
		
			}
			
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
		String coor=bundle.getString("coordenada");
		String[] url={"http://gps.axumvm.com.ar/LocationService.asmx",coor+""};
		new HttpAsyncTask().execute(url);
		
		return true;
	}

if (id == android.R.id.home) {
	volver();
      return true;
}	
	
	return super.onOptionsItemSelected(item);
}


	private void initilizeMap() {

		googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment)).getMap();

		// check if map is created successfully or not
		if (googleMap == null) {
			Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
		}

		(findViewById(R.id.mapFragment)).getViewTreeObserver().addOnGlobalLayoutListener(
				new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

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

public void getZonasClientes(int i){
		String zona="";
	if(i==0){
	  zona= getZona();
	   zona= "where zona ='"+zona+"'";
	}
	db.abrirBasedatos();
		ArrayList<String> list_zona = new ArrayList<String>();
		list_zona.add("");
		 Cursor localCursor = db.baseDatos.rawQuery("select codigo,nombre,dato11,direccion from clientes "+zona, null);
	 while (true)
			    {
			    
			      if (!localCursor.moveToNext())
			      {
			        localCursor.close();
			        db.baseDatos.close();
			        break;
			      }
			   
			      String l=localCursor.getString(2);
					if(l.length()>0){
						
							longitute=Double.parseDouble(l.split(";")[0]);
							latitude=Double.parseDouble(l.split(";")[1]);
							 BitmapDescriptor defaultMarker=null;
						if(!localCursor.getString(0).equals(bundle.getString("idCliente"))){
								defaultMarker=BitmapDescriptorFactory.fromResource(R.raw.gota_azul);
							 LatLng point = new LatLng(longitute,latitude);
								
							   Marker mapMarker = googleMap.addMarker(new MarkerOptions()
							    .position(point)					 								    
							    .title(localCursor.getString(1))
							    .snippet(localCursor.getString(3))
							    .icon(defaultMarker));	
						}
					
					}
			     
			    }
	
	 
 }
public String getZona()
{	
	 String ct="";
	 db.abrirBasedatos();
	// Log.d("base error","select codigo,descripcion from articulos where codigo="+id_articulo.trim()+" " );
	Cursor localCursor = db.baseDatos.rawQuery("select zona  from clientes  where codigo="+idCliente+" ", null);

	if(localCursor.getCount()>0){
	localCursor.moveToFirst();
	ct=localCursor.getString(0);
	}
	localCursor.close();
	db.baseDatos.close();
	return ct;
}




public void getZonasClientes1(int i){
		String zona="";
		if(i==0){
		  zona= getZona();
		   zona= "where zona ='"+zona+"'";
		}
		db.abrirBasedatos();
			ArrayList<String> list_zona = new ArrayList<String>();
			list_zona.add("");
			 Cursor localCursor = db.baseDatos.rawQuery("select codigo,nombre,coordenada from clientesposicion where vend ='"+vendId+"' "+zona, null);
		 while (true)
				    {
				    
				      if (!localCursor.moveToNext())
				      {
				        localCursor.close();
				        db.baseDatos.close();
				        break;
				      }
				   ///   MyMarker marker=null;
				      String l=localCursor.getString(2);
						if(l.length()>0){
							
							//l=dir.split(";")[1];
							longitute=Double.parseDouble(l.split(";")[0]);
							latitude=Double.parseDouble(l.split(";")[1]);
							 BitmapDescriptor defaultMarker=null;
								if(!localCursor.getString(0).equals(bundle.getString("idCliente")))
										defaultMarker=BitmapDescriptorFactory.fromResource(R.raw.gota_azul);
									else
										defaultMarker=BitmapDescriptorFactory.fromResource(R.raw.gota_rojo);
									
									 LatLng point = new LatLng(longitute,latitude);
										
									   Marker mapMarker = googleMap.addMarker(new MarkerOptions()
									    .position(point)					 								    
									    .title(localCursor.getString(0))
									    .snippet(localCursor.getString(1))
									    .icon(defaultMarker));	
						
						}
						
				      
				    }
		
		 
	 }


public  String getPOST(String[] url){
    InputStream inputStream = null;
    //String result = "";
    StringBuffer result = new StringBuffer(); 
    StringBuffer result1 = new StringBuffer(); 
    int responseE = -1;
    
    Log.w("XY", url[1]);
    int t=1;
    if(url[1].equals("0"))
   	 t++;
  
    HttpURLConnection connection = null;  
	    try {
	    Log.w("DATO CANT", t+"");
	    for(int u=0; u<t;u++){
	    	 byte[] arrayOfByte=null;
	    	 Log.w("DATO", getBody()+"");
	    		if(u==0)
	    			arrayOfByte = getBody().getBytes();
	    		if(u==1)
	    			arrayOfByte = getBody1().getBytes();
	    		
		         URL localURL = new URL(url[0]);
		         connection = (HttpURLConnection)localURL.openConnection();
		         connection.setDoInput(true);
		         connection.setDoOutput(true);
		         connection.setRequestMethod("POST");
		         connection.setRequestProperty("Content-Type","text/xml");
		         new DataOutputStream(connection.getOutputStream()).write(arrayOfByte);
		         responseE = connection.getResponseCode();                 
		         if (responseE == HttpURLConnection.HTTP_OK) {
		        	 InputStream is = connection.getInputStream();
		        	 BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		        	 String line;
		        	 while((line = rd.readLine()) != null) {
			        	if(u==0)
			        		 result.append(line);
			        	if(u==1)
			        		result1.append(line);
		        	}
		        	 if(u==1)
		        	 postXML1(result1.toString());
		        	 rd.close();
		      
		         	}
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
private void postXML1(String xml) {
	  ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();
	 String s="";
 XMLParser parser = new XMLParser();
 Document doc = parser.getDomElement(xml);
 NodeList definitionElements = doc.getElementsByTagName("allClientsPositionByVendedorResult");
 String strDefinition = "";
 Log.w("Datos", definitionElements.getLength()+"");
 if(definitionElements.getLength()>0){
	 //-------------------------------------codigo text,nombre text ,canal text,coordenada text,
	  String	sql = "INSERT OR IGNORE INTO clientesposicion (codigo,nombre,canal,coordenada,vend,dato2) VALUES (?,?,?,?,?,?)";
	  db.abrirBasedatos();
		   SQLiteStatement statement = db.baseDatos.compileStatement(sql);
		   db.baseDatos.beginTransaction();
		     //-------------------------
		     for (int i = 0; i < definitionElements.getLength(); i++) {
		       Node itemNode = definitionElements.item(i);
		       if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
		         Element definitionElement = (Element) itemNode;
		         NodeList wordDefinitionElements = (definitionElement).getElementsByTagName("string");
		         strDefinition = "";float max1=wordDefinitionElements.getLength();
		        Log.w("Datos2",wordDefinitionElements.getLength()+"");
		         for (int j = 0; j < wordDefinitionElements.getLength(); j++) {
		           Element wordDefinitionElement = (Element) wordDefinitionElements .item(j);
		           NodeList textNodes = ((Node) wordDefinitionElement).getChildNodes();
		          updateClientes2(((Node) textNodes.item(0)).getNodeValue(),j,statement);
		        }
		     
		       }
		     }
		     
		     db.baseDatos.setTransactionSuccessful();	
		     db.baseDatos.endTransaction();
		     db.baseDatos.close();
 }    
}

private void  updateClientes2(String clientes,int indice, SQLiteStatement statement) {
	// 1081,-27.3672184618666,-55.9307738422627,LIN YOU CHANG,Autoservicio
	String cli[]=clientes.split(",");
	
	
	//if(cli.length)
	  statement.clearBindings();
	   if( cli[0].indexOf(".")>0)cli[0]=cli[0].substring(0,cli[0].indexOf("."));
	  statement.bindString(1,cli[0]);
	  statement.bindString(2,cli[3]);
	  if(cli.length>4)
		  statement.bindString(3,cli[4]);
	  else
	  statement.bindString(3,"");
	  
	  statement.bindString(4, cli[1]+";"+ cli[2]);
	  statement.bindString(5,vendId);
	  statement.bindString(6, getFecha());
	 // Log.w("SQL",cli[0]+","+cli[3]+","+cli[4]+","+cli[1]+";"+cli[2]+","+vendId+","+getFecha()); 
    statement.execute();
              
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
	   	
	   String r=getPOST(urls);
		//getZonasClientes1(1);
        return r;
    }
  
 protected void onPreExecute() {
    	pb.setVisibility(ProgressBar.VISIBLE);
    }
 

    protected void onPostExecute(String result) {
   	 pb.setVisibility(ProgressBar.INVISIBLE);
   ///String respuesta[]=result.split("@");
   	 String s1=postXML(result);
   	 if(s1.length()>0){
   		
		    	 ///MyMarker marker=null;
		   	   	String s[]=s1.split(",");
		   	   	FormMapaGoogle.this.setTitle("Vend: "+vendId+"  ["+s[3]+"]");
		    	longitute=Double.parseDouble(s[0]);
				latitude=Double.parseDouble(s[1]);
				 LatLng point = new LatLng(longitute,latitude);
				 BitmapDescriptor defaultMarker=BitmapDescriptorFactory.fromResource(R.raw.vend);
				
				// String sd[]= getDatalleCliente(idCliente).split(",");
				   Marker mapMarker = googleMap.addMarker(new MarkerOptions()
				    .position(point)					 								    
				    .title("Vendedor : "+vendId)
				    .snippet("["+s[3]+"]")
				    .icon(defaultMarker));
				   googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));

				    // Zoom in, animating the camera.
				    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null); 
				
		    	if(vendId.equals(getConfig("vend")))
				getZonasClientes(1);
				
			
					///Toast.makeText( FormMapa.this,"id"+mapView.getLayerManager().getLayers().indexOf(marker), Toast.LENGTH_SHORT).show();
			 
   	 }else{
   		 alertaSalir();
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
	 
	 if(idCliente.length()>0){
	         Intent mainIntent = null;
	        mainIntent = new Intent().setClass(FormMapaGoogle.this, Clientes.class);
	        mainIntent.putExtra("idCliente",bundle.getString("idCliente"));
	        startActivity(mainIntent);
	        finish();
	 }else{
		 
		    Intent mainIntent = null;
	        mainIntent = new Intent().setClass(FormMapaGoogle.this, MenuPpal.class);
	        mainIntent.putExtra("m","FormUbicacion");
	        startActivity(mainIntent);
	        finish(); 
		 
	 }
}

public String  getDatalleCliente(String id_cliente){
	  StringBuffer stringbuffer = new StringBuffer();
	try
   {
		
		//this.baseDatos.close();
		db.abrirBasedatos();
	  
	
	 Cursor localCursor = db.baseDatos.rawQuery("select clientes.*, ramos.descripcion from clientes, ramos where clientes.ramo= ramos.codigo and clientes.codigo ='"+id_cliente.trim()+"' group by clientes.codigo", null);
//   Log.w("SQL","select clientes.*, ramos.descripcion from clientes, ramos where clientes.ramo= ramos.codigo and clientes.codigo ="+id_cliente );
			    while (true)
			    {
			    	
			      if (!localCursor.moveToNext())
			      {
			        localCursor.close();
			        db.baseDatos.close();
			        break;
			      }
			      //if(localCursor.getString(0)!=null)
			    
			      stringbuffer.append(localCursor.getString(1)+","+localCursor.getString(2));
			   }
	 
			  
    }
    catch (Exception exception) {
    	Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show();
    	
    }
    
    return stringbuffer.toString();
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



}
