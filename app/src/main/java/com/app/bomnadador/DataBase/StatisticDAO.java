package com.app.bomnadador.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface StatisticDAO {
    @Insert
    void insert(Statistic statistic);

    @Query("SELECT * FROM statistic LIMIT 1")
    Statistic getStatistic();

    //==========================//
    //==========================//
    @Query("UPDATE statistic SET totalExamesDone = totalExamesDone + 1 WHERE id = :id")
    void addExamesDone(int id);
    @Query("UPDATE statistic SET totalExamesPass = totalExamesPass + 1 WHERE id = :id")
    void addExamesPass(int id);
    @Query("UPDATE statistic SET totalExamesFail = totalExamesFail + 1 WHERE id = :id")
    void addExamesFail(int id);
    //==========================//
    //==========================//
    @Query("UPDATE statistic SET totalQuestionsDone = totalQuestionsDone + :quantidade WHERE id = :id")
    void addQuestionsDone(int id, int quantidade);
    @Query("UPDATE statistic SET totalQuestionsUnDone = totalQuestionsUnDone + :quantidade WHERE id = :id")
    void addQuestionsUnDone(int id, int quantidade);
    @Query("UPDATE statistic SET totalQuestionsCorrect = totalQuestionsCorrect + :quantidade WHERE id = :id")
    void addQuestionsCorrect(int id, int quantidade);
    @Query("UPDATE statistic SET totalQuestionsIncorrect = totalQuestionsIncorrect + :quantidade WHERE id = :id")
    void addQuestionsIncorrect(int id, int quantidade);
    //==========================//
    //==========================//
    @Query("UPDATE statistic SET listQuestionsIncorrect = :listQuestionsIncorrect WHERE id = :id")
    void updateQuestionsIncorrect(int id, String listQuestionsIncorrect);
    @Query("UPDATE statistic SET listQuestionsSave = :listQuestionsSave WHERE id = :id")
    void updateQuestionsSave(int id, String listQuestionsSave);
    @Query("UPDATE statistic SET listExamesTime = :listExamesTime WHERE id = :id")
    void updateExamesTime(int id, String listExamesTime);
}