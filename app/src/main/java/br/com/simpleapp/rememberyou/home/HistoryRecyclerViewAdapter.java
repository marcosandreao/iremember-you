package br.com.simpleapp.rememberyou.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.entity.History;
import br.com.simpleapp.rememberyou.utils.Emotions;

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
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mEmailView.setText(mValues.get(position).getEmail());
        holder.mDatetimeView.setText(mValues.get(position).getDateTime().toString());

        Picasso.with(holder.mEmotionView.getContext()).load(Emotions.getByKey(holder.mItem.getEmotion()))
                .tag(holder.mEmotionView.getContext()).into(holder.mEmotionView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });

        //holder.mDatetimeView.setText(prettyTime.format(new Date(secondsAgo)));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void addAll(List<History> data) {
        this.mValues.addAll(data);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mEmailView;
        public final TextView mDatetimeView;
        public final ImageView mEmotionView;
        public History mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.history_name);
            mEmailView = (TextView) view.findViewById(R.id.history_email);
            mDatetimeView = (TextView) view.findViewById(R.id.history_datetime);
            mEmotionView  = (ImageView) view.findViewById(R.id.iv_emotion);
        }

    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(History item);
    }
}
