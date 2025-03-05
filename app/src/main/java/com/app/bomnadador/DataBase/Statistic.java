package com.app.bomnadador.DataBase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import kotlin.text.UStringsKt;

@Entity(tableName = "statistic")
public class Statistic implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    //==========================//
    //==========================//
    private int totalExamesDone;
    private int totalExamesPass;
    private int totalExamesFail;
    //==========================//
    //==========================//
    private int totalQuestionsDone;
    private int totalQuestionsUnDone;
    private int totalQuestionsCorrect;
    private int totalQuestionsIncorrect;
    //==========================//
    //==========================//
    private String listQuestionsNewDone;
    private String listQuestionsIncorrect;
    private String listQuestionsSave;
    private String listExamesTime;

    public Statistic(int id, int totalExamesDone, int totalExamesPass, int totalExamesFail, int totalQuestionsDone, int totalQuestionsUnDone, int totalQuestionsCorrect, int totalQuestionsIncorrect, String listQuestionsNewDone, String listQuestionsIncorrect, String listQuestionsSave, String listExamesTime) {
        this.id = id;
        this.totalExamesDone = totalExamesDone;
        this.totalExamesPass = totalExamesPass;
        this.totalExamesFail = totalExamesFail;
        this.totalQuestionsDone = totalQuestionsDone;
        this.totalQuestionsUnDone = totalQuestionsUnDone;
        this.totalQuestionsCorrect = totalQuestionsCorrect;
        this.totalQuestionsIncorrect = totalQuestionsIncorrect;
        this.listQuestionsNewDone = listQuestionsNewDone;
        this.listQuestionsIncorrect = listQuestionsIncorrect;
        this.listQuestionsSave = listQuestionsSave;
        this.listExamesTime = listExamesTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotalExamesDone() {
        return totalExamesDone;
    }

    public void setTotalExamesDone(int totalExamesDone) {
        this.totalExamesDone = totalExamesDone;
    }

    public int getTotalExamesPass() {
        return totalExamesPass;
    }

    public void setTotalExamesPass(int totalExamesPass) {
        this.totalExamesPass = totalExamesPass;
    }

    public int getTotalExamesFail() {
        return totalExamesFail;
    }

    public void setTotalExamesFail(int totalExamesFail) {
        this.totalExamesFail = totalExamesFail;
    }

    public int getTotalQuestionsDone() {
        return totalQuestionsDone;
    }

    public void setTotalQuestionsDone(int totalQuestionsDone) {
        this.totalQuestionsDone = totalQuestionsDone;
    }

    public int getTotalQuestionsUnDone() {
        return totalQuestionsUnDone;
    }

    public void setTotalQuestionsUnDone(int totalQuestionsUnDone) {
        this.totalQuestionsUnDone = totalQuestionsUnDone;
    }

    public int getTotalQuestionsCorrect() {
        return totalQuestionsCorrect;
    }

    public void setTotalQuestionsCorrect(int totalQuestionsCorrect) {
        this.totalQuestionsCorrect = totalQuestionsCorrect;
    }

    public int getTotalQuestionsIncorrect() {
        return totalQuestionsIncorrect;
    }

    public void setTotalQuestionsIncorrect(int totalQuestionsIncorrect) {
        this.totalQuestionsIncorrect = totalQuestionsIncorrect;
    }

    public String getListQuestionsNewDone() {
        return listQuestionsNewDone;
    }

    public void setListQuestionsNewDone(String listQuestionsNewDone) {
        this.listQuestionsNewDone = listQuestionsNewDone;
    }

    public String getListQuestionsIncorrect() {
        return listQuestionsIncorrect;
    }

    public void setListQuestionsIncorrect(String listQuestionsIncorrect) {
        this.listQuestionsIncorrect = listQuestionsIncorrect;
    }

    public String getListQuestionsSave() {
        return listQuestionsSave;
    }

    public void setListQuestionsSave(String listQuestionsSave) {
        this.listQuestionsSave = listQuestionsSave;
    }

    public String getListExamesTime() {
        return listExamesTime;
    }

    public void setListExamesTime(String listExamesTime) {
        this.listExamesTime = listExamesTime;
    }
}
