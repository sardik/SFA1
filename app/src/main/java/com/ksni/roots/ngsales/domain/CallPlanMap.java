package com.ksni.roots.ngsales.domain;

/**
 * Created by #roots on 21/09/2015.
 */



import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.CustomerCall;
import com.ksni.roots.ngsales.util.Helper;

public class CallPlanMap extends AppCompatActivity implements OnMapReadyCallback{
    private boolean loaded = false;
    final int RQS_GooglePlayServices = 1;

    Polyline polyline;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_map);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();

        ab.setTitle("Route Maps");

        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);


        FragmentManager myFragmentManager = getSupportFragmentManager();
        SupportMapFragment mySupportMapFragment = (SupportMapFragment) myFragmentManager.findFragmentById(R.id.map);
        //GoogleMap  myMap = mySupportMapFragment.getMap();
        mySupportMapFragment.getMapAsync(this);







        /*
        LatLng caringin = new LatLng(-6.932332, 107.579183);
        LatLng rancaekek = new LatLng(-6.960205, 107.761955);


        MarkerOptions markerJogja = new MarkerOptions();
        markerJogja.position(caringin);
        markerJogja.title("Bandung");
        markerJogja.snippet("Caringin");
        markerJogja.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));


        MarkerOptions markerUgm = new MarkerOptions();
        markerUgm.position(rancaekek);
        markerUgm.title("Rancaekek");
        markerUgm.snippet("Rancaekek");
        markerUgm.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        // setting map
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        myMap.addMarker(markerJogja);
        myMap.addMarker(markerUgm);
        myMap.getUiSettings().setCompassEnabled(true);
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(caringin, 15));

        Location lokasiA = new Location("lokasi_a");
        lokasiA.setLatitude(caringin.latitude);
        lokasiA.setLongitude(caringin.longitude);

        Location lokasiB = new Location("lokasi_b");
        lokasiB.setLatitude(rancaekek.latitude);
        lokasiB.setLongitude(rancaekek.longitude);

        Double distance = (double) lokasiA.distanceTo(lokasiB);
        String jarak = String.valueOf(distance);

        setDirection(caringin, rancaekek);
        */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)         {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void setDirection(GoogleMap map, LatLng jogja, LatLng ugm) {
        // TODO Auto-generated method stub

        if (polyline != null) {
            polyline.remove();
        }

        PolylineOptions rectOptions = new PolylineOptions();
        rectOptions.add(jogja);
        rectOptions.add(ugm);
        rectOptions.width(5);
        rectOptions.color(Color.RED);

        polyline = map.addPolyline(rectOptions);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        final GoogleMap gmap = map;

        if (gmap!=null){
            gmap.setMyLocationEnabled(true);
            //gmap.getMyLocation()

            gmap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location arg0) {
                    if (!loaded){
                    LatLng curLoc = new LatLng(arg0.getLatitude(), arg0.getLongitude());

                    for(CustomerCall cMap:MainActivity.dataCustomerCall) {
                        MarkerOptions markerCustomer = new MarkerOptions();

                        LatLng custLatLang = new LatLng(cMap.getLatitude(), cMap.getLongitude());
                        markerCustomer.position(custLatLang);

                        markerCustomer.title(cMap.getCustomerName());


                        if (cMap.getStatus().equals(CustomerCall.VISIT))
                            markerCustomer.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        else if (cMap.getStatus().equals(CustomerCall.VISITED))
                            markerCustomer.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        else if (cMap.getStatus().equals(CustomerCall.NO_VISIT) && cMap.getCallStatus().equals("1"))
                            markerCustomer.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        else if (cMap.getStatus().equals(CustomerCall.NO_VISIT) && cMap.getCallStatus().equals("0"))
                            markerCustomer.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        else if (cMap.getStatus().equals(CustomerCall.PAUSED))
                            markerCustomer.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));



                        Location lokasiA = new Location("salesman");
                        lokasiA.setLatitude(curLoc.latitude);
                        lokasiA.setLongitude(curLoc.longitude);

                        Location lokasiB = new Location("customer");
                        lokasiB.setLatitude(custLatLang.latitude);
                        lokasiB.setLongitude(custLatLang.longitude);

                        Double distance = (double) lokasiA.distanceTo(lokasiB);

                        //if (distance>1000) distance = distance / 1000;

                        if (distance<1000)
                            markerCustomer.snippet(cMap.getAddress() + " " + Helper.getFormatCurrency(distance) + " Meters");
                        else
                            markerCustomer.snippet(cMap.getAddress() + " " + Helper.getFormatCurrencyWithDigit(distance/1000) + " Kilometers");

                        //markerCustomer.infoWindowAnchor(2400, 2400);


                        //markerCustomer.title(cMap.getCustomerName() + "\n Distance " + Helper.getFormatCurrency(distance)+" Meters");

                        gmap.addMarker(markerCustomer).showInfoWindow();
                        //Marker mark = gmap.addMarker(markerCustomer);
                        //mark.showInfoWindow();

                    }

                        //setDirection(map,curLoc, custLatLang);
                        loaded = true;
                        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 15));


                    }


                }
            });



            //Location cur = map.getMyLocation();
            //SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
            //double lang =  Double.parseDouble(session.getString("LATITUDE", "0"));
            //double longi =  Double.parseDouble(session.getString("LONGITUDE", "0"));

            //LatLng curLoc = new LatLng(cur.getLatitude(), cur.getLongitude());
            //LatLng curLoc = new LatLng(lang, longi);





            gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            gmap.getUiSettings().setCompassEnabled(true);
            gmap.getUiSettings().setZoomControlsEnabled(true);


            //gmap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            //    @Override
            //    public void onCameraChange(CameraPosition cameraPosition) {

            //    }
            //});

        }
    }
}
