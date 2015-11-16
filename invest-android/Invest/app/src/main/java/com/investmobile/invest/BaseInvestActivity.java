package com.investmobile.invest;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class BaseInvestActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView drawerListView;
    private CharSequence drawerTitle;
    private CharSequence Title;
    private final String[] titles = new String[]{"Home", "Search", "Settings"};
    private final static String APP_AUTH_SHARED_PREFS = "auth_preferences";
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        baseTest();

        setContentView(R.id.ma);
        Title = drawerTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(getNavigationConfiguration().getDrawerLayoutID());
        drawerListView = (ListView) findViewById(getNavigationConfiguration().getLeftDrawerID());
        drawerListView.setAdapter(new DrawerListviewCustomAdapter());
        drawerListView.setOnItemClickListener(new DrawerOnItemClickListner());
        this.initDrawerShadow();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        drawerToggle = new MyActionBarDrawerToggle(this, drawerLayout, getNavigationConfiguration().getDrawerOpenDesc(), getNavigationConfiguration().getDrawerCloseDesc());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_table_query, menu);

        // Setting up search bar
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconified(false);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchRequested();
            }
        });
        return true;
    }

    private void initDrawerShadow() {
        drawerLayout.setDrawerShadow(getNavigationConfiguration().getDrawerShadow(), GravityCompat.START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        baseTest();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        baseTest();
    }

    public void baseTest() {
        SharedPreferences sharedPrefs = context.getApplicationContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
        boolean isUserLoggedIn = sharedPrefs.getBoolean("userLoggedInState", false);
        boolean skippedLogin = sharedPrefs.getBoolean("userSkippedLoggedInState", false);
        if (!isUserLoggedIn && !skippedLogin) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
    }


    public boolean isActiveUser() {
        SharedPreferences sharedPrefs = context.getApplicationContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
        boolean isUserLoggedIn = sharedPrefs.getBoolean("userLoggedInState", false);
        return isUserLoggedIn;
    }

    public String getToken() {
        SharedPreferences sharedPrefs = context.getApplicationContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPrefs.getString("token", null);
    }


    public void logout() {
        SharedPreferences sharedPrefs = context.getApplicationContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
        sharedPrefs.edit().putBoolean("userSkippedLoggedInState", false).apply();
        sharedPrefs.edit().putBoolean("userLoggedInState", false).apply();
        sharedPrefs.edit().putString("token", null).apply();
    }

    public NavigationConfiguration getNavigationConfiguration() {
        return new NavigationConfiguration() {
            @Override
            int getMainLayout() {
                return 0;
            }

            @Override
            int getDrawerLayoutID() {
                return 0;
            }

            @Override
            int getLeftDrawerID() {
                return 0;
            }

            @Override
            int getDrawerShadow() {
                return 0;
            }

            @Override
            int getDrawerOpenDesc() {
                return 0;
            }

            @Override
            int getDrawerCloseDesc() {
                return 0;
            }
        }
    }


    public int getMainLayout() {

    }

    public int getDrawerLayoutID() {

    }

    public int getLeftDrawerID() {

    }

    public int getDrawerShadow() {

    }

    public int getDrawerOpenDesc() {

    }

    public int getDrawerCloseDesc() {

    }


    private class DrawerListviewCustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

    private class DrawerOnItemClickListner implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void selectItem(int position) {
        this.onNavItemSelected(position);
        drawerListView.setItemChecked(position, true);

//        if (selectedItem.updateActionBarTitle()) {
//            setTitle(drawerLayout.getLabel());
//        }

        if (this.drawerLayout.isDrawerOpen(this.drawerLayout)) {
            drawerLayout.closeDrawer(drawerLayout);
        }
    }

    protected void onNavItemSelected(int id) {
        switch ((int) id) {
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SearchResultsFragment()).commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
        }
    }

    private class MyActionBarDrawerToggle extends ActionBarDrawerToggle {

        public MyActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
        }
    }

}
