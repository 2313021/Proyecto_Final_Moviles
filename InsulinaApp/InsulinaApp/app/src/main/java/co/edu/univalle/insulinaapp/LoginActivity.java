package co.edu.univalle.insulinaapp;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class LoginActivity extends AppCompatActivity {

    EditText etCorreo, etContrasena;
    Button btnLogin, btnGoRegister;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCorreo = findViewById(R.id.etCorreoLogin);
        etContrasena = findViewById(R.id.etContrasenaLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoRegister = findViewById(R.id.btnGoRegister);
        Button btnSalir = findViewById(R.id.btnSalir);

        dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString();
            String contrasena = etContrasena.getText().toString();

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM Paciente WHERE correo=? AND contraseña=?", new String[]{correo, contrasena});
            if (cursor.moveToFirst()) {
                Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();
                // Puedes guardar el ID o nombre del paciente con SharedPreferences aquí
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        });

        btnSalir.setOnClickListener(v -> {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Confirmación")
                    .setMessage("¿Estás seguro que deseas salir?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        // Cierra la app completamente
                        finishAffinity();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });




        btnGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }
}
