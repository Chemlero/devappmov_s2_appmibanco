package com.example.appmibancosem2.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.appmibancosem2.data.model.SolicitudCredito
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class SolicitudDatabase(context: Context) :
    SQLiteOpenHelper(context, "mibanco.db", null, 1) {
    companion object {
        const val TABLA = "solicitudes_credito"
        const val COL_ID = "id"
        const val COL_MONTO = "monto"
        const val COL_PLAZO = "plazo_meses"
        const val COL_TIPO = "tipo"
        const val COL_DNI = "dni"
        const val COL_ESTADO = "estado"
        const val COL_FECHA = "fecha_local"
    }// Se ejecuta una sola vez al crear la BD por primera vez
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
CREATE TABLE $TABLA (
$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
$COL_MONTO REAL NOT NULL,
$COL_PLAZO INTEGER NOT NULL,
$COL_TIPO TEXT NOT NULL,
$COL_DNI TEXT NOT NULL,
$COL_ESTADO TEXT DEFAULT 'pendiente',
$COL_FECHA TEXT NOT NULL
)
""".trimIndent()
        )
    }
    // Se ejecuta cuando se incrementa el número de versión
    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA")
        onCreate(db)
    }
    // ── CREATE ────────────────────────────────────────────────────────
    fun insertar(s: SolicitudCredito): Long {
        val db = writableDatabase
        val ts = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date())
        val cv = ContentValues().apply {
            put(COL_MONTO, s.monto)
            put(COL_PLAZO, s.plazoMeses)
            put(COL_TIPO, s.tipo)
            put(COL_DNI, s.dni)
            put(COL_ESTADO, s.estado)
            put(COL_FECHA, ts)
        }
        val id = db.insert(TABLA, null, cv)
        db.close()
        return id
    }
    // ── READ: todas ordenadas por más reciente ─────────────────────────
    fun obtenerTodas(): List<SolicitudCredito> {
        val lista = mutableListOf<SolicitudCredito>()
        val db = readableDatabase
        val cursor = db.query(
            TABLA, null, null, null, null, null,
            "$COL_ID DESC"
        )
        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    SolicitudCredito(id =
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        monto =
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_MONTO)),
                        plazoMeses =
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_PLAZO)),
                        tipo =
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TIPO)),
                        dni =
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DNI)),
                        estado =
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ESTADO)),
                        fechaLocal =
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_FECHA))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
    // ── UPDATE: cambiar estado de una solicitud ────────────────────────
    fun actualizarEstado(id: Int, nuevoEstado: String): Int {
        val db = writableDatabase
        val cv = ContentValues().apply { put(COL_ESTADO, nuevoEstado) }
        val res = db.update(
            TABLA, cv,
            "$COL_ID = ?",
            arrayOf(id.toString())
        )
        db.close()
        return res
    }
    // ── DELETE ────────────────────────────────────────────────────────
    fun eliminar(id: Int): Int {
        val db = writableDatabase
        val res = db.delete(
            TABLA,
            "$COL_ID = ?",
            arrayOf(id.toString())
        )
        db.close()
        return res
    }
    // ── COUNT: solicitudes pendientes ──────────────────────────────────
    fun contarPendientes(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLA WHERE $COL_ESTADO = 'pendiente'",
            null)
        val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        db.close()
        return count
    }
}