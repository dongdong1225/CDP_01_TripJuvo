package com.knucapstone.tripjuvo.util;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.knucapstone.tripjuvo.activity.DialogUniversalInfoActivity;
import com.knucapstone.tripjuvo.R;

public class DialogUniversalInfoUtils {

	private DialogUniversalInfoActivity mDialogUniversalInfoActivity;
	private Dialog mDialog;

	private TextView mDialogHeader;
	private TextView mDialogText;
	public TextView mDialogOKButton;
	public TextView mDialogCANCELButton;
	private ImageView mDialogImage;

	public DialogUniversalInfoUtils(DialogUniversalInfoActivity mDialogUniversalActivity) {
		this.mDialogUniversalInfoActivity = mDialogUniversalActivity;
	}

	public void showDialog() {
		if (mDialog == null) {
			mDialog = new Dialog(mDialogUniversalInfoActivity, R.style.CustomDialogTheme);
		}
		mDialog.setContentView(R.layout.dialog_universal_info);
		mDialog.setCancelable(true);
		mDialog.show();

		mDialogHeader = (TextView) mDialog.findViewById(R.id.dialog_universal_info_title);
		mDialogText = (TextView) mDialog.findViewById(R.id.dialog_universal_info_text);
		mDialogOKButton = (TextView) mDialog.findViewById(R.id.dialog_universal_info_ok);
		mDialogCANCELButton = (TextView) mDialog.findViewById(R.id.dialog_universal_info_cancel);
		mDialogImage = (ImageView) mDialog.findViewById(R.id.dialog_universal_info_image);
		
		initDialogButtons();
	}

	private void initDialogButtons() {

		mDialogOKButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				mDialog.dismiss();
			}
		});
	}

	public void dismissDialog() {
		mDialog.dismiss();
	}
}
