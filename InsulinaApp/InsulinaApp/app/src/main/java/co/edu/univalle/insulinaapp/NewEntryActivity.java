package co.edu.univalle.insulinaapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.*;
import android.app.AlertDialog;


public class NewEntryActivity extends AppCompatActivity {

    Spinner spTipoComida, spAlimento;
    EditText etGlicemia, etCantidad;
    Button btnGuardar;
    TextView tvResultado;

    DatabaseHelper dbHelper;
    int pacienteId = 1; // asumiendo paciente logueado con ID 1
    Map<String, Integer> tipoComidaMap = new HashMap<>();
    Map<String, Integer> alimentoMap = new HashMap<>();
    Map<Integer, Float> choPorcionMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        dbHelper = new DatabaseHelper(this);

        spTipoComida = findViewById(R.id.spTipoComida);
        spAlimento = findViewById(R.id.spAlimento);
        etGlicemia = findViewById(R.id.etGlicemia);
        etCantidad = findViewById(R.id.etCantidad);
        btnGuardar = findViewById(R.id.btnGuardar);
        tvResultado = findViewById(R.id.tvResultado);

        cargarTiposComida();
        cargarAlimentos();

        btnGuardar.setOnClickListener(v -> {
            calcularYGuardar();
        });


        }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(NewEntryActivity.this)
                .setTitle("Confirmación")
                .setMessage("¿Deseas volver al menú principal?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    // Llamar super en lugar de finish()
                    super.onBackPressed();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }




    void cargarTiposComida() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT tipo_id, descripcion FROM TipoComida", null);
        List<String> lista = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nombre = cursor.getString(1);
            lista.add(nombre);
            tipoComidaMap.put(nombre, id);
        }
        cursor.close();
        spTipoComida.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lista));
    }

    void cargarAlimentos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT p.porcion_id, a.nombre || ' - ' || p.descripcion, p.cho_g FROM Porcion p JOIN Alimento a ON a.alimento_id = p.alimento_id", null);
        List<String> lista = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nombre = cursor.getString(1);
            float cho = cursor.getFloat(2);
            lista.add(nombre);
            alimentoMap.put(nombre, id);
            choPorcionMap.put(id, cho);
        }
        cursor.close();
        spAlimento.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lista));
    }

    void calcularYGuardar() {
        try {
            int glicemia = Integer.parseInt(etGlicemia.getText().toString());
            float cantidad = Float.parseFloat(etCantidad.getText().toString());

            String tipoComidaStr = spTipoComida.getSelectedItem().toString();
            int tipoId = tipoComidaMap.get(tipoComidaStr);
            int ratio = (tipoId == 2) ? 12 : 15;

            String alimentoStr = spAlimento.getSelectedItem().toString();
            int porcionId = alimentoMap.get(alimentoStr);
            float cho = choPorcionMap.get(porcionId);

            float totalCHOs = cantidad * cho;
            float IA = totalCHOs / ratio;

            int FC = 0;
            if (glicemia >= 70 && glicemia <= 100) FC = -1;
            else if (glicemia >= 140 && glicemia <= 200) FC = 2;
            else if (glicemia > 200 && glicemia <= 250) FC = 3;
            else if (glicemia > 250) FC = 4;

            float insulinaTotal = IA + FC;

            tvResultado.setText("Insulina Total: " + insulinaTotal);

            // Guardar registro
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

            ContentValues reg = new ContentValues();
            reg.put("paciente_id", pacienteId);
            reg.put("fecha_hora", fechaHora);
            reg.put("tipo_id", tipoId);
            reg.put("glicemia", glicemia);
            reg.put("Fc_id", obtenerFcId(glicemia, db));
            reg.put("insulina_total", insulinaTotal);

            long registroId = db.insert("Registro", null, reg);

            ContentValues detalle = new ContentValues();
            detalle.put("registro_id", registroId);
            detalle.put("porcion_id", porcionId);
            detalle.put("cantidad", cantidad);

            db.insert("RegistroDetalle", null, detalle);

            Toast.makeText(this, "Registro guardado", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    int obtenerFcId(int glicemia, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT fc_id FROM FactorCorreccion WHERE ? BETWEEN gL_min AND gL_max", new String[]{String.valueOf(glicemia)});
        int id = -1;
        if (cursor.moveToFirst()) id = cursor.getInt(0);
        cursor.close();
        return id;
    }




}
