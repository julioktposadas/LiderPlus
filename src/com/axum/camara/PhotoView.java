
package com.axum.camara;

import java.io.File;

import com.axum.liderplus.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;



public class PhotoView extends Activity {
    private static final String TAG = "PhotoViewActivity";

    private Uri uri;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        super.setContentView(R.layout.photo);
        this.uri = super.getIntent().getData();
        ImageView photo = (ImageView)super.findViewById(R.id.photo);
        Log.d(TAG, "Loading photo: " + this.uri);
        
        
        
        photo.setImageURI(this.uri);
    }

    // gets called by the button press
    public void guardar(View v) {
       Intent resultIntent = new Intent();
        resultIntent.putExtra("result", this.uri.toString());
        setResult(RESULT_OK, resultIntent);
         finish();
	}

    // gets called by the button press
    public void cancelar(View v) {
        if (new File(this.uri.getPath()).delete()) {
        	
        	  Intent resultIntent = new Intent();
              resultIntent.putExtra("result", "");
              setResult(RESULT_OK, resultIntent);
               finish();
        } else {
            Log.d(TAG, "Failed to delete: " + this.uri);
        }
        Log.d(TAG, "Going back");
        //PhotoCaptureActivity.primerShoot = true;
        super.finish();
    }
    
    
    
    
}
