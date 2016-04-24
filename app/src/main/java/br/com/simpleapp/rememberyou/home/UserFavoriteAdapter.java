package br.com.simpleapp.rememberyou.home;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.entity.User;
import br.com.simpleapp.rememberyou.home.UserFavoriteFragment.OnListFragmentInteractionListener;
import br.com.simpleapp.rememberyou.utils.Emotions;

public class UserFavoriteAdapter extends RecyclerView.Adapter<UserFavoriteAdapter.ViewHolder> {

    private final List<User> mValues;
    private final OnListFragmentInteractionListener mListener;

    public UserFavoriteAdapter(List<User> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_userfavorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvName.setText(holder.mItem.getName());

        if ( holder.mItem.getLastEmotion() != null && !"".equals(holder.mItem.getLastEmotion())){
            holder.ivEmotion.setTag(holder.mItem.getLastEmotion());
            Log.d("TAG", holder.mItem.getLastEmotion());
            holder.ivEmotion.setImageResource(Emotions.getByKey(holder.mItem.getLastEmotion()));
        } else {
            final String emotion = "emotion_1f609";
            holder.ivEmotion.setTag(emotion);
            holder.ivEmotion.setImageResource(Emotions.getByKey(emotion));
        }


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView ivEmotion;
        public final TextView tvName;
        public final ImageView ivPerfil;
        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvName = (TextView) view.findViewById(R.id.account_name);
            ivEmotion = (ImageView) view.findViewById(R.id.ivEmotionTarget);
            ivPerfil = (ImageView) view.findViewById(R.id.ivPerfil);
        }


    }
}
