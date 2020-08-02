package com.somercelik.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView usdTextView, tryTextView, jpyTextView, cadTextView, cnyTextView;


    /*TODO
     *  Öncelikle aşağıda bıraktığım API_KEY kısmına https://fixer.io adresine kayıt olarak aldığınız API access key'i yazmanız gerekmektedir.
     *
     * @somercelik*/
    final String API_URL = "http://data.fixer.io/api/latest?access_key=API_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usdTextView = findViewById(R.id.usdTextView);
        tryTextView = findViewById(R.id.tryTextView);
        jpyTextView = findViewById(R.id.jpyTextView);
        cadTextView = findViewById(R.id.cadTextView);
        cnyTextView = findViewById(R.id.cnyTextView);
    }

    public void getCurrencyData(View view) {
        DownloadData downloadData = new DownloadData();
        try {
            downloadData.execute(API_URL);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Veri alınamadı, internet bağlantınızı kontrol ediniz.", Toast.LENGTH_LONG).show();
        }


    }

    public class DownloadData extends AsyncTask<String, Void, String> {

        String usDollar, turkishLira, japaneseYen, canadianDollar, chineseRenminbi;
        URL url;
        String resultFromAPI = "";
        char receivedChar;

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL(strings[0]);                                                      //Verilen url URL sınıfından oluşturulan neseneye alınır.
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(); //API http protokolü kullandığı için bununla bağlantı açılır.
                InputStream inputStream = httpURLConnection.getInputStream();                   //Gelen veriler önce InputStream sonra reader nesnelerine alınır.
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();                                            //Önce integer değer döndüren fonksiyondan veriler tek tek alınır

                while (data > 0) {                                                              //tüm gelen veriler alınana kadar:
                    receivedChar = (char) data;                                                 //char'a cast edilerek geçici değişkene alınır
                    resultFromAPI += receivedChar;                                              //Metodun döndüreceği değişkene tek tek yazılır.
                    data = inputStreamReader.read();                                            //Sona kadar gitmek için tek tek inputstream okunur.
                }

                return resultFromAPI;                                                           //API'den gelen tüm veriler döndürülür.
                // onPostExecute bunları işlemek için kullanılabilir.
            } catch (IOException e) {
                return null;                                                                    //Eğer hatayla karşılaşılırsa null döndürülür.
            }

        }


        //API'den gelen veriler aşağıdaki metoda girer
        @Override
        protected void onPostExecute(String receivedData) {
            super.onPostExecute(receivedData);

            //Gelen veri JSON Objesine atanır.
            try {
                JSONObject rawData = new JSONObject(receivedData);                                  //Tamamı önce ham bir şekilde alınır
                JSONObject ratesData = new JSONObject(rawData.getString("rates"));            //Sonra rates altındakiler alınır.

                usDollar = ratesData.getString("USD");                                        //Bunların altında bulunanlar istediğimiz veriler.
                turkishLira = ratesData.getString("TRY");                                     //Hepsi tek tek textview'lara yazdırılır.
                japaneseYen = ratesData.getString("JPY");
                canadianDollar = ratesData.getString("CAD");
                chineseRenminbi = ratesData.getString("CNY");
                usdTextView.setText(usDollar);
                tryTextView.setText(turkishLira);
                jpyTextView.setText(japaneseYen);
                cadTextView.setText(canadianDollar);
                cnyTextView.setText(chineseRenminbi);

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Veri alınamadı, internet bağlantınızı kontrol ediniz.", Toast.LENGTH_LONG).show();
            }

        }
    }
}