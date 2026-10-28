
package com.example.apis_tarea;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AcercaDeActivity extends AppCompatActivity {

    Button btnCorreo;

    // Cambia este valor por tu correo profesional.
    String correo = "tu_correo@ejemplo.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca_de);

        btnCorreo = findViewById(R.id.btnCorreo);

        btnCorreo.setOnClickListener(v -> {
            Intent intent = new Intent(
                    Intent.ACTION_SENDTO,
                    Uri.parse("mailto:" + correo)
            );

            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(
                        this,
                        "No hay una aplicación de correo configurada.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
