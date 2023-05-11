package com.example.skillswap.fragments;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillswap.R;
import com.example.skillswap.adapters.SearchSkillAdapter;
import com.example.skillswap.models.MySkillViewModel;
import com.example.skillswap.models.Result;
import com.example.skillswap.models.Skill;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Locale;

public class AddSkillsFragment extends Fragment {
    private RecyclerView searchSkillRecyclerView;
    private TextInputEditText skillTie;
    private MySkillViewModel mMySkillViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_skills, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMySkillViewModel = new ViewModelProvider(requireActivity()).get(MySkillViewModel.class);
        searchSkillRecyclerView = view.findViewById(R.id.skillRecyclerView);
        skillTie = view.findViewById(R.id.skillTie);


        String[] sports = getResources().getStringArray(R.array.Sports);
        String[] arts = getResources().getStringArray(R.array.Arts);
        String[] languages = getResources().getStringArray(R.array.Languages);
        String[] general = getResources().getStringArray(R.array.General);
        String[] music = getResources().getStringArray(R.array.Music);
        ArrayList<Skill> skillList = new ArrayList<>();

        for (String skill : sports) {
            skillList.add(new Skill(skill, "sports"));
        }
        for (String skill : arts) {
            skillList.add(new Skill(skill, "arts"));
        }
        for (String skill : music) {
            skillList.add(new Skill(skill, "music"));
        }
        for (String skill : languages) {
            skillList.add(new Skill(skill, "languages"));
        }
        for (String skill : general) {
            skillList.add(new Skill(skill, "general"));
        }
        SearchSkillAdapter adapter = new SearchSkillAdapter(skillList, new SearchSkillAdapter.OnItemClickListener() {
            @Override
            public void onSkillClicked(Skill skill) {
                mMySkillViewModel.setSelectedSkill(skill, mMySkillViewModel.mSkillTypeLiveData.getValue(), new Result.CallBack() {
                    @Override
                    public void response(Result result) {
                        if (result != null) {
                            if (result instanceof Result.Success) {
                                NavHostFragment.findNavController(AddSkillsFragment.this).navigateUp();
                            } else {
                                String errorMessage = ((Result.Error) result).getError().getMessage();
                                Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
        searchSkillRecyclerView.setAdapter(adapter);


        skillTie.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable skill) {
                if (skill != null && skill.length() > 0) {
                    String enteredSkillStr = skill.toString();
                    ArrayList<Skill> newList = new ArrayList<>();
                    for (Skill item : skillList) {
                        if (item.getSkill().toLowerCase(Locale.getDefault()).startsWith(enteredSkillStr.toLowerCase(Locale.getDefault())) || item.getCategory().toLowerCase(Locale.getDefault()).startsWith(enteredSkillStr.toLowerCase(Locale.getDefault()))) {
                            newList.add(item);
                        }
                    }
                    adapter.setSkillList(newList);
                } else {
                    adapter.setSkillList(skillList);
                }
            }
        });
    }
}