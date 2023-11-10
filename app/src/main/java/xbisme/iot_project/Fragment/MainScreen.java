package xbisme.iot_project.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    TextView hi_text, location_text;
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
        if (user != null) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");
                databaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ReadWriteUserDetail writeUserDetail = snapshot.getValue(ReadWriteUserDetail.class);
                        if(writeUserDetail != null){
                            hi_text.setText("Hi, " + writeUserDetail.getName());
                            location_text.setText("Location: "+ writeUserDetail.getAddress());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        }
    }
}