
package com.example.apis_tarea;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnGenero;
    Button btnUsuario;
    Button btnUniversidades;
    Button btnClima;
    Button btnPokemon;
    Button btnNoticias;
    Button btnAcercaDe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGenero = findViewById(R.id.btnGenero);
        btnUsuario = findViewById(R.id.btnUsuario);
        btnUniversidades = findViewById(R.id.btnUniversidades);
        btnClima = findViewById(R.id.btnClima);
        btnPokemon = findViewById(R.id.btnPokemon);
        btnNoticias = findViewById(R.id.btnNoticias);
        btnAcercaDe = findViewById(R.id.btnAcercaDe);

        btnGenero.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GeneroActivity.class);
            startActivity(intent);
        });

        btnUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    UsuarioActivity.class
            );
            startActivity(intent);
        });

        btnUniversidades.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    UniversidadesActivity.class
            );
            startActivity(intent);
        });

        btnClima.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ClimaActivity.class);
            startActivity(intent);
        });

        btnPokemon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PokemonActivity.class);
            startActivity(intent);
        });

        btnNoticias.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    NoticiasActivity.class
            );
            startActivity(intent);
        });

        btnAcercaDe.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AcercaDeActivity.class
            );
            startActivity(intent);
        });
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
