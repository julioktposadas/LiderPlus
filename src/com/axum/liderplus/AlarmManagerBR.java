package com.axum.liderplus;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import com.axum.config.Db;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;
 
public class AlarmManagerBR extends BroadcastReceiver{
	TelephonyManager tManager;
    @SuppressLint("SimpleDateFormat")
   
    Context context;
    Db db;
	public static final int notifyID = 9001;
    public void onReceive(Context arg0, Intent arg1) {
    	context =arg0;
        StringBuilder sb=new StringBuilder();
        SimpleDateFormat format=new SimpleDateFormat("hh:mm:ss a");
        sb.append(format.format(new Date()));
        db= new Db(context);
      //Toast.makeText(arg0, sb, Toast.LENGTH_SHORT).show();
      //
        //if(getConfig("hora_inicio").length()==0)
	//	if(getConfig("hora_fin").length()==0)
			
        String[] url={"http://gps.axumvm.com.ar/LocationService.asmx"};
        if (isOnline())
		new HttpAsyncTask().execute(url);
    }
    

    private void sendNotification(String msg) {
		
	       Intent resultIntent = new Intent(context, MenuVendedor.class);
	      
	      
	       resultIntent.putExtra("msg",msg);
	       resultIntent.putExtra("m","MenuPpal");
	       
	       PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0,
	               resultIntent, PendingIntent.FLAG_ONE_SHOT);
	        
	        NotificationCompat.Builder mNotifyBuilder;
	        NotificationManager mNotificationManager;
	        
	        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	        
	        mNotifyBuilder = new NotificationCompat.Builder(context)
	                .setContentTitle("Ax-Lider")
	                //.setSmallIcon(R.drawable.ic_launcher)
	                //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_settings_icon))
	                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ax_lider))
	                .setContentText(msg)
	                .setSmallIcon(R.drawable.ic_stat_name);
	        // Set pending intent
	        mNotifyBuilder.setContentIntent(resultPendingIntent);
	        
	        // Set Vibrate, Sound and Light	        
	        int defaults = 0;
	        defaults = defaults | Notification.DEFAULT_LIGHTS;
	        defaults = defaults | Notification.DEFAULT_VIBRATE;
	        defaults = defaults | Notification.DEFAULT_SOUND;
	        
	        mNotifyBuilder.setDefaults(defaults);
	        // Set the content for Notification 
	       // mNotifyBuilder.setContentText("Axum VM");
	        // Set autocancel
	        mNotifyBuilder.setAutoCancel(true);
	        // Post a notification
	        mNotificationManager.notify(notifyID, mNotifyBuilder.build());
	}
	
    
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
  	 String  httpString;
 	   protected String doInBackground(String... urls) {
 		 
 		   httpString=getPOST(urls[0]);
 		   
 		   return httpString;
 	     }
 	   
 	  protected void onPreExecute() {
 	     	//pb.setVisibility(ProgressBar.VISIBLE);
 	     }
 	  

 	     protected void onPostExecute(String result) {
 	    
 	    	 //Log.w("JSON",result);
 	    	 postXML1(result);
 	    	
 	    }
 	 }
    
    private void postXML1(String xml) {
    	
    	
        XMLParser parser = new XMLParser();
        Document doc = parser.getDomElement(xml);
        NodeList definitionElements = doc.getElementsByTagName("lastPositionsResult");
        String strDefinition = "";
        int num,hor,min=-1,seg;
        
        if(definitionElements.getLength()>0){
        	int tiempo_max=Integer.parseInt(getConfig("tiempo_max"));
        	
        	  ArrayList<String> vendedor=  listarVendedor();
        	  
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
    		           String dato[]=((Node) textNodes.item(0)).getNodeValue().split(",");
    		           min=-1;
    		           if(!dato[4].equals("nnn")) {
    				        num=Integer.parseInt(dato[4]);
    				        hor=num/3600;
    				        min=(num-(3600*hor))/60;
    				        seg=num-((hor*3600)+(min*60));
    				       
    			         }
    		          // Log.w("DATOS1",min+" / "+tiempo_max);
    		           if(min>tiempo_max || min==-1) {
    		        	//   Log.w("DATOS",dato[2]+" / "+vendedor.indexOf(dato[2]));
    		        	   if(vendedor.indexOf(dato[2])!=-1) {
    		        		   
    		        	     sendNotification("El vendedor "+dato[2]+" dejo de trasmitir");
    		        	     j = wordDefinitionElements.getLength();
    		        	   }
    		           }
    		           
    		          
    		         }
    		       
    		       }
    		     }
    	
    		
        }   
        
        
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
  
    
    public boolean isOnline() {
    	ConnectivityManager connectivityManager;
        NetworkInfo wifiInfo, mobileInfo;
        boolean connected = false;
        try {
            connectivityManager = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);

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
    public String getConfig(String s)
	{
	     return context.getSharedPreferences("config", 0).getString(s,"");
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
}
