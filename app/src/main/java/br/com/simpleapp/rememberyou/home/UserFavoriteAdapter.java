package br.com.simpleapp.rememberyou.home;

import android.graphics.Color;
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

import org.w3c.dom.Text;

import java.util.List;

import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.entity.User;
import br.com.simpleapp.rememberyou.utils.Emotions;
import br.com.simpleapp.rememberyou.utils.SendState;

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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.tvName.setText(holder.mItem.getName());

        boolean loadEmotion = true;
        SendState state = this.mListener.getCurrentState(holder.mItem);
        if ( state != null ) {
            loadEmotion = false;
            holder.tvButton.setVisibility(View.INVISIBLE);

            switch (state) {
                case STATE_DONE_ERROR:
                    holder.ivEmotion.setColorFilter(Color.RED);
                    holder.pbSending.setVisibility(View.GONE);
                    holder.ivEmotion.setVisibility(View.VISIBLE);
                    holder.ivEmotion.setImageResource(R.drawable.ic_cloud_off_white_24dp);
                    break;
                case STATE_DONE_NEED_INVITE:
                    holder.ivEmotion.setColorFilter(Color.RED);
                    holder.pbSending.setVisibility(View.GONE);
                    holder.ivEmotion.setVisibility(View.VISIBLE);
                    holder.ivEmotion.setImageResource(R.drawable.ic_cloud_off_white_24dp);
                    break;
                case STATE_DONE_SUCCESS:
                    holder.ivEmotion.setColorFilter(holder.tvName.getContext().getResources().getColor(R.color.theme_primary_accent));
                    holder.pbSending.setVisibility(View.GONE);
                    holder.ivEmotion.setImageResource(R.drawable.ic_cloud_done_white_24dp);
                    holder.ivEmotion.setVisibility(View.VISIBLE);
                    break;
                case STATE_START:
                    holder.ivEmotion.setVisibility(View.INVISIBLE);
                    holder.pbSending.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            holder.ivEmotion.setColorFilter(Color.TRANSPARENT);
            holder.pbSending.setVisibility(View.GONE);
            holder.tvButton.setVisibility(View.VISIBLE);
            holder.ivEmotion.setVisibility(View.VISIBLE);
        }


        if (loadEmotion ) {
            int emotionResource = 0;
            if (holder.mItem.getLastEmotion() != null && !"".equals(holder.mItem.getLastEmotion())) {
                holder.ivEmotion.setTag(holder.mItem.getLastEmotion());
                Log.d("TAG", holder.mItem.getLastEmotion());
                emotionResource = Emotions.getByKey(holder.mItem.getLastEmotion());
            } else {
                final String emotion = "emotion_1f609";
                holder.ivEmotion.setTag(emotion);
                //holder.ivEmotion.setImageResource(Emotions.getByKey(emotion));
                emotionResource = Emotions.getByKey(emotion);
            }
            Picasso.with(holder.tvName.getContext()).load(emotionResource)
                    .tag(holder.tvName.getContext()).into(holder.ivEmotion);
        }

        final Uri contactUri = Uri.parse(holder.mItem.getContactId());

        Picasso.with(holder.tvName.getContext()).load(contactUri)
                .tag(holder.tvName.getContext())
                .placeholder(R.drawable.ic_contact_picture_180_holo_light).into(holder.ivPerfil);

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
                    mListener.onSendInteraction(holder.mItem, position);
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

    public int getPostionItemByEmail(String email) {
        for ( int i = 0; i < this.mValues.size(); i ++ ) {
            if ( this.mValues.get(i).getEmail().equals(email) ) {
                return i;
            }
        }
        return -1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView ivEmotion;
        public final TextView tvName;
        public final QuickContactBadge ivPerfil;
        public final View btStart;
        public final TextView tvButton;
        public final View pbSending;
        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvName = (TextView) view.findViewById(R.id.account_name);
            ivEmotion = (ImageView) view.findViewById(R.id.ivEmotionTarget);
            ivPerfil = (QuickContactBadge) view.findViewById(R.id.ivPerfil);
            this.btStart = view.findViewById(R.id.btStart);
            this.tvButton = (TextView) view.findViewById(R.id.tv_state);
            this.pbSending = view.findViewById(R.id.sending);
        }


    }

    public interface OnListFragmentInteractionListener {

        void onListFragmentInteraction(User item);

        void onSendInteraction(User mItem, int position);

        SendState getCurrentState(User mItem);
    }

}
