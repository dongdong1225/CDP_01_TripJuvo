package com.knucapstone.tripjuvo.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.knucapstone.tripjuvo.R;


public class AboutDialogFragment extends DialogFragment
{
	public static AboutDialogFragment newInstance()
	{
		AboutDialogFragment fragment = new AboutDialogFragment();
		return fragment;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setCancelable(true);
		setRetainInstance(true);
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		// cancelable on touch outside
		if(getDialog()!=null) getDialog().setCanceledOnTouchOutside(true);
	}
	
	
	@Override
	public void onDestroyView()
	{
		// http://code.google.com/p/android/issues/detail?id=17423
		if(getDialog() != null && getRetainInstance()) getDialog().setDismissMessage(null);
		super.onDestroyView();
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder
		.setTitle(R.string.dialog_about_title)
		.setMessage(Html.fromHtml(getResources().getString(R.string.dialog_about_message)))
		.setPositiveButton(android.R.string.ok, null);

		// create dialog from builder
		final AppCompatDialog dialog = builder.create();

		// override positive button
		dialog.setOnShowListener(new DialogInterface.OnShowListener()
		{
			@Override
			public void onShow(DialogInterface dialogInterface)
			{
				((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
			}
		});

		return dialog;
	}
}
