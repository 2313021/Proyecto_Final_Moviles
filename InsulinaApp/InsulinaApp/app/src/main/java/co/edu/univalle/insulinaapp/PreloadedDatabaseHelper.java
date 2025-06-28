package co.edu.univalle.insulinaapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.*;

public class PreloadedDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "InsulinaDB.db";
    private static final int DB_VERSION = 1;
    private final Context context;
    private final String dbPath;

    public PreloadedDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        this.dbPath = context.getDatabasePath(DB_NAME).getPath();
        copyDatabaseIfNeeded();
    }

    private void copyDatabaseIfNeeded() {
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            getReadableDatabase(); // Crea carpeta si no existe
            close();
            try {
                InputStream input = context.getAssets().open(DB_NAME);
                OutputStream output = new FileOutputStream(dbPath);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }

                output.flush();
                output.close();
                input.close();
            } catch (IOException e) {
                throw new RuntimeException("Error al copiar la base de datos", e);
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // No se ejecuta porque ya está precargada
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Opcional: manejo de versiones si lo necesitas
    }
}
