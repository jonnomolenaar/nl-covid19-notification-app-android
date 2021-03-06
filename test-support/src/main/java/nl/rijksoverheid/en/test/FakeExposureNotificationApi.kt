/*
 * Copyright (c) 2020 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *  Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *  SPDX-License-Identifier: EUPL-1.2
 */
package nl.rijksoverheid.en.test

import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration
import com.google.android.gms.nearby.exposurenotification.ExposureSummary
import nl.rijksoverheid.en.enapi.DiagnosisKeysResult
import nl.rijksoverheid.en.enapi.DisableNotificationsResult
import nl.rijksoverheid.en.enapi.EnableNotificationsResult
import nl.rijksoverheid.en.enapi.StatusResult
import nl.rijksoverheid.en.enapi.TemporaryExposureKeysResult
import nl.rijksoverheid.en.enapi.nearby.ExposureNotificationApi
import java.io.File

open class FakeExposureNotificationApi : ExposureNotificationApi {
    override suspend fun getStatus(): StatusResult = StatusResult.Disabled

    override suspend fun requestEnableNotifications(): EnableNotificationsResult =
        EnableNotificationsResult.Enabled

    override suspend fun disableNotifications(): DisableNotificationsResult =
        DisableNotificationsResult.Disabled

    override suspend fun requestTemporaryExposureKeyHistory(): TemporaryExposureKeysResult =
        TemporaryExposureKeysResult.Success(
            emptyList()
        )

    override suspend fun provideDiagnosisKeys(
        files: List<File>,
        configuration: ExposureConfiguration,
        token: String
    ): DiagnosisKeysResult = DiagnosisKeysResult.Success

    override suspend fun getSummary(token: String): ExposureSummary? = null
}
