package co.edu.univalle.insulinaapp;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    EditText etNombre, etCorreo, etContrasena;
    Button btnRegistrar;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        dbHelper = new DatabaseHelper(this);

        btnRegistrar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            String correo = etCorreo.getText().toString();
            String contrasena = etContrasena.getText().toString();

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("nombre", nombre);
            values.put("correo", correo);
            values.put("contraseña", contrasena);

            long resultado = db.insert("Paciente", null, values);
            if (resultado != -1) {
                Toast.makeText(this, "Registrado correctamente", Toast.LENGTH_SHORT).show();
                finish(); // vuelve al login
            } else {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
