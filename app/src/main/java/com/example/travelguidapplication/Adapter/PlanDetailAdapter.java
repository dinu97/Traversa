package com.example.travelguidapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.travelguidapplication.Model.Plan;
import com.example.travelguidapplication.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlanDetailAdapter extends RecyclerView.Adapter<PlanDetailAdapter.PlanDetailViewHolder>  {

    private Context context;
    private ArrayList<Plan> planDetailList;
    private final String url = "URL";
    private final String appid = "API_KEY";
    private DecimalFormat df = new DecimalFormat("#.##");

    private AdView mAdView;

    public PlanDetailAdapter(ArrayList<Plan> planDetailList, Context context,AdView mAdView) {
        this.planDetailList=planDetailList;
        this.context=context;
        this.mAdView=mAdView;
    }

    @NonNull
    @Override
    public PlanDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_plan_place,parent,false);

        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return new PlanDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanDetailViewHolder holder, int position) {

            Plan plan=planDetailList.get(position);

            String tempUrl=GetWeatherDetails(plan.getPlaceLatitude(),plan.getPlaceLongitude());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String description = jsonObjectWeather.getString("description");
                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                    double temp = jsonObjectMain.getDouble("temp") - 273.15;
                    double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                    float pressure = jsonObjectMain.getInt("pressure");
                    int humidity = jsonObjectMain.getInt("humidity");
                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    String wind = jsonObjectWind.getString("speed");
                    JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all");
                    JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                    String countryName = jsonObjectSys.getString("country");
                    String cityName = jsonResponse.getString("name");

                    if (description.equals("clear sky"))
                    {
                        holder.txt_description.setText("Clear Sky");
                        holder.img_weatherImage.setImageDrawable(context.getResources().getDrawable(R.drawable.clear_sky));
                    }else if (description.equals("few clouds"))
                    {
                        holder.txt_description.setText("Few Clouds");
                        holder.img_weatherImage.setImageDrawable(context.getResources().getDrawable(R.drawable.few_clouds));

                    }
                    else if (description.equals("overcast clouds"))
                    {
                        holder.txt_description.setText("Overcast Clouds");
                        holder.img_weatherImage.setImageDrawable(context.getResources().getDrawable(R.drawable.scattered_clouds));
                    }
                    else if (description.equals("scattered clouds"))
                    {
                        holder.txt_description.setText("Scattered Clouds");
                        holder.img_weatherImage.setImageDrawable(context.getResources().getDrawable(R.drawable.scattered_clouds));
                    }
                    else if (description.equals("broken clouds"))
                    {
                        holder.txt_description.setText("Broken Clouds");
                        holder.img_weatherImage.setImageDrawable(context.getResources().getDrawable(R.drawable.broken_clouds));
                    }
                    else if (description.equals("shower rain"))
                    {
                        holder.txt_description.setText("Shower Rain");
                        holder.img_weatherImage.setImageDrawable(context.getResources().getDrawable(R.drawable.shower_rain));
                    }
                    else if (description.equals("rain"))
                    {
                        holder.txt_description.setText("Rain");
                        holder.img_weatherImage.setImageDrawable(context.getResources().getDrawable(R.drawable.rain));
                    }
                    else if (description.equals("thunderstorm"))
                    {
                        holder.txt_description.setText("Thunderstorm");
                        holder.img_weatherImage.setImageDrawable(context.getResources().getDrawable(R.drawable.thunderstorm));
                    }
                    else if (description.equals("snow"))
                    {
                        holder.txt_description.setText("Snow");
                        holder.img_weatherImage.setImageDrawable(context.getResources().getDrawable(R.drawable.snow));
                    }
                    else if (description.equals("mist"))
                    {
                        holder.txt_description.setText("Mist");
                        holder.img_weatherImage.setImageDrawable(context.getResources().getDrawable(R.drawable.mist));
                    }else
                    {
                        holder.txt_description.setText("N/A");
                        holder.img_weatherImage.setImageDrawable(context.getResources().getDrawable(R.drawable.error_cloud));
                    }


                    holder.txt_temperature.setText(df.format(temp) + " °C");
                    holder.txt_humidity.setText(humidity + "%");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

            if (position==0)
            {
                holder.layout_timeDistance.setVisibility(View.GONE);

            }

                holder.txt_planPlaceName.setText(plan.getPlaceName());
                holder.txt_planDistance.setText(plan.getDistance());
                holder.txt_planTime.setText(plan.getTime());

            
    }

    @Override
    public int getItemCount() {
        return planDetailList.size();
    }

    public class PlanDetailViewHolder extends RecyclerView.ViewHolder{

        public TextView txt_planPlaceName,txt_planTime,txt_planDistance,txt_temperature,txt_humidity,txt_description;
        public LinearLayout layout_timeDistance;
        public CircleImageView img_weatherImage;
        public PlanDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_planPlaceName = itemView.findViewById(R.id.textViewPlanPlaceName);
            txt_planTime = itemView.findViewById(R.id.textViewPlanTime);
            txt_planDistance = itemView.findViewById(R.id.textViewPlanDistance);
            layout_timeDistance = itemView.findViewById(R.id.layoutTimeDistance);
            img_weatherImage = itemView.findViewById(R.id.imageViewWeatherImage);
            txt_description = itemView.findViewById(R.id.WeatherDescription);
            txt_temperature = itemView.findViewById(R.id.WeatherTemperature);
            txt_humidity = itemView.findViewById(R.id.WeatherHumidity);

        }
    }

    public String GetWeatherDetails(double latitude, double longitude) {
        String tempUrl = "";
        tempUrl = url + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + appid;
        return tempUrl;

    }



}
