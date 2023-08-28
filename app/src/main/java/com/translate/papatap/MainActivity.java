package com.translate.papatap;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private boolean isFloatingButtonVisible = false;
    private View floatingButton;
    private float xDelta = 0f;
    private float yDelta = 0f;
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 123; // 원하는 값으로 설정


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatingButton = findViewById(R.id.floatingButton);

        // 초기에는 플로팅 버튼을 숨겨둡니다.
        floatingButton.setVisibility(View.INVISIBLE);

        findViewById(R.id.startStopButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFloatingButtonVisibility();

                if (isFloatingButtonVisible) {
                    if (!hasOverlayPermission()) {
                        requestOverlayPermission();
                    } else {
                        startFloatingButtonService();
                        floatingButton.setVisibility(View.VISIBLE); // 플로팅 버튼을 보이도록 설정
                    }
                } else {
                    floatingButton.setVisibility(View.INVISIBLE); // 플로팅 버튼을 숨기도록 설정
                }
            }
        });

        floatingButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float x = event.getRawX();
                float y = event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        xDelta = x - view.getTranslationX();
                        yDelta = y - view.getTranslationY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.setTranslationX(x - xDelta);
                        view.setTranslationY(y - yDelta);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(x - xDelta) < 5 && Math.abs(y - yDelta) < 5) {
                            // 플로팅 버튼을 누르면 여기에서 원하는 동작을 수행할 수 있습니다.
                        }
                        break;
                }
                return true;
            }
        });
    }

    private boolean hasOverlayPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this);
    }

    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                startFloatingButtonService();
                floatingButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void toggleFloatingButtonVisibility() {
        isFloatingButtonVisible = !isFloatingButtonVisible;
        float translationY = isFloatingButtonVisible ? 0f : 200f;
        float alpha = isFloatingButtonVisible ? 1f : 0f;
        floatingButton.animate().translationY(translationY).alpha(alpha).start();

        Button startStopButton = findViewById(R.id.startStopButton);
        startStopButton.setText(isFloatingButtonVisible ? "Stop" : "Start");
    }

    private void startFloatingButtonService() {
        Intent serviceIntent = new Intent(this, FloatingButtonService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }
}