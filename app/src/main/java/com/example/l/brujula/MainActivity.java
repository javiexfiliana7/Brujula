package com.example.l.brujula;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import android.app.Activity;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity implements SensorEventListener {

    Button btnShowLocation;
    TextView textview,textview2;
    GPSTracker gps;

    /*************Brújula************/
    private ImageView image;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    TextView tvHeading;
    /*******************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview = (TextView)findViewById(R.id.textview);
        textview2 = (TextView)findViewById(R.id.textview2);

        /*************Brújula************/
        image = (ImageView) findViewById(R.id.imageViewCompass);// imagen de la brújula
        tvHeading = (TextView) findViewById(R.id.tvHeading);// TextView que mostrará los grados actuales
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//inicializa el sensor
        /*******************************/

        //Obtener la longitud, latitud y dirección al pulsar el botón
        btnShowLocation = (Button) findViewById(R.id.show_location);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gps = new GPSTracker(MainActivity.this);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    textview.setText("Latitud: " + latitude + " Longitud: " + longitude);
                    textview2.setText(getCompleteAddressString(latitude, longitude));

                }
            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // parar el listener cuando este en pausa para ahorrar bateria
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // obtener el angulo con respecto al eje z
        float degree = Math.round(event.values[0]);

        tvHeading.setText("Grados: " + Float.toString(degree));

        // crea una animación de rotación
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);
        // inicia la animación
        image.startAnimation(ra);
        currentDegree = -degree;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //obtener la dirección a partir de la latitud y longitud
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(" ");
                }
                strAdd = strReturnedAddress.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return strAdd;
    }
}
