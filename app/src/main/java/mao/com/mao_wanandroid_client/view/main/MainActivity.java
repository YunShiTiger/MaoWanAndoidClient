package mao.com.mao_wanandroid_client.view.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import mao.com.mao_wanandroid_client.R;
import mao.com.mao_wanandroid_client.application.Constants;
import mao.com.mao_wanandroid_client.base.activity.BaseActivity;
import mao.com.mao_wanandroid_client.compoent.RxBus;
import mao.com.mao_wanandroid_client.compoent.event.LoginStatusEvent;
import mao.com.mao_wanandroid_client.model.http.cookie.CookieManager;
import mao.com.mao_wanandroid_client.model.modelbean.rank.RankData;
import mao.com.mao_wanandroid_client.presenter.main.MainContract;
import mao.com.mao_wanandroid_client.presenter.main.MainPresenter;
import mao.com.mao_wanandroid_client.utils.NavHelper;
import mao.com.mao_wanandroid_client.utils.StartDetailPage;
import mao.com.mao_wanandroid_client.utils.StatusBarUtil;
import mao.com.mao_wanandroid_client.utils.ToastUtils;
import mao.com.mao_wanandroid_client.utils.ToolsUtils;
import mao.com.mao_wanandroid_client.view.drawer.fragment.ArticleShareDialogFragment;
import mao.com.mao_wanandroid_client.view.drawer.fragment.CoinFragment;
import mao.com.mao_wanandroid_client.view.drawer.fragment.CoinRankFragment;
import mao.com.mao_wanandroid_client.view.drawer.fragment.CollectionPageFragment;
import mao.com.mao_wanandroid_client.view.drawer.fragment.CommonWebFragment;
import mao.com.mao_wanandroid_client.view.drawer.fragment.SettingsFragment;
import mao.com.mao_wanandroid_client.view.drawer.fragment.SquareFragment;
import mao.com.mao_wanandroid_client.view.main.fragment.HomePageFragment;
import mao.com.mao_wanandroid_client.view.main.fragment.KnowledgeHierarchyPageFragment;
import mao.com.mao_wanandroid_client.view.main.fragment.NavigationFragment;
import mao.com.mao_wanandroid_client.view.main.fragment.OfficialAccountsPageFragment;
import mao.com.mao_wanandroid_client.view.main.fragment.ProjectFragment;
import mao.com.mao_wanandroid_client.view.main.fragment.SearchFragment;
import mao.com.mao_wanandroid_client.widget.CircleImageView;


public class MainActivity extends BaseActivity<MainPresenter>
        implements MainContract.MainView,
        View.OnClickListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener ,
        NavHelper.OnTabChangeListener<String> {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.fab_share)
    FloatingActionButton fabShare;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.main_bottom_navigation_view)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.tv_page_title)
    TextView pageTitle;
    @BindView(R.id.iv_search)
    ImageView mSearch;


    //用户头像
    private CircleImageView userImageIcon;
    //用户名
    private TextView mUserName;
    //积分 等级
    private TextView mUserCoin;
    private TextView mUserRank;

    //导航 tab 切换帮助类
    private NavHelper mNavHelper;

    SearchFragment mSearchFragment;
    CommonWebFragment mCommonWebFragment;
    SettingsFragment mSettingsFragment;
    CoinFragment mCoinFragment;
    CoinRankFragment mCoinRankFragment;
    ArticleShareDialogFragment articleShareDialogFragment;

    // 是否开启了主页，没有开启则会返回主页
    public static boolean isLaunch = false;

    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLaunch = true;
    }

    @Override
    protected void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        assert supportActionBar != null;
        //除去toolbar 默认显示的标题
        supportActionBar.setDisplayShowTitleEnabled(false);
        pageTitle.setText(getString(R.string.page_home));
        //沉浸式状态栏
        StatusBarUtil.setColorNoTranslucentForDrawerLayout(this,drawer,ContextCompat.getColor(this,R.color.colorPrimary));

    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();
        initFragment();
        initView();
    }

    private void initFragment() {
        /*HomePageFragment fragment = findFragment(HomePageFragment.class);
        if (fragment == null) {
            loadRootFragment(R.id.page_fragment_container, HomePageFragment.newInstance());
            //loadMultipleRootFragment();
        }*/
       /*mNavHelper =new NavHelper<String>(this,R.id.page_fragment_container,getSupportFragmentManager(),this)
               .add(R.id.tab_main,new NavHelper.Tab<String>(HomePageFragment.class,getString(R.string.page_home),Constants.TAG_HOME))
               .add(R.id.nav_home,new NavHelper.Tab<String>(HomePageFragment.class,getString(R.string.page_home),Constants.TAG_HOME))
               .add(R.id.tab_knowledge_hierarchy,new NavHelper.Tab<String>(KnowledgeHierarchyPageFragment.class,getString(R.string.knowledge_hierarchy),Constants.TAG_KNOWLEGER))
               .add(R.id.tab_official_accounts,new NavHelper.Tab<String>(OfficialAccountsPageFragment.class,getString(R.string.official_accounts),Constants.TAG_OFFICIAL))
               .add(R.id.tab_navigation,new NavHelper.Tab<String>(NavigationFragment.class,getString(R.string.navigation),Constants.TAG_NAVIGATION))
               .add(R.id.tab_project,new NavHelper.Tab<String>(ProjectFragment.class,getString(R.string.project),Constants.TAG_PROJECT))
               .add(R.id.collect_page,new NavHelper.Tab<String>(CollectionPageFragment.class,getString(R.string.nav_collect),Constants.TAG_COLLECTION));*/
        mNavHelper =new NavHelper<String>(this,R.id.page_fragment_container,getSupportFragmentManager(),this)
                .add(R.id.tab_main,HomePageFragment.newInstance())
                .add(R.id.nav_home,HomePageFragment.newInstance())
                .add(R.id.tab_knowledge_hierarchy,KnowledgeHierarchyPageFragment.newInstance())
                .add(R.id.tab_official_accounts,OfficialAccountsPageFragment.newInstance())
                .add(R.id.tab_navigation,NavigationFragment.newInstance())
                .add(R.id.tab_project,ProjectFragment.newInstance())
                .add(R.id.collect_page,CollectionPageFragment.newInstance())
                .add(R.id.square_page, SquareFragment.newInstance());
    }

    private void initView() {
        fab.setOnClickListener(this);
        fabShare.setOnClickListener(this);
        mSearch.setVisibility(View.VISIBLE);
        mSearch.setOnClickListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        //默认选择 首页
        navigationView.setCheckedItem(R.id.nav_home);
        //获取侧边栏头部属性
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        userImageIcon = headerView.findViewById(R.id.imageView_user_icon);
        userImageIcon.setOnClickListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        Menu menu = bottomNavigationView.getMenu();
        menu.performIdentifierAction(R.id.tab_main,0);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }


    @Override
    protected void initEventAndData() {
        super.initEventAndData();
    }
    //左侧导航栏、底部导航栏 点击事件
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id){
            case R.id.collect_page:
                //收藏
                if(!mPresenter.getLoginStatus()){
                    //是否已经登录
                    StartDetailPage.start(this,null, Constants.PAGE_LOGIN,Constants.ACTION_LOGIN_ACTIVITY);
                    return false;
                }
                initPageTitle(getString(R.string.nav_collect));
                bottomNavigationView.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
                break;
            case R.id.nav_settings:
                //设置
                if (mSettingsFragment == null) {
                    mSettingsFragment = SettingsFragment.newInstance();
                }
                if (!isDestroyed() && mSettingsFragment.isAdded()) {
                    mSettingsFragment.dismiss();
                }
                mSettingsFragment.show(getSupportFragmentManager(),"showSettings");
                break;
            case R.id.nav_todo:
                //TODO
                ToastUtils.showToast("暂未实现");
                break;
            case R.id.nav_home:
            case R.id.tab_main:
                //主页
                initPageTitle(getString(R.string.page_home));
                bottomNavigationView.setVisibility(View.VISIBLE);
                break;
            case R.id.square_page:
                //广场
                initPageTitle(getString(R.string.square_text));
                fab.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.GONE);
                break;
            case R.id.tab_knowledge_hierarchy:
                //知识体系
                initPageTitle(getString(R.string.knowledge_hierarchy));
                break;
            case R.id.tab_official_accounts:
                //公众号
                initPageTitle(getString(R.string.official_accounts));
                break;
            case R.id.tab_navigation:
                //导航
                initPageTitle(getString(R.string.navigation));
                break;
            case R.id.tab_project:
                //项目
                initPageTitle(getString(R.string.project));
                break;
            case R.id.common_website:
                //常用网站
                if (mCommonWebFragment == null) {
                    mCommonWebFragment = CommonWebFragment.newInstance(getString(R.string.common_web));
                }
                if (!isDestroyed() && mCommonWebFragment.isAdded()) {
                    mCommonWebFragment.dismiss();
                }
                mCommonWebFragment.show(getSupportFragmentManager(),"showCommonWeb");
                break;
            case R.id.nav_coin_rank:
                //积分排行榜
                if (mCoinRankFragment == null) {
                    mCoinRankFragment = CoinRankFragment.newInstance();
                }
                if (!isDestroyed() && mCoinRankFragment.isAdded()) {
                    mCoinRankFragment.dismiss();
                }
                mCoinRankFragment.show(getSupportFragmentManager(),"showCoinRank");
                break;
            case R.id.nav_coin:
                //我的积分
                if(!mPresenter.getLoginStatus()){
                    //是否已经登录
                    StartDetailPage.start(this,null, Constants.PAGE_LOGIN,Constants.ACTION_LOGIN_ACTIVITY);
                    return false;
                }
                if (mCoinFragment == null) {
                    mCoinFragment = CoinFragment.newInstance();
                }
                if (!isDestroyed() && mCoinFragment.isAdded()) {
                    mCoinFragment.dismiss();
                }
                mCoinFragment.show(getSupportFragmentManager(),"showCoin");
                break;
                default:
                    break;
        }
       /* if (id == R.id.nav_collect) {
            Toast.makeText(this,"点击了收藏",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this,"点击了设置",Toast.LENGTH_SHORT).show();
        }*/
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //点击之后关闭DrawerLayout
        drawer.closeDrawer(GravityCompat.START);
        if(R.id.square_page == id){
           fabShare.setVisibility(View.VISIBLE);
        }else{
            fabShare.setVisibility(View.GONE);
        }

        if(R.id.common_website ==  id || R.id.nav_settings ==id || R.id.nav_todo ==id ){
            //如果是 常用网站todo 则不改变选中状态
            //navigationView 选中
            return false;
        }else if(R.id.nav_home == id){
            //回到首页
            bottomNavigationView.setSelectedItemId(R.id.tab_main);
            return mNavHelper.performClickMenuFragment(R.id.tab_main);
        } else {
            navigationView.setCheckedItem(id);
            return mNavHelper.performClickMenuFragment(id);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 加载对应的页面
     */
    private void initPageTitle(String pagetitle) {
       pageTitle.setText(pagetitle);

    }

    //view 点击事件
    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.fab_share:
                 //分享文章
                 if(!mPresenter.getLoginStatus()){
                     //进入登录界面
                     StartDetailPage.start(MainActivity.this,null, Constants.PAGE_LOGIN,Constants.ACTION_LOGIN_ACTIVITY);
                 }else {
                     //开启 分享 dialog
                     articleShareDialogFragment = ArticleShareDialogFragment.newInstance();
                     if (!isDestroyed() && articleShareDialogFragment.isAdded()) {
                         articleShareDialogFragment.dismiss();
                     }
                     articleShareDialogFragment.show(getSupportFragmentManager(),"showArticleCollectionDialog");
                 }
                 break;
             case R.id.imageView_user_icon: //用户个人头像点击
                 if(!mPresenter.getLoginStatus()){
                     //进入登录界面
                     StartDetailPage.start(MainActivity.this,null, Constants.PAGE_LOGIN,Constants.ACTION_LOGIN_ACTIVITY);
                 }else {
                     //Toast.makeText(MainActivity.this,"进入个人中心暂未实现",Toast.LENGTH_SHORT).show();
                     startActivity(new Intent(this, UserCenterActivity.class));
                 }
                 break;
             case R.id.iv_search:
                 if (mSearchFragment == null) {
                     mSearchFragment = SearchFragment.newInstance(Constants.RESULT_CODE_HOME_PAGE,0,"");
                 }
                 if (!isDestroyed() && mSearchFragment.isAdded()) {
                     mSearchFragment.dismiss();
                 }
                 mSearchFragment.show(getSupportFragmentManager(),"showMainSearch");
                 break;
             default:
                 break;
         }
    }

    /**
     * tab 切换回调
     * @param newTab
     * @param oldTab
     */
    @Override
    public void onTabChange(NavHelper.Tab<String> newTab, NavHelper.Tab<String> oldTab) {
        //Log.e("毛麒添","当前tab  "+newTab.extra + "SimpleName"+newTab.getClass().getSimpleName());
    }


    @Override
    public void showLoginView() {
        Log.e("毛麒添","登录成功");
        if(navigationView == null){
            return;
        }
        userImageIcon = navigationView.getHeaderView(0).findViewById(R.id.imageView_user_icon);
        mUserName = navigationView.getHeaderView(0).findViewById(R.id.textView_user_name);
        mUserName.setText(mPresenter.getLoginUserName());
        userImageIcon.setImageDrawable(getDrawable(R.mipmap.ic_launcher));
        mPresenter.getCoinAndRank();

    }

    @Override
    public void showLogoutView() {
        Log.e("毛麒添","显示为登录页面");
        userImageIcon = navigationView.getHeaderView(0).findViewById(R.id.imageView_user_icon);
        mUserName = navigationView.getHeaderView(0).findViewById(R.id.textView_user_name);
        mUserName.setText(getString(R.string.nav_header_title));
        CookieManager.getInstance().clearAllCookie();
        userImageIcon.setImageDrawable(getDrawable(R.drawable.ic_default_avatar));
        mUserCoin = navigationView.getHeaderView(0).findViewById(R.id.tv_user_coin);
        mUserRank = navigationView.getHeaderView(0).findViewById(R.id.tv_user_rank);
        mUserCoin.setVisibility(View.GONE);
        mUserRank.setVisibility(View.GONE);
    }

    @Override
    public void showSingOutSuccess() {
        RxBus.getDefault().post(new LoginStatusEvent(false,true));
        showLogoutView();
    }

    @Override
    public void showSingOutFail(String errorMsg) {
        ToastUtils.showToast(errorMsg);
    }
    //登录成功显示积分模块
    @Override
    public void showCoinAndRank(RankData rankData) {
        mUserCoin = navigationView.getHeaderView(0).findViewById(R.id.tv_user_coin);
        mUserRank = navigationView.getHeaderView(0).findViewById(R.id.tv_user_rank);
        mUserCoin.setVisibility(View.VISIBLE);
        mUserCoin.setText("积分："+rankData.getCoinCount());
        mUserRank.setVisibility(View.VISIBLE);
        mUserRank.setText("lv "+ ToolsUtils.getRank(rankData.getCoinCount()));
    }

    @Override
    public void onBackPressedSupport() {
        Log.e("毛麒添","onBackPressedSupport MainActivity 调用");
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }else {
                //再点一次退出程序
                doubleClickExit();
            }

    }
    private static Boolean mIsExit = false;

    //再点一次退出程序
    private void doubleClickExit() {
        Timer exitTimer = null;
        if (!mIsExit) {
            mIsExit = true;
            ToastUtils.showToast(getString(R.string.exit_again));
            exitTimer = new Timer();
            exitTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mIsExit = false;
                }
            }, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    protected void onDestroy() {
        isLaunch = false;
        super.onDestroy();
    }
}
