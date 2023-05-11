package com.example.skillswap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillswap.models.MySkillViewModel;
import com.example.skillswap.R;
import com.example.skillswap.models.Result;
import com.example.skillswap.models.Skill;
import com.example.skillswap.adapters.SkillAdapter;
import com.example.skillswap.adapters.TeachSkillAdapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillswap.R;

import java.util.List;

public class MySkillsFragment extends Fragment {


    private RecyclerView learnSkillRecyclerView, teachSkillRecyclerView;
    private Button learnAddSkillBtn, teachAddSkillBtn;
    private MySkillViewModel mMySkillViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_skills, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMySkillViewModel = new ViewModelProvider(requireActivity()).get(MySkillViewModel.class);

        learnSkillRecyclerView = view.findViewById(R.id.learnSkillRecyclerView);
        teachSkillRecyclerView = view.findViewById(R.id.teachSkillRecyclerView);
        learnAddSkillBtn = view.findViewById(R.id.learnAddSkillBtn);
        teachAddSkillBtn = view.findViewById(R.id.teachAddSkillBtn);


        SkillAdapter adapter = new SkillAdapter(null, new SkillAdapter.OnItemClickListener() {
            @Override
            public void onSkillClicked(String skill) {
                new AlertDialog.Builder(requireContext())
                        .setMessage("Do you want to delete the skill "+skill+"?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                mMySkillViewModel.removeSkillForUser(skill, MySkillViewModel.SkillType.LEARN_SKILL, new Result.CallBack() {
                                    @Override
                                    public void response(Result result) {
                                        if (result != null) {
                                            if (result instanceof Result.Error) {
                                                String errorMessage = ((Result.Error) result).getError().getMessage();
                                                Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();
            }
        });
        learnSkillRecyclerView.setAdapter(adapter);
        TeachSkillAdapter teachAdapter = new TeachSkillAdapter(null, new TeachSkillAdapter.OnItemClickListener() {
            @Override
            public void onSkillClicked(String skill) {
                new AlertDialog.Builder(requireContext())
                        .setMessage("Do you want to delete the skill " + skill + "?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                mMySkillViewModel.removeSkillForUser(skill, MySkillViewModel.SkillType.TEACH_SKILL, new Result.CallBack() {
                                    @Override
                                    public void response(Result result) {
                                        if (result != null) {
                                            if (result instanceof Result.Error) {
                                                String errorMessage = ((Result.Error) result).getError().getMessage();
                                                Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();
            }
        });
        teachSkillRecyclerView.setAdapter(teachAdapter);

        learnAddSkillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMySkillViewModel.setSelectedSkillType(MySkillViewModel.SkillType.LEARN_SKILL);
                NavHostFragment.findNavController(MySkillsFragment.this).navigate(R.id.action_mySkillsFragment_to_addSkillsFragment);
            }
        });


        mMySkillViewModel.mListLiveData.observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> skills) {
                if (skills.size() >= 4)
                    learnAddSkillBtn.setVisibility(View.GONE);
                else
                    learnAddSkillBtn.setVisibility(View.VISIBLE);
                adapter.setSkillsList(skills);
            }

        });

        teachAddSkillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMySkillViewModel.setSelectedSkillType(MySkillViewModel.SkillType.TEACH_SKILL);
                NavHostFragment.findNavController(MySkillsFragment.this).navigate(R.id.action_mySkillsFragment_to_addSkillsFragment);
            }
        });

        mMySkillViewModel.tListLiveData.observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> skills) {
                if (skills.size() >= 4)
                    teachAddSkillBtn.setVisibility(View.GONE);
                else
                    teachAddSkillBtn.setVisibility(View.VISIBLE);
                teachAdapter.setSkillsList(skills);
            }

        });

        mMySkillViewModel.getAllUserSkills(new Result.CallBack() {
            @Override
            public void response(Result result) {
                if (result != null) {
                    if (result instanceof Result.Success) {
                    } else {
                        String errorMessage = ((Result.Error) result).getError().getMessage();
                        Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

}