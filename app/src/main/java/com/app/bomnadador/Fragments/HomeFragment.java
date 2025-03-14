package com.app.bomnadador.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

        binding.btnExamCapitulo.setOnClickListener(v -> showCapituloDialog());
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


    public void showCapituloDialog() {
        EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Insira o número do capítulo (1-9)");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Escolher Capítulo")
                .setMessage("Digite o número do capítulo que você deseja")
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            int capituloEscolhido = Integer.parseInt(input.getText().toString());

                            if (capituloEscolhido >= 1 && capituloEscolhido <= 9) {
                                startCapituloExam(capituloEscolhido);
                            } else {
                                Toast.makeText(getActivity(), "Capítulo inválido! Escolha entre 1 e 9.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(getActivity(), "Por favor, insira um número válido.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    public void startCapituloExam(int capituloEscolhido) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            ArrayList<Questions> questions = mainActivity.sendAPIdata();
            ArrayList<Questions> questionsFiltradas = new ArrayList<>();

            for (Questions question : questions) {
                if (question.getCapitulo() == capituloEscolhido) {
                    questionsFiltradas.add(question);
                }
            }

            if (questionsFiltradas.isEmpty()) {
                return;
            }

            Collections.shuffle(questionsFiltradas);

            Intent intent = new Intent(getActivity(), ExamActivity.class);
            intent.putExtra("questions", questionsFiltradas);
            intent.putExtra("api_size_all", questionsFiltradas.size());
            startActivityForResult(intent, 1);
        }
    }


}