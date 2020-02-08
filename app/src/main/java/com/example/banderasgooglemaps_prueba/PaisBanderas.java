package com.example.banderasgooglemaps_prueba;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import WebServices.Asynchtask;
import WebServices.WebService;

public class PaisBanderas extends AppCompatActivity implements Asynchtask {


    private TextView tvpais;
    private TextView tvcapital;
    private TextView tvcodigo;
    private ImageView ivbandera;

    private String west;
    private String east;
    private String north;
    private String south;
    private String codigoISO;
    private double lat, log;
    public LatLng posicionMap;

    private GoogleMap mapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pais_banderas);

        tvpais = findViewById(R.id.tvPais);
        tvcapital = findViewById(R.id.tvCapital);
        tvcodigo = findViewById(R.id.tvCodigoISO);
        ivbandera = findViewById(R.id.ivBandera);

        //obtener informacion enviada desde un activity
        Bundle bundle = this.getIntent().getExtras();
        ejecutarWS(bundle.getString("codISO"));
    }

    private void ejecutarWS(String dato){
        Map<String, String> datos = new HashMap<String, String>();
        WebService ws= new WebService("http://www.geognos.com/api/en/countries/info/"+dato+".json", datos, PaisBanderas.this, PaisBanderas.this  );
        ws.execute();
    }


    @Override
    public void processFinish(String result) throws JSONException {

        JSONObject jsonObject = new JSONObject(result);
        JSONObject jResults = jsonObject.getJSONObject("Results");
        tvpais.setText(jResults.getString("Name"));
        JSONObject jCapital = jResults.getJSONObject("Capital");
        tvcapital.setText(jCapital.getString("Name"));
        JSONObject jGeoRectangle = jResults.getJSONObject("GeoRectangle");
        west = jGeoRectangle.getString("West");
        east = jGeoRectangle.getString("East");
        north = jGeoRectangle.getString("North");
        south = jGeoRectangle.getString("South");
        JSONArray jGeoPt = jResults.getJSONArray("GeoPt");
        lat = jGeoPt.getDouble(0);
        log = jGeoPt.getDouble(1);
        JSONObject jCountryCodes = jResults.getJSONObject("CountryCodes");
        codigoISO = jCountryCodes.getString("iso2");
        tvcodigo.setText(codigoISO);
        //Cargar imagen desde una URL
        Glide.with(this).load("http://www.geognos.com/api/en/countries/flag/"+codigoISO+".png").into(ivbandera);
        cargarMapa();

    }

    public void cargarMapa(){
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mapa = googleMap;
                posicionMap = new LatLng(lat,log);  //creamos un objeto de latitu y logitud segun los datos obtenidos del ws
                //movimiento de camara
                CameraUpdate camUpd1 = CameraUpdateFactory.newLatLngZoom(posicionMap, 5);
                mapa.moveCamera(camUpd1);
                dibujarRectangulo();
            }
        });//necesita de una implementacion OnMapReadyCallback
    }

    public void dibujarRectangulo(){
        PolylineOptions rectangulo = new PolylineOptions()
                .add(new LatLng(Double.parseDouble(north),Double.parseDouble(west)))
                .add(new LatLng(Double.parseDouble(north),Double.parseDouble(east)))
                .add(new LatLng(Double.parseDouble(south),Double.parseDouble(east)))
                .add(new LatLng(Double.parseDouble(south),Double.parseDouble(west)))
                .add(new LatLng(Double.parseDouble(north),Double.parseDouble(west)));

        rectangulo.width(10);
        rectangulo.color(Color.RED);
        mapa.addPolyline(rectangulo);
    }
}
