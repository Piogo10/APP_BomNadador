package com.app.bomnadador.APP;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.bomnadador.Classes.Questions;
import com.app.bomnadador.DataBase.DataBase;
import com.app.bomnadador.DataBase.Statistic;
import com.app.bomnadador.DataBase.StatisticDAO;
import com.app.bomnadador.Fragments.ConfigFragment;
import com.app.bomnadador.Fragments.HomeFragment;
import com.app.bomnadador.Fragments.StatisticsFragment;
import com.app.bomnadador.R;
import com.app.bomnadador.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    //---------------------------//
    HomeFragment homeFragment = new HomeFragment();
    StatisticsFragment statisticsFragment = new StatisticsFragment();
    ConfigFragment configFragment = new ConfigFragment();
    //---------------------------//
    private ArrayList<Questions> questions;
    private DataBase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();

        binding.navHome.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
        });

        binding.navStatistics.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, statisticsFragment).commit();
        });

        binding.navConfig.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, configFragment).commit();
        });

        database = DataBase.getDataBase(this);
        StatisticDAO statisticDAO = database.estatisticasExamesDao();
        checkDataBase(statisticDAO);

        questions = new ArrayList<>();
        getAPIdata();
    }

    private void checkDataBase(StatisticDAO statisticDAO){
        Statistic estatistica = statisticDAO.getStatistic();
        if (estatistica == null) {
            estatistica = new Statistic(
                    0,
            0,
            0,
            0,
            0,
            -1,
            0,
            0,
            "",
            "",
            "",
            ""
            );
            statisticDAO.insert(estatistica);
            statisticDAO.getStatistic();
        }
    }

    private void getAPIdata() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api-lg-7u3l.onrender.com/api/questions/all";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        ArrayList<Questions> listaPerguntas = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            int id = obj.getInt("id");
                            int capitulo = obj.getInt("capitulo");
                            String pergunta = obj.getString("pergunta");
                            JSONArray opcoes = obj.getJSONArray("opcoes");
                            ArrayList<String> opcoeslist = new ArrayList<>();

                            for (int j = 0; j < opcoes.length(); j++) {
                                opcoeslist.add(opcoes.getString(j));
                            }

                            String respCerta = obj.getString("resposta");
                            listaPerguntas.add(new Questions(id, capitulo, pergunta, opcoeslist, respCerta));
                        }

                        questions.clear();
                        questions.addAll(listaPerguntas);

                        Toast.makeText(this, "API DONE", Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "size: " + listaPerguntas.size(), Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        retryAPICall(queue, this);
                    }
                },
                error -> {
                    Log.d("API_LOG", "Error: " + error);
                    Toast.makeText(this, "API ERRO", Toast.LENGTH_SHORT).show();
                    retryAPICall(queue, this);
                }
        );

        queue.add(stringRequest);
    }

    private void retryAPICall(RequestQueue queue, Context context) {
        new Handler(Looper.getMainLooper()).postDelayed(this::getAPIdata, 1000);
        Toast.makeText(context, "Tentar dar GET outravez...", Toast.LENGTH_SHORT).show();
    }
    public ArrayList<Questions> sendAPIdata() {
        return questions;
    }
}