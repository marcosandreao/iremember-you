package br.com.simpleapp.rememberyou.contacts.dialog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.emotions.EmotionManager;


public class EmotionsRecyclerViewAdapter extends RecyclerView.Adapter<EmotionsRecyclerViewAdapter.ViewHolder> {

    private String[] mValues = new String[0];

    private final OnListFragmentInteractionListener mListener;
    private final String cat;

    public EmotionsRecyclerViewAdapter(String cat, OnListFragmentInteractionListener listener) {
        this.cat = cat;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_picker_emotions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues[position];

        Picasso.with(this.mListener.getContext()).load(EmotionManager.getInstance().buildUri(this.cat, holder.mItem)).into(holder.ivEmotion);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onEmotionsSelected(cat, holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.length;
    }

    public void addAll(String[] data) {
        this.mValues = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView ivEmotion;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            this.mView = view;
            this.ivEmotion = (ImageView) view.findViewById(R.id.iv_emotion);
        }

    }

    public interface OnListFragmentInteractionListener {
        void onEmotionsSelected(String category, String item);

        Context getContext();
    }
}
