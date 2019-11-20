package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;
import java.lang.String;
import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;
import java.util.UUID;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    private ViewGroup mainLayout;
    private Button button;
    private  int xDelta;
    private  int yDelta;
    Button i1;
    TextView t1;
    private Button cs;
    private boolean isVisible=false;
    private boolean isPause=false;
    private Vector v;


    String address=null , name= null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    Set<BluetoothDevice> pairedDevices;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cs=findViewById(R.id.control);
        button= (Button) findViewById(R.id.control);


        button= (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            openActivity2();
            }
        });


        final ImageView  up=findViewById(R.id.up);
        final ImageView  dn=findViewById(R.id.down);
        final ImageView lf=findViewById(R.id.left);
        final ImageView rt=findViewById(R.id.right);
        final ImageView md=findViewById(R.id.resume);

        try {setw();} catch (Exception e){}

        View view;
        view=this.getWindow().getDecorView();
        //view.setBackgroundResource(R.color.brown);

        ImageView chees=findViewById(R.id.chess);
        final ImageView robot=findViewById(R.id.robo);


        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    robot.setTranslationY(robot.getTranslationY()-70);

            }
        });

        dn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.setTranslationY(robot.getTranslationY()+70);
            }
        });
        lf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.setTranslationX(robot.getTranslationX()-55);
            }
        });
        rt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.setTranslationX(robot.getTranslationX()+55);
            }
        });




        cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isVisible){
                    up.setVisibility(View.INVISIBLE);
                    dn.setVisibility(View.INVISIBLE);
                    rt.setVisibility(View.INVISIBLE);
                    lf.setVisibility(View.INVISIBLE);
                    md.setVisibility(View.INVISIBLE);
                    isVisible=false;
                }else {
                    up.setVisibility(View.VISIBLE);
                    dn.setVisibility(View.VISIBLE);
                    rt.setVisibility(View.VISIBLE);
                    lf.setVisibility(View.VISIBLE);
                    md.setVisibility(View.VISIBLE);
                    isVisible=true;
                }
            }
        });



        robot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int x=(int) motionEvent.getRawX();
                final int y=(int) motionEvent.getRawY();
                Log.d("Location",x+" , "+y);

                switch (motionEvent.getAction()&MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams=(RelativeLayout.LayoutParams)view.getLayoutParams();
                        xDelta=x-lParams.leftMargin;
                        yDelta=y-lParams.topMargin;
                        break;
                    case MotionEvent.ACTION_UP:
                        Toast.makeText(MainActivity.this,"I'm coming",Toast.LENGTH_LONG).show();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)view.getLayoutParams();
                        layoutParams.leftMargin=x-xDelta;
                        layoutParams.rightMargin=0;
                        layoutParams.topMargin=y-yDelta;
                        layoutParams.bottomMargin=0;
                        view.setLayoutParams(layoutParams);
                        break;

                }

                return true;
            }
        });
        chees.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("distance",motionEvent.getRawX()+" , "+motionEvent.getRawY());
                return false;
            }
        });


    }

    private void setw() throws IOException
    {
        t1=(TextView)findViewById(R.id.textView1);
        bluetooth_connect_device();
        i1=(Button)findViewById(R.id.control);

        i1.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event){
                if (event.getAction()== MotionEvent.ACTION_DOWN) {led_on_off("f");}
                if (event.getAction()== MotionEvent.ACTION_UP) {led_on_off("b");}
                return true;}
        });



    }

    private void bluetooth_connect_device() throws IOException
    {
        try
        {
            myBluetooth=BluetoothAdapter.getDefaultAdapter();
            address=myBluetooth.getAddress();
            pairedDevices= myBluetooth.getBondedDevices();
            if(pairedDevices.size()>0)
            {
                for(BluetoothDevice bt: pairedDevices)
                {
                    address=bt.getAddress().toString(); name=bt.getName().toString();
                    Toast.makeText(getApplicationContext(),"connected",Toast.LENGTH_LONG).show();
                }
            }
        }
        catch (Exception we) {}
        myBluetooth=BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
        BluetoothDevice dispositivo=myBluetooth.getRemoteDevice(address);//connects to the divice address and check if its available
        btSocket= dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
        btSocket.connect();
        try{t1.setText("BT Name :"+name+"\nBT Address :"+address);}
        catch (Exception e){}
    }


    private void led_on_off(String i)
    {
        try
        {
            if(btSocket!=null)
            {
                btSocket.getOutputStream().write(i.toString().getBytes());
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void openActivity2(){
        Intent intent=new Intent(this, Activity2.class);
        startActivity(intent);
    }



}
