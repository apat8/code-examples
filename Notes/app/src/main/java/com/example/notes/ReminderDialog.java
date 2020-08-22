package com.example.notes;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Dialog used to set a Reminder.
 * Reminder consists spinner for date, time, and frequency
 */
public class ReminderDialog extends DialogFragment implements AdapterView.OnItemSelectedListener  {

    /**
     * The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it.
     */
    public interface ReminderDialogListener{
        void onDialogPositiveClick(Date reminderDate);
        void onDialogNegativeClick();
    }

    // Use this instance of the interface to deliver action events
    ReminderDialogListener mReminderDialogListener;

    // Declare date formatter
    DateFormat mMonthDayDateFormatter;
    DateFormat mWeekdayMonthDayDateFormatter;
    DateFormat mTimeFormatter;
    Spinner mDateSpinner;
    Spinner mTimeSpinner;

    // Declare Reminder date and formatted date
    Calendar mReminderDate;
    String mFormattedReminderDate;

    // Declare date and formatted date for today and tomorrow
    Calendar mCalendarDateToday;
    Calendar mCalendarDateTomorrow;
    String mFormattedDateToday;
    String mFormattedDateTomorrow;

    // When spinners are set onItemSelected is called. To ensure that date picker is not shown
    // when the spinners are created for reminder dates other than today or tomorrow, a flag is set.
    // When true, only the spinner text is update, when false, the date picker is shown.
    boolean isFirstDateSelectionOptionTwo;

    // When spinners are set onItemSelected is called. To ensure that time picker is not shown
    // when the spinners are created for times other than 8, 1, 6, or 10, a flag is set.
    // When true, only the spinner text is update, when false, the time picker is shown.
    boolean isFirstTimeSelectionOptionFour;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Date formatter
        mMonthDayDateFormatter = new SimpleDateFormat("MMM dd", Locale.getDefault());
        mWeekdayMonthDayDateFormatter = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault());
        // Time formatter
        mTimeFormatter = new SimpleDateFormat("h:mm a", Locale.getDefault());
        // Initialize initial date, time selection
        int dateSelection = 0;
        int timeSelection = 0;
        // Initialize first date selection flag
        isFirstDateSelectionOptionTwo = false;
        isFirstTimeSelectionOptionFour = false;

        // Initialize formatted today and tomorrow dates
        initializeReminderTodayTomorrowDates();

        // Get arguments, reminder date
        Bundle args = getArguments();
        if(args != null){
            Reminder reminder = (Reminder)args.getSerializable("Date");
            Calendar calendarDate = Calendar.getInstance();
            calendarDate.setTime(reminder.getDateTime());
            mReminderDate = calendarDate;
            mFormattedReminderDate = mWeekdayMonthDayDateFormatter.format(mReminderDate.getTime());

            String formattedDate = mMonthDayDateFormatter.format(reminder.getDateTime());

            if(formattedDate.equals(mFormattedDateTomorrow)){
                dateSelection = 1;
            }
            else if(!formattedDate.equals(mFormattedDateToday)){
                dateSelection = 2;
                isFirstDateSelectionOptionTwo = true;
            }

            // Format time and check which option it is from the time spinner
            String formattedTime = mTimeFormatter.format(mReminderDate.getTime());
            switch (formattedTime) {
                case "8:00 AM":
                    timeSelection = 0;
                    break;
                case "1:00 PM":
                    timeSelection = 1;
                    break;
                case "6:00 PM":
                    timeSelection = 2;
                    break;
                case "10:00 PM":
                    timeSelection = 3;
                    break;
                default:
                    timeSelection = 4;
                    isFirstTimeSelectionOptionFour = true;
                    break;
            }
        }

        // Setup spinners
        View view = setupSpinners(dateSelection, timeSelection);
        // Create alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle("Add Reminder")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Send the positive button event back to the host activity, EditNoteActivity
                        mReminderDialogListener.onDialogPositiveClick(mReminderDate.getTime());
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Send the negative button event back to the host activity, EditNoteActivity
                        mReminderDialogListener.onDialogNegativeClick();
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        return builder.create();
    }

    /**
     * Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try{
            // Instantiate the NoticeDialogListener so we can send events to the host
            mReminderDialogListener = (ReminderDialogListener) context;

        }catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement ReminderDialogListener");
        }
    }


    /**
     * Update reminder data when spinner options are selected
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, final View view, int i, long l) {
        switch (adapterView.getId()){
            // Date spinner is selected
            case R.id.reminder_dialog_date_spinner:
                switch (i){
                    // Today's date selected
                    case 0:
                        mReminderDate.set(mCalendarDateToday.get(Calendar.YEAR), mCalendarDateToday.get(Calendar.MONTH),
                                mCalendarDateToday.get(Calendar.DAY_OF_MONTH));
                        break;
                    // Tomorrow's date selected
                    case 1:
                        mReminderDate.set(mCalendarDateTomorrow.get(Calendar.YEAR), mCalendarDateTomorrow.get(Calendar.MONTH),
                                mCalendarDateTomorrow.get(Calendar.DAY_OF_MONTH));
                        break;
                    // Pick a date is selected
                    case 2:
                        // When the spinner is created, only the spinner text should be updated
                        // when the reminder date is not today or tomorrow
                        if(isFirstDateSelectionOptionTwo){
                            ((TextView)view).setText(mFormattedReminderDate);
                            isFirstDateSelectionOptionTwo = false;
                        }
                        else{
                            new DatePickerDialog(getActivity(),
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                            mReminderDate.set(i, i1, i2);
                                            mFormattedReminderDate = mWeekdayMonthDayDateFormatter.format(mReminderDate.getTime());
                                            ((TextView)view).setText(mFormattedReminderDate);
                                        }
                                    }, mReminderDate.get(Calendar.YEAR), mReminderDate.get(Calendar.MONTH),
                                    mReminderDate.get(Calendar.DAY_OF_MONTH)).show();
                        }
                        break;
                }
                break;
            case R.id.reminder_dialog_time_spinner:
                switch (i){
                    // 8:00 AM option
                    case 0:
                        mReminderDate.set(Calendar.HOUR, 8);
                        mReminderDate.set(Calendar.MINUTE, 0);
                        mReminderDate.set(Calendar.AM_PM, Calendar.AM);
                        break;
                    // 1:00 PM option
                    case 1:
                        mReminderDate.set(Calendar.HOUR, 1);
                        mReminderDate.set(Calendar.MINUTE, 0);
                        mReminderDate.set(Calendar.AM_PM, Calendar.PM);
                        break;
                    // 6:00 PM option
                    case 2:
                        mReminderDate.set(Calendar.HOUR, 6);
                        mReminderDate.set(Calendar.MINUTE, 0);
                        mReminderDate.set(Calendar.AM_PM, Calendar.PM);
                        break;
                    // 10:00 PM option
                    case 3:
                        mReminderDate.set(Calendar.HOUR, 10);
                        mReminderDate.set(Calendar.MINUTE, 0);
                        mReminderDate.set(Calendar.AM_PM, Calendar.PM);
                        break;
                    // Pick a time option
                    case 4:
                        if(isFirstTimeSelectionOptionFour){
                            ((TextView)view).setText(mTimeFormatter.format(mReminderDate.getTime()));
                            isFirstTimeSelectionOptionFour = false;
                        }
                        else{
                            new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                    mReminderDate.set(Calendar.HOUR_OF_DAY, i);
                                    mReminderDate.set(Calendar.MINUTE, i1);
                                    ((TextView)view).setText(mTimeFormatter.format(mReminderDate.getTime()));
                                }
                            }, mReminderDate.get(Calendar.HOUR_OF_DAY), mReminderDate.get(Calendar.MINUTE),false).show();
                        }

                        break;
                }
            break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Setup date, time and frequency spinners
     * @param dateSelection The initial date spinner option
     * @param timeSelection The initial time spinner option
     * @return view Returns the Reminder dialog layout
     */
    View setupSpinners(int dateSelection, int timeSelection){
        // Inflate reminder dialog layout (for spinners)
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.reminder_dialog, null);

        // Setup date spinner
        setupDateSpinner(view, dateSelection);

        // Setup time spinner
        setupTimeSpinner(view, timeSelection);

        return view;
    }


    /**
     * Setup date spinner
     * @param view The reminder dialog layout that holds the date spinner
     * @param dateSelection The initial spinner selection
     */
    void setupDateSpinner(View view, int dateSelection){
        // Date spinner options
        String[] dateSpinnerOptions = {"Today, " + mFormattedDateToday,"Tomorrow, " + mFormattedDateTomorrow, "Pick a date"};

        // Get date spinner and create array adapter for it
        mDateSpinner = view.findViewById(R.id.reminder_dialog_date_spinner);

        // Set the item selected listener
        mDateSpinner.setOnItemSelectedListener(this);

        //  Set the adapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item,dateSpinnerOptions);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mDateSpinner.setAdapter(adapter);

        // Set initial option for date spinner
        mDateSpinner.setSelection(dateSelection);
    }

    /**
     * Setup time spinner with its options
     * @param view The reminder dialog layout that holds the time spinner
     * @param timeSelection The initial spinner selection
     */
    void setupTimeSpinner(View view, int timeSelection){
        // Time spinner options
        String[] timeSpinnerOptions = {"8:00 AM", "1:00 PM", "6:00 PM", "10:00 PM", "Pick a time"};

        // Get time spinner
        mTimeSpinner = view.findViewById(R.id.reminder_dialog_time_spinner);

        // Set the item selected listener
        mTimeSpinner.setOnItemSelectedListener(this);

        // Set the adapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, timeSpinnerOptions);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mTimeSpinner.setAdapter(adapter);

        // Set initial option for time spinner
        mTimeSpinner.setSelection(timeSelection);
    }


    /**
     * Initialize Reminder, Today and Tomorrows formatted dates (Month day)
     */
    void initializeReminderTodayTomorrowDates(){
        // Set today's formatted date
        mCalendarDateToday = Calendar.getInstance();
        mFormattedDateToday = mMonthDayDateFormatter.format(mCalendarDateToday.getTime());

        // Set tomorrow's formatted date
        mCalendarDateTomorrow = Calendar.getInstance();
        mCalendarDateTomorrow.add(Calendar.DAY_OF_MONTH, 1);
        mFormattedDateTomorrow = mMonthDayDateFormatter.format(mCalendarDateTomorrow.getTime());

        // Initialize reminder date and set formatted reminder date to Today's date
        mReminderDate = mCalendarDateToday;
        mFormattedReminderDate = mFormattedDateToday;
    }




}
