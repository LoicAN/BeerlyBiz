package beerly.ansteph.beerlybiz.view.promotion.datetimepicker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import beerly.ansteph.beerlybiz.R;

/**
 * Created by loicstephan on 2018/06/16.
 */

public class EndTimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }



    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

        TextView txtStartTime = (TextView) getActivity().findViewById(R.id.txtenddatetime);

        String min ="";
        if (minute<10)
        {
            min = "0"+String.valueOf(minute);
        }else{
            min =String.valueOf(minute);
        }

        String hour = "";
        if(hourOfDay<10)
        {
            hour="0"+String.valueOf(hourOfDay);
        }else{
            hour=String.valueOf(hourOfDay);
        }

        txtStartTime.setText( hour+ ":"+ min+":00");
    }
}
