package com.example.sanofrank.comicsappback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;




public class FormActivity extends AppCompatActivity {





    int match;
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

    ArrayList<HashMap<String, String>> productsList;

    JSONParser jsonParser = new JSONParser();

   // EditText inputCodb;
    EditText editText;
    EditText inputTitolo;
    EditText inputDisegnatore;
    EditText inputAutore;
    EditText inputCasa_ed;
    static EditText inputAnno;
    EditText inputGen;
    static EditText inputPrezzo;
    static EditText inputQuantita;
    EditText inputDescr;

    //Date Fragment

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            inputAnno.setText( ""+ day + "-" + (month + 1) + "-" + year);
        }
    }

    //NumberPickerFragment

    public static class NumberPickerFragment extends DialogFragment
            implements NumberPicker.OnValueChangeListener {
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View view = inflater.inflate(R.layout.numberdialog, null);
            builder.setView(view);
            final NumberPicker np = (NumberPicker) view.findViewById(R.id.numberPicker1);
            builder.setTitle("Quantità:");
            np.setMaxValue(100);
            np.setMinValue(0);
            np.setWrapSelectorWheel(false);
            np.setOnValueChangedListener(this);
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    inputQuantita.setText(String.valueOf(np.getValue()));

                }
            });
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    inputQuantita.setText(String.valueOf(0));

                }
            });

            return builder.create();
        }

        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            inputQuantita.setText(String.valueOf(newVal));
        }
    }

    //Price picker

    public static class PricePickerFragment extends DialogFragment {
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View view = inflater.inflate(R.layout.pricedialog, null);
            builder.setView(view);
            final NumberPicker np = (NumberPicker) view.findViewById(R.id.numberPicker1);
            final NumberPicker np2 = (NumberPicker) view.findViewById(R.id.numberPicker2);
            builder.setTitle("Prezzo:");
            np.setMaxValue(500);
            np.setMinValue(0);
            np2.setMinValue(0);
            np2.setMaxValue(99);
            np.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int i) {
                    return String.format("%02d", i);
                }
            });
            np2.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int i) {
                    return String.format("%02d", i);
                }
            });
            np.setWrapSelectorWheel(false);
            np2.setWrapSelectorWheel(true);
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    inputPrezzo.setText(String.valueOf(np.getValue())+","+String.valueOf(np2.getValue()));
                }
            });
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    inputPrezzo.setText(String.valueOf(00)+","+String.valueOf(00));

                }
            });
            return builder.create();
        }



    }











    // url to create new product
    private static String url_create_product = "http://comicsapp.altervista.org/create_product.php";
    //url to update product
    private static String url_update_product = "http://comicsapp.altervista.org/update_product.php";
    //url to delete product
    private static String url_delete_product = "http://comicsapp.altervista.org/delete_product.php";
    //url to download info
    private static String url_downloadinfo_product = "http://comicsapp.altervista.org/check_product.php";
    //url to check product
    private static String url_check_product = "http://comicsapp.altervista.org/check_product.php";

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        productsList = new ArrayList<HashMap<String, String>>();

        //Creo i bottoni
        Button btnCreateProduct = (Button) findViewById(R.id.invia);
        Button btnUpdate = (Button) findViewById(R.id.modifica);
        Button btnDelete = (Button) findViewById(R.id.elimina);

        //Rendo i bottoni invisibili

        btnDelete.setVisibility(View.GONE);
        btnUpdate.setVisibility(View.GONE);

        //prendo i valori

        match = getIntent().getExtras().getInt("match");
        barcode = getIntent().getExtras().getString("barcode");
        /*titolo = getIntent().getExtras().getString("titolo");
        autore = getIntent().getExtras().getString("autore");
        disegnatore = getIntent().getExtras().getString("disegnatore");
        casa_ed = getIntent().getExtras().getString("casa_ed");
        anno = getIntent().getExtras().getString("anno");
        gen = getIntent().getExtras().getString("gen");
        prezzo = getIntent().getExtras().getString("prezzo");
        quantita = getIntent().getExtras().getInt("quantita");
        descr = getIntent().getExtras().getString("descr");*/

        //prendo gli editTExt
        editText = (EditText) findViewById(R.id.edit_barcode);
        inputTitolo = (EditText) findViewById(R.id.edit_name);
        inputAutore = (EditText) findViewById(R.id.edit_autore);
        inputDisegnatore = (EditText) findViewById(R.id.edit_disegnatore);
        inputCasa_ed = (EditText) findViewById(R.id.edit_casa_ed);
        inputAnno = (EditText) findViewById(R.id.edit_date);
        inputGen = (EditText) findViewById(R.id.edit_genere);
        inputPrezzo = (EditText) findViewById(R.id.edit_prezzo);
        inputQuantita = (EditText) findViewById(R.id.edit_quantita);
        inputDescr = (EditText) findViewById(R.id.edit_descr);

        if(match==1){

            btnCreateProduct.setVisibility(View.GONE);
            btnDelete.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.VISIBLE);
            editText.setKeyListener(null);

            new DownloadInfo().execute();

            /*inputTitolo.setText(titolo);
            inputAutore.setText(autore);
            inputDisegnatore.setText(disegnatore);
            inputCasa_ed.setText(casa_ed);
            inputAnno.setText(anno);
            inputGen.setText(gen);
            inputPrezzo.setText(prezzo);
            inputQuantita.setText(String.valueOf(quantita));
            inputDescr.setText(descr);*/
        }

        if (barcode != null){
            editText.setText(barcode);
        }







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

        //update click event
        btnUpdate.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(FormActivity.this);
                                                    builder.setTitle("Attenzione!");
                                                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                                                    builder.setMessage("Sei sicuro di voler modificare l'elemento? \n ");
                                                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
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
                                                            new UpdateProduct().execute(codB,titolo,autore,disegnatore,casa_ed,anno,gen,prezzo,quantita,descr);
                                                        }
                                                    });
                                                    builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    });
                                                    final AlertDialog alert = builder.create();
                                                    alert.show();

                                                }
                                            }

        );

        //delete click event
        btnDelete.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {

                                             AlertDialog.Builder builder = new AlertDialog.Builder(FormActivity.this);
                                             builder.setTitle("Attenzione!");
                                             builder.setIcon(android.R.drawable.ic_dialog_alert);
                                             builder.setMessage("Sei sicuro di voler eliminare l'elemento? \n ");
                                             builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                 public void onClick(DialogInterface dialog, int which) {
                                                     // creating new product in background thread
                                                     String codB = editText.getText().toString();
                                                     new DeleteProduct().execute(codB);
                                                 }
                                             });
                                             builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                 public void onClick(DialogInterface dialog, int which) {

                                                 }
                                             });
                                             final AlertDialog alert = builder.create();
                                             alert.show();
                                         }
                                     }

        );
    }

    //Per quando ruoto il dispositivo
    @Override
    protected void onStart() {
        super.onStart();



    }

    /**
     * Background Async Task to Create new product
     * */
    class CreateNewProduct extends AsyncTask<String, String, List<NameValuePair>> {


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
        protected List<NameValuePair> doInBackground(String... args) {

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
            params.add(new BasicNameValuePair("quantita", quantita));
            params.add(new BasicNameValuePair("descrizione", descr));

            Log.d("params ",params.toString());
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_check_product,
                    "POST", params);


            // check log cat fro response
            Log.d("Create Response", json.toString());
            

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 0) {

                    // No match
                    JSONObject json2 = jsonParser.makeHttpRequest(url_create_product,"POST",params);
                    // check log cat fro response
                    Log.d("Create Response", json2.toString());

                    Intent i = new Intent(getApplicationContext(), Aggiungi.class);
                    startActivity(i);
                    finish();

                }

                if (success == 1){
                    return params;
                }




            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(final List<NameValuePair> params) {
            // dismiss the dialog once done
            pDialog.dismiss();

            if(params != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FormActivity.this);
                builder.setTitle("Attenzione!");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage("Elemento già inserito, continuare con la modifica? \n ");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        new Thread(new Runnable() {
                            public void run() {
                                JSONObject json = jsonParser.makeHttpRequest(url_update_product, "POST", params);
                                Log.d("Create Response", json.toString());
                            }
                        }).start();



                        Intent i = new Intent(getApplicationContext(), Aggiungi.class);
                        startActivity(i);

                        // closing this screen
                        finish();
                    }
                });
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent i = new Intent(getApplicationContext(), Aggiungi.class);
                        startActivity(i);
                        //closing this screen
                        finish();


                    }
                });
                final AlertDialog alert = builder.create();
                FormActivity.this.runOnUiThread(new java.lang.Runnable() {
                    public void run() {
                        //show AlertDialog
                        alert.show();
                    }
                });


            }

        }
    }


    //Update Product class

    class UpdateProduct extends AsyncTask<String, String, String> {


        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FormActivity.this);
            pDialog.setMessage("Updating product...");
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
            params.add(new BasicNameValuePair("quantita", quantita));
            params.add(new BasicNameValuePair("descrizione", descr));



            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_product,
                    "POST", params);


            // check log cat fro response
            Log.d("Update", json.toString());


            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), Aggiungi.class);
                    startActivity(i);


                }




            } catch (JSONException e) {
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

    //Delete Product Class

    class DeleteProduct extends AsyncTask<String, String, String> {


        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FormActivity.this);
            pDialog.setMessage("Deleting Product...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {

            String codB = args[0];

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("cod_b", codB));

            Log.d("params",params.toString());

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_delete_product,
                    "POST", params);

            // check log cat fro response
            Log.d("Delete ", json.toString());


            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), Aggiungi.class);
                    startActivity(i);

                }




            } catch (JSONException e) {
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

    //Download informations

    class DownloadInfo extends AsyncTask <String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FormActivity.this);
            pDialog.setMessage("Downloading Information...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("cod_b", barcode));

            Log.d("cod_b",barcode);
            Log.d("params",params.toString());

            JSONObject json2 = jsonParser.makeHttpRequest(url_downloadinfo_product,"POST", params);
            Log.d("Risultato ", json2.toString());

            try{

                int success = json2.getInt(TAG_SUCCESS);

                if (success == 1) {

                    product = json2.getJSONArray(TAG_PRODUCT);

                    JSONObject check = product.getJSONObject(0);

                    Log.d("check",check.toString());

                    String cod_b = check.getString(TAG_COD_B);
                    String titolo = check.getString(TAG_TITOLO);
                    String autore = check.getString(TAG_AUTORE);
                    String disegnatore = check.getString(TAG_DISEGNATORE);
                    String casa_ed = check.getString(TAG_CASA_ED);
                    String anno = check.getString(TAG_ANNO);
                    String gen = check.getString(TAG_GEN);
                    String prezzo = check.getString(TAG_PREZZO);
                    int quantita = check.getInt(TAG_QUANTITA);
                    String descr = check.getString(TAG_DESCR);

                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    map.put(TAG_COD_B, cod_b);
                    map.put(TAG_TITOLO, titolo);
                    map.put(TAG_AUTORE, autore);
                    map.put(TAG_DISEGNATORE, disegnatore);
                    map.put(TAG_CASA_ED, casa_ed);
                    map.put(TAG_ANNO, anno);
                    map.put(TAG_GEN, gen);
                    map.put(TAG_PREZZO, prezzo);
                    map.put(TAG_QUANTITA, String.valueOf(quantita));
                    map.put(TAG_DESCR, descr);

                    productsList.add(map);

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
            editText.setText(productsList.get(0).get(TAG_COD_B));
            inputTitolo.setText(productsList.get(0).get(TAG_TITOLO));
            inputAutore.setText(productsList.get(0).get(TAG_AUTORE));
            inputDisegnatore.setText(productsList.get(0).get(TAG_DISEGNATORE));
            inputCasa_ed.setText(productsList.get(0).get(TAG_CASA_ED));
            inputAnno.setText(productsList.get(0).get(TAG_ANNO));
            inputGen.setText(productsList.get(0).get(TAG_GEN));
            inputPrezzo.setText(productsList.get(0).get(TAG_PREZZO));
            inputQuantita.setText(productsList.get(0).get(TAG_QUANTITA));
            inputDescr.setText(productsList.get(0).get(TAG_DESCR));
            titolo = productsList.get(0).get(TAG_COD_B);

        

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

    public void showPricePickerDialog(View v){
        hideKeyboard(this);
        DialogFragment newFragment = new PricePickerFragment();
        newFragment.show(getSupportFragmentManager(),"pricepicker");
    }

    public void showNumberPickerDialog(View v){

        hideKeyboard(this);
        DialogFragment newFragment = new NumberPickerFragment();
        newFragment.show(getSupportFragmentManager(),"numberpicker");
    }

    public void showDatePickerDialog(View v) {
        hideKeyboard(this);
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(),"datepicker");
    }



    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



}
