package co.edu.univalle.insulinaapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

import android.app.AlertDialog;

public class ViewRecordsActivity extends AppCompatActivity {

    ListView listView;
    DatabaseHelper dbHelper;
    int pacienteId = 1; // Por ahora fijo, luego usar SharedPreferences si quieres multicuenta

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_records);

        listView = findViewById(R.id.listViewRegistros);
        dbHelper = new DatabaseHelper(this);

        cargarRegistros();

    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(ViewRecordsActivity.this)
                .setTitle("Confirmación")
                .setMessage("¿Deseas volver al menú principal?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    Intent intent = new Intent(ViewRecordsActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    void cargarRegistros() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> registros = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT r.registro_id, r.fecha_hora, tc.descripcion, r.glicemia, r.insulina_total " +
                "FROM Registro r JOIN TipoComida tc ON r.tipo_id = tc.tipo_id " +
                "WHERE r.paciente_id = ? ORDER BY r.fecha_hora DESC", new String[]{String.valueOf(pacienteId)});

        while (cursor.moveToNext()) {
            int registroId = cursor.getInt(0);
            String fecha = cursor.getString(1);
            String tipo = cursor.getString(2);
            int glicemia = cursor.getInt(3);
            float insulina = cursor.getFloat(4);

            StringBuilder sb = new StringBuilder();
            sb.append("🕒 ").append(fecha).append("\n");
            sb.append("🍽️ ").append(tipo).append(" | Glicemia: ").append(glicemia).append(" mg/dL\n");
            sb.append("💉 Insulina Total: ").append(insulina).append(" U\n");
            sb.append("🥗 Alimentos:\n");

            // Cargar alimentos asociados al registro
            Cursor detalle = db.rawQuery(
                    "SELECT a.nombre, p.descripcion, rd.cantidad " +
                            "FROM RegistroDetalle rd " +
                            "JOIN Porcion p ON rd.porcion_id = p.porcion_id " +
                            "JOIN Alimento a ON p.alimento_id = a.alimento_id " +
                            "WHERE rd.registro_id = ?",
                    new String[]{String.valueOf(registroId)}
            );

            while (detalle.moveToNext()) {
                String alimento = detalle.getString(0);
                String porcion = detalle.getString(1);
                float cantidad = detalle.getFloat(2);
                sb.append(" - ").append(alimento).append(" (").append(porcion).append(") x ").append(cantidad).append("\n");
            }

            detalle.close();
            registros.add(sb.toString());
        }

        cursor.close();
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, registros));
    }

}
