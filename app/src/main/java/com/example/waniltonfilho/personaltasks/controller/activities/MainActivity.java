package com.example.waniltonfilho.personaltasks.controller.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.waniltonfilho.personaltasks.R;
import com.example.waniltonfilho.personaltasks.controller.adapter.WalletTransactionAdapter;
import com.example.waniltonfilho.personaltasks.controller.fragment.ChangeWalletFragment;
import com.example.waniltonfilho.personaltasks.controller.tasks.TaskWallet;
import com.example.waniltonfilho.personaltasks.model.entities.Login;
import com.example.waniltonfilho.personaltasks.model.entities.Wallet;
import com.example.waniltonfilho.personaltasks.model.entities.WalletTransaction;
import com.example.waniltonfilho.personaltasks.model.persistance.wallet_transaction.WalletRepository;
import com.example.waniltonfilho.personaltasks.model.service.WalletService;
import com.example.waniltonfilho.personaltasks.model.service.WalletTransactionService;
import com.example.waniltonfilho.personaltasks.util.MyValueFormatter;
import com.melnykov.fab.FloatingActionButton;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

/**
 * Created by wanilton.filho on 22/01/2016.
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {


    private Toolbar mToolbar;
    public static Login selectedLogin;
    private MaterialSearchView mSearchView;
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView mRecyclerView;
    private List<WalletTransaction> mListTransactions;
    private TextView mTextViewMoney;
    private boolean dialogVisible = false;
    private ChangeWalletFragment changeFragment;
    private Wallet wallet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindComponents();
    }

    private void bindComponents() {
        new TaskWallet().execute();
        String ae;
        checkWallet();
        bindToolbar();
        bindFloatingButton();
        bindRecyclerView();
        bindTextViewMoney();
        bindNavigationView();
        //bindSearchView();

    }

    private void checkWallet() {
        if (WalletRepository.getWallet() == null) {
            wallet = new Wallet();
            wallet.setValue(0f);
            WalletService.save(wallet);
        } else {
            wallet = WalletRepository.getWallet();
        }
    }

    private void bindNavigationView() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void bindTextViewMoney() {
        MyValueFormatter myValueFormatter = new MyValueFormatter();
        mTextViewMoney = (TextView) findViewById(R.id.textViewMoney);
        mTextViewMoney.setText(myValueFormatter.getMaskFormatted(wallet.getValue()));
    }

    private void bindRecyclerView() {
        mListTransactions = WalletTransactionService.getLastTransactions(2);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerLastTransaction);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new WalletTransactionAdapter(mListTransactions, this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListTransaction();
    }

    private void updateListTransaction() {

    }

    private void bindFloatingButton() {
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fabAddTransaction);
        setupFloatingButton(mFloatingActionButton);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog(v);
            }
        });
    }


    private void bindSearchView() {
        //mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                Animation animation = AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_out);
                animation.setDuration(3000);
                animation.start();
            }
        });
        initLogin();
        mSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        mSearchView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mSearchView.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
    }


    private void initLogin() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.selectedLogin = getIntent().getExtras().getParcelable(LoginMainActivity.PARAM_LOGIN);
        }
        this.selectedLogin = this.selectedLogin == null ? new Login() : this.selectedLogin;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//
//        MenuItem item = menu.findItem(R.id.action_search);
//        mSearchView.setMenuItem(item);
//
//        return true;
//    }

    private void bindToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setupToolbar(mToolbar);
    }

    private void showAddDialog(View v) {
        String ae = "";
        if (!dialogVisible) {
            changeFragment = new ChangeWalletFragment(0, mTextViewMoney, mRecyclerView, getCategories());
            FragmentTransaction fm = getFragmentManager().beginTransaction();
            fm.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
            fm.replace(R.id.frameChange, changeFragment);
            fm.commit();
            dialogVisible = true;
        } else {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right)
                    .remove(changeFragment)
                    .commit();
            dialogVisible = false;
        }

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_list:
                Intent goToListActivity = new Intent(MainActivity.this, ListActivity.class);
                startActivity(goToListActivity);
                break;
            case R.id.nav_graph:
                Intent goToGraphActivity = new Intent(MainActivity.this, Chart.class);
                startActivity(goToGraphActivity);
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    mMonthTitle.setOnTouchListener(new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            Drawable[] compoundDrawables = mMonthTitle.getCompoundDrawables();
//            Drawable leftDrawable = compoundDrawables[0];
//            Drawable rightDrawable = compoundDrawables[2];
//            float viewX = mMonthTitle.getWidth();
//            float eventX = event.getX();
//            if (eventX < leftDrawable.getMinimumWidth()) {
//                mMonthTitle.setText(mManipulateList.swipe_left(mMonthTitle.getText().toString()));
//            }
//            if (eventX > (viewX - rightDrawable.getMinimumWidth())) {
//                mMonthTitle.setText(mManipulateList.swipe_right(mMonthTitle.getText().toString()));
//            }
//
//            return false;
//        }
//    });
}
