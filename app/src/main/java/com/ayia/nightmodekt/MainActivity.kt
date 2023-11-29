package com.ayia.nightmodekt

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var userPrefs: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userPrefs = UserPreferencesRepository.getInstance(this)

        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(ChatsFragment(), "Gitrang")
        adapter.addFragment(CallsFragment(), "App Info")
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        // Set the saved theme on activity creation
        ThemeChanger().invoke(userPrefs.appTheme)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.header_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_option1 -> {
                showThemeDialog()
                true
            }
            // Handle other menu items if necessary
            // ...
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        showExitConfirmationDialog()
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                super.onBackPressed()
            }
            .setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showThemeDialog() {
        val themeOptions = arrayOf("Light Mode", "Dark Mode", "Follow System")

        val selectedThemeIndex = getSelectedThemeIndex()

        MaterialAlertDialogBuilder(this)
            .setTitle("Select Theme")
            .setSingleChoiceItems(themeOptions, selectedThemeIndex) { dialog, position ->
                val selectedTheme = when (position) {
                    0 -> Theme.LIGHT_MODE
                    1 -> Theme.DARK_MODE
                    else -> Theme.FOLLOW_SYSTEM
                }
                saveTheme(selectedTheme)
                dialog.dismiss()
            }
            .show()
    }

    private fun getSelectedThemeIndex(): Int {
        val currentTheme = userPrefs.appTheme
        return when (currentTheme) {
            Theme.LIGHT_MODE -> 0
            Theme.DARK_MODE -> 1
            else -> 2
        }
    }

    private fun saveTheme(theme: Theme) {
        userPrefs.updateTheme(theme)
        ThemeChanger().invoke(theme)
        recreate()
    }

    private inner class ViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val fragmentList = ArrayList<Fragment>()
        private val titleList = ArrayList<String>()

        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titleList[position]
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            titleList.add(title)
        }
    }
}
