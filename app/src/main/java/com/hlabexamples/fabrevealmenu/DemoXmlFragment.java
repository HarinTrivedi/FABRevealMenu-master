package com.hlabexamples.fabrevealmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.hlab.fabrevealmenu.enums.Direction;
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener;
import com.hlab.fabrevealmenu.view.FABRevealMenu;

public class DemoXmlFragment extends BaseFragment implements OnFABMenuSelectedListener {

    private String[] mDirectionStrings = {"Direction - LEFT", "Direction - UP"};
    private Direction currentDirection = Direction.LEFT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_xml, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        final FABRevealMenu fabMenu = (FABRevealMenu) view.findViewById(R.id.fabMenu);

        try {
            if (fab != null && fabMenu != null) {
                setFabMenu(fabMenu);
                fabMenu.bindAncherView(fab);
                fabMenu.setOnFABMenuSelectedListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        view.findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ScrollingActivity.class);
                startActivity(i);
            }
        });

//        CheckBox cbTitle = (CheckBox) view.findViewById(R.id.chTitle);
//        cbTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (fabMenu != null) {
//                    fabMenu.setTitleVisible(b);
//                }
//            }
//        });
//
//        CheckBox cbShowOverlay = (CheckBox) view.findViewById(R.id.chOverlay);
//        cbShowOverlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (fabMenu != null) {
//                    fabMenu.setShowOverlay(b);
//                }
//            }
//        });

        Spinner spDirections = (Spinner) view.findViewById(R.id.spDirection);
        spDirections.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mDirectionStrings));
        spDirections.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (fabMenu != null) {
                    if (position == 0 && currentDirection != Direction.LEFT) {
                        currentDirection = Direction.LEFT;
                        fabMenu.setMenuDirection(Direction.LEFT);
                    } else if (position == 1 && currentDirection != Direction.UP) {
                        currentDirection = Direction.UP;
                        fabMenu.setMenuDirection(Direction.UP);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (fabMenu != null) {
                    fabMenu.setMenuDirection(Direction.LEFT);
                }
            }
        });
    }

    @Override
    public void onMenuItemSelected(View view) {
        int id = (int) view.getTag();
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