package com.vietlh.wethoong.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vietlh.wethoong.DetailsActivity;
import com.vietlh.wethoong.DetailsPhantichActivity;
import com.vietlh.wethoong.R;
import com.vietlh.wethoong.entities.Phantich;
import com.vietlh.wethoong.utils.SearchFor;
import com.vietlh.wethoong.utils.UtilsHelper;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class PhantichListRecyclerViewAdapter extends RecyclerView.Adapter<PhantichListRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Phantich> allPhantich = new ArrayList<>();
    private Context context;
    private UtilsHelper uHelper = new UtilsHelper();
    private SearchFor search;
    private int type = 0; //default value for normal view
    private int noidungLengthThreshold = 0;

    public PhantichListRecyclerViewAdapter(Context context, ArrayList<Phantich> allPhantich) {
        this.allPhantich = allPhantich;
        this.context = context;
        this.search = new SearchFor(this.context);
    }

    public PhantichListRecyclerViewAdapter(Context context, ArrayList<Phantich> allPhantich, int type, int noidungLengthThreshold) {
        this.allPhantich = allPhantich;
        this.context = context;
        this.search = new SearchFor(this.context);
        this.type = type;
        this.noidungLengthThreshold = noidungLengthThreshold;
    }

    public PhantichListRecyclerViewAdapter(Context context, ArrayList<Phantich> allPhantich, int type) {
        this.allPhantich = allPhantich;
        this.context = context;
        this.search = new SearchFor(this.context);
        this.type = type;
    }

    public void updateView(ArrayList<Phantich> allDieukhoan) {
        this.allPhantich = allDieukhoan;
        notifyDataSetChanged();
    }

//    public void showDetails(){
//        int itemPosition = mRecyclerView.getChildLayoutPosition(view);
//        String item = mList.get(itemPosition);
//        Toast.makeText(mContext, item, Toast.LENGTH_LONG).show();
//    }

    @Override
    public PhantichListRecyclerViewAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        final View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_items, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.lblVanban);
            }
        });

        PhantichListRecyclerViewAdapter.ViewHolder viewHolder = new PhantichListRecyclerViewAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PhantichListRecyclerViewAdapter.ViewHolder vHolder, int position) {

        Phantich pt = allPhantich.get(position);
                vHolder.hideMinhhoa();
        vHolder.setBtnBreadscrubs(pt);
        vHolder.setLblTitle(pt.getTittle());
        String noidung = pt.getShortContent();

            vHolder.hideLblVanban(false);
            vHolder.hideBtnBreadscrubs(true);
            vHolder.setLblAuthor(pt.getAuthor());

            //TODO: the valude of 250 should be configurable
            if (noidung.length() > noidungLengthThreshold && noidungLengthThreshold > 0) {
                noidung = noidung.substring(0, noidungLengthThreshold - 1) + "...";
            }

        vHolder.setLblNoidung(noidung);
        vHolder.setPhantichId(String.valueOf(pt.getIdKey()));
//        vHolder.fitItemDetails();
    }

    @Override
    public int getItemCount() {
        return allPhantich.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imgView;
        private LinearLayout item;
        private LinearLayout itemDetails;
        private TextView lblAuthor;
        private TextView lblTitle;
        private TextView lblNoidung;
        private Button btnBreadscrubs;
        private String phantichId = "";

        public ViewHolder(View v) {

            super(v);
            imgView = v.findViewById(R.id.item).findViewById(R.id.imageView);
            item = v.findViewById(R.id.item);
            itemDetails = v.findViewById(R.id.itemDetails);
            lblAuthor = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.lblVanban);
            lblTitle = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.breadscrubsView).findViewById(R.id.lblDieukhoan);
            btnBreadscrubs = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.breadscrubsView).findViewById(R.id.btnBreadscrubs);
            lblNoidung = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.lblNoidung);
            v.setOnClickListener(this);
        }

        public void hideMinhhoa() {
            ViewGroup.LayoutParams parms = imgView.getLayoutParams();
            parms.width = 0;
            parms.height = 0;
            imgView.setLayoutParams(parms);
        }

        public void showMinhhoa() {
            ViewGroup.LayoutParams parms = imgView.getLayoutParams();
            parms.width = (int) (uHelper.getScreenWidth() * 0.2);
            parms.height = parms.width;
            imgView.setLayoutParams(parms);
        }

        public ImageView getImgView() {
            return imgView;
        }

        public void setImgView(Bitmap bitmap) throws URISyntaxException {
            this.imgView.setImageBitmap(bitmap);
        }

        public TextView getLblAuthor() {
            return lblAuthor;
        }

        public void setLblAuthor(String vanbanCode) {
            this.lblAuthor.setText(vanbanCode);
        }

        public void hideLblVanban(Boolean isHidden) {
            if (isHidden) {
                lblAuthor.setVisibility(View.GONE);
            } else {
                lblAuthor.setVisibility(View.VISIBLE);
            }
        }

        public TextView getLblTitle() {
            return lblTitle;
        }

        public void setLblTitle(String dieukhoanNumber) {
            this.lblTitle.setText(dieukhoanNumber);
        }

        public TextView getLblNoidung() {
            return lblNoidung;
        }

        public void setLblNoidung(String noidung) {
            this.lblNoidung.setText(noidung);
        }

        public Button getBtnBreadscrubs() {
            return btnBreadscrubs;
        }

        public void setBtnBreadscrubs(Phantich parent) {
            this.btnBreadscrubs.setText("");
        }

        public void hideBtnBreadscrubs(Boolean isHidden) {
            if (isHidden) {
                btnBreadscrubs.setVisibility(View.GONE);
            } else {
                btnBreadscrubs.setVisibility(View.VISIBLE);
            }
        }

        public void setPhantichId(String id) {
            phantichId = id.trim();
        }

        //this workaround for fitting itemDetail does not work as expected. Layout param width return 0 for image and -1 for item (parent view).
        // This leads to incorrect value set to itemDetail width
        public void fitItemDetails() {
            ViewGroup.LayoutParams params = itemDetails.getLayoutParams();
            int iw = item.getLayoutParams().width;
            int imw = imgView.getLayoutParams().width;
            params.width = item.getLayoutParams().width - imgView.getLayoutParams().width;
            itemDetails.setLayoutParams(params);
        }

        @Override
        public void onClick(View v) {
            Log.i("Message", "tapping on: " + phantichId);
            Intent i = new Intent(context.getApplicationContext(), DetailsPhantichActivity.class);
            //TODO: need to change the hardcode phantichId to something that configurable.
            i.putExtra("phantichId", phantichId);
            context.startActivity(i);
        }
    }
}
