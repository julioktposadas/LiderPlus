package com.axum.liderplus;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;





import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.axum.config.Db;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ActualizarBase extends AsyncTask<String,String,String> {

    private Context mContext;
    Db db;	
	 long time_start, time_end;

	 Activity mActivity;
	int cli_update=0;
	 private ProgressBar progressBar;
	 private TextView  l_estado,t_vendedor,t_supervisor,t_clave;
	 private int max = 0;
	 private String error = "";
	 int i_zona=0,i_cliente = 0,i_ramos=0,i_impuesto=0,i_boni=0;
	 int c_cambio=0,c_rubro=0,c_linea=0, idx_ramo=0;
	   
    ArrayList<String> list_rubro = new ArrayList<String>();
    ArrayList<String> list_zona = new ArrayList<String>();
    ArrayList<String> list_rubroCli = new ArrayList<String>();
   	ArrayList<String> list_linea = new ArrayList<String>();
    ArrayList<String> list_id = new ArrayList<String>();
	ArrayList<String> list_nombre = new ArrayList<String>();
   	Button b_actualizar;
   	int tabla[] ={0,0,0,0,0,0};
   	String grupo="Preguntas";
   	String aux_grupo="";
    public ActualizarBase(Context context, Activity activity){

        this.mContext = context;
        this.mActivity= activity;
        db= new Db(context);
		progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
		l_estado = (TextView) activity.findViewById(R.id.l_estado);
		t_vendedor= (TextView) activity.findViewById(R.id.t_vendedor);
		t_supervisor= (TextView) activity.findViewById(R.id.t_supervisor);
		t_clave= (TextView) activity.findViewById(R.id.t_clave);
		b_actualizar = (Button) activity.findViewById(R.id.b_actualizar);
		
		
		// progressBar.setProgress(progressStatus);
 }

    protected void onPreExecute() {
    	
    }
    protected void onProgressUpdate(String...progress){
    	
    	 if(progress[0].length()>0){
    		 if(progress[1].length()>0)	 
    			 l_estado.setText(progress[1]);
    		 	int in=Integer.parseInt(progress[0]);
    		 	progressBar.setProgress(in);
          
    	 }
       /// l_estado.setText("Recibiendo datos..");

    }
    	
   protected String doInBackground(String... urls) {
       procesarHttp(urls);
   	     progressBar.setProgress(0);
        getPOST("http://gps.axumvm.com.ar/LocationService.asmx");
      //  doInBackground("http://gps.axumvm.com.ar/LocationService.asmx");
		return "";
       
    }

    

	protected void onPostExecute(String content) {
		progressBar.setProgress(0);
		
        l_estado.setText("");
      if (error.length()>0) {
    	  b_actualizar.setEnabled(true);
    	  
    	  //Log.w("ERROR:",error);
        showLoginError("ERROR, "+error);
          
        } else {
        	
        	alertaMensaje();
     }
     
    }
	
	
	public void procesarHttp(String[] urls)
	{
		
		 InputStream stream = null;
			try {
				  time_start = System.currentTimeMillis();
				int i=0,x=0;cli_update=0;
				limpiarDato();db.abrirBasedatos();
				list_rubroCli.clear();
				 SQLiteStatement statement1= null;
			
		 for(int u=0; u< urls.length; u++){	
			 
			 Log.w("URL:","Conectando "+urls[u].split("_")[1]+"...");
			 publishProgress(0+"","Conectando "+urls[u].split("_")[1]+"...");
			 String URL=""; error="";
			
			 list_rubro.clear();list_linea.clear();
			 if(u<3)
			 URL ="http://www.axum.com.ar/"+getConfig("empresa")+"/ControllerJME.aspx?Ver=11.76&IMEI=&cmd="+urls[u]+"&Vend="+t_vendedor.getText().toString()+"&clave="+t_clave.getText().toString();
			 if(u==3 || u==4)
			 URL ="http://www.axum.com.ar/"+getConfig("empresa")+"/ControllerJME.aspx?sup=&cmd="+urls[u]+"&Vend="+t_supervisor.getText().toString()+"&clave=";
			 if(u==5)
				 URL ="http://www.axum.com.ar/"+getConfig("empresa")+"/ControllerJME.aspx?cmd=B_Vendedores&Sup="+t_supervisor.getText().toString();
			 
			
			   URL url = new URL(URL);
				URLConnection connection = url.openConnection();
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				httpConnection.setRequestMethod("GET");
				httpConnection.setDoInput(true);
				httpConnection.setConnectTimeout(20000);
				httpConnection.setReadTimeout(20000);
				httpConnection.connect();

				if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					stream = httpConnection.getInputStream();
					BufferedReader rd= new BufferedReader(new InputStreamReader(stream));
					String line;i=0;x=0;
					max=httpConnection.getContentLength(); 
				//------------------------------------------
					String sql ="";
					 if(u==0){
						 sql = "INSERT INTO clientes (codigo,nombre ,direccion ,telefono,lista,zona,ramo,dato8,dato10,dato11,flag) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
						 statement1 = db.baseDatos.compileStatement("INSERT  INTO ramos (codigo,descripcion,flag) VALUES (?,?,?)");
					 }
					 if(u==1)
						 sql = "INSERT OR IGNORE INTO articulos (codigo,descripcion,lista,linea,rubro,capacidad,pack,flag) VALUES (?,?,?,?,?,?,?,?)";
				
					 if(u==2)
					    sql = "INSERT INTO encuesta (id_encuesta,nombre,grupo,id_pregunta,pregunta,tipodato,opciones,fecahaini,fechafin,habilita) VALUES (?,?,?,?,?,?,?,?,?,?)";
					 if(u==3)
						 sql = "UPDATE clientes SET dato9= ? WHERE codigo like ?";
					 if(u==4)
					   sql = "INSERT INTO encuesta (id_encuesta,nombre,grupo,id_pregunta,pregunta,tipodato,opciones,fecahaini,fechafin,habilita) VALUES (?,?,?,?,?,?,?,?,?,?)";
					  if(u==5)
						   sql = "INSERT INTO vendedores (codigo,nombre,flag) VALUES (?,?,?)";
				
					  Log.w("INDICE:",u+"");
					 
					 	SQLiteStatement statement = db.baseDatos.compileStatement(sql);
					     db.baseDatos.beginTransaction();
					    
					///-----------------------------------------
			               while ((line=rd.readLine())!=null)
			               {
			            	   i+=line.getBytes().length;
			            	   
			            	   publishProgress((int) ((i / (float) max) * 102)+"","Recibiendo datos..");
			            	  if(u==0)
			            	   cargarClientes1(line,x,statement,statement1);
			            	  if(u==1)
			            	   cargarArticulos1(line,x,statement);
			            	  if(u==2)
			            		 cargarEncuenta1(line,x,statement);
			            	  if(u==3)
			            		updateClientes1(line,x,statement);
			            	  if(u==4 && cli_update >0)
			            		  cargarEncuenta1(line,x,statement);
			            	  if(u==5)
			            		  cargarVendedor(line,x,statement);
			            	  // gurdarDatos(x,line,u);
			            	x++;
			               }
			                if(u==0 && x==0) {
			            	   error="No fue posible actualizar los datos \nEs posible que requiera clave";
			            		db.baseDatos.setTransactionSuccessful();	
			    	 			db.baseDatos.endTransaction();
			            		this.db.baseDatos.close();
			            		break;  
			               }
			              
			               tabla[u]=x;
			              
			  }else
		           {
					//error="SIN SEÑAL. INTENTE EN OTRO MOMENTO";
		               Log.e("Error","No hay clientes respuesta!");
		           }
				
				db.baseDatos.setTransactionSuccessful();	
	 			db.baseDatos.endTransaction();
			 }
		 
 			this.db.baseDatos.close();
		 		
		 			
			} catch (Exception ex) {
				error=ex.toString();
				Log.e("Error",ex.toString());
				//error="SIN SEÑAL. INTENTE EN OTRO MOMENTO";
				//ex.printStackTrace();
			}
		
			
}

 public String getVend()
		{	
			db.abrirBasedatos();
			Cursor localCursor = this.db.baseDatos.rawQuery("select nombre from vendedores where codigo ="+t_vendedor.getText().toString(), null);
			localCursor.moveToFirst();
			String count= localCursor.getString(0);
			localCursor.close();
			this.db.baseDatos.close();
			return count;
		}	
	
	public void showLoginError(String result)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.cancel();
			}
		});
		
		builder.setMessage(result);
		builder.setTitle("Alerta");
		AlertDialog alert = builder.create();
		alert.setCancelable(false);
		alert.show();
	}
	
	 public void limpiarDato()
	  {
		 db.abrirBasedatos();
	    this.db.baseDatos.execSQL("DELETE FROM clientes ");
	    this.db.baseDatos.execSQL("DELETE FROM encuesta ");
	    this.db.baseDatos.execSQL("DROP TABLE IF EXISTS  ramos ");
	    this.db.baseDatos.execSQL("DROP TABLE IF EXISTS  vendedores ");
	    this.db.baseDatos.execSQL("DELETE FROM  clientesposicion");
	    
	    
	    
	    
	  //  this.db.baseDatos.execSQL("DELETE FROM respuestas");
	  
	    this.db.baseDatos.close();
	  }


	 public void guardarConfig(String paramString1, String paramString2)
	 {
	   SharedPreferences.Editor localEditor = mContext.getSharedPreferences("config", 0).edit();
	   localEditor.putString(paramString1, paramString2);
	   localEditor.commit();
	 }
 public void alertaMensaje() {
	String sf= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
	 guardarConfig("super",t_supervisor.getText().toString());
	 guardarConfig("vend",t_vendedor.getText().toString());
	 guardarConfig("estandar",cli_update+"");
	 guardarConfig("filtro","");
     guardarConfig("filtro_id","0");
     guardarConfig("lblUltAct",sf);
 	time_end = System.currentTimeMillis();
	 String s="";
	 if(tabla[0]>0){s+="T. Clientes actualizada\n";}else s+="no hay clientes\n";
	 if(tabla[1]>0)s+="T. Artiulos actualizada\n";else s+="no hay articulos\n";
	 if(tabla[2]>0)s+="T. Encuesta actualizada\n";else s+="no hay encuestas\n";
	 if(tabla[4]>0 )s+="T. Estandar actualizada\n";else s+="no hay estandar\n";
	 s+="Tiempo:"+ ( time_end - time_start ) +" milisegundos";
	 
		    new AlertDialog.Builder(mContext)
		    		.setTitle("Resultados")
		            .setMessage(s)
		            .setCancelable(false)
		            .setIcon( mContext.getResources().getDrawable(android.R.drawable.ic_dialog_alert))
		            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		                   
		            	   Intent intent = new Intent();
		            	   intent.putExtra("m","MenuPpal");
		                  intent.setClass(mContext, MenuPpal1.class);
		                 mContext.startActivity(intent);
		                 mActivity.finish();
		               }
		           })
		           
		           .show();
		}
 private void cargarClientes1(String clientes,int indice, SQLiteStatement statement, SQLiteStatement statement1) {
	 
	if(indice==0){
	 String s= clientes.replace('|',';');
	String s1[]=s.split(";");
	i_zona=Integer.parseInt(s1[0].split(",")[1]);
	i_cliente=Integer.parseInt(s1[1].split(",")[1]);
	i_ramos=Integer.parseInt(s1[2].split(",")[1]);
	i_impuesto=Integer.parseInt(s1[3].split(",")[1]);
	}
	
 if(indice!=0 && indice<=i_zona){
	 
	 String lineas[]=clientes.split(",");
	 
	   list_zona.add(lineas[0]);
	
	 }
	
   if(indice>i_zona && indice<(1+i_zona+i_cliente)){
	   
	 
	  // Log.w("CLIENTE",clientes);
	   	String cli[]=clientes.split(",");
		
				   statement.clearBindings();
				   if( cli[0].indexOf(".")>0)cli[0]=cli[0].substring(0,cli[0].indexOf("."));
				 	//Log.w("DATO:", cli[0].substring(0,cli[0].indexOf(".")));
	               statement.bindString(1, cli[0]);
	               statement.bindString(2, cli[1]);
	               statement.bindString(3, cli[2]);
	               statement.bindString(4, cli[3]);
	               statement.bindString(5, cli[4]);
	       
	               statement.bindString(6,list_zona.get(Integer.parseInt(cli[5])-1).toString());
	               statement.bindString(7, cli[6]);
	               statement.bindString(8, cli[13].trim());
	               if(cli.length>=15)
	               statement.bindString(9, cli[14]);else
	            	statement.bindString(9, "");	   
	               statement.bindString(10,"");
	               statement.bindString(11,"0");
	           statement.execute();
	      // 	Log.w("DATO:",clientes );
	 }
   if(indice>(i_zona+i_cliente) && indice<(1+i_zona+i_cliente+i_ramos)){
	   
	 //Log.w("DATO:",idx_ramo+" > "+clientes );
	   
	 	String cli[]=clientes.split(",");
	 	 statement1.clearBindings();
         statement1.bindString(1, idx_ramo+"");
         statement1.bindString(2, cli[0]);
         statement1.bindString(3, "0");
	 	
	     Log.w("DATO:",idx_ramo+" > "+cli[0] );
         idx_ramo++;
         statement1.execute();
	 }
     
 	} 
 
 private void cargarArticulos1(String articulos,int indice, SQLiteStatement statement) {

	 if(indice==0){
		
		String s= articulos.replace('|',';');
		String s1[]=s.split(";");
		c_cambio=Integer.parseInt(s1[0].split(",")[1]);
	    c_rubro=Integer.parseInt(s1[1].split(",")[1]);
	    c_linea=Integer.parseInt(s1[2].split(",")[1]);
	 }
	 	if(indice>(c_cambio)&& indice< (1+c_cambio+c_rubro) ){
    		 String rubros[]=articulos.split(",");
				list_rubro.add(rubros[0]);
				//Log.w("RUBRO:",rubros[0]);
			}
			if(indice>(c_cambio+c_rubro)&& indice< (1+c_cambio+c_rubro+c_linea) ){
				String lineas[]=articulos.split(",");
				list_linea.add(lineas[0]);
				//Log.w("Linea:",lineas[0]);
			}
			if(indice>(1+c_cambio+c_rubro+c_linea)){
				//Log.w("Art:",articulos);
				String art[]=articulos.split(";");
				if(art.length>6 && art[0].length()>0 && isNumeric(art[0])){
					
					     statement.clearBindings();
			               statement.bindString(1, art[0]);
			               statement.bindString(2, art[1]);
			               statement.bindString(3, art[2]);
			               statement.bindString(4, list_linea.get(Integer.parseInt(art[3])-1));
			               statement.bindString(5, list_rubro.get(Integer.parseInt(art[4])-1));
			               statement.bindString(6, art[5]);
			               statement.bindString(7, art[6]);
			               statement.bindString(8,"0");
			             statement.execute();
			    }
				
			}
   
} 
 
 
 private void cargarEncuenta1(String encuesta,int indice, SQLiteStatement statement) {
	
	 if (indice==0){
		 list_id.clear();list_nombre.clear();
		 String s=(encuesta.split("=")[1]).replace('|',';');
		 String HEADER[]=s.split(";");
		// Log.w("Item R:",s+"*"+HEADER.length);
		 	for (int i=0;i<HEADER.length;i++  ){
		 		String d[]=HEADER[i].split(",");
		 		list_id.add(d[0]);
		 		list_nombre.add(d[1]);
		 	
		 	}
		 	
		 
	  }
		 
	  if(indice>0){
		
			
			String encu[]=encuesta.split(",");
			
		  
		    	     String s= encu[0].replace('|',';');
		   			  String s1[]=s.split(";");
		   		
		   		
					   statement.clearBindings();
					   statement.bindString(1,s1[0].trim());
		           
		               statement.bindString(2,list_nombre.get(list_id.indexOf(s1[0])) );
		               statement.bindString(3,grupo);
		               statement.bindString(4,s1[1]);
		               //-----------------------------
		               statement.bindString(5, encu[1]);
		               statement.bindString(6, encu[2]);
		               statement.bindString(7, encu[3]);
		               //------------------------------
		               statement.bindString(8, encu[4]);
		               statement.bindString(9, encu[5]);
		               if(encu.length>6){
		            	  String item[]=  encu[6].split(">");
		            	//  Log.w("Item22:",item[1].split("=")[0]);
		               statement.bindString(10, item[1].split("=")[0]);
		               }
		               else
			           statement.bindString(10, "0");
		              statement.execute();
		          
		    
		//  Log.w("Item22:",encuesta);
		 }
	     
	} 
 private void  updateClientes1(String clientes,int indice, SQLiteStatement statement) {
	int ct=0; 
	if(indice==0){
	 String s= clientes.replace('|',';');
	String s1[]=s.split(";");
	i_zona=Integer.parseInt(s1[0].split(",")[1]);
	i_cliente=Integer.parseInt(s1[1].split(",")[1]);
	i_ramos=Integer.parseInt(s1[2].split(",")[1]);
	//i_impuesto=Integer.parseInt(s1[3].split(",")[1]);
	}
   if(indice>i_zona && indice<(1+i_zona+i_cliente)){
	   
	 //  Log.w("Cliente:",clientes);
	   	String cli[]=clientes.split(",");
				   statement.clearBindings();
				   if( cli[0].indexOf(".")>0)cli[0]=cli[0].substring(0,cli[0].indexOf("."));
				   statement.bindString(1, cli[13].trim());
	               statement.bindString(2,  cli[0]);
	               statement.execute();
	               cli_update=1;
	 }

  //	Log.w("FIN :",clientes );

 	} 
 private void  updateClientes2(String clientes,int indice, SQLiteStatement statement) {

		String cli[]=clientes.split(",");
		
		statement.clearBindings();
		statement.bindString(1, cli[1]+";"+ cli[2]);
		 if( cli[0].indexOf(".")>0)cli[0]=cli[0].substring(0,cli[0].indexOf("."));
	     statement.bindString(2,cli[0]);
	    statement.execute();
	              
	 }


 	 
 private void cargarEstandar(String encuesta,int indice, SQLiteStatement statement) {
		
	   if(indice>0){
		//	Log.w("Item:",encuesta);
		   	String encu[]=encuesta.split(",");
		   				String s= encu[0].replace('|',';');
		   				String s1[]=s.split(";");
					   statement.clearBindings();
		               statement.bindString(1, s1[0].trim());
		               statement.bindString(2, s1[1]);
		               //-----------------------------
		               String s2[]=encu[1].split("-");
		               statement.bindString(3, s2[0]);
		               statement.bindString(4, s2[1]);
		               statement.bindString(5, s2[2]);
		               //------------------------------
		               statement.bindString(6, encu[2]);
		               statement.bindString(7, encu[3]);
		               statement.bindString(8, encu[4]);
		               statement.bindString(9,encu[5]);
		               statement.execute();
		      // 	Log.w("DATO:",clientes );
		 }
	     
	 	} 
 
 
 
 
 private void cargarVendedor(String encuesta,int indice, SQLiteStatement statement) {
		
		        //Log.w("VENDEDOR:",encuesta);
		   
		            	String encu[]=encuesta.split(",");
		            	 if(encu.length>1){
			   				statement.clearBindings();
			               statement.bindString(1, encu[1]);
			               if(encu.length>2)
			               statement.bindString(2, encu[2]);
			               else
			            	 statement.bindString(2,"");
			               
			               statement.bindString(3, "0");
			               statement.execute();
		            	 }
		               //------------------------------
		         
		 }
	     
	
 

 
 
 public String getFechaFormat(String s){
	 
	 return  s.substring(0, 4)+"-"+s.substring(0, 4)+"-"+s.substring(4, 6)+"-"+s.substring(6, 8);
 }
 
 private static boolean isNumeric(String cadena){
		try {
			Integer.parseInt(cadena);
			return true;
		} catch (NumberFormatException nfe){
			return false;
		}
	}
 public String getConfig(String p1){
		SharedPreferences prefs = this.mContext.getSharedPreferences("config",this.mContext.MODE_PRIVATE);
			 return  prefs.getString(p1, "");
	}

 
 public  String getPOST(String url){
     InputStream inputStream = null;
     //String result = "";
     StringBuffer result = new StringBuffer(); 
     int responseE = -1;

     HttpURLConnection connection = null;  
	    try {
	    	
	    	 byte[] arrayOfByte = getBody1().getBytes();
	         URL localURL = new URL(url);
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
	        	// Log.w("resultado",line);
	        		 result.append(line);
	       
	        	 	}
	        	 rd.close();
	        	 postXML(result.toString());
	         	}
    	    

     } catch (Exception e) {
         Log.d("InputStream", e.getLocalizedMessage());
     }

     // 11. return result
     return result.toString();
 }

 private void postXML(String xml) {
	 //Log.w("XML",xml);
	  ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();
	 String s="";
     XMLParser parser = new XMLParser();
     Document doc = parser.getDomElement(xml);
     NodeList definitionElements = doc.getElementsByTagName("allClientsPositionByVendedorResult");
     String strDefinition = "";
   //  Log.w("Datos", definitionElements.getLength()+"");
     if(definitionElements.getLength()>0){
    	 //-------------------------------------
    	  String	sql = "UPDATE clientes SET dato11= ? WHERE codigo like ?";
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
		        // Log.w("Datos2",wordDefinitionElements.getLength()+"");
		         for (int j = 0; j < wordDefinitionElements.getLength(); j++) {
		           Element wordDefinitionElement = (Element) wordDefinitionElements .item(j);
		           NodeList textNodes = ((Node) wordDefinitionElement).getChildNodes();
		          publishProgress((int) ((j / (float) max1) * 102)+"","Recibiendo Coordenadas..");
		    	  updateClientes2(((Node) textNodes.item(0)).getNodeValue(),j,statement);
		          // Log.d("data00",j+" "+ ((Node) textNodes.item(0)).getNodeValue());
		           
		           //strDefinition += ((Node) textNodes.item(0)).getNodeValue() + ",";
		         }
		        /* Toast.makeText(getBaseContext(), strDefinition,
		             Toast.LENGTH_SHORT).show();
		             */
		       }
		     }
		     
		 	db.baseDatos.setTransactionSuccessful();	
 			db.baseDatos.endTransaction();
		    this.db.baseDatos.close();
     }    
}
 
 
 
public String getBody1(){
String POST="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
+"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
+" <soap:Body>"
    +"<allClientsPositionByVendedor xmlns=\"http://tempuri.org/\">"
     +" <userName>"+getConfig("empresa")+"</userName>"
     +" <idVendedor>"+t_vendedor.getText()+"</idVendedor>"
    +"</allClientsPositionByVendedor>"
 +" </soap:Body>"
+"</soap:Envelope>";
	return POST;
}



}