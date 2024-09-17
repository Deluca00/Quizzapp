package np.com.bimalkafle.quizonline.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import np.com.bimalkafle.quizonline.DictionaryFragment
import np.com.bimalkafle.quizonline.HomeFragment
import np.com.bimalkafle.quizonline.LeaderboardFragment
import np.com.bimalkafle.quizonline.ProfileFragment
import np.com.bimalkafle.quizonline.R

class MainActivity_menu : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_leaderboard -> {
                moveToFragment(LeaderboardFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_distionary -> {
                moveToFragment(DictionaryFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                moveToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        supportActionBar?.hide()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        moveToFragment(HomeFragment())




    }

    private fun moveToFragment(fragment: Fragment) {
        // Tạo một phiên chuyển đổi Fragment
        val fragmentTrans = supportFragmentManager.beginTransaction()

        // Thay thế Fragment hiện tại trong container (R.id.fragment_container) bằng Fragment mới
        fragmentTrans.replace(R.id.fragment_container, fragment)

        // Xác nhận và thực hiện chuyển đổi Fragment
        fragmentTrans.commit()
    }


}