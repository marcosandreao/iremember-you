package br.com.simpleapp.rememberyou.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.entity.History;
import br.com.simpleapp.rememberyou.service.HistoryService;

public class HistoryFragment extends Fragment implements HistoryRecyclerViewAdapter.OnListFragmentInteractionListener,
        LoaderManager.LoaderCallbacks<List<History>>{

    private int mColumnCount = 1;
    private HistoryRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getActivity().setTitle(this.getActivity().getString(R.string.history_fragment_title));
        this.getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            this.adapter = new HistoryRecyclerViewAdapter(this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onListFragmentInteraction(History item) {

    }

    @Override
    public Loader<List<History>> onCreateLoader(int id, Bundle args) {
        return new HistoryLoader(this.getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<History>> loader, List<History> data) {
        this.adapter.addAll(data);
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<History>> loader) {

    }

    public static class HistoryLoader extends AsyncTaskLoader<List<History>>{

        private HistoryService service = new HistoryService();
        private List<History> items;

        public HistoryLoader(Context context) {
            super(context);
        }

        @Override
        public List<History> loadInBackground() {
            this.items = this.service.list();
            return this.items;
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override
        protected void onStartLoading() {
            if (this.items != null) {
                deliverResult(this.items);
            }

            if (takeContentChanged() || this.items == null) {
                forceLoad();
            }
        }
    }
}
