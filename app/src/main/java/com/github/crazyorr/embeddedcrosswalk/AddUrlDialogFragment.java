package com.github.crazyorr.embeddedcrosswalk;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AddUrlDialogFragment extends DialogFragment {

    private OnAddUrlItemListener onAddUrlItemListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddUrlItemListener) {
            onAddUrlItemListener = (OnAddUrlItemListener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_add_url, null);
        final EditText etUrl = (EditText) view.findViewById(R.id.et_url);
        final EditText etDescription = (EditText) view.findViewById(R.id.et_description);
        return new AlertDialog.Builder(context)
                .setTitle(R.string.add_url)
                .setView(view)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (onAddUrlItemListener != null) {
                                    String url = etUrl.getText().toString();
                                    if (!TextUtils.isEmpty(url)) {
                                        onAddUrlItemListener.onAddUrlItem(
                                                url,
                                                etDescription.getText().toString());
                                    }
                                }
                            }
                        }
                )
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    interface OnAddUrlItemListener {
        void onAddUrlItem(String url, String description);
    }
}
