package galilel.org.galilelwallet.rate.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;

import galilel.org.galilelwallet.contacts.AbstractSqliteDb;
import global.GalilelRate;
import global.store.RateDbDao;

public class RateDb extends AbstractSqliteDb<GalilelRate> implements RateDbDao<GalilelRate> {


    private static final String DATABASE_NAME = "Rates";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "rates";

    private static final String KEY_ID = "id";
    private static final String KEY_COIN = "coin";
    private static final String KEY_VALUE = "value";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_LINK = "link";

    private static final int KEY_POS_ID = 0;
    private static final int KEY_POS_COIN = 1;
    private static final int KEY_POS_VALUE = 2;
    private static final int KEY_POS_TIMESTAMP = 3;
    private static final int KEY_POS_LINK = 4;


    public RateDb(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " +TABLE_NAME+
                        "(" +
                        KEY_ID + " INTEGER primary key autoincrement, "+
                        KEY_COIN + " TEXT, "+
                        KEY_VALUE + " TEXT, "+
                        KEY_TIMESTAMP + " INTEGER, "+
                        KEY_LINK + " TEXT "
                        +")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // todo: this is just for now..
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    @Override
    protected String getTableName() {
        return DATABASE_NAME;
    }

    @Override
    protected ContentValues buildContent(GalilelRate obj) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_COIN,obj.getCode());
        contentValues.put(KEY_VALUE,obj.getRate().toEngineeringString());
        contentValues.put(KEY_TIMESTAMP,obj.getTimestamp());
        contentValues.put(KEY_LINK,obj.getLink());
        return contentValues;
    }

    @Override
    protected GalilelRate buildFrom(Cursor cursor) {
        String coin = cursor.getString(KEY_POS_COIN);
        BigDecimal value = new BigDecimal(cursor.getString(KEY_POS_VALUE));
        long timestap = cursor.getLong(KEY_POS_TIMESTAMP);
        return new GalilelRate(coin,value,timestap);
    }

    public GalilelRate getRate(String coin){
        return get(KEY_COIN,coin);
    }


    public void insertOrUpdateIfExist(GalilelRate galilelRate) {
        if (getRate(galilelRate.getCode())==null){
            insert(galilelRate);
        }else {
            updateByKey(KEY_COIN,galilelRate.getCode(),galilelRate);
        }
    }
}