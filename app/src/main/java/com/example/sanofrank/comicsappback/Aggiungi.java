package com.example.sanofrank.comicsappback;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Aggiungi extends AppCompatActivity {

    //private String barcode;
    private String cod_b;

    // Progress Dialog
    private ProgressDialog pDialog;


    JSONParser jsonParser = new JSONParser();

    // url to create check product
    private static String url_check_product = "http://comicsapp.altervista.org/check_product.php";

    //Alert Dialog
    AlertDialog.Builder builder1;


    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_COD_B = "cod_b";
    private static final String TAG_TITOLO = "titolo";
    private static final String TAG_AUTORE = "autore";
    private static final String TAG_DISEGNATORE = "disegnatore";
    private static final String TAG_CASA_ED = "casa_ed";
    private static final String TAG_ANNO = "anno";
    private static final String TAG_GEN = "genere";
    private static final String TAG_PREZZO = "prezzo";
    private static final String TAG_QUANTITA = "quantita";
    private static final String TAG_DESCR = "descrizione";


    JSONArray product = null;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString("barcode", cod_b);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi);
        Button btn = (Button) findViewById(R.id.manuale);
        Button btn2 = (Button) findViewById(R.id.scansiona);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(Aggiungi.this, FormActivity.class);
                add.putExtra("",cod_b);
                startActivity(add);
                finish();
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();


    }


    public void onScansiona(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                cod_b = result.getContents();
                if (cod_b != null){
                    /*Intent ag = new Intent(Aggiungi.this, FormActivity.class);

                    ag.putExtra("barcode", barcode);
                    startActivity(ag);*/
                    Log.d("codice",cod_b);
                    new CheckProduct().execute(cod_b);
                }

            }
        } else {
            Log.d("MainActivity", "Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    class CheckProduct extends AsyncTask <String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Aggiungi.this);
            pDialog.setMessage("Checking Product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            String codB = args[0];

            // Building Parameters
            List<NameValuePair> params2 = new ArrayList<NameValuePair>();
            params2.add(new BasicNameValuePair("cod_b", codB));

            Log.d("cod_b",codB);
            Log.d("params",params2.toString());

            JSONObject json2 = jsonParser.makeHttpRequest(url_check_product,"POST", params2);
            Log.d("Risultato ", json2.toString());

            try{

                int success = json2.getInt(TAG_SUCCESS);



                if (success == 1) {

                    product = json2.getJSONArray(TAG_PRODUCT);

                    JSONObject check = product.getJSONObject(0);

                    Log.d("check",check.toString());

                    String cod_b = check.getString(TAG_COD_B);
                   /* String titolo = check.getString(TAG_TITOLO);
                    String autore = check.getString(TAG_AUTORE);
                    String disegnatore = check.getString(TAG_DISEGNATORE);
                    String casa_ed = check.getString(TAG_CASA_ED);
                    String anno = check.getString(TAG_ANNO);
                    String gen = check.getString(TAG_GEN);
                    String prezzo = check.getString(TAG_PREZZO);
                    int quantita = check.getInt(TAG_QUANTITA);
                    String descr = check.getString(TAG_DESCR);*/



                    // successfully checked product
                    Intent i = new Intent(Aggiungi.this, FormActivity.class);
                    i.putExtra("match",success);
                    i.putExtra("barcode", cod_b);
                    /*i.putExtra("titolo", titolo);
                    i.putExtra("autore", autore);
                    i.putExtra("disegnatore", disegnatore);
                    i.putExtra("casa_ed", casa_ed);
                    i.putExtra("anno", anno);
                    i.putExtra("gen", gen);
                    i.putExtra("prezzo", prezzo);
                    i.putExtra("quantita", quantita);
                    i.putExtra("descr", descr);*/


                    startActivity(i);




                } else{

                    Intent i = new Intent(Aggiungi.this, FormActivity.class);
                    Log.d("barcode",cod_b);
                    i.putExtra("barcode", cod_b);
                    i.putExtra("match",0);
                    Log.d("match",i.putExtra("match",0).toString());
                    startActivity(i);


                }

            }
            catch(JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            // closing this screen
            finish();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_aggiungi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
