package br.com.simpleapp.rememberyou.contacts;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.simpleapp.rememberyou.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmotionsFragment extends Fragment {


    public EmotionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.emotions_fragment, container, false);
    }

}
