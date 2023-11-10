package xbisme.iot_project.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xbisme.iot_project.R;

public class SplashScreen extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_splash_screen, container, false);
        Handler h = new Handler();
        h.postDelayed(() -> Navigation.findNavController(rootView)
                .navigate(R.id.action_splashScreen_to_tutorialScreen),2000);
        return rootView;
    }
}