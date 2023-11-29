package com.ayia.nightmodekt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment

class CallsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calls, container, false)

        val gmailImageButton: ImageButton = view.findViewById(R.id.ic_gmail)
        val facebookImageButton: ImageButton = view.findViewById(R.id.ic_fb)
        val instagramImageButton: ImageButton = view.findViewById(R.id.ic_insta)

        // Set OnClickListener for Gmail ImageButton
        gmailImageButton.setOnClickListener {
            sendEmail()
        }

        // Set OnClickListener for Facebook ImageButton
        facebookImageButton.setOnClickListener {
            gotoFacebook()
        }

        // Set OnClickListener for Instagram ImageButton
        instagramImageButton.setOnClickListener {
            gotoInstagram()
        }

        return view
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // This opens the email app
            putExtra(Intent.EXTRA_EMAIL, arrayOf("arsrang32@gmail.com")) // Replace with your Gmail account
        }

        if (emailIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(emailIntent)
        } else {
            showToast("No email app found.")
        }
    }

    private fun gotoFacebook() {
        val facebookIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://www.facebook.com/arsrang.marak.5/") // Replace with your Facebook page URL
        }

        if (facebookIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(facebookIntent)
        } else {
            showToast("No web browser app found.")
        }
    }

    private fun gotoInstagram() {
        val instagramIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://www.instagram.com/r_srang/") // Replace with your Instagram page URL
        }

        if (instagramIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(instagramIntent)
        } else {
            showToast("No web browser app found.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
