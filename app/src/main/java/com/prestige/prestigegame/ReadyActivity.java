package com.prestige.prestigegame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReadyActivity extends AppCompatActivity implements View.OnDragListener, View.OnTouchListener{
    private boolean toPauseMusic = true;
    String msg;
    private android.widget.RelativeLayout.LayoutParams layoutParams;
    int x_cord;
    int y_cord;
    private int charactersSelected = 0;
    private View v;
    long then = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);

        ImageView dpsChar = findViewById(R.id.dpsCharacter);
        dpsChar.setTag(R.drawable.damageidle);
        dpsChar.setOnTouchListener(this);
        ImageView healerChar = findViewById(R.id.healerCharacter);
        healerChar.setTag(R.drawable.healeridle);
        healerChar.setOnTouchListener(this);
        ImageView tankChar = findViewById(R.id.tankCharacter);
        tankChar.setTag(R.drawable.tankidle);
        tankChar.setOnTouchListener(this);
        //Set Drag Event Listeners for defined layouts
        findViewById(R.id.dpsFrame).setOnDragListener(this);
        findViewById(R.id.healerFrame).setOnDragListener(this);
        findViewById(R.id.tankFrame).setOnDragListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
       if(((Long) System.currentTimeMillis() - then) > 500){
           // Create a new ClipData.Item from the ImageView object's tag
           ClipData.Item item = new ClipData.Item(String.valueOf(v.getTag()));
           // Create a new ClipData using the tag as a label, the plain text MIME type, and
           // the already-created item. This will create a new ClipDescription object within the
           // ClipData, and set its MIME type entry to "text/plain"
           String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
           ClipData data = new ClipData(v.getTag().toString(), mimeTypes, item);
           // Instantiates the drag shadow builder.
           View.DragShadowBuilder dragshadow = new View.DragShadowBuilder(v);
           // Starts the drag
           v.startDrag(data        // data to be dragged
                   , dragshadow   // drag shadow builder
                   , v           // local data about the drag and drop operation
                   , 0          // flags (not currently used, set to 0)
           );
           return true;
       }
       return false;
    }

    public boolean onLongClick(View v) {
        // Create a new ClipData.Item from the ImageView object's tag
        ClipData.Item item = new ClipData.Item(String.valueOf(v.getTag()));
        // Create a new ClipData using the tag as a label, the plain text MIME type, and
        // the already-created item. This will create a new ClipDescription object within the
        // ClipData, and set its MIME type entry to "text/plain"
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData data = new ClipData(v.getTag().toString(), mimeTypes, item);
        // Instantiates the drag shadow builder.
        View.DragShadowBuilder dragshadow = new View.DragShadowBuilder(v);
        // Starts the drag
        v.startDrag(data        // data to be dragged
                , dragshadow   // drag shadow builder
                , v           // local data about the drag and drop operation
                , 0          // flags (not currently used, set to 0)
        );
        return true;
    }
    // This is the method that the system calls when it dispatches a drag event to the listener.
    @Override
    public boolean onDrag(View v, DragEvent event) {
        this.v = v;
        // Defines a variable to store the action type for the incoming event
        int action = event.getAction();
        // Handles each of the expected events
        switch (action) {

            case DragEvent.ACTION_DRAG_STARTED:
                return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

            case DragEvent.ACTION_DRAG_ENTERED:
                // Applies a GRAY or any color tint to the View. Return true; the return value is ignored.
                v.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                // Invalidate the view to force a redraw in the new tint
                v.invalidate();
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                // Ignore the event
                return true;

            case DragEvent.ACTION_DRAG_EXITED:

            case DragEvent.ACTION_DRAG_ENDED:
                // Turns off any color tinting
                // Re-sets the color tint to blue. Returns true; the return value is ignored.
                // view.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                //It will clear a color filter .
                v.getBackground().clearColorFilter();
                // Invalidate the view to force a redraw in the new tint
                v.invalidate();
                return true;

            case DragEvent.ACTION_DROP:
                // Gets the item containing the dragged data
                ClipData.Item item = event.getClipData().getItemAt(0);
                // Gets the text data from the item.
                String dragData = item.getText().toString();
                // Turns off any color tints
                v.getBackground().clearColorFilter();
                // Invalidates the view to force a redraw
                v.invalidate();

                View vw = (View) event.getLocalState();
                ViewGroup owner = (ViewGroup) vw.getParent();
                owner.removeView(vw); //remove the dragged view
                //caste the view into LinearLayout as our drag acceptable layout is LinearLayout
                ImageView container = (ImageView) v;
                container.setBackgroundResource(Integer.valueOf(dragData));//Add the dragged view
                charactersSelected++;
                vw.setVisibility(View.VISIBLE);//finally set Visibility to VISIBLE
                // Returns true. DragEvent.getResult() will return true.
                return true;

            default:
                break;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (MainActivity.bgmPlayer != null) {
            if (MainActivity.bgmPlayer.isPlaying() && toPauseMusic) {
                MainActivity.bgmPlayer.pause();
            }
        }
        toPauseMusic = true;
    }

    public void backButtonClick(View view){
        startActivity(new Intent(ReadyActivity.this, MainActivity.class));
        toPauseMusic = true;
    }

    public void startButtonClick(View view){
        if (charactersSelected != 3){
            Toast.makeText(this, "Please select ALL 3 heroes!", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(ReadyActivity.this, GameActivity.class));
            toPauseMusic = true;
        }
    }

    public void resetButtonClick(View view){
        super.recreate();
        toPauseMusic = false;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ReadyActivity.this, MainActivity.class));
        toPauseMusic = true;
    }
}