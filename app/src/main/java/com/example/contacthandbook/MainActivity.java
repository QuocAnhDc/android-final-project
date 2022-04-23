package com.example.contacthandbook;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.contacthandbook.firebaseManager.FirebaseCallBack;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.fragment.classes.ClassFragment;
import com.example.contacthandbook.fragment.feedback.FeedbackFragment;
import com.example.contacthandbook.fragment.home.HomeFragment;
import com.example.contacthandbook.fragment.notification.NotificationFragment;
import com.example.contacthandbook.fragment.students.StudentFragment;
import com.example.contacthandbook.fragment.teachers.TeacherFragment;
import com.example.contacthandbook.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;



public class MainActivity  extends AppCompatActivity  {
    private static final String PREFS_NAME = "USER_INFO";
    Toolbar toolbar;
    PrimaryDrawerItem logout = new PrimaryDrawerItem().withName("Logout");
    Drawer drawer;
    MaterialSearchView searchView;

    PrimaryDrawerItem home = new PrimaryDrawerItem().withName("Home");
    PrimaryDrawerItem studentList = new PrimaryDrawerItem().withName("Students");
    PrimaryDrawerItem teacherList = new PrimaryDrawerItem().withName("Teachers");
    PrimaryDrawerItem notificationList = new PrimaryDrawerItem().withName("Notifications");
    PrimaryDrawerItem classList = new PrimaryDrawerItem().withName("Classes");
    PrimaryDrawerItem feedback = new PrimaryDrawerItem().withName("Feedback");
    PrimaryDrawerItem changePassword = new PrimaryDrawerItem().withName("Change Password");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchView = findViewById(R.id.search_view);
        User user = getSavedInfo();
        getSupportActionBar().setTitle("Home");
        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(
                        new ProfileDrawerItem().withName(user.getName()).withEmail(user.getRole())
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {
                        return false;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .build();

        if (user.getRole().equals("Admin")) {
            adminAction(headerResult);
            loadFragment(new NotificationFragment());
        }
        if (user.getRole().equals("Student") || user.getRole().equals("Parents")) {
            studentAction(headerResult);
            loadFragment(new HomeFragment());
        }
        if (user.getRole().equals("Teacher")) {
            teacherAction(headerResult);
            loadFragment(new NotificationFragment());
        }
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
//         // optional
        User user = getSavedInfo();
        if (user.getRole().equals("Student")) {
            Fragment homeFrag = new HomeFragment();
            loadFragment(homeFrag);
        }
        else {
            Fragment notiFrag = new NotificationFragment();
            loadFragment(notiFrag);
        }
        getSupportActionBar().setTitle("Home");
    }

    public User getSavedInfo() {
        User user = new User();
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        user.setUsername(sharedPref.getString("username", "contact"));
        user.setName(sharedPref.getString("name", "Contact Handbook"));
        user.setRole(sharedPref.getString("role", "student"));
        return  user;
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    public void studentAction(AccountHeader header) {
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        home,
                        new DividerDrawerItem(),
                        notificationList,
                        classList,
                        new DividerDrawerItem(),
                        feedback,
                        new DividerDrawerItem(),
                        changePassword,
                        logout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem == home) {
                            Fragment homeFrag = new HomeFragment();
                            loadFragment(homeFrag);
                            getSupportActionBar().setTitle("Home");
                        }
                        if (drawerItem == logout) {
                            logoutAction();
                            return true;
                        }
                        if (drawerItem == notificationList){
                            toolbar.setTitle("Notifications");
                            loadFragment(new NotificationFragment());
                        }
                        if (drawerItem == classList) {
                            toolbar.setTitle("Your Class");
                            loadFragment(new ClassFragment());
                        }
                        if(drawerItem == feedback){
                            toolbar.setTitle("Your feedback");
                            loadFragment(new FeedbackFragment());
                        }
                        if (drawerItem == changePassword) {
                            changePassword();
                        }
                        return false;
                    }
                })
                .build();
    }

    public void adminAction(AccountHeader header) {
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        home,
                        studentList,
                        teacherList,
                        new DividerDrawerItem(),
                        notificationList,
                        classList,
                        new DividerDrawerItem(),
                        feedback,
                        new DividerDrawerItem(),
                        changePassword,
                        logout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem == home) {
                            loadFragment(new NotificationFragment());
                            getSupportActionBar().setTitle("Home");
                        }
                        if (drawerItem == logout) {
                            logoutAction();
                            return true;
                        }
                        if (drawerItem == studentList) {
                            toolbar.setTitle("Student List");
                            loadFragment(new StudentFragment(searchView));
                        }
                        if (drawerItem == teacherList){
                            toolbar.setTitle("Teacher List");
                            loadFragment(new TeacherFragment(searchView));
                        }
                        if (drawerItem == notificationList){
                            toolbar.setTitle("Notifications");
                            loadFragment(new NotificationFragment());
                        }

                        if (drawerItem == classList) {
                            toolbar.setTitle("Class");
                            loadFragment(new ClassFragment());
                        }
                        if(drawerItem == feedback){
                            toolbar.setTitle("Feedback");
                            loadFragment(new FeedbackFragment());
                        }
                        if (drawerItem == changePassword) {
                            changePassword();
                        }
                        return false;
                    }
                })
                .build();
    }

    public void teacherAction(AccountHeader header) {
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        home,
                        new DividerDrawerItem(),
                        notificationList,
                        classList,
                        new DividerDrawerItem(),
                        feedback,
                        new DividerDrawerItem(),
                        changePassword,
                        logout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem == home) {
                            loadFragment(new NotificationFragment());
                            getSupportActionBar().setTitle("Home");
                        }
                        if (drawerItem == logout) {
                            logoutAction();
                            return true;
                        }

                        if (drawerItem == notificationList){
                            toolbar.setTitle("Notifications");
                            loadFragment(new NotificationFragment());
                        }
                        if (drawerItem == classList) {
                            toolbar.setTitle("Your Class");
                            loadFragment(new ClassFragment());
                        }
                        if(drawerItem == feedback){
                            toolbar.setTitle("Feedback");
                            loadFragment(new FeedbackFragment());
                        }
                        if (drawerItem == changePassword) {
                            changePassword();
                        }
                        return false;
                    }
                })
                .build();
    }


    void changePassword() {
        User user = getSavedInfo();
        FirebaseManager firebaseManager = new FirebaseManager(MainActivity.this);
        View dialogLayout = LayoutInflater.from(MainActivity.this).inflate(R.layout.password_dialog, null);
        TextInputEditText currentPassword= dialogLayout.findViewById(R.id.currentPassword_text);
        TextInputEditText newPassword = dialogLayout.findViewById(R.id.newPassword_text);
        TextInputEditText rePassword = dialogLayout.findViewById(R.id.rePassword_text);
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(MainActivity.this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setHeaderView(dialogLayout)
                .addButton("Confirm", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    if (!currentPassword.getText().toString().equals("") && !newPassword.getText().toString().equals("") && !rePassword.getText().toString().equals("") && newPassword.getText().toString().equals(rePassword.getText().toString())) {
                        firebaseManager.changePassword(user.getUsername(), currentPassword.getText().toString(), newPassword.getText().toString(), new FirebaseCallBack.SuccessCallBack() {
                            @Override
                            public void onCallback(boolean success) {
                                dialog.dismiss();
                                if (success) {
                                    CommonFunction.showCommonAlert(MainActivity.this, "Done", "Password changed");
                                }
                                else  {
                                    CommonFunction.showCommonAlert(MainActivity.this, "Error", "Something went wrong");
                                }
                            }
                        });
                    }
                    else {
                        dialog.dismiss();
                        CommonFunction.showCommonAlert(MainActivity.this, "Error", "Your data is missing");
                    }

                });
        builder.show();
    }
    void logoutAction() {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(MainActivity.this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle("Are you sure?")
                .setMessage("Logout and clear your local data?")
                .addButton("YES", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    SharedPreferences settings = (SharedPreferences) getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    settings.edit().clear().commit();
                    dialog.dismiss();
                    finish();
                })
                .addButton("CANCEL", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.show();
    }

}