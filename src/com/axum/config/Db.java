package com.axum.config;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Db {

	public static final String nombreBD = "db";
	public SQLiteDatabase baseDatos;
    private Context mContext;
	
	public  Db(Context ct){
		this.mContext=ct;
	}

	public void abrirBasedatos()
	{
		 
		//codigo_contrato;contrato;correctivos;periodo;razon_social;cod_cliente;cod_sucursal;desp_suc;localidad;departamento;provincia;zona_suc;abono_desc;ruta_fecha;ruta_tecnico;ruta_cnt;maqui;modelo;serie;OT_nro;OT_fecha;periodo;tecnico;cant_real;borra;agen_cnt;tecni_super;domi;tecni_contacto;maqui_tipo;piezas_proce_canti
		  try
		  {
		    this.baseDatos = mContext.openOrCreateDatabase(nombreBD, android.content.Context.MODE_PRIVATE, null);
		    this.baseDatos.execSQL("create table if not exists  clientes (codigo text,nombre text ,direccion text,telefono text,lista text,zona text,ramo text,dato8 text,dato9 text,dato10 text,dato11 text,dato12 text,dato13 text,dato14 text,dado15 text,flag text );");
		    this.baseDatos.execSQL("create table if not exists  articulos (codigo integer primary key,descripcion text,lista text,linea text,rubro text,capacidad text,pack text,agrupacion text DEFAULT '',flag text);");
		    this.baseDatos.execSQL("create table if not exists  estandar (id_encuesta text, id_bloque text,bloque text,sub_bloque text,item text,tipo text ,respuesta text,fecahaini text,fechafin text );");
		    this.baseDatos.execSQL("create table if not exists  respuestas (id_cliente text,fecha dete,id_encuesta text,id_pregunta text,respuesta text,flag text,vend text,grupo text,destino text, dato9 text, dato10 text, primary key (id_cliente,id_encuesta,id_pregunta) );");
		    this.baseDatos.execSQL("create table if not exists  clientesposicion (codigo integer primary key,nombre text ,canal text,coordenada text,vend text,dato2 text,dato3 text,dato4 text,dado5 text,flag text );");
		    this.baseDatos.execSQL("create table if not exists  vendedores (codigo text ,nombre text,flag integer);");
		    this.baseDatos.execSQL("create table if not exists  tareas (codigo integer primary key autoincrement,tarea text,fecha text,flag text);");
		    this.baseDatos.execSQL("create table if not exists  encuesta (id_encuesta text,nombre text,grupo text ,id_pregunta text,pregunta text,tipodato text,opciones text,fecahaini text,fechafin text,habilita text  );");
		    this.baseDatos.execSQL("create table if not exists  ramos (codigo text ,descripcion text primary key,flag text);");
		    return;
		  }
		  catch (Exception localException)
		  {
		    Log.d("base error", "Error al abrir o crear la base de datos" + localException);
		  }
	}
		

}
