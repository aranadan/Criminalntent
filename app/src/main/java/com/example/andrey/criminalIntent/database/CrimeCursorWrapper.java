package com.example.andrey.criminalIntent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.andrey.criminalIntent.Crime;
import com.example.andrey.criminalIntent.database.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;


public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    //convert cursor to Crime object
    public Crime getCrime(){
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setmTitle(title);
        crime.setmDate(new Date(date));
        crime.setmSolved(isSolved != 0);
        crime.setmSuspect(suspect);

        return crime;
    }
}
