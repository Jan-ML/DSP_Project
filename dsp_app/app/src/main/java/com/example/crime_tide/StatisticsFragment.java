package com.example.crime_tide;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RetryPolicy;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.lang.reflect.Type;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class StatisticsFragment extends Fragment {
    String [] year = {"2022", "2023", "2024"};
    String[] months = {};
    int forecast_limit = 0;
    AutoCompleteTextView autoCompleteTextView_year;
    ArrayAdapter<String> arrayAdapter_year;
    GraphView graphView;
    Button predicted_crimes;
    private RequestQueue mQueue;
    private RequestQueue results;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_statistics, container, false);
        String[] selected_year = new String[1];
        selected_year = getYear(v);
        predicted_crimes = v.findViewById(R.id.show_prediction);
        graphView = (GraphView) v.findViewById(R.id.graph);
        mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        results = Volley.newRequestQueue(getActivity().getApplicationContext());
        String[] finalSelected_year = selected_year;
        predicted_crimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(finalSelected_year[0].equals("2022")){
                    forecast_limit = 60;
                    months = new String[]{"2018", "2019", "2020", "2021", "2022"};
                }
                else if(finalSelected_year[0].equals("2023")){
                    forecast_limit = 72;
                    months = new String[]{"2018", "2019", "2020", "2021", "2022", "2023"};
                }
                else{
                    forecast_limit = 84;
                    months = new String[]{"2018", "2019", "2020", "2021", "2022",
                            "2023", "2024"};
                }
                jsonParse(new CallBack() {
                    @Override
                    public void onSuccess(Forecast forecast) {
                        System.out.println(forecast_limit);
                        System.out.println(finalSelected_year[0]);
                        checkForecast(graphView, forecast, v);
                    }
                    @Override
                    public void onFail(String msg) {

                        System.out.println("Failed");
                    }
                });
            }
        });

        return v;
    }

    private void jsonParse(final CallBack onCallBack){
        Forecast forecast =  new Forecast();;
        final JSONObject[] feed = new JSONObject[1];
        String url = "https://gres-four.azurewebsites.net/feed";
        String url_predict = "https://azure-lstm-five.azurewebsites.net/predict";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    feed[0] = response;
                    JSONArray jsonArray = response.getJSONArray("feed");
                    JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.POST,url_predict, feed[0], new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("prediction");
                                System.out.println("Call");
                                System.out.println(jsonArray.get(1));
                                for(int i = 0; i < forecast_limit; i++){
                                    int crime = jsonArray.getInt(i);
                                    forecast.prediction.add(crime);

                                }
                                System.out.println("In scope");
                                System.out.println(forecast.prediction);
                                onCallBack.onSuccess(forecast);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    request1.setRetryPolicy(new RetryPolicy() {
                        @Override
                        public int getCurrentTimeout() {
                            return 5000;
                        }

                        @Override
                        public int getCurrentRetryCount() {
                            return 5000;
                        }

                        @Override
                        public void retry(VolleyError error) throws VolleyError {

                        }
                    });
                    mQueue.add(request1);

                } catch (JSONException e) {
                    e.printStackTrace();
                    onCallBack.onFail("Failed");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 5000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 5000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        mQueue.add(request);
    }
    public void checkForecast(GraphView graphView, Forecast forecast1, View v){
        System.out.println("Check");
        System.out.println(forecast1.prediction);
        DataPoint[] dp = new DataPoint[forecast1.prediction.size()];
        for(int i=0;i<forecast1.prediction.size();i++) {
            dp[i] = new DataPoint(i, forecast1.prediction.get(i));
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dp);
        graphView.addSeries(series);
        series.setColor(Color.rgb(183, 132, 40));
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
        staticLabelsFormatter.setHorizontalLabels(months);
        graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScrollableY(true);
        graphView.getViewport().setScalableY(true);

    }


    private String[] getYear(View v){
        // Select Year
        final String[] selected_year = new String[1];
        autoCompleteTextView_year = v.findViewById(R.id.auto_complete_txt_year);
        arrayAdapter_year = new ArrayAdapter<String>(getActivity(),R.layout.list_item,year);
        autoCompleteTextView_year.setAdapter(arrayAdapter_year);
        autoCompleteTextView_year.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getActivity(), "Year: " + item, Toast.LENGTH_SHORT).show();
                selected_year[0] = item;
            }
        });
        return selected_year;
    }

    public interface CallBack {
        void onSuccess(Forecast forecast);
        void onFail(String msg);
    }



}
