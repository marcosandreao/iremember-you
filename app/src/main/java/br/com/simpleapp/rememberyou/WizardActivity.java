package br.com.simpleapp.rememberyou;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.AccountPicker;

import br.com.simpleapp.rememberyou.gcm.QuickstartPreferences;
import br.com.simpleapp.rememberyou.gcm.RegistrationIntentService;
import br.com.simpleapp.rememberyou.wizard.IWizardListener;

public class WizardActivity extends AppCompatActivity implements IWizardListener {

    private boolean lockBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new StepOneFragment()).commit();
    }

    @Override
    public void gotoStepTwo(String account, String name) {

        final Bundle mBundle = new Bundle();
        mBundle.putString(AccountManager.KEY_ACCOUNT_NAME, account);
        mBundle.putString("name", name);

        final StepTwoFragment fragment = new StepTwoFragment();
        fragment.setArguments(mBundle);

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(android.R.id.content, fragment);

        transaction.commit();

    }

    @Override
    public void lockBackButton(boolean lock) {
        this.lockBackButton = lock;
    }

    @Override
    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        if (!this.lockBackButton){
            super.onBackPressed();
        }
    }

    public static class StepOneFragment extends Fragment implements View.OnClickListener {

        private IWizardListener mListener;

        private static final int REQUEST_CHOOSE_ACCOUNT = 1;

        private Button btChoose;

        private EditText tvName;
        private String account;

        private View tvChooseError;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            try {
                mListener = (IWizardListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
            }
        }
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.wizard_stepone, null);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            this.btChoose = (Button) view.findViewById(R.id.bt_choose);
            this.btChoose.setOnClickListener(this);
            view.findViewById(R.id.bt_confirmar).setOnClickListener(this);
            this.tvName = (EditText) view.findViewById(R.id.input_name);
            this.tvChooseError = view.findViewById(R.id.tv_choose_error);
            this.tvChooseError.setVisibility(View.GONE);

        }

        @Override
        public void onClick(View v) {
            if ( v.getId() == R.id.bt_choose ) {
                this.pickerAccount();
            } else if ( v.getId() == R.id.bt_confirmar ) {
                boolean valid = true;
                 if ("".equals(this.tvName.getText().toString())) {
                     this.tvName.setError(getActivity().getString(R.string.name_required));
                     valid = false;
                 }
                if (account == null || "".equals(account)) {
                    valid = false;
                    this.tvChooseError.setVisibility(View.VISIBLE);
                    this.btChoose.requestFocus();
                } else {
                    this.tvChooseError.setVisibility(View.GONE);
                }
                if ( valid ) {
                    this.mListener.gotoStepTwo(this.account, this.tvName.getText().toString());
                }

            }
        }

        private void pickerAccount() {
            final Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                    false, null, null, null, null);
            startActivityForResult(intent, REQUEST_CHOOSE_ACCOUNT);
        }


        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CHOOSE_ACCOUNT && resultCode == RESULT_OK) {
                this.account = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                this.btChoose.setText(this.account);
                this.tvChooseError.setVisibility(View.GONE);
            } else if ( requestCode == REQUEST_CHOOSE_ACCOUNT ) {
                this.tvChooseError.setVisibility(View.GONE);
            }
        }

    }

    public static class StepTwoFragment extends Fragment {

        private IWizardListener mListener;
        private String account;
        private String name;
        private boolean isReceiverRegistered;

        private ProgressBar mRegistrationProgressBar;
        private TextView mInformationTextView;
        private View btStart;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            try {
                mListener = (IWizardListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
            }
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);


        }


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.wizard_steptwo, null);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            this.mRegistrationProgressBar = (ProgressBar) view.findViewById(R.id.registrationProgressBar);
            this.mInformationTextView = (TextView) view.findViewById(R.id.informationTextView);

            this.account = getArguments().getString(AccountManager.KEY_ACCOUNT_NAME);
            this.name = getArguments().getString("name");

            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            sharedPreferences.edit().putString(QuickstartPreferences.NICK_NAME, this.name).apply();

            this.registerReceiver();

            this.registerDevice(this.account);

            this.btStart = view.findViewById(R.id.bt_confirmar);
            this.btStart.setVisibility(View.GONE);
            this.btStart.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.startMainActivity();
                }
            });

            this.mListener.lockBackButton(true);

        }

        private void registerFinish(boolean sendToken){
            this.mListener.lockBackButton(false);
            if (sendToken) {
                this.mRegistrationProgressBar.setVisibility(View.GONE);
                this.mInformationTextView.setText(R.string.register_success);
                this.btStart.setVisibility(View.VISIBLE);
            } else {
                this.mInformationTextView.setText(R.string.register_error);
            }
        }

        private void registerDevice(String accountName) {
            Intent intent = new Intent(this.getActivity(), RegistrationIntentService.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
            this.getActivity().startService(intent);
        }

        @Override
        public void onDestroy() {
            LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
            isReceiverRegistered = false;
            super.onDestroy();
        }

        private void registerReceiver(){
            if(!isReceiverRegistered) {
                LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                        new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
                isReceiverRegistered = true;
            }
        }

        private final BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                registerFinish(sentToken);
            }
        };

    }

}
