package com.intigate.offers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.intigate.emall.R;
import com.intigate.setup.Login;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

import listner.Listener_service;
import listner.PostData;
import listner.Toast;
import utils.PrefUtils;


public class Offers extends Activity implements Listener_service {

    Vector<Integer> v_id = null;

    Spinner spinner1;
    Vector<String> imgUrl;
    Vector<Integer> OfferId;
    GridView grid_offers;


    ArrayAdapter<String> adapter_spinner_cmp;
    ImageAdapter adapter;
    boolean chk = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offers);
        if (v_id == null) {
            v_id = new Vector<>();
            spinner1 = (Spinner) findViewById(R.id.spinner1);

            grid_offers = (GridView) findViewById(R.id.grid_offers);
            getCom();

        } else {

            spinner1 = (Spinner) findViewById(R.id.spinner1);
            spinner1.setAdapter(adapter_spinner_cmp);
            grid_offers = (GridView) findViewById(R.id.grid_offers);
            grid_offers.setAdapter(adapter);


            spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    if (position == 0 && chk) {

                    } else {
                        chk = false;
                        getOffer(v_id.get(position));
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }


    public void getCom() {


        JSONObject object = new JSONObject();
        try {

            object.put("SimNumber", new PrefUtils().getFromPrefs(getApplicationContext().getApplicationContext(), PrefUtils.SimNumber, ""));
            object.put("UDID", new PrefUtils().getFromPrefs(getApplicationContext(), PrefUtils.UDID
                    , ""));


        } catch (Exception e) {
        }


        new PostData(0, object.toString(), "GetCompany", Offers.this).execute();

    }

    public void setUpSpinner(int method, String response) {
        switch (method) {

            case 0:
                try {
                    JSONArray array = new JSONArray(response);
                    String[] items = new String[array.length()];
                    for (int i = 0; i < array.length(); i++) {
                        if (array.getJSONObject(i).getString("isValid").equals("true")) {

                        } else {
                            new PrefUtils().saveToPrefs(getApplicationContext(), "IsLoggedIn", "0");
                            Intent ii = new Intent(getApplicationContext(), Error.class);
                            startActivity(ii);
                            this.finish();


                            break;
                        }
                        items[i] = array.getJSONObject(i).getString("Name");
                        v_id.add(array.getJSONObject(i).getInt("Id"));
                    }
                    adapter_spinner_cmp = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_adapter, items);

                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            spinner1.setAdapter(adapter_spinner_cmp);

                        }
                    });

                    spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                            chk = false;
                            getOffer(v_id.get(position));


                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    public void setUpGrid(String response) {
        imgUrl = new Vector<>();
        OfferId = new Vector<>();
        try {
            JSONArray arr = new JSONArray(response);

            for (int i = 0; i < arr.length(); i++) {
                if (arr.getJSONObject(i).getString("isValid").equals("true")) {

                } else {
                    new PrefUtils().saveToPrefs(getApplicationContext(), "IsLoggedIn", "0");
                    Intent ii = new Intent(getApplicationContext(), Error.class);
                    startActivity(ii);
                    this.finish();


                    break;
                }
                imgUrl.add(arr.getJSONObject(i).getString("OfferImagePath"));
                OfferId.add(arr.getJSONObject(i).getInt("OfferId"));
            }

            adapter = new ImageAdapter(getApplicationContext(), imgUrl);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    grid_offers.setAdapter(adapter);
                }
            });

            grid_offers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {



                    Intent inten_detal = new Intent(Offers.this, Offers_Detail.class);
                    inten_detal.putExtra("id", OfferId.get(i));
                    startActivity(inten_detal);

                    chk = true;

                }
            });
        } catch (
                Exception e
                )

        {
        }
    }

    public void getOffer(int cmp_id) {


        JSONObject object = new JSONObject();
        try {

            object.put("SimNumber", new PrefUtils().getFromPrefs(getApplicationContext(), PrefUtils.SimNumber, ""));
            object.put("UDID", new PrefUtils().getFromPrefs(getApplicationContext(), PrefUtils.UDID, ""));
            object.put("CompanyID", cmp_id);
            object.put("IndexNumber", 0);


        } catch (Exception e) {

            e.printStackTrace();
        }

        new PostData(10, object.toString(), "GetOffers", Offers.this).execute();
    }


    @Override
    public void onRequestSuccess(int method, String response) {
        switch (method) {
            case 0:
                setUpSpinner(method, response);
                break;

            case 10:
                setUpGrid(response);
                break;
        }

    }

    @Override
    public void onRequestFail(int method, String message) {

        Offers.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Toast(getApplicationContext(), "Please check your N/w Connection").show();
            }
        });

    }

    @Override
    public void onStatus404() {
        Offers.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Toast(getApplicationContext(), "You are a invalid user").show();
                new PrefUtils().saveToPrefs(getApplicationContext(), "IsLoggedIn", "0");
                Intent i = new Intent(Offers.this, Login.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void back(View v) {
        finish();
    }


    @Override
    protected void onPause() {
        super.onPause();

        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
    }
}
