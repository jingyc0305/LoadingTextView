package custom.com.loadingtextview

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //模拟网络请求数据
        Handler().postDelayed({ loading_text_view1.setText("1234") }, 3000)
        //模拟网络请求数据
        Handler().postDelayed({ loading_text_view2.setText("567") }, 5000)
        //模拟网络请求数据
        Handler().postDelayed({ loading_text_view3.setText("890") }, 7000)
        //模拟网络请求数据
        Handler().postDelayed({ loading_text_view4.setText("10") }, 9000)
    }
}
