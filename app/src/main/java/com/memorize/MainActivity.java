package com.memorize;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import com.memorize.Database.DatabaseHelper;
import com.memorize.Database.RememberWordsAdapter;
import com.memorize.Model.RememberWord;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ShakeEventManager.ShakeListener {
    private static final String TAG = "MainActivity";
    RememberWordsAdapter rememberWordsAdapter;
    private ShakeEventManager shakeEventManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rememberWordsAdapter = new RememberWordsAdapter(getApplicationContext());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        shakeEventManager = new ShakeEventManager();
        shakeEventManager.setListener(this);
        shakeEventManager.init(this);

        Fragment MainFragment = new MainFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction= fm.beginTransaction();
        fragmentTransaction.replace(R.id.container, MainFragment);
        fragmentTransaction.commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.search) {
            getFragmentManager().popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Fragment MainFragment = new MainFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction= fm.beginTransaction();
            fragmentTransaction.replace(R.id.container, MainFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.add) {
            Intent intent = new Intent(MainActivity.this, WordAddActivity.class);
            startActivity(intent);

        } else if (id == R.id.rememberWord){

            getFragmentManager().popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Fragment rememberWordFragment = new RememberWordFragment();
            FragmentManager fm = getFragmentManager();
            fm.popBackStack("test", 0);
            FragmentTransaction fragmentTransaction= fm.beginTransaction();
            fragmentTransaction.replace(R.id.container, rememberWordFragment);
            fragmentTransaction.addToBackStack("Text");
            fragmentTransaction.commit();
        } else if (id == R.id.settings) {
            getFragmentManager().popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Fragment settingsFragment = new SettingsFragment();
            FragmentManager fm = getFragmentManager();
            fm.popBackStack("bla", 0);
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.container, settingsFragment);
            fragmentTransaction.addToBackStack("Text");
            fragmentTransaction.commit();

        } else if (id == R.id.github) {
            Intent intent = new Intent(MainActivity.this, WebActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
            Log.d(TAG,"Back pressed");
        }
        else{
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.exit)
                    .setTitle("Толь бичиг")
                    .setMessage("Та програмаас гарах уу?")
                    .setPositiveButton("Тийм", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("Үгүй", null)
                    .show();
        }

    }
    @Override
    public void onShake() {
        Log.d(TAG,"Shake shake shake shake shake");
        List<String> remember = new ArrayList<String>();
        //qList.add("");
        List<RememberWord> rememberWords = rememberWordsAdapter.getAllRememberWords();
        for (RememberWord rememberWord : rememberWords){
            String listWord = "\n"+rememberWord.getRememberEnglish() +
                    " - "+rememberWord.getRememberType()+
                    " "+rememberWord.getRememberMongolia()+"";
            remember.add(listWord);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Цээжлэх үгийн жагсаалт");
        builder.setIcon(R.drawable.task);
        builder.setMessage(remember.toString());
        builder.setPositiveButton("Хаах", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onResume();
                // Do something
            }
        });
        builder.show();
        onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        shakeEventManager.register();
    }
    @Override
    protected void onPause() {
        super.onPause();
        shakeEventManager.deregister();
    }
}
