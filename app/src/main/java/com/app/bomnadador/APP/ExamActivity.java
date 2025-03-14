package com.app.bomnadador.APP;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.bomnadador.Classes.Questions;
import com.app.bomnadador.DataBase.DataBase;
import com.app.bomnadador.DataBase.Statistic;
import com.app.bomnadador.DataBase.StatisticDAO;
import com.app.bomnadador.R;
import com.app.bomnadador.databinding.ActivityExamBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExamActivity extends AppCompatActivity {

    private ActivityExamBinding binding;
    private ArrayList<Questions> questions = new ArrayList<>();
    private int contPerguntas = 0;
    private String respostaSelect = null;
    private String[] respostasDadas;
    private boolean[] perguntasRespondidas;

    private boolean modorevisao = false;

    private Handler handler = new Handler();
    private Runnable runnable;
    private long tempoDecorrido = 0;
    private String tempoFinal = "00:00";

    private DataBase database;

    private int api_size_all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityExamBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        database = DataBase.getDataBase(this);

        binding.btnNext.setOnClickListener(v -> verifyResposta());
        binding.btnLeft.setOnClickListener(v -> navPerguntas(-1));
        binding.btnRight.setOnClickListener(v -> navPerguntas(1));
        binding.btnvoltar.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> savePergunta());

        startExam();
    }

    @Override
    public void finish() {
        if (api_size_all != 0){
            new AlertDialog.Builder(this)
                    .setTitle("Confirmação")
                    .setMessage("Deseja mesmo sair?")
                    .setPositiveButton("Sim", (dialog, which) -> super.finish())
                    .setNegativeButton("Não", null)
                    .show();
        }
        else
            super.finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            handler.removeCallbacks(runnable);
        }
    }

    //===================================//
    //===================================//
    private void startTimer() {
        runnable = new Runnable() {
            @Override
            public void run() {
                tempoDecorrido++;
                updateTimer();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }
    private void updateTimer() {
        int min = (int) (tempoDecorrido / 60);
        int sec = (int) (tempoDecorrido % 60);
        tempoFinal = String.format("%02d:%02d", min, sec);
        binding.txtTimer.setText("⏱  " + tempoFinal);
    }
    //===================================//
    //===================================//
    private void startExam() {
        questions = (ArrayList<Questions>) getIntent().getSerializableExtra("questions");
        api_size_all = getIntent().getIntExtra("api_size_all", 0);

        if (questions == null || questions.isEmpty()) {
            Toast.makeText(this, "Erro ao carregar perguntas", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        respostasDadas = new String[questions.size()];
        perguntasRespondidas = new boolean[questions.size()];

        startTimer();
        showQuestions();
    }
    private void showQuestions() {
        Questions pergunta = questions.get(contPerguntas);
        binding.txtPergunta.setText(pergunta.getPergunta());
        binding.layoutOpcoes.removeAllViews();
        respostaSelect = null;

        for (int i = 0; i < pergunta.getOpcoes().size(); i++) {
            addOpcoes(pergunta.getOpcoes().get(i), i);
        }

        if (modorevisao || perguntasRespondidas[contPerguntas]) {
            addCores();
        }


        binding.btnNext.setEnabled(!perguntasRespondidas[contPerguntas]);
        binding.btnLeft.setVisibility(contPerguntas == 0 ? View.GONE : View.VISIBLE);
        binding.btnRight.setVisibility(contPerguntas < questions.size() - 1 ? View.VISIBLE : View.GONE);


        updateProgressBar();
        updateSaveIcon();
    }
    private void addOpcoes(String textoOpcao, int index) {
        View opcaoView = getLayoutInflater().inflate(R.layout.item_opcao, binding.layoutOpcoes, false);
        TextView letra = opcaoView.findViewById(R.id.textletra);
        TextView pergunta = opcaoView.findViewById(R.id.pergunta);
        LinearLayout layoutOpcao = opcaoView.findViewById(R.id.container);

        letra.setText(String.valueOf((char) ('A' + index)));
        pergunta.setText(textoOpcao);

        layoutOpcao.setOnClickListener(v -> {
            if (!perguntasRespondidas[contPerguntas]) {
                respostaSelect = textoOpcao;
                resetCores();
                layoutOpcao.setBackgroundResource(R.drawable.question_bk_selected);
            }
        });

        binding.layoutOpcoes.addView(opcaoView);
    }
    private void navPerguntas(int direcao) {
        contPerguntas += direcao;
        showQuestions();
    }
    private void verifyResposta() {
        if (respostaSelect == null) {
            Toast.makeText(this, "Selecione uma Opção", Toast.LENGTH_SHORT).show();
            return;
        }

        perguntasRespondidas[contPerguntas] = true;
        respostasDadas[contPerguntas] = respostaSelect;
        binding.btnNext.setEnabled(false);

        verifyNewQuestion();
        savePerguntaErrada();
        addCores();
        if (contPerguntas == questions.size() - 1) {
            binding.btnRight.setVisibility(View.GONE);
        }

        boolean allQuestionsAnswered = true;
        for (boolean respondida : perguntasRespondidas) {
            if (!respondida) {
                allQuestionsAnswered = false;
                break;
            }
        }
        if (allQuestionsAnswered) {
            showResultado();
        }

        updateProgressBar();
    }

    private void verifyNewQuestion(){
        StatisticDAO statisticDAO = database.estatisticasExamesDao();
        Statistic estatistica = statisticDAO.getStatistic();

        Questions perguntaAtual = questions.get(contPerguntas);
        String perguntaId = String.valueOf(perguntaAtual.getId());


        List<String> listaPerguntas = estatistica.getListQuestionsNewDone() == null || estatistica.getListQuestionsNewDone().isEmpty()
                ? new ArrayList<>()
                : new ArrayList<>(Arrays.asList(estatistica.getListQuestionsNewDone().split(",")));

        if (!listaPerguntas.contains(perguntaId)) {
            listaPerguntas.add(perguntaId);

            int totalQuestionsUnDone = api_size_all - listaPerguntas.size();
            statisticDAO.updateQuestionsUnDone(estatistica.getId(), totalQuestionsUnDone);

            String novaStringPerguntas = String.join(",", listaPerguntas);
            statisticDAO.updateQuestionsNewDone(estatistica.getId(), novaStringPerguntas);
        }
    }
    private void showResultado() {
        handler.removeCallbacks(runnable);

        int corretas = 0, erradas = 0;

        for (int i = 0; i < questions.size(); i++) {
            if (respostasDadas[i] != null && respostasDadas[i].equals(questions.get(i).getResposta())) {
                corretas++;
            } else {
                erradas++;
            }
        }

        boolean aprovado = erradas <= 5;
        atualizarEstatisticas(true, aprovado);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_resultado, null);
        TextView txtResultado = dialogView.findViewById(R.id.txtResultado);
        TextView txtErros = dialogView.findViewById(R.id.txtErros);
        TextView tempoFim = dialogView.findViewById(R.id.tempoFinal);
        Button btnRever = dialogView.findViewById(R.id.btnRever);
        Button btnLeave = dialogView.findViewById(R.id.btnLeave);

        txtResultado.setText(aprovado ? "APROVADO" : "REPROVADO");
        txtResultado.setBackgroundColor(getResources().getColor(aprovado ? R.color.green : R.color.red));
        txtErros.setText(erradas + " Erradas");
        tempoFim.setText("⏱  " + tempoFinal);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        btnRever.setOnClickListener(v -> {
            dialog.dismiss();
            contPerguntas = 0;
            modorevisao = true;
            showQuestions();
        });

        btnLeave.setOnClickListener(v -> finish());

        dialog.show();
    }
    //===================================//
    //===================================//
    private void addCores() {
        Questions pergunta = questions.get(contPerguntas);
        String respostaCerta = pergunta.getResposta();
        String respostaDada = respostasDadas[contPerguntas];

        StatisticDAO statisticDAO = database.estatisticasExamesDao();
        Statistic estatistica = statisticDAO.getStatistic();

        statisticDAO.addQuestionsDone(estatistica.getId(), 1);

        if(respostaDada != null && respostaDada.equals(respostaCerta)){
            statisticDAO.addQuestionsCorrect(estatistica.getId(), 1);
        }
        else{
            statisticDAO.addQuestionsIncorrect(estatistica.getId(), 1);
        }


        for (int i = 0; i < binding.layoutOpcoes.getChildCount(); i++) {
            View opcaoView = binding.layoutOpcoes.getChildAt(i);
            TextView txtOpcao = opcaoView.findViewById(R.id.pergunta);
            LinearLayout layoutOpcao = opcaoView.findViewById(R.id.container);

            if (txtOpcao.getText().toString().equals(respostaCerta)) {
                layoutOpcao.setBackgroundResource(R.drawable.question_bk_certa);
            } else if (respostaDada != null && respostaDada.equals(txtOpcao.getText().toString())) {
                layoutOpcao.setBackgroundResource(R.drawable.question_bk_errada);
            }
        }
    }
    private void resetCores() {
        for (int i = 0; i < binding.layoutOpcoes.getChildCount(); i++) {
            View opcaoView = binding.layoutOpcoes.getChildAt(i);
            LinearLayout layoutOpcao = opcaoView.findViewById(R.id.container);
            layoutOpcao.setBackgroundResource(R.drawable.question_bk);
        }
    }
    //===================================//
    //===================================//
    private void updateProgressBar() {
        binding.progressContainer.removeAllViews();
        int total = questions.size();
        int alturaProgressBar = 30;
        int larguraBorda = 3;

        for (int i = 0; i < total; i++) {
            View segmento = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, alturaProgressBar, 1);
            segmento.setLayoutParams(params);

            int corSegmento;
            if (!perguntasRespondidas[i]) {
                corSegmento = getResources().getColor(R.color.grey);
            } else if (respostasDadas[i].equals(questions.get(i).getResposta())) {
                corSegmento = getResources().getColor(R.color.green);
            } else {
                corSegmento = getResources().getColor(R.color.red);
            }

            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(corSegmento);

            float radius = 12f;

            if (i == 0) {
                drawable.setCornerRadii(new float[]{radius, radius, 0, 0, 0, 0, radius, radius});
            } else if (i == total - 1) {
                drawable.setCornerRadii(new float[]{0, 0, radius, radius, radius, radius, 0, 0});
            }

            if (i == contPerguntas) {
                drawable.setStroke(larguraBorda, Color.BLUE);
            }

            segmento.setBackground(drawable);
            binding.progressContainer.addView(segmento);

            if (i < total - 1) {
                View divisor = new View(this);
                LinearLayout.LayoutParams paramsDivisor = new LinearLayout.LayoutParams(2, alturaProgressBar);
                divisor.setLayoutParams(paramsDivisor);
                divisor.setBackgroundColor(Color.WHITE);
                binding.progressContainer.addView(divisor);
            }
        }

        binding.txtProgresso.setText((contPerguntas + 1) + "/" + total);
    }
    //===================================//
    //===================================//
    private void savePergunta() {
        Questions perguntaAtual = questions.get(contPerguntas);
        String perguntaId = String.valueOf(perguntaAtual.getId());

        StatisticDAO statisticDAO = database.estatisticasExamesDao();
        Statistic estatistica = statisticDAO.getStatistic();

        List<String> listaPerguntas = estatistica.getListQuestionsSave() == null || estatistica.getListQuestionsSave().isEmpty()
                ? new ArrayList<>()
                : new ArrayList<>(Arrays.asList(estatistica.getListQuestionsSave().split(",")));

        boolean perguntaRemovida = listaPerguntas.remove(perguntaId);
        if (!perguntaRemovida) {
            listaPerguntas.add(perguntaId);
        }

        String novaStringPerguntas = String.join(",", listaPerguntas);
        statisticDAO.updateQuestionsSave(estatistica.getId(), novaStringPerguntas);

        Toast.makeText(this, perguntaRemovida ? "Pergunta removida dos guardados" : "Pergunta guardada", Toast.LENGTH_SHORT).show();
        updateSaveIcon();
    }
    private void saveTime() {

        StatisticDAO statisticDAO = database.estatisticasExamesDao();
        Statistic estatistica = statisticDAO.getStatistic();

        List<String> listaTime = estatistica.getListExamesTime() == null || estatistica.getListExamesTime().isEmpty()
                ? new ArrayList<>()
                : new ArrayList<>(Arrays.asList(estatistica.getListExamesTime().split(",")));

        listaTime.add(tempoFinal);

        String novaStringPerguntas = String.join(",", listaTime);
        statisticDAO.updateExamesTime(estatistica.getId(), novaStringPerguntas);

    }
    private void savePerguntaErrada() {
        Questions perguntaAtual = questions.get(contPerguntas);
        String perguntaId = String.valueOf(perguntaAtual.getId());

        StatisticDAO statisticDAO = database.estatisticasExamesDao();
        Statistic estatistica = statisticDAO.getStatistic();

        List<String> listaPerguntas = estatistica.getListQuestionsIncorrect() == null || estatistica.getListQuestionsIncorrect().isEmpty()
                ? new ArrayList<>()
                : new ArrayList<>(Arrays.asList(estatistica.getListQuestionsIncorrect().split(",")));

        if (!respostasDadas[contPerguntas].equals(perguntaAtual.getResposta())) {
            if (!listaPerguntas.contains(perguntaId)) {
                listaPerguntas.add(perguntaId);
            }
        } else {
            listaPerguntas.remove(perguntaId);
        }

        String novaStringPerguntas = String.join(",", listaPerguntas);
        statisticDAO.updateQuestionsIncorrect(estatistica.getId(), novaStringPerguntas);
    }
    private void updateSaveIcon() {
        StatisticDAO statisticDAO = database.estatisticasExamesDao();
        Statistic estatistica = statisticDAO.getStatistic();

        boolean isPerguntaGuardada = false;
        if (estatistica != null) {
            String perguntasGuardadas = estatistica.getListQuestionsSave();
            String perguntaId = String.valueOf(questions.get(contPerguntas).getId());
            isPerguntaGuardada = perguntasGuardadas != null &&
                    !perguntasGuardadas.isEmpty() &&
                    Arrays.asList(perguntasGuardadas.split(",")).contains(perguntaId);
        }

        binding.btnSave.setImageResource(isPerguntaGuardada ? R.drawable.ic_mark_fill : R.drawable.ic_mark);
    }
    //===================================//
    //===================================//
    private void atualizarEstatisticas(boolean exameFinalizado, boolean aprovado) {

        StatisticDAO statisticDAO = database.estatisticasExamesDao();
        Statistic estatistica = statisticDAO.getStatistic();

        if (exameFinalizado) {
            statisticDAO.addExamesDone(estatistica.getId());
            saveTime();
            if (aprovado) {
                statisticDAO.addExamesPass(estatistica.getId());
            } else {
                statisticDAO.addExamesFail(estatistica.getId());
            }
        }
    }
    //===================================//
    //===================================//
}