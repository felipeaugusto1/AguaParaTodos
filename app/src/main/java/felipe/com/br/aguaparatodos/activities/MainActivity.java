package felipe.com.br.aguaparatodos.activities;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.utils.PreferenciasUtil;
import felipe.com.br.aguaparatodos.utils.ToastUtil;

import static felipe.com.br.aguaparatodos.R.string.app_name;

/**
 * Created by felipe on 8/23/15.
 */
public class MainActivity extends AppCompatActivity {

    private Drawer navigationDrawer;
    private Toolbar toolbar;

    private static final int ID_MENU_MAPA = 1;
    private static final int ID_MENU_REGISTRAR_OCORRENCIA = 2;
    private static final int ID_MENU_SOBRE = 3;
    private static final int ID_MENU_SAIR = 6;

    private GoogleApiClient mGoogleApiClient;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    private SupportMapFragment mapFragment;
    private GoogleMap mapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa);

        String nomeUsuario = PreferenciasUtil.getPreferenciasUsuarioLogado(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_NOME, getApplicationContext());
        String emailUsuario = PreferenciasUtil.getPreferenciasUsuarioLogado(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_EMAIL, getApplicationContext());
        String fotoUsuario = PreferenciasUtil.getPreferenciasUsuarioLogado(PreferenciasUtil.KEY_PREFERENCIAS_USUARIO_LOGADO_FOTO, getApplicationContext());

        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        // Connected to Google Play services!
                        // The good stuff goes here.
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        // The connection has been interrupted.
                        // Disable any UI components that depend on Google APIs
                        // until onConnected() is called.
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        if (mResolvingError) {
                            // Already attempting to resolve an error.
                            return;
                        } else if (connectionResult.hasResolution()) {
                            mResolvingError = true;
                            //connectionResult.startResolutionForResult(  ((MainActivity) getSupportParentActivityIntent()), REQUEST_RESOLVE_ERROR);
                        } else {
                            // Show dialog using GoogleApiAvailability.getErrorDialog()
                            showErrorDialog(connectionResult.getErrorCode());
                            mResolvingError = true;
                        }
                    }
                })
                .build();

        this.toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        this.toolbar.setTitle(getResources().getString(app_name));
        this.toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(this.toolbar);

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName(getResources().getString(R.string.menu_mapa)).withIcon(GoogleMaterial.Icon.gmd_map).withIdentifier(ID_MENU_MAPA);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withName(getResources().getString(R.string.menu_registrar_ocorrencia)).withIcon(GoogleMaterial.Icon.gmd_new_releases).withIdentifier(ID_MENU_REGISTRAR_OCORRENCIA);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withName(getResources().getString(R.string.menu_sobre)).withIcon(GoogleMaterial.Icon.gmd_info).withIdentifier(ID_MENU_SOBRE);
        SwitchDrawerItem item4 = new SwitchDrawerItem().withName(getResources().getString(R.string.menu_notificacao)).withCheckable(false);
        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withName(getResources().getString(R.string.menu_sair)).withIcon(GoogleMaterial.Icon.gmd_exit_to_app).withIdentifier(ID_MENU_SAIR);

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.agua)
                .addProfiles(
                        new ProfileDrawerItem().withName(nomeUsuario).withEmail(emailUsuario).withIcon(Uri.parse(fotoUsuario))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }

                    ;
                })
                .build();

        //create the drawer and remember the `Drawer` result object
        this.navigationDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(this.toolbar)
                .withSelectedItem(0)
                .withTranslucentStatusBar(true)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        item1,
                        item2,
                        item3,
                        item4,
                        new DividerDrawerItem(),
                        item5
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //Fragment fragment;
                        //FragmentTransaction transaction = getFragmentManager().beginTransaction();

                        if (position == ID_MENU_REGISTRAR_OCORRENCIA) {
                            navigationDrawer.setSelection(1);
                            startActivity(new Intent(MainActivity.this, RegistrarOcorrencia.class));
                        } else if (position == ID_MENU_MAPA) {
                            navigationDrawer.setSelection(0);
                        } else if (position == ID_MENU_SAIR) {
                            LoginManager.getInstance().logOut();
                            Intent intent = new Intent(MainActivity.this, Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                        //transaction.addToBackStack(null);
                        //transaction.commit();
                        navigationDrawer.closeDrawer();
                        return true;
                    }

                    ;
                })
                .build();

        // ---- hamburger icon ----
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        navigationDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        // ----

        this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        configurarMapa();
    }

    private void configurarMapa() {
        if (this.mapa == null) {
            this.mapa = this.mapFragment.getMap();

            if (this.mapa != null) {
                this.mapa.setMyLocationEnabled(true);
            }
        }

        MarkerOptions marker = new MarkerOptions();
        marker.position(new LatLng(-5.751593, -35.238532)).title("Marcador").snippet("Legenda");

        this.mapa.addMarker(marker);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    // The rest of this code is all about building the error dialog
    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MainActivity) getActivity()).onDialogDismissed();
        }
    }
}
