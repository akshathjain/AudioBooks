/*
Name: Akshath Jain
Date: 10/22/17
Purpose: Chapter picker dialog
*/

package com.akshathjain.bookworm.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.akshathjain.bookworm.interfaces.MusicPlayer;

public class ChapterPickerDialog extends DialogFragment {
    private MusicPlayer playerReference;
    private String[] chapterNames;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        playerReference = (MusicPlayer) getArguments().getSerializable("callback");
        chapterNames = getArguments().getStringArray("chapterNames");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick a chapter");
        builder.setItems(chapterNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                playerReference.selectTrack(i);
            }
        });

        return builder.create();
    }
}
