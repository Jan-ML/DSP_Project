package com.example.crime_tide;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OccurredFragment extends Fragment {
    String[] crimes = {"All", "Public order", "Anti-social behaviour", "Violence and sexual offences",
            "Criminal damage and arson","Burglary","Drugs","Vehicle crime",
            "Shoplifting","Bicycle theft", "Robbery", "Other theft", "Other crime",
            "Theft from the person","Possession of weapons"};
    String [] year = {"2021", "2020", "2019", "2018"};
    String [] district = {"All", "Southville", "Harbourside and Hotwells", "Brislington East", "Clifton",
            "St George Troopers Hill", "Brislington West", "Bedminster", "Windmill",
            "Old City Docks", "Stokes Croft", "Filwood", "Knowle", "Bishopsworth",
            "St George West", "Easton and Redfield", "Stoke Bishop", "Barton Hill",
            "Broadmead", "St George Central", "Clifton Down", "Eastville", "Cotham",
            "Hartcliffe and Withywood", "Trinity", "Redcliffe and Temple",
            "Hengrove and Whitchurch", "Dings Walk", "Stockwood", "Kingsweston",
            "Southmead", "Henbury and Brentry", "Avonmouth and Shirehampton", "St Pauls",
            "Montpelier and St Werburghs", "Redland", "Bishopston", "Hillfields",
            "Henleaze and Westbury-on-Trym", "Lockleaze and Eastgate", "Frome Vale",
            "Horfield", "Patchway and Cribbs Causeway"};
    //Select Boxes
    AutoCompleteTextView autoCompleteTextView_crime;
    ArrayAdapter<String> arrayAdapter_crime;

    AutoCompleteTextView autoCompleteTextView_district;
    ArrayAdapter<String> arrayAdapter_district;

    AutoCompleteTextView autoCompleteTextView_year;
    ArrayAdapter<String> arrayAdapter_year;

//    ArrayList barArraylist;
    Button occurred_crimes;

    private RequestQueue mQueue;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String[] selected_crime = new String[1];
        String[] selected_district = new String[1];
        String[] selected_year = new String[1];

        View v = inflater.inflate(R.layout.fragment_occuerred, container, false);
        BarChart barChart = v.findViewById(R.id.barchart);

        // Select a Crime
        selected_crime = getCrime(v);
        String[] finalSelected_crime = selected_crime;

        // Select a District
        selected_district = getDistrict(v);
        String[] finalSelected_district = selected_district;

        // Select a Year
        selected_year = getYear(v);
        String[] finalSelected_year = selected_year;

        // Pass Data
        occurred_crimes = v.findViewById(R.id.show_occurred_crimes);
        occurred_crimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonParse(new CallBack() {
                    @Override
                    public void onSuccess(OccurredCrimes occurredCrimes) {
                        System.out.println("True");
                        System.out.println("Out of scope");
                        System.out.println(occurredCrimes.occurred_crimes);
                        System.out.println(occurredCrimes.occurred_month);
                        generateGraph(barChart, occurredCrimes, v);
                    }

                    @Override
                    public void onFail(String msg) {
                        System.out.println("False");
                    }
                }, finalSelected_crime[0], finalSelected_district[0],
                        finalSelected_year[0]);

            }

        });


        return v;
    }

    private void jsonParse(final CallBack callBack, String crime, String district,
                           String year) {
        OccurredCrimes oc = new OccurredCrimes();
        Map<String, Object> data=new HashMap<String, Object>();
        Map<String,Object> send_request=new HashMap<String, Object>();
        data.put("Year", year);
        data.put("District", district);
        data.put("Crime_type", crime);
        send_request.put("crime", data);
        JSONObject feed = new JSONObject(send_request);
        String url = "https://gres-four.azurewebsites.net/feed";
        System.out.println(feed);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, feed, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = response.getJSONObject("crime");
                    JSONArray crime_array = (JSONArray) jsonObject.get("Crime_number");
                    JSONArray date_array = (JSONArray) jsonObject.get("Date");
                    for(int i = 0; i < crime_array.length(); i++){
                        int crime = crime_array.getInt(i);
                        String month = date_array.getString(i);
                        oc.occurred_crimes.add(crime);
                        oc.occurred_month.add(month);
                    }
                    System.out.println("In Scope");
                    System.out.println(oc.occurred_crimes);
                    System.out.println(oc.occurred_month);
                    callBack.onSuccess(oc);
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

    private void show_occurrence(String c, String d, String y){
        Map<String, Object> data=new HashMap<String, Object>();
        Map<String,Object> send_request=new HashMap<String, Object>();
        Toast.makeText(getActivity(), "Year: " + c, Toast.LENGTH_SHORT).show();

    }




    private void generateGraph(BarChart barChart, OccurredCrimes occurredCrimes, View v){
        System.out.println("Occurred Crimes");
        int sub_str = 5;
        final ArrayList<String> xLabel = new ArrayList<>();
        for(int i = 0; i < occurredCrimes.occurred_month.size(); i++){
           String month = occurredCrimes.occurred_month.get(i).substring(sub_str);
           month = convertMonth(month);
           xLabel.add(month);
        }
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabel));
        xAxis.setTextColor(Color.WHITE);
        xAxis.setLabelCount(xLabel.size());

        ArrayList barArraylist = null;
        System.out.println(occurredCrimes.occurred_month);
        barArraylist = getData(barArraylist, occurredCrimes);
        BarDataSet barDataSet = new BarDataSet(barArraylist, null);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barChart.setGridBackgroundColor(Color.WHITE);
//        barChart.getXAxis().setEnabled(true);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisRight().setTextColor(Color.WHITE);
        //Colour Bar data set
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        //Text colour
        barDataSet.setValueTextColor(Color.WHITE);
        //Setting text size
        barChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        barChart.getAxisRight().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        barDataSet.setDrawValues(false);
        barDataSet.setValueTextSize(12);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
//        barChart.getLegend().setTextColor(Color.WHITE);
    }

    private ArrayList getData(ArrayList barArraylist, OccurredCrimes occurredCrimes){
        barArraylist = new ArrayList();
        for(int i = 0; i < occurredCrimes.occurred_month.size(); i++){
            barArraylist.add(new BarEntry(i,
                    occurredCrimes.occurred_crimes.get(i)));
        }
        return barArraylist;
    }

    private String convertMonth(String m){
        switch (m){
            case "01":
                m = "Jan";
                break;
            case "02":
                m = "Feb";
                break;
            case "03":
                m = "Mar";
                break;
            case "04":
                m = "Apr";
                break;
            case "05":
                m = "May";
                break;
            case "06":
                m = "Jun";
                break;
            case "07":
                m = "Jul";
                break;
            case "08":
                m = "Aug";
                break;
            case "09":
                m = "Sept";
                break;
            case "10":
                m = "Oct";
                break;
            case "11":
                m = "Nov";
                break;
            case "12":
                m = "Dec";
                break;
            default:
                System.out.println("Month does not exist");
        }
        return m;
    }

    private String[] getCrime(View v){
        // Select Crime
        final String[] selected_crime = new String[1];
        autoCompleteTextView_crime = v.findViewById(R.id.auto_complete_txt_crime_occ);
        arrayAdapter_crime = new ArrayAdapter<String>(getActivity(),R.layout.list_item,crimes);
        autoCompleteTextView_crime.setAdapter(arrayAdapter_crime);
        autoCompleteTextView_crime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getActivity(), "Crime: " + item, Toast.LENGTH_SHORT).show();
                selected_crime[0] = item;
            }
        });
        return selected_crime;
    }

    private String[] getDistrict(View v){
        // Select District
        final String[] selected_district = new String[1];
        autoCompleteTextView_district = v.findViewById(R.id.auto_complete_txt_district_occ);
        arrayAdapter_district = new ArrayAdapter<String>(getActivity(),R.layout.list_item,district);
        autoCompleteTextView_district.setAdapter(arrayAdapter_district);
        autoCompleteTextView_district.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getActivity(), "District: " + item, Toast.LENGTH_SHORT).show();
                selected_district[0] = item;
            }
        });
        return selected_district;
    }

    private String[] getYear(View v){
        // Select Year
        final String[] selected_year = new String[1];
        autoCompleteTextView_year = v.findViewById(R.id.auto_complete_txt_year_occ);
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
        void onSuccess(OccurredCrimes occurredCrimes);
        void onFail(String msg);
    }

}
