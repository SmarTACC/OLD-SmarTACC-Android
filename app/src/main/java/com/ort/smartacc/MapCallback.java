package com.ort.smartacc;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapCallback implements OnMapReadyCallback{

    Activity activity;

    public MapCallback(Activity activity){
        this.activity=activity;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng buenosAires = new LatLng(-34.5573429,-58.4594648);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(buenosAires, 14.0f));

        googleMap.setInfoWindowAdapter(new CustomInfoWindow(activity));

        SQLiteDatabase db =new SQLiteHelper(activity,SQLiteHelper.getVersion(activity))
                .getReadableDatabase();
        Cursor c = db.query("lugares", null, null, null, null, null, null);

        while(c.moveToNext()){
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(c.getDouble(c.getColumnIndex("lat")), c.getDouble(c.getColumnIndex("lon"))))
                    .title(c.getString(c.getColumnIndex("name")))
                    .snippet(c.getString(c.getColumnIndex("address")) + "\n"+ c.getString(c.getColumnIndex("description"))));
            //+"<br>Dirección: "+ c.getString(c.getColumnIndex("address")) + "<br>Descripción: "+ c.getString(c.getColumnIndex("description")
        }

        c.close();
    }

    class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {
        Activity activity;
        CustomInfoWindow(Activity activity) {
            this.activity=activity;
        }
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View infoWindow=activity.getLayoutInflater().inflate(R.layout.info_window, null);

            TextView name=(TextView)infoWindow.findViewById(R.id.info_window_name);
            name.setText(marker.getTitle());
            String[] snippet = marker.getSnippet().split("\n");
            TextView address=(TextView)infoWindow.findViewById(R.id.info_window_address);
            address.setText(String.format(activity.getResources().getString(R.string.mapa_info_direccion), snippet[0]));
            if(snippet.length>1) {
                TextView description = (TextView) infoWindow.findViewById(R.id.info_window_description);
                description.setText(String.format(activity.getResources().getString(R.string.mapa_info_direccion), snippet[1]));
            }
            return(infoWindow);
        }
    }
}
