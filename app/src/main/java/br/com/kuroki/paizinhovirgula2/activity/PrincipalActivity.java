package br.com.kuroki.paizinhovirgula2.activity;

import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.List;

import br.com.kuroki.paizinhovirgula2.R;
import br.com.kuroki.paizinhovirgula2.fragment.BlogFragment;
import br.com.kuroki.paizinhovirgula2.fragment.EventoFragment;
import br.com.kuroki.paizinhovirgula2.fragment.PodcastFragment;
import br.com.kuroki.paizinhovirgula2.fragment.SinucaFragment;
import br.com.kuroki.paizinhovirgula2.fragment.VideoFragment;

public class PrincipalActivity extends AppCompatActivity {

    private static final String PRINCIPAL_TAG = "### PAIZINHO";

    private BottomNavigationView bottomNavigationView;

    private Fragment currentFragment;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OneSignal.startInit(this).init();

        setContentView(R.layout.activity_principal);

        if (fragmentList.size() != 5) {
            fragmentList.clear();
            fragmentList.add(new BlogFragment()); //0
            fragmentList.add(new PodcastFragment()); //1
            fragmentList.add(new SinucaFragment()); //2
            fragmentList.add(new VideoFragment()); //3
            fragmentList.add(new EventoFragment()); //4
        }

        fragmentManager = this.getSupportFragmentManager();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                transaction = fragmentManager.beginTransaction();

                Log.i(PRINCIPAL_TAG, "Cliquei no item: " + item.getTitle());
                switch (item.getItemId()) {
                    case R.id.fragment_blog:
                        //currentFragment = new BlogFragment();
                        currentFragment = fragmentList.get(0);
                        break;
                    case R.id.fragment_podcast:
                        //currentFragment = new PodcastFragment();
                        currentFragment = fragmentList.get(1);
                        break;
                    case R.id.fragment_video:
                        //currentFragment = new VideoFragment();
                        currentFragment = fragmentList.get(3);
                        break;
                    case R.id.fragment_evento:
                        //currentFragment = new EventoFragment();
                        currentFragment = fragmentList.get(4);
                        break;
                    case R.id.fragment_sinuca:
                        //currentFragment = new SinucaFragment();
                        currentFragment = fragmentList.get(2);
                        break;
                }
                transaction.replace(R.id.main_container, currentFragment).commit();
                return true;
            }
        });

        //TODO: 11/03/17 retirar quando implementar os outros item desse menu
        //bottomNavigationView.getMenu().getItem(1).setChecked(true);

        bottomNavigationView.getMenu().getItem(0).setEnabled(true);
        bottomNavigationView.getMenu().getItem(1).setEnabled(true);
        bottomNavigationView.getMenu().getItem(2).setEnabled(true);
        bottomNavigationView.getMenu().getItem(3).setEnabled(false);
        bottomNavigationView.getMenu().getItem(4).setEnabled(true);

        if (currentFragment == null)
            currentFragment = fragmentList.get(0);

        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.main_container, currentFragment).commit();
    }

}