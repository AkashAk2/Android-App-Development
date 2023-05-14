package com.example.skillswap.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillswap.R;
import com.example.skillswap.adapters.SkillAdapter;
import com.example.skillswap.adapters.TeachSkillAdapter;
import com.example.skillswap.databinding.FragmentMySkillsBinding;
import com.example.skillswap.models.MySkillViewModel;
import com.example.skillswap.models.Result;
import com.example.skillswap.models.Skill;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.slider.LabelFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class MySkillsFragment extends Fragment {


    private RecyclerView learnSkillRecyclerView, teachSkillRecyclerView;
    private Button learnAddSkillBtn, teachAddSkillBtn;
    private MySkillViewModel mMySkillViewModel;
    private Map<String, Integer> skillCatCountLearnSkill = new HashMap<>();
    private List<Skill> mLearnSkills = new ArrayList<>();
    private List<Skill> mTeachSkills = new ArrayList<>();
    private FragmentMySkillsBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentMySkillsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMySkillViewModel = new ViewModelProvider(requireActivity()).get(MySkillViewModel.class);

        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Update the username TextView
        TextView usernameTextView = view.findViewById(R.id.username_textview);
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.hasChild("firstName")) {
                        String firstName = snapshot.child("firstName").getValue(String.class);
                        usernameTextView.setText(firstName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle the error
                }
            });

            Spinner roleSpinner = view.findViewById(R.id.role_spinner);
            ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(requireContext(),
                    R.array.roles, android.R.layout.simple_spinner_item);

            roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Get the selected role
                    String role = parent.getItemAtPosition(position).toString();

                    // Update the user role in Firebase
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        String uid = currentUser.getUid();
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
                        usersRef.child("role").setValue(role);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Do nothing
                }
            });

            roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            roleSpinner.setAdapter(roleAdapter);


            learnSkillRecyclerView = view.findViewById(R.id.learnSkillRecyclerView);
            teachSkillRecyclerView = view.findViewById(R.id.teachSkillRecyclerView);
            learnAddSkillBtn = view.findViewById(R.id.learnAddSkillBtn);
            teachAddSkillBtn = view.findViewById(R.id.teachAddSkillBtn);
        }


        SkillAdapter adapter = new SkillAdapter(null, new SkillAdapter.OnItemClickListener() {
            @Override
            public void onSkillClicked(Skill skill) {
                new AlertDialog.Builder(requireContext())
                        .setMessage("Do you want to delete the skill " + skill.getSkill() + "?")
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
            public void onSkillClicked(Skill skill) {
                new AlertDialog.Builder(requireContext())
                        .setMessage("Do you want to delete the skill " + skill.getSkill() + "?")
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


        mMySkillViewModel.mListLiveData.observe(getViewLifecycleOwner(), new Observer<List<Skill>>() {
            @Override
            public void onChanged(List<Skill> skills) {
                if(skills.stream().filter(Skill::isEnabled).count() >= 4)
                    learnAddSkillBtn.setVisibility(View.GONE);
                else
                    learnAddSkillBtn.setVisibility(View.VISIBLE);
                adapter.setSkillsList(skills.stream().filter(Skill::isEnabled).collect(Collectors.toList()));

                mLearnSkills.clear();
                mLearnSkills.addAll(skills);
                populateGraphs();


            }

        });

        teachAddSkillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMySkillViewModel.setSelectedSkillType(MySkillViewModel.SkillType.TEACH_SKILL);
                NavHostFragment.findNavController(MySkillsFragment.this).navigate(R.id.action_mySkillsFragment_to_addSkillsFragment);
            }
        });

        mMySkillViewModel.tListLiveData.observe(getViewLifecycleOwner(), new Observer<List<Skill>>() {
            @Override
            public void onChanged(List<Skill> skills) {
                if (skills.stream().filter(Skill::isEnabled).count() >= 4)
                    teachAddSkillBtn.setVisibility(View.GONE);
                else
                    teachAddSkillBtn.setVisibility(View.VISIBLE);
                teachAdapter.setSkillsList(skills.stream().filter(Skill::isEnabled).collect(Collectors.toList()));
                for (Skill skill : skills) {
                    Log.d("TAG", skill.toString());
                }
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


        mBinding.startDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance(Locale.getDefault());
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                        mBinding.startDateEt.setText(dateFormat.format(cal.getTime()));
                        populateGraphs();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mBinding.endDateEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance(Locale.getDefault());
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                        mBinding.endDateEdittext.setText(dateFormat.format(cal.getTime()));
                        populateGraphs();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mBinding.toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayGraphsOrChart();
            }
        });


    }

    private void populateGraphs() {
        String startDate = mBinding.startDateEt.getText().toString();
        String endDate = mBinding.endDateEdittext.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        if (!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
            try {
                long startDateTime = dateFormat.parse(startDate).getTime();
                long endDateTime = dateFormat.parse(endDate).getTime();
                skillCatCountLearnSkill.clear();
                for (Skill skill : mLearnSkills) {
                    long addedDate = dateFormat.parse(dateFormat.format(skill.getAddedDate())).getTime();
                    if (addedDate >= startDateTime && addedDate <= endDateTime) {
                        String category = skill.getCategory();
                        if (!skillCatCountLearnSkill.containsKey(category)) {
                            skillCatCountLearnSkill.put(category, 1);
                        } else {
                            skillCatCountLearnSkill.put(category, skillCatCountLearnSkill.get(category) + 1);
                        }
                    }
                }
                displayGraphsOrChart();

            } catch (ParseException | NullPointerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void displayGraphsOrChart() {

        if (mBinding.toggleButton.getText().equals(getString(R.string.bar_graph))) {
            mBinding.barChart.setVisibility(View.VISIBLE);
            mBinding.pieChart.setVisibility(View.GONE);
            setupAndDisplayBarGraph();
        } else {
            mBinding.barChart.setVisibility(View.GONE);
            mBinding.pieChart.setVisibility(View.VISIBLE);
            setupPieChart();
            loadPieChartData(skillCatCountLearnSkill);
        }
    }

    private void setupPieChart() {
        PieChart pieChart = mBinding.pieChart;
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(16);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterTextSize(18);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setWordWrapEnabled(true);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPieChartData(Map<String, Integer> skillCatCountLearnSkill) {
        PieChart pieChart = mBinding.pieChart;
        ArrayList<PieEntry> entries = new ArrayList<>();
        int totalCat = 0;
        for (String key : skillCatCountLearnSkill.keySet()) {
            totalCat += skillCatCountLearnSkill.get(key);
        }
        for (String key : skillCatCountLearnSkill.keySet()) {
            Log.d("TAG", key + ":" + skillCatCountLearnSkill.get(key));
            entries.add(new PieEntry(skillCatCountLearnSkill.get(key) / Float.valueOf(totalCat), key));
        }

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(new DecimalFormat("###,###,##0")));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        //pieChart.animateY(1400, Easing.EasingOption);
    }

    private void setupAndDisplayBarGraph() {
        BarChart barChart = mBinding.barChart;

        int totalCat = 0;
        for (String key : skillCatCountLearnSkill.keySet()) {
            totalCat += skillCatCountLearnSkill.get(key);
        }
        // Create an ArrayList of BarEntry objects
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        int index = 0;
        for (String key : skillCatCountLearnSkill.keySet()) {
            barEntries.add(new BarEntry(index++, skillCatCountLearnSkill.get(key)));
        }

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }


        // Create a BarDataSet object
        BarDataSet barDataSet = new BarDataSet(barEntries, "Data Set");

        // Set the colors of the bars
        barDataSet.setColors(colors);

        // Create a BarData object and add the BarDataSet to it
        BarData barData = new BarData(barDataSet);

        // Customize the chart
        barChart.setData(barData);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBorders(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        // Refresh the chart
        barChart.invalidate();
    }




}