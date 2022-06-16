package com.example.travelguidapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarBottomSheet extends BottomSheetDialogFragment {

    private MaterialCalendarView calendarView_selectDate;
    private Button btn_selectDate;
    private SimpleDateFormat simpleDateFormat;
    private String selectedDate;
    private BottomSheetListener mListener;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, R.style. CustomBottomSheetDialogTheme);

    }


    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog= (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view= LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_layout_calender,null);


        calendarView_selectDate = (MaterialCalendarView)view.findViewById(R.id.calendarViewSelectThisDate);


        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        calendarView_selectDate.setHeaderTextAppearance(R.style.SelectCalendarWidgetHeader);
        calendarView_selectDate.setWeekDayTextAppearance(R.style.SelectCalendarWidgetWeekDate);


        calendarView_selectDate.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {


                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, date.getYear());
                calendar.set(Calendar.MONTH, date.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, date.getDay());

                selectedDate = simpleDateFormat.format(calendar.getTime());
                mListener.onButtonCLiked(selectedDate);
                bottomSheetDialog.cancel();
            }


        });


        bottomSheetDialog.setContentView(view);

        return bottomSheetDialog;
    }

    public interface BottomSheetListener
    {
        void onButtonCLiked(String text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListener =(BottomSheetListener)context;

    }
}
