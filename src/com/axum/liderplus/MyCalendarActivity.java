package com.axum.liderplus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.axum.config.Db;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.graphics.drawable.ColorDrawable;
import android.inputmethodservice.Keyboard.Row;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

@SuppressLint("NewApi")
@TargetApi(3)
public class MyCalendarActivity extends Activity implements OnClickListener {
	private static final String tag = "MyCalendarActivity";
	ArrayList<HashMap<String, String>> menuItems ;
    Db db;

	private TextView currentMonth;
	//private Button selectedDayMonthYearButton;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private Calendar _calendar;
	@SuppressLint("NewApi")
	private int month, year;
	 SimpleAdapter adapterList;
	 Dialog dialog;
	 int id_tarea=0;
	  ListView lv;
	 String id_fecha="";


	private final DateFormat dateFormatter = new DateFormat();
	private static final String dateTemplate = "MMMM yyyy";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new Db(this);
		setContentView(R.layout.my_calendar_view);
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		getActionBar().setHomeButtonEnabled(true); 
		getActionBar().setDisplayHomeAsUpEnabled(true);
		this.setTitle("Agenda");
		_calendar = Calendar.getInstance(Locale.getDefault());
		month = _calendar.get(Calendar.MONTH) + 1;
		year = _calendar.get(Calendar.YEAR);


		//selectedDayMonthYearButton = (Button) this.findViewById(R.id.selectedDayMonthYear);
		
		//selectedDayMonthYearButton.setText("Selected: ");

		prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		currentMonth = (TextView) this.findViewById(R.id.currentMonth);
		currentMonth.setText(DateFormat.format(dateTemplate,_calendar.getTime()));

		nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(this);

		calendarView = (GridView) this.findViewById(R.id.calendar);

		// Initialised
		adapter = new GridCellAdapter(getApplicationContext(), month, year);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
		//spinner1 = (Spinner) findViewById(R.id.sp_tarea);
	
	}


	
	private void setGridCellAdapterToDate(int month, int year) {
		adapter = new GridCellAdapter(getApplicationContext(), month, year);
		_calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
		currentMonth.setText(DateFormat.format(dateTemplate,
				_calendar.getTime()));
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		if (v == prevMonth) {
			if (month <= 1) {
				month = 12;
				year--;
			} else {
				month--;
			}
			Log.d(tag, "Setting Prev Month in GridCellAdapter: " + "Month: "
					+ month + " Year: " + year);
			setGridCellAdapterToDate(month, year);
		}
		if (v == nextMonth) {
			if (month > 11) {
				month = 1;
				year++;
			} else {
				month++;
			}
		
			setGridCellAdapterToDate(month, year);
		}

	}

	@Override
	public void onDestroy() {
		Log.d(tag, "Destroying View ...");
		super.onDestroy();
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.m_tareas, menu);
		return true;
	}
	
	
	
public class GridCellAdapter extends BaseAdapter implements OnClickListener {
		private static final String tag = "GridCellAdapter";
		private final Context _context;

		private final List<String> list;
		private static final int DAY_OFFSET = 1;
		private final String[] weekdays = new String[] { "Dom", "Lun", "Mar",
				"Mie", "Jue", "Vie", "Sab" };
		private final String[] months = { "Enero", "Febrero"," Marzo",
"Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
"Octubre","Noviembre", "Diciembre" };
		private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
				31, 30, 31 };
		private int daysInMonth;
		private int currentDayOfMonth;
		private int currentWeekDay;
		private Button gridcell;
		private Button num_events_per_day;
		private final HashMap<String, Integer> eventsPerMonthMap;
		private final SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"dd-MMM-yyyy");

		// Days in Current Month
		public GridCellAdapter(Context context,int month, int year) {
			super();
			this._context = context;
			this.list = new ArrayList<String>();
			Log.d(tag, "=> Aprobada en la fecha para el mes de:" + month + " "
					+ "Year: " + year);
			Calendar calendar = Calendar.getInstance();
			setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
		
			printMonth(month, year);

			// Find Number of Events
			eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
		}

		private String getMonthAsString(int i) {
			return months[i];
		}

		private String getWeekDayAsString(int i) {
			return weekdays[i];
		}

		private int getNumberOfDaysOfMonth(int i) {
			return daysOfMonth[i];
		}

		public String getItem(int position) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		/**
		 * Prints Month
		 * 
		 * @param mm
		 * @param yy
		 */
		private void printMonth(int mm, int yy) {
			Log.d(tag, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
			int trailingSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;

			int currentMonth = mm - 1;
			String currentMonthName = getMonthAsString(currentMonth);
			daysInMonth = getNumberOfDaysOfMonth(currentMonth);
			GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
			
			if (currentMonth == 11) {
				prevMonth = currentMonth - 1;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 0;
				prevYear = yy;
				nextYear = yy + 1;
			
			} else if (currentMonth == 0) {
				prevMonth = 11;
				prevYear = yy - 1;
				nextYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 1;
		
			} else {
				prevMonth = currentMonth - 1;
				nextMonth = currentMonth + 1;
				nextYear = yy;
				prevYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
			
			}

			int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
			trailingSpaces = currentWeekDay;


			if (cal.isLeapYear(cal.get(Calendar.YEAR)))
				if (mm == 2)
					++daysInMonth;
				else if (mm == 3)
					++daysInPrevMonth;

			// Trailing Month days
			for (int i = 0; i < trailingSpaces; i++) {
				
				list.add(String
						.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
								+ i)
						+ "-GREY"
						+ "-"
						+ getMonthAsString(prevMonth)
						+ "-"
						+ prevYear);
			}

			// Current Month Days
			for (int i = 1; i <= daysInMonth; i++) {
				
				if (i == getCurrentDayOfMonth()) {
					list.add(String.valueOf(i) + "-BLUE" + "-"
							+ getMonthAsString(currentMonth) + "-" + yy);
				} else {
					list.add(String.valueOf(i) + "-WHITE" + "-"
							+ getMonthAsString(currentMonth) + "-" + yy);
				}
			}

			// Leading Month days
			for (int i = 0; i < list.size() % 7; i++) {
			//	Log.d(tag, "NEXT MONTH:= " + getMonthAsString(nextMonth));
				list.add(String.valueOf(i + 1) + "-GREY" + "-"
						+ getMonthAsString(nextMonth) + "-" + nextYear);
			}
		}

	
		private HashMap<String, Integer> findNumberOfEventsPerMonth(int year,
				int month) {
			HashMap<String, Integer> map = new HashMap<String, Integer>();

			return map;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) _context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.screen_gridcell, parent, false);
			}

			// Get a reference to the Day gridcell
			gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
			gridcell.setOnClickListener(this);
		//	num_events_per_day = (Button) row.findViewById(R.id.num_events_per_day);
			
			
			// ACCOUNT FOR SPACING
		
		//	Log.d(tag, "Current Day: " + getCurrentDayOfMonth());
			String[] day_color = list.get(position).split("-");
			String theday = day_color[0];
			String themonth = day_color[2];
			String theyear = day_color[3];

		
			// Set the Day GridCell
			gridcell.setText(theday);
		//	num_events_per_day.setText("5");
			gridcell.setTag(theday + "-" + themonth + "-" + theyear);
			Log.d(tag, "Setting GridCell " + theday + "-" + themonth + "-"
					+ theyear);
			num_events_per_day = (Button) row.findViewById(R.id.num_events_per_day);
			if (day_color[1].equals("GREY")) {
				
				gridcell.setTextColor(getResources().getColor(R.color.lightgray));
				num_events_per_day.setVisibility(View.INVISIBLE);
				
			}
			if (day_color[1].equals("WHITE")) {
				gridcell.setTextColor(getResources().getColor(R.color.gray));
				int c= tieneTareas(theday + "-" + themonth + "-" + theyear);
				if(c==0)
				num_events_per_day.setVisibility(View.INVISIBLE);
				else
				num_events_per_day.setText(c+"");	
				
			}
			if (day_color[1].equals("BLUE")) {
				gridcell.setTextColor(getResources().getColor(R.color.orrange));
				int c= tieneTareas(theday + "-" + themonth + "-" + theyear);
				if(c==0)
				num_events_per_day.setVisibility(View.INVISIBLE);
				else
				num_events_per_day.setText(c+"");
			}
		  
			
			return row;
		}

		@Override
		public void onClick(View view) {
			String date_month_year = (String) view.getTag();
		
			showInputDialog(date_month_year);
			
			//Log.e("Selected date", date_month_year);
			try {
				Date parsedDate = dateFormatter.parse(date_month_year);
				Log.d(tag, "Parsed Date: " + parsedDate.toString());

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		public int getCurrentDayOfMonth() {
			return currentDayOfMonth;
		}

		private void setCurrentDayOfMonth(int currentDayOfMonth) {
			this.currentDayOfMonth = currentDayOfMonth;
		}

		public void setCurrentWeekDay(int currentWeekDay) {
			this.currentWeekDay = currentWeekDay;
		}

		public int getCurrentWeekDay() {
			return currentWeekDay;
		}
	}
	
	public void volver(){
		
		     Intent intent = new Intent();
		     intent.putExtra("m","MenuPpal");
		     intent.setClass(MyCalendarActivity.this, MenuPpal1.class);
			 startActivity(intent);
			 finish();
	}

	public void showInputDialog(final String f) {

		id_fecha=f;
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View promptView = layoutInflater.inflate(R.layout.a_tareas_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptView);
		final EditText editTarea = (EditText) promptView.findViewById(R.id.t_tarea);
		final Spinner spinner1= (Spinner) promptView.findViewById(R.id.sp_tarea);
		spinner1.setBackgroundResource(R.drawable.spinner_selector);
		editTarea.setBackgroundResource(android.R.drawable.edit_text);
		
		 menuItems = new ArrayList<HashMap<String, String>>();
		 adapterList = new SimpleAdapter(this, menuItems,R.layout.row_tarea,new String[] {"imagen","id" }, new int[] {R.id.icon,R.id.t_nota_detalle});
		lv  = (ListView) promptView.findViewById(R.id.l_tareas);
		 lv.setAdapter(adapterList);
		 listarTareas(f);
		final Button bAgregar = (Button) promptView.findViewById(R.id.b_agregar);
	
		bAgregar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				int s1=spinner1.getSelectedItemPosition();
				String s2="";
				if(editTarea.getText().toString().length()>0 || s1>0 ){
					if(s1>0)s2=spinner1.getSelectedItem().toString();
					cargarTarea(editTarea.getText().toString()+s2,f);
					editTarea.setText("");spinner1.setSelection(0);
					listarTareas(f);
					addItemsOnSpinner1(spinner1);
					adapterList.notifyDataSetChanged();
				}
				
			   }
			});
	
		alertDialogBuilder.setCancelable(false)
		.setPositiveButton("Listo", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					
					adapter = new GridCellAdapter(getApplicationContext(), month, year);
					adapter.notifyDataSetChanged();
					calendarView.setAdapter(adapter);
				 }
				});
	
lv.setOnItemLongClickListener(new OnItemLongClickListener() {
	        @Override
	        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
	                int arg2, long arg3) {
	        	    id_tarea=arg2;
	        	    registerForContextMenu(arg0);
	        	    openContextMenu(arg0);
	            return false;
	        }
 });

	
spinner1.setOnItemSelectedListener(
	                new OnItemSelectedListener() {
	                    public void onItemSelected( AdapterView<?> parent, View view, int position, long id) {
	                    	if(position>0){
	                    		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	                    		imm.hideSoftInputFromWindow(editTarea.getWindowToken(), 0);
	                    		editTarea.setText("");
	                    		editTarea.setEnabled(false);
	                    		editTarea.setBackgroundResource(android.R.color.transparent);
	                    		
	                    	}
	                    	else{
	                    		editTarea.setEnabled(true);
	                    		editTarea.setBackgroundResource(android.R.drawable.edit_text);
	                    	}
	                  }
	                    public void onNothingSelected(AdapterView<?> parent) {
	                    }
 }); 	
addItemsOnSpinner1(spinner1);
		AlertDialog alert = alertDialogBuilder.create();
		alert.setTitle(f);
	
		alert.show();
}
public void addItemsOnSpinner1(Spinner spinner1 ) {
	List<String> list = new ArrayList<String>();
		 list.add("");
		 db.abrirBasedatos();
	//Toast.makeText(getBaseContext(),id_fecha+"", Toast.LENGTH_SHORT).show();
	String f= id_fecha.split("-")[1];
	   Cursor localCursor = db.baseDatos.rawQuery("select *  from tareas where fecha like '%"+f+"%' group by tarea  ", null);
				    while (true)
				    {
				      if (!localCursor.moveToNext())
				      { 
				        localCursor.close();
				        db.baseDatos.close();
				        break;
				      }
				 	 list.add(localCursor.getString(1));
			}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(dataAdapter);
	  }

public void onClick1(View v) {
	 String s1 = menuItems.get(id_tarea).get("codigo");
	eliminarTare(s1);
	listarTareas(id_fecha);
}
	
	
	
public boolean onOptionsItemSelected(MenuItem item) {
			// Handle item selection
		
			switch (item.getItemId()) {
		       case android.R.id.home:
				   volver();
			    return true;
		       case R.id.mi_enviar_mail:
		    	   String correos=PreferenceManager.getDefaultSharedPreferences(this).getString("correo","");
		    	   if(correos.length()>0){
		    		   
		    	   if(saveExcelFile(this,"Agenda.xls"))
		    		sendMail(correos);
		    	  }else
		    		 alertaInfo();
		    	   
			    return true;  
			    
		
			   default:
			    return super.onOptionsItemSelected(item);
			}
	}

public boolean onKeyDown(int keyCode, KeyEvent event) {
     	super.onKeyDown(keyCode, event);
     	    	if (keyCode == KeyEvent.KEYCODE_BACK) {
     	    	volver();
     	    	}
     	    	return false;
    }
@SuppressLint("NewApi")
public void alertaInfo() {
    new AlertDialog.Builder(this)
    		.setTitle("Alerta")
           .setMessage("Debe tener configurado al menos una dirección de corre en Preferencia!")
           .setCancelable(false)
            .setIconAttribute(android.R.attr.alertDialogIcon)
           .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
        
               }
           }) 
           .show();
}
public void listarTareas(String fecha){
	  db.abrirBasedatos();
	  HashMap map = new HashMap();
	  menuItems.clear();
		 Cursor localCursor = db.baseDatos.rawQuery("select *  from tareas where fecha like '"+fecha+"' order by codigo desc ", null);
			    while (true)
			    {
			      if (!localCursor.moveToNext())
			      { 
			        localCursor.close();
			        db.baseDatos.close();
			        break;
			      }
			  	map = new HashMap();
			    map.put("imagen",R.drawable.notes_icon1);
			    map.put("id",localCursor.getString(1));
			    map.put("codigo",localCursor.getString(0));
			  
			   menuItems.add(map);
			    
			 }
			    adapterList.notifyDataSetChanged();
	
}

public int tieneTareas(String fecha)
	{	
		db.abrirBasedatos();
		Cursor localCursor = db.baseDatos.rawQuery("select count(codigo) from tareas where fecha like '"+fecha+"' ", null);
		localCursor.moveToFirst();
		int count= localCursor.getInt(0);
		localCursor.close();
		db.baseDatos.close();
		return count;
	}



private void cargarTarea(String tarea,String fecha) {
	 db.abrirBasedatos();
	 	String sql = "INSERT INTO tareas (tarea,fecha,flag) VALUES (?,?,?)";
	 	SQLiteStatement statement = db.baseDatos.compileStatement(sql);
      db.baseDatos.beginTransaction();
     statement.clearBindings();
     statement.bindString(1,tarea.toLowerCase());
	 statement.bindString(2,fecha);
	 statement.bindString(3,"0");
	 statement.execute();
	 db.baseDatos.setTransactionSuccessful();	
	 db.baseDatos.endTransaction();
     db.baseDatos.close();
} 

public void eliminarTare(String s1)
 {
	 db.abrirBasedatos();
	 Log.d("SQL","DELETE FROM tareas WHERE codigo like '"+s1+"'");
   db.baseDatos.execSQL("DELETE FROM tareas WHERE codigo like '"+s1+"'");

    db.baseDatos.close();
 }

private  boolean saveExcelFile(Context context, String fileName) { 
	 
    // check if available and not read only 
    if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) { 
        Log.w("FileUtils", "Storage not available or read only"); 
      //  Toast.makeText(getBaseContext(),"Almacenamiento no están disponibles o de sólo lectura", Toast.LENGTH_SHORT).show();
        return false; 
    } 

    boolean success = false; 

    HSSFWorkbook wb = null;
    File file =null;
	try {
		AssetManager assManager = getApplicationContext().getAssets();
		String[] files = assManager.list("Files");
		
		file = new File(getExternalFilesDir(null), fileName);
	   // Log.w("NOMBRE ARCHIVO ",  getFilesDir().getParentFile().getPath());
		InputStream input = assManager.open("files/"+fileName);
		 getFilesDir().getParentFile().getPath();
	   // file = new File("//android_asset/Files/", fileName); 
	    //  FileInputStream myInput = new FileInputStream(file);
		  wb = new HSSFWorkbook(input);
		
	} catch (IOException e1) {
		  Log.w("ERROR ARCHIVO ", e1);
		//e1.printStackTrace();
	}
	String periodo[]=currentMonth.getText().toString().split(" ");
    Sheet sheet = wb.getSheetAt(0);
    
    Log.w("DATOS DE ARCHIVO ", sheet.getSheetName()+"");
	org.apache.poi.ss.usermodel.Row row = sheet.getRow(1);
	Cell cell = ((org.apache.poi.ss.usermodel.Row) row).getCell(2);
	cell.setCellValue(periodo[0].toUpperCase());
	 row =  sheet.getRow(2);
	 cell = ((org.apache.poi.ss.usermodel.Row) row).getCell(2);
	cell.setCellValue(periodo[1]);
	
	int f=6,c=0;
	for(int d=0; d<adapter.getCount();d++){
		String[] day_color = adapter.getItem(d).split("-");
		String theday = day_color[0];
		String themonth = day_color[2];
		String theyear = day_color[3];
		
		 if(!esMultiplo(d,7)){
			// if(esMultiplo(d,6))
			 if (!day_color[1].equals("GREY")) {
				//  Log.d("Data",f+"-"+c+" /"+d);
				 row =  sheet.getRow(f);
				 cell = ((org.apache.poi.ss.usermodel.Row) row).getCell(c);
				 cell.setCellValue(theday+"-"+themonth.substring(0, 3)); 
				 row =  sheet.getRow(f+2);
				 cell = ((org.apache.poi.ss.usermodel.Row) row).getCell(c);
				cell.setCellValue(getTarea(theday+"-"+themonth+"-"+theyear));
				  if (c==6){
					  f=f+5; 
					  }
				
			 }
	
		 }else{
			  c=0;
		 }
		 c++;
	}

    FileOutputStream os = null; 
   
    try { 
        os = new FileOutputStream(file);
        wb.write(os);
       // Toast.makeText(getBaseContext(),"Generando archivo....", Toast.LENGTH_SHORT).show();
       // Log.w("FileUtils", "Generando archivo.." + file); 
        success = true; 
    } catch (IOException e) { 
        Log.w("FileUtils", "Error writing " + file, e); 
    } catch (Exception e) { 
        Log.w("FileUtils", "Failed to save file", e); 
    } finally { 
        try { 
            if (null != os) 
                os.close(); 
        } catch (Exception ex) { 
        } 
    } 

    return success; 
} 

public static boolean esMultiplo(int n1,int n2){
	if (n1%n2==0)
		return true;
	else
		return false;
}



public static boolean isExternalStorageReadOnly() { 
    String extStorageState = Environment.getExternalStorageState(); 
    if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) { 
        return true; 
    } 
    return false; 
} 

public static boolean isExternalStorageAvailable() { 
    String extStorageState = Environment.getExternalStorageState(); 
    if (Environment.MEDIA_MOUNTED.equals(extStorageState)) { 
        return true; 
    } 
    return false; 
} 

public String getTarea(String fecha)
{		db.abrirBasedatos();
	 String ct="";
	 Cursor localCursor = db.baseDatos.rawQuery("select *  from tareas where fecha like '"+fecha+"' order by codigo desc ", null);
	    while (true)
	    {
	      if (!localCursor.moveToNext())
	      { 
	        localCursor.close();
	        db.baseDatos.close();
	        break;
	      }
	      ct+=localCursor.getString(1)+"\t\n";
	    
	 }
	return ct;
}

public void sendMail(String correos) {
	Intent i = new Intent(Intent.ACTION_SEND);
	String e[]=correos.split(";");
    i.setType("message/rfc822");
    i.putExtra(Intent.EXTRA_EMAIL  ,e);
    i.putExtra(Intent.EXTRA_SUBJECT, "Agenda_"+currentMonth.getText().toString().replace(" ","_"));
    i.putExtra(Intent.EXTRA_TEXT   , "");
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
    Uri uri = Uri.fromFile(new File(getExternalFilesDir(null), "Agenda.xls"));
    i.putExtra(Intent.EXTRA_STREAM, uri);
    startActivity(Intent.createChooser(i, "Enviar correo electrónico..."));
    }
}
