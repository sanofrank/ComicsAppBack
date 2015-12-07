package com.example.sanofrank.comicsappback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.Date;



public class FormActivity extends Activity {

    EditText editText;
    String barcode;
    String titolo;
    String autore;
    String disegnatore;
    String casa_ed;
    String anno;
    String gen;
    String prezzo;
    int quantita;
    String descr;

    // Progress Dialog
    private ProgressDialog pDialog;


    JSONParser jsonParser = new JSONParser();
   // EditText inputCodb;
    EditText inputTitolo;
    EditText inputDisegnatore;
    EditText inputAutore;
    EditText inputCasa_ed;
    EditText inputAnno;
    EditText inputGen;
    EditText inputPrezzo;
    EditText inputQuantita;
    EditText inputDescr;

    //Date Formatter
    /*DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); // Make sure user insert date into edittext in this format.
    Date dateObject;*/

    // url to create new product
    private static String url_create_product = "http://comicsapp.altervista.org/create_product.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";

    JSONArray product = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        //prendo i valori
        barcode = getIntent().getExtras().getString("barcode");
        titolo = getIntent().getExtras().getString("titolo");
        autore = getIntent().getExtras().getString("autore");
        disegnatore = getIntent().getExtras().getString("disegnatore");
        casa_ed = getIntent().getExtras().getString("casa_ed");
        anno = getIntent().getExtras().getString("anno");
        gen = getIntent().getExtras().getString("gen");
        prezzo = getIntent().getExtras().getString("prezzo");
        quantita = getIntent().getExtras().getInt("quantita");
        descr = getIntent().getExtras().getString("descr");

        //prendo gli editTExt
        editText = (EditText) findViewById(R.id.edit_barcode);
        inputTitolo = (EditText) findViewById(R.id.edit_name);
        inputAutore = (EditText) findViewById(R.id.edit_autore);
        inputDisegnatore = (EditText) findViewById(R.id.edit_disegnatore);
        inputCasa_ed = (EditText) findViewById(R.id.edit_casa_ed);
        inputAnno = (EditText) findViewById(R.id.edit_anno);
        inputGen = (EditText) findViewById(R.id.edit_genere);
        inputPrezzo = (EditText) findViewById(R.id.edit_prezzo);
        inputQuantita = (EditText) findViewById(R.id.edit_quantita);
        inputDescr = (EditText) findViewById(R.id.edit_descr);

        // Create button

        Button btnCreateProduct = (Button) findViewById(R.id.form_button);

        // button click event
        btnCreateProduct.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    // creating new product in background thread
                                                    String codB = editText.getText().toString();
                                                    String titolo = inputTitolo.getText().toString();
                                                    String autore = inputAutore.getText().toString();
                                                    String disegnatore = inputDisegnatore.getText().toString();
                                                    String casa_ed = inputCasa_ed.getText().toString();
                                                    String anno = inputAnno.getText().toString();
                                                    String gen = inputGen.getText().toString();
                                                    String prezzo = inputPrezzo.getText().toString();
                                                    String quantita = inputQuantita.getText().toString();
                                                    String descr = inputDescr.getText().toString();
                                                    new CreateNewProduct().execute(codB,titolo,autore,disegnatore,casa_ed,anno,gen,prezzo,quantita,descr);
                                                }
                                            }

        );
    }

    //Per quando ruoto il dispositivo
    @Override
    protected void onStart() {
        super.onStart();

        //se ha trovato un codice a barre lo inserisce all'interno dell'EditText
        if (barcode != null){
            editText.setText(barcode);
            inputTitolo.setText(titolo);
            inputAutore.setText(autore);
            inputDisegnatore.setText(disegnatore);
            inputCasa_ed.setText(casa_ed);
            inputAnno.setText(anno);
            inputGen.setText(gen);
            inputPrezzo.setText(prezzo);
            inputQuantita.setText(String.valueOf(quantita));
            inputDescr.setText(descr);
            //Toast.makeText(this, barcode , Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Background Async Task to Create new product
     * */
    class CreateNewProduct extends AsyncTask<String, String, String> {


        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FormActivity.this);
            pDialog.setMessage("Creating Product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {

            String codB = args[0];
            String titolo = args[1];
            String autore = args[2];
            String disegnatore = args[3];
            String casa_ed = args[4];
            String anno = args[5];
            String gen = args[6];
            String prezzo = args[7];
            String quantita = args[8];
            String descr = args[9];


            int quantitaInt = Integer.parseInt(quantita);



            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("cod_b", codB));
            params.add(new BasicNameValuePair("titolo", titolo));
            params.add(new BasicNameValuePair("autore", autore));
            params.add(new BasicNameValuePair("disegnatore", disegnatore));
            params.add(new BasicNameValuePair("casa_ed", casa_ed));
            params.add(new BasicNameValuePair("anno", anno));
            params.add(new BasicNameValuePair("gen", gen));
            params.add(new BasicNameValuePair("prezzo",prezzo));
            params.add(new BasicNameValuePair("quantita", Integer.toString(quantitaInt)));
            params.add(new BasicNameValuePair("descrizione", descr));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_product,
                    "POST", params);


            // check log cat fro response
            Log.d("Create Response", json.toString());
            

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), Aggiungi.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                }




            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_form, menu);
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
