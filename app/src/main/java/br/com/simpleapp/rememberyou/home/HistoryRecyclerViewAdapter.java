package br.com.simpleapp.rememberyou.home;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.emotions.EmotionManager;
import br.com.simpleapp.rememberyou.entity.History;
import br.com.simpleapp.rememberyou.home.dto.HistoryDTO;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> {

    private final List<History> mValues = new ArrayList<>();
    private final OnListFragmentInteractionListener mListener;

    public HistoryRecyclerViewAdapter( OnListFragmentInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        if ( holder.mItem instanceof HistoryDTO ) {
            holder.vUngrouped.setVisibility(View.GONE);
            holder.vGrouped.setVisibility(View.VISIBLE);
            holder.mGroupNameView.setText(mValues.get(position).getName());
            holder.mGroupEmailView.setText(mValues.get(position).getEmail());
            final Map<String, Integer> emotions = ((HistoryDTO) holder.mItem).countEmotions;

            Object[] keys = emotions.keySet().toArray();
            ImageView iv = null;
            TextView tv = null;
            for ( int i = 0; i < keys.length; i++){
                String key = keys[i].toString();
                int count = emotions.get(key);
                ViewGroup target = (ViewGroup) holder.vGroupEmotions.getChildAt(i);
                iv = (ImageView) target.getChildAt(0);
                tv = (TextView) target.getChildAt(1);
                tv.setText(String.valueOf(count));
                Picasso.with(iv.getContext()).load(EmotionManager.getInstance().buildUri(key))
                        .tag(iv.getContext()).into(iv);
            }
        } else {
            holder.vUngrouped.setVisibility(View.VISIBLE);
            holder.vGrouped.setVisibility(View.GONE);
            holder.mNameView.setText(mValues.get(position).getName());
            holder.mEmailView.setText(mValues.get(position).getEmail());
            CharSequence dt = DateUtils.getRelativeDateTimeString(holder.mDatetimeView.getContext(),
                    mValues.get(position).getDateTime().getTime(), System.currentTimeMillis(), 0, 0);
            holder.mDatetimeView.setText(dt);


            Picasso.with(holder.mEmotionView.getContext()).load(EmotionManager.getInstance().buildUri(holder.mItem.getEmotion()))
                    .tag(holder.mEmotionView.getContext()).into(holder.mEmotionView);
        }



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void addAll(List<? extends History> data) {
        this.mValues.clear();
        this.mValues.addAll(data);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mEmailView;
        public final TextView mDatetimeView;
        public final ImageView mEmotionView;
        public final ViewGroup vUngrouped;

        public final TextView mGroupNameView;
        public final TextView mGroupEmailView;
        public final ViewGroup vGrouped;
        public final ViewGroup vGroupEmotions;


        public History mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.history_name);
            mEmailView = (TextView) view.findViewById(R.id.history_email);
            mDatetimeView = (TextView) view.findViewById(R.id.history_datetime);
            mEmotionView  = (ImageView) view.findViewById(R.id.iv_emotion);
            vUngrouped = (ViewGroup) view.findViewById(R.id.ungrouped);

            mGroupNameView = (TextView) view.findViewById(R.id.history_grouped_name);
            mGroupEmailView = (TextView) view.findViewById(R.id.history_grouped_email);
            vGrouped = (ViewGroup) view.findViewById(R.id.grouped);
            vGroupEmotions = (ViewGroup) view.findViewById(R.id.emotions);
        }

    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(History item);
    }
}
