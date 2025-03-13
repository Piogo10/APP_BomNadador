package com.app.bomnadador.Fragments;

import android.animation.LayoutTransition;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import com.app.bomnadador.APP.MainActivity;
import com.app.bomnadador.DataBase.DataBase;
import com.app.bomnadador.DataBase.Statistic;
import com.app.bomnadador.DataBase.StatisticDAO;
import com.app.bomnadador.R;
import com.app.bomnadador.databinding.FragmentStatisticsBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;
    private DataBase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = DataBase.getDataBase(requireContext());

        StatisticDAO statisticDAO = database.estatisticasExamesDao();
        Statistic estatistica = statisticDAO.getStatistic();

        binding.layoutExames.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        binding.layoutPerguntas.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        binding.layoutExames.setOnClickListener(v -> toggleDetails(binding.detailsExames));
        binding.layoutPerguntas.setOnClickListener(v -> toggleDetails(binding.detailsQuestions));


        showStatistics(estatistica);

    }


    private void showStatistics(Statistic estatistica){
        MainActivity mainActivity = (MainActivity) getActivity();

        int totalExamesPass = estatistica.getTotalExamesPass();
        int totalExamesFail = estatistica.getTotalExamesFail();
        int totalExamesDone = estatistica.getTotalExamesDone();
        int totalQuestionsCorrect = estatistica.getTotalQuestionsCorrect();
        int totalQuestionsIncorrect = estatistica.getTotalQuestionsIncorrect();
        int totalQuestionsUnDone = estatistica.getTotalQuestionsUnDone();
        int totalQuestionsDone = estatistica.getTotalQuestionsDone();

        Integer yellowColor = R.color.yellow_dark;
        Integer redColor = R.color.red;
        Integer blackColor = R.color.black;
        Integer blueColor = R.color.main_blue;
        Integer greenColor = R.color.green;
        Integer greyColor = R.color.grey;

        updateStatistics(totalExamesPass, totalExamesFail, totalExamesDone, totalQuestionsCorrect, totalQuestionsIncorrect, totalQuestionsDone, totalQuestionsUnDone);

        if (totalExamesDone == 0)
            setupChart(binding.chartExames, 100, 0, greyColor, blackColor, "");
        else
            setupChart(binding.chartExames, totalExamesPass, totalExamesFail, yellowColor, redColor, "");

        if (totalQuestionsCorrect == 0 && totalQuestionsIncorrect == 0)
            setupChart(binding.chartQuestionsRight, 100, 0, greyColor, blackColor, "");
        else
            setupChart(binding.chartQuestionsRight, totalQuestionsCorrect, totalQuestionsIncorrect, greenColor, redColor, "");

        if (totalQuestionsDone == 0 && totalQuestionsUnDone == mainActivity.sendAPIsize())
            setupChart(binding.chartQuestionsAwnser, 100, 0, blueColor, yellowColor, "0%");
        else
            setupChart(binding.chartQuestionsAwnser, totalQuestionsUnDone, totalExamesDone, blueColor, yellowColor, String.valueOf((int) ((double) totalQuestionsDone / (totalQuestionsDone + totalQuestionsUnDone) * 100)+"%"));

        int nivelPreparacao = calcularPreparacao(totalQuestionsDone, totalQuestionsCorrect, totalExamesDone, totalExamesPass);
        setupChart(binding.chartReady, nivelPreparacao, 100 - nivelPreparacao, yellowColor, greyColor, String.valueOf(nivelPreparacao)+"%");

        binding.txtExameTempoMedio.setText(calcularTempoMedio(estatistica)+" min");
    }

    private int calcularPreparacao(int totalQuestionsDone, int totalQuestionsCorrect, int totalExamesDone, int totalExamesPass) {
        if (totalExamesDone == 0) {
            return 0;
        }

        double precisaoRespostas = ((double) totalQuestionsCorrect / totalQuestionsDone) * 100;
        double taxaSucessoExames = totalExamesDone > 0 ? ((double) totalExamesPass / totalExamesDone) * 100 : 0;
        double experienciaPeso = Math.log(totalQuestionsDone) * 25;
        double falhasQuestions = ((double) (totalQuestionsDone - totalQuestionsCorrect) / totalQuestionsDone) * 100;
        double falhasExames = ((double) (totalExamesDone - totalExamesPass) / totalExamesDone) * 100;


        double resultadoFinal =
                (precisaoRespostas * 0.15) + // 15% (Precissão nas Perguntas)
                (taxaSucessoExames * 0.60) + // 60% (Taxa de Sucesso nos Exames)
                (experienciaPeso * 0.25) - // 25% (Experiencia, quantidade de Exames Feitos)
                (falhasQuestions * 0.05) - // -5% (Penalizações nas Perguntas Erradas)
                (falhasExames * 0.15); // -15% (Penalizações nos Exames Reprovados)

        return (int) resultadoFinal;
    }


    private void toggleDetails(ScrollView detailsView) {
        int visibility = (detailsView.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
        TransitionManager.beginDelayedTransition((ViewGroup) detailsView.getParent(), new AutoTransition());
        detailsView.setVisibility(visibility);
    }

    private String calcularTempoMedio(Statistic estatistica){

        List<String> listaPerguntas = estatistica.getListExamesTime() == null || estatistica.getListExamesTime().isEmpty()
                ? new ArrayList<>()
                : new ArrayList<>(Arrays.asList(estatistica.getListExamesTime().split(",")));

        if (listaPerguntas.isEmpty()) {
            return "00:00";
        }

        int totalSegundos = 0;
        int numTempos = listaPerguntas.size();

        for (String tempo : listaPerguntas) {
            String[] partes = tempo.split(":");
            int minutos = Integer.parseInt(partes[0]);
            int segundos = Integer.parseInt(partes[1]);
            totalSegundos += (minutos * 60) + segundos;
        }

        int tempoMedioSegundos = totalSegundos / numTempos;

        int minutosMedios = tempoMedioSegundos / 60;
        int segundosMedios = tempoMedioSegundos % 60;

        return String.format("%02d:%02d", minutosMedios, segundosMedios);
    }

    private void updateStatistics(int examesAprovados, int examesReprovados, int examesRealizados, int questionsAprovados, int questionsReprovados, int questionsRealizados, int questionsNaoRealizados) {
        binding.txtExameAprovados.setText(String.valueOf(examesAprovados));
        binding.txtExameReprovados.setText(String.valueOf(examesReprovados));
        binding.txtExameRealizados.setText(String.valueOf(examesRealizados));

        binding.txtQuestionsAprovadas.setText(String.valueOf(questionsAprovados));
        binding.txtQuestionsReprovados.setText(String.valueOf(questionsReprovados));
        binding.txtQuestionsRealizados.setText(String.valueOf(questionsRealizados));
        binding.txtQuestionsNaoRealizados.setText(String.valueOf(questionsNaoRealizados));
    }

    private void setupChart(PieChart chart, int valor1, int valor2, Integer color1, Integer color2, String msg) {

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        colors.add(ContextCompat.getColor(requireContext(), color1));
        colors.add(ContextCompat.getColor(requireContext(), color2));

        entries.add(new PieEntry((float) valor1, ""));
        entries.add(new PieEntry((float) valor2, ""));
        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setColors(colors);
        dataSet.setDrawValues(false);

        PieData data = new PieData(dataSet);
        chart.setData(data);
        chart.setCenterText(msg);
        chart.setCenterTextSize(18f);
        chart.setCenterTextColor(Color.BLACK);
        chart.setHighlightPerTapEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateY(1000);
        chart.invalidate();
    }
}
