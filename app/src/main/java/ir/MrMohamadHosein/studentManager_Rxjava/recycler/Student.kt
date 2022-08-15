package ir.MrMohamadHosein.studentManager_Rxjava.recycler

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Student(

    val id: Long,
    val name: String,
    val course: String,
    val score: Int

) :Parcelable