package co.edu.univalle.insulinaapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;

public class MainActivity extends AppCompatActivity {

    Button btnNuevaEntrada, btnVerRegistros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNuevaEntrada = findViewById(R.id.btnNuevaEntrada);
        btnVerRegistros = findViewById(R.id.btnVerRegistros);

        btnNuevaEntrada.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, NewEntryActivity.class));
        });

        btnVerRegistros.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ViewRecordsActivity.class));
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Confirmación")
                .setMessage("¿Deseas salir de la aplicación?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    finishAffinity(); // Cierra toda la app
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


}
