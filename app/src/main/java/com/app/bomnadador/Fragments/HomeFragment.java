package com.app.bomnadador.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.bomnadador.APP.ExamActivity;
import com.app.bomnadador.APP.MainActivity;
import com.app.bomnadador.Classes.Questions;
import com.app.bomnadador.DataBase.DataBase;
import com.app.bomnadador.DataBase.Statistic;
import com.app.bomnadador.DataBase.StatisticDAO;
import com.app.bomnadador.R;
import com.app.bomnadador.databinding.FragmentConfigBinding;
import com.app.bomnadador.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DataBase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = DataBase.getDataBase(requireContext());

        binding.btnExamNormal.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirmação")
                    .setMessage("Deseja iniciar o Exame Normal?")
                    .setPositiveButton("Sim", (dialog, which) -> startNormalExam())
                    .setNegativeButton("Não", null)
                    .show();
        });

        binding.btnExamWrong.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirmação")
                    .setMessage("Deseja iniciar o Exame de Perguntas Erradas?")
                    .setPositiveButton("Sim", (dialog, which) -> startWrongQuestionsExam())
                    .setNegativeButton("Não", null)
                    .show();
        });
g
    }

    public void startNormalExam() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            ArrayList<Questions> questions = mainActivity.sendAPIdata();
            int api_size_all = questions.size();

            Collections.shuffle(questions);
            questions = new ArrayList<>(questions.subList(0, Math.min(40, questions.size())));

            Intent intent = new Intent(getActivity(), ExamActivity.class);
            intent.putExtra("questions", questions);
            intent.putExtra("api_size_all", api_size_all);
            startActivityForResult(intent, 1);
        }
    }

    public void startWrongQuestionsExam() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {

            StatisticDAO statisticDAO = database.estatisticasExamesDao();
            Statistic estatistica = statisticDAO.getStatistic();

            List<String> listaPerguntas = estatistica.getListQuestionsIncorrect() == null || estatistica.getListQuestionsIncorrect().isEmpty()
                    ? new ArrayList<>()
                    : new ArrayList<>(Arrays.asList(Objects.requireNonNullElse(estatistica.getListQuestionsIncorrect(), "").split(",")));

            ArrayList<Questions> questions = mainActivity.sendAPIdata();
            ArrayList<Questions> questionsWrong = new ArrayList<>();

            for (String idStr : listaPerguntas) {
                int id = Integer.parseInt(idStr);
                for (Questions question : questions) {
                    if (Objects.equals(question.getId(), id)) {
                        questionsWrong.add(question);
                        break;
                    }
                }
            }

            if (questionsWrong.isEmpty()) {
                Collections.shuffle(questions);
                questionsWrong = new ArrayList<>(questions.subList(0, Math.min(40, questions.size())));
            } else if (questionsWrong.size() < 40) {
                ArrayList<Questions> remainingQuestions = new ArrayList<>(questions);
                remainingQuestions.removeAll(questionsWrong);
                Collections.shuffle(remainingQuestions);

                while (questionsWrong.size() < 40 && !remainingQuestions.isEmpty()) {
                    questionsWrong.add(remainingQuestions.remove(0));
                }
            } else {
                Collections.shuffle(questionsWrong);
                questionsWrong = new ArrayList<>(questionsWrong.subList(0, 40));
            }

            Intent intent = new Intent(getActivity(), ExamActivity.class);
            intent.putExtra("questions", questionsWrong);
            intent.putExtra("api_size_all", questionsWrong.size());
            startActivityForResult(intent, 1);
        }
    }

}