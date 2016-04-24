package br.com.simpleapp.rememberyou.wizard;

/**
 * Created by socram on 24/04/16.
 */
public interface IWizardListener {

    void gotoStepTwo(String account, String name);

    void lockBackButton(boolean lock);

    void startMainActivity();
}
