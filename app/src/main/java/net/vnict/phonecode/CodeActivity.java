package net.vnict.phonecode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import net.vnict.phonecode.adapter.CodeAdapter;
import net.vnict.phonecode.utils.RMS;
import net.vnict.phonecode.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static net.vnict.phonecode.Constants.FIRST_COLUMN;
import static net.vnict.phonecode.Constants.SECOND_COLUMN;
import static net.vnict.phonecode.Constants.THIRD_COLUMN;

public class CodeActivity extends AppCompatActivity {
    String[] arrProvider, arrOldCode, arrNewCode, arrBanner, arrInterstitial, arrIdAdmob;
    
    private String DATA_FILE_NAME = "content.json";
    private String mDataPath;
    private ArrayList<HashMap<String, String>> list;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        ListView listView = (ListView) findViewById(R.id.listView1);
        list = new ArrayList<HashMap<String, String>>();
        mDataPath = getExternalFilesDir(null).getAbsolutePath() + "/" + DATA_FILE_NAME;
        String content = Utils.readFile(mDataPath);
        Utils.LOG(content);
        if (RMS.getInstance().getNumberOfLaunchApp() == 0) {
            arrProvider = getResources().getStringArray(R.array.provinces);
            arrOldCode = getResources().getStringArray(R.array.old_prefixes);
            arrNewCode = getResources().getStringArray(R.array.new_prefixes);
            for (int i = 0; i < arrOldCode.length; i++) {
                HashMap<String, String> temp = new HashMap<String, String>();
                temp.put(FIRST_COLUMN, arrProvider[i]);
                temp.put(SECOND_COLUMN, arrOldCode[i]);
                temp.put(THIRD_COLUMN, arrNewCode[i]);
                list.add(temp);
            }
            Utils.LOG("Load Offline CodeActivity");


        } else {
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray topicJsonArray = jsonObject.getJSONArray("Data");
                arrProvider = new String[topicJsonArray.length()];
                arrNewCode = new String[topicJsonArray.length()];
                arrOldCode = new String[topicJsonArray.length()];
                for (int i = 0; i < topicJsonArray.length(); i++) {
                    JSONObject object = topicJsonArray.getJSONObject(i);
                    arrOldCode[i] = object.getString("old");
                    arrNewCode[i] = object.getString("new");
                    arrProvider[i] = object.getString("ten");
                    HashMap<String, String> temp = new HashMap<String, String>();
                    temp.put(FIRST_COLUMN, arrProvider[i]);
                    temp.put(SECOND_COLUMN, arrOldCode[i]);
                    temp.put(THIRD_COLUMN, arrNewCode[i]);
                    list.add(temp);
                }
                Utils.LOG("Load Updated CodeActivity");
            } catch (Exception ex) {
                Utils.LOG("Parse error: " + ex.toString());
            }

        }
        CodeAdapter adapter = new CodeAdapter(CodeActivity.this, list);
        listView.setAdapter(adapter);
        loadAd();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mInterstitialAd.isLoaded())
                {
                    mInterstitialAd.show();
                }
            }
        });
    }
    private void loadAd() {
        String idAdmob ="ca-app-pub-9078637596840810~3533728208";
        String idBanner ="ca-app-pub-3940256099942544/6300978111";
        String idInterstitial = "ca-app-pub-3940256099942544/1033173712";
        Utils.LOG("##################mLaucher:"+RMS.getInstance().getNumberOfLaunchApp());
        if (RMS.getInstance().getNumberOfLaunchApp() != 0) {
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
                idAdmob = arrIdAdmob[length-1];
                idBanner = arrBanner[length-1];
                idInterstitial = arrInterstitial[length-1];
                Utils.LOG("Da load id admob moi thanh cong ");
            } catch (Exception ex) {
                Utils.LOG("Khong the load id admob " + ex.toString());
            }
        }
        else
        {
            arrBanner = getResources().getStringArray(R.array.arrBanner);
            arrInterstitial = getResources().getStringArray(R.array.interstitial);
            arrIdAdmob = getResources().getStringArray(R.array.idAdmob);
            idAdmob = arrIdAdmob[0];
            idBanner = arrBanner[0];
            idInterstitial = arrInterstitial[0];
            Utils.LOG("Load id tu local array ");
        }
        MobileAds.initialize(this, idAdmob);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(idInterstitial);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }
}