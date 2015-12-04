package com.example.sanofrank.comicsappback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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


public class Aggiungi extends Activity {

    //private String barcode;
    String cod_b;

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    // url to create check product
    private static String url_create_product = "http://comicsapp.altervista.org/check_barcode.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_COD_B = "cod_b";
    private static final String TAG_TITOLO = "titolo";
    private static final String TAG_AUTORE = "autore";
    private static final String TAG_DISEGNATORE = "disegnatore";
    private static final String TAG_CASA_ED = "casa_ed";
    private static final String TAG_ANNO = "anno";
    private static final String TAG_GEN = "gen";
    private static final String TAG_PREZZO = "prezzo";
    private static final String TAG_QUANTITA = "quantita";
    private static final String TAG_DESCR = "descr";


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
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(Aggiungi.this, FormActivity.class);
                add.putExtra("",cod_b);
                startActivity(add);
            }
        });

        //Toast.makeText(this, barcode , Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume(){
        super.onResume();


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
            /*String codB = args[0];
            String titolo = args[1];
            String autore = args[2];
            String disegnatore = args[3];
            String casa_ed = args[4];
            String anno = args[5];
            String gen = args[6];
            String prezzo = args[7];
            String quantita = args[8];
            String descr = args[9];*/

            // Building Parameters
            List<NameValuePair> params2 = new ArrayList<NameValuePair>();
            params2.add(new BasicNameValuePair("cod_b", codB));
            /*params.add(new BasicNameValuePair("cod_b", codB));
            params.add(new BasicNameValuePair("titolo", titolo));
            params.add(new BasicNameValuePair("autore", autore));
            params.add(new BasicNameValuePair("disegnatore", disegnatore));
            params.add(new BasicNameValuePair("casa_ed", casa_ed));
            params.add(new BasicNameValuePair("anno", anno));
            params.add(new BasicNameValuePair("gen", gen));
            params.add(new BasicNameValuePair("prezzo", prezzo));
            params.add(new BasicNameValuePair("quantita", quantita));
            params.add(new BasicNameValuePair("descrizione", descr));*/



            JSONObject json2 = jsonParser.makeHttpRequest(url_create_product, "POST", params2);
            Log.d("Match product: ", json2.toString());

            try{

                int success = json2.getInt(TAG_SUCCESS);

                if (success == 1) {

                    product = json2.getJSONArray(TAG_PRODUCT);

                    JSONObject check = product.getJSONObject(1);

                    String cod_b = check.getString(TAG_COD_B);
                    String titolo = check.getString(TAG_TITOLO);
                    String autore = check.getString(TAG_AUTORE);
                    String disegnatore = check.getString(TAG_DISEGNATORE);
                    String casa_ed = check.getString(TAG_CASA_ED);
                    String anno = check.getString(TAG_ANNO);
                    String gen = check.getString(TAG_GEN);
                    String prezzo = check.getString(TAG_PREZZO);
                    String quantita = check.getString(TAG_QUANTITA);
                    String descr = check.getString(TAG_DESCR);


                    // successfully checked product
                    Intent i = new Intent(getApplicationContext(), FormActivity.class);
                    i.putExtra("barcode", cod_b);
                    i.putExtra("titolo", titolo);
                    startActivity(i);


                    // closing this screen
                    finish();
                }

                else{
                    Intent i = new Intent(getApplicationContext(), FormActivity.class);
                    i.putExtra("barcode", cod_b);
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
        }
    }



}
