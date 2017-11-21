package com.akshathjain.bookworm.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akshathjain.bookworm.R;

import java.io.Serializable;

public class Explorer extends Fragment implements Serializable{
    private Player.PlayerControls playerControls;
    private SearchView searchView;
    private FragmentManager fm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_explorer, container, false);

        Toolbar toolbar = layout.findViewById(R.id.toolbar);
        toolbar.setTitle("Bookworm");

        fm = getActivity().getSupportFragmentManager();

        searchView = layout.findViewById(R.id.search_icon);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            private SearchResult searchView;

            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle b = new Bundle();
                b.putString("query", query);

                searchView = new SearchResult();
                searchView.setArguments(b);
                fm.beginTransaction().add(R.id.explorer_container, searchView).commit();

                searchView.addOnBackPressedListener(new SearchResult.OnBackPressed() {
                    @Override
                    public void backPressed() {
                        fm.beginTransaction().remove(searchView).commit();
                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //set arguments
        Bundle arguments = getArguments();
        playerControls = (Player.PlayerControls) arguments.getSerializable("PlayerControls");

        return layout;
    }
}
