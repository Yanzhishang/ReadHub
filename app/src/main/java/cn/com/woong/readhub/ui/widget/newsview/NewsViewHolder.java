package cn.com.woong.readhub.ui.widget.newsview;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;

import cn.com.woong.readhub.R;
import cn.com.woong.readhub.bean.NewsMo;
import cn.com.woong.readhub.db.DBManager;
import cn.com.woong.readhub.eventbus.Event;
import cn.com.woong.readhub.ui.WebActivity;
import cn.com.woong.readhub.utils.CommonUtils;

/**
 * Created by wong on 2018/3/9.
 */

public class NewsViewHolder extends RecyclerView.ViewHolder {
    private Context mContext;

    private CardView newsCardView;
    private TextView tvItemTitle;
    private TextView tvItemTime;
    private TextView tvItemAuthor;
    private TextView tvItemContent;
    private ImageView ivCollect;
    private ImageView ivShare;
    private ImageView ivDelete;

    public NewsViewHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;

        newsCardView = itemView.findViewById(R.id.news_card_view);
        tvItemTitle = itemView.findViewById(R.id.tv_item_title);
        tvItemContent = itemView.findViewById(R.id.tv_item_content);
        tvItemAuthor = itemView.findViewById(R.id.tv_item_author);
        tvItemTime = itemView.findViewById(R.id.tv_item_time);
        ivCollect = itemView.findViewById(R.id.iv_collect);
        ivShare = itemView.findViewById(R.id.iv_share);
        ivDelete = itemView.findViewById(R.id.iv_delete);
    }

    public void showDelete() {
        ivDelete.setVisibility(View.VISIBLE);
        ivCollect.setVisibility(View.GONE);
    }

    public void bind(final NewsMo newsMo) {
        tvItemTitle.setText(newsMo.title);
        tvItemContent.setText(newsMo.summary);
        tvItemAuthor.setText(newsMo.siteName + "/" + newsMo.authorName);

        long publishDate = CommonUtils.getTimeStampByReadhubDateString(newsMo.publishDate);
        tvItemTime.setText(TimeUtils.millis2String(publishDate, new SimpleDateFormat("MM-dd HH:mm")));

        newsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.startWebActivity(mContext, newsMo.mobileUrl);
            }
        });

        ivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort(R.string.add_read_delay);
                DBManager.getInstance(mContext).insertNewsMo(newsMo);
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event.ReadLaterNewsRemoveEvent event = new Event.ReadLaterNewsRemoveEvent();
                event.position = getLayoutPosition();
                EventBus.getDefault().post(event);
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("ShareUrl", newsMo.mobileUrl);

                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(mClipData);
                    ToastUtils.showShort(R.string.copy_url);
                }
            }
        });
    }
}
