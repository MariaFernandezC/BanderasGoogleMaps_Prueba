package com.example.banderasgooglemaps_prueba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import WebServices.Asynchtask;
import WebServices.WebService;

public class MainActivity extends AppCompatActivity implements Asynchtask {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.lvPaises);
        ejecutarWS();

    }

    private void ejecutarWS(){
        Map<String, String> datos = new HashMap<String, String>();
        WebService ws= new WebService("http://www.geognos.com/api/en/countries/info/all.json", datos, MainActivity.this, MainActivity.this  );
        ws.execute();
    }



    @Override
    public void processFinish(String result) throws JSONException {
        parseoWS(result);
    }

    private void parseoWS(String result) throws JSONException{
        List<Paises> lpais = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(result);
        JSONObject jresults = jsonObject.getJSONObject("Results");
        Iterator<?> iterator = jresults.keys();
        while (iterator.hasNext()){
            String key =(String)iterator.next();
            JSONObject jpais = jresults.getJSONObject(key);
            Paises pais = new Paises();
            pais.setNombres(jpais.getString("Name"));
            JSONObject jCountryCodes = jpais.getJSONObject("CountryCodes");
            pais.setCodigoISO(jCountryCodes.getString("iso2"));
            lpais.add(pais);
        }
        //adaptar lisview
        AdaptadorPaises adaptadorrevistas = new AdaptadorPaises(this,lpais);
        listView.setAdapter(adaptadorrevistas);
        //obtener un objeto de la lista al seleccionar
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PaisBanderas.class);
                Bundle b = new Bundle();
                b.putString("codISO", ((Paises)parent.getItemAtPosition(position)).getCodigoISO());
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }


}
