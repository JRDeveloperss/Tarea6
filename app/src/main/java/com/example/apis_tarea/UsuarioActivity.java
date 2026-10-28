package com.example.apis_tarea;


import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

public class UsuarioActivity extends AppCompatActivity {

    ImageView imgUsuario;
    TextView tvNombreUsuario, tvNacimiento, tvEdad;
    Button btnNuevoUsuario;

    RequestQueue colaSolicitudes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        imgUsuario = findViewById(R.id.imgUsuario);
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        tvNacimiento = findViewById(R.id.tvNacimiento);
        tvEdad = findViewById(R.id.tvEdad);
        btnNuevoUsuario = findViewById(R.id.btnNuevoUsuario);

        colaSolicitudes = Volley.newRequestQueue(this);

        cargarUsuario();

        btnNuevoUsuario.setOnClickListener(v -> cargarUsuario());
    }

    private void cargarUsuario() {

        btnNuevoUsuario.setEnabled(false);
        tvNombreUsuario.setText("Cargando usuario...");

        String url = "https://randomuser.me/api/?results=1";

        JsonObjectRequest solicitud = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                respuesta -> {
                    btnNuevoUsuario.setEnabled(true);

                    try {
                        JSONArray resultados =
                                respuesta.getJSONArray("results");

                        JSONObject usuario =
                                resultados.getJSONObject(0);

                        JSONObject nombre =
                                usuario.getJSONObject("name");

                        String nombreCompleto =
                                nombre.getString("first") + " "
                                        + nombre.getString("last");

                        JSONObject nacimiento =
                                usuario.getJSONObject("dob");

                        String fecha =
                                nacimiento.getString("date").substring(0, 10);

                        int edad = nacimiento.getInt("age");

                        JSONObject fotografia =
                                usuario.getJSONObject("picture");

                        String urlFoto =
                                fotografia.getString("large");

                        tvNombreUsuario.setText(nombreCompleto);
                        tvNacimiento.setText(
                                "Fecha de nacimiento: " + fecha
                        );
                        tvEdad.setText("Edad: " + edad + " años");

                        Glide.with(this)
                                .load(urlFoto)
                                .placeholder(
                                        android.R.drawable.ic_menu_gallery
                                )
                                .error(
                                        android.R.drawable.ic_menu_report_image
                                )
                                .into(imgUsuario);

                    } catch (Exception e) {
                        tvNombreUsuario.setText(
                                "No se pudieron leer los datos."
                        );
                    }
                },
                error -> {
                    btnNuevoUsuario.setEnabled(true);
                    tvNombreUsuario.setText(
                            "No se pudo consultar la API."
                    );

                    Toast.makeText(
                            this,
                            "Comprueba tu conexión a Internet",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        colaSolicitudes.add(solicitud);
    }
}
