package br.com.simpleapp.rememberyou.contacts.dialog;

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

import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.emotions.EmotionManager;

public class EmotionsFragment extends Fragment implements EmotionsRecyclerViewAdapter.OnListFragmentInteractionListener,
        LoaderManager.LoaderCallbacks<String[]> {

    private int mColumnCount = 6;

    private EmotionsRecyclerViewAdapter adapter;

    public EmotionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static EmotionsFragment getInstance(String category){
        final  Bundle mBundle = new Bundle();
        mBundle.putString("cat", category);
        final EmotionsFragment fragment = new EmotionsFragment();
        fragment.setArguments(mBundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picker_emotions_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            this.adapter = new EmotionsRecyclerViewAdapter(getCategory(), this);
            recyclerView.setAdapter(this.adapter);
        }
        return view;
    }

    private String getCategory() {
        return this.getArguments().getString("cat");
    }


    @Override
    public void onEmotionsSelected(String category, String item) {

    }

    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {
        return new EmotionLoader(this.getContext(), this.getCategory());
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        this.adapter.addAll(data);
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }

    public static class EmotionLoader extends AsyncTaskLoader<String[]> {

        private final String category;

        private String[] emotions;

        public EmotionLoader(Context ctx, String category){
            super(ctx);
            this.category = category;
        }

        @Override
        public String[] loadInBackground() {
            this.emotions = EmotionManager.getInstance().listBycategories(this.category, this.getContext());
            return this.emotions;
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override
        protected void onStartLoading() {
            if (this.emotions != null) {
                deliverResult(this.emotions);
            }

            if (takeContentChanged() || this.emotions == null) {
                forceLoad();
            }
        }
    }
}
