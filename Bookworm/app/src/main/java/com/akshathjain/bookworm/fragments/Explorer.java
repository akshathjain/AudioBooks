package com.akshathjain.bookworm.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akshathjain.bookworm.R;

import java.io.Serializable;

public class Explorer extends Fragment implements Serializable{
    private Player.PlayerControls playerControls;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_explorer, container, false);

        Toolbar toolbar = layout.findViewById(R.id.toolbar);
        toolbar.setTitle("Bookworm");

        //set arguments
        Bundle arguments = getArguments();
        playerControls = (Player.PlayerControls) arguments.getSerializable("PlayerControls");

        return layout;
    }
}
