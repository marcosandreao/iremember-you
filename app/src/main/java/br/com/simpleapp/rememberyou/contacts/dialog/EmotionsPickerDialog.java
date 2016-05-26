package br.com.simpleapp.rememberyou.contacts.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.emotions.EmotionManager;

/**
 * Created by marcos on 26/05/16.
 */
public class EmotionsPickerDialog extends AppCompatDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.emotions_fragment, container);

        WindowManager.LayoutParams wmlp = getDialog().getWindow().getAttributes();
        wmlp.gravity = Gravity.FILL_HORIZONTAL | Gravity.BOTTOM;

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new FragmentAdapter(this.getChildFragmentManager()));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Window window = this.getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onStart() {
        super.onStart();
        final Dialog d = getDialog();
        if ( d != null){
            d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public class FragmentAdapter  extends FragmentPagerAdapter {

        final String[] categories;
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
            this.categories = EmotionManager.getInstance().getCategories();
        }

        @Override
        public Fragment getItem(int position) {
            return EmotionsFragment.getInstance(this.categories[position]);
        }

        @Override
        public int getCount() {
            return this.categories.length;
        }


    }

}
