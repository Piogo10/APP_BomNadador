package com.app.bomnadador.Classes;

import java.io.Serializable;
import java.util.ArrayList;


public class Questions implements Serializable {

    private int id;
    private int capitulo;
    private String pergunta;
    private ArrayList<String> opcoes;
    private String resposta;

    public Questions(int id, int capitulo, String pergunta, ArrayList<String> opcoes, String resposta) {
        this.id = id;
        this.capitulo = capitulo;
        this.pergunta = pergunta;
        this.opcoes = opcoes;
        this.resposta = resposta;
    }

    public int getId() {
        return id;
    }

    public int getCapitulo() {
        return capitulo;
    }

    public String getPergunta() {
        return pergunta;
    }

    public ArrayList<String> getOpcoes() {
        return opcoes;
    }

    public String getResposta() {
        return resposta;
    }
}
