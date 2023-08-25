package com.translate.papatap;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;

public class FloatingButtonService extends Service {

    private WindowManager windowManager;
    private Button floatingButton;

    @Override
    public void onCreate() {
        super.onCreate();

        // Window Manager 생성
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // 버튼 레이아웃 및 속성 설정
        floatingButton = new Button(this);
        floatingButton.setText("Floating Button");

        // 버튼을 그리기 위한 레이아웃 파라미터 설정
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        // 버튼을 윈도우에 추가
        windowManager.addView(floatingButton, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 서비스 종료 시 버튼을 제거
        if (floatingButton != null) {
            windowManager.removeView(floatingButton);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}


