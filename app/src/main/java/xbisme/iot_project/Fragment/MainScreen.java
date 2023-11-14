package xbisme.iot_project.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import xbisme.iot_project.Data.ReadWriteUserDetail;
import xbisme.iot_project.R;


public class MainScreen extends Fragment {
    private TextView hi_text, location_text, temperature_text, humidity_text, gas_tex;
    private ProgressBar temperature, humidity, gas;
    private ImageView flame;
    private static final String SHARE_PREFS = "sharedPrefs";
    private AppCompatButton logOut_btn;
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

        logOut_btn = view.findViewById(R.id.logout_btn);

        logOut_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARE_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name","");
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                Navigation.findNavController(view).navigate(R.id.action_mainScreen_to_loginScreen);
            }
        });

        if (user != null) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");
                databaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ReadWriteUserDetail writeUserDetail = snapshot.getValue(ReadWriteUserDetail.class);
                        if(writeUserDetail != null){
                            hi_text.setText("Hi, " + writeUserDetail.getName());
                            location_text.setText("Location: "+ writeUserDetail.getAddress());

                            temperature.setSecondaryProgress((int) writeUserDetail.getTemp());
                            temperature_text.setText(writeUserDetail.getTemp() + "°C");

                            humidity.setProgress((int)(writeUserDetail.getHumidity()));
                            humidity_text.setText(writeUserDetail.getHumidity() + "%");

                            gas.setProgress((int)writeUserDetail.getGas()/10);
                            gas_tex.setText(writeUserDetail.getGas() + "ppm");
                            changeFlameUi(writeUserDetail);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        }
    }
    private void changeTempUi(ReadWriteUserDetail writeUserDetail) {
        if(writeUserDetail != null) {
            double temp = writeUserDetail.getTemp();
            int backgroundColor = ContextCompat.getColor(getContext(), R.color.back_progress);
            int color = R.color.normal;
            if(temp < 40) {
                 color = ContextCompat.getColor(getContext(),R.color.normal);
            }
            else if (40 <= temp && temp < 80) {
                 color = ContextCompat.getColor(getContext(),R.color.warning);
            }
            else if (80 <= temp && temp <= 100) {
                 color = ContextCompat.getColor(getContext(),R.color.dangerous);
            }

            Drawable[] layers = new Drawable[2];
            GradientDrawable backgroundDrawable = new GradientDrawable();
            backgroundDrawable.setShape(GradientDrawable.RING);
            backgroundDrawable.setThicknessRatio(16);
            backgroundDrawable.setUseLevel(false);
            backgroundDrawable.setColor(backgroundColor);
            layers[0] = backgroundDrawable;

            // Lớp thứ hai: Màu sắc tiến trình
            GradientDrawable progressDrawable = new GradientDrawable();
            progressDrawable.setShape(GradientDrawable.RING);
            progressDrawable.setThicknessRatio(16);
            progressDrawable.setUseLevel(true);
            progressDrawable.setColor(color);
            layers[1] = progressDrawable;

            LayerDrawable layerDrawable = new LayerDrawable(layers);

            // Đặt progressDrawable của ProgressBar là LayerDrawable mới
            temperature.setProgressDrawable(layerDrawable);

        }
    }
    private void changeFlameUi(ReadWriteUserDetail writeUserDetail) {
        if (writeUserDetail != null) {
            if(writeUserDetail.getFlame() == 0) {
                if (writeUserDetail.getTemp() >= 40 && writeUserDetail.getTemp() < 80) {
                    flame.setImageResource(R.drawable.warning);
                }
                else if (writeUserDetail.getTemp() >= 80 && writeUserDetail.getTemp() < 100) {
                    flame.setImageResource(R.drawable.fire);
                }
                else flame.setImageResource(R.drawable.no_fire);
            }
            else flame.setImageResource(R.drawable.no_fire);
        }
    }
}