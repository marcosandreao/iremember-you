package br.com.simpleapp.rememberyou.custom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.squareup.picasso.Picasso;

/**
 * Created by socram on 28/04/16.
 */
public class SampleScrollListener extends RecyclerView.OnScrollListener {
    private final Context context;

    public SampleScrollListener(Context context) {
        this.context = context;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        final Picasso picasso = Picasso.with(context);
        if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            picasso.resumeTag(context);
        } else {
            picasso.pauseTag(context);
        }
    }

}
