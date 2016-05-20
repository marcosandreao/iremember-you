package br.com.simpleapp.rememberyou.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;

import java.util.HashMap;
import java.util.Map;

import br.com.simpleapp.rememberyou.AnalyticsTrackers;
import br.com.simpleapp.rememberyou.IConstatns;
import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.contacts.ui.ContactDetailActivity;
import br.com.simpleapp.rememberyou.contacts.ui.ContactsListActivity;
import br.com.simpleapp.rememberyou.custom.SampleScrollListener;
import br.com.simpleapp.rememberyou.entity.StatusSend;
import br.com.simpleapp.rememberyou.entity.User;
import br.com.simpleapp.rememberyou.service.SendRemember;
import br.com.simpleapp.rememberyou.service.UserService;
import br.com.simpleapp.rememberyou.utils.DialogUtils;
import br.com.simpleapp.rememberyou.utils.SendState;

public class UserFavoriteFragment extends Fragment implements UserFavoriteAdapter.OnListFragmentInteractionListener {

    private static final int REQUEST_CHOOSE_ACCOUNT = 1;

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;

    private UserFavoriteAdapter adapter;

    private final IntentFilter filter = IConstatns.INTENT_FILTER_HOME;
    private HashMap<String, SendState> states  = new HashMap<>();
    private HashMap<String, Runnable> postStates  = new HashMap<>();

    private View emptyView;
    private RecyclerView recyclerView;

    public UserFavoriteFragment() {
    }

    @SuppressWarnings("unused")
    public static UserFavoriteFragment newInstance(int columnCount) {
        UserFavoriteFragment fragment = new UserFavoriteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getActivity().setTitle(this.getActivity().getString(R.string.app_name));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_userfavorite_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final AdView mAdView = (AdView) view.findViewById(R.id.adView);
        final AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        final Context context = view.getContext();
        this.recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        recyclerView.addOnScrollListener(new SampleScrollListener(getContext()));
        this.adapter = new UserFavoriteAdapter(new UserService().listFavorites(), this);
        recyclerView.setAdapter(this.adapter);
        this.adapter.registerAdapterDataObserver(this.emptyObserver);

        this.emptyView = view.findViewById(android.R.id.empty);
        this.emptyView.setVisibility(View.GONE);

        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(this.receiverSend, this.filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(this.receiverSend);
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsTrackers.getInstance().get().setScreenName("FavoriteFragment");
        AnalyticsTrackers.getInstance().get().send(new HitBuilders.ScreenViewBuilder().build());
        if ( this.adapter != null ) {
            this.adapter.setItems(new UserService().listFavorites());
        }
    }

    @Override
    public void onListFragmentInteraction(User item) {
        Uri contactUri = Uri.parse(item.getContactId());
        Intent intent = new Intent(this.getActivity(), ContactDetailActivity.class);
        intent.setData(contactUri);
        startActivityForResult(intent, REQUEST_CHOOSE_ACCOUNT);

        AnalyticsTrackers.getInstance().get().send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Selected User From Favorite")
                .build());
    }

    @Override
    public SendState getCurrentState(User mItem) {
        if ( this.states.containsKey(mItem.getEmail()) ) {
            return this.states.get(mItem.getEmail());
        }
        return null;

    }

    private void removePostByEmail(String email) {
        if ( this.postStates.containsKey(email)) {
            this.mHandler.removeCallbacks(this.postStates.get(email));
            this.postStates.remove(email);
        }
    }

    @Override
    public void onSendInteraction(final User mItem, final int position) {
        Log.d("DTE", "onSendInteraction " + mItem.getEmail());
        if ( this.states.containsKey(mItem.getEmail()) ) {

            final SendState state = this.states.get(mItem.getEmail());

            if ( state == SendState.STATE_START ) {
                return;
            }

            Log.d("DTE", "!=STATE_START ");
            this.removePostByEmail(mItem.getEmail());

            this.states.remove(mItem.getEmail());
            this.adapter.notifyItemChanged(position);

            switch (state) {
                case STATE_DONE_ERROR:
                    DialogUtils.showLocationDialogError(this.getActivity(), mItem.getName(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onSendInteraction(mItem, position);
                        }
                    });
                    return;
                case STATE_DONE_SUCCESS:
                    return;
                case STATE_START:
                    return;
                case STATE_DONE_NEED_INVITE:
                    DialogUtils.showLocationDialogNeedInvite(this.getActivity(), mItem.getName(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    return;
                default:

            }

        }
        Log.d("DTE", "ELSE ");

        this.removePostByEmail(mItem.getEmail());

        AnalyticsTrackers.getInstance().get().send(new HitBuilders.EventBuilder()
                .setCategory("Send")
                .setAction(mItem.getLastEmotion())
                .build());
        try {
            SendRemember.startActionSend(this.getActivity(), mItem.getEmail(), mItem.getLastEmotion(), IConstatns.INTENT_FILTER_ACTION_HOME);
            this.states.put(mItem.getEmail(), SendState.STATE_START);
            this.adapter.notifyItemChanged(position);
        } catch (Exception e ) {
            e.printStackTrace();
            Log.e("onSendInteraction", e.getMessage());
        }
    }

    private RecyclerView.AdapterDataObserver emptyObserver = new RecyclerView.AdapterDataObserver() {


        @Override
        public void onChanged() {
            if(adapter != null && emptyView != null) {
                if(adapter.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else {
                    emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

        }
    };

    private Handler mHandler = new Handler();

    @Override
    public void onPause() {
        super.onPause();
        this.mHandler.removeCallbacks(null);
    }

    private void postDelay(final String email){

        this.postStates.put(email, new Runnable() {
            @Override
            public void run() {
                try {
                    states.remove(email);
                    postStates.remove(email);
                    final int position = adapter.getPostionItemByEmail(email);
                    if ( position != -1 ) {
                        adapter.notifyItemChanged(position);
                    }
                } catch (Exception e){}
            }
        });

        this.mHandler.postDelayed(this.postStates.get(email), 4000);
    }

    private void handlerReceiver(final Intent intent){
        final int state = intent.getIntExtra(SendRemember.EXTRA_STATE, -1);
        final String email = intent.getStringExtra(SendRemember.EXTRA_TO);

        switch (state){
            case SendRemember.STATE_DONE_ERROR:
                Toast.makeText(UserFavoriteFragment.this.getActivity(), R.string.toast_send_error, Toast.LENGTH_LONG).show();
                this.states.put(email, SendState.STATE_DONE_ERROR);
                postDelay(email);
                break;
            case SendRemember.STATE_DONE_SUCCESS:
                this.states.put(email, SendState.STATE_DONE_SUCCESS);
                break;
            case SendRemember.STATE_DONE_NEED_INVITE:
                Toast.makeText(UserFavoriteFragment.this.getActivity(),
                        email + getString(R.string.toast_send_not_found_user), Toast.LENGTH_LONG).show();

                this.states.put(email, SendState.STATE_DONE_NEED_INVITE);
                postDelay(email);
                break;
            case SendRemember.STATE_START:
                this.states.put(email, SendState.STATE_START);
                break;
        }

        final int position = this.adapter.getPostionItemByEmail(email);
        if ( position != -1 ) {
            this.adapter.notifyItemChanged(position);
        }
    }

    public BroadcastReceiver receiverSend = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            handlerReceiver(intent);
        }
    };


}
