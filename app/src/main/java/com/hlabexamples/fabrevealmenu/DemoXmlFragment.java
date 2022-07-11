package com.hlabexamples.fabrevealmenu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.hlab.fabrevealmenu.helper.RevealDirection;
import com.hlab.fabrevealmenu.helper.OnFABMenuSelectedListener;
import com.hlab.fabrevealmenu.view.FABRevealMenu;

public class DemoXmlFragment extends BaseFragment implements OnFABMenuSelectedListener {

    private String[] mDirectionStrings = {"LEFT", "UP"};
    private RevealDirection currentRevealDirection = RevealDirection.LEFT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_xml, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FABRevealMenu fabMenu = initFabMenu(view);

        initListeners(view, fabMenu);
    }

    private FABRevealMenu initFabMenu(View view) {
        final FloatingActionButton fab = view.findViewById(R.id.fab);
        final FABRevealMenu fabMenu = view.findViewById(R.id.fabMenu);

        try {
            if (fab != null && fabMenu != null) {
                setFabMenu(fabMenu);
                //attach menu to fab
                fabMenu.bindAnchorView(fab);
                //set menu selection listener
                fabMenu.setOnFABMenuSelectedListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fabMenu;
    }

    private void initListeners(View view, FABRevealMenu fabMenu) {
        Spinner spDirections = view.findViewById(R.id.spDirection);
        spDirections.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mDirectionStrings));
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

    @Override
    public void onMenuItemSelected(View view, int id) {
        if (id == R.id.menu_attachment) {
            Toast.makeText(getActivity(), "Attachment Selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_image) {
            Toast.makeText(getActivity(), "Image Selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_place) {
            Toast.makeText(getActivity(), "Place Selected", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_emoticon) {
            Toast.makeText(getActivity(), "Emoticon Selected", Toast.LENGTH_SHORT).show();
        }
    }
}