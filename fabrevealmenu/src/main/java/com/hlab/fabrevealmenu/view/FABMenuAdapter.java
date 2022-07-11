package com.hlab.fabrevealmenu.view;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hlab.fabrevealmenu.R;
import com.hlab.fabrevealmenu.helper.RevealDirection;
import com.hlab.fabrevealmenu.model.FABMenuItem;

import java.util.List;

public class FABMenuAdapter extends RecyclerView.Adapter<FABMenuAdapter.ViewHolder> {

    private FABRevealMenu parent;
    private List<FABMenuItem> mItems;
    private int rowLayoutResId = 0;
    private boolean showTitle = false;
    private boolean showIcon = true;
    private int titleTextColor;
    private int titleDisabledTextColor;
    private RevealDirection revealDirection;
    private boolean isCircularShape;
    private Typeface mMenuTitleTypeface;

    FABMenuAdapter(FABRevealMenu parent, List<FABMenuItem> mItems, int rowLayoutResId, boolean isCircularShape, int titleTextColor, int titleDisabledTextColor,
                   boolean showTitle, boolean showIcon, RevealDirection revealDirection) {
        this.parent = parent;
        this.mItems = mItems;
        this.rowLayoutResId = rowLayoutResId;
        this.isCircularShape = isCircularShape;
        this.showTitle = showTitle;
        this.showIcon = showIcon;
        this.titleTextColor = titleTextColor;
        this.titleDisabledTextColor = titleDisabledTextColor;
        this.revealDirection = revealDirection;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(rowLayoutResId, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mItems.get(position));
        holder.itemView.setEnabled(mItems.get(position).isEnabled());
        holder.tvTitle.setEnabled(mItems.get(position).isEnabled());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    FABMenuItem getItemByIndex(int index) {
        if (index >= 0 && index < mItems.size()) {
            return mItems.get(index);
        }
        return null;
    }

    FABMenuItem getItemById(int id) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getId() == id) {
                return mItems.get(i);
            }
        }
        return null;
    }

    void notifyItemChangedById(int id) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getId() == id) {
                notifyItemChanged(i);
                return;
            }
        }
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public boolean isShowIcon() {
        return showIcon;
    }

    public void setShowIcon(boolean showIcon) {
        this.showIcon = showIcon;
    }

    public int getTitleTextColor() {
        return titleTextColor;
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    void setTitleDisabledTextColor(int titleDisabledTextColor) {
        this.titleDisabledTextColor = titleDisabledTextColor;
    }

    public RevealDirection getDirection() {
        return revealDirection;
    }

    void setDirection(RevealDirection revealDirection) {
        this.revealDirection = revealDirection;
    }

    void setMenuTitleTypeface(Typeface mMenuTitleTypeface) {
        this.mMenuTitleTypeface = mMenuTitleTypeface;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        FABMenuItem item;
        TextView tvTitle;
        ImageView imgIcon;
        RelativeLayout viewParent;

        ViewHolder(View itemView) {
            super(itemView);
            viewParent = itemView.findViewById(R.id.view_parent);
            tvTitle = itemView.findViewById(R.id.txt_title_menu_item);
            tvTitle.setTextColor(new ColorStateList(new int[][]{new int[]{android.R.attr.state_enabled}, new int[]{-android.R.attr.state_enabled}}, new int[]{titleTextColor, titleDisabledTextColor}));
            tvTitle.setVisibility(showTitle ? View.VISIBLE : View.GONE);
            imgIcon = itemView.findViewById(R.id.img_menu_item);
            imgIcon.setVisibility(showIcon ? View.VISIBLE : View.GONE);

            if (mMenuTitleTypeface != null)
                tvTitle.setTypeface(mMenuTitleTypeface);

            viewParent.setBackgroundResource(isCircularShape ?
                    R.drawable.drawable_bg_selected_round : R.drawable.drawable_bg_selected);

            viewParent.setOnClickListener(this);
            viewParent.invalidate();
        }

        public void setData(FABMenuItem item) {
            this.item = item;
            viewParent.setTag(item.getId());
            tvTitle.setText(item.getTitle());
            imgIcon.setImageDrawable(item.getIconDrawable());

        }

        @Override
        public void onClick(View v) {
            if (item.isEnabled()) {
                parent.closeMenu();
                parent.menuSelectedListener.onMenuItemSelected(v, item.getId());
            }
        }
    }

}
