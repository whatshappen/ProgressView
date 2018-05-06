package com.whathappen.dialogstyle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whathappen.dialogstyle.R;
import com.whathappen.dialogstyle.dao.ItemBean;

import java.util.List;
import java.util.zip.Inflater;

/**
 * @author created by Wangw ;
 * @version 1.0
 * @data created time at 2018/5/6 ;
 * @Description mainactivity中rv的adapter
 */
public class MainRVAdapter extends RecyclerView.Adapter<MainRVAdapter.ViewHodler> {

    private Context context;
    private List<ItemBean> itemBeanList;
    private OnItemClickListener listener;

    public MainRVAdapter(Context context, List<ItemBean> itemBeanList) {
        this.context = context;
        this.itemBeanList = itemBeanList;
    }

    @Override
    public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_list_main_item, parent, false);
        return new ViewHodler(inflate);
    }

    @Override
    public int getItemCount() {
        return itemBeanList == null ? 0 : itemBeanList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHodler holder, final int position) {
        ItemBean itemBean = itemBeanList.get(position);
        holder.iv_num_icon.setBackgroundDrawable(context.getResources().getDrawable(itemBean.numIconId));
        holder.iv_progress_icon.setBackgroundDrawable(context.getResources().getDrawable(itemBean.progressTypeId));
        holder.tv_progress_name.setText(itemBean.progressName);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClick(holder.rootView, position);
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        this.listener = listener;
    }

    public class ViewHodler extends RecyclerView.ViewHolder {

        private final ImageView iv_num_icon;
        private final ImageView iv_progress_icon;
        private final TextView tv_progress_name;
        private final LinearLayout rootView;

        public ViewHodler(View itemView) {
            super(itemView);
            rootView = (LinearLayout)itemView.findViewById(R.id.ll_root);
            iv_num_icon = (ImageView) itemView.findViewById(R.id.iv_num_icon);
            iv_progress_icon = (ImageView) itemView.findViewById(R.id.iv_progress_icon);
            tv_progress_name = (TextView) itemView.findViewById(R.id.tv_progress_name);
        }
    }
}
