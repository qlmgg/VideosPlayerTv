package com.hr.videosplayertv.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.hr.videosplayertv.R;
import com.hr.videosplayertv.net.entry.ListData;
import com.hr.videosplayertv.net.entry.response.CommHot;
import com.hr.videosplayertv.ui.adapter.base.CommonRecyclerViewAdapter;
import com.hr.videosplayertv.ui.adapter.base.CommonRecyclerViewHolder;
import com.hr.videosplayertv.utils.UrlUtils;

/**
 * 评论适配器
 */
public class CommentAdapter extends CommonRecyclerViewAdapter {


    public CommentAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return   R.layout.item_detail_comment;
    }

    @Override
    public void onBindItemHolder(CommonRecyclerViewHolder helper, Object item, int position) {

        if(item instanceof CommHot){
            helper.getHolder()
                    .showImage(R.id.image_tou, UrlUtils.getUrl(((CommHot) item).getHeadImage()))
                    .setText(R.id.tv_date,((CommHot) item).getPost_Date())
                    .setText(R.id.title, ((CommHot) item).getComment().getNickName()+"：   "+ ((CommHot) item).getContext());
        }
    }
}
