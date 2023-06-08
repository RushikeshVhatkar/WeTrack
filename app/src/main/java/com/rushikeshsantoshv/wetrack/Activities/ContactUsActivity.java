package com.rushikeshsantoshv.wetrack.Activities;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

public class ContactUsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mapAPI;
    private SupportMapFragment mapFragment;
    private ImageButton contactus_back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        new Functions().coloredStatusBarDarkTextDesign(ContactUsActivity.this, R.color.maincolor_light, R.color.white);
        contactus_back_btn = findViewById(R.id.contactus_back_btn);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googlemap_api);

        contactus_back_btn.setOnClickListener(v -> finish());
        mapFragment.getMapAsync(ContactUsActivity.this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapAPI = googleMap;
        LatLng mappoints = new LatLng(9.559701767388233, 76.78681761188922);
        mapAPI.addMarker(new MarkerOptions().position(mappoints).title("Kanjirapally, Kerala"));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLngZoom(mappoints, 13F));

    }
}