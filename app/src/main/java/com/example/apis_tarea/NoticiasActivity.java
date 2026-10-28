package com.example.apis_tarea;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

public class NoticiasActivity extends AppCompatActivity {

    ImageView imgLogoKinsta;
    TextView tvEstadoNoticias;
    LinearLayout layoutListaNoticias;
    Button btnActualizarNoticias, btnVisitarKinsta;

    RequestQueue colaSolicitudes;

    String urlSitio = "https://kinsta.com/";
    String urlApi = "https://kinsta.com/wp-json/wp/v2/posts"
            + "?per_page=3&_fields=title,excerpt,link";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticias);

        imgLogoKinsta = findViewById(R.id.imgLogoKinsta);
        tvEstadoNoticias = findViewById(R.id.tvEstadoNoticias);
        layoutListaNoticias = findViewById(R.id.layoutListaNoticias);
        btnActualizarNoticias = findViewById(R.id.btnActualizarNoticias);
        btnVisitarKinsta = findViewById(R.id.btnVisitarKinsta);

        colaSolicitudes = Volley.newRequestQueue(this);

        // Cargar el icono del sitio web.
        Glide.with(this)
                .load("https://www.google.com/s2/favicons?domain=kinsta.com&sz=128")
                .into(imgLogoKinsta);

        btnActualizarNoticias.setOnClickListener(v -> cargarNoticias());

        btnVisitarKinsta.setOnClickListener(v -> abrirPagina(urlSitio));

        cargarNoticias();
    }

    private void cargarNoticias() {
        btnActualizarNoticias.setEnabled(false);
        tvEstadoNoticias.setText("Cargando publicaciones...");
        layoutListaNoticias.removeAllViews();

        JsonArrayRequest solicitud = new JsonArrayRequest(
                Request.Method.GET,
                urlApi,
                null,
                publicaciones -> {
                    btnActualizarNoticias.setEnabled(true);

                    if (publicaciones.length() == 0) {
                        tvEstadoNoticias.setText(
                                "No se encontraron publicaciones."
                        );
                        return;
                    }

                    tvEstadoNoticias.setText(
                            "Publicaciones recientes: " + publicaciones.length()
                    );

                    for (int i = 0; i < publicaciones.length(); i++) {
                        try {
                            JSONObject publicacion =
                                    publicaciones.getJSONObject(i);

                            JSONObject tituloJson =
                                    publicacion.getJSONObject("title");

                            JSONObject extractoJson =
                                    publicacion.getJSONObject("excerpt");

                            String titulo = Html.fromHtml(
                                    tituloJson.optString("rendered", "Sin título"),
                                    Html.FROM_HTML_MODE_LEGACY
                            ).toString().trim();

                            String extracto = Html.fromHtml(
                                    extractoJson.optString("rendered", ""),
                                    Html.FROM_HTML_MODE_LEGACY
                            ).toString().trim();

                            String enlace = publicacion.optString("link", urlSitio);

                            agregarPublicacion(titulo, extracto, enlace);

                        } catch (Exception e) {
                            // Continúa con la siguiente publicación.
                        }
                    }
                },
                error -> {
                    btnActualizarNoticias.setEnabled(true);
                    tvEstadoNoticias.setText(
                            "No se pudieron cargar las publicaciones."
                    );

                    Toast.makeText(
                            this,
                            "Verifica tu conexión o inténtalo más tarde.",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        colaSolicitudes.add(solicitud);
    }

    private void agregarPublicacion(
            String titulo, String extracto, String enlace) {

        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(16, 16, 16, 16);
        tarjeta.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams parametros =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametros.setMargins(0, 0, 0, 20);
        tarjeta.setLayoutParams(parametros);

        TextView tvTitulo = new TextView(this);
        tvTitulo.setText(titulo);
        tvTitulo.setTextSize(19);
        tvTitulo.setTextColor(Color.rgb(65, 45, 170));
        tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvExtracto = new TextView(this);
        tvExtracto.setText(extracto.isEmpty()
                ? "No hay extracto disponible."
                : extracto);
        tvExtracto.setTextSize(15);
        tvExtracto.setTextColor(Color.DKGRAY);
        tvExtracto.setPadding(0, 10, 0, 10);

        TextView tvEnlace = new TextView(this);
        tvEnlace.setText("Leer publicación");
        tvEnlace.setTextSize(16);
        tvEnlace.setTextColor(Color.BLUE);
        tvEnlace.setPaintFlags(
                tvEnlace.getPaintFlags()
                        | android.graphics.Paint.UNDERLINE_TEXT_FLAG
        );
        tvEnlace.setOnClickListener(v -> abrirPagina(enlace));

        tarjeta.addView(tvTitulo);
        tarjeta.addView(tvExtracto);
        tarjeta.addView(tvEnlace);

        layoutListaNoticias.addView(tarjeta);
    }

    private void abrirPagina(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
