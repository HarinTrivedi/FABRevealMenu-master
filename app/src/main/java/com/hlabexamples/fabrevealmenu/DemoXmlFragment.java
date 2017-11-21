package com.hlabexamples.fabrevealmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.hlab.fabrevealmenu.enums.Direction;
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener;
import com.hlab.fabrevealmenu.view.FABRevealMenu;

public class DemoXmlFragment extends BaseFragment implements OnFABMenuSelectedListener {

    private String[] mDirectionStrings = {"LEFT", "UP"};
    private Direction currentDirection = Direction.LEFT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_xml, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        CheckBox cbFont = view.findViewById(R.id.chFont);
        cbFont.setOnCheckedChangeListener((compoundButton, b) -> {
            if (fabMenu != null) {
                fabMenu.setMenuTitleTypeface(ResourcesCompat.getFont(getActivity(), R.font.quicksand));
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
        view.findViewById(R.id.textView).setOnClickListener(view1 -> {
            Intent i = new Intent(getActivity(), ScrollingActivity.class);
            startActivity(i);
        });

        Spinner spDirections = view.findViewById(R.id.spDirection);
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