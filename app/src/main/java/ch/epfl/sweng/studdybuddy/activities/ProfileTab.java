package ch.epfl.sweng.studdybuddy.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.studdybuddy.Consumer;
import ch.epfl.sweng.studdybuddy.Course;
import ch.epfl.sweng.studdybuddy.CourseAdapter;
import ch.epfl.sweng.studdybuddy.FirebaseReference;
import ch.epfl.sweng.studdybuddy.Group;
import ch.epfl.sweng.studdybuddy.GroupsRecyclerAdapter;
import ch.epfl.sweng.studdybuddy.Metabase;
import ch.epfl.sweng.studdybuddy.Pair;
import ch.epfl.sweng.studdybuddy.R;
import ch.epfl.sweng.studdybuddy.RecyclerAdapterAdapter;
import ch.epfl.sweng.studdybuddy.ReferenceWrapper;
import ch.epfl.sweng.studdybuddy.StudyBuddy;
import ch.epfl.sweng.studdybuddy.User;

public class
ProfileTab extends AppCompatActivity {

    private final List<Course> userCourses =  new ArrayList<>();
    private  final List<Group> userGroups = new ArrayList<>();
    private RecyclerView recyclerView_groups;
    private RecyclerView recyclerView_courses;
    protected ReferenceWrapper firebase;
    private GroupsRecyclerAdapter ad;
    private CourseAdapter adCourse;
    private User user;
    private String userID;
    private Metabase metabase;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_tab);

       //
        firebase = getDB();
        user = ((StudyBuddy) ProfileTab.this.getApplication()).getAuthendifiedUser();
        userID = user.getUserID().toString();
        metabase = new Metabase();
        //usersCourses.addAll(.getCoursesPreset());
        setUI();
        setCoursesUp();
        setGroupsUp();
    }
    private void removeCourse(String course){
        userCourses.remove(course);
    }

    private void setGroupsUp() {
        metabase.addListenner(new RecyclerAdapterAdapter(ad));
        metabase.getUserGroups(userID, userGroups);
    }

    private void setCoursesUp() {
        //metabase.getUserCourses(userID, userCourses);
    }

    private void setUI(){
        TextView nameView = (TextView) findViewById(R.id.profile_name_text);
        nameView.setText(user.getName());

        adCourse = new CourseAdapter(userCourses);
        recyclerView_courses = (RecyclerView) findViewById(R.id.courses_list);
        recyclerView_courses.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_courses.setAdapter(adCourse);

        ad = new GroupsRecyclerAdapter(userGroups);
        recyclerView_groups = (RecyclerView) findViewById(R.id.groups_list);
        recyclerView_groups.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_groups.setAdapter(ad);
    }

    public ReferenceWrapper getDB(){
        return new FirebaseReference();
    }

    public void setDB(ReferenceWrapper r){ this.firebase = r; }

}
