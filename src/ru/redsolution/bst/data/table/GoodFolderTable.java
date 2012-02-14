package ru.redsolution.bst.data.table;

/**
 * Список групп организаций.
 * 
 * @author alexander.ivanov
 * 
 */
public class GoodFolderTable extends ParentableTable {

	private static final String NAME = "good_folder";

	private final static GoodFolderTable instance;

	static {
		instance = new GoodFolderTable();
		DatabaseHelper.getInstance().addTable(instance);
	}

	public static GoodFolderTable getInstance() {
		return instance;
	}

	private GoodFolderTable() {
	}

	@Override
	public String getTableName() {
		return NAME;
	}
}