package com.akshathjain.bookworm.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akshathjain.bookworm.R;
import com.akshathjain.bookworm.activities.SearchResult;

import java.io.Serializable;

public class Explorer extends Fragment implements Serializable{
    private Player.PlayerControls playerControls;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_explorer, container, false);

        Toolbar toolbar = layout.findViewById(R.id.toolbar);
        toolbar.setTitle("Bookworm");

        searchView = layout.findViewById(R.id.search_icon);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getActivity(), SearchResult.class);
                intent.putExtra("query", query);
                startActivity(intent);

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
