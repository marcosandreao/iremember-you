package br.com.simpleapp.rememberyou.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.List;

import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.entity.History;
import br.com.simpleapp.rememberyou.service.HistoryService;

public class HistoryFragment extends Fragment implements HistoryRecyclerViewAdapter.OnListFragmentInteractionListener,
        LoaderManager.LoaderCallbacks<List<? extends History>>, AdapterView.OnItemSelectedListener{

    private int mColumnCount = 1;
    private HistoryRecyclerViewAdapter adapter;

    private static final int FILTER_ALL = 0;
    private View emptyView;
    private RecyclerView recyclerView;

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
        this.getActivity().getSupportLoaderManager().initLoader(FILTER_ALL, null, this).forceLoad();
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_history_list, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final ArrayAdapter filterOptions = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.history_filter_options));
        filterOptions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final AppCompatSpinner mSpinner = (AppCompatSpinner) menu.findItem(R.id.finter_spinner).getActionView();
        mSpinner.setAdapter(filterOptions);
        mSpinner.setOnItemSelectedListener(this);
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
        Context context = view.getContext();
        this.emptyView = view.findViewById(android.R.id.empty);
        this.emptyView.setVisibility(View.GONE);

        this.recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        this.adapter = new HistoryRecyclerViewAdapter(this);
        this.adapter.registerAdapterDataObserver(this.emptyObserver);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onListFragmentInteraction(History item) {

    }

    @Override
    public Loader<List<? extends History>> onCreateLoader(int id, Bundle args) {
        return new HistoryLoader(this.getContext(), id == FILTER_ALL);
    }

    @Override
    public void onLoadFinished(Loader<List<? extends History>> loader, List<? extends History> data) {
        this.adapter.addAll(data);
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<? extends History>> loader) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.getLoaderManager().initLoader(position, null, this).forceLoad();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

    public static class HistoryLoader extends AsyncTaskLoader<List<? extends History>>{

        private final boolean filterAll;
        private HistoryService service = new HistoryService();
        private List<? extends History> items;

        public HistoryLoader(Context context, boolean all) {
            super(context);
            this.filterAll = all;
        }

        @Override
        public List<? extends History> loadInBackground() {
            this.items = this.service.list(filterAll);
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
