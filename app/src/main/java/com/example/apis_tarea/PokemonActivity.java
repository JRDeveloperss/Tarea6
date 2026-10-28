package com.example.apis_tarea;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

public class PokemonActivity extends AppCompatActivity {

    EditText etPokemon;
    Button btnBuscarPokemon, btnSonidoPokemon;
    ImageView imgPokemon;
    TextView tvNombrePokemon, tvExperiencia, tvHabilidades;

    RequestQueue colaSolicitudes;
    MediaPlayer reproductor;
    String urlSonido = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        etPokemon = findViewById(R.id.etPokemon);
        btnBuscarPokemon = findViewById(R.id.btnBuscarPokemon);
        btnSonidoPokemon = findViewById(R.id.btnSonidoPokemon);
        imgPokemon = findViewById(R.id.imgPokemon);
        tvNombrePokemon = findViewById(R.id.tvNombrePokemon);
        tvExperiencia = findViewById(R.id.tvExperiencia);
        tvHabilidades = findViewById(R.id.tvHabilidades);

        colaSolicitudes = Volley.newRequestQueue(this);

        btnBuscarPokemon.setOnClickListener(v -> buscarPokemon());

        btnSonidoPokemon.setOnClickListener(v -> reproducirSonido());

        etPokemon.setText("pikachu");
        buscarPokemon();
    }

    private void buscarPokemon() {
        String nombre = etPokemon.getText().toString()
                .trim().toLowerCase();

        if (nombre.isEmpty()) {
            etPokemon.setError("Escribe un nombre o número");
            return;
        }

        String url = "https://pokeapi.co/api/v2/pokemon/" + nombre;

        btnBuscarPokemon.setEnabled(false);
        btnSonidoPokemon.setEnabled(false);
        tvNombrePokemon.setText("Buscando Pokémon...");
        tvExperiencia.setText("Experiencia base: --");
        tvHabilidades.setText("Habilidades: --");
        imgPokemon.setImageDrawable(null);
        urlSonido = "";

        if (reproductor != null) {
            reproductor.release();
            reproductor = null;
        }

        JsonObjectRequest solicitud = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                respuesta -> {
                    btnBuscarPokemon.setEnabled(true);

                    try {
                        String nombreReal = respuesta.getString("name");
                        nombreReal = nombreReal.substring(0, 1).toUpperCase()
                                + nombreReal.substring(1);

                        int experiencia =
                                respuesta.getInt("base_experience");

                        JSONArray habilidadesJson =
                                respuesta.getJSONArray("abilities");

                        StringBuilder habilidades = new StringBuilder();

                        for (int i = 0; i < habilidadesJson.length(); i++) {
                            JSONObject elemento =
                                    habilidadesJson.getJSONObject(i);

                            JSONObject habilidad =
                                    elemento.getJSONObject("ability");

                            if (i > 0) {
                                habilidades.append(", ");
                            }

                            habilidades.append(
                                    habilidad.getString("name").replace("-", " ")
                            );
                        }

                        JSONObject sprites =
                                respuesta.getJSONObject("sprites");

                        JSONObject artwork =
                                sprites.getJSONObject("other")
                                        .getJSONObject("official-artwork");

                        String urlImagen =
                                artwork.optString("front_default", "");

                        if (urlImagen.isEmpty()) {
                            urlImagen = sprites.optString("front_default", "");
                        }

                        if (!urlImagen.isEmpty()) {
                            Glide.with(PokemonActivity.this)
                                    .load(urlImagen)
                                    .into(imgPokemon);
                        }

                        JSONObject cries =
                                respuesta.optJSONObject("cries");

                        if (cries != null) {
                            urlSonido = cries.optString("latest", "");
                        }

                        tvNombrePokemon.setText("Nombre: " + nombreReal);
                        tvExperiencia.setText(
                                "Experiencia base: " + experiencia
                        );
                        tvHabilidades.setText(
                                "Habilidades: " + habilidades
                        );

                        btnSonidoPokemon.setEnabled(!urlSonido.isEmpty());

                    } catch (Exception e) {
                        tvNombrePokemon.setText(
                                "No se pudieron leer los datos."
                        );
                    }
                },
                error -> {
                    btnBuscarPokemon.setEnabled(true);
                    tvNombrePokemon.setText(
                            "No se encontró el Pokémon."
                    );

                    Toast.makeText(
                            this,
                            "Verifica el nombre o número e inténtalo otra vez.",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        colaSolicitudes.add(solicitud);
    }

    private void reproducirSonido() {
        if (urlSonido.isEmpty()) {
            Toast.makeText(this, "No hay sonido disponible.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (reproductor != null) {
            reproductor.release();
            reproductor = null;
        }

        reproductor = new MediaPlayer();

        try {
            reproductor.setDataSource(urlSonido);
            reproductor.setOnPreparedListener(MediaPlayer::start);
            reproductor.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(this, "No se pudo reproducir el sonido.",
                        Toast.LENGTH_SHORT).show();
                return true;
            });
            reproductor.prepareAsync();

        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar el sonido.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (reproductor != null) {
            reproductor.release();
            reproductor = null;
        }

        super.onDestroy();
    }
}
