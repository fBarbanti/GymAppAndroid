package com.example.myapplication

import java.util.regex.Pattern

// Extend String functionality
fun String.isValidPhoneNumber() : Boolean {
    val patterns =  "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$"
    return Pattern.compile(patterns).matcher(this).matches()
}


class PersonalTrainer(val name: String, val surname: String, var phoneNumber: String) {
    private val workingHours: ArrayList<Availability> = ArrayList<Availability>()

    init {
        if(!phoneNumber.isValidPhoneNumber())
            phoneNumber = "None"

    }

    operator fun plus(availability: Availability) : PersonalTrainer {
        var ris = false
        for(av in workingHours) {
            if(av.contains(availability))
                ris = true
        }
        if(!ris)
            workingHours.add(availability)
        return this
    }

    operator fun minus(availability: Availability) : PersonalTrainer {
        if(workingHours.contains(availability))
            workingHours.remove(availability)
        return this
    }

    override fun toString(): String {
        var s : String = "Personal Trainer $name is available on:\n"
        for(av in workingHours)
            s += "$av\n"
        return s
    }


}
