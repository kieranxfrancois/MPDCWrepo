// Kieran Francois, S1835773, MPD CW TRI B 2021/22
package org.me.gcu.kfmpdcws1835773;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;

public class MainActivity extends FragmentActivity implements View.OnClickListener
{
    private Button liveIncidentsButton;
    private Intent IncidentsActivity;
    private Button futureRoadworksButton;
    private Intent futureRoadworksActivity;
    private Button currentRoadworksButton;
    private Intent currentRoadworksActivity;

//display the buttons linked to their activity classes
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        liveIncidentsButton = (Button)findViewById(R.id.liveIncidentsButton);
        liveIncidentsButton.setOnClickListener(this);

        currentRoadworksButton = (Button)findViewById(R.id.currentRoadworksButton);
        currentRoadworksButton.setOnClickListener(this);

        futureRoadworksButton = (Button)findViewById(R.id.plannedRoadworksButton);
        futureRoadworksButton.setOnClickListener(this);
    }

//when buttons are pressed, navigate to the relevant page (trigger relevant activity class)
    public void onClick(View aview)
    {
        if(aview == liveIncidentsButton)
        {
            IncidentsActivity = new Intent(this, liveIncidentsActivity.class);
            startActivity(IncidentsActivity);
        }
        else if(aview == currentRoadworksButton)
        {
            currentRoadworksActivity = new Intent(this, currentRoadworksActivity.class);
            startActivity(currentRoadworksActivity);
        }
        else if(aview == futureRoadworksButton)
        {
            futureRoadworksActivity = new Intent(this, futureRoadworksActivity.class);
            startActivity(futureRoadworksActivity);
        }
    }
}
//MainActivity end