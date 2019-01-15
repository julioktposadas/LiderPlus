package  com.axum.camara;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.axum.liderplus.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;



public class PhotoCapture extends Activity implements PictureCallback, LocationListener {
    private static final String TAG = "PhotoCaptureActivity";

    boolean primerShoot;
    private final int ACTIVITY_PHOTOVIEW=0;
    
    Camera camera;
    private Uri uri;
    ImageButton takePictureButton;

    FrameLayout cameraPreviewFrame;

    CameraPreview cameraPreview;
    
    LocationManager locationManager;
    String currentversion;
    int currentInt;
   int widthIM=  640;
	int heightIM=480;
	private File outPutFile = null;
	Bundle bundle1;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        super.setContentView(R.layout.camera);
        this.cameraPreviewFrame = (FrameLayout)super.findViewById(R.id.camera_preview);
        this.takePictureButton = (ImageButton)super.findViewById(R.id.takePictureButton);
        this.takePictureButton.setEnabled(false);
        this.locationManager = (LocationManager)super.getSystemService(LOCATION_SERVICE);
        bundle1 = getIntent().getExtras();
        
    	File appDirectory = new File( Environment.getExternalStorageDirectory() + "/AX" );
    	
    	  if ( !appDirectory.exists() ) {
    			appDirectory.mkdir();
    		}
    	  File logDirectory = new File( appDirectory + "/Foto" );
    		if ( !logDirectory.exists() ) {
    			logDirectory.mkdir();
    		}
    		// dato= bundle.getString("fotoid").split(",");
    		 outPutFile = new File(logDirectory,bundle1.getString("fotoid"));
    		  primerShoot = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // initialize the camera in background, as this may take a while
        new AsyncTask<Void, Void, Camera>() {

            @Override
            protected Camera doInBackground(Void... params) {
                try {
                    Camera camera = Camera.open();
                    return camera == null ? Camera.open(0) : camera;
                } catch (RuntimeException e) {
                    Log.wtf(TAG, "Failed to get camera", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Camera camera) {
                if (camera == null) {
                    Log.wtf(TAG, "Failed to get camera");
                    Toast.makeText(PhotoCapture.this, "Failed to open camera",
                            Toast.LENGTH_SHORT);
                } else {
                    PhotoCapture.this.initCamera(camera);
                }
            }
        }.execute();
    }

    // gets called from onResume()'s AsyncTask
    void initCamera(Camera camera) {
        // we now have the camera
        this.camera = camera;
        // create a preview for our camera
        this.cameraPreview = new CameraPreview(PhotoCapture.this, this.camera);
        // add the preview to our preview frame
        this.cameraPreviewFrame.addView(this.cameraPreview, 0);
        this.takePictureButton.setEnabled(true);

        //this.camera.getParameters().setJpegQuality(75);
        
        setCameraDisplayOrientation(this, 0 , this.camera);
        
        // optionally, use camera.setPreviewCallback(PreviewCallback) to get
        // each preview frame

        // we also want to configure location details with our camera, but we
        // first need to request location data
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider == null) {
            Log.d(TAG, "No good location provider is available");
        } else {
            Log.d(TAG, "Set the location provider to " + provider);
            this.onLocationChanged(this.locationManager.getLastKnownLocation(provider));
            this.locationManager.requestLocationUpdates(provider, 1000, 100, this);
            // the updates will be given to us via onLocationChanged
        }
    }

    
    
    @Override
    public void onPause() {
        super.onPause();
        if (this.camera != null) {
            this.camera.stopPreview();
            this.camera.release();
            this.camera = null;
            this.cameraPreviewFrame.removeView(this.cameraPreview);
        }
        this.locationManager.removeUpdates(this);
    }

    // gets called by the button press
    public void takePicture(View v) {
    	if(primerShoot){
    		Log.d(TAG, "takePicture()");
            // record the picture as jpeg and notify us when done via onPictureTaken
            this.camera.takePicture(null, null, this);
            primerShoot = false;
    	}
        
    }

    // the data will come back in jpeg format
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.d(TAG, "onPictureTaken()");
        File file =outPutFile;//getFile();
        data=a(data);
        Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data.length);
        bitmap = imageOreintationValidator(bitmap,file.getAbsolutePath());
       // a(bitmap);
        //lo vuelvo a convertir a byte array
        ByteArrayOutputStream  bos = new ByteArrayOutputStream ();
        bitmap.compress(CompressFormat.JPEG, 60, bos);
        byte[] bitmapdata = bos.toByteArray();
        
        try {
            OutputStream out = new FileOutputStream(file);
            try {
                //out.write(data);
            	out.write(bitmapdata);
            } finally {
                out.close();
            }
            Log.d(TAG, "Wrote picture to: " + file.getAbsolutePath());
            Uri uri = Uri.fromFile(file);
            
            Intent intent = new Intent(this, PhotoView.class);
            intent.setData(uri);
            //super.startActivity(intent);
            super.startActivityForResult(intent, ACTIVITY_PHOTOVIEW);
           
        } catch (IOException e) {
            Log.d(TAG, "Failed to save picture", e);
        }
    }
    byte[] a(byte abyte0[])
    {
    	
    	Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(abyte0, 0, abyte0.length), widthIM,heightIM, true);
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 60, bytearrayoutputstream);
        return bytearrayoutputstream.toByteArray();
    }    
    
    @Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
       switch (requestCode) {
    		case ACTIVITY_PHOTOVIEW:{
    			   Bundle res = data.getExtras();
    			   String result = res.getString("result");
    			    primerShoot = true;
    			    Intent resultIntent = new Intent();
    			    resultIntent.putExtra("result", result);
    		        setResult(RESULT_OK, resultIntent);
    		        finish();
    			
    		}
    			break;
    		default:
    			break;
    	}
    	
    }
    
    private Bitmap imageOreintationValidator(Bitmap bitmap, String path) {

    	Display display = getWindowManager().getDefaultDisplay(); 
    	int w = display.getWidth();  // deprecated
    	int h = display.getHeight();  // deprecated
        
        try {
           
	        if(h>w){
	        	bitmap = rotateImage(bitmap, 90);
	        }    
           
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private Bitmap rotateImage(Bitmap source, float angle) {

        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }
    
    

    public void onLocationChanged(Location location) {
        if (this.camera != null && location != null) {
            if (location.hasAccuracy() && location.getAccuracy() < 500
                    && location.getTime() < System.currentTimeMillis() - (30 * 60 * 1000)) {
                Log.d(TAG, "Ignoring inaccurate or stale location: " + location);
            } else {
                Log.d(TAG, "Setting camera location: " + location);
                this.camera.getParameters().setGpsLatitude(location.getLatitude());
                this.camera.getParameters().setGpsLongitude(location.getLongitude());
                this.camera.getParameters().setGpsAltitude(location.getAltitude());
                this.camera.getParameters().setGpsTimestamp(location.getTime());
            }
        } else {
            Log.d(TAG, "No camera or location. Cannot configure the camera.");
        }
    }

    
    public static void setCameraDisplayOrientation(Activity activity,
            int cameraId, android.hardware.Camera camera) {
    	try {
    		android.hardware.Camera.CameraInfo info =
                    new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(cameraId, info);
            int rotation = activity.getWindowManager().getDefaultDisplay()
                    .getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0: degrees = 0; break;
                case Surface.ROTATION_90: degrees = 90; break;
                case Surface.ROTATION_180: degrees = 180; break;
                case Surface.ROTATION_270: degrees = 270; break;
            }
            
            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;  // compensate the mirror
            } else {  // back-facing
                result = (info.orientation - degrees + 360) % 360;
            }
            camera.setDisplayOrientation(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    }
    
   
    
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // ignored
    }

    public void onProviderEnabled(String provider) {
        // ignored
    }

    public void onProviderDisabled(String provider) {
        // ignored
    }
    
    private File getFile() {
        File dir = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), this.getClass().getPackage().getName());
        if (!dir.exists() && !dir.mkdirs()) {
            Log.d(TAG, "Failed to create storage directory");
            return null;
        } else {
            return new File(dir.getAbsolutePath(), new SimpleDateFormat(
                    "'IMG_'yyyyMMddHHmmss'.jpg'").format(new Date()));
        }
    }  
    
    
    
}
