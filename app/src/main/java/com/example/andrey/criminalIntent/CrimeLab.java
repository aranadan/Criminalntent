package com.example.andrey.criminalIntent;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//Singletone
public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);

        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();
        /*for (int i = 0; i < 100; i++){
            Crime crime = new Crime();
            crime.setmTitle("Crime #" + i);
            crime.setmSolved(i % 2 == 0); //for every second object
            mCrimes.add(crime);
        }*/
    }

    public List<Crime> getCrimes(){
        return mCrimes;
    }

    public Crime getCrime(UUID id){
        for (Crime crime:mCrimes){
            if (crime.getmId().equals(id)){
                return crime;

            }
        }
        return null;
    }

    public void addCrime(Crime crime){
        mCrimes.add(crime);
    }

}
