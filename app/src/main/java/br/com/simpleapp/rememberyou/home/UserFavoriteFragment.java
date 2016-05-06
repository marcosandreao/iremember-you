package br.com.simpleapp.rememberyou.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

import br.com.simpleapp.rememberyou.IConstatns;
import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.contacts.ui.ContactDetailActivity;
import br.com.simpleapp.rememberyou.contacts.ui.ContactsListActivity;
import br.com.simpleapp.rememberyou.custom.SampleScrollListener;
import br.com.simpleapp.rememberyou.entity.StatusSend;
import br.com.simpleapp.rememberyou.entity.User;
import br.com.simpleapp.rememberyou.service.SendRemember;
import br.com.simpleapp.rememberyou.service.UserService;
import br.com.simpleapp.rememberyou.utils.SendState;

public class UserFavoriteFragment extends Fragment implements UserFavoriteAdapter.OnListFragmentInteractionListener {

    private static final int REQUEST_CHOOSE_ACCOUNT = 1;

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;

    private UserFavoriteAdapter adapter;

    private final IntentFilter filter = IConstatns.INTENT_FILTER_HOME;
    private HashMap<String, SendState> states  = new HashMap<>();

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

        final Context context = view.getContext();
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        recyclerView.addOnScrollListener(new SampleScrollListener(getContext()));
        this.adapter = new UserFavoriteAdapter(new UserService().listFavorites(), this);
        recyclerView.setAdapter(this.adapter);

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 startActivityForResult(new Intent(getActivity(), ContactsListActivity.class), REQUEST_CHOOSE_ACCOUNT);
            }
        });
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
    }

    @Override
    public SendState getCurrentState(User mItem) {
        if ( this.states.containsKey(mItem.getEmail()) ) {
            return this.states.get(mItem.getEmail());
        }
        return null;

    }

    @Override
    public void onSendInteraction(User mItem, int position) {
        if ( this.states.containsKey(mItem.getEmail()) ) {
            SendState state = this.states.get(mItem.getEmail());
            switch (state) {
                case STATE_DONE_ERROR:
                case STATE_DONE_SUCCESS:
                case STATE_DONE_NEED_INVITE:
                    this.states.remove(mItem.getEmail());
                    this.adapter.notifyItemChanged(position);
                    return;
                default:

            }
        }

        try {
            SendRemember.startActionSend(this.getActivity(), mItem.getEmail(), mItem.getLastEmotion(), IConstatns.INTENT_FILTER_ACTION_HOME);
            this.states.put(mItem.getEmail(), SendState.STATE_START);
            this.adapter.notifyItemChanged(position);
        } catch (Exception e ) {
            e.printStackTrace();
            Log.e("onSendInteraction", e.getMessage());
        }
    }

    private void handlerReceiver(final Intent intent){
        final int state = intent.getIntExtra(SendRemember.EXTRA_STATE, -1);
        final String email = intent.getStringExtra(SendRemember.EXTRA_TO);

        switch (state){
            case SendRemember.STATE_DONE_ERROR:
                this.states.put(email, SendState.STATE_DONE_ERROR);
                break;
            case SendRemember.STATE_DONE_SUCCESS:
                this.states.put(email, SendState.STATE_DONE_SUCCESS);
                break;
            case SendRemember.STATE_DONE_NEED_INVITE:
                this.states.put(email, SendState.STATE_DONE_NEED_INVITE);
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
