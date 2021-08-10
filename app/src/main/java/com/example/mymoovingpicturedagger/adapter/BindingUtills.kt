package com.example.mymoovingpicturedagger.adapter

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.mymoovingpicturedagger.domain.CoordinatesDomain
import com.example.mymoovingpicturedagger.domain.RouteDomain
import com.example.mymoovingpicturedagger.domain.SendRouteDomain
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n")
@BindingAdapter("setHeading")
fun TextView.setHeading(item: RouteDomain?) {
    item?.let {
        text = "${item.recordRouteName} "
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("setTime")
fun TextView.setTime(item: RouteDomain?) {
    item?.let {
        val time: Long = item.time
        val locale = Locale("ru", "RU")
        val format2: DateFormat = SimpleDateFormat(("EEEE, dd MMM yyyy, HH:mm"), locale)
        val daytoday: String = format2.format(time).capitalize()
        text = daytoday
    }
}


@SuppressLint("SetTextI18n")
@BindingAdapter("setHeadingArchive")
fun TextView.setHeadingArchive(item: SendRouteDomain?) {
    item?.let {
        text = "${item.recordName} "
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("setLongitude")
fun TextView.setLongitude(item: CoordinatesDomain?) {
    item?.let {
        text = "${item.longittude} "
    }
}