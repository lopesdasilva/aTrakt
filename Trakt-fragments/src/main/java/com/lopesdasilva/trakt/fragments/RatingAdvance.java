package com.lopesdasilva.trakt.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lopesdasilva.trakt.R;

/**
 * Created by NB20308 on 08-04-2014.
 */
public class RatingAdvance extends DialogFragment {

    private RatingBar ratingBar;
    private TextView txtRatingValue;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.rate_advance, null))
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RatingAdvance.this.getDialog().cancel();
                    }
                });


        ratingBar = (RatingBar) inflater.inflate(R.layout.rate_advance, null).findViewById(R.id.ratingBar);
        txtRatingValue = (TextView) inflater.inflate(R.layout.rate_advance, null).findViewById(R.id.textViewRatingBar);

        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                txtRatingValue.setText(String.valueOf(rating));

            }
        });

        return builder.create();
    }
}
