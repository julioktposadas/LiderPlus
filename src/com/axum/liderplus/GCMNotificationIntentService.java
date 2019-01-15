package com.axum.liderplus;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;


import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMNotificationIntentService extends IntentService {
	// Sets an ID for the notification, so it can be updated
	public static final int notifyID = 9001;
	NotificationCompat.Builder builder;
	static final String MSG_KEY = "m";

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Enviar error:: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Los mensajes eliminados en el servidor: "
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				sendNotification( extras.get(MSG_KEY).toString());
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg) {
		
	       Intent resultIntent = new Intent(this, MenuPpal.class);
	      
	       String s[]=msg.split(":");
	       if(s[0].equals("v")){
	    	   guardarConfig("v",s[1]);
	    	   
	       }
	       resultIntent.putExtra("msg",msg);
	       resultIntent.putExtra("m","MenuPpal");
	       
	       PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
	               resultIntent, PendingIntent.FLAG_ONE_SHOT);
	        
	        NotificationCompat.Builder mNotifyBuilder;
	        NotificationManager mNotificationManager;
	        
	        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	        
	        mNotifyBuilder = new NotificationCompat.Builder(this)
	                .setContentTitle("Alerta")
	                .setContentText("Has recibido un nuevo mensaje.")
	                .setSmallIcon(R.drawable.ic_launcher);
	        // Set pending intent
	        mNotifyBuilder.setContentIntent(resultPendingIntent);
	        
	        // Set Vibrate, Sound and Light	        
	        int defaults = 0;
	        defaults = defaults | Notification.DEFAULT_LIGHTS;
	        defaults = defaults | Notification.DEFAULT_VIBRATE;
	        defaults = defaults | Notification.DEFAULT_SOUND;
	        
	        mNotifyBuilder.setDefaults(defaults);
	        // Set the content for Notification 
	        mNotifyBuilder.setContentText("Axum VM");
	        // Set autocancel
	        mNotifyBuilder.setAutoCancel(true);
	        // Post a notification
	        mNotificationManager.notify(notifyID, mNotifyBuilder.build());
	}
	

 public String getConfig(String p1){
			SharedPreferences prefs = getSharedPreferences("config",MODE_PRIVATE);
				 return  prefs.getString(p1, "");
}
	 public void guardarConfig(String paramString1, String paramString2)
	 {
	   SharedPreferences.Editor localEditor =getSharedPreferences("config", 0).edit();
	   localEditor.putString(paramString1, paramString2);
	   localEditor.commit();
	   //Toast.makeText(this, "Impresora guardada con exito!!", Toast.LENGTH_LONG).show();
	 }   
}
