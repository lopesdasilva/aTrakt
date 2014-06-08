package com.lopesdasilva.trakt.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lopesdasilva.trakt.R;

/**
 * Created by NB20308 on 08-04-2014.
 */
public class RatingAdvance extends DialogFragment {

    private String currentRating;
    private int position=-1;
    private SeekBar ratingBar;
    private TextView txtRatingValue;
    private OnRatingComplete listener;

    public RatingAdvance(){
    }


    public RatingAdvance(OnRatingComplete listener, String ratingAdvanced, int position) {
        this.listener=listener;
        this.currentRating=ratingAdvanced;
        this.position=position;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.rate_advance, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                            listener.onRatingComplete(Integer.parseInt(txtRatingValue.getText().toString()),position);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RatingAdvance.this.getDialog().cancel();
                    }
                });


        ratingBar = (SeekBar) view.findViewById(R.id.ratingBar);
        ratingBar.setProgress(Integer.parseInt(currentRating));
        txtRatingValue = (TextView) view.findViewById(R.id.textViewRatingBar);
        txtRatingValue.setText(currentRating);
        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txtRatingValue.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return builder.create();
    }

    public interface OnRatingComplete{
        void onRatingComplete(int rating, int position);
    }


}
