package galilel.org.galilelwallet.ui.address_add_activity;

import android.support.v4.content.ContextCompat;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.galilelj.uri.GalilelURI;

import galilel.org.galilelwallet.R;
import global.AddressLabel;
import global.exceptions.ContactAlreadyExistException;
import galilel.org.galilelwallet.ui.base.BaseActivity;
import galilel.org.galilelwallet.utils.scanner.ScanActivity;

import static android.Manifest.permission_group.CAMERA;
import static galilel.org.galilelwallet.utils.scanner.ScanActivity.INTENT_EXTRA_RESULT;

public class AddContactActivity extends BaseActivity implements View.OnClickListener {

    public static final String ADDRESS_TO_ADD = "address";
    private static final int SCANNER_RESULT = 122;

    private View root;
    private EditText edit_name;
    private EditText edit_address;
    private ImageView imgQr;
    private String address;
    private String name;

    @Override
    protected void onCreateView(Bundle savedInstanceState, ViewGroup container) {
        root = getLayoutInflater().inflate(R.layout.fragment_new_address, container);
        setTitle(getString(R.string.contact_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        edit_name = (EditText) root.findViewById(R.id.edit_name);
        edit_address = (EditText) root.findViewById(R.id.edit_address);
        imgQr = (ImageView) root.findViewById(R.id.img_qr);
        imgQr.setOnClickListener(this);
        edit_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = s.toString();
                if (galilelModule.chechAddress(temp))
                    address = temp;
                edit_address.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey85black));
            }
        });
        Intent intent = getIntent();
        if (intent!=null){
            if (intent.hasExtra(ADDRESS_TO_ADD)){
                edit_address.setText(intent.getStringExtra(ADDRESS_TO_ADD));
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                name = edit_name.getText().toString();
                if (address!=null) {
                    if (name.length() > 0 && address.length() > 0) {
                        try {
                            if (!galilelModule.chechAddress(address)) {
                                Toast.makeText(this, getString(R.string.invalid_input_address), Toast.LENGTH_LONG).show();
                                return true;
                            }
                            AddressLabel addressLabel = new AddressLabel(name);
                            addressLabel.addAddress(address);
                            galilelModule.saveContact(addressLabel);
                            Toast.makeText(this, getString(R.string.contact_saved), Toast.LENGTH_LONG).show();
                            onBackPressed();
                        } catch (ContactAlreadyExistException e) {
                            Toast.makeText(this, getString(R.string.contact_already_exist), Toast.LENGTH_LONG).show();
                        }
                    }
                }else {
                    Toast.makeText(this,getString(R.string.invalid_input_address),Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    protected void onNavigationBackPressed() {
        // save contact

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.img_qr){
            if (!checkPermission(CAMERA)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permsRequestCode = 200;
                    String[] perms = {"android.permission.CAMERA"};
                    requestPermissions(perms, permsRequestCode);
                }
            }
            startActivityForResult(new Intent(this, ScanActivity.class),SCANNER_RESULT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCANNER_RESULT){
            if (resultCode==RESULT_OK) {
                try {
                    String address = data.getStringExtra(INTENT_EXTRA_RESULT);
                    String usedAddress;
                    if (galilelModule.chechAddress(address)){
                        usedAddress = address;
                    }else {
                        GalilelURI galilelUri = new GalilelURI(address);
                        usedAddress = galilelUri.getAddress().toBase58();
                    }
                    final String tempPubKey = usedAddress;
                    edit_address.setText(tempPubKey);
                }catch (Exception e){
                    Toast.makeText(this,getString(R.string.bad_address),Toast.LENGTH_LONG).show();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
