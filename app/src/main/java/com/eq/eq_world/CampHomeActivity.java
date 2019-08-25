package com.eq.eq_world;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


import android.app.ProgressDialog;

import android.os.Bundle;


import com.eq.eq_world.Adapter.ViewPagerAdapter;
import com.eq.eq_world.Fragments.CampAnnounceFragment;
import com.eq.eq_world.Fragments.CampMemberFragment;
import com.eq.eq_world.Fragments.CampScheduleFragment;
import com.eq.eq_world.Fragments.CampSettingFragment;
import com.eq.eq_world.Model.CampUser;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CampHomeActivity extends AppCompatActivity {

    public static List<String> mUsers;
    public static List<CampUser> memberList;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camp_home);

        // UI Fragment Config
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter vpa =
                new ViewPagerAdapter(getSupportFragmentManager());
        vpa.addFragments(new CampAnnounceFragment(), "Announcements");
        vpa.addFragments(new CampScheduleFragment(), "Schedule");
        vpa.addFragments(new CampMemberFragment(), "Members");
        vpa.addFragments(new CampSettingFragment(), "Settings");
        viewPager.setAdapter(vpa);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);


        // Loading Bar Config
        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("ถ้าโหลดนานเกินไป โปรดตรวจสอบการเชื่อมต่ออินเตอร์เน็ต");
        loadingBar.setTitle("Staff Home");
        loadingBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingBar.setCancelable(false);
        loadingBar.show();



        mUsers = new ArrayList<>();
        memberList = new ArrayList<>();
        readMemberList("Camp name1");///


    }

    private void readMemberList(final String camp){

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long numOfMem = dataSnapshot.child("Camps").child(camp).child("members").getChildrenCount();
                mUsers.clear();
                while (mUsers.size()!=numOfMem) {
                    for (DataSnapshot snapshot : dataSnapshot.child("Camps")
                            .child(camp).child("members").getChildren()) {
                        String memberID = snapshot.getKey()+"@"+snapshot.getValue(String.class);
                        assert memberID != null;
                        mUsers.add(memberID);
                    }
                }
                memberList.clear();
                for (String memberID : mUsers){
                    String[] uid = memberID.split("@");
                    CampUser member = dataSnapshot.child("Users").child(uid[0]).getValue(CampUser.class);
                    member.setRole(uid[1]);
                    memberList.add(member);
                }
                loadingBar.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }





}