package com.example.andrey.criminalIntent;

import android.content.Intent;
import android.support.v4.app.Fragment;


public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callback, CrimeFragment.Callback {
    @Override
    protected Fragment createFragment() {
        return  new CrimeListFragment();
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        // если макет содержить
        if (findViewById(R.id.detail_fragment_container)== null){
            Intent intent = CrimePagerActivity.newIntent(this, crime.getmId());
            startActivity(intent);
        }else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getmId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container,newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated() {
        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
