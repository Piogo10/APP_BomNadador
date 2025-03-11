package com.app.bomnadador.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.bomnadador.APP.ExamActivity;
import com.app.bomnadador.APP.MainActivity;
import com.app.bomnadador.Classes.Questions;
import com.app.bomnadador.R;
import com.app.bomnadador.databinding.FragmentConfigBinding;
import com.app.bomnadador.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnExam.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirmação")
                    .setMessage("Deseja iniciar o exame?")
                    .setPositiveButton("Sim", (dialog, which) -> {
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
                    })
                    .setNegativeButton("Não", null)
                    .show();
        });
    }

}