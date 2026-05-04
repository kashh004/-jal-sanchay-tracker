package com.jalsanchay.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.jalsanchay.data.models.RainfallEntry;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RainfallDao_Impl implements RainfallDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RainfallEntry> __insertionAdapterOfRainfallEntry;

  private final EntityDeletionOrUpdateAdapter<RainfallEntry> __deletionAdapterOfRainfallEntry;

  public RainfallDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRainfallEntry = new EntityInsertionAdapter<RainfallEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `rainfall_entries` (`id`,`date`,`rainfallMm`,`roofAreaM2`,`runoffCoefficient`,`litersHarvested`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RainfallEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        statement.bindDouble(3, entity.getRainfallMm());
        statement.bindDouble(4, entity.getRoofAreaM2());
        statement.bindDouble(5, entity.getRunoffCoefficient());
        statement.bindDouble(6, entity.getLitersHarvested());
        statement.bindLong(7, entity.getTimestamp());
      }
    };
    this.__deletionAdapterOfRainfallEntry = new EntityDeletionOrUpdateAdapter<RainfallEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `rainfall_entries` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RainfallEntry entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final RainfallEntry entry, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRainfallEntry.insertAndReturnId(entry);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final RainfallEntry entry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfRainfallEntry.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<List<RainfallEntry>> getAllEntries() {
    final String _sql = "SELECT * FROM rainfall_entries ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"rainfall_entries"}, false, new Callable<List<RainfallEntry>>() {
      @Override
      @Nullable
      public List<RainfallEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfRainfallMm = CursorUtil.getColumnIndexOrThrow(_cursor, "rainfallMm");
          final int _cursorIndexOfRoofAreaM2 = CursorUtil.getColumnIndexOrThrow(_cursor, "roofAreaM2");
          final int _cursorIndexOfRunoffCoefficient = CursorUtil.getColumnIndexOrThrow(_cursor, "runoffCoefficient");
          final int _cursorIndexOfLitersHarvested = CursorUtil.getColumnIndexOrThrow(_cursor, "litersHarvested");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<RainfallEntry> _result = new ArrayList<RainfallEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RainfallEntry _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final double _tmpRainfallMm;
            _tmpRainfallMm = _cursor.getDouble(_cursorIndexOfRainfallMm);
            final double _tmpRoofAreaM2;
            _tmpRoofAreaM2 = _cursor.getDouble(_cursorIndexOfRoofAreaM2);
            final double _tmpRunoffCoefficient;
            _tmpRunoffCoefficient = _cursor.getDouble(_cursorIndexOfRunoffCoefficient);
            final double _tmpLitersHarvested;
            _tmpLitersHarvested = _cursor.getDouble(_cursorIndexOfLitersHarvested);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new RainfallEntry(_tmpId,_tmpDate,_tmpRainfallMm,_tmpRoofAreaM2,_tmpRunoffCoefficient,_tmpLitersHarvested,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllEntriesOnce(final Continuation<? super List<RainfallEntry>> $completion) {
    final String _sql = "SELECT * FROM rainfall_entries ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RainfallEntry>>() {
      @Override
      @NonNull
      public List<RainfallEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfRainfallMm = CursorUtil.getColumnIndexOrThrow(_cursor, "rainfallMm");
          final int _cursorIndexOfRoofAreaM2 = CursorUtil.getColumnIndexOrThrow(_cursor, "roofAreaM2");
          final int _cursorIndexOfRunoffCoefficient = CursorUtil.getColumnIndexOrThrow(_cursor, "runoffCoefficient");
          final int _cursorIndexOfLitersHarvested = CursorUtil.getColumnIndexOrThrow(_cursor, "litersHarvested");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<RainfallEntry> _result = new ArrayList<RainfallEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RainfallEntry _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final double _tmpRainfallMm;
            _tmpRainfallMm = _cursor.getDouble(_cursorIndexOfRainfallMm);
            final double _tmpRoofAreaM2;
            _tmpRoofAreaM2 = _cursor.getDouble(_cursorIndexOfRoofAreaM2);
            final double _tmpRunoffCoefficient;
            _tmpRunoffCoefficient = _cursor.getDouble(_cursorIndexOfRunoffCoefficient);
            final double _tmpLitersHarvested;
            _tmpLitersHarvested = _cursor.getDouble(_cursorIndexOfLitersHarvested);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new RainfallEntry(_tmpId,_tmpDate,_tmpRainfallMm,_tmpRoofAreaM2,_tmpRunoffCoefficient,_tmpLitersHarvested,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<Double> getTotalLiters() {
    final String _sql = "SELECT SUM(litersHarvested) FROM rainfall_entries";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"rainfall_entries"}, false, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<Double> getMonthlyLiters(final String monthPrefix) {
    final String _sql = "SELECT SUM(litersHarvested) FROM rainfall_entries WHERE date LIKE ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, monthPrefix);
    return __db.getInvalidationTracker().createLiveData(new String[] {"rainfall_entries"}, false, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<RainfallEntry>> getEntriesForMonth(final String monthPrefix) {
    final String _sql = "SELECT * FROM rainfall_entries WHERE date LIKE ? || '%' ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, monthPrefix);
    return __db.getInvalidationTracker().createLiveData(new String[] {"rainfall_entries"}, false, new Callable<List<RainfallEntry>>() {
      @Override
      @Nullable
      public List<RainfallEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfRainfallMm = CursorUtil.getColumnIndexOrThrow(_cursor, "rainfallMm");
          final int _cursorIndexOfRoofAreaM2 = CursorUtil.getColumnIndexOrThrow(_cursor, "roofAreaM2");
          final int _cursorIndexOfRunoffCoefficient = CursorUtil.getColumnIndexOrThrow(_cursor, "runoffCoefficient");
          final int _cursorIndexOfLitersHarvested = CursorUtil.getColumnIndexOrThrow(_cursor, "litersHarvested");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<RainfallEntry> _result = new ArrayList<RainfallEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RainfallEntry _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final double _tmpRainfallMm;
            _tmpRainfallMm = _cursor.getDouble(_cursorIndexOfRainfallMm);
            final double _tmpRoofAreaM2;
            _tmpRoofAreaM2 = _cursor.getDouble(_cursorIndexOfRoofAreaM2);
            final double _tmpRunoffCoefficient;
            _tmpRunoffCoefficient = _cursor.getDouble(_cursorIndexOfRunoffCoefficient);
            final double _tmpLitersHarvested;
            _tmpLitersHarvested = _cursor.getDouble(_cursorIndexOfLitersHarvested);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new RainfallEntry(_tmpId,_tmpDate,_tmpRainfallMm,_tmpRoofAreaM2,_tmpRunoffCoefficient,_tmpLitersHarvested,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<Integer> getEntryCount() {
    final String _sql = "SELECT COUNT(*) FROM rainfall_entries";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"rainfall_entries"}, false, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
