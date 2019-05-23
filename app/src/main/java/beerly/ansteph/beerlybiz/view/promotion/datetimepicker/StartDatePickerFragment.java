package beerly.ansteph.beerlybiz.view.promotion.datetimepicker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import beerly.ansteph.beerlybiz.R;

/**
 * Created by loicstephan on 2018/06/16.
 */

public class StartDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the current date as default date in picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month,day);
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

        TextView txtStartDate = (TextView) getActivity().findViewById(R.id.txtstartdateday);


        String monthcon = (month<10)? "0"+String.valueOf(month+1):String.valueOf(month+1);
        String daycon =  (dayOfMonth<10)? "0"+String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);

        txtStartDate.setText(String.valueOf(year) +"-"+ monthcon +"-"+  daycon);
    }
}
