package net.vnict.phonecode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import net.vnict.phonecode.utils.RMS;
import net.vnict.phonecode.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class Search extends AppCompatActivity {
    AutoCompleteTextView autoTinhThanh;
    String[] arrTinhThanh;
    ArrayAdapter<String> adapterTinhThanh;
    private String DATA_FILE_NAME = "content.json";
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        mPath = getExternalFilesDir(null).getAbsolutePath() + "/" + DATA_FILE_NAME; //duong dan luu file
        addControls();
        //  addEvents();

    }

    private void addControls() {
        autoTinhThanh = (AutoCompleteTextView) findViewById(R.id.auto);
        // arrProvider=getResources().getStringArray(R.array.search);
        loadData();
        adapterTinhThanh = new ArrayAdapter<String>(
                Search.this,
                android.R.layout.simple_list_item_1,
                arrTinhThanh);
        autoTinhThanh.setAdapter(adapterTinhThanh);
    }

    public void loadData() {
        String content = Utils.readFile(mPath);
        Utils.LOG(content);
        if (RMS.getInstance().getNumberOfLaunchApp() == 0) {
            arrTinhThanh = getResources().getStringArray(R.array.search);
        } else {
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray topicJsonArray = jsonObject.getJSONArray("Data");
                arrTinhThanh = new String[topicJsonArray.length()];
                for (int i = 0; i < topicJsonArray.length(); i++) {
                    JSONObject object = topicJsonArray.getJSONObject(i);
                    arrTinhThanh[i] = object.getString("tim");
                }
            } catch (Exception ex) {
                Utils.LOG("Parse error: " + ex.toString());
            }
        }
    }

}
