package com.sena.notbateria.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper para gestionar la base de datos SQLite local.
 * Almacena el umbral de batería configurado por el usuario.
 */
public class ConfiguracionDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notbateria.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CONFIG = "configuracion";
    public static final String COL_ID = "id";
    public static final String COL_LIMITE = "limite";

    public ConfiguracionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_CONFIG + " (" +
                COL_ID + " INTEGER PRIMARY KEY, " +
                COL_LIMITE + " INTEGER NOT NULL DEFAULT 80)";
        db.execSQL(sql);

        ContentValues valores = new ContentValues();
        valores.put(COL_ID, 1);
        valores.put(COL_LIMITE, 80);
        db.insert(TABLE_CONFIG, null, valores);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONFIG);
        onCreate(db);
    }

    /**
     * Obtiene el umbral configurado (por defecto 80).
     */
    public int obtenerLimite() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONFIG, new String[]{COL_LIMITE},
                COL_ID + " = ?", new String[]{"1"}, null, null, null);
        int limite = 80;
        if (cursor.moveToFirst()) {
            limite = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return limite;
    }

    /**
     * Guarda el nuevo umbral configurado.
     */
    public void guardarLimite(int limite) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_LIMITE, limite);
        db.update(TABLE_CONFIG, valores, COL_ID + " = ?", new String[]{"1"});
        db.close();
    }
}
