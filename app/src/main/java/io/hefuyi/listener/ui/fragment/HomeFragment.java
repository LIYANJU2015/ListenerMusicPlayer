package io.hefuyi.listener.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.hefuyi.listener.ListenerApp;
import io.hefuyi.listener.R;

import io.hefuyi.listener.injector.component.ApplicationComponent;
import io.hefuyi.listener.injector.component.DaggerHomeComponent;
import io.hefuyi.listener.injector.component.HomeComponent;
import io.hefuyi.listener.injector.module.ActivityModule;
import io.hefuyi.listener.injector.module.HomeModule;
import io.hefuyi.listener.mvp.contract.HomeContract;
import io.hefuyi.listener.mvp.model.HomeSound;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by liyanju on 2017/11/18.
 */

public class HomeFragment extends Fragment implements HomeContract.View {

    @Inject
    HomeContract.Presenter mPresenter;

    @BindView(R.id.view_empty)
    ViewStub emptyView;

    @BindView(R.id.home_recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.progressbar)
    MaterialProgressBar progressBar;

    private static ArrayList<HomeSoundInAdapter> mDatas = new ArrayList<>();

    private Context mContext;

    private MultiItemTypeAdapter itemTypeAdapter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependences();

        mPresenter.attachView(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mDatas.size() == 0) {
            progressBar.setVisibility(View.VISIBLE);
            mPresenter.requestHomeSound();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);
        itemTypeAdapter = new MultiItemTypeAdapter(mContext, mDatas);
        itemTypeAdapter.addItemViewDelegate(new TitleItemDelagate());
        itemTypeAdapter.addItemViewDelegate(new ListItemDelagate());
        itemTypeAdapter.addItemViewDelegate(new SingleItemDelagate());
        recyclerView.setAdapter(itemTypeAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    public void injectDependences() {
        ApplicationComponent applicationComponent = ((ListenerApp) getActivity().getApplication()).getApplicationComponent();
        HomeComponent homeComponent = DaggerHomeComponent.builder()
                .applicationComponent(applicationComponent)
                .activityModule(new ActivityModule(getActivity()))
                .homeModule(new HomeModule())
                .build();
        homeComponent.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_layout, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void showHomeSound(HomeSound homeSound) {
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        try {
            HomeSoundInAdapter.convertHomeSound(homeSound, mDatas);
            itemTypeAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            mDatas.clear();
            showEmptyView();
        }
    }

    @Override
    public void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    class SingleItemDelagate implements ItemViewDelegate<HomeSoundInAdapter> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.home_singleitem_layout;
        }

        @Override
        public boolean isForViewType(HomeSoundInAdapter item, int position) {
            return item.type == HomeSoundInAdapter.SINGLE_ITEM_TYPE;
        }

        @Override
        public void convert(ViewHolder holder, HomeSoundInAdapter homeSoundInAdapter, int position) {
            holder.setText(R.id.single_title_tv, homeSoundInAdapter.contentsBean2.getName());

            Glide.with(mContext).load(homeSoundInAdapter.contentsBean2.getContents().get(0).getAlbum_images()).crossFade()
                    .placeholder(R.drawable.icon_album_default).error(R.drawable.icon_album_default).centerCrop()
                    .into((ImageView) holder.getView(R.id.item_album_iv));
            holder.setOnClickListener(R.id.home_single_relative, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    class ListItemDelagate implements ItemViewDelegate<HomeSoundInAdapter> {
        @Override
        public int getItemViewLayoutId() {
            return R.layout.home_list_item_layout;
        }

        @Override
        public boolean isForViewType(HomeSoundInAdapter item, int position) {
            return item.type == HomeSoundInAdapter.LIST_ITEM_TYPE;
        }

        @Override
        public void convert(ViewHolder holder, HomeSoundInAdapter homeSoundInAdapter, int position) {
            holder.setText(R.id.song_name_tv1, homeSoundInAdapter.list.get(0).getSong_name());
            holder.setText(R.id.song_singer_tv1, homeSoundInAdapter.list.get(0).getSinger());
            Glide.with(mContext).load(homeSoundInAdapter.list.get(0).getAlbum_images()).crossFade().centerCrop()
                    .placeholder(R.drawable.icon_album_default).error(R.drawable.icon_album_default)
                    .into((ImageView) holder.getView(R.id.song_image1));
            holder.setOnClickListener(R.id.list_item_linear3, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            holder.setText(R.id.song_name_tv2, homeSoundInAdapter.list.get(1).getSong_name());
            holder.setText(R.id.song_singer_tv2, homeSoundInAdapter.list.get(1).getSinger());
            Glide.with(mContext).load(homeSoundInAdapter.list.get(1).getAlbum_images()).crossFade().centerCrop()
                    .placeholder(R.drawable.icon_album_default).error(R.drawable.icon_album_default)
                    .into((ImageView) holder.getView(R.id.song_image2));
            holder.setOnClickListener(R.id.list_item_linear2, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            holder.setText(R.id.song_name_tv3, homeSoundInAdapter.list.get(2).getSong_name());
            holder.setText(R.id.song_singer_tv3, homeSoundInAdapter.list.get(2).getSinger());
            Glide.with(mContext).load(homeSoundInAdapter.list.get(2).getAlbum_images()).crossFade().centerCrop()
                    .placeholder(R.drawable.icon_album_default).error(R.drawable.icon_album_default)
                    .into((ImageView) holder.getView(R.id.song_image3));
            holder.setOnClickListener(R.id.list_item_linear3, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    static class TitleItemDelagate implements ItemViewDelegate<HomeSoundInAdapter> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.home_title_item_layout;
        }

        @Override
        public boolean isForViewType(HomeSoundInAdapter item, int position) {
            return item.type == HomeSoundInAdapter.TITLE_ITEM_TYPE;
        }

        @Override
        public void convert(ViewHolder holder, HomeSoundInAdapter homeSoundInAdapter, int position) {
            holder.setText(R.id.home_item_title, homeSoundInAdapter.contentsBeanX.getName());
            holder.setOnClickListener(R.id.title_relative, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            if (homeSoundInAdapter.contentsBeanX.getData_type() == 0) {
                holder.getView(R.id.home_back_iv).setVisibility(View.INVISIBLE);
            } else {
                holder.getView(R.id.home_back_iv).setVisibility(View.VISIBLE);
            }
        }
    }

    static class HomeSoundInAdapter {

        public static final int TITLE_ITEM_TYPE = 1;

        public static final int LIST_ITEM_TYPE = 2;

        public static final int SINGLE_ITEM_TYPE = 3;

        public int type;

        public ArrayList<HomeSound.ContentsBeanX.ContentsBean> list = new ArrayList<>();
        public HomeSound.ContentsBean2 contentsBean2;

        public HomeSound.ContentsBeanX contentsBeanX;


        public static void convertHomeSound(HomeSound homeSound, ArrayList<HomeSoundInAdapter> homeSoundInAdapters) {
            List<HomeSound.ContentsBeanX> arrayList = homeSound.getContents();

            for (HomeSound.ContentsBeanX contentsBeanX : arrayList) {
                HomeSoundInAdapter homeSoundInAdapter = new HomeSoundInAdapter();
                if (contentsBeanX.getData_type() == 0) {
                    homeSoundInAdapter.type = TITLE_ITEM_TYPE;
                    homeSoundInAdapter.contentsBeanX = new HomeSound.ContentsBeanX();
                    homeSoundInAdapter.contentsBeanX.setName(contentsBeanX.getName());
                    homeSoundInAdapters.add(homeSoundInAdapter);

                    for (HomeSound.ContentsBean2 contentsBean2 : contentsBeanX.contents2) {
                        homeSoundInAdapter = new HomeSoundInAdapter();
                        homeSoundInAdapter.type = SINGLE_ITEM_TYPE;
                        homeSoundInAdapter.contentsBean2 = contentsBean2;
                        homeSoundInAdapters.add(homeSoundInAdapter);
                    }

                } else {

                    homeSoundInAdapter.type = TITLE_ITEM_TYPE;
                    homeSoundInAdapter.contentsBeanX = contentsBeanX;
                    homeSoundInAdapters.add(homeSoundInAdapter);

                    homeSoundInAdapter = new HomeSoundInAdapter();
                    homeSoundInAdapter.type = LIST_ITEM_TYPE;
                    homeSoundInAdapter.list.add(contentsBeanX.contents.get(4));
                    homeSoundInAdapter.list.add(contentsBeanX.contents.get(1));
                    homeSoundInAdapter.list.add(contentsBeanX.contents.get(2));
                    homeSoundInAdapters.add(homeSoundInAdapter);
                }
            }
        }

    }
}
