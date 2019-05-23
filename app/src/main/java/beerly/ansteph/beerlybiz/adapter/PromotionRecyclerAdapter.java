package beerly.ansteph.beerlybiz.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import beerly.ansteph.beerlybiz.R;
import beerly.ansteph.beerlybiz.model.Promotion;
import beerly.ansteph.beerlybiz.view.promotion.EditPromotion;

/**
 * Created by loicstephan on 2018/06/16.
 */

public class PromotionRecyclerAdapter extends RecyclerView.Adapter<PromotionRecyclerAdapter.ViewHolder> {


    private ArrayList<Promotion> Promotions;
    Context mContext;

    public PromotionRecyclerAdapter(ArrayList<Promotion> promotions, Context mContext) {
        Promotions = promotions;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beer_pref_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        holder.txtPromoTitle.setText(Promotions.get(position).getTitle());
        holder.txtPromoPrice.setText("R"+Promotions.get(position).getPrice());

        holder.imgPromoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i =new Intent(mContext, EditPromotion.class);
                i.putExtra("promo", Promotions.get(position));
                mContext.startActivity(i);

                //  mContext.startActivity(new Intent(mContext, EditEntry.class));
            }
        });

        final View itemView = holder.itemView;
    }

    @Override
    public int getItemCount() {
        return Promotions.size();
    }


    public void removeItem(int position) {
        Promotions.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Promotion item, int position) {
        Promotions.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        public RelativeLayout viewBackground, viewForeground;

        public final TextView txtPromoTitle;
        public final TextView txtPromoPrice;

        public final ImageButton imgPromoEdit;

        public ViewHolder(View view) {
            super(view);
            this.txtPromoTitle =(TextView) itemView.findViewById(R.id.txtbeeritem);
            this.txtPromoPrice =(TextView) itemView.findViewById(R.id.txtpromoprice);
            this.imgPromoEdit = (ImageButton) itemView.findViewById(R.id.imgBeerSelect) ;

            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }
}
