package ch.epfl.sweng.studdybuddy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.studdybuddy.activities.group.GlobalBundle;
import ch.epfl.sweng.studdybuddy.core.Group;
import ch.epfl.sweng.studdybuddy.core.ID;
import ch.epfl.sweng.studdybuddy.firebase.MetaMeeting;
import ch.epfl.sweng.studdybuddy.services.meeting.Meeting;
import ch.epfl.sweng.studdybuddy.tools.AdapterAdapter;
import ch.epfl.sweng.studdybuddy.tools.Consumer;
import ch.epfl.sweng.studdybuddy.tools.Intentable;
import ch.epfl.sweng.studdybuddy.util.ActivityHelper;
import ch.epfl.sweng.studdybuddy.util.MapsHelper;
import ch.epfl.sweng.studdybuddy.util.Messages;

import static android.app.Activity.RESULT_OK;
import static ch.epfl.sweng.studdybuddy.util.ActivityHelper.getConsumerForMeetings;
import static ch.epfl.sweng.studdybuddy.util.ActivityHelper.listenDate;
import static ch.epfl.sweng.studdybuddy.util.ActivityHelper.listenTime;
import static ch.epfl.sweng.studdybuddy.util.ActivityHelper.onClickLaunch;
import static ch.epfl.sweng.studdybuddy.util.CoreFactory.blankGroupWId;
import static ch.epfl.sweng.studdybuddy.util.CoreFactory.randomMeeting;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

//import static ch.epfl.sweng.studdybuddy.util.ActivityHelper.listenDate;
//import static ch.epfl.sweng.studdybuddy.util.ActivityHelper.listenTime;
//import static ch.epfl.sweng.studdybuddy.util.ActivityHelper.meetingConsumer;

public class ActivityHelperTest {
  @Test
  public void EmptyTest(){}
    Meeting mee = randomMeeting();
    Group group = blankGroupWId("?");
    MetaMeeting mm = mock(MetaMeeting.class);
    AdapterAdapter adapter = mock(AdapterAdapter.class);
    DatePicker view = mock(DatePicker.class);
    TimePicker time = mock(TimePicker.class);
    TextView title = mock(TextView.class);
    Button timeB = mock(Button.class);
    //Button date = mock(Button.class);
    Button plus = mock(Button.class);
    Date date;
    List<Meeting> meetingsList = new ArrayList<>();

    RecyclerView.Adapter rvAdapter;

    @Before
    public void setUp(){
        rvAdapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        };
        Calendar c = Calendar.getInstance();
        c.set(2018, 10, 25, 0, 0);
        date = c.getTime();
    }

    @Test
    public void testListenDate() {
        listenDate(title, date, date, adapter).onDateSet(view, 2018, 10, 5);
        verify(title, times(1)).setText("11/5/2018");
        verify(adapter, times(1)).update();
        //verify(mee).getDeadline().setMonth(11);
    }

    @Test
    public void testListenTime() {
        listenTime(title, date, adapter).onTimeSet(time, 0, 0);
        verify(title, times(1)).setText("0:00");
        verify(adapter, times(1)).update();
    }

    @Test
    public void testGetConsumerForMeetingsWithEmptyList(){
        Consumer<List<Meeting>> consumer = getConsumerForMeetings(meetingsList, mm, new ID<>(Messages.TEST), rvAdapter);
        consumer.accept(new ArrayList<>());
        verify(mm, times(0)).deleteMeeting(any(), any());
        //verify(rvAdapter, times(1)).notifyDataSetChanged();
    }


    private void checkerGetConsumerForMeetings(int addToYear, int nTimesMm, int sizeOfList ){
        Date date = setDate(addToYear);
        mee.setStarting(date.getTime());
        List<Meeting> list = new ArrayList<>();
        list.add(mee);
        Consumer<List<Meeting>> consumer = getConsumerForMeetings(meetingsList, mm, new ID<>(Messages.TEST), rvAdapter);
        consumer.accept(list);
        verify(mm, times(nTimesMm)).deleteMeeting(any(), any());
        assertTrue(meetingsList.size() == sizeOfList);
    }

    @Test
    public void testGetConsumerForMeetingsWithNoDeletion(){
        checkerGetConsumerForMeetings(1,0,1);
        assertTrue(ActivityHelper.comparator.compare(meetingsList.get(0), mee) == 0);
    }

    @Test
    public void testGetConsumerForMeetingsWithDeletion(){
        checkerGetConsumerForMeetings(-1, 1, 0);
    }

    private Date setDate(int i){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int year = c.get(Calendar.YEAR);
        c.set(Calendar.YEAR, year + i);
        return c.getTime();
    }

/*    @Test
    public void testNoMeetingConsumer() {
        meetingConsumer(title, timeB, date, plus).accept(new ArrayList<>());
        verify(timeB).setVisibility(View.GONE);
        verify(date).setVisibility(View.GONE);
        verify(title).setVisibility(View.GONE);
    }

    @Test
    public void testMeetingConsumer() {
        meetingConsumer(title, timeB, date, plus).accept(Arrays.asList(mee));
        verify(timeB).setVisibility(View.VISIBLE);
        verify(date).setVisibility(View.VISIBLE);
        verify(title).setVisibility(View.VISIBLE);
        verify(date).setText(mee.date());
        verify(timeB).setText(mee.time());
        verify(plus).setVisibility(View.GONE);
    }

    @Test
    public void testAdminSeesAddButton() {
        adminMeeting(plus, withAdmin("a"), "a");
        verify(plus).setVisibility(View.VISIBLE);
     }

    @Test
    public void testUserCantAdd() {
        adminMeeting(plus, withAdmin("--"), "sjdnn");
        verify(plus).setVisibility(View.GONE);
    }
*/
   @Test
    public void meetingLocationFromBundleNullTest(){
       assertTrue(ActivityHelper.meetingLocationFromBundle(0,0) == null);
   }

   @Test
    public void meetingLocationFromBundleTest() {
       Bundle bundle = GlobalBundle.getInstance().getSavedBundle();
       bundle.putString(Messages.LOCATION_TITLE, MapsHelper.ROLEX_LOCATION.getTitle());
       bundle.putString(Messages.ADDRESS, MapsHelper.ROLEX_LOCATION.getAddress());
       bundle.putDouble(Messages.LATITUDE, MapsHelper.ROLEX_LOCATION.getLatitude());
       bundle.putDouble(Messages.LONGITUDE, MapsHelper.ROLEX_LOCATION.getLongitude());
       GlobalBundle.getInstance().putAll(bundle);
       assertTrue(ActivityHelper.meetingLocationFromBundle(1,RESULT_OK).equals(MapsHelper.ROLEX_LOCATION));

   }
}
