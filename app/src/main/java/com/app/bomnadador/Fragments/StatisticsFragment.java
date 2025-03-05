package com.app.bomnadador.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.bomnadador.R;
import com.app.bomnadador.databinding.FragmentHomeBinding;
import com.app.bomnadador.databinding.FragmentStatisticsBinding;

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}