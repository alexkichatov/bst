package ru.redsolution.bst.ui;

import org.apache.http.auth.AuthenticationException;

import ru.redsolution.bst.R;
import ru.redsolution.bst.data.BST;
import ru.redsolution.bst.data.DocumentType;
import ru.redsolution.bst.data.OperationListener;
import ru.redsolution.bst.ui.dialog.AuthorizationDialog;
import ru.redsolution.dialogs.DialogBuilder;
import ru.redsolution.dialogs.DialogListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

/**
 * Главное окно приложения.
 * 
 * @author alexander.ivanov
 * 
 */
public class MainActivity extends PreferenceActivity implements
		OnPreferenceClickListener, OperationListener, DialogListener {

	private static final int DIALOG_AUTH_ID = 1;

	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main);
		findPreference(getString(R.string.continue_action))
				.setOnPreferenceClickListener(this);
		findPreference(getString(R.string.import_action))
				.setOnPreferenceClickListener(this);
		findPreference(getString(R.string.inventory_action))
				.setOnPreferenceClickListener(this);
		findPreference(getString(R.string.settings_action))
				.setOnPreferenceClickListener(this);

		progressDialog = new ProgressDialog(this);
		progressDialog.setIndeterminate(true);
		progressDialog.setTitle(R.string.import_action);
		progressDialog.setMessage(getString(R.string.wait));
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				BST.getInstance().cancelImport();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateView();
		BST.getInstance().setOperationListener(this);
		if (BST.getInstance().isImporting())
			onBegin();
	}

	@Override
	protected void onPause() {
		super.onPause();
		BST.getInstance().setOperationListener(null);
		dismissProgressDialog();
	}

	@Override
	public boolean onPreferenceClick(Preference paramPreference) {
		if (paramPreference.getKey()
				.equals(getString(R.string.continue_action))) {
			startActivity(new Intent(this, DocumentActivity.class));
		} else if (paramPreference.getKey().equals(
				getString(R.string.import_action))) {
			if ("".equals(BST.getInstance().getLogin()))
				showDialog(DIALOG_AUTH_ID);
			else
				BST.getInstance().importData();
		} else if (paramPreference.getKey().equals(
				getString(R.string.inventory_action))) {
			startActivity(new Intent(this, InventoryActivity.class));
		} else if (paramPreference.getKey().equals(
				getString(R.string.settings_action))) {
			startActivity(new Intent(this, SettingsActivity.class));
		}
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_AUTH_ID:
			return new AuthorizationDialog(this, id, this).create();
		default:
			return super.onCreateDialog(id);
		}
	}

	@Override
	public void onAccept(DialogBuilder dialogBuilder) {
		switch (dialogBuilder.getDialogId()) {
		case DIALOG_AUTH_ID:
			BST.getInstance().importData();
			break;
		default:
			break;
		}
		updateView();
	}

	@Override
	public void onDecline(DialogBuilder dialogBuilder) {
	}

	@Override
	public void onCancel(DialogBuilder dialogBuilder) {
	}

	@Override
	public void onBegin() {
		updateView();
		progressDialog.show();
	}

	@Override
	public void onDone() {
		dismissProgressDialog();
	}

	@Override
	public void onCancelled() {
		dismissProgressDialog();
	}

	@Override
	public void onError(RuntimeException exception) {
		dismissProgressDialog();
		if (exception.getCause() instanceof AuthenticationException) {
			showDialog(DIALOG_AUTH_ID);
			Toast.makeText(this, R.string.auth_error, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, R.string.connection_error, Toast.LENGTH_LONG)
					.show();
		}
	}

	private void dismissProgressDialog() {
		updateView();
		progressDialog.dismiss();
	}

	private void updateView() {
		boolean isImported = BST.getInstance().isImported();
		DocumentType documentType = BST.getInstance().getDocumentType();
		findPreference(getString(R.string.continue_action)).setEnabled(
				isImported && documentType != null);
		if (documentType == DocumentType.inventory) {
			findPreference(getString(R.string.continue_action)).setSummary(
					R.string.continue_inventory_summary);
		} else {
			findPreference(getString(R.string.continue_action)).setSummary(
					R.string.continue_summary);
		}
		findPreference(getString(R.string.inventory_action)).setEnabled(
				isImported);
		findPreference(getString(R.string.settings_action)).setEnabled(
				isImported);
	}

}