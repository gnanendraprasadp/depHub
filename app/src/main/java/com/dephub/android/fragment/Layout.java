package com.dephub.android.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.dephub.android.R;
import com.dephub.android.cardview.CardAdapter;
import com.dephub.android.cardview.CardModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Layout extends Fragment {
    public static final String url = "https://gnanendraprasadp.github.io/DepHub-Web/json/dependency.json";
    private RecyclerView cardRecyclerViewLayout;
    private ArrayList<CardModel> cardLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        View view = inflater.inflate(R.layout.activity_fragment_layout, container, false);

        cardRecyclerViewLayout = view.findViewById(R.id.layoutFragment);
        setRetainInstance(true);

        cardLayout = new ArrayList<>();

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutFragment);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardLayout.clear();
                        swipeRefreshLayout.setRefreshing(false);
                        getDependency();
                    }
                }, 3000);
            }
        });

        getDependency();

        buildCardView();

        return view;
    }

    private void getDependency() {
        @SuppressWarnings("ConstantConditions") RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.getCache().clear();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        String type = jsonObject.getString("type");

                        if (type.equals("Layout")) {
                            String dependencyName = jsonObject.getString("dependency_name");
                            String developerName = jsonObject.getString("developer_name");
                            String fullName = jsonObject.getString("full_name");
                            String githubLink = jsonObject.getString("github_link");
                            @SuppressLint("ResourceType")
                            String bg = getString(R.color.whitetoblack);
                            String youtube = jsonObject.getString("youtube_link");
                            String id = jsonObject.getString("id");
                            String license = jsonObject.getString("license");
                            String licenseLink = jsonObject.getString("license_link");
                            cardLayout.add(new CardModel(dependencyName, developerName, githubLink, bg, youtube, id, license, licenseLink, fullName));

                            Collections.sort(cardLayout, new Comparator<CardModel>() {
                                @Override
                                public int compare(CardModel o1, CardModel o2) {
                                    return o1.getDependencyName().compareTo(o2.getDependencyName());
                                }
                            });

                            buildCardView();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        jsonArrayRequest.setShouldCache(false);
        requestQueue.add(jsonArrayRequest);
    }

    private void buildCardView() {
        CardAdapter cardViewAdapterLayout = new CardAdapter(cardLayout, getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        cardRecyclerViewLayout.setHasFixedSize(true);
        cardRecyclerViewLayout.setLayoutManager(linearLayoutManager);
        cardRecyclerViewLayout.setAdapter(cardViewAdapterLayout);
    }
}