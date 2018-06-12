package net.vnict.phonecode;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import net.vnict.phonecode.adapter.ContactAdapter;
import net.vnict.phonecode.model.Contact;
import net.vnict.phonecode.model.TransProviders;
import net.vnict.phonecode.utils.CommonCode;
import net.vnict.phonecode.utils.RMS;
import net.vnict.phonecode.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class RevertActivity extends AppCompatActivity {
    public static final int STATUS_OK = 1;
    public static final int STATUS_ERROR = 2;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final String DOWNLOAD_URL = "https://www.dropbox.com/s/zdmgkyi3hce55j8/mobile.json?dl=1";
    private ListView lvDanhBa;
    private TextView text_list, btnUpdate, btnRevert, txtProgress;
    private ProgressBar progress_barUpdate;
    private ArrayList<Contact> dsDanhBa;
    private ArrayList<Contact> dsDanhBaChecked;
    private ContactAdapter adapterDanhBa;
    private ArrayList<ArrayList<TransProviders>> listCode;
    private ArrayList<String> listOldCode;
    private ArrayList<String> listNewCode;
    private CheckBox chkAll;
    private String[] arrOldCode, arrNewCode, arrBanner, arrInterstitial, arrIdAdmob;
    private String mDataPath;
    private String DATA_FILE_NAME = "content.json";
    private LoadContact loadContact;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;
    RunAsyncUpdate runAsyncUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstInit();
    }

    public void firstInit() {
        mDataPath = getExternalFilesDir(null).getAbsolutePath() + "/" + DATA_FILE_NAME;
        RMS.getInstance().init(this);
        RMS.getInstance().load();
        start();
    }

    public void start() {
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle(R.string.title_revert);
        loadAd();
        init();
    }

    private void loadAd() {
        String idAdmob = "ca-app-pub-8530721204937057~3620432071";
        String idBanner = "ca-app-pub-8530721204937057/6773757768";
        String idInterstitial = "ca-app-pub-8530721204937057/4518580771";
        if (RMS.getInstance().getNumberOfDownloadData() != 0) {
            String content = Utils.readFile(mDataPath);
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray adJsonArray = jsonObject.getJSONArray("Admob");
                int length = adJsonArray.length();
                arrBanner = new String[length];
                arrInterstitial = new String[length];
                arrIdAdmob = new String[length];
                for (int i = 0; i < length; i++) {
                    JSONObject object = adJsonArray.getJSONObject(i);
                    arrIdAdmob[i] = object.getString("idAdmob");
                    arrBanner[i] = object.getString("banner");
                    arrInterstitial[i] = object.getString("interstitial");
                }
                idAdmob = arrIdAdmob[length - 1];
                idBanner = arrBanner[length - 1];
                idInterstitial = arrInterstitial[length - 1];
                Utils.LOG("Da load id admob moi thanh cong ");
            } catch (Exception ex) {
                Utils.LOG("Khong the load id admob " + ex.toString());
            }
        }
        // init banner admob
        MobileAds.initialize(this, idAdmob);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.adView);
        mAdView = new AdView(RevertActivity.this);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(idBanner);
        relativeLayout.addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        // init mInterstitialAd
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(idInterstitial);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    public void init() {
        addControls();
        addEvents();
        checkPermission();
    }

    public void updateContact(String oldPhoneNumber, String newPhoneNumber) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        String baseSelection = ContactsContract.CommonDataKinds.Phone.NUMBER + "=?";
        String[] baseSelectionArgs = new String[]{
                String.valueOf(oldPhoneNumber)
        };
        String selection = baseSelection + " AND " + ContactsContract.Data.MIMETYPE + "=? AND " + String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE) + " = ? ";
        for (int dem = 1; dem <= 12; dem++) {
            String[] selectionArgs = new String[]{
                    baseSelectionArgs[0],
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                    String.valueOf(dem)// Nếu chọn tất cả TYPE
            };
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI).withSelection(selection, selectionArgs).withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber).build());
            try {
                this.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void addEvents() {
        chkAll = (CheckBox) findViewById(R.id.check_all);
        chkAll.setChecked(false);
        //Khi click vào check all thì sử dụng hàm như vậy.
        chkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyCheckBoxAll(chkAll.isChecked());
                adapterDanhBa.notifyDataSetChanged();
            }
        });
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                updateContactWithAd();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdClosed() {
                mAdView.setVisibility(View.GONE);
            }
        });
    }

    private void xuLyCheckBoxAll(boolean isChecked) {
        int totalChecked =0;
        if (isChecked) {
            //Nếu chọn checkall là true thì chạy vòng lặp để check tất cả danh bạ
            for (int i = 0; i <= dsDanhBa.size() - 1; i++) {
                totalChecked++;
                if (dsDanhBa.get(i).isChecked()) continue;
                dsDanhBa.get(i).setChecked(true);
            }
            chkAll.setText(getResources().getString(R.string.selected)+" "+totalChecked+" "+getResources().getString(R.string.contact));
        }
        else {
            //Nếu chọn checkall là false thì chạy vòng lặp để bỏ check tất cả danh bạ
            for (int i = 0; i <= dsDanhBa.size() - 1; i++) {
                dsDanhBa.get(i).setChecked(false);
            }
            chkAll.setText(getResources().getString(R.string.seleted_all));
        }
    }

    private void addControls() {
        lvDanhBa = (ListView) findViewById(R.id.lvDanhBa);
        dsDanhBa = new ArrayList<>();
        text_list = (TextView) findViewById(R.id.text_list);
        btnUpdate = (TextView) findViewById(R.id.btnUpdate);
        btnRevert = (TextView) findViewById(R.id.btnRevert);
        Drawable top = getResources().getDrawable(R.drawable.ic_home);
        btnRevert.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
        btnRevert.setText(R.string.homepage);
        txtProgress = (TextView) findViewById(R.id.txtProgress);
        progress_barUpdate = (ProgressBar) findViewById(R.id.progress_barUpdate);
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            default:
                return;
            case R.id.btnUpdate:
                AlertDialog.Builder b = new AlertDialog.Builder(RevertActivity.this);
                b.setTitle(R.string.notice);
                b.setMessage(R.string.confirm_update);
                b.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //  if (mInterstitialAd.isLoaded() && mIsLoadAd) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            updateContactWithAd();
                        }

                    }
                });
                b.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                b.create().show();
                break;
            case R.id.btnRevert:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.text_about:
                new AlertDialog.Builder(this).setTitle(R.string.about).setMessage(R.string.about_description).setIcon(R.mipmap.ic_launcher).create().show();
                break;
            case R.id.text_code:
                Intent intent1 = new Intent(RevertActivity.this, CodeActivity.class);
                startActivity(intent1);
                break;
//            case R.id.btnShare:
//                ShareLinkContent content = new ShareLinkContent.Builder()
//                        .setContentUrl(Uri.parse("https://vnict.net"))
//                        .setShareHashtag(new ShareHashtag.Builder()
//                                .setHashtag("#Test")
//                                .build())
//                        .build();
//                ShareDialog.show(this, content);
//                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //showContact();
                loadContact = new LoadContact();
                loadContact.execute();
            } else {
                AlertDialog.Builder b = new AlertDialog.Builder(RevertActivity.this);
                b.setTitle("Chú ý");
                b.setMessage("Nếu bạn không cấp quyền ứng dụng sẽ thoát?");
                b.setPositiveButton("OK, Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                b.setNegativeButton("Cấp quyền", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        checkPermission();
                    }
                });
                b.create().show();
            }
        }
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
            }, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            // showContact();
            loadContact = new LoadContact();
            loadContact.execute();
        }
    }

    public void showContact() {
        //Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        dsDanhBa.clear();
        if (RMS.getInstance().getNumberOfDownloadData() != 0) {

            Utils.LOG("Show Contact - Loading Updated Data: ");
            Utils.LOG("Count:" + RMS.getInstance().getNumberOfDownloadData());
            loadData();
        } else {
            loadLocalData();
            Utils.LOG("Show Contact - Loading LocalData....................");
        }
        while (cursor.moveToNext()) {
            String tenCotId = ContactsContract.RawContacts.CONTACT_ID;
            String tenCotName = ContactsContract.Contacts.DISPLAY_NAME;
            String tenCotPhone = ContactsContract.CommonDataKinds.Phone.NUMBER;
            int vtCotId = cursor.getColumnIndex(tenCotId);
            int vtTenCotName = cursor.getColumnIndex(tenCotName);
            int vtTenCotPhone = cursor.getColumnIndex(tenCotPhone);
            String id = cursor.getString(vtCotId);
            String name = cursor.getString(vtTenCotName);
            String phone = cursor.getString(vtTenCotPhone);
            Contact contact = new Contact(id, name, phone);
            contact.setTransnumber(CommonCode.ChuanHoaChuoi(contact));
            if (CommonCode.CheckChangingPhone(contact, listCode)) {
                contact.setNewphonenumber(CommonCode.ChangingPhone(contact, listCode));
                dsDanhBa.add(contact);
            }

        }
    }

    private void updateContactWithAd() {
        int temp;
        dsDanhBaChecked = new ArrayList<Contact>();
        temp = 0;
        for (int i = 0; i <= dsDanhBa.size() - 1; i++) {
            if (dsDanhBa.get(i).isChecked() == true) {
                dsDanhBaChecked.add(dsDanhBa.get(i));
                temp = (temp + 1);
            }
        }
        if (temp == 0) {
            Toast.makeText(getApplicationContext(), R.string.no_selected, Toast.LENGTH_SHORT).show();
        } else
        {
            runAsyncUpdate = new RunAsyncUpdate();
            runAsyncUpdate.execute(temp);
        }
    }

    public void loadData() {
        String content = Utils.readFile(mDataPath);
        Utils.LOG(content);
        try {
            JSONObject jsonObject = new JSONObject(content);
            JSONArray dataJsonArray = jsonObject.getJSONArray("Data");
            arrNewCode = new String[dataJsonArray.length()];
            arrOldCode = new String[dataJsonArray.length()];
            for (int i = 0; i < dataJsonArray.length(); i++) {
                JSONObject object = dataJsonArray.getJSONObject(i);
                arrOldCode[i] = object.getString("new");
                arrNewCode[i] = object.getString("old");
            }

        } catch (Exception ex) {
            Utils.LOG("Parse error: " + ex.toString());
        }
        listOldCode = new ArrayList<String>(Arrays.asList(arrOldCode));
        listNewCode = new ArrayList<String>(Arrays.asList(arrNewCode));
        listCode = CommonCode.TachMaVungTheoDoDai(listOldCode, listNewCode);
    }

    public void loadLocalData() {
        String[] arrOldCode = getResources().getStringArray(R.array.old_prefixes);
        String[] arrNewCode = getResources().getStringArray(R.array.new_prefixes);
        listOldCode = new ArrayList<String>(Arrays.asList(arrNewCode));
        listNewCode = new ArrayList<String>(Arrays.asList(arrOldCode));
        listCode = CommonCode.TachMaVungTheoDoDai(listOldCode, listNewCode);
    }

    private void showDownloadError() {
        new AlertDialog.Builder(this).setTitle("Download").setMessage("Download Error, Please check internet connection").create().show();
    }

    private class Download extends AsyncTask<String, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            String urlInput = params[0];
            String urlOutput = params[1];
            Utils.LOG("Data Url: " + urlInput);
            Utils.LOG("Save Url: " + urlOutput);
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(urlInput);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                long totalSize = httpURLConnection.getContentLength();
                long downloadedSize = 0;
                byte[] buffer = new byte[1024];
                int bufferLength = 0;
                //out put
                FileOutputStream fos = new FileOutputStream(urlOutput);
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    int percent = (int) (downloadedSize * 100 / totalSize);
                    publishProgress(percent);
                }
                httpURLConnection.disconnect();
            } catch (Exception e) {
                httpURLConnection.disconnect();
                return STATUS_ERROR;
            }
            return STATUS_OK;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Utils.LOG("Percent " + values[0]);
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);
            Utils.LOG("Download status " + s);
            if (s == STATUS_OK) {
                Utils.LOG("Loading Updated Data....................");
                RMS.getInstance().increaseNumberOfDownloadData();
                start();


            } else {
                //showDownloadError();
                start();
                Utils.LOG("Loading LocalData....................");

            }
        }
    }

    private class LoadContact extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            showContact();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapterDanhBa = new ContactAdapter(RevertActivity.this, R.layout.item, dsDanhBa);
            lvDanhBa.setAdapter(adapterDanhBa);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.CALL_PHONE
                }, 100);
            }
            if (dsDanhBa.isEmpty()) {
                text_list.setVisibility(View.VISIBLE);
                text_list.setText(R.string.no_contact);
                btnUpdate.setVisibility(View.GONE);
                lvDanhBa.setVisibility(View.GONE);
            }
        }
    }

    private class RunAsyncUpdate extends AsyncTask<Integer, Integer,  Void> {
        private int temp;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txtProgress.setVisibility(View.VISIBLE);
            progress_barUpdate.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.GONE);
            btnRevert.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), R.string.waiting, Toast.LENGTH_LONG).show();
        }
        @Override
        protected Void doInBackground(Integer... params) {
        temp = params[0];
            int size = dsDanhBaChecked.size();
            for (int j = 0; j < size; j++) {
                int percent = (int) (j * 100 / size);
                publishProgress(percent);
                String id = dsDanhBaChecked.get(j).getPhone();
                String num = dsDanhBaChecked.get(j).getNewphonenumber();
                updateContact(id, num);
            }
        return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            txtProgress.setText((getResources().getString(R.string.updating)+" "+String.valueOf(values[0])+ "%"));
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            txtProgress.setVisibility(View.GONE);
            progress_barUpdate.setVisibility(View.GONE);
            btnRevert.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.VISIBLE);
            chkAll.setText(R.string.seleted_all);
            loadContact = new LoadContact();
            loadContact.execute();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.updated) +" "+ String.valueOf(temp) +" "+ getResources().getString(R.string.contact), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }
}