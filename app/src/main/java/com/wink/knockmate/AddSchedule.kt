package com.wink.knockmate

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.Fade
import android.transition.TransitionManager
import android.util.AndroidException
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.Exception

class AddSchedule : BottomSheetDialogFragment() {
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    @SuppressLint("CommitPrefEdits")
    override fun onAttach(context: Context) {
        super.onAttach(context)

        val pref = activity?.getSharedPreferences("LoginInfo", MODE_PRIVATE)
        val email = pref?.getString("email", "").toString()
        AddScheduleInfo.reset()
        val bundle = arguments
        val args = bundle?.getString("ScheduleType")
        if (args == "ADD") {
            val client = OkHttpClient().newBuilder()
                .build()
            val request: Request = Request.Builder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(
                    "http://3.35.146.57:3000/user?query=${email}"
                )
                .get()
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("log1", e.message.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    object : Thread() {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun run() {
                            if (response.code() == 200) {
                                val res = JSONObject(response.body()?.string())
                                Log.v("test", res.toString())
                                try {
                                    val resTemp = res.getJSONArray("data")
                                    AddScheduleInfo.userEmail =
                                        resTemp.getJSONObject(0).getString("email")
                                    AddScheduleInfo.userId =
                                        resTemp.getJSONObject(0).getString("id")
                                    AddScheduleInfo.color = resTemp.getJSONObject(0).getInt("color")
                                } catch (e: Exception) {
                                    dismiss()
                                    Toast
                                        .makeText(
                                            context,
                                            "유저 정보를 가져올 수 없습니다. 다시 시도해주세요.",
                                            Toast.LENGTH_LONG
                                        )
                                        .show()
                                }
                            } else {
                                dismiss()
                                Toast
                                    .makeText(
                                        context,
                                        "유저 정보를 가져올 수 없습니다. 다시 시도해주세요.",
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                            }
                        }
                    }.run()
                }
            })
            AddScheduleInfo.resetStartCal()
            AddScheduleInfo.startDay = dayOfWeek(AddScheduleInfo.startCal.get(Calendar.DAY_OF_WEEK))
            AddScheduleInfo.endDay = dayOfWeek(AddScheduleInfo.endCal.get(Calendar.DAY_OF_WEEK))

            val request2: Request = Request.Builder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url("http://3.35.146.57:3000/myfollower?email=${email}")
                .get()
                .build()
            client.newCall(request2).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("log2", e.message.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    object : Thread() {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun run() {
                            if (response.code() == 200) {
                                val res = JSONObject(response.body()?.string())
                                val resTemp = res.getJSONArray("data")
                                AddScheduleInfo.followerList.apply {
                                    for (i in 0 until resTemp.length()) {
                                        AddScheduleInfo.followerList.add(
                                            UserModel(
                                                resTemp.getJSONObject(i).getString("id"),
                                                resTemp.getJSONObject(i).getString("nickname"),
                                                true,
                                                resTemp.getJSONObject(i).getString("email"),
                                                i
                                            )
                                        )
                                    }
                                }
                            } else if (response.code() == 201) {
                            } else {
                            }
                        }
                    }.run()
                }
            })

            val request3: Request = Request.Builder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url("http://3.35.146.57:3000/mygroup?email=${email}")
                .get()
                .build()
            client.newCall(request3).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("log3", e.message.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    object : Thread() {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun run() {
                            if (response.code() == 200) {
                                val res = JSONObject(response.body()?.string())
                                val resTemp = res.getJSONArray("data")
                                AddScheduleInfo.groupList.apply {
                                    for (i in 0 until resTemp.length()) {
                                        AddScheduleInfo.groupList.add(
                                            UserModel(
                                                resTemp.getJSONObject(i).getString("id"),
                                                resTemp.getJSONObject(i).getString("nickname"),
                                                false,
                                                "",
                                                i,
                                                false,
                                                true,
                                            )
                                        )
                                    }
                                }
                                for (i in 0 until AddScheduleInfo.groupList.size) {
                                    val temp = AddScheduleInfo.groupList[i].id
                                    val client2 = OkHttpClient().newBuilder()
                                        .build()
                                    val request5: Request = Request.Builder()
                                        .addHeader(
                                            "Content-Type",
                                            "application/x-www-form-urlencoded"
                                        )
                                        .url("http://3.35.146.57:3000/groupuserlist?groupid=${temp}")
                                        .get()
                                        .build()
                                    client2.newCall(request5).enqueue(object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                            Log.d("log6", e.message.toString())
                                        }

                                        override fun onResponse(call: Call, response: Response) {
                                            object : Thread() {
                                                @SuppressLint("NotifyDataSetChanged")
                                                override fun run() {
                                                    if (response.code() == 200) {
                                                        val res =
                                                            JSONObject(response.body()?.string())
                                                        val resTemp = res.getJSONArray("data")
                                                        AddScheduleInfo.groupList[i].members =
                                                            resTemp.length()
                                                    } else if (response.code() == 201) {
                                                    } else {
                                                    }
                                                }
                                            }.run()
                                        }
                                    })
                                }
                            } else if (response.code() == 201) {
                            } else {
                            }
                        }
                    }.run()
                }
            })
        } else if (args == "KNOCK") {
            var startDate = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA)
            var startDateString = bundle?.getString("startDate")
            startDate.set(
                Calendar.DAY_OF_MONTH,
                startDateString!!.split(" ")[0].split("-")[2].toInt()
            )
            startDate.set(Calendar.MONTH, startDateString!!.split(" ")[0].split("-")[1].toInt())
            startDate.set(Calendar.HOUR, startDateString!!.split(" ")[1].split(":")[0].toInt())
            startDate.set(Calendar.MINUTE, 0)
            startDate.set(Calendar.SECOND, 0)
            var endDate = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA)
            var endDateString = bundle?.getString("endDate")
            endDate.set(Calendar.DAY_OF_MONTH, endDateString!!.split(" ")[0].split("-")[2].toInt())
            endDate.set(Calendar.MONTH, endDateString!!.split(" ")[0].split("-")[1].toInt())
            endDate.set(Calendar.HOUR, endDateString!!.split(" ")[1].split(":")[0].toInt() + 1)
            endDate.set(Calendar.MINUTE, 0)
            endDate.set(Calendar.SECOND, 0)
            AddScheduleInfo.startCal = startDate
            AddScheduleInfo.endCal = endDate
            val client = OkHttpClient().newBuilder()
                .build()
            val request: Request = Request.Builder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(
                    "http://3.35.146.57:3000/user?query=${email}"
                )
                .get()
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("log4", e.message.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    object : Thread() {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun run() {
                            if (response.code() == 200) {
                                val res = JSONObject(response.body()?.string())
                                Log.v("test", res.toString())
                                try {
                                    val resTemp = res.getJSONArray("data")
                                    AddScheduleInfo.userEmail =
                                        resTemp.getJSONObject(0).getString("email")
                                    AddScheduleInfo.userId =
                                        resTemp.getJSONObject(0).getString("id")
                                    AddScheduleInfo.color = resTemp.getJSONObject(0).getInt("color")
                                } catch (e: Exception) {
                                    dismiss()
                                }
                            } else {
                                dismiss()
                            }
                        }
                    }.run()
                }
            })
            //AddScheduleInfo.resetStartCal()
            AddScheduleInfo.startDay = dayOfWeek(AddScheduleInfo.startCal.get(Calendar.DAY_OF_WEEK))
            AddScheduleInfo.endDay = dayOfWeek(AddScheduleInfo.endCal.get(Calendar.DAY_OF_WEEK))

            val inviteTemp = bundle.getString("invite")
            val inviteType = bundle.getString("inviteType")

            val request2: Request = Request.Builder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url("http://3.35.146.57:3000/myfollower?email=${email}")
                .get()
                .build()
            client.newCall(request2).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("log5", e.message.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    object : Thread() {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun run() {
                            if (response.code() == 200) {
                                val res = JSONObject(response.body()?.string())
                                val resTemp = res.getJSONArray("data")
                                for (i in 0 until resTemp.length()) {
                                    if (resTemp.getJSONObject(i)
                                            .getString("id") == inviteTemp &&
                                        inviteType == "user"
                                    ) {
                                        AddScheduleInfo.priorInviteMembers.add(
                                            UserModel(
                                                resTemp.getJSONObject(i).getString("id"),
                                                resTemp.getJSONObject(i).getString("nickname"),
                                                true,
                                                resTemp.getJSONObject(i).getString("email"),
                                                i,
                                                true
                                            )
                                        )
                                        AddScheduleInfo.priorInvitersNumber++
                                        AddScheduleInfo.invitersNumber++
                                        AddScheduleInfo.inviteMembers.add(
                                            UserModel(
                                                resTemp.getJSONObject(i).getString("id"),
                                                resTemp.getJSONObject(i).getString("nickname"),
                                                true,
                                                resTemp.getJSONObject(i).getString("email"),
                                                i,
                                                true
                                            )
                                        )
                                        AddScheduleInfo.followerList.add(
                                            UserModel(
                                                resTemp.getJSONObject(i).getString("id"),
                                                resTemp.getJSONObject(i).getString("nickname"),
                                                true,
                                                resTemp.getJSONObject(i).getString("email"),
                                                i,
                                                true
                                            )
                                        )
                                    } else {
                                        AddScheduleInfo.followerList.add(
                                            UserModel(
                                                resTemp.getJSONObject(i).getString("id"),
                                                resTemp.getJSONObject(i).getString("nickname"),
                                                true,
                                                resTemp.getJSONObject(i).getString("email"),
                                                i,
                                            )
                                        )
                                    }

                                }
                            } else if (response.code() == 201) {
                            } else {
                            }
                        }
                    }.run()
                }
            })

            val request3: Request = Request.Builder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url("http://3.35.146.57:3000/mygroup?email=${email}")
                .get()
                .build()
            client.newCall(request3).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("log6", e.message.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    object : Thread() {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun run() {
                            if (response.code() == 200) {
                                val res = JSONObject(response.body()?.string())
                                val resTemp = res.getJSONArray("data")
                                for (i in 0 until resTemp.length()) {
                                    if (resTemp.getJSONObject(i)
                                            .getString("id") == inviteTemp &&
                                        inviteType == "group"
                                    ) {
                                        AddScheduleInfo.priorInviteGroups.add(
                                            UserModel(
                                                resTemp.getJSONObject(i).getString("id"),
                                                resTemp.getJSONObject(i).getString("nickname"),
                                                false,
                                                "",
                                                i,
                                                true,
                                                true,
                                            )
                                        )
                                        AddScheduleInfo.priorInviteGroupsNumber++
                                        AddScheduleInfo.inviteGroupsNumber++
                                        AddScheduleInfo.inviteGroups.add(
                                            UserModel(
                                                resTemp.getJSONObject(i).getString("id"),
                                                resTemp.getJSONObject(i).getString("nickname"),
                                                false,
                                                "",
                                                i,
                                                true,
                                                true,
                                            )
                                        )
                                        AddScheduleInfo.groupList.add(
                                            UserModel(
                                                resTemp.getJSONObject(i).getString("id"),
                                                resTemp.getJSONObject(i).getString("nickname"),
                                                false,
                                                "",
                                                i,
                                                true,
                                                true,
                                            )
                                        )
                                    } else {
                                        AddScheduleInfo.groupList.add(
                                            UserModel(
                                                resTemp.getJSONObject(i).getString("id"),
                                                resTemp.getJSONObject(i).getString("nickname"),
                                                false,
                                                "",
                                                i,
                                            )
                                        )
                                    }
                                }
                                for (i in 0 until AddScheduleInfo.groupList.size) {
                                    val temp = AddScheduleInfo.groupList[i].id
                                    val client2 = OkHttpClient().newBuilder()
                                        .build()
                                    val request5: Request = Request.Builder()
                                        .addHeader(
                                            "Content-Type",
                                            "application/x-www-form-urlencoded"
                                        )
                                        .url("http://3.35.146.57:3000/groupuserlist?groupid=${temp}")
                                        .get()
                                        .build()
                                    client2.newCall(request5).enqueue(object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                            Log.d("log6", e.message.toString())
                                        }

                                        override fun onResponse(call: Call, response: Response) {
                                            object : Thread() {
                                                @SuppressLint("NotifyDataSetChanged")
                                                override fun run() {
                                                    if (response.code() == 200) {
                                                        val res =
                                                            JSONObject(response.body()?.string())
                                                        val resTemp = res.getJSONArray("data")
                                                        AddScheduleInfo.groupList[i].members =
                                                            resTemp.length()
                                                    } else if (response.code() == 201) {
                                                    } else {
                                                    }
                                                }
                                            }.run()
                                        }
                                    })
                                }
                            } else if (response.code() == 201) {
                            } else {
                            }
                        }
                    }.run()
                }
            })
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet = super.onCreateDialog(savedInstanceState)

        val view = View.inflate(context, R.layout.addschedule, null)

        bottomSheet.setContentView(view)

        val transition = Fade()
        transition.duration = 200

        val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)

        bottomSheetBehavior.peekHeight = 400

        childFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.addschedule_frame, AddSchedule_brief())
            .commit()

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    childFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.fade_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.fade_out
                        )
                        .replace(R.id.addschedule_frame, AddSchedule_brief())
                        .commit()

                    AddScheduleInfo.brief = true
                }
                if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    if (AddScheduleInfo.brief) {
                        childFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                            )
                            .replace(R.id.addschedule_frame, AddSchedule_detail())
                            .commit()
                    }

                    AddScheduleInfo.brief = false
                }
                if (BottomSheetBehavior.STATE_DRAGGING == newState) {
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

        })


        return bottomSheet
    }

    private fun dayOfWeek(d: Int): String {
        return when (d) {
            1 -> "일"
            2 -> "월"
            3 -> "화"
            4 -> "수"
            5 -> "목"
            6 -> "금"
            7 -> "토"
            else -> " "
        }
    }
}