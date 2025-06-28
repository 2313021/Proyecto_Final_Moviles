package co.edu.univalle.insulinaapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "insulina.db";
    public static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Tabla Paciente
        db.execSQL("CREATE TABLE Paciente (" +
                "paciente_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "correo TEXT, " +
                "contraseña TEXT)");

        // Tabla TipoComida
        db.execSQL("CREATE TABLE TipoComida (" +
                "tipo_id INTEGER PRIMARY KEY, " +
                "descripcion TEXT)");

        // Tabla Ratio (valor por tipo de comida)
        db.execSQL("CREATE TABLE Ratio (" +
                "tipo_id INTEGER PRIMARY KEY, " +
                "valor INTEGER, " +
                "FOREIGN KEY(tipo_id) REFERENCES TipoComida(tipo_id))");

        // Tabla FactorCorreccion
        db.execSQL("CREATE TABLE FactorCorreccion (" +
                "fc_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "gL_min INTEGER, " +
                "gL_max INTEGER, " +
                "fc_valor INTEGER)");

        // Tabla Registro (registro de insulina)
        db.execSQL("CREATE TABLE Registro (" +
                "registro_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "paciente_id INTEGER, " +
                "fecha_hora TEXT, " +
                "tipo_id INTEGER, " +
                "glicemia INTEGER, " +
                "Fc_id INTEGER, " +
                "insulina_total REAL, " +
                "FOREIGN KEY(paciente_id) REFERENCES Paciente(paciente_id), " +
                "FOREIGN KEY(tipo_id) REFERENCES TipoComida(tipo_id), " +
                "FOREIGN KEY(Fc_id) REFERENCES FactorCorreccion(fc_id))");

        // Tabla Categoria
        db.execSQL("CREATE TABLE Categoria (" +
                "categoria_id INTEGER PRIMARY KEY, " +
                "nombre TEXT)");

        // Tabla Alimento
        db.execSQL("CREATE TABLE Alimento (" +
                "alimento_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "categoria_id INTEGER, " +
                "nombre TEXT, " +
                "FOREIGN KEY(categoria_id) REFERENCES Categoria(categoria_id))");

        // Tabla Porcion
        db.execSQL("CREATE TABLE Porcion (" +
                "porcion_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "alimento_id INTEGER, " +
                "descripcion TEXT, " +
                "peso_g REAL, " +
                "cho_g REAL, " +
                "FOREIGN KEY(alimento_id) REFERENCES Alimento(alimento_id))");

        // Tabla RegistroDetalle (alimentos consumidos por registro)
        db.execSQL("CREATE TABLE RegistroDetalle (" +
                "detalle_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "registro_id INTEGER, " +
                "porcion_id INTEGER, " +
                "cantidad REAL, " +
                "FOREIGN KEY(registro_id) REFERENCES Registro(registro_id), " +
                "FOREIGN KEY(porcion_id) REFERENCES Porcion(porcion_id))");

        // Opcional: insertar datos base
        insertarDatosIniciales(db);
    }

    private void insertarDatosIniciales(SQLiteDatabase db) {
        // Tipos de comida
        db.execSQL("INSERT INTO TipoComida (tipo_id, descripcion) VALUES (1, 'Desayuno'), (2, 'Almuerzo'), (3, 'Cena')");
        db.execSQL("INSERT INTO Ratio (tipo_id, valor) VALUES (1, 15), (2, 12), (3, 15)");

        // Factores de corrección
        db.execSQL("INSERT INTO FactorCorreccion (gL_min, gL_max, fc_valor) VALUES (70, 100, -1), (140, 200, 2), (200, 250, 3), (251, 9999, 4)");

        // Categorías básicas
        db.execSQL("INSERT INTO Categoria (categoria_id, nombre) VALUES (1, 'Frutas'), (2, 'Lácteos'), (3, 'Cereales')");

        // Alimentos de ejemplo
        db.execSQL("INSERT INTO Alimento (categoria_id, nombre) VALUES (1, 'Manzana'), (1, 'Banano'), (3, 'Avena')");

        // Porciones de ejemplo
        db.execSQL("INSERT INTO Porcion (alimento_id, descripcion, peso_g, cho_g) VALUES " +
                "(1, '1 mediana (25 g)', 125, 25), " +
                "(2, '1 unidad (30 g)', 120, 27), " +
                "(3, '1 taza cocida (37 g)', 250, 30)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS RegistroDetalle");
        db.execSQL("DROP TABLE IF EXISTS Registro");
        db.execSQL("DROP TABLE IF EXISTS Porcion");
        db.execSQL("DROP TABLE IF EXISTS Alimento");
        db.execSQL("DROP TABLE IF EXISTS Categoria");
        db.execSQL("DROP TABLE IF EXISTS Ratio");
        db.execSQL("DROP TABLE IF EXISTS TipoComida");
        db.execSQL("DROP TABLE IF EXISTS FactorCorreccion");
        db.execSQL("DROP TABLE IF EXISTS Paciente");
        onCreate(db);
    }
}
