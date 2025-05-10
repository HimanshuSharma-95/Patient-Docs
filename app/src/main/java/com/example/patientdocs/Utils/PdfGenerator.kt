package com.example.patientdocs.Utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.patientdocs.Model.FormDataReceive
import java.io.IOException


@RequiresApi(Build.VERSION_CODES.Q)
fun createPatientPdf(context: Context, patient: FormDataReceive) {


    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = document.startPage(pageInfo)

    val canvas = page.canvas

    val titlePaint = android.graphics.Paint().apply {
        textSize = 20f
        isFakeBoldText = true
        color = Color.BLACK
    }

    val sectionPaint = android.graphics.Paint().apply {
        textSize = 16f
        isFakeBoldText = true
        color = Color.DKGRAY
    }

    val textPaint = android.graphics.Paint().apply {
        textSize = 14f
        color = Color.BLACK
    }

    val dividerPaint = android.graphics.Paint().apply {
        strokeWidth = 1.5f
        color = Color.LTGRAY
    }

    var y = 50

    fun drawDivider() {
        canvas.drawLine(40f, y.toFloat(), 555f, y.toFloat(), dividerPaint)
        y += 30
    }

    fun drawLine(label: String, value: String, spacing: Int = 25) {
        canvas.drawText("$label: $value", 40f, y.toFloat(), textPaint)
        y += spacing
    }

    fun drawColumn(
        canvas: Canvas,
        label1: String,
        value1: String,
        label2: String,
        value2: String,
        y: Int,  // Starting y position
        pageWidth: Int,
        verticalSpacing: Int  // Add this parameter for spacing between lines
    ) {
        // Left side text
        val leftText = "$label1: $value1"
        canvas.drawText(leftText, 40f, (y + verticalSpacing).toFloat(), textPaint)

        // Right side text
        val rightText = "$label2: $value2"
        val rightTextWidth = textPaint.measureText(rightText)
        canvas.drawText(rightText, pageWidth - rightTextWidth - 40f, (y + verticalSpacing).toFloat(), textPaint)

        // Adjust the y position for the next row (you can add more vertical spacing if you want)
        val nextYPosition = y + verticalSpacing + 40 // Adjust 40 based on text height, if necessary
    }


    // Title
    drawDivider()
    canvas.drawText("OPD Details", 250f, y.toFloat(), titlePaint)
    y += 15

    drawDivider()
//    y +=
    y += 15
    drawColumn(canvas, "Date", patient.createdAt.take(10), "Billing ID", "${"%05d".format(patient.billingId)}", y = 100, pageWidth = pageInfo.pageWidth,verticalSpacing = 20 )
    y += 10


//    drawDivider()
    y+=10
    // Section: Patient Details
    canvas.drawText("Patient Details", 40f, y.toFloat(), sectionPaint)
    y += 25
    drawLine("Patient ID", patient.patient_id.toString())
    drawLine("Name", patient.fullname)
    drawLine("DOB", patient.DOB.take(10))
    drawLine("Gender", patient.gender)
    drawLine("Email", patient.email)
    drawLine("Phone", patient.phone_number)
    drawLine("Address", patient.address)

    y += 10
    drawDivider()

    // Section: Doctor Details
    canvas.drawText("Doctor's Details", 40f, y.toFloat(), sectionPaint)
    y += 25
    drawLine("Doctor", patient.doctor)
    drawLine("Department", patient.department)

    y += 10
    drawDivider()

    // Section: Payment Details
    canvas.drawText("Payment Details", 40f, y.toFloat(), sectionPaint)
    y += 25
    drawLine("Paid via", patient.paymentMethod)
    drawLine("Fees", patient.fees.toString())
    drawLine("Cash In", patient.cash_in.toString())

    if(patient.paymentMethod == "cash"){
        drawLine("Cash Out", patient.cash_out.toString())
    }

    document.finishPage(page)

    val fileName = "Patient_Report_${patient.fullname}_${patient.billingId}_${System.currentTimeMillis()}.pdf"

    val contentValues = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
        put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        put(MediaStore.Downloads.IS_PENDING, 1)
    }

    val resolver = context.contentResolver
    val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    val itemUri = resolver.insert(collection, contentValues)

    itemUri?.let { uri ->
        try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                document.writeTo(outputStream)
            }
            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
            Toast.makeText(context, "PDF saved to Downloads", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save PDF", Toast.LENGTH_SHORT).show()
        } finally {
            document.close()
        }
    } ?: run {
        Toast.makeText(context, "Unable to create file", Toast.LENGTH_SHORT).show()
    }
}
