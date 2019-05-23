package beerly.ansteph.beerlybiz.slidingmenu;

import android.view.ViewGroup;

/**
 * Created by loicstephan on 2017/10/01.
 */

public  abstract  class DrawerItem<T extends DrawerAdapter.ViewHolder> {
    protected boolean isChecked;

    public abstract T createViewHolder(ViewGroup parent);

    public abstract void bindViewHolder(T holder);

    public DrawerItem setChecked(boolean isChecked) {
        this.isChecked = isChecked;
        return this;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public boolean isSelectable() {
        return true;
    }
}
