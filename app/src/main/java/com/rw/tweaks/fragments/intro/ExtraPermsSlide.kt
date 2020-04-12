package com.rw.tweaks.fragments.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.heinrichreimersoftware.materialintro.app.SlideFragment
import com.rw.tweaks.R
import eu.chainfire.libsuperuser.Shell
import kotlinx.android.synthetic.main.wss_slide.*
import kotlinx.coroutines.*

class ExtraPermsSlide : SlideFragment(), CoroutineScope by MainScope() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.extra_perms_slide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.apply {
            grant.setOnClickListener {
                launch {
                    val hasRoot = async { Shell.SU.available() }

                    if (hasRoot.await()) {
                        val result = async {
                            Shell.Pool.SU.run("pm grant ${requireContext().packageName} ${android.Manifest.permission.PACKAGE_USAGE_STATS}")
                            Shell.Pool.SU.run("pm grant ${requireContext().packageName} ${android.Manifest.permission.DUMP}")
                        }

                        result.await()
                    } else {
                        AlertDialog.Builder(
                            requireActivity()
                        )
                            .setTitle(R.string.no_root_title)
                            .setMessage(R.string.no_root_msg)
                            .setPositiveButton(android.R.string.ok, null)
                            .show()
                    }
                }
            }
        }
    }

    override fun canGoForward(): Boolean {
        return true //Maybe add a confirmation dialog?
    }

    override fun onDestroy() {
        super.onDestroy()

        cancel()
    }
}