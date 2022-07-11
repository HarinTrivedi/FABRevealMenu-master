package com.hlabexamples.fabrevealmenu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.content.res.AppCompatResources;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.hlab.fabrevealmenu.helper.RevealDirection;
import com.hlab.fabrevealmenu.helper.OnFABMenuSelectedListener;
import com.hlab.fabrevealmenu.model.FABMenuItem;
import com.hlab.fabrevealmenu.view.FABRevealMenu;

import java.util.ArrayList;
import java.util.Objects;

public class DemoCodeFragment extends BaseFragment implements OnFABMenuSelectedListener {

    private ArrayList<FABMenuItem> items;
    private String[] mDirectionStrings = {"LEFT", "UP"};
    private RevealDirection currentRevealDirection = RevealDirection.LEFT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initItems(false);

        final FABRevealMenu fabMenu = initFabMenu(view);

        initListeners(view, fabMenu);
    }

    private FABRevealMenu initFabMenu(@NonNull View view) {
        FloatingActionButton fab = view.findViewById(R.id.fab);
        final FABRevealMenu fabMenu = view.findViewById(R.id.fabMenu);

        try {
            if (fab != null && fabMenu != null) {
                //attach menu to fab
                setFabMenu(fabMenu);
                //set menu items from arrylist
                fabMenu.setMenuItems(items);
                //attach menu to fab
                fabMenu.bindAnchorView(fab);
                //set menu item selection
                fabMenu.setOnFABMenuSelectedListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fabMenu;
    }

    private void initListeners(@NonNull View view, FABRevealMenu fabMenu) {
        CheckBox cbTitle = view.findViewById(R.id.chTitle);
        cbTitle.setOnCheckedChangeListener((compoundButton, b) -> {
            if (fabMenu != null) {
                fabMenu.setTitleVisible(b);
            }
        });
        CheckBox cbShowOverlay = view.findViewById(R.id.chOverlay);
        cbShowOverlay.setOnCheckedChangeListener((compoundButton, b) -> {
            if (fabMenu != null) {
                fabMenu.setShowOverlay(b);
            }
        });
        CheckBox cbDouble = view.findViewById(R.id.chDouble);
        cbDouble.setOnCheckedChangeListener((compoundButton, b) -> {
            if (fabMenu != null) {
                initItems(b);
                fabMenu.setMenuItems(items);
            }
        });
        CheckBox cbFont = view.findViewById(R.id.chFont);
        cbFont.setOnCheckedChangeListener((compoundButton, b) -> {
            if (fabMenu != null) {
                //set custom font typeface
                fabMenu.setMenuTitleTypeface(ResourcesCompat.getFont(requireActivity(), R.font.quicksand));
            }
        });
        CheckBox chSmall = view.findViewById(R.id.chSmall);
        chSmall.setOnCheckedChangeListener((compoundButton, isSmaller) -> {
            if (fabMenu != null) {
                if (isSmaller)
                    fabMenu.setSmallerMenu();
                else
                    fabMenu.setNormalMenu();
            }
        });
        Spinner spDirections = view.findViewById(R.id.spDirection);
        spDirections.setAdapter(new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, mDirectionStrings));
        spDirections.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (fabMenu != null) {
                    if (position == 0 && currentRevealDirection != RevealDirection.LEFT) {
                        currentRevealDirection = RevealDirection.LEFT;
                        fabMenu.setMenuDirection(RevealDirection.LEFT);
                    } else if (position == 1 && currentRevealDirection != RevealDirection.UP) {
                        currentRevealDirection = RevealDirection.UP;
                        fabMenu.setMenuDirection(RevealDirection.UP);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (fabMenu != null) {
                    fabMenu.setMenuDirection(RevealDirection.LEFT);
                }
            }
        });
    }

    private void initItems(boolean toShowDoubleItems) {
        items = new ArrayList<>();
        items.add(new FABMenuItem("Attachments", AppCompatResources.getDrawable(requireActivity(), R.drawable.ic_attachment)));
        items.add(new FABMenuItem("Images", AppCompatResources.getDrawable(getActivity(), R.drawable.ic_image)));
        items.add(new FABMenuItem("Places", AppCompatResources.getDrawable(getActivity(), R.drawable.ic_place)));
        items.add(new FABMenuItem("Emoticons", AppCompatResources.getDrawable(getActivity(), R.drawable.ic_emoticon)));
        if (toShowDoubleItems) {
            items.add(new FABMenuItem("Attachments", AppCompatResources.getDrawable(getActivity(), R.drawable.ic_attachment)));
            items.add(new FABMenuItem("Images", AppCompatResources.getDrawable(getActivity(), R.drawable.ic_image)));
            items.add(new FABMenuItem("Places", AppCompatResources.getDrawable(getActivity(), R.drawable.ic_place)));
            items.add(new FABMenuItem("Emoticons", AppCompatResources.getDrawable(getActivity(), R.drawable.ic_emoticon)));
        }
    }

    @Override
    public void onMenuItemSelected(View view, int id) {
        if (id >= 0 && items != null && items.size() > id) {
            Toast.makeText(getActivity(), items.get(id).getTitle() + "Clicked", Toast.LENGTH_SHORT).show();
        }
    }
}