package com.example.myapplication

class Workers{
    private val pts : ArrayList<PersonalTrainer> = ArrayList<PersonalTrainer>()



    operator fun plus(pt : PersonalTrainer) : Workers {
        if(!pts.contains(pt))
            pts.add(pt)
        return this
    }

    operator fun minus(pt: PersonalTrainer) : Workers {
        if(pts.contains(pt))
            pts.remove(pt)
        return this
    }

    override fun toString(): String {
        var s : String = ""
        for(pt in pts)
            s += "$pt\n"

        return s
    }

}


fun main(args: Array<String>) {
    val workers = Workers()

    val ava = Availability("Monday", 9, 50, 10, 30)
    val ava2 = Availability("Monday", 10, 0,10, 50)
    val ava1 = Availability("Monday", 8, 0,9, 20)
    val pt1 = PersonalTrainer("Mario", "Rossi", "3457711709")
    pt1 + ava
    pt1 + ava2
    pt1 + ava1

    workers + pt1
    print(workers)



}