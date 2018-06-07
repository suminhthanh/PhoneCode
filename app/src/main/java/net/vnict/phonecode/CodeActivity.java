package net.vnict.phonecode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
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

import static net.vnict.phonecode.utils.Constants.FIRST_COLUMN;
import static net.vnict.phonecode.utils.Constants.SECOND_COLUMN;
import static net.vnict.phonecode.utils.Constants.THIRD_COLUMN;

public class CodeActivity extends AppCompatActivity {
    private String[] arrProvider, arrOldCode, arrNewCode, arrBanner, arrInterstitial, arrIdAdmob;
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
        if (RMS.getInstance().getNumberOfDownloadData() == 0) {
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
                if (mInterstitialAd.isLoaded())
                {
                    mInterstitialAd.show();
                }
            }
        });
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

    }
    private void loadAd() {
        String idAdmob ="ca-app-pub-8530721204937057~3620432071";
        String idBanner ="ca-app-pub-8530721204937057/6773757768";
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
                idAdmob = arrIdAdmob[length-1];
                idBanner = arrBanner[length-1];
                idInterstitial = arrInterstitial[length-1];
                Utils.LOG("Da load id admob moi thanh cong ");
            } catch (Exception ex) {
                Utils.LOG("Khong the load id admob " + ex.toString());
            }
        }
        // init banner admob
        MobileAds.initialize(this, idAdmob);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.adView);
        mAdView = new AdView(CodeActivity.this);
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
}