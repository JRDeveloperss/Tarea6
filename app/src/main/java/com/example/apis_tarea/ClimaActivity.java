package com.example.apis_tarea;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class ClimaActivity extends AppCompatActivity {

    TextView tvTemperatura, tvDescripcionClima, tvFechaClima;
    TextView tvSensacion, tvHumedad, tvViento, tvMaxMin;
    Button btnActualizarClima;

    RequestQueue colaSolicitudes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clima);

        tvTemperatura = findViewById(R.id.tvTemperatura);
        tvDescripcionClima = findViewById(R.id.tvDescripcionClima);
        tvFechaClima = findViewById(R.id.tvFechaClima);
        tvSensacion = findViewById(R.id.tvSensacion);
        tvHumedad = findViewById(R.id.tvHumedad);
        tvViento = findViewById(R.id.tvViento);
        tvMaxMin = findViewById(R.id.tvMaxMin);
        btnActualizarClima = findViewById(R.id.btnActualizarClima);

        colaSolicitudes = Volley.newRequestQueue(this);

        btnActualizarClima.setOnClickListener(v -> consultarClima());

        consultarClima();
    }

    private void consultarClima() {
        String url = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=18.4861"
                + "&longitude=-69.9312"
                + "&current=temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m"
                + "&daily=temperature_2m_max,temperature_2m_min"
                + "&timezone=America%2FSanto_Domingo";

        tvDescripcionClima.setText("Consultando el clima...");

        JsonObjectRequest solicitud = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                respuesta -> {
                    try {
                        JSONObject actual = respuesta.getJSONObject("current");
                        JSONObject diario = respuesta.getJSONObject("daily");

                        double temperatura = actual.getDouble("temperature_2m");
                        double sensacion = actual.getDouble("apparent_temperature");
                        int humedad = actual.getInt("relative_humidity_2m");
                        double viento = actual.getDouble("wind_speed_10m");
                        int codigoClima = actual.getInt("weather_code");

                        JSONArray fechas = diario.getJSONArray("time");
                        JSONArray maximas = diario.getJSONArray("temperature_2m_max");
                        JSONArray minimas = diario.getJSONArray("temperature_2m_min");

                        String fecha = fechas.getString(0);
                        double maxima = maximas.getDouble(0);
                        double minima = minimas.getDouble(0);

                        tvTemperatura.setText(String.format("%.1f °C", temperatura));
                        tvDescripcionClima.setText(describirClima(codigoClima));
                        tvFechaClima.setText("Fecha: " + fecha);
                        tvSensacion.setText(String.format("Sensación térmica: %.1f °C", sensacion));
                        tvHumedad.setText("Humedad: " + humedad + "%");
                        tvViento.setText(String.format("Viento: %.1f km/h", viento));
                        tvMaxMin.setText(String.format(
                                "Máxima: %.1f °C  |  Mínima: %.1f °C",
                                maxima, minima
                        ));

                    } catch (Exception e) {
                        tvDescripcionClima.setText("No se pudieron leer los datos del clima.");
                    }
                },
                error -> {
                    tvDescripcionClima.setText("No se pudo consultar el clima.");
                    Toast.makeText(this,
                            "Revisa tu conexión a Internet e inténtalo otra vez.",
                            Toast.LENGTH_LONG).show();
                }
        );

        colaSolicitudes.add(solicitud);
    }

    private String describirClima(int codigo) {
        if (codigo == 0) return "Despejado";
        if (codigo == 1) return "Mayormente despejado";
        if (codigo == 2) return "Parcialmente nublado";
        if (codigo == 3) return "Nublado";
        if (codigo == 45 || codigo == 48) return "Niebla";
        if (codigo >= 51 && codigo <= 57) return "Llovizna";
        if (codigo >= 61 && codigo <= 67) return "Lluvia";
        if (codigo >= 71 && codigo <= 77) return "Nevada";
        if (codigo >= 80 && codigo <= 82) return "Chubascos";
        if (codigo >= 85 && codigo <= 86) return "Chubascos de nieve";
        if (codigo >= 95) return "Tormenta eléctrica";
        return "Condición meteorológica variable";
    }
}
