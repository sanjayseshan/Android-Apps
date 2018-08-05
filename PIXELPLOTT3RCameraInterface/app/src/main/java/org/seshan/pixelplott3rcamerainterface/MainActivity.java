package org.seshan.pixelplott3rcamerainterface;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
//import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
//import org.opencv.core.Rect;
//import org.opencv.core.Rect2d;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
//import android.graphics.Bitmap;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Camera;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
//import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Toast;
/*import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
*/
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
//import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.IOException;
/*
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;
*/
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.PrintWriter;
import java.security.Policy;
import java.util.HashMap;
import java.util.Map;


//public class MainActivity extends AppCompatActivity {
public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    public static final int      VIEW_MODE_RGBA      = 0;
    public static final int      VIEW_MODE_HIST      = 1;
    public static final int      VIEW_MODE_CANNY     = 2;
    public static final int      VIEW_MODE_SEPIA     = 3;
    public static final int      VIEW_MODE_SOBEL     = 4;
    public static final int      VIEW_MODE_ZOOM      = 5;
    public static final int      VIEW_MODE_PIXELIZE  = 6;
    public static final int      VIEW_MODE_POSTERIZE = 7;

    private MenuItem             mItemPreviewRGBA;
    private MenuItem             mItemPreviewHist;
    private MenuItem             mItemPreviewCanny;
    private MenuItem             mItemPreviewSepia;
    private MenuItem             mItemPreviewSobel;
    private MenuItem             mItemPreviewZoom;
    private MenuItem             mItemPreviewPixelize;
    private MenuItem             mItemPreviewPosterize;
//    private CameraBridgeViewBase mOpenCvCameraView;

    private Size mSize0;

    private Mat                  mIntermediateMat;
    private Mat                  mMat0;
    private MatOfInt             mChannels[];
    private MatOfInt mHistSize;
    private int                  mHistSizeNum = 25;
    private MatOfFloat mRanges;
    private Scalar               mColorsRGB[];
    private Scalar               mColorsHue[];
    private Scalar               mWhilte;
    private Point                mP1;
    private Point                mP2;
    private float                mBuff[];
    private Mat                  mSepiaKernel;
    SeekBar max_seek;
    SeekBar min_seek;
    CheckBox pause_btn;

    public static int           viewMode = VIEW_MODE_RGBA;

    private static final String TAG = "OCVSample::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }



    private CameraManager mCameraManager;
    private String mCameraId;
    private Boolean isTorchOn;

    /** Called when the activity is first created. */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



   /*     mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        try {
            mCameraManager.setTorchMode(mCameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

*/


        setContentView(R.layout.activity_main);




        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_camera_view);
        max_seek = (SeekBar)findViewById(R.id.max);
        min_seek = (SeekBar)findViewById(R.id.min);
        pause_btn = (CheckBox) findViewById(R.id.pause);




        Rect camsize = new Rect(0,0,500,300);

        mOpenCvCameraView.setMaxFrameSize(1500,900);


        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);


        mOpenCvCameraView.setCvCameraViewListener(this);



    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mIntermediateMat = new Mat();
        mSize0 = new Size();
        mChannels = new MatOfInt[] { new MatOfInt(0), new MatOfInt(1), new MatOfInt(2) };
        mBuff = new float[mHistSizeNum];
        mHistSize = new MatOfInt(mHistSizeNum);
        mRanges = new MatOfFloat(0f, 256f);
        mMat0  = new Mat();
        mColorsRGB = new Scalar[] { new Scalar(200, 0, 0, 255), new Scalar(0, 200, 0, 255), new Scalar(0, 0, 200, 255) };
        mColorsHue = new Scalar[] {
                new Scalar(255, 0, 0, 255),   new Scalar(255, 60, 0, 255),  new Scalar(255, 120, 0, 255), new Scalar(255, 180, 0, 255), new Scalar(255, 240, 0, 255),
                new Scalar(215, 213, 0, 255), new Scalar(150, 255, 0, 255), new Scalar(85, 255, 0, 255),  new Scalar(20, 255, 0, 255),  new Scalar(0, 255, 30, 255),
                new Scalar(0, 255, 85, 255),  new Scalar(0, 255, 150, 255), new Scalar(0, 255, 215, 255), new Scalar(0, 234, 255, 255), new Scalar(0, 170, 255, 255),
                new Scalar(0, 120, 255, 255), new Scalar(0, 60, 255, 255),  new Scalar(0, 0, 255, 255),   new Scalar(64, 0, 255, 255),  new Scalar(120, 0, 255, 255),
                new Scalar(180, 0, 255, 255), new Scalar(255, 0, 255, 255), new Scalar(255, 0, 215, 255), new Scalar(255, 0, 85, 255),  new Scalar(255, 0, 0, 255)
        };
        mWhilte = Scalar.all(255);
        mP1 = new Point();
        mP2 = new Point();

        // Fill sepia kernel
        mSepiaKernel = new Mat(4, 4, CvType.CV_32F);
        mSepiaKernel.put(0, 0, /* R */0.189f, 0.769f, 0.393f, 0f);
        mSepiaKernel.put(1, 0, /* G */0.168f, 0.686f, 0.349f, 0f);
        mSepiaKernel.put(2, 0, /* B */0.131f, 0.534f, 0.272f, 0f);
        mSepiaKernel.put(3, 0, /* A */0.000f, 0.000f, 0.000f, 1f);

    }

    public void onCameraViewStopped() {
        // Explicitly deallocate Mats
        if (mIntermediateMat != null)
            mIntermediateMat.release();

        mIntermediateMat = null;

    }
/*
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();
    }
*/
//    public CvCameraViewFrame inputFrameGlobal;
//    public CvCameraViewFrame inputFrame;

    public Mat rgbabkup;
   // public Mat canny_rgba;
    public Mat rgba;
    public int init_val = 0, init_val2=0, print_active = 0;
    public boolean cannyready = false;
    Size sizeRgba;

    boolean pause_val = false;

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {



        System.gc();


        pause_val = pause_btn.isChecked();

/*        if (init_val2 == 1 && print_active == 0) {
            canny_rgba.release();
            cannyready = false;
        } else {
            cannyready = true;
        }*/
        System.out.println(print_active);
        init_val2 = 1;

        if (pause_val) {
            if (init_val == 0) {
                init_val = 1;
                rgba = inputFrame.rgba();
                rgbabkup = rgba.clone();
            } else if (init_val == 1) {
                rgba.release();
                rgba = rgbabkup.clone();
                System.out.println("Paused");
        //        init_val = 2;
            }
                //           inputFrame = inputFrameGlobal;
            } else {
            //    rgba.copyTo(rgbabkup);
//                rgbabkup = new rgba();

                //            inputFrame = inputFrameTemp;
                //            inputFrameGlobal = (CvCameraViewFrame) inputFrameTemp;
            rgba = inputFrame.rgba();
            System.out.println("Resumed");
            init_val = 0;
            }


        sizeRgba = rgba.size();

        Mat rgbaInnerWindow;

        int rows = (int) sizeRgba.height;
        int cols = (int) sizeRgba.width;

        int left = cols / 8;
        int top = rows / 8;

        int width = cols * 3 / 4;
        int height = rows * 3 / 4;

        //        rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);
        rgbaInnerWindow = rgba.submat(0, rows, 0, cols);

        int max_thresh = max_seek.getProgress() * 255 / 100;
        int min_thresh = min_seek.getProgress() * 255 / 100;


        System.out.println("Max: " + max_thresh);
        System.out.println("Min: " + min_thresh);


        Imgproc.Canny(rgbaInnerWindow, mIntermediateMat, min_thresh, max_thresh);
        Imgproc.cvtColor(mIntermediateMat, rgbaInnerWindow, Imgproc.COLOR_GRAY2BGRA, 4);
        rgbaInnerWindow.release();

    //    canny_rgba = rgba.clone();
        if (print_active == 1) {
            try {
                printpixreal(rgba.clone());
            } catch (IOException e) {
                e.printStackTrace();
            }
            print_active = 0;
        }
        System.gc();



        return rgba;

    }


   /* public void pauseresume(View view) {
        boolean pause_val = pause_btn.isChecked();

        if (pause_val) {
            onPause();
            System.out.println("Paused");
        }
        if (!pause_val) {
            mOpenCvCameraView.enableView();
            System.out.println("Resumed");
        }
    }*/

    private static String encodeFileToBase64Binary(File file){
        String encodedfile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            encodedfile = new String(Base64.encode(bytes, Base64.DEFAULT), "UTF-8");
//            encodedfile = Base64.encodeBase64(bytes).toString();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return encodedfile;
    }

    public void printpix(View view) throws IOException {
        print_active = 1;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Uploaded to Server...");
        builder1.setCancelable(true);

        builder1.setNegativeButton(
                "Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    public void printpixreal(Mat canny_rgba)throws IOException {
        pause_btn.setChecked(true);

        Mat resizeimage = new Mat();
        Size sz = new Size((1280 * 500 / 1280), (720 * 500 / 1280));

        Imgproc.resize(canny_rgba, resizeimage, sz);
        canny_rgba.release();

        System.out.println("Success: " + Imgcodecs.imwrite("/sdcard/data.jpg", resizeimage));

        File imageBitmap = new File("/sdcard/data.jpg");


        //     System.out.println("data:image/jpg;base64,"+ encodeFileToBase64Binary(imageBitmap));

        final String dataurl = new String("data:image/jpg;base64," + encodeFileToBase64Binary(imageBitmap));

        PrintWriter  output = new PrintWriter( "/sdcard/tmp.txt" );

        output.println(dataurl);

        CallAPI printclass = new CallAPI();

        //  printclass.doInBackground("http://192.168.8.1:8000/upload.php?name=pi","test");

   /*     URL url = new URL("http://192.168.8.1:8000/upload.php");
        HttpURLConnection client = null;
        try {
            client = (HttpURLConnection) url.openConnection();
            System.out.println("uploaded");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("failed");

        }*/

        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);

        String url = "http://192.168.8.1:8000/upload.php?name=pi";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                System.out.println("success: "+ response);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("failed");
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("hidden_data", dataurl); //Add the data you'd like to send to the server.
                return MyData;
            }
        };

     //   Toast.makeText(getApplicationContext(), "Uploaded to server!", Toast.LENGTH_SHORT).show();




        MyRequestQueue.add(MyStringRequest);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            pause_btn.setChecked(!pause_btn.isChecked());
        }
        return true;
    }



    }

