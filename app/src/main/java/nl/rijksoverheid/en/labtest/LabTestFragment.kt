/*
 * Copyright (c) 2020 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *  Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *  SPDX-License-Identifier: EUPL-1.2
 */
package nl.rijksoverheid.en.labtest

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import nl.rijksoverheid.en.BaseFragment
import nl.rijksoverheid.en.ExposureNotificationsViewModel
import nl.rijksoverheid.en.R
import nl.rijksoverheid.en.databinding.FragmentListBinding
import nl.rijksoverheid.en.lifecyle.EventObserver
import timber.log.Timber

private const val RC_REQUEST_UPLOAD_CONSENT = 1

class LabTestFragment : BaseFragment(R.layout.fragment_list) {
    private val labViewModel: LabTestViewModel by viewModels()
    private val viewModel: ExposureNotificationsViewModel by activityViewModels()
    private val section = LabTestSection(
        retry = { labViewModel.retry() },
        upload = { labViewModel.upload() },
        requestConsent = { viewModel.requestEnableNotifications() }
    )
    private val adapter = GroupAdapter<GroupieViewHolder>().apply { add(section) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentListBinding.bind(view)

        binding.toolbar.apply {
            setTitle(R.string.lab_test_toolbar_title)
            setNavigationIcon(R.drawable.ic_close)
            setNavigationContentDescription(R.string.cd_close)
        }
        binding.content.adapter = adapter

        labViewModel.keyState.observe(viewLifecycleOwner) { keyState -> section.update(keyState) }
        viewModel.notificationState.observe(viewLifecycleOwner) { state -> section.update(state) }

        labViewModel.uploadDiagnosisKeysResult.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is LabTestRepository.RequestUploadDiagnosisKeysResult.RequireConsent -> requestConsent(
                    it.resolution.intentSender
                )
                is LabTestRepository.RequestUploadDiagnosisKeysResult.Success -> findNavController().navigate(
                    R.id.action_lab_test_done
                )
                is LabTestRepository.RequestUploadDiagnosisKeysResult.UnknownError -> TODO("Implement error handling when API is not enabled")
            }
        })
    }

    override fun onStart() {
        super.onStart()
        labViewModel.retry()
    }

    private fun requestConsent(intentSender: IntentSender) {
        try {
            requireActivity().startIntentSenderFromFragment(
                this, intentSender,
                RC_REQUEST_UPLOAD_CONSENT, null, 0, 0, 0, null
            )
        } catch (ex: Exception) {
            Timber.e(ex, "Error requesting consent")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_REQUEST_UPLOAD_CONSENT && resultCode == Activity.RESULT_OK) {
            labViewModel.upload()
            findNavController().navigate(R.id.action_lab_test_done)
        }
    }
}
