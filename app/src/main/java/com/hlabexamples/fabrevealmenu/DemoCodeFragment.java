package com.hlabexamples.fabrevealmenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.content.res.AppCompatResources;
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
import com.hlab.fabrevealmenu.model.FABMenuItem;
import com.hlab.fabrevealmenu.view.FABRevealMenu;

import java.util.ArrayList;

public class DemoCodeFragment extends BaseFragment implements OnFABMenuSelectedListener {

    private ArrayList<FABMenuItem> items;
    private String[] mDirectionStrings = {"LEFT", "UP"};
    private Direction currentDirection = Direction.LEFT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_code, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initItems(false);

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
        CheckBox chAnimate = view.findViewById(R.id.chAnimate);
        chAnimate.setOnCheckedChangeListener((compoundButton, enable) -> {
            if (fabMenu != null) {
                //set custom font typeface
                fabMenu.enableItemAnimation(enable);
            }
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

        assert fabMenu != null;
        fabMenu.setOverlayBackground(R.color.colorAccent);
        fabMenu.setMenuBackground(R.color.colorWhite);
    }

    private void initItems(boolean toShowDoubleItems) {
        items = new ArrayList<>();
        items.add(new FABMenuItem("Attachments", AppCompatResources.getDrawable(getActivity(), R.drawable.ic_attachment)));
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