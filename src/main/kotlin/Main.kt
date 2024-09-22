package work.kyokko

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.bigquery.BigQueryOptions
import com.google.cloud.bigquery.JobId
import com.google.cloud.bigquery.JobInfo
import com.google.cloud.bigquery.QueryJobConfiguration
import com.google.cloud.bigquery.QueryParameterValue
import org.tukaani.xz.LZMA2Options
import org.tukaani.xz.XZOutputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID

fun main() {
    val credentials = GoogleCredentials.getApplicationDefault()

    val bigquery =
        BigQueryOptions.newBuilder()
            .setCredentials(credentials)
            .build()
            .service

    val query = """
        SELECT DISTINCT origin,
                        experimental.popularity.rank
        FROM   `chrome-ux-report.experimental.global`
        WHERE  yyyymm = @min_age
        GROUP  BY origin,
                  experimental.popularity.rank
        ORDER  BY experimental.popularity.rank,
                  origin
    """

    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, -1)
    val sdf = SimpleDateFormat("yyyyMM")
    val yearMonth = sdf.format(calendar.time)

    println("download: $yearMonth")

    val ageParam = QueryParameterValue.int64(yearMonth.toLong())

    val queryConfig =
        QueryJobConfiguration.newBuilder(query)
            .setUseLegacySql(false)
            .addNamedParameter("min_age", ageParam)
            .build()

    val jobId = JobId.of(UUID.randomUUID().toString())
    var queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build())

    queryJob = queryJob.waitFor()

    queryJob?.let {
        if (it.status.error != null) {
            throw RuntimeException(it.status.error.toString())
        }
    } ?: throw RuntimeException("Job no longer exists")

    val result = queryJob.getQueryResults()

    FileOutputStream("$yearMonth.csv.xz").use { fos ->
        XZOutputStream(fos, LZMA2Options()).use { xzOut ->
            OutputStreamWriter(xzOut).use { writer ->
                writer.write("origin,rank\n")

                result.iterateAll().forEach { row ->
                    val origin = row.get("origin").stringValue
                    val rank = row.get("rank").longValue
                    writer.write("$origin,$rank\n")
                }
            }
        }
    }
}
