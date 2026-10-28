package com.example.apis_tarea;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;

import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;

public class UniversidadesActivity extends AppCompatActivity {

    EditText etPais;
    Button btnBuscarUniversidades;
    TextView tvEstadoUniversidades;
    LinearLayout layoutListaUniversidades;

    RequestQueue colaSolicitudes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universidades);

        etPais = findViewById(R.id.etPais);
        btnBuscarUniversidades = findViewById(R.id.btnBuscarUniversidades);
        tvEstadoUniversidades = findViewById(R.id.tvEstadoUniversidades);
        layoutListaUniversidades =
                findViewById(R.id.layoutListaUniversidades);

        colaSolicitudes = Volley.newRequestQueue(this);

        btnBuscarUniversidades.setOnClickListener(v -> buscarUniversidades());
    }


    private void buscarUniversidades() {

        String pais = etPais.getText().toString().trim();

        if (pais.isEmpty()) {
            etPais.setError("Escribe el nombre de un país");
            return;
        }

        String paisCodificado;

        try {
            paisCodificado = URLEncoder.encode(pais, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            tvEstadoUniversidades.setText("Error al preparar el país.");
            return;
        }

        String url = "https://adamix.net/proxy.php?country=" + paisCodificado;

        btnBuscarUniversidades.setEnabled(false);
        tvEstadoUniversidades.setText("Buscando universidades...");
        layoutListaUniversidades.removeAllViews();

        JsonArrayRequest solicitud = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                universidades -> {

                    btnBuscarUniversidades.setEnabled(true);

                    if (universidades.length() == 0) {
                        tvEstadoUniversidades.setText(
                                "No se encontraron universidades."
                        );
                        return;
                    }

                    tvEstadoUniversidades.setText(
                            "Universidades encontradas: " + universidades.length()
                    );

                    for (int i = 0; i < universidades.length(); i++) {

                        try {
                            JSONObject universidad =
                                    universidades.getJSONObject(i);

                            String nombre =
                                    universidad.optString("name", "Sin nombre");

                            JSONArray dominios =
                                    universidad.optJSONArray("domains");

                            JSONArray paginas =
                                    universidad.optJSONArray("web_pages");

                            String dominio = "No disponible";
                            String pagina = "";

                            if (dominios != null && dominios.length() > 0) {
                                dominio = dominios.optString(0, "No disponible");
                            }

                            if (paginas != null && paginas.length() > 0) {
                                pagina = paginas.optString(0, "");
                            }

                            agregarUniversidad(nombre, dominio, pagina);

                        } catch (Exception e) {
                            // Si un registro está incompleto, continúa con el siguiente.
                        }
                    }
                },
                error -> {

                    btnBuscarUniversidades.setEnabled(true);

                    tvEstadoUniversidades.setText(
                            "Error al consultar las universidades."
                    );

                    String detalle = error.networkResponse == null
                            ? "Sin respuesta del servidor."
                            : "Código HTTP: " + error.networkResponse.statusCode;

                    Toast.makeText(
                            this,
                            detalle,
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        colaSolicitudes.add(solicitud);
    }



    private void agregarUniversidad(
            String nombre, String dominio, String pagina) {

        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(20, 16, 20, 16);
        tarjeta.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams parametros =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametros.setMargins(0, 0, 0, 16);
        tarjeta.setLayoutParams(parametros);

        TextView tvNombre = new TextView(this);
        tvNombre.setText(nombre);
        tvNombre.setTextSize(18);
        tvNombre.setTypeface(null, Typeface.BOLD);
        tvNombre.setTextColor(Color.rgb(32, 79, 71));

        TextView tvDominio = new TextView(this);
        tvDominio.setText("Dominio: " + dominio);
        tvDominio.setTextSize(15);

        TextView tvPagina = new TextView(this);
        tvPagina.setText(
                pagina.isEmpty() ? "Página web no disponible"
                        : "Visitar página web"
        );
        tvPagina.setTextSize(15);
        tvPagina.setTextColor(Color.rgb(21, 101, 192));

        if (!pagina.isEmpty()) {
            tvPagina.setOnClickListener(v -> abrirPagina(pagina));
        }

        tarjeta.addView(tvNombre);
        tarjeta.addView(tvDominio);
        tarjeta.addView(tvPagina);

        layoutListaUniversidades.addView(tarjeta);
    }

    private void abrirPagina(String pagina) {

        if (!pagina.startsWith("https://")
                && !pagina.startsWith("http://")) {
            pagina = "https://" + pagina;
        }

        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(pagina)
        );

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "No se pudo abrir la página web",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
