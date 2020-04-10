package com.example.workoutreminder.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.workoutreminder.R
import kotlinx.android.synthetic.main.main_fragment.*
import android.content.Intent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.work.WorkManager
import com.example.workoutreminder.ui.main.MainViewModel.Companion.periodicWorkTag
import com.example.workoutreminder.ui.util.BounceInterpolator
import androidx.work.WorkInfo


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var viewModel: MainViewModel

    private lateinit var bounceAnimation: Animation


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        checkIfWorkRequestIsActive()
        initButtons()
        initBounceAnimator()

    }

    private fun checkIfWorkRequestIsActive() {
        val info = WorkManager.getInstance(requireContext().applicationContext)
            .getWorkInfosByTag(periodicWorkTag).get()

        if (info == null || info.size == 0) {
            viewModel.setNotificationsEnabled(false)
        } else {
            viewModel.setNotificationsEnabled(
                (info[0].state == WorkInfo.State.RUNNING) or (info[0].state == WorkInfo.State.ENQUEUED)
            )
        }
    }

    private fun showActiveButton() {
        viewModel.notificationsEnabled.observe(this, Observer {
            if (it == true) {
                startButton.visibility = View.GONE
                stopButton.visibility = View.VISIBLE
            } else {
                startButton.visibility = View.VISIBLE
                stopButton.visibility = View.GONE
            }
        })
    }


    private fun initButtons() {
        startButton = start_button
        stopButton = stop_button

        startButton.setOnClickListener {
            Toast.makeText(context, "Notification created", Toast.LENGTH_SHORT).show()
//            openNotificationSettingsForApp()
            it.startAnimation(bounceAnimation)

            viewModel.schedulePeriodicWork(requireContext().applicationContext)
            viewModel.setNotificationsEnabled(true)
        }

        stopButton.setOnClickListener {
            it.startAnimation(bounceAnimation)
            viewModel.cancelWork(context!!)
            Toast.makeText(context, "Notification stopped", Toast.LENGTH_SHORT).show()
            viewModel.setNotificationsEnabled(false)
        }

        showActiveButton()
    }

    private fun initBounceAnimator() {
        bounceAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.bounce)
        val interpolator = BounceInterpolator(0.05, 10.0)
        bounceAnimation.interpolator = interpolator
    }


    private fun openNotificationSettingsForApp() {
        // Links to this app's notification settings.
        val intent = Intent()
        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        intent.putExtra("app_package", requireActivity().packageName)
        intent.putExtra("app_uid", requireActivity().applicationInfo?.uid)
        startActivity(intent)
    }
}
