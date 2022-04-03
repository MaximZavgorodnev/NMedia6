package ru.netology.nmedia.error

import android.database.SQLException
import android.provider.Settings.Global.getString
import ru.netology.nmedia.R
import java.io.IOException

sealed class AppError(var code: String): RuntimeException() {
    companion object {
        fun from(e: Throwable): AppError = when (e) {
            is AppError -> e
            is SQLException -> DbError
            is IOException -> NetworkError
            else -> UnknownError
        }
    }
}
class ApiError(val status: Int, code: String): AppError(code)
object NetworkError : AppError((R.string.error_network).toString())
object UnknownError: AppError((R.string.error_unknown).toString())
object DbError : AppError((R.string.error_db).toString())

//
//
//class MainActivity : AppCompatActivity(),ContentAdapter.ContentListener{
//    override fun onItemClicked(item: MainMarketTickClass) {
//        var ft1 : FragmentTransaction = supportFragmentManager.beginTransaction()
//        ft1.replace(R.id.MainFrame,AddCar.newInstanceaddcar())
//        ft1.commit()}
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContentView(R.layout.activity_main)
//        var bnv = findViewById(R.id.navigation) as BottomNavigationView
//        bnv.setOnNavigationItemSelectedListener (object : BottomNavigationView.OnNavigationItemSelectedListener{
//            override fun onNavigationItemSelected(item: MenuItem): Boolean {
//                var selectFragment : Fragment? = null
//                when (item.itemId) {
//                    R.id.navigation_home -> {
//                        selectFragment = MainMarket.newInstance()
//                    }
//                    R.id.navigation_dashboard -> {
//                        selectFragment = AddCar.newInstanceaddcar()
//                    }
//                    R.id.navigation_notifications -> {
//
//                    }
//                }
//                var ft : FragmentTransaction = supportFragmentManager.beginTransaction()
//                ft.replace(R.id.MainFrame,selectFragment)
//                ft.commit()
//                return true
//            }
//        })
//
//        var ft : FragmentTransaction = supportFragmentManager.beginTransaction()
//        ft.replace(R.id.MainFrame,MainMarket.newInstance())
//        ft.commit()
//    }
//
//}