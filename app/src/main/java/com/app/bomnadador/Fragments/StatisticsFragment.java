package com.app.bomnadador.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.bomnadador.APP.MainActivity;
import com.app.bomnadador.Classes.Questions;
import com.app.bomnadador.DataBase.DataBase;
import com.app.bomnadador.DataBase.Statistic;
import com.app.bomnadador.DataBase.StatisticDAO;
import com.app.bomnadador.R;
import com.app.bomnadador.databinding.FragmentHomeBinding;
import com.app.bomnadador.databinding.FragmentStatisticsBinding;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;
    private DataBase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = DataBase.getDataBase(getContext());

        StatisticDAO statisticDAO = database.estatisticasExamesDao();
        Statistic estatistica = statisticDAO.getStatistic();

        binding.totalExamesDone.setText("Total Exames Feitos: " + estatistica.getTotalExamesDone());
        binding.totalExamesPass.setText("Total Exames Aprovados: " + estatistica.getTotalExamesPass());
        binding.totalExamesFail.setText("Total Exames Reprovados: " + estatistica.getTotalExamesFail());

        binding.totalQuestionsDone.setText("Total Questões Feitas: " + estatistica.getTotalQuestionsDone());

        if (estatistica.getTotalQuestionsUnDone() == -1){
            MainActivity mainActivity = (MainActivity) getActivity();
            ArrayList<Questions> questions = mainActivity.sendAPIdata();
            statisticDAO.updateQuestionsUnDone(estatistica.getId(), questions.size());
            binding.totalQuestionsUnDone.setText("Total Questões Não Feitas: " + questions.size());
        }
        else
            binding.totalQuestionsUnDone.setText("Total Questões Não Feitas: " + estatistica.getTotalQuestionsUnDone());

        binding.totalQuestionsCorrect.setText("Total Questões Corretas: " + estatistica.getTotalQuestionsCorrect());
        binding.totalQuestionsIncorrect.setText("Total Questões Incorretas: " + estatistica.getTotalQuestionsIncorrect());

    }
}