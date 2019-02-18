package com.home.konovaloff.homework;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Диалог с 3-мя кнопками
 * TODO Builder
 * TODO id для многократного использования в одной активности
 */
public class MyDialogFragment extends DialogFragment{
    private static final String EXTRA_CAPTION = "MyDialogFragment.caption";
    private static final String EXTRA_TEXT = "MyDialogFragment.text";
    private static final String EXTRA_BT_POSITIVE = "MyDialogFragment.button.positive";
    private static final String EXTRA_BT_NEGATIVE = "MyDialogFragment.button.negative";

    public static final byte RESULT_YES = 1;
    public static final byte RESULT_NO = 2;
    public static final byte RESULT_CANCEL = 3;

    private String caption;
    private String text;
    private String btPositiveText;
    private String btNegativeText;

    private IDlgResult listener;

    public interface IDlgResult {
        void onDialogResult(byte result);
    }

    public static MyDialogFragment newInstance(String caption, String text, String btPos, String btNeg) {
        Bundle args = new Bundle();
        args.putString(EXTRA_CAPTION, caption);
        args.putString(EXTRA_TEXT, text);
        args.putString(EXTRA_BT_POSITIVE, btPos);
        args.putString(EXTRA_BT_NEGATIVE, btNeg);

        MyDialogFragment fragment = new MyDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener(IDlgResult listener){
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        caption = getArguments().getString(EXTRA_CAPTION);
        text = getArguments().getString(EXTRA_TEXT);
        btPositiveText = getArguments().getString(EXTRA_BT_POSITIVE);
        btNegativeText = getArguments().getString(EXTRA_BT_NEGATIVE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(caption)
                .setPositiveButton(btPositiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null){
                            listener.onDialogResult(RESULT_YES);
                        }
                    }
                })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null){
                            listener.onDialogResult(RESULT_CANCEL);
                        }
                    }
                });

        //Убираем текст, если не нужен
        if (text != null){
            adb.setMessage(text);
        }

        //Проверка нужна чтобы убрать кнопку, если не нужна
        if (btNegativeText != null){
            adb.setNegativeButton(btNegativeText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (listener != null){
                        listener.onDialogResult(RESULT_NO);
                    }
                }
            });

        }
        return adb.create();
    }
}
