package com.example.andrey.criminalIntent;

import android.support.v4.app.Fragment;

/**
 * Created by Andrey on 07.12.2017.
 */

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return  new CrimeListFragment();
    }
}
