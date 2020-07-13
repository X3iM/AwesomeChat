package com.android.greena.awesomechat.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.android.greena.awesomechat.fragments.ChatFragment;
import com.android.greena.awesomechat.fragments.FriendsFragment;
import com.android.greena.awesomechat.fragments.RequestsFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    public static final int COUNT_OF_FRAGMENT_ITEM = 3;

    public PagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0 : return new RequestsFragment();
            case 1 : return new ChatFragment();
            case 2 : return new FriendsFragment();
        }
//                  ---DEFAULT CHATFRAGMENT---                  \\
        return new ChatFragment();
    }

    @Override
    public int getCount() {
        return COUNT_OF_FRAGMENT_ITEM;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0 : return "REQUEST";
            case 1 : return "CHATS";
            case 2 : return "FRIENDS";
        }

        return super.getPageTitle(position);
    }
}