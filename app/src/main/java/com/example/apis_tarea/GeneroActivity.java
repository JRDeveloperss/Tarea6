package com.example.apis_tarea;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class GeneroActivity extends AppCompatActivity {

    EditText etNombre;
    Button btnPredecir;
    TextView tvResultado;
    LinearLayout layoutGenero;

    RequestQueue colaSolicitudes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genero);

        etNombre = findViewById(R.id.etNombre);
        btnPredecir = findViewById(R.id.btnPredecir);
        tvResultado = findViewById(R.id.tvResultado);
        layoutGenero = findViewById(R.id.layoutGenero);

        colaSolicitudes = Volley.newRequestQueue(this);

        btnPredecir.setOnClickListener(v -> predecirGenero());
    }

    private void predecirGenero() {

        String nombre = etNombre.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombre.setError("Escribe un nombre");
            return;
        }

        String url = "https://api.genderize.io/?name=" + nombre.replace(" ", "%20");

        btnPredecir.setEnabled(false);
        tvResultado.setText("Consultando...");

        JsonObjectRequest solicitud = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                respuesta -> {

                    btnPredecir.setEnabled(true);

                    try {
                        String genero = respuesta.optString("gender", "");
                        double probabilidad = respuesta.optDouble("probability", 0.0);

                        if (genero.equals("male")) {

                            layoutGenero.setBackgroundColor(Color.rgb(187, 222, 251));

                            tvResultado.setText(
                                    "Nombre: " + nombre +
                                            "\nGénero estimado: Masculino" +
                                            "\nProbabilidad: " + (int) (probabilidad * 100) + "%"
                            );

                        } else if (genero.equals("female")) {

                            layoutGenero.setBackgroundColor(Color.rgb(248, 187, 208));

                            tvResultado.setText(
                                    "Nombre: " + nombre +
                                            "\nGénero estimado: Femenino" +
                                            "\nProbabilidad: " + (int) (probabilidad * 100) + "%"
                            );

                        } else {

                            layoutGenero.setBackgroundColor(Color.rgb(245, 245, 245));

                            tvResultado.setText(
                                    "No se pudo determinar el género para ese nombre."
                            );
                        }

                    } catch (Exception e) {
                        tvResultado.setText("Error al procesar la respuesta.");
                    }
                },

                error -> {
                    String mensaje;

                    if (error.networkResponse != null) {
                        int codigo = error.networkResponse.statusCode;

                        if (codigo == 429) {
                            mensaje = "La API alcanzó su límite de consultas. Intenta más tarde.";
                        } else if (codigo == 401) {
                            mensaje = "La API requiere revisar la autenticación.";
                        } else if (codigo >= 500) {
                            mensaje = "El servidor de la API está teniendo problemas.";
                        } else {
                            mensaje = "Error HTTP: " + codigo;
                        }
                    } else {
                        mensaje = "No se recibió respuesta. Revisa Internet o intenta más tarde.";
                    }

                    Toast.makeText(
                            GeneroActivity.this,
                            mensaje,
                            Toast.LENGTH_LONG
                    ).show();
                }

        );

        colaSolicitudes.add(solicitud);
    }
}
