package com.example.sanofrank.comicsappback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Visualizza extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

    // url to get all products list
    private static String url_all_products = "http://comicsapp.altervista.org/get_all_products.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_PID = "cod_b";
    private static final String TAG_NAME = "titolo";

    // products JSONArray
    JSONArray products = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_visualizza);

        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();

        // Loading products in Background Thread
        new LoadAllProducts().execute();

        // Get listview
        ListView lv = getListView();
    }
        class LoadAllProducts extends AsyncTask<String, String, String> {

            /**
             * Before starting background thread Show Progress Dialog
             * */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(Visualizza.this);
                pDialog.setMessage("Loading products. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            protected String doInBackground(String[] args) {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                // getting JSON string from URL
                JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);

                // Check your log cat for JSON reponse
                Log.d("All Products: ", json.toString());

                try {
                    // Checking for SUCCESS TAG
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // products found
                        // Getting Array of Products

                        products = json.getJSONArray(TAG_PRODUCTS);

                        // looping through All Products
                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);

                            // Storing each json item in variable
                            String id = c.getString(TAG_PID);
                            String name = c.getString(TAG_NAME);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_PID, id);
                            map.put(TAG_NAME, name);

                            // adding HashList to ArrayList
                            productsList.add(map);

                        }
                    } else {
                        // no products found
                        // Launch Add New product Activity
                        Intent i = new Intent(getApplicationContext(),
                                Aggiungi.class);
                        // Closing all previous activities
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            /**
             * After completing background task Dismiss the progress dialog
             * **/
            protected void onPostExecute(String file_url) {
                // dismiss the dialog after getting all products

                pDialog.dismiss();
                // updating UI from Background Thread

                runOnUiThread(new Runnable() {
                    public void run() {
                        /**
                         * Updating parsed JSON data into ListView
                         * */
                        ListAdapter adapter = new SimpleAdapter(
                                Visualizza.this, productsList,
                                R.layout.list_item, new String[]{TAG_PID,
                                TAG_NAME},
                                new int[]{R.id.cod_b, R.id.titolo});
                        // updating listview
                        setListAdapter(adapter);
                    }
                });
            }



            }

}
