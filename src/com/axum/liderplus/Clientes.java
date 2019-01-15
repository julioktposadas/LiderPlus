package com.axum.liderplus;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.axum.config.Db;
import com.axum.mapa.FormMapaClientes;
import com.axum.mapa.FormMapaClientes2;
import com.axum.mapa.FormMapaGoogle;


public class Clientes extends Activity implements SearchView.OnQueryTextListener{
		ArrayList<HashMap<String, String>> menuItems ;
	 ArrayList<String> list_Ramo = new ArrayList<String>();
		int index_list=0;
		String rel="",item_ramo="",  m="";
		Bundle bundle ;
		Db db;
		 //TextView buscarTx;
		 TextView titulo;
		 SimpleAdapter adapter;
		 ListView lv ;
		 int totalCli=0;
		 private static final String LIST_STATE = "listState";
		 private Parcelable mListState = null;
		 LinearLayout l1=null;
		 EditText buscarTx;
		 boolean b_visible=false;
		 private SearchView mSearchView;
		 private TextView mStatusView;
		 String menuId="",idCliente="";
		 int id_cliente=0;
		 Dialog dialog ;
		 AlertDialog levelDialog;
		private CharSequence[] items;
		int xx[]= {0};
		
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db= new Db(this);
		 bundle = getIntent().getExtras();
	
		setContentView(R.layout.menu_lista_cliente);
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		getActionBar().setHomeButtonEnabled(true); 
		getActionBar().setDisplayHomeAsUpEnabled(true);
		idCliente=	bundle.getString("idCliente");
		

		 mStatusView = (TextView) findViewById(R.id.status_text);
		 
	   menuItems = new ArrayList<HashMap<String, String>>();

	  adapter = new SimpleAdapter(this, menuItems,
				R.layout.row_simple_list,new String[] {"imagen","row_toptext" ,"row_bottontext","row_bottontext_r"}, new int[] {R.id.icon,
				R.id.row_toptext,R.id.row_bottontext,R.id.row_bottontext_r});
	
	
		 getTrackDB("",0,item_ramo);
		  
		 lv = (ListView) findViewById(R.id.listR2);
		    lv.setAdapter(adapter);
		    registerForContextMenu(lv);
	

		lv.setOnItemClickListener(new OnItemClickListener() {
             @Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
            	 
            	       id_cliente=position;
						String s1 = menuItems.get(position).get("row_toptext").split("-")[0];
						String s2 = menuItems.get(position).get("id_encuesta");
						
				        Intent intent = new Intent().setClass(Clientes.this, MenuPpal.class);
			     	    intent.putExtra("m","FormRelevamiento");
		                intent.putExtra("idCliente",s1);
		                intent.putExtra("id_encuesta",s2);
		               
		                startActivity(intent);
		               finish(); 
		               }
		});
		
		 lv.postDelayed(new Runnable() {
		     public void run() {
		    	 lv.requestFocusFromTouch();
		    	 lv.setSelection(index_list);
		     }
		    },150);


	 


// Creating and Building the Dialog 
AlertDialog.Builder builder = new AlertDialog.Builder(this);
  builder.setTitle("Zona");
  int z=getZonas();
  builder.setSingleChoiceItems(items,z, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
         
    	   guardarConfig("filtro",items[id]+"");  
           guardarConfig("filtro_id",id+""); 
           getTrackDB("",0,"");
            levelDialog.dismiss();     
          }
      });
  
  levelDialog = builder.create();
  
//int errer=xx[3];
}
	
public void actualizarDatos(View v){
		Intent intent = new Intent();
	    intent.setClass(Clientes.this, FormActualizar.class);
		startActivity(intent);
		finish();

}
public void enviarDatos(View v){
		Intent intent = new Intent();
		intent.putExtra("m","EnviarDatos");
	    intent.setClass(Clientes.this, MenuPpal.class);
	    startActivity(intent);
		finish();

}
public void mapaClientes(View v){
	
	
    Intent intent = new Intent().setClass(Clientes.this, FormMapaClientes2.class);
    intent.putExtra("m","Clientes"); 
    String s1 = menuItems.get(0).get("row_toptext").split("-")[0];
	String s2 = menuItems.get(0).get("id_encuesta");
    intent.putExtra("idCliente",s1);
    intent.putExtra("id_encuesta",s2);
    intent.putExtra("vendId",getConfig("vend")); 
    
   startActivity(intent);
	finish(); 

}


	public void setSelect( String id){
	  int i1=Integer.parseInt(id.split(",")[0]);
	  int i2=Integer.parseInt(id.split(",")[1]);
		lv.setSelectionFromTop(i1,i2);
	
		adapter.notifyDataSetChanged();
		
	}
	 public boolean isGPSEnabled()
 	  {
 	    String str = Settings.Secure.getString(getBaseContext().getContentResolver(), "location_providers_allowed");
 	    if (str != null)
 	      return str.contains("gps");
 	    return false;
 	  }
	

	
public void getTrackDB(String s, int n,String ramo)
		  {
		String and="";
		 ramo=getConfig("filtro");
	  if(s.equals("*")){
		  and= "and respuestas.flag is not null";
		  s="";
	  }
			 Cursor localCursor = null;
			String col="nombre";
			if(n==1)col="codigo";	
			 menuItems.clear();
			 db.abrirBasedatos();
			
			 int i=1;totalCli=0;
		
			 if(n==2){
			 localCursor=db.baseDatos.rawQuery("select codigo,nombre ,direccion,telefono,lista,zona,ramo,dato8,dato9,dato10,dato11,dato12,dato13,dato14,dado15,flag from clientes as c where exists(select * from respuestas as r where c.codigo=r.id_cliente and r.fecha like '"+ramo+"')", null);
			// Log.w("SQL:","select codigo,nombre ,direccion ,telefono,lista,zona,ramo,dato8 from clientes as c where exists(select * from respuestas as r where c.codigo=r.id_cliente and r.fecha like '"+ramo+"')");
			 }
			 if(n!=2)
			  localCursor = db.baseDatos.rawQuery("select clientes.*,respuestas.flag from clientes LEFT JOIN respuestas ON clientes.codigo=respuestas.id_cliente where "+col+" like '"+s+"%' and  zona like '"+ramo+"%' "+and+" group by clientes.codigo ", null);
		/// Log.w("SQL:","select clientes.*,respuestas.flag from clientes LEFT JOIN respuestas ON clientes.codigo=respuestas.id_cliente where "+col+" like '"+s+"%' and  ramo like '"+ramo+"%' "+and+" group by clientes.codigo ");
			 
			    while (true)
			    {
			      if (!localCursor.moveToNext())
			      {
			    	  
			    
			        localCursor.close();
			        db.baseDatos.close();
			        this.setTitle("Clientes:  "+totalCli);
			        adapter.notifyDataSetChanged();
			        
			        break;
			      }
			      
			  if(bundle.getString("idCliente").equals(localCursor.getString(0)))
			      this.index_list=	totalCli;

		
			     HashMap map = new HashMap();
			   String r0=localCursor.getString(7);
			   String r1=localCursor.getString(8);
			 
				
				String ico=localCursor.getString(16);
				
				 if(ico==null)
					 map.put("imagen",R.drawable.vacio);
					 else{
						 if(ico.equals("0"))
						 	map.put("imagen",R.drawable.folder3);
						 if(ico.equals("1"))
					        map.put("imagen",R.drawable.folder4);
						 if(ico.equals("2"))
						        map.put("imagen",R.drawable.vacio);
			       }
					map.put("row_toptext",localCursor.getString(0)+"-"+localCursor.getString(1));
					String dir=localCursor.getString(2);
					map.put("latlog",localCursor.getString(10));
					
					map.put("row_bottontext",dir);
					
			
					if(r1!=null)
						{
							r1=r1.replace("|",";");
							map.put("row_bottontext_r",R.drawable.verde);
						}
					map.put("id_encuesta",r0.replace("|",";")+";"+r1);
					menuItems.add(map);
			//	}
				totalCli++;
				i++;
			    //  strBuf.append(localCursor.getString(1));
			    }
			    
		 }
		 public boolean onKeyDown(int keyCode, KeyEvent event) {
	        	super.onKeyDown(keyCode, event);
	        	    	if (keyCode == KeyEvent.KEYCODE_BACK) {
	        	    	
	        	    		volver();
	        	    		
	        	    	}
	        	    	return false;
	       }
		
public void volver(){
	
	   Intent intent = new Intent();
	     intent.putExtra("m","MenuPpal");
	     intent.setClass(Clientes.this, MenuPpal1.class);
		 startActivity(intent);
		 finish();
	

 }

	 public void guardarConfig(String paramString1, String paramString2)
		 {
		   SharedPreferences.Editor localEditor = getSharedPreferences("config", 0).edit();
		   localEditor.putString(paramString1, paramString2);
		   localEditor.commit();
		 }
	 public String getConfig(String p1){
				SharedPreferences prefs = getSharedPreferences("config",getBaseContext().MODE_PRIVATE);
					 return  prefs.getString(p1, "");
			}	
	 public boolean onCreateOptionsMenu(Menu menu) {

	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.vistabusqueda, menu);
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

	        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	       mSearchView.setOnQueryTextListener(this);
	    }

	 protected boolean isAlwaysExpanded() {
	        return false;
	    }
	 public boolean onOptionsItemSelected(MenuItem item) {
			// Handle item selection
		
			switch (item.getItemId()) {
			case R.id.mi_filtro:
				   levelDialog.show();
			    return true;
			case android.R.id.home:
				   volver();
			    return true;
		
			default:
			    return super.onOptionsItemSelected(item);
			}
			}
	 public void mostrarRamo() {
		 
		 final CharSequence[] items =list_Ramo.toArray(new CharSequence[list_Ramo.size()]);
		 /* final CharSequence[] items = {
	                "Rajesh", "Mahesh", "Vijayakumar"
	        };
	        */
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("Ramos");
	        builder.setItems(items, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int item) {
	            	
	                // Do something with the selection
	            	item_ramo=(String) items[item];
	            	//Log.w("Ramo:",item_ramo );
	            	getTrackDB("",0,item_ramo);
	            }
	        });
	        AlertDialog alert = builder.create();
	        alert.show();

	    }
 public int getZonas(){
	int z=0;
		db.abrirBasedatos();
		ArrayList<String> list_zona = new ArrayList<String>();
		list_zona.add("");
		 Cursor localCursor = db.baseDatos.rawQuery("select zona from clientes group by zona", null);
	 while (true)
			    {
			    
			      if (!localCursor.moveToNext())
			      {
			        localCursor.close();
			        db.baseDatos.close();
			        break;
			      }
			      list_zona.add(localCursor.getString(0));
			    }
	 items = list_zona.toArray(new CharSequence[list_zona.size()]);
	 if(getConfig("filtro_id")!="")
		 z=Integer.parseInt(getConfig("filtro_id"));
	 return  z;
 }


	@Override
	public boolean onQueryTextSubmit(String query) {
	//	Toast.makeText(this, "Data:"+query, Toast.LENGTH_LONG).show();
		return false;
	}
/*
	
 public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	 {
	        super.onCreateContextMenu(menu, v, menuInfo);
	        menu.add(0, 2, 0, "Cargar");
	        menu.add(0, 6, 0, "Historico");
	        menu.add(0, 7, 0, "Info Cliente");
	        menu.add(0, 4, 0, "Mapa");
	      
	    	String s1 = menuItems.get(id_cliente).get("row_toptext");
	        menu.setHeaderTitle(s1);
	 }
 }
 */
 public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
     super.onCreateContextMenu(menu, v, menuInfo);
     if (v.getId()==R.id.listR2) {
    	 
    	 lv = (ListView) findViewById(v.getId());
         MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.m_contextual_clientes, menu);
     }
}

 public boolean onContextItemSelected(MenuItem item) {
	 String s1,s2;
	 
	   AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	 //int  number=new ContactListAdapter(this).numberList.get(info.position);
	 switch(item.getItemId()) {
     case R.id.i_cargar:
    	  s1 = menuItems.get(info.position).get("row_toptext").split("-")[0];
 		  s2 = menuItems.get(info.position).get("id_encuesta");
         Intent intent = new Intent().setClass(Clientes.this, MenuPpal.class);
  	     intent.putExtra("m","FormRelevamiento");
         intent.putExtra("idCliente",s1);
         intent.putExtra("id_encuesta",s2);
         startActivity(intent);
         finish(); 
        return true;
      case R.id.i_info:
    	   s1 = menuItems.get(info.position).get("row_toptext").split("-")[0];
    	  onCreateDialogo(s1).show();
            return true;
      case R.id.i_historico:
    	     s1 = menuItems.get(info.position).get("row_toptext");
     	    Intent localIntent = new Intent(this, FormHistorico.class);
    	  	localIntent.putExtra("idCliente",s1.split("-")[0]);
    	  	startActivity(localIntent);
 		    finish();
    	  return true;  
      case R.id.i_ubicacion:
    	   s1 = menuItems.get(info.position).get("row_toptext").split("-")[0];
    		s2 = menuItems.get(info.position).get("latlog");
        // this.setTitle(s1+" "+ s2);
    	  Intent intent2 = new Intent().setClass(Clientes.this, FormMapaGoogle.class);
    	  intent2.putExtra("idCliente",s1);
    	  intent2.putExtra("latlog",s2);
    	 
    	 startActivity(intent2);
        finish(); 
            return true;
      default:
            return super.onContextItemSelected(item);
  }
 }
	
 
 public String  getDatalleCliente(String id_cliente){
	  StringBuffer stringbuffer = new StringBuffer();
	try
     {
	  db.abrirBasedatos();
	  stringbuffer.append("<html ><body bgcolor=\"#FFFFFF\"> <font size=\"-1\" ><table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\" bgcolor=\"#5E8EBA\">");
     
      Cursor localCursor = db.baseDatos.rawQuery("select clientes.*, ramos.descripcion from clientes, ramos where clientes.ramo= ramos.codigo and clientes.codigo ='"+id_cliente.trim()+"' group by clientes.codigo", null);
   //Log.w("SQL","select clientes.*, ramos.descripcion from clientes, ramos where clientes.ramo= ramos.codigo and clientes.codigo ='"+id_cliente.trim()+"' group by clientes.codigo" );
			    while (true)
			    {
			    	
			      if (!localCursor.moveToNext())
			      {
			        localCursor.close();
			        db.baseDatos.close();
			        break;
			      }
			      //if(localCursor.getString(0)!=null)
			     // Log.w("SQL",localCursor.getString(0)+","+localCursor.getString(1)+","+localCursor.getString(2)+","+localCursor.getString(3)+","+localCursor.getString(4)+","+localCursor.getString(5)+","+localCursor.getString(6)+","+localCursor.getString(7)+","+localCursor.getString(8));
			      stringbuffer.append("<tr bgcolor='#5E8EBA' > <td width='75%'><font color='#FFFFFF'>Codigo</font></td>  </tr>").append("<tr bgcolor='#FFFFFF' > <td width='85%'>"+localCursor.getString(0)+"&nbsp;</td> </tr>");
			      stringbuffer.append("<tr bgcolor='#5E8EBA' > <td width='75%'><font color='#FFFFFF'>Razon Social</font></td>  </tr>").append("<tr bgcolor='#FFFFFF' > <td width='85%'>"+localCursor.getString(1)+"&nbsp;</td> </tr>");
			      stringbuffer.append("<tr bgcolor='#5E8EBA' > <td width='75%'><font color='#FFFFFF'>Direccion </font></td>  </tr>").append("<tr bgcolor='#FFFFFF' > <td width='85%'>"+localCursor.getString(2)+"&nbsp;</td> </tr>");
			      stringbuffer.append("<tr bgcolor='#5E8EBA' > <td width='75%'><font color='#FFFFFF'>Telefono</font></td>  </tr>").append("<tr bgcolor='#FFFFFF' > <td width='85%'>"+localCursor.getString(3)+"&nbsp;</td> </tr>");
			      stringbuffer.append("<tr bgcolor='#5E8EBA' > <td width='75%'><font color='#FFFFFF'>Cuit</font></td>  </tr>").append("<tr bgcolor='#FFFFFF' > <td width='85%'>"+localCursor.getString(5)+"&nbsp;</td> </tr>");
			      stringbuffer.append("<tr bgcolor='#5E8EBA' > <td width='75%'><font color='#FFFFFF'>Ramo</font></td>  </tr>").append("<tr bgcolor='#FFFFFF' > <td width='85%'>"+localCursor.getString(16)+"&nbsp;</td> </tr>");
			      stringbuffer.append("<tr bgcolor='#5E8EBA' > <td width='75%'><font color='#FFFFFF'>Cond.IVA</font></td>  </tr>").append("<tr bgcolor='#FFFFFF' > <td width='85%'>"+localCursor.getString(8)+"&nbsp;</td> </tr>");
			      stringbuffer.append("<tr bgcolor='#5E8EBA' > <td width='75%'><font color='#FFFFFF'>Lista P.</font></td>  </tr>").append("<tr bgcolor='#FFFFFF' > <td width='85%'>"+localCursor.getString(4)+"&nbsp;</td> </tr>");
			      stringbuffer.append("<tr bgcolor='#5E8EBA' > <td width='75%'><font color='#FFFFFF'>Observacion</font></td>  </tr>").append("<tr bgcolor='#FFFFFF' > <td width='85%'>"+localCursor.getString(9)+"&nbsp;</td> </tr>");
			     
		          
			    }
	 
			    stringbuffer.append("</table></font></body></html>");
      }
      catch (Exception exception) { }
      
      return stringbuffer.toString();
}
 protected Dialog onCreateDialogo(String title) {
		
	    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    AlertDialog.Builder builder = null;
	    		 dialog = new Dialog(this);
	            final View v = inflater.inflate(R.layout.form_historico, null);
	            //********
	           // LinearLayout l1 = (LinearLayout) v.findViewById(R.id.linearLayout_Relev_Views1);
	            WebView myWebView  = (WebView)  v.findViewById(R.id.webResumen);
	        //    Log.w("SQL","select clientes.*, ramos.descripcion from clientes, ramos where clientes.ramo= ramos.codigo and clientes.codigo ="+title );
	            myWebView.loadData(getDatalleCliente(title), "text/html", "UTF-8");
	           //*******
	            builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
	            builder.setView(v);
	            builder.setTitle("Detalle Cliente: "+title);
	            
	             builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
					 public void onClick(DialogInterface dialog, int which) {
						
						     
		                      dialog.dismiss();
		                     
		                }
		            });
	          
	             dialog = builder.create();
	          
	    return dialog;
	}
 
 
	public boolean onQueryTextChange(String s1) {
		
		 if (!s1.matches("\\+?[0-9]+"))
    		 getTrackDB(s1,0,"");
    	 else
    		 getTrackDB(s1,1,"");
	
		return false;
	}
	


}
