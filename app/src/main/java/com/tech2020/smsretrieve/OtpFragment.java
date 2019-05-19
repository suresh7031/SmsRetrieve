package com.tech2020.smsretrieve;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OtpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OtpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtpFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView textViewResend, smsDesc;
    Button btn_verify;
    EditText otpText;
    ContentLoadingProgressBar progressBar;

    CountDownTimer countDownTimer;

    private OnFragmentInteractionListener mListener;

    public OtpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OtpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OtpFragment newInstance(String param1, String param2) {
        OtpFragment fragment = new OtpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.fragment_otp, container, false);
        textViewResend=view.findViewById(R.id.id_resend);
        btn_verify=view.findViewById(R.id.verify_otp);
        otpText=view.findViewById(R.id.otptext);
        smsDesc=view.findViewById(R.id.sms_desc);

        smsDesc.setText(new String(getResources().getString(R.string.sms_desc))+ "+919032652986");

        btn_verify.setEnabled(false);

        progressBar=view.findViewById(R.id.otploading);
        startCountDown(textViewResend);
        progressBar.show();
        textViewResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCountDown(view);
                progressBar.show();
                onButtonPressed("resend");
                ///code to resend otp here

                ///
            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed("verify_otp");
            }
        });

        otpText.addTextChangedListener(watcher);

        return view;
    }


    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {}
        @Override
        public void afterTextChanged(Editable s) {
            if (otpText.getText().toString().length() == 0) {
                btn_verify.setEnabled(false);
            } else {
                btn_verify.setEnabled(true);
                progressBar.hide();
            }
        }
    };


    private void startCountDown(final View view){
        countDownTimer=new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                textViewResend.setText("Resend OTP in: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                textViewResend.setText("Resend OTP");
                textViewResend.setEnabled(true);
            }
        }.start();
        textViewResend.setEnabled(false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setOtpCode(String otp) {
        if(otpText!=null){
            otpText.setText(otp);
            btn_verify.setEnabled(true);
            if(progressBar!=null){
                progressBar.hide();
            }
            /*textViewResend.setText("Resend OTP");
            textViewResend.setEnabled(false);
            if(countDownTimer!=null){
            countDownTimer.cancel();
            }*/
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String uri);
    }
}
