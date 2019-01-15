package com.axum.liderplus;




import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.axum.config.Db;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

@SuppressLint("NewApi")

public class Avance extends Activity {

 private static Context context;
 Bundle bundle ;
 String data1="";
 Db db;
 //Spinner spinTema ;
 int b=0;
 WebView myWebView ;
 
// private ActionBar mActionBar; 
 private LayoutInflater mInflater;
 private View mCustomView;
 private TextView mTitleTextView;
 String vend="",sup="101";
 ProgressBar pb;
 double max=0;
 String[] avanceColor = {"#CAFEB8","#FFF284","#FF7373"};
 String[] myArrayColor1 = {"#0000A0","#FF0000","#800080","#008000","#FFA500","#FFFF00","#808080","#FF00FF","#800000","#D4A017","#9F000F","#9F000F","#0C0400"};
 String[] myArrayColor = {"#99CCFF","#FFCCCC","#99FF99","#F0D58C","#FF9966","#FF9999","#CC6699","#669999","#CCFFFF","#FFFF99","#CCCCCC","#FF4E02","#0C0400"};
 int[][] numColor={ {0,0,255},{0,255,0},{0,128,0}, {255,0,255}, {255,0,0}, {192,192,192},{128,128,128},{128,0,128},{255,255,0},{132,54,37},{255,78,2},{0,0,0}};

	String[] dataItem;
	 String htm="<html ><body bgcolor=\"#FFFFFF\"><table width=\"100%\" border=\"1\" cellpadding=\"0\" cellspacing=\"1\" bordercolor=\"#D3D3D3\" bgcolor=\"#FFFFFF\"  style=\"border-collapse:collapse;\">";
	String pie="</table></body></html>";
	 LinearLayout ll,lg ;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_historico);
		db = new Db(this);
		getActionBar().setIcon(R.drawable.menu);
		getActionBar().setHomeButtonEnabled(true); 
	    bundle = getIntent().getExtras();
	    myWebView = (WebView) this.findViewById(R.id.webResumen);
	    data1=bundle.getString("data");
		pb = (ProgressBar) findViewById(R.id.progressCarga);
		
		 
		 if(bundle.getString("vend").length()==0){
			 dataItem = data1.split(";");
			 this.setTitle("Ventas S:"+getConfig("super"));
			 myWebView.loadData(getHtml(data1), "text/html", "UTF-8");
		 }else{
			String URL ="http://www.axum.com.ar/"+getConfig("empresa")+"/ControllerJME.aspx?cmd=B_Supervisor&Vend="+getConfig("super")+"&vend2="+bundle.getString("vend").trim();
			 // Log.w("url", URL);
			 new HttpAsyncTask().execute(URL);
		 }
		
		 
	
		 //--------------------------------------------------
		 myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.setWebChromeClient(new WebChromeClient());
	
		
}
	

	
/*	

	public void mostrarTorta(String dataG, int tipo){
		
	   if(tipo==2&&spinTema.getSelectedItem().toString().equals("Venta x Agrupacion"))
		 dataG= getGrupo(dataG);
		String[] separated = dataG.split(",");
		if(tipo>2)separated[0].replace(' ','-');
		
	
		if(separated.length>1){
			// myWebView2.loadUrl("file:///android_res/raw/bar.html");
		}
		
}
	
*/  
	

	 
public  String getHtml(String data){
	String[] s;
		StringBuffer sf = new StringBuffer().append("");
		 String[] separated = data.split(";");
	if(separated.length>1){
		sf.append("<html ><body bgcolor=\"#FFFFFF\"> <font size=\"-1\" ><table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\" bgcolor=\"#97e08f\">");
		
		for(int f=1;f<separated.length;f++){
			
		 String[] fila = separated[f].split(",");
			 for(int i=0; i<fila.length;i++){
				 
			 if(i==0){
				      if(fila[0].indexOf("-")>0) s = fila[0].split("-");
				      else{
				    	 s = fila[0].split(" ");
				     }
					sf.append(" <tr bgcolor='#6495ED' height='5' > <td colspan='2'></td> </tr>");  
					sf.append(" <tr bgcolor='#F0F0F0' > <td colspan='2'><center>"+s[0].toUpperCase()+"</center></td> </tr>");
					 sf.append(" <tr bgcolor='#97e08f' > <td width='75%'><font color='#FFFFFF'>"+s[0]+"</font></td> <td><font color='#FFFFFF'>"+s[1]+"</font></td> </tr>"); 	 
				 }
				 else{
					 String[] info = fila[i].split("-"); 

			    	//  Log.w("DATO:",info[0].trim()+"*"+isNumeric(info[0].trim()));
					 if(isNumeric(info[0].trim())) info[0]=getArticulo(info[0]);
					 sf.append(" <tr bgcolor='#FFFFFF' > <td width='85%'>"+info[0].toLowerCase()+"</td> <td>"+info[1].split("/")[0]+"</td> </tr>");  
				 }
			 }
		 }
			 sf.append("</table></font></body></html>");
		}
	
		return ( sf.toString() );	
	}	

public static boolean isNumeric(String string) {
    return string.matches("^[-+]?\\d+(\\.\\d+)?$");
}
public void saveFile(String label,String data ) {
		try {
			
			OutputStreamWriter out=new OutputStreamWriter(openFileOutput("var.js", 0));
			 if(label.length()>0){
		       label= label.substring(0, label.length()-1); 
		       data= data.substring(0, data.length()-1);
		       out.write("var _labels = ["+label+"];");
		       out.write("var _data = ["+data+"];");
		       out.write("var _max = "+max+";");
			 }else{
				 out.write("");
			 }
			out.close();
		//Toast.makeText(this, label, Toast.LENGTH_LONG).show();
		}
		catch (Throwable t) {
			Toast.makeText(this, "Exception: "+t.toString(), Toast.LENGTH_LONG).show();
		
		}
}
	
	public String getGrupo(String sg){
		String s="";
		//Log.w("DATO original:",sg);
		ArrayList listG= new ArrayList();
		ArrayList<Double> listAC = new ArrayList<Double>();
	//	ArrayList listAC= new ArrayList();
		double ac=0;
		s=sg.replace("Articulos", "Grupo");
	//	 Log.w("DATO SG:",sg);
		String[] separated = sg.split(",");
		for(int i=1; i<separated.length;i++){
			
			 String[] s1=separated[i].split("-");
			 if(s1.length==2 && !s1[0].equals(" ")){
			 if(s1[1].indexOf("/")!=-1)s1[1]=s1[1].trim().substring(0,s1[1].trim().indexOf("/"));
			 String g1=getGrupoDB(s1[0]);
			 if(g1.length()>0){
				 int xg=listG.indexOf(g1);
					//Log.w("CANTIDAD:",s1[1]);
				 if(xg==-1){
				 listG.add(g1);
				
				 listAC.add(Double.parseDouble(s1[1]));
				 }else{
					 listAC.add(xg,(listAC.get(xg)+Double.parseDouble(s1[1]))) ;
				 }
				// if(String !=null)
				 }	
			 }
			//Log.w("DATO:",g1);
		}
		 StringBuffer sf = new StringBuffer().append("Agrupacion - Cant,");
		for(int i=0; i<listG.size();i++){
			if(!listG.get(i).equals(""))
				sf.append(listG.get(i)+"-"+listAC.get(i)+",")	;
		}
	//	Log.w("DATO TOTALES:",sf.toString());
		return sf.toString();
	}
	
	public String  getGrupoDB(String s)
	  {
		String grupo="";
		 db.abrirBasedatos();
		//Log.w("DATO:","select agrupacion from articulos WHERE codigo="+s);
		 Cursor localCursor = db.baseDatos.rawQuery("select agrupacion from articulos WHERE codigo="+s, null);
		 if(localCursor.moveToFirst()){
			//localCursor.moveToFirst();
			grupo= localCursor.getString(0);
		 }
			localCursor.close();
			db.baseDatos.close();
			return grupo;
	 }
	//@Override
/*	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.avance, menu);
		return true;
	}
	*/
	public static void writeFile(String fileName, String text) {
	    FileOutputStream fos;
		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			 fos.write(text.getBytes());
			    fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	}

	

	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	     	super.onKeyDown(keyCode, event);
	     	    	if (keyCode == KeyEvent.KEYCODE_BACK) {
	     	    		
	     				Intent intent = new Intent();
	     				intent.putExtra("data",data1);
	     	  	  	    intent.setClass(Avance.this, FormAvance.class);
	     	  	  		startActivity(intent);
	     	  	  		finish();
	     	    	}
	     	    	return false;
	     	    }
		
 public String getConfig(String p1){
				SharedPreferences prefs = getSharedPreferences("config",getBaseContext().MODE_PRIVATE);
					 return  prefs.getString(p1, "");
			}

		 
		 public String getArticulo(String id_articulo)
		 {	
			 String ct=id_articulo.trim();
		 	db.abrirBasedatos();
			// Log.d("base error","select codigo,descripcion from articulos where codigo="+id_articulo.trim()+" " );
		 	Cursor localCursor = db.baseDatos.rawQuery("select codigo,descripcion from articulos where codigo="+id_articulo.trim()+" ", null);
		 
		 	if(localCursor.getCount()>0){
		 	localCursor.moveToFirst();
		 	ct+="-"+localCursor.getString(1);
		 	}
		 	localCursor.close();
		 	db.baseDatos.close();
		 	return ct;
		 }

		 


	
public static String GET(String url){
    InputStream inputStream = null;
    String result = "";
  //  Log.d("ok1", "ok");
    try {

        HttpClient httpclient = new DefaultHttpClient();

        HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

        inputStream = httpResponse.getEntity().getContent();
      //  Log.d("ok2", "ok2");
        if(inputStream != null){
        	  
        	 result =convertInputStreamToString(inputStream);
        }
        else
            result = "Did not work!";

    } catch (Exception e) {
        Log.d("Error", e.getLocalizedMessage());
    }

    return result;
}
private static String convertInputStreamToString(InputStream inputStream) throws IOException{
    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
    String line = "";
    String result = "";
    while((line = bufferedReader.readLine()) != null)
        result += line+",";

    inputStream.close();
    return result.trim();

}
public boolean isConnected(){
    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) 
            return true;
        else
            return false;   
}

private class HttpAsyncTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {

        return GET(urls[0]);
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
  
    	pb.setVisibility(ProgressBar.INVISIBLE);
    	 dataItem = result.split(";");
		setTitle("Ventas "+dataItem[0].split(" ")[1]+dataItem[0].split(" ")[2].replace(",",""));
     myWebView.loadData(getHtml(result), "text/html", "UTF-8");
  
 
 }
  
    protected void onPreExecute() {
    	pb.setVisibility(ProgressBar.VISIBLE);
    }
}



}
