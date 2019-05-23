package beerly.ansteph.beerlybiz.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;

import beerly.ansteph.beerlybiz.R;
import beerly.ansteph.beerlybiz.model.Beer;
import gr.escsoft.michaelprimez.revealedittext.tools.UITools;
import gr.escsoft.michaelprimez.searchablespinner.interfaces.ISpinnerSelectedView;

/**
 * Created by loicstephan on 2018/06/18.
 */

public class BeerArrayListAdapter extends ArrayAdapter<Beer> implements Filterable, ISpinnerSelectedView {


    private Context mContext;
    private ArrayList<Beer> mBackupBeers;
    private ArrayList<Beer> mBeers;
    private StringFilter mStringFilter = new StringFilter();



    public BeerArrayListAdapter(@NonNull Context context, ArrayList<Beer> beers) {
        super(context,  R.layout.view_list_item);
        this.mBackupBeers = beers;
        this.mBeers = beers;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mBeers == null ? 0 : mBeers.size() + 1;
    }

    @Override
    public Beer getItem(int position) {
        if (mBeers != null && position > 0)
            return mBeers.get(position - 1);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        if (mBeers == null && position > 0)
            return mBeers.get(position).hashCode();
        else
            return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (position == 0) {
            view = getNoSelectionView();
        } else {
            view = View.inflate(mContext, R.layout.view_list_item, null);
            ImageView letters = (ImageView) view.findViewById(R.id.ImgVw_Letters);
            TextView dispalyName = (TextView) view.findViewById(R.id.TxtVw_DisplayName);
           // letters.setImageDrawable(getTextDrawable(mBeers.get(position-1).getName()));
            dispalyName.setText(mBeers.get(position-1).getName());
        }
        return view;
    }





    @Override
    public View getNoSelectionView() {
        View view = View.inflate(mContext, R.layout.view_list_no_selection_item, null);
        return view;
    }

    @Override
    public View getSelectedView(int position) {
        View view = null;
        if (position == 0) {
            view = getNoSelectionView();
        } else {
            view = View.inflate(mContext, R.layout.view_list_item, null);
            ImageView letters = (ImageView) view.findViewById(R.id.ImgVw_Letters);
            TextView dispalyName = (TextView) view.findViewById(R.id.TxtVw_DisplayName);
           // letters.setImageDrawable(getTextDrawable(mBeers.get(position-1).getName()));
            dispalyName.setText(mBeers.get(position-1).getName());
        }
        return view;
    }


    private TextDrawable getTextDrawable(String displayName) {
        TextDrawable drawable = null;
        if (!TextUtils.isEmpty(displayName)) {
            int color2 = ColorGenerator.MATERIAL.getColor(displayName);
            drawable = TextDrawable.builder()
                    .beginConfig()
                    .width(UITools.dpToPx(mContext, 32))
                    .height(UITools.dpToPx(mContext, 32))
                    .textColor(Color.WHITE)
                    .toUpperCase()
                    .endConfig()
                    .round()
                    .build(displayName.substring(0, 1), color2);
        } else {
            drawable = TextDrawable.builder()
                    .beginConfig()
                    .width(UITools.dpToPx(mContext, 32))
                    .height(UITools.dpToPx(mContext, 32))
                    .endConfig()
                    .round()
                    .build("?", Color.GRAY);
        }
        return drawable;
    }

    @Override
    public Filter getFilter() {
        return mStringFilter;
    }


    public class StringFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults filterResults = new FilterResults();
            if (TextUtils.isEmpty(constraint)) {
                filterResults.count = mBackupBeers.size();
                filterResults.values = mBackupBeers;
                return filterResults;
            }
            final ArrayList<Beer> filterStrings = new ArrayList<>();
            for (Beer beer : mBackupBeers) {
                if (beer.getName().toLowerCase().contains(constraint)) {
                    filterStrings.add(beer);
                }
            }
            filterResults.count = filterStrings.size();
            filterResults.values = filterStrings;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mBeers = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    }

    private class ItemView {
        public ImageView mImageView;
        public TextView mTextView;
    }

    public enum ItemViewType {
        ITEM, NO_SELECTION_ITEM;
    }
}
