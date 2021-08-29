package crazyboyfeng.accSettings

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import crazyboyfeng.accSettings.acc.Command
import crazyboyfeng.accSettings.fragment.SettingsFragment

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!Command.isRoot()){
            val textView=findViewById<TextView>(android.R.id.content)
            textView.setText(R.string.need_root_permission)
            return
        }
        supportFragmentManager.addOnBackStackChangedListener {
            val preferenceFragment =
                supportFragmentManager.findFragmentById(android.R.id.content) as PreferenceFragmentCompat
            supportActionBar?.subtitle = preferenceFragment.preferenceScreen.title
        }
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()
    }
}