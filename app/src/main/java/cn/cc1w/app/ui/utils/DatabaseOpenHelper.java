package cn.cc1w.app.ui.utils;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

/**
 *
 * @author xiezonglin
 * 数据库操作
 */
public class DatabaseOpenHelper {
    private static DbManager.DaoConfig daoConfig;
    private static DbManager db;
    private static final String DB_NAME = "ccwbNews";
    private static final int VERSION = 1;

    private DatabaseOpenHelper() {

    }

    public static DbManager getInstance() {
        if (null == db) {
            daoConfig = new DbManager.DaoConfig()
                    .setDbName(DB_NAME)
                    .setDbVersion(VERSION)
//                    .setDbDir(new File(Environment.getExternalStorageDirectory().getPath() + "/DB/"))
                    .setDbOpenListener(new DbManager.DbOpenListener() {
                        @Override
                        public void onDbOpened(DbManager db) {
                            db.getDatabase().enableWriteAheadLogging();
                            //开启WAL, 对写入加速提升巨大(作者原话)
                        }
                    })
                    .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                        @Override
                        public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                            //数据库升级操作 ...
                            // db.addColumn(...);
                            // db.dropTable(...);
                            // ...
//                        try {
//                            db.addColumn(Child.class, "REGTIME");//Child表，新增列名。记得修改之后，VERSION增加数值
//                            //db.saveOrUpdate(Child.class);
//                        } catch (DbException e) {
//                            e.printStackTrace();
//                        }
                        }
                    });
            try {
                db = x.getDb(daoConfig);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        return db;
    }
}