package messenger.people.messenger.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import messenger.people.messenger.R;
import messenger.people.messenger.view.fragments.WebViewFragment;
import com.google.android.material.tabs.TabLayout;

import messenger.people.messenger.adapter.TabAdapter;

public class PagerActivity extends AppCompatActivity {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int[] tabIcons = {
            R.drawable.fb,
            R.drawable.message
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new WebViewFragment(1), "Tab 1");
        adapter.addFragment(new WebViewFragment(2), "Tab 2");
//        com.allinonesocialapp.adapter.addFragment(new WebViewFragment(3), "Tab 3");
//        com.allinonesocialapp.adapter.addFragment(new WebViewFragment(4), "Tab 4");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
//        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
//        tabLayout.getTabAt(3).setIcon(tabIcons[3]);

        if(getIntent().getStringExtra("pageId") != null){
            int pageId = Integer.parseInt(getIntent().getStringExtra("pageId"));
            viewPager.setCurrentItem(pageId - 1);
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }
}
