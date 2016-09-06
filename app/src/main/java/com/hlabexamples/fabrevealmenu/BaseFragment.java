package com.hlabexamples.fabrevealmenu;


import android.support.v4.app.Fragment;

import com.hlab.fabrevealmenu.view.FABRevealMenu;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    public boolean onBackPressed() {
        if (fabMenu != null) {
            if (fabMenu.isShowing()) {
                fabMenu.closeMenu();
                return false;
            }
        }
        return true;
    }

    private FABRevealMenu fabMenu;

    public FABRevealMenu getFabMenu() {
        return fabMenu;
    }

    public void setFabMenu(FABRevealMenu fabMenu) {
        this.fabMenu = fabMenu;
    }
}
