package com.example.waniltonfilho.personaltasks.controller.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.waniltonfilho.personaltasks.R;
import com.example.waniltonfilho.personaltasks.controller.adapter.CategoryAdapterr;
import com.example.waniltonfilho.personaltasks.controller.adapter.WalletTransactionAdapter;
import com.example.waniltonfilho.personaltasks.model.entities.Wallet;
import com.example.waniltonfilho.personaltasks.model.entities.WalletTransaction;
import com.example.waniltonfilho.personaltasks.model.persistance.wallet_transaction.WalletRepository;
import com.example.waniltonfilho.personaltasks.model.service.WalletTransactionService;
import com.example.waniltonfilho.personaltasks.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wanilton.filho on 04/02/2016.
 */
public class ChangeWalletFragment extends Fragment implements View.OnClickListener {

    private TextView textViewTitle;
    private EditText editTextName;
    private EditText editTextPrice;
    private Button mButtonConfirm;
    private Button mButtonCancel;
    private WalletTransaction mWalletTransaction;
    private int mOperation;
    private FrameLayout mFrameAnimation;
    private Spinner mSpinnerIcons;
    private Integer mIconSelected;
    private TextView mTextViewMoney;
    private RecyclerView recyclerViewWallet;

    public ChangeWalletFragment(int operation, TextView textViewMoney, RecyclerView recyclerView){
        mOperation = operation;
        mTextViewMoney = textViewMoney;
        recyclerViewWallet = recyclerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_wallet, container, false);
        textViewTitle = (TextView) v.findViewById(R.id.textViewTitleChangeWallet);
        editTextName = (EditText) v.findViewById(R.id.editTextNameWallet);
        //editTextDate.addTextChangedListener(new EditTextMaskDate(editTextDate));
        editTextPrice = (EditText) v.findViewById(R.id.editTextPriceWallet);
        mButtonCancel = (Button) v.findViewById(R.id.buttonCancelChange);
        mButtonCancel.setOnClickListener(this);
        mButtonConfirm = (Button) v.findViewById(R.id.buttonConfirmChange);
        mButtonConfirm.setOnClickListener(this);
        mFrameAnimation = (FrameLayout) v.findViewById(R.id.frameAnimation);
        mSpinnerIcons = (Spinner) v.findViewById(R.id.spinnerIcons);
        bindSpinner(mSpinnerIcons);
        return v;
    }

    private void bindSpinner(Spinner spinner) {
        spinner.setAdapter(new CategoryAdapterr(getActivity()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mIconSelected = (Integer) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startFrameAnimation();
    }

    private void startFrameAnimation() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        mFrameAnimation.startAnimation(fadeInAnimation);
    }

    private void endFrameAnimation() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
        mFrameAnimation.startAnimation(fadeInAnimation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCancelChange:
                onButtonCancel();
                break;
            case R.id.buttonConfirmChange:
                onButtonConfirm();
                break;
        }
    }

    private void onButtonCancel() {
        endFrameAnimation();
        getActivity().getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right)
                .remove(this)
                .commit();
    }

    private void onButtonConfirm() {
        bindWalletTransaction();
        if(mWalletTransaction != null) {
            WalletTransactionService.save(mWalletTransaction, mOperation);
            endFrameAnimation();
            updateTransactions();
            getActivity().getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right)
                    .remove(this)
                    .commit();
            changeWalletTextValue();
            Snackbar.make(getView(), getString(R.string.action_button_transaction_confirm), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void changeWalletTextValue() {
        Wallet wallet = WalletRepository.getWallet();
        mTextViewMoney.setText(wallet.getValue().toString());
    }


    private void bindWalletTransaction() {
        if(!StringUtil.isNullOrBlank(editTextPrice.getText().toString(), editTextPrice, getActivity()) && (StringUtil.isDouble(editTextPrice.getText().toString(), editTextPrice, getActivity()))){
            mWalletTransaction = new WalletTransaction();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDateandTime = sdf.format(new Date());
            mWalletTransaction.setDate(currentDateandTime);
            mWalletTransaction.setName(editTextName.getText() != null ? editTextName.getText().toString() : "Transação");
            mWalletTransaction.setPrice(Float.parseFloat(editTextPrice.getText().toString()));
            mWalletTransaction.setItemCategory(mIconSelected);
        }
    }

    private void updateTransactions(){
        List<WalletTransaction> mListTransactions = WalletTransactionService.getLastTransactions(2);
        WalletTransactionAdapter adapter = (WalletTransactionAdapter) recyclerViewWallet.getAdapter();
        adapter.setItens(mListTransactions);
        adapter.notifyDataSetChanged();
    }

}
