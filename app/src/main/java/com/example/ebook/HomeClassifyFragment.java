package com.example.ebook;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
class HomeClassifyFragment extends Fragment {
    private View contentView;
    private TabLayout tab;
    private ViewPager content;
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> fragmentTitles = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.homeclassify_content, null);
        return contentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tab = (TabLayout) view.findViewById(R.id.tl_homeclassify);
        content = (ViewPager) view.findViewById(R.id.vp_homeclassifyconetent);
        setViewPager();
        content.setOffscreenPageLimit(2);
        tab.setupWithViewPager(content);
    }
    private void setViewPager() {
        fragments.add(HomeClassifyListFragment.newInstance(0));
        fragments.add(HomeClassifyListFragment.newInstance(1));
        fragments.add(HomeClassifyListFragment.newInstance(2));
        fragments.add(HomeClassifyListFragment.newInstance(3));
        fragments.add(HomeClassifyListFragment.newInstance(4));
        fragments.add(HomeClassifyListFragment.newInstance(5));
        fragments.add(HomeClassifyListFragment.newInstance(6));
        fragments.add(HomeClassifyListFragment.newInstance(7));
        fragments.add(HomeClassifyListFragment.newInstance(8));
        fragments.add(HomeClassifyListFragment.newInstance(9));
        fragmentTitles.add("玄幻奇幻");
        fragmentTitles.add("武侠仙侠");
        fragmentTitles.add("女频言情");
        fragmentTitles.add("现代都市");
        fragmentTitles.add("历史军事");
        fragmentTitles.add("游戏竞技");
        fragmentTitles.add("科幻灵异");
        fragmentTitles.add("美文同人");
        fragmentTitles.add("剧本教程");
        fragmentTitles.add("名著杂志");

        ZoneFragmentAdapter adapter = new ZoneFragmentAdapter(getChildFragmentManager(),fragments,fragmentTitles);
        content.setAdapter(adapter);
    }
}
