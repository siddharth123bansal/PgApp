package com.patel.tanmay.assignment_login.activities

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.patel.tanmay.assignment_login.Utils
import org.json.JSONObject
import java.util.*
import kotlin.reflect.KFunction2

class RentDatePicker(val fetchData: KFunction2<Int, Int, Unit>) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day)

    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
      //  Toast.makeText(context, "M : "+month+"Y : "+year , Toast.LENGTH_SHORT).show()
        val timeObj = JSONObject()
        timeObj.put("month",month+1)
        timeObj.put("year",year)
        Utils.saveDate(context,timeObj.toString())
        fetchData(month+1,year)
    }
}