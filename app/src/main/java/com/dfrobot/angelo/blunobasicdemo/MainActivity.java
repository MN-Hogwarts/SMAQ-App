package com.dfrobot.angelo.blunobasicdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity  extends BlunoLibrary {
    private static final String TAG = "MainActivity";
    private Button buttonScan;
	private Button buttonSerialSend;
	private EditText serialSendText;
	private TextView serialReceivedText;

	private TextView mTextViewAngle;
	private TextView mTextViewStrength;
	private TextView mTextViewCoordinate;
	private TextView mTextViewMotors;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        onCreateProcess();														//onCreate Process by BlunoLibrary


        serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200

        serialReceivedText=(TextView) findViewById(R.id.serialReveicedText);	//initial the EditText of the received data
        serialSendText=(EditText) findViewById(R.id.serialSendText);			//initial the EditText of the sending data

        buttonSerialSend = (Button) findViewById(R.id.buttonSerialSend);		//initial the button for sending the data
        buttonSerialSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				serialSend(serialSendText.getText().toString());				//send the data to the BLUNO
			}
		});

        buttonScan = (Button) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device
        buttonScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				buttonScanOnClickProcess();										//Alert Dialog for selecting the BLE device
			}
		});

		mTextViewAngle = (TextView) findViewById(R.id.textView_angle);
		mTextViewStrength = (TextView) findViewById(R.id.textView_strength);
		mTextViewCoordinate = (TextView) findViewById(R.id.textView_coordinate);
		mTextViewMotors = (TextView) findViewById(R.id.textView_motorValues);
		final double[] lmotor = {0};
		final double[] rmotor = {0};

		final JoystickView joystick = (JoystickView) findViewById(R.id.joystickView);
		joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
			@SuppressLint("DefaultLocale")
			@Override
			public void onMove(int angle, int strength) {
				String ang = angle + "°";
				String str = strength + "%";
				mTextViewAngle.setText(ang);
				mTextViewStrength.setText(str);
				mTextViewCoordinate.setText(
						String.format("x%03d:y%03d",
								joystick.getNormalizedX(),
								joystick.getNormalizedY())
				);
				lmotor[0] = Math.cos( (angle / 2) * Math.PI / 180) * Math.sqrt(2) * strength / 100;
				if (lmotor[0] > 1) {
					lmotor[0] = 1;
				}
				else if (lmotor[0] < -1) {
					lmotor[0] = -1;
				}

				rmotor[0] = Math.cos( (((-angle + 540) % 360) / 2) * Math.PI / 180) * Math.sqrt(2) * strength / 100;
				if (rmotor[0] > 1) {
					rmotor[0] = 1;
				}
				else if (rmotor[0] < -1) {
					rmotor[0] = -1;
				}

				String motorVals = String.format("L%03fR%03f",
                        lmotor[0],
                        rmotor[0]);

				mTextViewMotors.setText(motorVals);

				serialSend(motorVals);

                Log.i(TAG, "onMove: " + motorVals);
			}
		});

	}

	protected void onResume(){
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess();														//onResume Process by BlunoLibrary
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();														//onPause Process by BlunoLibrary
    }
	
	protected void onStop() {
		super.onStop();
		onStopProcess();														//onStop Process by BlunoLibrary
	}
    
	@Override
    protected void onDestroy() {
        super.onDestroy();	
        onDestroyProcess();														//onDestroy Process by BlunoLibrary
    }

	@Override
	public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
		switch (theConnectionState) {											//Four connection state
		case isConnected:
			buttonScan.setText("Connected");
			break;
		case isConnecting:
			buttonScan.setText("Connecting");
			break;
		case isToScan:
			buttonScan.setText("Scan");
			break;
		case isScanning:
			buttonScan.setText("Scanning");
			break;
		case isDisconnecting:
			buttonScan.setText("isDisconnecting");
			break;
		default:
			break;
		}
	}

	@Override
	public void onSerialReceived(String theString) {							//Once connection data received, this function will be called
		// TODO Auto-generated method stub
		serialReceivedText.setText(theString);							//append the text into the EditText
        Log.i(TAG, "onSerialReceived: " + theString);
		//The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
		((ScrollView)serialReceivedText.getParent()).fullScroll(View.FOCUS_DOWN);
	}

}