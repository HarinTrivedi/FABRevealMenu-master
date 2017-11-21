package com.hlab.fabrevealmenu.view;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hlab.fabrevealmenu.R;
import com.hlab.fabrevealmenu.enums.Direction;
import com.hlab.fabrevealmenu.model.FABMenuItem;

import java.util.List;

public class FABMenuAdapter extends RecyclerView.Adapter<FABMenuAdapter.ViewHolder> {

    private final int ANIMATED_ITEMS_COUNT = 1;
    private final Interpolator animInterpolator = new AccelerateDecelerateInterpolator();
    private final int TRANSLATE_DISTANCE = 80;
    private final int ANIM_DURATION = 350;
    private final int ANIM_OFFSET = 50;

    private FABRevealMenu parent;
    private List<FABMenuItem> mItems;
    private int rowLayoutResId = 0;
    private boolean showTitle = false;
    private int titleTextColor;
    private int titleDisabledTextColor;
    private Direction direction;
    private boolean isCircularShape;

    private boolean isReturning = false;

    private boolean animateItems = true;
    private int lastAnimatedPosition = -1;
    private int maxDuration;
    private Typeface mMenuTitleTypeface;

    FABMenuAdapter(FABRevealMenu parent, List<FABMenuItem> mItems, int rowLayoutResId, boolean isCircularShape, int titleTextColor, int titleDisabledTextColor,
                   boolean showTitle, Direction direction, boolean animateItems) {
        this.parent = parent;
        this.mItems = mItems;
        this.rowLayoutResId = rowLayoutResId;
        this.isCircularShape = isCircularShape;
        this.showTitle = showTitle;
        this.titleTextColor = titleTextColor;
        this.titleDisabledTextColor = titleDisabledTextColor;
        this.animateItems = animateItems;
        this.direction = direction;
    }

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
        // Here you apply the animation when the view is bound
        runEnterAnimation(holder.itemView, position);
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

    private void runEnterAnimation(View view, int position) {
        if (!animateItems || position < ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            if (isReturning)
                startExitAnimation(view, position);
            else
                startEnterAnimation(view, position);
        }
    }

    void resetAdapter(boolean returning) {
        lastAnimatedPosition = -1;
        isReturning = returning;
        notifyDataSetChanged();
        maxDuration = getItemCount() * ANIM_OFFSET;
    }

    private void startEnterAnimation(View view, int position) {
        int translateTo = (direction == Direction.DOWN) ? -TRANSLATE_DISTANCE : TRANSLATE_DISTANCE;
        view.setTranslationY(translateTo);
        view.setAlpha(0);
        view.animate()
                .translationY(0)
                .alpha(1)
                .setInterpolator(animInterpolator)
                .setDuration(ANIM_DURATION)
                .setStartDelay(ANIM_OFFSET + position * ANIM_OFFSET)
                .start();
    }

    private void startExitAnimation(View view, int position) {
        int translateTo = (direction == Direction.DOWN) ? -TRANSLATE_DISTANCE : TRANSLATE_DISTANCE;
        view.setTranslationY(0);
        view.setAlpha(1);
        view.animate()
                .translationY(translateTo)
                .alpha(0)
                .setInterpolator(animInterpolator)
                .setDuration(ANIM_DURATION)
                .setStartDelay(maxDuration - position * ANIM_OFFSET)
                .start();
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
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

    public Direction getDirection() {
        return direction;
    }

    void setDirection(Direction direction) {
        this.direction = direction;
    }

    void setMenuTitleTypeface(Typeface mMenuTitleTypeface) {
        this.mMenuTitleTypeface = mMenuTitleTypeface;
    }

    public boolean isAnimateItems() {
        return animateItems;
    }

    public void setAnimateItems(boolean animateItems) {
        this.animateItems = animateItems;
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
