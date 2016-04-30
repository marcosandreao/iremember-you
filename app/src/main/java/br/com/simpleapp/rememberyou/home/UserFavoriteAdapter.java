package br.com.simpleapp.rememberyou.home;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.entity.User;
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

        int emotionResource = 0;
        if ( holder.mItem.getLastEmotion() != null && !"".equals(holder.mItem.getLastEmotion())){
            holder.ivEmotion.setTag(holder.mItem.getLastEmotion());
            Log.d("TAG", holder.mItem.getLastEmotion());
            //holder.ivEmotion.setImageResource(Emotions.getByKey(holder.mItem.getLastEmotion()));
            emotionResource = Emotions.getByKey(holder.mItem.getLastEmotion());
        } else {
            final String emotion = "emotion_1f609";
            holder.ivEmotion.setTag(emotion);
            //holder.ivEmotion.setImageResource(Emotions.getByKey(emotion));
            emotionResource = Emotions.getByKey(emotion);
        }

        final Uri contactUri = Uri.parse(holder.mItem.getContactId());

        Picasso.with(holder.tvName.getContext()).load(contactUri)
                .tag(holder.tvName.getContext())
                .placeholder(R.drawable.ic_contact_picture_180_holo_light).into(holder.ivPerfil);

        Picasso.with(holder.tvName.getContext()).load(emotionResource)
                .tag(holder.tvName.getContext()).into(holder.ivEmotion);

        holder.ivPerfil.assignContactUri(contactUri);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });

        holder.btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onSendInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setItems(List<User> items) {
        if ( this.mValues == null ) {
            return;
        }
        this.mValues.clear();
        this.mValues.addAll(items);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView ivEmotion;
        public final TextView tvName;
        public final QuickContactBadge ivPerfil;
        public final View btStart;
        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvName = (TextView) view.findViewById(R.id.account_name);
            ivEmotion = (ImageView) view.findViewById(R.id.ivEmotionTarget);
            ivPerfil = (QuickContactBadge) view.findViewById(R.id.ivPerfil);
            this.btStart = view.findViewById(R.id.btStart);
        }


    }

    public static interface OnListFragmentInteractionListener {

        void onListFragmentInteraction(User item);

        void onSendInteraction(User mItem);
    }

}
