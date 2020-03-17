package comi.example.modern.modernvideopopup;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

public class ScreenOrientationSwitcher 
{
	private final View view;
    private final WindowManager windows;
    private boolean isRunning;

    public ScreenOrientationSwitcher(Context context) 
    {
        windows = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        view = new View(context);

    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void start()
    {
        isRunning =true;
        try {
            WindowManager.LayoutParams layout = generateLayout();
            windows.addView(view, layout);
            view.setVisibility(View.VISIBLE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


	public void stop() 
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (view.isAttachedToWindow()) {
                windows.removeView(view);
            }
        }
        else
        {
            windows.removeView(view);
        }
        isRunning =false;
        // view.setVisibility(View.GONE);
    }



    private WindowManager.LayoutParams generateLayout()
    {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        //So we don't need a permission or activity
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;

        //Just in case the window type somehow doesn't enforce this
        layoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        //Prevents breaking apps that detect overlying windows enabling
        //(eg UBank app, or enabling an accessibility service)
        layoutParams.width = 0;
        layoutParams.height = 0;

        //Try to make it completely invisible
        layoutParams.format = PixelFormat.TRANSPARENT;
        layoutParams.alpha = 0f;


        
        //The orientation to force
        layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
     {
         layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
     }*/
        return layoutParams;
    }

}
