package com.example.admin.mapdistanceproject;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button btnDistance;
    private double lat1,lng1,lat2,lng2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        btnDistance = (Button)findViewById(R.id.btn_distance);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        btnDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("tag","btn onclick");
                if((lat1 != 0) && (lng1 != 0) && (lat2 != 0) && (lng2 != 0)) {
                    new GetDistance(lat1, lng1, lat2, lng2).execute();
                }else{
                    Toast.makeText(MapActivity.this,"click any area to find distance",Toast.LENGTH_LONG).show();
                }
            }
        });




    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        Log.e("tag","============onMapReady============");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        if(mMap != null) {
            mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        }


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                Log.e("tag", "onclick");

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);


                // Setting the title for the marker.
                // This will be displayed on taping the marker

                lat2 = latLng.latitude;
                lng2 = latLng.longitude;
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                // Clears the previously touched position
                mMap.clear();

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                mMap.addMarker(markerOptions);

              /*  new GetDistance(latLng.latitude,latLng.longitude,11.953349,79.819793).execute();*/

                //getDistance(latLng.latitude,latLng.longitude,11.953349,79.819793);

            }
        });

    /*    if(mMap != null){
            mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        }
*/



        // Add a marker in Sydney, Australia, and move the camera.
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.setMyLocationEnabled(true)
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            Marker  mMarker = mMap.addMarker(new MarkerOptions().position(loc));
           /* if(mMap != null){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 5.0f));
            }*/
            lat1 = loc.latitude;
            lng1 = loc.longitude;
           // Log.e("tag","========lat========:"+lat1+"  ============lng============:"+lng1);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));

        }
    };




    public class GetDistance extends AsyncTask<Void, Void, String>{
        ProgressDialog progressDialog;
        double lat1,lng1,lat2,lng2;
        String parsedDistance,parsedDuration;
        String response;

        public GetDistance(double latitude, double longitude, double lat2, double lng2) {
            this.lat1 = latitude;
            this.lng1 = longitude;
            this.lat2 = lat2;
            this.lng2 = lng2;
        }




        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MapActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lng1 + "&destination=" + lat2 + "," + lng2 + "&sensor=false&units=metric&mode=driving");
                Log.e("tag","===========url============="+url);
                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                InputStream in = new BufferedInputStream(conn.getInputStream());
                response = IOUtils.toString(in, "UTF-8");

                JSONObject jsonObject = new JSONObject(response);
                MapLog.debug("tag","=======map response====="+response);
                //     Log.e("tag","==========response========="+response);
                JSONArray array = jsonObject.getJSONArray("routes");
                JSONObject routes = array.getJSONObject(0);
                JSONArray legs = routes.getJSONArray("legs");
                JSONObject steps = legs.getJSONObject(0);
                JSONObject distance = steps.getJSONObject("distance");
                parsedDistance=distance.getString("text");
                Log.e("tag"," ==========parsedDistance[0]===========:"+ parsedDistance);
                JSONObject duration =steps.getJSONObject("duration");
                parsedDuration =duration.getString("text");
                Log.e("tag"," ==========parsedDuration===========:"+ parsedDuration);

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return parsedDistance+":"+parsedDuration;

        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            Log.d("tag","============S========="+s);

            String[] distdura = s.split(":");
            Toast.makeText(MapActivity.this,"Distance :"+distdura[0]+"\nDuration:"+distdura[1],Toast.LENGTH_LONG).show();
        }
    }


    public void getDistance(final double lat1, final double lon1, final double lat2, final double lon2){

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String parsedDistance;
                    String response;

                    URL url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&sensor=false&units=metric&mode=driving");
                    Log.e("tag","===========url============="+url);
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = IOUtils.toString(in, "UTF-8");

                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("tag","==========response========="+response);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    parsedDistance=distance.getString("text");
                    Log.e("tag"," ==========parsedDistance[0]===========:"+ parsedDistance);
                    Toast.makeText(MapActivity.this,"Distance :"+parsedDistance,Toast.LENGTH_LONG).show();

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //  return parsedDistance;
    }
}