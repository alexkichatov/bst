package ru.redsolution.bst.data.table;

import java.util.ArrayList;

import ru.redsolution.bst.data.BST;
import ru.redsolution.bst.data.Debugger;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Помошник для работы с базой данных.
 * 
 * @author alexander.ivanov
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "sqlite-q.db";
	private static final int DATABASE_VERSION = 7;

	private static final DatabaseHelper instance;

	static {
		instance = new DatabaseHelper(BST.getInstance());
	}

	public static DatabaseHelper getInstance() {
		return instance;
	}

	private final ArrayList<DatabaseTable> tables;

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		tables = new ArrayList<DatabaseTable>();
	}

	public void addTable(DatabaseTable table) {
		tables.add(table);
	}

	@Override
	public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
		for (DatabaseTable table : tables)
			table.create(paramSQLiteDatabase);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion > newVersion) {
			throw new IllegalStateException();
		} else {
			while (oldVersion < newVersion) {
				oldVersion += 1;
				for (DatabaseTable table : tables)
					table.migrate(db, oldVersion);
			}
		}
	}

	/**
	 * Удалить все данные.
	 */
	public void clear() {
		for (DatabaseTable table : tables)
			table.clear();
	}

	public static void execSQL(SQLiteDatabase db, String sql) {
		if (Debugger.ENABLED)
			Log.i("SQL", sql);
		db.execSQL(sql);
	}

}
