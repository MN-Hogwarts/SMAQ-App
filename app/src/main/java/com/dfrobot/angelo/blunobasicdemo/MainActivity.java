package com.dfrobot.angelo.blunobasicdemo;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;

public class MainActivity  extends BlunoLibrary {
    private static final String TAG = "MainActivity";
//    private Button buttonScan;
//	private Button buttonSerialSend;
	private TextView connectionText; //ONE
//	private EditText serialSendText;
//	private TextView serialReceivedText;

	private TextView mTextViewAngle;
	private TextView mTextViewStrength;
	private TextView mTextViewCoordinate;
	private TextView mTextViewMotors;

	static final char TURB_CHAR	= 'V';
	static final char TEMP_CHAR	= 'T';
	static final char PH_CHAR	= 'P';
	char currentSensor = '0'; // Starting value for character to determine which sensor value is incoming,
	// Global to keep it the same between different strings being received
	String turbStr	= "";
	String tempStr	= "";
	String pHStr	= "";
	double turb;
	double temp;
	double pH;

	// For graphs
    private LineGraphSeries<DataPoint> s1;
    private LineGraphSeries<DataPoint> s2;
    private LineGraphSeries<DataPoint> s3;
    private static final Random RANDOM = new Random();
    private static int lastX = 0;
    private GraphView tempGraph;
    private GraphView phGraph;
    private GraphView turbGraph;
    public static ArrayList<Double> tempArray;
    public static ArrayList<Double> phArray;
    public static ArrayList<Double> turbArray;
   // public static ArrayList<Integer> timeArray;
   private final DecimalFormat df = new DecimalFormat("0.#");
	private TextView currentTemp;
	private TextView currentTurb;
	private TextView currentpH;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
        onCreateProcess();														//onCreate Process by BlunoLibrary


        serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200

//        serialReceivedText=(TextView) findViewById(R.id.serialReveicedText);	//initial the EditText of the received data
//        serialSendText=(EditText) findViewById(R.id.serialSendText);			//initial the EditText of the sending data
//
//        buttonSerialSend = (Button) findViewById(R.id.buttonSerialSend);		//initial the button for sending the data
//        buttonSerialSend.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//				serialSend(serialSendText.getText().toString());				//send the data to the BLUNO
//			}
//		});
//
//        buttonScan = (Button) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device
//        buttonScan.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//				buttonScanOnClickProcess();										//Alert Dialog for selecting the BLE device
//			}
//		});

		connectionText=(TextView) findViewById(R.id.connectionState);	// ONE
		mTextViewAngle = (TextView) findViewById(R.id.textView_angle2);
		mTextViewStrength = (TextView) findViewById(R.id.textView_strength2);
		mTextViewCoordinate = (TextView) findViewById(R.id.textView_coordinate2);
		mTextViewMotors = (TextView) findViewById(R.id.textView_motorValues2);
		final double[] lmotor = {0};
		final double[] rmotor = {0};

        tempGraph = (GraphView) findViewById(R.id.graph);
        phGraph = (GraphView) findViewById(R.id.graph2);
        turbGraph = (GraphView) findViewById(R.id.graph3);
        s1 = new LineGraphSeries<DataPoint>();
        s2 = new LineGraphSeries<DataPoint>();
        s3 = new LineGraphSeries<DataPoint>();
        tempGraph.addSeries(s1);
        phGraph.addSeries(s2);
        turbGraph.addSeries(s3);
        tempGraph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        tempGraph.getGridLabelRenderer().setLabelVerticalWidth(75);
        phGraph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
		phGraph.getGridLabelRenderer().setLabelVerticalWidth(75);
        turbGraph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
		turbGraph.getGridLabelRenderer().setLabelVerticalWidth(75);
        tempArray = new ArrayList<Double>();
        phArray = new ArrayList<Double>();
        turbArray = new ArrayList<Double>();
      //  timeArray = new ArrayList<Integer>();
        Viewport viewport1 = tempGraph.getViewport();
        viewport1.setXAxisBoundsManual(true);
        viewport1.setMinX(0);
        viewport1.setMaxX(4);
        Viewport viewport2 = phGraph.getViewport();
        viewport2.setXAxisBoundsManual(true);
        viewport2.setMinX(0);
        viewport2.setMaxX(4);
        Viewport viewport3 = turbGraph.getViewport();
        viewport3.setXAxisBoundsManual(true);
        viewport3.setMinX(0);
        viewport3.setMaxX(4);
        lastX = 0;

        currentTemp = (TextView) findViewById(R.id.currTemp);
        currentTurb = (TextView) findViewById(R.id.currTurb);
        currentpH = (TextView) findViewById(R.id.currpH);

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

			serialSend("@");	// Tells Arduino to stop sending
			serialSend(motorVals);
			serialSend("#");	// Tells Arduino that app is done sending

			Log.i(TAG, "onMove: " + motorVals);
			}
		});

		//For detecting already connected Bluetooth
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		this.registerReceiver(mReceiver, filter);
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            	//Device found
				Log.i(TAG, "onReceive: found");
			}
			else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            	//Device is now connected
				Log.i(TAG, "onReceive: acl connected");
				Log.i(TAG, "onReceive: " + device.getName());
//				serialReceivedText.setText(device.getName());

				if (device.getName().equals("Bluno")) {
					if (mBluetoothLeService.connect(device.getAddress())) {
						Log.d(TAG, "Connect request success");
						mConnectionState = connectionStateEnum.isConnecting;
						onConectionStateChange(mConnectionState);
						mHandler.postDelayed(mConnectingOverTimeRunnable, 10000);
					} else {
						Log.d(TAG, "Connect request fail");
						mConnectionState = connectionStateEnum.isToScan;
						onConectionStateChange(mConnectionState);
					}
				}
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				//Done searching
				Log.i(TAG, "onReceive: discovery finished");
			}
			else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
            	//Device is about to disconnect
				Log.i(TAG, "onReceive: disconnect requested");
			}
			else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            	//Device has disconnected
//				buttonScan.setText("Disconnected");
				connectionText.setText("Disconnected"); // ONE
				Log.i(TAG, "onReceive: disconnected");
			}
		}
	};

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
//			buttonScan.setText("Connected");
			connectionText.setText("Connected");	// ONE
			break;
		case isConnecting:
//			buttonScan.setText("Connecting");
			connectionText.setText("Connecting");	// ONE
			break;
		case isToScan:
//			buttonScan.setText("Scan");
			connectionText.setText("Scan");	// ONE
			break;
		case isScanning:
//			buttonScan.setText("Scanning");
			connectionText.setText("Scanning");	// ONE
			break;
		case isDisconnecting:
//			buttonScan.setText("isDisconnecting");
			connectionText.setText("isDisconnecting");	// ONE
			break;
		default:
			break;
		}
	}

	@Override
	public void onSerialReceived(String theString) {							//Once connection data received, this function will be called
		// TODO Auto-generated method stub
//		serialReceivedText.setText(theString);							//append the text into the EditText
		System.out.println("Current received string: " + theString);
		parseStringForSensorVals(theString);
		//The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
//		((ScrollView)serialReceivedText.getParent()).fullScroll(View.FOCUS_DOWN);
	}

	void parseStringForSensorVals(String string) {
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == '!') {	// All sensor data has been received

				boolean conversionSuccess = true;

				try {
					turb = Double.valueOf(turbStr);        // Converting string to double
					turb = -1120.4*turb*turb + 5742.3 * turb -4352.9;
					if (turb < 0){ turb = 0;}
					System.out.println("Turbidity: " + turb);
					turbArray.add(turb);
					s3.appendData(new DataPoint(lastX, turbArray.get(lastX)), true, 10000);
					currentTurb.setText(df.format(turb) + " NTU");
				} catch (NumberFormatException n) {
					System.out.println("Number Format Exception");
					n.printStackTrace();
					conversionSuccess = false;
				}

				try {
					pH = Double.valueOf(pHStr);
					System.out.println("pH: " + pH);
					phArray.add(pH);
					s2.appendData(new DataPoint(lastX, phArray.get(lastX)), true, 10000);
					currentpH.setText(df.format(pH));
				} catch (NumberFormatException n) {
					System.out.println("Number Format Exception");
					n.printStackTrace();
					conversionSuccess = false;
				}

				try {
					temp = Double.valueOf(tempStr);
					System.out.println("Temperature: " + temp);
					tempArray.add(temp);
					s1.appendData(new DataPoint(lastX, tempArray.get(lastX)), true, 10000);
					currentTemp.setText(df.format(temp)+"\u00B0" + "F");

				} catch (NumberFormatException n) {
					System.out.println("Number Format Exception");
					n.printStackTrace();
					conversionSuccess = false;
				}

				if (conversionSuccess) {
					lastX++;
				}


			} else if (string.charAt(i) == '$') { // Arduino has acknowledged that it has received joystick data
				//sending = false;
			} else if (string.charAt(i) == TURB_CHAR) {	//Determine which sensor value is coming in using preceding char
				currentSensor = string.charAt(i);

				turbStr = "";	// Emptying string for the next data point

			} else if (string.charAt(i) == TEMP_CHAR) {
				currentSensor = string.charAt(i);

				tempStr = "";

			} else if (string.charAt(i) == PH_CHAR) {
				currentSensor = string.charAt(i);

				pHStr = "";

			} else if (isLetter(string.charAt(i))) {
				currentSensor = string.charAt(i);
			} else if (isDigit(string.charAt(i)) || string.charAt(i) == '-' || string.charAt(i) == '.') {
				if (currentSensor == TURB_CHAR) {
					turbStr += string.charAt(i);
				} else if (currentSensor == TEMP_CHAR) {
					tempStr += string.charAt(i);
				} else if (currentSensor == PH_CHAR) {
					pHStr += string.charAt(i);
				}
			}
		}
	}

	static ArrayList<Double> getTempArray(){
		return tempArray;
	}
	static ArrayList<Double> getPhArray(){
		return phArray;
	}
	static ArrayList<Double> getTurbArray(){ return turbArray; }
	//static ArrayList<Integer> getTimeArray(){return timeArray;}

	// For end button
	public void sendMessage(View view) {
		Intent intent = new Intent(this, SummaryActivity.class);
		startActivity(intent);
		finish();
	}
}