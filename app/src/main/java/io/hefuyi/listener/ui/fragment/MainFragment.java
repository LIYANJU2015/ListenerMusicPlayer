package io.hefuyi.listener.ui.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.appthemeengine.ATE;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.hefuyi.listener.Constants;
import io.hefuyi.listener.R;
import io.hefuyi.listener.util.ATEUtil;
import io.hefuyi.listener.util.DensityUtil;
import io.hefuyi.listener.util.PreferencesUtility;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private PreferencesUtility mPreferences;
    private String action;

    public static MainFragment newInstance(String action) {
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        switch (action) {
            case Constants.NAVIGATE_ALLSONG:
                bundle.putString(Constants.PLAYLIST_TYPE, action);
                break;
            case Constants.NAVIGATE_PLAYLIST_RECENTADD:
                bundle.putString(Constants.PLAYLIST_TYPE, action);
                break;
            case Constants.NAVIGATE_PLAYLIST_RECENTPLAY:
                bundle.putString(Constants.PLAYLIST_TYPE, action);
                break;
            case Constants.NAVIGATE_PLAYLIST_FAVOURATE:
                bundle.putString(Constants.PLAYLIST_TYPE, action);
                break;
            default:
                throw new RuntimeException("wrong action type");
        }
        mainFragment.setArguments(bundle);
        return mainFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferencesUtility.getInstance(getActivity());

        if (getArguments() != null) {
            action = getArguments().getString(Constants.PLAYLIST_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ATE.apply(this, ATEUtil.getATEKey(getActivity()));

        ButterKnife.bind(this, view);

        if (Build.VERSION.SDK_INT < 21 && view.findViewById(R.id.status_bar) != null) {
            view.findViewById(R.id.status_bar).setVisibility(View.GONE);
            int statusBarHeight = DensityUtil.getStatusBarHeight(getContext());
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            toolbar.setPadding(0, statusBarHeight, 0, 0);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            params.setScrollFlags(0);
            toolbar.setLayoutParams(params);
        }

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(6);

        switch (action) {
            case Constants.NAVIGATE_ALLSONG:
                ab.setTitle(R.string.library);
                if (viewPager != null) {
                    viewPager.setCurrentItem(mPreferences.getStartPageIndex());
                }
                break;
            case Constants.NAVIGATE_PLAYLIST_RECENTADD:
                ab.setTitle(R.string.recent_add);
                if (viewPager != null && mPreferences.getStartPageIndex() == 0) {
                    viewPager.setCurrentItem(1);
                } else {
                    viewPager.setCurrentItem(mPreferences.getStartPageIndex());
                }
                break;
            case Constants.NAVIGATE_PLAYLIST_RECENTPLAY:
                ab.setTitle(R.string.recent_play);
                if (viewPager != null && mPreferences.getStartPageIndex() == 0) {
                    viewPager.setCurrentItem(1);
                } else {
                    viewPager.setCurrentItem(mPreferences.getStartPageIndex());
                }
                break;
            case Constants.NAVIGATE_PLAYLIST_FAVOURATE:
                ab.setTitle(R.string.favourate);
                if (viewPager != null && mPreferences.getStartPageIndex() == 0) {
                    viewPager.setCurrentItem(1);
                } else {
                    viewPager.setCurrentItem(mPreferences.getStartPageIndex());
                }
                break;
        }


    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(HomeFragment.newInstance(), this.getString(R.string.main_page));
        adapter.addFragment(RecommendFragment.newInstance(), this.getString(R.string.main_recomm));
        adapter.addFragment(SongsFragment.newInstance(action), this.getString(R.string.songs));
        adapter.addFragment(ArtistFragment.newInstance(action), this.getString(R.string.artists));
        adapter.addFragment(AlbumFragment.newInstance(action), this.getString(R.string.albums));
        adapter.addFragment(new FoldersFragment(), this.getString(R.string.folders));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPreferences.setStartPageIndex(viewPager.getCurrentItem());
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

}
