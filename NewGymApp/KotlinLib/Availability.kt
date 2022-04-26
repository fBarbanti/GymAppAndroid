package com.example.myapplication

class Availability(var day: String, var startHour: Int, var startMinute: Int, var endHour: Int, var endMinute: Int){

    private val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", "None")

    init {
        checkHour();
        checkMinute();
        checkDay();
    }

    private fun checkDay() {
        if(!daysOfWeek.contains(day))
            day = "None"
    }

    private fun checkMinute() {
        if(startMinute > 60 || startMinute < 0)
            startMinute = 0;
        if(endMinute > 60 || endMinute < 0)
            endMinute = 0;
    }

    private fun checkHour() {
        if(startHour > 24 || startHour < 0)
            startHour = 0;
        if(endHour > 24 || endHour < 0)
            endHour = 0;
        if(startHour > endHour){
            val temp = startHour
            startHour = endHour
            endHour = temp
        }
    }

    override fun toString(): String {
        return "$day $startHour:$startMinute - $endHour:$endMinute"
    }


    // RITORNA TRUE SE availability NON TERMINA PRIMA DELL'INIZIO DI THIS
    private fun startBefore(availability : Availability) : Boolean {
        if(startHour > availability.endHour)
            return false
        if((startHour == availability.endHour) && (startMinute > availability.endMinute))
            return false
        return true
    }

    // RITORNA TRUE SE availability NON INIZIA PRIMA DELLA FINE DI THIS
    private fun startAfter(availability : Availability) : Boolean {
        if(endHour < availability.startHour)
            return false
        if((endHour == availability.startHour) && (endMinute <= availability.startMinute))
            return false
        return true
    }

    operator fun contains(availability: Availability) : Boolean {
        val indexArg = daysOfWeek.indexOf(availability.day);
        val indexThis = daysOfWeek.indexOf(day)

        if(indexThis != indexArg)
            return false
        if(day == "None")
            return false

        if(startBefore(availability) && startAfter(availability))
            return true
        return false
    }



}