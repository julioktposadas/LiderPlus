package com.axum.liderplus;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class AvanceDiario extends Activity {

	WebView mWebView ;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_avance_diario);
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		getActionBar().setHomeButtonEnabled(true); 
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mWebView = (WebView) findViewById(R.id.webView1);
		mWebView.loadUrl("file:///android_asset/avance_diario.html");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.avance_diario, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
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
  intent.setClass(AvanceDiario.this, MenuPpal.class);
	 startActivity(intent);
	 finish();


}	
}
