package com.example.skillswap;

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


    private RecyclerView learnSkillRecyclerView,teachSkillRecyclerView;
    private Button learnAddSkillBtn,teachAddSkillBtn;
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


        SkillAdapter adapter = new SkillAdapter(null);
        learnSkillRecyclerView.setAdapter(adapter);
        TeachSkillAdapter teachAdapter = new TeachSkillAdapter(null);
        teachSkillRecyclerView.setAdapter(teachAdapter);

        learnAddSkillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(MySkillsFragment.this).navigate(R.id.action_mySkillsFragment_to_addSkillsFragment);
            }
        });

        mMySkillViewModel.mSkillLiveData.observe(getViewLifecycleOwner(), new Observer<Skill>() {
            @Override
            public void onChanged(Skill skill) {
                if(skill!=null){
                    Toast.makeText(requireContext(), skill.toString(), Toast.LENGTH_SHORT).show();
                    mMySkillViewModel.addToList(skill.getSkill());
                    mMySkillViewModel.resetSkill();
                }
            }
        });
        mMySkillViewModel.mListLiveData.observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> skills) {
                if(skills.size()>4){
                    Toast.makeText(requireContext(), "Only 4 skills can be added!", Toast.LENGTH_SHORT).show();
                    adapter.setSkillsList(skills);
                }else {
                    adapter.setSkillsList(skills);
                }

            }

        });

        teachAddSkillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(MySkillsFragment.this).navigate(R.id.action_mySkillsFragment_to_addSkillsFragment);
            }
        });

        mMySkillViewModel.tSkillLiveData.observe(getViewLifecycleOwner(), new Observer<Skill>() {
            @Override
            public void onChanged(com.example.skillswap.Skill skill) {
                if(skill!=null){
                    Toast.makeText(requireContext(), skill.toString(), Toast.LENGTH_SHORT).show();
                    mMySkillViewModel.addToTeachList(skill.getSkill());
                    mMySkillViewModel.resetTeachSkill();
                }
            }
        });
        mMySkillViewModel.tListLiveData.observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> skills) {
                if(skills.size()>4){
                    Toast.makeText(requireContext(), "Only 4 skills can be added!", Toast.LENGTH_SHORT).show();
                    teachAdapter.setSkillsList(skills);
                }else {
                    teachAdapter.setSkillsList(skills);
                }

            }

        });

    }

}