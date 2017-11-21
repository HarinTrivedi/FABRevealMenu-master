package com.hlabexamples.fabrevealmenu;


import android.support.v4.app.Fragment;

import com.hlab.fabrevealmenu.view.FABRevealMenu;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    private FABRevealMenu fabMenu;

    public boolean onBackPressed() {
        if (fabMenu != null) {
            if (fabMenu.isShowing()) {
                fabMenu.closeMenu();
                return false;
            }
        }
        return true;
    }

    public FABRevealMenu getFabMenu() {
        return fabMenu;
    }

    public void setFabMenu(FABRevealMenu fabMenu) {
        this.fabMenu = fabMenu;
    }
}
