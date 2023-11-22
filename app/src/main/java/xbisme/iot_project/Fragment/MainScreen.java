package xbisme.iot_project.Fragment;

import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xbisme.iot_project.Data.ReadWriteUserDetail;
import xbisme.iot_project.R;


public class MainScreen extends Fragment {
    private TextView hi_text, location_text, temperature_text, humidity_text, gas_tex, human_inside;
    private ProgressBar temperature, humidity, gas;
    private ImageView flame;
    private static final String SHARE_PREFS = "sharedPrefs";

    private SwipeRefreshLayout swipeRefreshLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_screen, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        hi_text = view.findViewById(R.id.hi_text);
        location_text = view.findViewById(R.id.location_text);
        temperature = view.findViewById(R.id.temper_sensor);
        temperature_text = view.findViewById(R.id.temper_text);
        humidity = view.findViewById(R.id.humidity_sensor);
        humidity_text = view.findViewById(R.id.humidity_text);
        gas = view.findViewById(R.id.gas_sensor);
        gas_tex = view.findViewById(R.id.gas_text);
        flame = view.findViewById(R.id.flame_sensor);
        human_inside = view.findViewById(R.id.human_inside);
        swipeRefreshLayout = view.findViewById(R.id.swipe_container);

        swipeToRefresh(user);
        AppCompatButton logOut_btn = view.findViewById(R.id.logout_btn);

        logOut_btn.setOnClickListener(view1 -> {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARE_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name","");
            editor.apply();
            FirebaseAuth.getInstance().signOut();
            Navigation.findNavController(view1).navigate(R.id.action_mainScreen_to_loginScreen);
        });

        if (user != null) {
            updateUI(user);

        }

    }

    private void updateUI(FirebaseUser user) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");
        databaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetail writeUserDetail = snapshot.getValue(ReadWriteUserDetail.class);
                if(writeUserDetail != null){
                    changeUi(writeUserDetail);
                    getFCMToken(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void swipeToRefresh(FirebaseUser user) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateUI(user);
                swipeRefreshLayout.setRefreshing(false);
                sendNotification();

            }

            private void sendNotification() {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");
                databaseReference.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()) {
                            ReadWriteUserDetail readWriteUserDetail = task.getResult().getValue(ReadWriteUserDetail.class);
                            checkWarningFire(databaseReference,readWriteUserDetail);
                        }
                    }
                    private void checkWarningFire(DatabaseReference databaseReference, ReadWriteUserDetail readWriteUserDetail) {

                        databaseReference.addValueEventListener(new ValueEventListener() {
                            int level;
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if((readWriteUserDetail.getGas() == 400
                                        || readWriteUserDetail.getTemp() == 40)) {
                                    level = 1;
                                } else if ((readWriteUserDetail.getTemp() > 40 && readWriteUserDetail.getTemp() < 80)
                                        ||(readWriteUserDetail.getGas() > 400 && readWriteUserDetail.getGas() < 800)) {
                                    level = 2;
                                }
                                else if (readWriteUserDetail.getTemp() >= 80 || readWriteUserDetail.getGas() >= 800) {
                                    level = 3;
                                }
                                getValue2SendNoti(snapshot,level);
                            }
                            private void getValue2SendNoti(DataSnapshot snapshot, int level) {
                                for(DataSnapshot allSnapshot : snapshot.getChildren()) {
                                    try {
                                        ReadWriteUserDetail otherUser = allSnapshot.getValue(ReadWriteUserDetail.class);
                                        if (otherUser != null) {
                                            String message = "Hiện tại nhà " + readWriteUserDetail.getName()
                                                    + " tại địa chỉ: " + readWriteUserDetail.getAddress()
                                                    + " đang bị cháy mức độ " + level + ", bạn hãy chú di tán";
                                            JSONObject jsonObject =new JSONObject();
                                            JSONObject notification =  new JSONObject();
                                            notification.put("title", readWriteUserDetail.getName());
                                            notification.put("body", message);
                                            JSONObject dataObj =  new JSONObject();
                                            dataObj.put("userId", readWriteUserDetail.getName());
                                            jsonObject.put("notification", notification);
                                            jsonObject.put("data", dataObj);
                                            jsonObject.put("to",otherUser.getToken());
                                            callApi(jsonObject);
                                        }
                                    }
                                    catch (Exception e) {
                                        Log.e("Error in MainScreen", e.getCause().toString());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });


            }
            private  void callApi(JSONObject jsonObject) {
                MediaType JSON = MediaType.get("application/json; charset=utf-8");
                OkHttpClient client = new OkHttpClient();
                String url = "https://fcm.googleapis.com/fcm/send";
                RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .header("Authorization", "Bearer  AAAA6zKOl44:APA91bFvb720S0L8JFS7dnKzDjEDD_qI9rnBfOHHzuyA-oqaCHM9rWLckhKwrRb8SyJV2JZSDjZyQhoedf1LJH0trVGESS-ApL_c1gsdD-N_yajcl35Vno8Du16WexKTSOdeld-oRbli")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) {

                    }
                });
            }
        });
    }

    private void changeUi(ReadWriteUserDetail writeUserDetail) {
        String hiText = "Hi, " + writeUserDetail.getName();
        String locationText = "Location: "+ writeUserDetail.getAddress();
        String tempText = writeUserDetail.getTemp() + "°C";
        String humidityText = writeUserDetail.getHumidity() + "%";
        String gasText = writeUserDetail.getGas() + "ppm";
        String humanInside = "People: " + writeUserDetail.getHuman_inside();

        hi_text.setText(hiText);
        location_text.setText(locationText);
        temperature.setSecondaryProgress((int) writeUserDetail.getTemp());
        temperature_text.setText(tempText);
        humidity.setProgress((int)(writeUserDetail.getHumidity()));
        humidity_text.setText(humidityText);
        gas.setProgress((int)writeUserDetail.getGas()/10);
        gas_tex.setText(gasText);
        human_inside.setText(humanInside);

        if (writeUserDetail.getFlame() == 0) {
            if (writeUserDetail.getTemp() >= 40 && writeUserDetail.getTemp() < 80) {
                flame.setImageResource(R.drawable.warning);
            } else if (writeUserDetail.getTemp() >= 80) {
                flame.setImageResource(R.drawable.fire);
            } else flame.setImageResource(R.drawable.no_fire);
        } else flame.setImageResource(R.drawable.no_fire);
    }
    private void getFCMToken(FirebaseUser user){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");
                databaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ReadWriteUserDetail writeUserDetail = snapshot.getValue(ReadWriteUserDetail.class);
                        if(writeUserDetail!=null){
                            writeUserDetail.setToken(token);
                            databaseReference.child(user.getUid()).setValue(writeUserDetail);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Error in MainScreen in Token", error.toString());
                    }
                });
            }
        });
    }

}