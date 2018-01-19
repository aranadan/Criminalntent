package com.example.andrey.criminalIntent;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.text.format.DateFormat;
import android.widget.ImageButton;
import android.widget.ImageView;


import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.text.format.DateFormat.format;

public class CrimeFragment extends android.support.v4.app.Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mDeleteCrimeButton;
    private Button mSuspectButton;
    private Button mReportButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private Callback mCallbacks;

    //необходимый интерфейс дл\ активности хоста
    public interface Callback{
        void onCrimeUpdated();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    // метод для передечи во фрагмент Ид преступление перед отображением его в активности
    public static CrimeFragment newInstance (UUID crimeID){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeID);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get crime id from fragment argument
        UUID crimeID = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        //get crime object from singletone by id
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeID);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getmTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setmTitle(charSequence.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getmDate());
                // задаю цулевой фрегмент для объекта dialog
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager,DIALOG_DATE);
            }
        });
        mDeleteCrimeButton = (Button) v.findViewById(R.id.delete_button);
        mDeleteCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CrimeLab.get(getActivity()).deleteCrime(mCrime);

                /*FLAG_ACTIVITY_CLEAR_TOP приказывает Android провести поиск существующего экземпляра активности,
                 и если он существует вывести из стека все сотальные активности,
                 что бы запускаемая активнойсть была верхней */
                Intent intent = new Intent(getActivity(),CrimeListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.ismSolved());
        mCrimeIsSolvedGetContentDescription();
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCrime.setmSolved(isChecked);
                mCrimeIsSolvedGetContentDescription();
                updateCrime();
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBuilder = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setSubject(getString(R.string.crime_report_subject))
                        .setText(getCrimeReport())
                        .getIntent();

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                //заголовок
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                //для отображения списка обработчиков неявного интенте
                i = Intent.createChooser(i,getString(R.string.send_report));
                startActivity(intentBuilder);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });

        if (mCrime.getmSuspect() != null){
            mSuspectButton.setText(mCrime.getmSuspect());
        }

        //если нет приложения контакты то кнопка задания подозреваемого деактивируется
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FileProvider.getUriForFile - преобразует локальный путь к файлу в объект Uri, понятный приложению камеры
                Uri uri = FileProvider.getUriForFile(getActivity(),"com.example.andrey.criminalIntent.fileprovider",mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager()
                        .queryIntentActivities(captureImage,PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities){
                    getActivity()
                            // Intent.FLAG_GRANT_WRITE_URI_PERMISSION - флаг предоставляет разрешение на запись камере для каждого Uri
                            .grantUriPermission(activity.activityInfo.packageName,uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoFragment.newInstance(mPhotoFile).show(getFragmentManager(),"PhotoFragment");
            }
        });
        updatePhotoView();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode ==  REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setmDate(date);
            updateCrime();
            updateDate();
        }else if (requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri = data.getData();
            //определение полей, значение которых должны быть возвращены запросом
            String[] queryString = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
            //Выполнение запроса - contactUri здесь выполняет функции условия "where"
            Cursor c = getActivity().getContentResolver().query(contactUri,queryString,null,null,null);
            try {
            //проверка получения результатов
                if(c.getCount() == 0){
                    return;
                }
                //извлечение первого столбца данных - имени подозреваемого
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setmSuspect(suspect);
                updateCrime();
                mSuspectButton.setText(suspect);

            }finally {
                c.close();
            }
        }else if (requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),"com.example.andrey.criminalIntent.fileprovider",mPhotoFile);
            getActivity().revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateCrime();
            updatePhotoView();
        }

    }

    private void updateDate() {
        mDateButton.setText(DateFormat.format("EEE, MMM dd",mCrime.getmDate()));
    }
    // метод формирует строку для отчета
    private String getCrimeReport(){
        String solvedString = null;
        if (mCrime.ismSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }else{
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = format(dateFormat, mCrime.getmDate()).toString();

        String suspect = mCrime.getmSuspect();
        if (suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        }else {
            suspect = getString(R.string.crime_report_suspect,suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getmTitle(),dateString,
                solvedString,suspect);

        return report;
    }

    private void updatePhotoView(){
        if (mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
            mPhotoView.setContentDescription(getString(R.string.crime_photo_no_image_description));
        }else{
            Bitmap bitmap = PictureUtils.getScaleBitmap(mPhotoFile.getPath(),getActivity());
            mPhotoView.setImageBitmap(bitmap);
            mPhotoView.setContentDescription(getString(R.string.crime_photo_image_description));
        }
    }

    private void updateCrime(){
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated();
    }

    private void mCrimeIsSolvedGetContentDescription(){
        if (mCrime.ismSolved()){
            mSolvedCheckBox.setContentDescription(getString(R.string.crime_solved));
        }else
        {mSolvedCheckBox.setContentDescription(getString(R.string.crime_not_solved));}
    }
}
