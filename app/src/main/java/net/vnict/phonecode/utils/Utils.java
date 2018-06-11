package net.vnict.phonecode.utils;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {
    private static boolean sShowLog = true;
    private static String TAG = "PhoneCode";

    public static void LOG(String str) {
        if (sShowLog) {
            Log.d(TAG, str);
        }
    }

    static public void showToast(Context context, String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static String readFile(String pathFile) {
        String content = "";

        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileInputStream = new FileInputStream(pathFile);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            String line = bufferedReader.readLine();
            while (line != null) {
                content += line + "\n";
                line = bufferedReader.readLine();
            }
        } catch (Exception ex) {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }
}
